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
package net.sf.jmimemagic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This class is the primary class for jMimeMagic
 *
 * @author $Author: arimus $
 * @version $Revision: 1.8 $
 */
public class Magic
{
    private static String magicFile = "/magic.xml";
    private static Log log = LogFactory.getLog(Magic.class);
    private static boolean initialized = false;
    private static MagicParser magicParser = null;
    private static HashMap hintMap = new HashMap();

    /**
     * constructor
     */
    private Magic()
    {
        log.debug("instantiated");
    }

    /**
     * Add a hint to use the specified matcher for the given extension
     * 
     * @param extension DOCUMENT ME!
     * @param matcher DOCUMENT ME!
     */
    private static void addHint(String extension, MagicMatcher matcher)
    {
        if (hintMap.keySet().contains(extension)) {
            ArrayList a = (ArrayList) hintMap.get(extension);
            a.add(matcher);
        } else {
            ArrayList a = new ArrayList();
            a.add(matcher);
            hintMap.put(extension, a);
        }
    }

    /**
     * create a parser and initialize it
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public static synchronized void initialize()
        throws MagicParseException
    {
        // get the magic file URL
        URL magicURL = MagicParser.class.getResource(magicFile);

        if (magicURL == null) {
            log.error("initialize(): couldn't load '" + magicURL + "'");
            throw new MagicParseException("couldn't load '" + magicURL + "'");
        }
        
        initialize(magicURL);
     }

    public static synchronized void reset() {
    	initialized = false;
    	hintMap.clear();
    }
    
   /**
     * create a parser and initialize it with the provided match configuration XML
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public static synchronized void initialize(URL magicXmlUrl)
        throws MagicParseException
    {
        log.debug("initialize()");

        if (!initialized) {
            log.debug("initializing");
            magicParser = new MagicParser();
            magicParser.initialize(magicXmlUrl);

            // build hint map
            Iterator i = magicParser.getMatchers().iterator();

            while (i.hasNext()) {
                MagicMatcher matcher = (MagicMatcher) i.next();
                String ext = matcher.getMatch().getExtension();

                if ((ext != null) && !ext.trim().equals("")) {
                    if (log.isDebugEnabled()) {
                        log.debug("adding hint mapping for extension '" + ext + "'");
                    }

                    addHint(ext, matcher);
                } else if (matcher.getMatch().getType().equals("detector")) {
                    String[] exts = matcher.getDetectorExtensions();

                    for (int j = 0; j < exts.length; j++) {
                        if (log.isDebugEnabled()) {
                            log.debug("adding hint mapping for extension '" + exts[j] + "'");
                        }

                        addHint(exts[j], matcher);
                    }
                }
            }

            initialized = true;
        }
    }

    /**
     * return the parsed MagicMatch objects that were created from the magic.xml
     * definitions
     *
     * @return the parsed MagicMatch objects
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public static Collection getMatchers()
        throws MagicParseException
    {
        log.debug("getMatchers()");

        if (!initialized) {
            initialize();
        }

        Iterator i = magicParser.getMatchers().iterator();
        ArrayList m = new ArrayList();

        while (i.hasNext()) {
            MagicMatcher matcher = (MagicMatcher) i.next();

            try {
                m.add(matcher.clone());
            } catch (CloneNotSupportedException e) {
                log.error("failed to clone matchers");
                throw new MagicParseException("failed to clone matchers");
            }
        }

        return m;
    }

    /**
     * get a match from a stream of data
     *
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MagicParseException DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(byte[] data)
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        return getMagicMatch(data, false);
    }

    /**
     * get a match from a stream of data
     *
     * @param data DOCUMENT ME!
     * @param onlyMimeMatch DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MagicParseException DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(byte[] data, boolean onlyMimeMatch)
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        log.debug("getMagicMatch(byte[])");

        if (!initialized) {
            initialize();
        }

        Collection matchers = magicParser.getMatchers();
        log.debug("getMagicMatch(byte[]): have " + matchers.size() + " matchers");

        MagicMatcher matcher = null;
        MagicMatch match = null;
        Iterator i = matchers.iterator();

        while (i.hasNext()) {
            matcher = (MagicMatcher) i.next();

            log.debug("getMagicMatch(byte[]): trying to match: " +
                matcher.getMatch().getMimeType());

            try {
                if ((match = matcher.test(data, onlyMimeMatch)) != null) {
                    log.debug("getMagicMatch(byte[]): matched " + matcher.getMatch().getMimeType());

                    return match;
                }
            } catch (IOException e) {
                log.error("getMagicMatch(byte[]): " + e);
                throw new MagicException(e);
            } catch (UnsupportedTypeException e) {
                log.error("getMagicMatch(byte[]): " + e);
                throw new MagicException(e);
            }
        }

        throw new MagicMatchNotFoundException();
    }

    /**
     * get a match from a file
     *
     * @param file the file to match content in
     * @param extensionHints whether or not to use extension to optimize order of content tests
     *
     * @return the MagicMatch object representing a match in the file
     *
     * @throws MagicParseException DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(File file, boolean extensionHints)
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        return getMagicMatch(file, extensionHints, false);
    }

    /**
     * get a match from a file
     *
     * @param file the file to match content in
     * @param extensionHints whether or not to use extension to optimize order of content tests
     * @param onlyMimeMatch only try to get mime type, no submatches are processed when true
     *
     * @return the MagicMatch object representing a match in the file
     *
     * @throws MagicParseException DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException DOCUMENT ME!
     */
    public static MagicMatch getMagicMatch(File file, boolean extensionHints, boolean onlyMimeMatch)
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        log.debug("getMagicMatch(File)");

