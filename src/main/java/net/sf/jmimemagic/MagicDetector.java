/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;

import java.io.File;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public interface MagicDetector
{
    // get the short name of this detector
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName();

    // get the display name of this detector
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDisplayName();

    // get the version of this plugin
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion();

    // get a list of types this detector handles
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledTypes();

    // get a list of file extensions this detector typically deals with
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getHandledExtensions();

    // process the stream and return all matching content types
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
        String mimeType, Map<String,String> params);

    // process the file and return all matching content types
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
        String mimeType, Map<String,String> params);
}
