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
package railo.runtime.orm.hibernate.naming;

import railo.runtime.Component;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.orm.naming.NamingStrategy;
import railo.runtime.type.UDF;

public class CFCNamingStrategy implements NamingStrategy {
	
	Component cfc;
	
	
	public CFCNamingStrategy(String cfcName) throws PageException{
		this.cfc=ThreadLocalPageContext.get().loadComponent(cfcName);
	}
	
	
	public Component getComponent() {
		return cfc;
	}


	@Override
	public String convertTableName(String tableName) {
		return call("getTableName",tableName);
	}

	@Override
	public String convertColumnName(String columnName) {
		return call("getColumnName",columnName);
	}

	private String call(String functionName, String name) {
		Object res = cfc.get(functionName,null);
		if(!(res instanceof UDF)) return name;
		
		try {
			return Caster.toString(cfc.call(ThreadLocalPageContext.get(), functionName, new Object[]{name}));
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

	@Override
	public String getType() {
		return "cfc";
	}

}
