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
package railo.runtime.config;

import railo.runtime.type.Null;

public class NullSupportHelper {
	private static final Null NULL=Null.NULL;
	
	protected static boolean fullNullSupport=false;
	
	public static boolean full() {
		return fullNullSupport;
	}
	
	public static Object NULL() {
		return fullNullSupport?NULL:null;
	}
	
	public static Object empty() {
		return fullNullSupport?null:"";
	}
}
