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
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


/**
 * DOCUMENT ME!
 *
 * @author $Author$
 * @version $Revision$
  */
public class MagicParser extends DefaultHandler implements ContentHandler, ErrorHandler
{
    private static Log log = LogFactory.getLog(MagicParser.class);

    // Namespaces feature id (http://xml.org/sax/features/namespaces).
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    // Validation feature id (http://xml.org/sax/features/validation). 
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    // Schema validation feature id (http://apache.org/xml/features/validation/schema). 
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

    // Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). 
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

    // Default parser name. 
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    // Default namespaces support (true). 
    protected static final boolean DEFAULT_NAMESPACES = true;

    // Default validation support (false). 
    protected static final boolean DEFAULT_VALIDATION = false;

    // Default Schema validation support (false). 
    protected static final boolean DEFAULT_SCHEMA_VALIDATION = false;

    // Default Schema full checking support (false). 
    protected static final boolean DEFAULT_SCHEMA_FULL_CHECKING = false;
    private boolean initialized = false;
    private XMLReader parser = null;
    private ArrayList stack = new ArrayList();
    private Collection matchers = new ArrayList();
    private MagicMatcher matcher = null;
    private MagicMatch match = null;
    private HashMap properties = null;
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
        log.debug("instantiated");
    }
    
    /**
     * parse the provided xml and create our MagicMatcher object list
     *
     * @throws MagicParseException DOCUMENT ME!
     */
    public synchronized void initialize(URL magicXmlUrl)
        throws MagicParseException
    {
        boolean namespaces = DEFAULT_NAMESPACES;
        boolean validation = DEFAULT_VALIDATION;
        boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
        boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;

        if (!initialized) {
            // use default parser
            try {
                parser = XMLReaderFactory.createXMLReader();
            } catch (Exception e) {
                try {
                    log.debug("falling back to default parser: " + DEFAULT_PARSER_NAME);
                    parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
                } catch (Exception ee) {
                    throw new MagicParseException("unable to instantiate parser");
                }
            }

            // set parser features
            try {
                parser.setFeature(NAMESPACES_FEATURE_ID, namespaces);
            } catch (SAXException e) {
                log.debug("initialize(): warning: Parser does not support feature (" +
                    NAMESPACES_FEATURE_ID + ")");
            }

            try {
                parser.setFeature(VALIDATION_FEATURE_ID, validation);
            } catch (SAXException e) {
                log.debug("initialize(): warning: Parser does not support feature (" +
                    VALIDATION_FEATURE_ID + ")");
            }

            try {
                parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
            } catch (SAXNotRecognizedException e) {
                // ignore
            } catch (SAXNotSupportedException e) {
                log.debug("initialize(): warning: Parser does not support feature (" +
                    SCHEMA_VALIDATION_FEATURE_ID + ")");
            }

            try {
                parser.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
            } catch (SAXNotRecognizedException e) {
                // ignore
            } catch (SAXNotSupportedException e) {
                log.debug("initialize(): warning: Parser does not support feature (" +
                    SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
            }

            // set handlers
            parser.setErrorHandler(this);
            parser.setContentHandler(this);

            // set resolver for DTD
            parser.setEntityResolver(new MagicDtdResolver());
            
            // parse file
            try {

                parser.parse(magicXmlUrl.toString());
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
    public Collection getMatchers()
    {
        return matchers;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startDocument()
        throws SAXException
    {
        log.debug("startDocument()");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endDocument()
        throws SAXException
    {
        log.debug("endDocument()");
    }

    /**
     * DOCUMENT ME!
     *
     * @param target DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        // do nothing
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void characters(char[] ch, int offset, int length)
        throws SAXException
    {
        String value = new String(ch, offset, length);
        log.debug("characters(): value is '" + value + "'");

        finalValue += value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param offset DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void ignorableWhitespace(char[] ch, int offset, int length)
        throws SAXException
    {
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
     * @throws SAXException DOCUMENT ME!
     */
    public void startElement(String uri, String localName, String qname, Attributes attributes)
        throws SAXException
    {
        log.debug("startElement()");
        log.debug("startElement(): localName is '" + localName + "'");

        // create a new matcher
        if (localName.equals("match")) {
            log.debug("startElement(): creating new matcher");
            // match to hold data
            match = new MagicMatch();
            // our matcher
            matcher = new MagicMatcher();
            matcher.setMatch(match);
        }

        // these are subelements of matcher, but also occur elsewhere
        if (matcher != null) {
            if (localName.equals("mimetype")) {
                isMimeType = true;
            } else if (localName.equals("extension")) {
                isExtension = true;
            } else if (localName.equals("description")) {
                isDescription = true;
            } else if (localName.equals("test")) {
                isTest = true;

                int length = attributes.getLength();

                for (int i = 0; i < length; i++) {
                    String attrLocalName = attributes.getLocalName(i);
                    String attrValue = attributes.getValue(i);

                    if (attrLocalName.equals("offset")) {
                        if (!attrValue.equals("")) {
                            match.setOffset(new Integer(attrValue).intValue());
                            log.debug("startElement():   setting offset to '" + attrValue + "'");
                        }
                    } else if (attrLocalName.equals("length")) {
                        if (!attrValue.equals("")) {
                            match.setLength(new Integer(attrValue).intValue());
                            log.debug("startElement():   setting length to '" + attrValue + "'");
                        }
                    } else if (attrLocalName.equals("type")) {
                        match.setType(attrValue);
                        log.debug("startElement():   setting type to '" + attrValue + "'");
                    } else if (attrLocalName.equals("bitmask")) {
                        if (!attrValue.equals("")) {
                            match.setBitmask(attrValue);
                            log.debug("startElement():   setting bitmask to '" + attrValue + "'");
                        }
                    } else if (attrLocalName.equals("comparator")) {
                        match.setComparator(attrValue);
                        log.debug("startElement():   setting comparator to '" + attrValue + "'");
                    }
                }
            } else if (localName.equals("property")) {
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
                        properties = new HashMap();
                    }

                    if (!properties.containsKey(name)) {
                        properties.put(name, value);
                        log.debug("startElement():   setting property '" + name + "'='" + value +
                            "'");
                    } else {
                        log.debug("startElement():   not setting property '" + name +
                            "', duplicate key");
                    }
                }
            } else if (localName.equals("match-list")) {
                log.debug("startElement(): found submatcher list");

                // this means we are processing a child match, so we need to push
                // the existing match on the stack
                log.debug("startElement(): pushing current matcher to stack");
                stack.add(matcher);
            } else {
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
     * @throws SAXException DOCUMENT ME!
     */
    public void endElement(String uri, String localName, String qname)
        throws SAXException
    {
        log.debug("endElement()");
        log.debug("endElement(): localName is '" + localName + "'");

        // determine which tag these chars are for and save them
        if (isMimeType) {
            isMimeType = false;
            match.setMimeType(finalValue);
            log.debug("characters(): setting mimetype to '" + finalValue + "'");
        } else if (isExtension) {
            isExtension = false;
            match.setExtension(finalValue);
            log.debug("characters(): setting extension to '" + finalValue + "'");
        } else if (isDescription) {
            isDescription = false;
            match.setDescription(finalValue);
            log.debug("characters(): setting description to '" + finalValue + "'");
        } else if (isTest) {
            isTest = false;
            match.setTest(convertOctals(finalValue));
            log.debug("characters(): setting test to '" + convertOctals(finalValue) + "'");
        } else {
            // do nothing
        }

        finalValue = "";

        // need to save the current matcher here if it is filled out enough and
        // we have an /matcher
        if (localName.equals("match")) {
            // FIXME - make sure the MagicMatcher isValid() test works
            if (matcher.isValid()) {
                // set the collected properties on this matcher
                match.setProperties(properties);

                // add root match
                if (stack.size() == 0) {
                    log.debug("endElement(): adding root matcher");
                    matchers.add(matcher);
                } else {
                    // we need to add the match to it's parent which is on the
                    // stack
                    log.debug("endElement(): adding sub matcher");

                    MagicMatcher m = (MagicMatcher) stack.get(stack.size() - 1);
                    m.addSubMatcher(matcher);
                }
            } else {
                // don't add invalid matchers
                log.info("endElement(): not adding invalid matcher '" + match.getDescription() +
                    "'");
            }

            matcher = null;
            properties = null;

            // restore matcher from the stack if we have an /matcher-list
        } else if (localName.equals("match-list")) {
            if (stack.size() > 0) {
                log.debug("endElement(): popping from the stack");
                matcher = (MagicMatcher) stack.get(stack.size() - 1);
                // pop from the stack
                stack.remove(matcher);
            }
        } else if (localName.equals("mimetype")) {
            isMimeType = false;
        } else if (localName.equals("extension")) {
            isExtension = false;
        } else if (localName.equals("description")) {
            isDescription = false;
        } else if (localName.equals("test")) {
            isTest = false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ex DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void warning(SAXParseException ex)
        throws SAXException
    {
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
        int end = 0;
        int c1;
        int c2;
        int c3;
        int chr;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        while ((end = s.indexOf('\\', beg)) != -1) {
            if (s.charAt(end + 1) != '\\') {
                //log.debug("appending chunk '"+s.substring(beg, end)+"'");
                for (int z = beg; z < end; z++) {
                    buf.write((int) s.charAt(z));
                }

                //log.debug("found \\ at position "+end);
                //log.debug("converting octal '"+s.substring(end, end+4)+"'");
                if ((end + 4) <= s.length()) {
                    try {
                        chr = Integer.parseInt(s.substring(end + 1, end + 4), 8);

                        //log.debug("converted octal '"+s.substring(end+1,end+4)+"' to '"+chr);
                        //log.debug("converted octal back to '"+Integer.toOctalString(chr));

                        //log.debug("converted '"+s.substring(end+1,end+4)+"' to "+chr+"/"+((char)chr));
                        buf.write(chr);
                        beg = end + 4;
                        end = beg;
                    } catch (NumberFormatException nfe) {
                        //log.debug("not an octal");
                        buf.write((int) '\\');
                        beg = end + 1;
                        end = beg;
                    }
                } else {
                    //log.debug("not an octal, not enough chars left in string");
                    buf.write((int) '\\');
                    beg = end + 1;
                    end = beg;
                }
            } else {
                //log.debug("appending \\");
                buf.write((int) '\\');
                beg = end + 1;
                end = beg;
            }
        }

        if (end < s.length()) {
            for (int z = beg; z < s.length(); z++) {
                buf.write((int) s.charAt(z));
            }
        }

        try {
            log.debug("convertOctals(): returning buffer size '" + buf.size() + "'");

            ByteBuffer b = ByteBuffer.allocate(buf.size());

            return b.put(buf.toByteArray());
        } catch (Exception e) {
            log.error("convertOctals(): error parsing string: " + e);

            return ByteBuffer.allocate(0);
        }
    }
}
