/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.library.function;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

 

/**
 * Hilfsklasse fuer die FunctionLibFactory, diese Klasse definiert den DTDHandler fuer den Sax Parser. 
 * Die Klasse laedt wenn moeglich die DTD, anhand der Public-id vom lokalen System.
 * 
 * @see  org.xml.sax.EntityResolver
 */
public final class FunctionLibEntityResolver implements EntityResolver {
	/**
	 * Definert den DTD welche eine FLD validieren kann
	 */
	public final static String DTD_1_0=	"/resource/dtd/web-cfmfunctionlibrary_1_0.dtd";
		
	/**
	 * Laedt die DTD vom lokalen System.
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
	public InputSource resolveEntity(String publicId, String systemId) {
		if ( publicId.equals( "-//Railo//DTD CFML Function Library 1.0//EN" ) ) {
			return new InputSource( getClass().getResourceAsStream(DTD_1_0) );
		}
		return null;
	}

 }