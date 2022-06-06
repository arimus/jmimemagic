/*
jMimeMagic (TM) is a Java Library for determining the content type of files or streams
Copyright (C) 2003-2017 David Castro
*/
package net.sf.jmimemagic;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class MagicParser extends DefaultHandler implements ContentHandler, ErrorHandler
{


    // Namespaces feature id (http://xml.org/sax/features/namespaces).
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    // Validation feature id (http://xml.org/sax/features/validation).
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    // Schema validation feature id (http://apache.org/xml/features/validation/schema).
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

    // Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking).
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

    // Default namespaces support (true).
    protected static final boolean DEFAULT_NAMESPACES = true;

    // Default validation support (false).
    protected static final boolean DEFAULT_VALIDATION = false;

    // Default Schema validation support (false).
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    // Default Schema full checking support (false).
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;
    private boolean initialized = false;
    private final List<MagicMatcher> stack = new ArrayList<>();
    private final Collection<MagicMatcher> matchers = new ArrayList<>();
    private MagicMatcher matcher = null;
    private MagicMatch match = null;
    private Map<String,String> properties = null;
    private String finalValue = "";
    private boolean isMimeType = false;
    private boolean isExtension = false;
    private boolean isDescription = false;
    private boolean isTest = false;

    /** 
     * constructor 
     */
    public MagicParser()
    {
    }

    /**
     * parse the xml file and create our MagicMatcher object list
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public synchronized void initialize()
        throws MagicParseException
    {

        if (!initialized) {
            // use default parser

            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser;
            XMLReader reader;
            try {
                parser = parserFactory.newSAXParser();
                reader = parser.getXMLReader();
                reader.setFeature(NAMESPACES_FEATURE_ID, DEFAULT_NAMESPACES);
                reader.setFeature(VALIDATION_FEATURE_ID, DEFAULT_VALIDATION);
                reader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, DEFAULT_SCHEMA_VALIDATION);
                reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, DEFAULT_SCHEMA_FULL_CHECKING);

            } catch (ParserConfigurationException | SAXException e) {
                throw new MagicParseException(e.getMessage());
            }

            // set handlers
            reader.setErrorHandler(this);
            reader.setContentHandler(this);

            // parse file
            try {
                // get the magic file URL
                String magicFile = "/magic.xml";
                URL resource = MagicParser.class.getResource(magicFile);

                String magicURL = resource != null ? resource.toString() : null;

                if (magicURL == null) {

                    throw new MagicParseException("couldn't load '" + null + "'");
                }

                reader.parse(magicURL);
            } catch (SAXParseException e) {
                // ignore
            } catch (Exception e) {
                e.printStackTrace();
                throw new MagicParseException("parse error occurred - " + e.getMessage());
            }

            initialized = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection<MagicMatcher> getMatchers()
    {
        return matchers;
    }

    /**
     * DOCUMENT ME!
     *
     */
    public void startDocument() {

    }

    /**
     * DOCUMENT ME!
     *
     */
    public void endDocument() {

    }

    /**
     * DOCUMENT ME!
     *
     * @param target DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     */
    public void processingInstruction(String target, String data) {
        // do nothing
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     */
    public void characters(char[] ch, int offset, int length) {
        String value = new String(ch, offset, length);

        finalValue += value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     */
    public void ignorableWhitespace(char[] ch, int offset, int length) {
        // do nothing
    }

    /**
     * DOCUMENT ME!
     *
     * @param uri DOCUMENT ME!
     * @param localName DOCUMENT ME!
     * @param qname DOCUMENT ME!
     * @param attributes DOCUMENT ME!
     *
     */
    public void startElement(String uri, String localName, String qname, Attributes attributes) {

        // create a new matcher
        if (localName.equals("match")) {

            // match to hold data
            match = new MagicMatch();
            // our matcher
            matcher = new MagicMatcher();
            matcher.setMatch(match);
        }

        // these are subelements of matcher, but also occur elsewhere
        if (matcher != null) {
            switch (localName) {
                case "mimetype" -> isMimeType = true;
                case "extension" -> isExtension = true;
                case "description" -> isDescription = true;
                case "test" -> {
                    isTest = true;

                    int length = attributes.getLength();

                    for (int i = 0; i < length; i++) {
                        String attrLocalName = attributes.getLocalName(i);
                        String attrValue = attributes.getValue(i);

                        switch (attrLocalName) {
                            case "offset":
                                if (!attrValue.equals("")) {
                                    match.setOffset(Integer.parseInt(attrValue));

                                }
                                break;
                            case "length":
                                if (!attrValue.equals("")) {
                                    match.setLength(Integer.parseInt(attrValue));
                                }
                                break;
                            case "type":
                                match.setType(attrValue);

                                break;
                            case "bitmask":
                                if (!attrValue.equals("")) {
                                    match.setBitmask(attrValue);

                                }
                                break;
                            case "comparator":
                                match.setComparator(attrValue);

                                break;
                        }
                    }
                }
                case "property" -> {
                    int length = attributes.getLength();
                    String name = null;
                    String value = null;

                    for (int i = 0; i < length; i++) {
                        String attrLocalName = attributes.getLocalName(i);
                        String attrValue = attributes.getValue(i);

                        if (attrLocalName.equals("name")) {
                            if (!attrValue.equals("")) {
                                name = attrValue;
                            }
                        } else if (attrLocalName.equals("value")) {
                            if (!attrValue.equals("")) {
                                value = attrValue;
                            }
                        }
                    }

                    // save the property to our map
                    if ((name != null) && (value != null)) {
                        if (properties == null) {
                            properties = new HashMap<>();
                        }

                        if (!properties.containsKey(name)) {
                            properties.put(name, value);

                        }
                    }
                }
                case "match-list" -> // this means we are processing a child match, so we need to push
                    // the existing match on the stack
                        stack.add(matcher);
                default -> {
                }
                // we don't care about this type
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param uri DOCUMENT ME!
     * @param localName DOCUMENT ME!
     * @param qname DOCUMENT ME!
     *
     */
    public void endElement(String uri, String localName, String qname) {

        // determine which tag these chars are for and save them
        if (isMimeType) {
            isMimeType = false;
            match.setMimeType(finalValue);

        } else if (isExtension) {
            isExtension = false;
            match.setExtension(finalValue);

        } else if (isDescription) {
            isDescription = false;
            match.setDescription(finalValue);

        } else if (isTest) {
            isTest = false;
            match.setTest(convertOctals(finalValue));

        }  // do nothing


        finalValue = "";

        // need to save the current matcher here if it is filled out enough and
        // we have an /matcher
        switch (localName) {
            case "match":
                // FIXME - make sure the MagicMatcher isValid() test works
                if (matcher.isValid()) {
                    // set the collected properties on this matcher
                    match.setProperties(properties);

                    // add root match
                    if (stack.size() == 0) {

                        matchers.add(matcher);
                    } else {
                        // we need to add the match to it's parent which is on the
                        // stack

                        MagicMatcher m = stack.get(stack.size() - 1);
                        m.addSubMatcher(matcher);
                    }
                }

                matcher = null;
                properties = null;

                // restore matcher from the stack if we have an /matcher-list
                break;
            case "match-list":
                if (stack.size() > 0) {
                    matcher = stack.get(stack.size() - 1);
                    // pop from the stack
                    stack.remove(matcher);
                }
                break;
            case "mimetype":
                isMimeType = false;
                break;
            case "extension":
                isExtension = false;
                break;
            case "description":
                isDescription = false;
                break;
            case "test":
                isTest = false;
                break;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ex DOCUMENT ME!
     *
     */
    public void warning(SAXParseException ex) {
        // FIXME
    }

    /**
     * DOCUMENT ME!
     *
     * @param ex DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void error(SAXParseException ex)
        throws SAXException
    {
        // FIXME
        throw ex;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ex DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void fatalError(SAXParseException ex)
        throws SAXException
    {
        // FIXME
        throw ex;
    }

    /**
     * replaces octal representations of bytes, written as \ddd to actual byte values.
     *
     * @param s a string with encoded octals
     *
     * @return string with all octals decoded
     */
    private ByteBuffer convertOctals(String s)
    {
        int beg = 0;
        int end;
        int chr;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        while ((end = s.indexOf('\\', beg)) != -1) {
            if (s.charAt(end + 1) != '\\') {
                //log.debug("appending chunk '"+s.substring(beg, end)+"'");
                for (int z = beg; z < end; z++) {
                    buf.write(s.charAt(z));
                }

                if ((end + 4) <= s.length()) {
                    try {
                        chr = Integer.parseInt(s.substring(end + 1, end + 4), 8);

                        buf.write(chr);
                        beg = end + 4;
                    } catch (NumberFormatException nfe) {
                        //log.debug("not an octal");
                        buf.write('\\');
                        beg = end + 1;
                    }
                } else {
                    //log.debug("not an octal, not enough chars left in string");
                    buf.write('\\');
                    beg = end + 1;
                }
            } else {
                //log.debug("appending \\");
                buf.write('\\');
                beg = end + 1;
            }
        }

        for (int z = beg; z < s.length(); z++) {
            buf.write(s.charAt(z));
        }

        try {

            ByteBuffer b = ByteBuffer.allocate(buf.size());

            return b.put(buf.toByteArray());
        } catch (Exception e) {
            return ByteBuffer.allocate(0);
        }
    }
}
