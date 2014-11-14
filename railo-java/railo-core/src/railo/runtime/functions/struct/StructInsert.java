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
 * Implements the CFML Function structinsert
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructInsert extends BIF {
	
	private static final long serialVersionUID = 4244527243856690926L;
	
	public static boolean call(PageContext pc , Struct struct, String key, Object value) throws PageException {
		return call(pc , struct, key, value, false);
	}
	
	public static boolean call(PageContext pc , Struct struct, String strKey, Object value, boolean allowoverwrite) throws PageException {
		Key key = KeyImpl.init(strKey);
		if(allowoverwrite) { 
			struct.set(key,value);
		}
		else {
			if(struct.get(key,null)!=null) throw new ExpressionException("key ["+key+"] already exist in struct");
			struct.set(key,value);
		}
		return true;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==4) return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),args[2],Caster.toBooleanValue(args[3]));
		return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),args[2]);
	}
}