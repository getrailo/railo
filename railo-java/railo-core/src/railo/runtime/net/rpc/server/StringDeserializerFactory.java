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
package railo.runtime.net.rpc.server;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;

import org.apache.axis.encoding.ser.SimpleDeserializerFactory;

public class StringDeserializerFactory extends SimpleDeserializerFactory {
	public StringDeserializerFactory(Class javaType, QName xmlType) {
		super(javaType, xmlType);
	}
	
	public Deserializer getDeserializerAs(String mechanismType) {
		if (javaType == String.class) {
			return new StringDeserializer(javaType, xmlType);
		}
		
		return super.getDeserializerAs(mechanismType);
	}
}
