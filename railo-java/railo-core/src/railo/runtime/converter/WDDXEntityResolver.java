package railo.runtime.converter;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public final class WDDXEntityResolver implements EntityResolver {
	/**
	 * constant for the DTD File for WDDX
	 */
	public final static String WDDX=	"/railo/runtime/converter/wddx.dtd";
		
	@Override
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource( getClass().getResourceAsStream(WDDX) );
	}

 }