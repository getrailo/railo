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
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.util.QueryUtil;

public final class QueryDeleteColumn extends BIF {

	private static final long serialVersionUID = 5363459913899891827L;

	public static Array call(PageContext pc, Query query, String strColumn) throws PageException {
        return toArray(query.removeColumn(KeyImpl.init(strColumn)));
    }
    
    public static Array toArray(QueryColumn column) throws PageException {
        Array clone=new ArrayImpl();
        int len=column.size();
        clone.resize(len);
        
        for(int i=1;i<=len;i++) {
            clone.setE(i,QueryUtil.getValue(column,i));
        }
        return clone;
    }
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
	}
}