package railo.transformer.library.tag;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

 

/**
 * Hilfsklasse fuer die TagLibFactory, diese Klasse definiert den DTDHandler fuer den Sax Parser. 
 * Die Klasse laedt wenn moeglich die DTD, anhand der Public-id vom lokalen System.
 * 
 * @see  org.xml.sax.EntityResolver
 */
public final class TagLibEntityResolver implements EntityResolver {
	/**
	 * Definert den DTD welche eine TLD validieren kann
	 */
	public final static String RAILO_DTD_1_0=	"/resource/dtd/web-cfmtaglibrary_1_0.dtd";

	public final static String SUN_DTD_1_0=	"/resource/dtd/web-jsptaglibrary_1_0.dtd";
	public final static String SUN_DTD_1_1=	"/resource/dtd/web-jsptaglibrary_1_1.dtd";
	public final static String SUN_DTD_1_2=	"/resource/dtd/web-jsptaglibrary_1_2.dtd";
		
	/**
	 * Laedt die DTD vom lokalen System.
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) {
		
		if ( publicId.equals( "-//Railo//DTD CFML Tag Library 1.0//EN" ) ) {
			return new InputSource( getClass().getResourceAsStream(RAILO_DTD_1_0) );
		}
		else if ( publicId.equals( "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN" ) ) {
			return new InputSource( getClass().getResourceAsStream(SUN_DTD_1_1) );
		}
		else if ( publicId.equals( "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN" ) ) {
			return new InputSource( getClass().getResourceAsStream(SUN_DTD_1_2) );
		}
		return null;
		// -//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN
		// http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd
	}

 }