/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class is the primary class for jMimeMagic
 *
 * @author $Author: arimus $
 * @version $Revision: 1.8 $
 */
public class Magic
{
    private static boolean initialized = false;
    private static MagicParser magicParser = null;
    private static final Map<String,List<MagicMatcher>> hintMap = new HashMap<>();

    /**
     * constructor
     */
    public Magic()
    {

    }

    /**
     * Add a hint to use the specified matcher for the given extension
     * 
     * @param extension DOCUMENT ME!
     * @param matcher DOCUMENT ME!
     */
    private static void addHint(String extension, MagicMatcher matcher)
    {
        if (hintMap.containsKey(extension)) {
            List<MagicMatcher> a = hintMap.get(extension);
            a.add(matcher);
        } else {
        	List<MagicMatcher> a = new ArrayList<>();
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

        if (!initialized) {

            magicParser = new MagicParser();
            magicParser.initialize();

            // build hint map

            for (MagicMatcher matcher : magicParser.getMatchers()) {
                String ext = matcher.getMatch().getExtension();

                if ((ext != null) && !ext.trim().equals("")) {

                    addHint(ext, matcher);
                } else if (matcher.getMatch().getType().equals("detector")) {
                    String[] exts = matcher.getDetectorExtensions();

                    for (String s : exts) {

                        addHint(s, matcher);
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
    public static Collection<MagicMatcher> getMatchers()
        throws MagicParseException
    {

        if (!initialized) {
            initialize();
        }

        Iterator<MagicMatcher> i = magicParser.getMatchers().iterator();
        List<MagicMatcher> m = new ArrayList<>();

        while (i.hasNext()) {
            MagicMatcher matcher = i.next();

            try {
                m.add(matcher.clone());
            } catch (CloneNotSupportedException e) {

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
    @SuppressWarnings({"unused"})
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

        if (!initialized) {
            initialize();
        }

        Collection<MagicMatcher> matchers = magicParser.getMatchers();

        MagicMatcher matcher;
        MagicMatch match;

        for (MagicMatcher magicMatcher : matchers) {
            matcher = magicMatcher;

            try {
                if ((match = matcher.test(data, onlyMimeMatch)) != null) {
                    return match;
                }
            } catch (IOException | UnsupportedTypeException e) {

                throw new MagicException(e);
            }
        }

        throw new MagicMatchNotFoundException();
    }

    /**
     * get a match from a file
     *
     * @param file the file to match content in
     * @param extensionHints whether to use extension to optimize order of content tests
     *
     * @return the MagicMatch object representing a match in the file
     *
     * @throws MagicParseException DOCUMENT ME!
     * @throws MagicMatchNotFoundException DOCUMENT ME!
     * @throws MagicException DOCUMENT ME!
     */
    @SuppressWarnings({"unused"})
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

        if (!initialized) {
            initialize();
        }

        MagicMatcher matcher;
        MagicMatch match;

        // check for extension hints
        List<MagicMatcher> checked = new ArrayList<>();

        if (extensionHints) {

            String name = file.getName();
            int pos = name.lastIndexOf('.');

            if (pos > -1) {
                String ext = name.substring(pos + 1);

                if (!ext.equals("")) {

                    Collection<MagicMatcher> c = hintMap.get(ext);

                    if (c != null) {

                        for (MagicMatcher magicMatcher : c) {
                            matcher = magicMatcher;


                            try {
                                if ((match = matcher.test(file, onlyMimeMatch)) != null) {

                                    return match;
                                }
                            } catch (UnsupportedTypeException | IOException e) {

                                throw new MagicException(e);
                            }

                            // add to the already checked list
                            checked.add(matcher);
                        }
                    }
                }
            }
        }

        Collection<MagicMatcher> matchers = magicParser.getMatchers();

        for (MagicMatcher magicMatcher : matchers) {
            matcher = magicMatcher;

            if (!checked.contains(matcher)) {

                try {
                    if ((match = matcher.test(file, onlyMimeMatch)) != null) {
                        return match;
                    }
                } catch (UnsupportedTypeException | IOException e) {
                    throw new MagicException(e);
                }
            }
        }

        throw new MagicMatchNotFoundException();
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

                System.out.println("filename: " + args[0]);

            } else {
                System.err.println("file '" + f.getCanonicalPath() + "' not found");
            }
        } catch (Exception e) {
            System.err.println("error: " + e);
            e.printStackTrace(System.err);
        }
    }
}
