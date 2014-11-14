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
package railo.runtime.sql.exp;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;

public interface Column extends Expression {
	public String getFullName();
	public Collection.Key getColumn();
	public String getTable();
	public boolean hasBracked();
	public void hasBracked(boolean b);
	public int getColumnIndex();
	public Object getValue(Query qry, int row) throws PageException;
	public Object getValue(Query qry, int row, Object defaultValue);
	
}
