package railo.transformer.library.function;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

 

/**
 * Hilfsklasse f�r die FunctionLibFactory, diese Klasse definiert den DTDHandler f�r den Sax Parser. 
 * Die Klasse l�dt wenn m�glich die DTD, anhand der Public-id vom lokalen System.
 * 
 * @see  org.xml.sax.EntityResolver
 */
public final class FunctionLibEntityResolver implements EntityResolver {
	/**
	 * Definert den DTD welche eine FLD validieren kann
	 */
	public final static String DTD_1_0=	"/resource/dtd/web-cfmfunctionlibrary_1_0.dtd";
		
	/**
	 * L�dt die DTD vom lokalen System.
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) {
		if ( publicId.equals( "-//Railo//DTD CFML Function Library 1.0//EN" ) ) {
			return new InputSource( getClass().getResourceAsStream(DTD_1_0) );
		}
		return null;
	}

 }