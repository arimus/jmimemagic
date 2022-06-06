/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;


import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class represents a single match test
 *
 * @author $Author: arimus $
 * @version $Revision: 1.10 $
 */
public class MagicMatch implements Cloneable
{
    private String mimeType = null;
    private String extension = null;
    private String description = null;
    private ByteBuffer test = null;
    private int offset = 0;
    private int length = 0;

    // possible types:
    //     byte, short, long, string, date, beshort, belong, bedate, leshort,
    //     lelong, ledate, regex
    private String type = "";
    private long bitmask = 0xFFFFFFFFL;
    private char comparator = '\0';
    private final List<MagicMatch> subMatches = new ArrayList<>(0);
    private Map<String,String> properties;

    /** 
     * constructor 
     */
    public MagicMatch()
    {

    }



    /**
     * set the mime type for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setMimeType(String value)
    {
        mimeType = value;
    }

    /**
     * get the magic match for this magic match
     *
     * @return the mime type for this magic match
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * set the extension for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setExtension(String value)
    {
        extension = value;
    }

    /**
     * get the extension for this magic match
     *
     * @return the extension for this magic match
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * set the description for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value)
    {
        description = value;
    }

    /**
     * get the description for this magic match
     *
     * @return the description for thie magic match
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * set the test value for thie magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setTest(ByteBuffer value)
    {
        test = value;
    }

    /**
     * get the test value for this magic match
     *
     * @return DOCUMENT ME!
     */
    public ByteBuffer getTest()
    {
        return test;
    }

    /**
     * set the offset in the stream we are comparing to the test value for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setOffset(int value)
    {
        this.offset = value;
    }

    /**
     * get the offset in the stream we are comparing to the test value for this magic match
     *
     * @return the offset for this magic match
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * set the length we are restricting the comparison to for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setLength(int value)
    {
        this.length = value;
    }

    /**
     * get the length we are restricting the comparison to for this magic match
     *
     * @return DOCUMENT ME!
     */
    public int getLength()
    {
        return length;
    }

    /**
     * set the type of match to perform for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setType(String value)
    {
        this.type = value;
    }

    /**
     * get the type of match for this magic match
     *
     * @return DOCUMENT ME!
     */
    public String getType()
    {
        return type;
    }

    /**
     * set the bitmask that will be applied for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setBitmask(String value)
    {
        if (value != null) {
            this.bitmask = Long.decode(value).intValue();
        }
    }

    /**
     * get the bitmask that will be applied for this magic match
     *
     * @return the bitmask for this magic match
     */
    public long getBitmask()
    {
        return bitmask;
    }

    /**
     * set the comparator for this magic match
     *
     * @param value DOCUMENT ME!
     */
    public void setComparator(String value)
    {
        this.comparator = value.charAt(0);
    }

    /**
     * get the comparator for this magic match
     *
     * @return the comparator for this magic match
     */
    public char getComparator()
    {
        return comparator;
    }

    /**
     * set the properties for this magic match
     *
     * @param properties DOCUMENT ME!
     */
    public void setProperties(Map<String,String> properties)
    {
        this.properties = properties;
    }

    /**
     * get the properties for this magic match
     *
     * @return the properties for this magic match
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }

    /**
     * add a submatch to this magic match
     *
     * @param m a magic match
     */
    public void addSubMatch(MagicMatch m)
    {
        subMatches.add(m);
    }

    /**
     * set all submatches
     *
     * @param a a collection of submatches
     */
    public void setSubMatches(Collection<MagicMatch> a)
    {
        subMatches.clear();
        subMatches.addAll(a);
    }

    /**
     * get all submatches for this magic match
     *
     * @return a collection of submatches
     */
    public Collection<MagicMatch> getSubMatches()
    {
        return subMatches;
    }

    /**
     * determine if this match or any submatches has the description
     *
     * @param desc DOCUMENT ME!
     *
     * @return whether or not the description matches
     */
    public boolean descriptionMatches(String desc)
    {
        if ((description != null) && description.equals(desc)) {
            return true;
        }

        Collection<MagicMatch> submatches = getSubMatches();
        Iterator<MagicMatch> i = submatches.iterator();
        MagicMatch m;

        while (i.hasNext()) {
            m = i.next();

            if (m.descriptionMatches(desc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * determine if this match or any submatches has the description
     *
     * @param desc DOCUMENT ME!
     *
     * @return whether or not the description matches
     */
    public boolean mimeTypeMatches(String desc)
    {
        if ((mimeType != null) && mimeType.equals(desc)) {
            return true;
        }

        Collection<MagicMatch> submatches = getSubMatches();
        Iterator<MagicMatch> i = submatches.iterator();
        MagicMatch m;

        while (i.hasNext()) {
            m = i.next();

            if (m.mimeTypeMatches(desc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CloneNotSupportedException DOCUMENT ME!
     */
    protected Object clone()
        throws CloneNotSupportedException
    {
        // Add super call
        super.clone();

        MagicMatch clone = new MagicMatch();
        clone.setBitmask(Long.toString(bitmask, 8));
        clone.setComparator("" + comparator);
        clone.setDescription(description);
        clone.setExtension(extension);
        clone.setLength(length);
        clone.setMimeType(mimeType);
        clone.setOffset(offset);

        // these properties should only be String types, so we shouldn't have to clone them
        if(properties!= null) {
            Map<String, String> m = new HashMap<>(properties);
	        clone.setProperties(m);
        }

        Iterator<MagicMatch> i = subMatches.iterator();
        List<MagicMatch> a = new ArrayList<>();

        while (i.hasNext()) {
            MagicMatch mm = i.next();
            a.add(mm);
        }

        clone.setSubMatches(a);

        clone.setTest(test);
        clone.setType(type);

        // TODO Auto-generated method stub
        return clone;
    }
}
