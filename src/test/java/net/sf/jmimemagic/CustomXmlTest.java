package net.sf.jmimemagic;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import junit.framework.*;

public class CustomXmlTest extends TestCase {
	
	public void testHelloWorld() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		Magic.reset();
		Magic.initialize(getClass().getResource("/config/custom_magic.xml"));
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/custom/hello.world")));
		assertEquals("text/custom", match.getMimeType());
	}

	public void test() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		Magic.initialize(getClass().getResource("/config/custom_magic.xml"));
		// disable
		Magic.reset();
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/custom/hello.world")));
		assertFalse("text/custom".equals(match.getMimeType()));
	}

}
