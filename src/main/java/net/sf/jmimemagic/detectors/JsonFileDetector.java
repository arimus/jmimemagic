/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic.detectors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import net.sf.jmimemagic.MagicDetector;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class JsonFileDetector implements MagicDetector
{
    private static Log log = LogFactory.getLog(JsonFileDetector.class);

    /**
     * Creates a new TextFileDetector object.
     */
    public JsonFileDetector()
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
        return "Json File Detector";
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
        return new String[] { "js", "json" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledTypes()
    {
        return new String[] { "application/json" };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName()
    {
        return "jsonfiledetector";
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

        try {
          String s = new String(data, "UTF-8");
          JsonParser parser = new JsonFactory().createParser(s);
          while (!parser.isClosed()) {
            parser.nextToken();
          }
          return new String[] { "application/json" };
        } catch (IOException e) {
          
          log.error("JsonFileDetector: failed to process data", e);
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
            log.error("JsonFileDetector: error", e);
        } finally {
        	IOUtils.closeQuietly(is);
        }

        return null;
    }
}
