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
package railo.runtime.net.rpc;

import javax.xml.namespace.QName;

public final class RPCConstants {
	
	
	public static final QName COMPONENT = new QName("http://rpc.xml.coldfusion","Component");
	public static QName QUERY_QNAME=new QName("http://rpc.xml.coldfusion","QueryBean");
	public static QName ARRAY_QNAME=new QName("http://rpc.xml.coldfusion","Array");
    //private static QName componentQName=new QName("http://components.test.jm","address");
    //private static QName dateTimeQName=new QName("http://www.w3.org/2001/XMLSchema","dateTime");
    public static final QName STRING_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "string");
}
