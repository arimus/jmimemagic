package net.sf.jmimemagic;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MagicDtdResolver implements EntityResolver {
	private static final String MAGIC_DTD_PUBLIC_ID = "-//jmimemagic//DTD magic config 1.0//EN";
	
	private InputStream dtdStream;
	
	public MagicDtdResolver() {
		this(MagicDtdResolver.class.getResourceAsStream("/magic_1_0.dtd"));
	}
	
	public MagicDtdResolver(InputStream dtdStream) {
		super();
		this.dtdStream = dtdStream;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (MAGIC_DTD_PUBLIC_ID.equals(publicId)) {
			return new InputSource(dtdStream);
		}
		return null;
	}

}