        if (!initialized) {
            initialize();
        }

        long start = System.currentTimeMillis();

        MagicMatcher matcher = null;
        MagicMatch match = null;

        // check for extension hints
        ArrayList checked = new ArrayList();

        if (extensionHints) {
            log.debug("trying to use hints first");

            String name = file.getName();
            int pos = name.lastIndexOf('.');

            if (pos > -1) {
                String ext = name.substring(pos + 1, name.length());

                if ((ext != null) && !ext.equals("")) {
                    if (log.isDebugEnabled()) {
                        log.debug("using extension '" + ext + "' for hinting");
                    }

                    Collection c = (Collection) hintMap.get(ext);

                    if (c != null) {
                        Iterator i = c.iterator();

                        while (i.hasNext()) {
                            matcher = (MagicMatcher) i.next();

                            log.debug("getMagicMatch(File): trying to match: " +
                                matcher.getMatch().getDescription());

                            try {
                                if ((match = matcher.test(file, onlyMimeMatch)) != null) {
                                    log.debug("getMagicMatch(File): matched " +
                                        matcher.getMatch().getDescription());

                                    if (log.isDebugEnabled()) {
                                        long end = System.currentTimeMillis();
                                        log.debug("found match in '" + (end - start) +
                                            "' milliseconds");
                                    }

                                    return match;
                                }
                            } catch (UnsupportedTypeException e) {
                                log.error("getMagicMatch(File): " + e);
                                throw new MagicException(e);
                            } catch (IOException e) {
                                log.error("getMagicMatch(File): " + e);
                                throw new MagicException(e);
                            }

                            // add to the already checked list
                            checked.add(matcher);
                        }
                    }
                } else {
                    log.debug("no file extension, ignoring hints");
                }
            } else {
                log.debug("no file extension, ignoring hints");
            }
        }

        Collection matchers = magicParser.getMatchers();
        log.debug("getMagicMatch(File): have " + matchers.size() + " matches");

        Iterator i = matchers.iterator();

