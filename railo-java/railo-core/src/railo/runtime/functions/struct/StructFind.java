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
/**
 * Implements the CFML Function structfind
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructFind extends BIF {

	private static final long serialVersionUID = 6251275814429295997L;

	public static Object call(PageContext pc, Struct struct, String key) throws PageException {
		return struct.get(KeyImpl.init(key));
	}
	
	public static Object call(PageContext pc, Struct struct, Collection.Key key) throws PageException {
		return struct.get(key);
	}

	public static Object call(PageContext pc, Struct struct, String key, Object defaultValue) throws PageException {

		return struct.get( Caster.toKey(key), defaultValue );
	}

	public static Object call(PageContext pc, Struct struct, Collection.Key key, Object defaultValue) {

		return struct.get( key, defaultValue );
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if ( args.length == 3 )
			return call(pc, Caster.toStruct(args[0]), Caster.toKey(args[1]), args[2] );

		return call(pc,Caster.toStruct(args[0]),Caster.toKey(args[1]));
	}
}