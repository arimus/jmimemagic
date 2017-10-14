/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


/**
 * This class represents a single match test
 *
 * @author $Author: arimus $
 * @version $Revision: 1.1 $
 */
public class MagicMatcher implements Cloneable
{
    private static Log log = LogFactory.getLog(MagicMatcher.class);
    private List<MagicMatcher> subMatchers = new ArrayList<MagicMatcher>(0);
    private MagicMatch match = null;

    /** 
     * constructor 
     */
    public MagicMatcher()
    {
        log.debug("instantiated");
    }

    /**
     * DOCUMENT ME!
     *
     * @param match DOCUMENT ME!
     */
    public void setMatch(MagicMatch match)
    {
        log.debug("setMatch()");
        this.match = match;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MagicMatch getMatch()
    {
        log.debug("getMatch()");

        return this.match;
    }

    /**
     * test to see if everything is in order for this match
     *
     * @return whether or not this match has enough data to be valid
     */
    public boolean isValid()
    {
        log.debug("isValid()");

        if ((match == null) || (match.getTest() == null)) {
            return false;
        }

        String type = new String(match.getTest().array());
        char comparator = match.getComparator();
        String description = match.getDescription();
        String test = new String(match.getTest().array());

        if ((type != null) && !type.equals("") && (comparator != '\0') &&
                ((comparator == '=') || (comparator == '!') || (comparator == '>') ||
                (comparator == '<')) && (description != null) && !description.equals("") &&
                (test != null) && !test.equals("")) {
            return true;
        }

        return false;
    }

    /**
     * add a submatch to this magic match
     *
     * @param m a magic match
     */
    public void addSubMatcher(MagicMatcher m)
    {
        log.debug("addSubMatcher()");
        subMatchers.add(m);
    }

    /**
     * set all submatches
     *
     * @param a a collection of submatches
     */
    public void setSubMatchers(Collection<MagicMatcher> a)
    {
        log.debug("setSubMatchers(): for match '" + match.getDescription() + "'");
        subMatchers.clear();
        subMatchers.addAll(a);
    }

    /**
     * get all submatches for this magic match
     *
     * @return a collection of submatches
     */
    public Collection<MagicMatcher> getSubMatchers()
    {
        log.debug("getSubMatchers()");

        return subMatchers;
    }

    /**
     * test to see if this match or any submatches match
     *
     * @param f the file that should be used to test the match
     * @param onlyMimeMatch DOCUMENT ME!
     *
     * @return the deepest magic match object that matched
     *
     * @throws IOException DOCUMENT ME!
     * @throws UnsupportedTypeException DOCUMENT ME!
     */
    public MagicMatch test(File f, boolean onlyMimeMatch)
        throws IOException, UnsupportedTypeException
    {
        log.debug("test(File)");

        int offset = match.getOffset();
        String description = match.getDescription();
        String type = match.getType();

        log.debug("test(File): testing '" + f.getName() + "' for '" + description + "'");

        log.debug("test(File): \n=== BEGIN MATCH INFO ==");
        log.debug(match.print());
        log.debug("test(File): \n=== END MATCH INFO ====\n");

        RandomAccessFile file = null;
        file = new RandomAccessFile(f, "r");

        try {
            int length = 0;

            if (type.equals("byte")) {
                length = 1;
            } else if (type.equals("short") || type.equals("leshort") || type.equals("beshort")) {
                length = 4;
            } else if (type.equals("long") || type.equals("lelong") || type.equals("belong")) {
                length = 8;
            } else if (type.equals("string")) {
                length = match.getTest().capacity();
            } else if (type.equals("regex")) {
                
                
                final int matchLength = match.getLength();
                length = (matchLength == 0) ? (int) file.length() - offset : matchLength;

                if (length < 0) {
                    length = 0;
                }
            } else if (type.equals("detector")) {
                length = (int) file.length() - offset;

                if (length < 0) {
                    length = 0;
                }
            } else {
                throw new UnsupportedTypeException("unsupported test type '" + type + "'");
            }

            // we know this match won't work since there isn't enough data for the test
            if (length > (file.length() - offset)) {
                return null;
            }

            byte[] buf = new byte[length];
            file.seek(offset);

            int bytesRead = 0;
            int size = 0;
            boolean gotAllBytes = false;
            boolean done = false;

            while (!done) {
                size = file.read(buf, 0, length - bytesRead);

                if (size == -1) {
                    throw new IOException("reached end of file before all bytes were read");
                }

                bytesRead += size;

                if (bytesRead == length) {
                    gotAllBytes = true;
                    done = true;
                }
            }

            log.debug("test(File): stream size is '" + buf.length + "'");

            MagicMatch match = null;
            MagicMatch submatch = null;

            if (testInternal(buf)) {
                // set the top level match to this one
                try {
                    match = getMatch() != null ? (MagicMatch) getMatch()
                            .clone() : null;
                } catch (CloneNotSupportedException e) {
                    // noop
                }

                log.debug("test(File): testing matched '" + description + "'");

                // set the data on this match
                if ((onlyMimeMatch == false) && (subMatchers != null) && (subMatchers.size() > 0)) {
                    log.debug("test(File): testing " + subMatchers.size() + " submatches for '" +
                        description + "'");

                    for (int i = 0; i < subMatchers.size(); i++) {
                        log.debug("test(File): testing submatch " + i);

                        MagicMatcher m = (MagicMatcher) subMatchers.get(i);

                        if ((submatch = m.test(f, false)) != null) {
                            log.debug("test(File): submatch " + i + " matched with '" +
                                submatch.getDescription() + "'");
                            match.addSubMatch(submatch);
                        } else {
                            log.debug("test(File): submatch " + i + " doesn't match");
                        }
                    }
                }
            }

            return match;
        } finally {
            try {
                file.close();
            } catch (Exception fce) {
            }
        }
    }

    /**
     * test to see if this match or any submatches match
     *
     * @param data the data that should be used to test the match
     * @param onlyMimeMatch DOCUMENT ME!
     *
     * @return the deepest magic match object that matched
     *
     * @throws IOException DOCUMENT ME!
     * @throws UnsupportedTypeException DOCUMENT ME!
     */
    public MagicMatch test(byte[] data, boolean onlyMimeMatch)
        throws IOException, UnsupportedTypeException
    {
        log.debug("test(byte[])");

        int offset = match.getOffset();
        String description = match.getDescription();
        String type = match.getType();
        String test = new String(match.getTest().array());
        String mimeType = match.getMimeType();

        log.debug("test(byte[]): testing byte[] data for '" + description + "'");

        log.debug("test(byte[]): \n=== BEGIN MATCH INFO ==");
        log.debug(match.print());
        log.debug("test(byte[]): \n=== END MATCH INFO ====\n");

        int length = 0;

        if (type.equals("byte")) {
            length = 1;
        } else if (type.equals("short") || type.equals("leshort") || type.equals("beshort")) {
            length = 4;
        } else if (type.equals("long") || type.equals("lelong") || type.equals("belong")) {
            length = 8;
        } else if (type.equals("string")) {
            length = match.getTest().capacity();
        } else if (type.equals("regex")) {
            // FIXME - something wrong here, shouldn't have to subtract 1???
            length = data.length - offset - 1;

            if (length < 0) {
                length = 0;
            }
        } else if (type.equals("detector")) {
            // FIXME - something wrong here, shouldn't have to subtract 1???
            length = data.length - offset - 1;

            if (length < 0) {
                length = 0;
            }
        } else {
            throw new UnsupportedTypeException("unsupported test type " + type);
        }

        byte[] buf = new byte[length];
        log.debug("test(byte[]): offset=" + offset + ",length=" + length + ",data length=" +
            data.length);

        if ((offset + length) < data.length) {
            System.arraycopy(data, offset, buf, 0, length);

            log.debug("test(byte[]): stream size is '" + buf.length + "'");

            MagicMatch match = null;
            MagicMatch submatch = null;

            if (testInternal(buf)) {
                // set the top level match to this one
                try {
                    match = getMatch() != null ? (MagicMatch) getMatch()
                            .clone() : null;
                } catch (CloneNotSupportedException e) {
                    // noop
                }

                log.debug("test(byte[]): testing matched '" + description + "'");

                // set the data on this match
                if ((onlyMimeMatch == false) && (subMatchers != null) && (subMatchers.size() > 0)) {
                    log.debug("test(byte[]): testing " + subMatchers.size() + " submatches for '" +
                        description + "'");

                    for (int i = 0; i < subMatchers.size(); i++) {
                        log.debug("test(byte[]): testing submatch " + i);

                        MagicMatcher m = (MagicMatcher) subMatchers.get(i);

                        if ((submatch = m.test(data, false)) != null) {
                            log.debug("test(byte[]): submatch " + i + " matched with '" +
                                submatch.getDescription() + "'");
                            match.addSubMatch(submatch);
                        } else {
                            log.debug("test(byte[]): submatch " + i + " doesn't match");
                        }
                    }
                }
            }

            return match;
        } else {
            return null;
        }
    }

    /**
     * internal test switch
     * 
     * @param data DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private boolean testInternal(byte[] data)
    {
        log.debug("testInternal(byte[])");

        if (data.length == 0) {
            return false;
        }

        String type = match.getType();
        String test = new String(match.getTest().array());
        String mimeType = match.getMimeType();
        String description = match.getDescription();

        ByteBuffer buffer = ByteBuffer.allocate(data.length);

        if ((type != null) && (test != null) && (test.length() > 0)) {
            if (type.equals("string")) {
                buffer = buffer.put(data);

                return testString(buffer);
            } else if (type.equals("byte")) {
                buffer = buffer.put(data);

                return testByte(buffer);
            } else if (type.equals("short")) {
                buffer = buffer.put(data);

                return testShort(buffer);
            } else if (type.equals("leshort")) {
                buffer = buffer.put(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);

                return testShort(buffer);
            } else if (type.equals("beshort")) {
                buffer = buffer.put(data);
                buffer.order(ByteOrder.BIG_ENDIAN);

                return testShort(buffer);
            } else if (type.equals("long")) {
                buffer = buffer.put(data);

                return testLong(buffer);
            } else if (type.equals("lelong")) {
                buffer = buffer.put(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);

                return testLong(buffer);
            } else if (type.equals("belong")) {
                buffer = buffer.put(data);
                buffer.order(ByteOrder.BIG_ENDIAN);

                return testLong(buffer);
            } else if (type.equals("regex")) {
                return testRegex(new String(data));
            } else if (type.equals("detector")) {
                buffer = buffer.put(data);

                return testDetector(buffer);

                //			} else if (type.equals("date")) {
                //				return testDate(data, BIG_ENDIAN);
                //			} else if (type.equals("ledate")) {
                //				return testDate(data, LITTLE_ENDIAN);
                //			} else if (type.equals("bedate")) {
                //				return testDate(data, BIG_ENDIAN);
            } else {
                log.error("testInternal(byte[]): invalid test type '" + type + "'");
            }
        } else {
            log.error("testInternal(byte[]): type or test is empty for '" + mimeType + " - " +
                description + "'");
        }

        return false;
    }

    /**
     * test the data against the test byte
     *
     * @param data the data we are testing
     *
     * @return if we have a match
     */
    private boolean testByte(ByteBuffer data)
    {
        log.debug("testByte()");

        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        String s = test;
        byte b = data.get(0);
        b = (byte) (b & bitmask);
        log.debug("testByte(): decoding '" + test + "' to byte");

        int tst = Integer.decode(test).byteValue();
        byte t = (byte) (tst & 0xff);
        log.debug("testByte(): applying bitmask '" + bitmask + "' to '" + tst + "', result is '" +
            t + "'");
        log.debug("testByte(): comparing byte '" + b + "' to '" + t + "'");

        switch (comparator) {
        case '=':
            return t == b;

        case '!':
            return t != b;

        case '>':
            return t > b;

        case '<':
            return t < b;
        }

        return false;
    }

    /**
     * test the data against the byte array
     *
     * @param data the data we are testing
     *
     * @return if we have a match
     */
    private boolean testString(ByteBuffer data)
    {
        log.debug("testString()");

        ByteBuffer test = match.getTest();
        char comparator = match.getComparator();

        byte[] b = data.array();
        byte[] t = test.array();

        boolean diff = false;
        int i = 0;

        for (i = 0; i < t.length; i++) {
            log.debug("testing byte '" + b[i] + "' from '" + new String(data.array()) +
                "' against byte '" + t[i] + "' from '" + new String(test.array()) + "'");

            if (t[i] != b[i]) {
                diff = true;

                break;
            }
        }

        switch (comparator) {
        case '=':
            return !diff;

        case '!':
            return diff;

        case '>':
            return t[i] > b[i];

        case '<':
            return t[i] < b[i];
        }

        return false;
    }

    /**
     * test the data against a short
     *
     * @param data the data we are testing
     *
     * @return if we have a match
     */
    private boolean testShort(ByteBuffer data)
    {
        log.debug("testShort()");

        short val = 0;
        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        val = byteArrayToShort(data);

        // apply bitmask before the comparison
        val = (short) (val & (short) bitmask);

        short tst = 0;

        try {
            tst = Integer.decode(test).shortValue();
        } catch (NumberFormatException e) {
            log.error("testShort(): " + e);

            return false;

            //if (test.length() == 1) {	
            //	tst = new Integer(Character.getNumericValue(test.charAt(0))).shortValue();
            //}
        }

        log.debug("testShort(): testing '" + Long.toHexString(val) + "' against '" +
            Long.toHexString(tst) + "'");

        switch (comparator) {
        case '=':
            return val == tst;

        case '!':
            return val != tst;

        case '>':
            return val > tst;

        case '<':
            return val < tst;
        }

        return false;
    }

    /**
     * test the data against a long
     *
     * @param data the data we are testing
     *
     * @return if we have a match
     */
    private boolean testLong(ByteBuffer data)
    {
        log.debug("testLong()");

        long val = 0;
        String test = new String(match.getTest().array());
        char comparator = match.getComparator();
        long bitmask = match.getBitmask();

        val = byteArrayToLong(data);

        // apply bitmask before the comparison
        val = val & bitmask;

        long tst = Long.decode(test).longValue();

        log.debug("testLong(): testing '" + Long.toHexString(val) + "' against '" + test +
            "' => '" + Long.toHexString(tst) + "'");

        switch (comparator) {
        case '=':
            return val == tst;

        case '!':
            return val != tst;

        case '>':
            return val > tst;

        case '<':
            return val < tst;
        }

        return false;
    }

    /**
     * test the data against a regex
     *
     * @param text the data we are testing
     *
     * @return if we have a match
     */
    private boolean testRegex(String text)
    {
        log.debug("testRegex()");

        String test = new String(match.getTest().array());
        char comparator = match.getComparator();

        log.debug("testRegex(): searching for '" + test + "'");

        if (comparator == '=') {
            if (Pattern.matches(test, text)) {
                return true;
            } else {
                return false;
            }
        } else if (comparator == '!') {
            if (Pattern.matches(test, text)) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * test the data using a detector
     *
     * @param data the data we are testing
     *
     * @return if we have a match
     */
    private boolean testDetector(ByteBuffer data)
    {
        log.debug("testDetector()");

        String detectorClass = new String(match.getTest().array());

        try {
            log.debug("loading class: " + detectorClass);

            Class<?> c = Class.forName(detectorClass);
            MagicDetector detector = (MagicDetector) c.newInstance();
            String[] types = detector.process(data.array(), match.getOffset(), match.getLength(),
                    match.getBitmask(), match.getComparator(), match.getMimeType(),
                    match.getProperties());

            if ((types != null) && (types.length > 0)) {
                // the match object has no mime type set, so set from the detector class processing
                match.setMimeType(types[0]);

                return true;
            }
        } catch (ClassNotFoundException e) {
            log.error("failed to load detector: " + detectorClass, e);
        } catch (InstantiationException e) {
            log.error("specified class is not a valid detector class: " + detectorClass, e);
        } catch (IllegalAccessException e) {
            log.error("specified class cannot be accessed: " + detectorClass, e);
        }

        return false;
    }

    /**
     * Get the extensions for the underlying detectory
     *
     * @return DOCUMENT ME!
     */
    public String[] getDetectorExtensions()
    {
        log.debug("testDetector()");

        String detectorClass = new String(match.getTest().array());

        try {
            log.debug("loading class: " + detectorClass);

            Class<?> c = Class.forName(detectorClass);
            MagicDetector detector = (MagicDetector) c.newInstance();

            return detector.getHandledTypes();
        } catch (ClassNotFoundException e) {
            log.error("failed to load detector: " + detectorClass, e);
        } catch (InstantiationException e) {
            log.error("specified class is not a valid detector class: " + detectorClass, e);
        } catch (IllegalAccessException e) {
            log.error("specified class cannot be accessed: " + detectorClass, e);
        }

        return new String[0];
    }

    /**
     * encode a byte as an octal string
     *
     * @param b a byte of data
     *
     * @return an octal representation of the byte data
     */
    private String byteToOctalString(byte b)
    {
        int n1;
        int n2;
        int n3;
        n1 = (b / 32) & 7;
        n2 = (b / 8) & 7;
        n3 = b & 7;

        return String.valueOf(n1) + String.valueOf(n2) + String.valueOf(n3);
    }

    /**
     * convert a byte array to a short
     *
     * @param data buffer of byte data
     *
     * @return byte array converted to a short
     */
    private short byteArrayToShort(ByteBuffer data)
    {
        return data.getShort(0);
    }

    /**
     * convert a byte array to a long
     *
     * @param data buffer of byte data
     *
     * @return byte arrays (high and low bytes) converted to a long value
     */
    private long byteArrayToLong(ByteBuffer data)
    {
        return (long) data.getInt(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CloneNotSupportedException DOCUMENT ME!
     */
    protected MagicMatcher clone()
        throws CloneNotSupportedException
    {
        MagicMatcher clone = new MagicMatcher();

        clone.setMatch((MagicMatch) match.clone());

        Iterator<MagicMatcher> i = subMatchers.iterator();
        List<MagicMatcher> sub = new ArrayList<MagicMatcher>();

        while (i.hasNext()) {
            MagicMatcher m = (MagicMatcher) i.next();
            sub.add(m.clone());
        }

        clone.setSubMatchers(sub);

        return clone;
    }
}
