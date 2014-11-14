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
package railo.runtime.functions.orm;


import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;

public class EntityNew {

	public static Object call(PageContext pc, String name) throws PageException {
		return call(pc, name, null);
	}
	
	public static Object call(PageContext pc, String name,Struct properties) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(properties==null)return session.create(pc,name);
		
		Component entity = session.create(pc,name);
		setPropeties(pc,entity,properties,false);
		return entity;
		
	}

	public static void setPropeties(PageContext pc, Component cfc, Struct properties, boolean ignoreNotExisting) throws PageException { 
		if(properties==null) return;
		
		// argumentCollection
		if(properties.size()==1 && properties.containsKey(KeyConstants._argumentCollection) && !cfc.containsKey(KeyConstants._setArgumentCollection)) {
			properties=Caster.toStruct(properties.get(KeyConstants._argumentCollection));
		}
		
		Iterator<Entry<Key, Object>> it = properties.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			if(ignoreNotExisting) {
				try {
					cfc.call(pc, "set"+e.getKey().getString(), new Object[]{e.getValue()});
				}
				catch(Throwable t){}
			}
			else {
				cfc.call(pc, "set"+e.getKey().getString(), new Object[]{e.getValue()});
			}
		}
	}
}
