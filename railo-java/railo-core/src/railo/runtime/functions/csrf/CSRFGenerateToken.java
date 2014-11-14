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
package railo.runtime.functions.csrf;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.storage.StorageScope;

public class CSRFGenerateToken implements Function {
	
	private static final long serialVersionUID = -2411153524245619987L;

	public static String call(PageContext pc) throws PageException {
		return call(pc, null, false);
	}
	public static String call(PageContext pc, String key) throws PageException {
		return call(pc, key, false);
	}
	
	public static String call(PageContext pc, String key, boolean forceNew) throws PageException {
		if(key==null) key="";
		
		return getStorageScope(pc).generateToken(key, forceNew);
	}
	public static StorageScope getStorageScope(PageContext pc) throws PageException {
		Session session = pc.sessionScope();
		if(!(session instanceof StorageScope))
			throw new ExpressionException("this function only works with CF Sessions");
		return (StorageScope) session;
	}
}
