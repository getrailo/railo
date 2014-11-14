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
package railo.runtime.db;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.sql.SQLParserException;
import railo.runtime.sql.SelectParser;
import railo.runtime.sql.Selects;
import railo.runtime.sql.exp.Column;

public class HSQLUtil2 {

	private Selects selects;

	public HSQLUtil2(SQL sql) throws SQLParserException {
		selects = new SelectParser().parse(sql.getSQLString());
	}

	public HSQLUtil2(Selects selects) {
		this.selects = selects;
	}

	public boolean isUnion() {
		return selects.getSelects().length>1;
	}

	public Set<String> getInvokedTables() {
		HashSet<String> set=new HashSet<String>();
		Column[] tables = selects.getTables();
		for(int i=0;i<tables.length;i++) {
			set.add(tables[i].getFullName());
		}		
		return set;
	}

}
