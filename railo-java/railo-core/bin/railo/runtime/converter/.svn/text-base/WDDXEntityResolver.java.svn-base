package railo.runtime.converter;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

 

/**
 * this class defines the dtd for WDDX
 * @see  org.xml.sax.EntityResolver
 */
public final class WDDXEntityResolver implements EntityResolver {
	/**
	 * constant for the DTD File for WDDX
	 */
	public final static String WDDX=	"/railo/runtime/converter/wddx.dtd";
		
	/**
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) {
		return new InputSource( getClass().getResourceAsStream(WDDX) );
	}

 }