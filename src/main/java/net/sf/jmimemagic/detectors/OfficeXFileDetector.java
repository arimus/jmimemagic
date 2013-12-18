/*
jMimeMagic(TM) is a Java library for determining the content type of files or
streams.

Copyright (C) 2004 David Castro

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information, please email arimus@users.sourceforge.net
*/
package net.sf.jmimemagic.detectors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import net.sf.jmimemagic.MagicDetector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class OfficeXFileDetector implements MagicDetector
{
    private static final String POWERPOINT_EXTENSION = "pptx";
    private static final String EXCEL_EXTENSION = "xlsx";
    private static final String WORD_EXTENSION = "docx";
    private static final String POWERPOINT_MIME = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    private static final String EXCEL_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String WORD_MIME = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    
    private static final byte[] PK_ZIP_SEQUENCE = {0x50,0x4B,0x03,0x04};
    private static final byte[] XML_SQEUENCE = { 0x5B, 0x43, 0x6F, 0x6E, 0x74, 0x65, 0x6E, 0x74, 0x5F, 0x54, 0x79, 0x70, 0x65, 0x73, 0x5D, 0x2E, 0x78, 0x6D,0x6C };
    private static final byte[] DOCX_SEQUENCE = {0x77,0x6F,0x72,0x64, 0x2F };
    private static final byte[] XLSX_SEQUENCE = {0x78,0x6C, 0x2F };
    private static final byte[] PPTX_SEQUENCE = {0x70,0x70, 0x74, 0x2F };
   
    private static final String OFFICE_TYPE_PARAMETER_NAME= "office-type";
    private static final String WORD_TYPE="word";
    private static final String EXCEL_TYPE="excel";
    private static final String POWERPOINT_TYPE="powerpoint";
    
    private static Log log = LogFactory.getLog(OfficeXFileDetector.class);

    /**
     * Creates a new TextFileDetector object.
     */
    public OfficeXFileDetector()
    {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDisplayName()
    {
        return "Office Detector";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion()
    {
        return "0.1";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledExtensions()
    {
        return new String[] { WORD_EXTENSION, EXCEL_EXTENSION, POWERPOINT_EXTENSION };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledTypes()
    {
        return new String[] { WORD_MIME,EXCEL_MIME, POWERPOINT_MIME };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return "officefiledetector";
    }
    

    
    /**
     * Based on: http://serverfault.com/questions/338087/making-libmagic-file-detect-docx-files/377792#377792
     * 
     * 
     */
    public String[] process(byte[] data, int offset, int length, long bitmask, char comparator,
        String mimeType, Map params)
    {
        log.debug("processing stream data");
        
        // start by checking for ZIP local file header signature
        if(checkSequence(data, PK_ZIP_SEQUENCE,0)) {
            // make sure the first file is correct
            if(checkSequence(data, XML_SQEUENCE, 30)) {
                // skip to the second local file header
                int newOffset = scanForSequence(data, PK_ZIP_SEQUENCE, 49, 2000);
                if(newOffset!=-1) {
                    // now skip to the *third* local file header
                    newOffset = scanForSequence(data, PK_ZIP_SEQUENCE, newOffset, 1000);
                    if(newOffset!=-1) {
                        // and check the subdirectory name to determine which type of OOXML file we have
                        String officeType = (String)params.get(OFFICE_TYPE_PARAMETER_NAME);
                        final byte[] sequence;
                        final String mime;
                        if(WORD_TYPE.equalsIgnoreCase(officeType)) {
                             sequence = DOCX_SEQUENCE;
                             mime = WORD_MIME;
                        } else if(EXCEL_TYPE.equalsIgnoreCase(officeType)) {
                            sequence = XLSX_SEQUENCE;
                            mime = EXCEL_MIME;
                        } else if(POWERPOINT_TYPE.equalsIgnoreCase(officeType)) {
                            sequence = PPTX_SEQUENCE;
                            mime = POWERPOINT_MIME;
                        } else {
                            sequence = null;
                            mime = null;
                        }                        
                        if(sequence != null && checkSequence(data, sequence,newOffset+26)) {
                            return new String[] { mime };
                        }
                    }
                }
            }
        } 
        return null;
    }
    
    private int scanForSequence(byte[] data, byte[] sqeuence, int offset, int lookahead) {
        for(int i=offset;i<(offset+lookahead);i++) {
            boolean sequenceComplete=true;
            for(int j=0;j<sqeuence.length;j++) {
                if(data[i] != sqeuence[j]) {
                    sequenceComplete=false;
                    break;
                }  else {
                    i++;
                }
            }
            if(sequenceComplete) {
                return (i);
            }
        }
        return -1;
    }
    private boolean checkSequence(byte[] data, byte[] sqeuence, int offset) {
        for(int i=0;i<sqeuence.length;i++) {
            if(data[offset+i] != sqeuence[i]) return false;
        }
        return true;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     * @param bitmask DOCUMENT ME!
     * @param comparator DOCUMENT ME!
     * @param mimeType DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] process(File file, int offset, int length, long bitmask, char comparator,
        String mimeType, Map params)
    {
        log.debug("processing file data");
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));

            byte[] b = new byte[length];
            int n = is.read(b, offset, length);
            if (n > 0) {
                return process(b, offset, length, bitmask, comparator, mimeType, params);
            }
        } catch (IOException e) {
            log.error("error opening stream", e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("error closing stream");
                }
            }
        }

        return null;
    }
}