        while (i.hasNext()) {
            matcher = (MagicMatcher) i.next();

            if (!checked.contains(matcher)) {
                log.debug("getMagicMatch(File): trying to match: " +
                    matcher.getMatch().getDescription());

                try {
                    if ((match = matcher.test(file, onlyMimeMatch)) != null) {
                        log.debug("getMagicMatch(File): matched " +
                            matcher.getMatch().getDescription());

                        if (log.isDebugEnabled()) {
                            long end = System.currentTimeMillis();
                            log.debug("found match in '" + (end - start) + "' milliseconds");
                        }

                        return match;
                    }
                } catch (UnsupportedTypeException e) {
                    log.error("getMagicMatch(File): " + e);
                    throw new MagicException(e);
                } catch (IOException e) {
                    log.error("getMagicMatch(File): " + e);
                    throw new MagicException(e);
                }
            } else {
                log.debug("getMagicMatch(File): already checked, skipping: " +
                    matcher.getMatch().getDescription());
            }
        }

        throw new MagicMatchNotFoundException();
    }

    /**
     * print the contents of a magic file
     *
     * @param stream DOCUMENT ME!
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public static void printMagicFile(PrintStream stream)
        throws MagicParseException
    {
        if (!initialized) {
            initialize();
        }

        Collection matchers = Magic.getMatchers();
        log.debug("have " + matchers.size() + " matches");

        MagicMatcher matcher = null;
        Iterator i = matchers.iterator();

        while (i.hasNext()) {
            matcher = (MagicMatcher) i.next();
            log.debug("printing");
            printMagicMatcher(stream, matcher, "");
        }
    }

    /**
     * print a magic match
     *
     * @param stream DOCUMENT ME!
     * @param matcher DOCUMENT ME!
     * @param spacing DOCUMENT ME!
     */
    private static void printMagicMatcher(PrintStream stream, MagicMatcher matcher, String spacing)
    {
        stream.println(spacing + "name: " + matcher.getMatch().getDescription());
        stream.println(spacing + "children: ");

        Collection matchers = matcher.getSubMatchers();
        Iterator i = matchers.iterator();

        while (i.hasNext()) {
            printMagicMatcher(stream, (MagicMatcher) i.next(), spacing + "  ");
        }
    }

    /**
     * print a magic match
     *
     * @param stream DOCUMENT ME!
     * @param match DOCUMENT ME!
     * @param spacing DOCUMENT ME!
     */
    public static void printMagicMatch(PrintStream stream, MagicMatch match, String spacing)
    {
        stream.println(spacing + "=============================");
        stream.println(spacing + "mime type: " + match.getMimeType());
        stream.println(spacing + "description: " + match.getDescription());
        stream.println(spacing + "extension: " + match.getExtension());
        stream.println(spacing + "test: " + new String(match.getTest().array()));
        stream.println(spacing + "bitmask: " + match.getBitmask());
        stream.println(spacing + "offset: " + match.getOffset());
        stream.println(spacing + "length: " + match.getLength());
        stream.println(spacing + "type: " + match.getType());
        stream.println(spacing + "comparator: " + match.getComparator());
        stream.println(spacing + "=============================");

        Collection submatches = match.getSubMatches();
        Iterator i = submatches.iterator();

        while (i.hasNext()) {
            printMagicMatch(stream, (MagicMatch) i.next(), spacing + "    ");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        try {
            if (args.length == 0) {
                System.err.println("usage: test <file>");
                System.exit(1);;
            }
            File f = new File(args[0]);

            if (f.exists()) {
                MagicMatch match = Magic.getMagicMatch(f, true, false);

                System.out.println("filename: " + args[0]);
                printMagicMatch(System.out, match, "");
            } else {
                System.err.println("file '" + f.getCanonicalPath() + "' not found");
            }
        } catch (MagicMatchNotFoundException e) {
            System.out.println("no match found");
        } catch (Exception e) {
            System.err.println("error: " + e);
            e.printStackTrace(System.err);
        }
    }
}
