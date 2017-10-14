/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic.detectors;

import net.sf.jmimemagic.MagicDetector;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

import java.util.Map;
import java.util.regex.Pattern;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class TextFileDetector implements MagicDetector
{
    private static Log log = LogFactory.getLog(TextFileDetector.class);

    /**
     * Creates a new TextFileDetector object.
     */
    public TextFileDetector()
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
        return "Text File Detector";
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
        return new String[] { "txt", "text" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledTypes()
    {
        return new String[] { "text/plain" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return "textfiledetector";
    }

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     * @param bitmask DOCUMENT ME!
     * @param comparator DOCUMENT ME!
     * @param mimeType DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] process(byte[] data, int offset, int length, long bitmask, char comparator,
        String mimeType, Map<String,String> params)
    {
        log.debug("processing stream data");

        BOMInputStream bomIn = null;
        try {
            bomIn = new BOMInputStream(new ByteArrayInputStream(data), ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE);
            if (bomIn.hasBOM()) {
                return new String[] { "text/plain" };
            }
        } catch (IOException e) {
            log.error("TextFileDetector: error detecting byte order mark");
        } finally {
        	IOUtils.closeQuietly(bomIn);
        }

        try {
            String s = new String(data, "UTF-8");

            if (!Pattern.matches("/[^[:ascii:][:space:]]/", s)) {
                return new String[] { "text/plain" };
            }
        } catch (UnsupportedEncodingException e) {
            log.error("TextFileDetector: failed to process data");
        }

        return null;
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
        String mimeType, Map<String,String> params)
    {
        log.debug("processing file data");

        BufferedInputStream is =null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));

            byte[] b = new byte[length];
            int n = is.read(b, offset, length);
            if (n > 0) {
                return process(b, offset, length, bitmask, comparator, mimeType, params);
            }
        } catch (IOException e) {
            log.error("TextFileDetector: error", e);
        } finally {
        	IOUtils.closeQuietly(is);
        }

        return null;
    }
}
