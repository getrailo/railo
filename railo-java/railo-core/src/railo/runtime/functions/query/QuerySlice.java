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
 * Implements the CFML Function arraymin
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

public final class QuerySlice extends BIF {

	private static final long serialVersionUID = -2760070317171532995L;

	public static Query call(PageContext pc , Query qry,double offset) throws PageException {
		return call(pc , qry, offset,0);
	}
	public static Query call(PageContext pc , Query qry,double offset,double length) throws PageException {
			
		int len=qry.getRecordcount();
		if(offset>0) {
			if(len<offset)throw new FunctionException(pc,"querySlice",2,"offset","offset can be greater than recordcount of the query");
			
			int to=0;
			if(length>0)to=(int)(offset+length-1);
			else if(length<=0)to=(int)(len+length);
			if(len<to)
				throw new FunctionException(pc,"querySlice",3,"length","offset+length can be greater than recordcount of the query");
			
			return get(qry,(int)offset,to);
		}
		return call(pc ,qry,len+offset,length);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toQuery(args[0]),Caster.toDoubleValue(args[1]));
		return call(pc,Caster.toQuery(args[0]),Caster.toDoubleValue(args[1]),Caster.toDoubleValue(args[2]));
	}

	private static Query get(Query qry, int from, int to) throws PageException {
		Collection.Key[] columns;
		//print.out(from+"::"+to);
		Query nq=new QueryImpl(columns=qry.getColumnNames(),0,qry.getName());
		
		int row=1;
		for(int i=from;i<=to;i++) {nq.addRow();
			for(int y=0;y<columns.length;y++) {
				nq.setAt(columns[y], row, qry.getAt(columns[y], i));
			}
			row++;
		}
		return nq;
	}
	

}