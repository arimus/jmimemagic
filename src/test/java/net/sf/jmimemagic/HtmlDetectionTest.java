package net.sf.jmimemagic;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import junit.framework.*;

public class HtmlDetectionTest extends TestCase {
	public void testDoctypeLower() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/doctype_lower.html")));
		assertEquals("text/html", match.getMimeType());
	}

	public void testDoctypeMixed1() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/doctype_mixed1.html")));
		assertEquals("text/html", match.getMimeType());
	}

	public void testDoctypeMixed2() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/doctype_mixed2.html")));
		assertEquals("text/html", match.getMimeType());
	}

	public void testDoctypeUpper() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/doctype_upper.html")));
		assertEquals("text/html", match.getMimeType());
	}

	public void testNoDoctypeLower() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/no_doctype_lower.html")));
		assertEquals("text/html", match.getMimeType());
	}

	public void testNoDoctypeUpper() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/no_doctype_upper.html")));
		assertEquals("text/html", match.getMimeType());
	}


	public void testNoHtmlLower() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/no_html_lower.html")));
		assertEquals("text/html", match.getMimeType());
	}


	public void testNoHtmlUpper() throws MagicParseException, MagicMatchNotFoundException, MagicException, IOException {
		MagicMatch match = Magic.getMagicMatch(IOUtils.toByteArray(getClass().getResourceAsStream("/html/no_html_upper.html")));
		assertEquals("text/html", match.getMimeType());
	}

}
