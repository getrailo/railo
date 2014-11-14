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
package railo.intergral.fusiondebug.server.type.qry;

import java.util.ArrayList;
import java.util.List;

import railo.intergral.fusiondebug.server.type.FDValueNotMutability;
import railo.intergral.fusiondebug.server.type.FDVariable;
import railo.intergral.fusiondebug.server.type.simple.FDSimpleVariable;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDQuery extends FDValueNotMutability {

	private static final int INTERVAL = 10;
	private ArrayList children=new ArrayList();
	private Query qry;
	
	public FDQuery(IFDStackFrame frame,Query qry){
		this.qry=qry;
		
		// columns
		String[] strColumns = qry.getColumns();
		List lstColumns=new ArrayList();
		String type;
		for(int i=0;i<strColumns.length;i++){
			type=qry.getColumn(strColumns[i],null).getTypeAsString();
			//else type="";
			lstColumns.add(new FDSimpleVariable(frame,strColumns[i],type,null));
		}
		children.add(new FDSimpleVariable(frame,"Columns",Caster.toString(strColumns.length),lstColumns));
		
		// rows
		int rowcount=qry.getRowCount();
		List lstRows=new ArrayList();//,values;
		fill(frame,qry,lstRows,1,rowcount-1,strColumns);
		children.add(new FDSimpleVariable(frame,"Rows",Caster.toString(rowcount),lstRows));
	}
	
	private static void fill(IFDStackFrame frame, Query qry, List lstRows, int start,int len, String[] strColumns) {
		int to=start+len;
		int interval = INTERVAL;
		while(interval*interval<len)
			interval*=interval;
		if(len>interval){
			int max;
			for(int i=start;i<to;i+=interval)	{
				max=(i+interval)<to?(interval-1):to-i;
				ArrayList group=new ArrayList();
				lstRows.add(new FDSimpleVariable(frame,"Rows","["+i+"-"+((i+max))+"]",group));
				fill(frame, qry, group, i, max, strColumns);
			}
		}
		else {
			ArrayList values;
			for(int r=start;r<=to;r++){
				values=new ArrayList();
				for(int c=0;c<strColumns.length;c++){
					values.add(new FDVariable(frame,strColumns[c],new FDQueryNode(frame,qry,r,strColumns[c])));
				}
				lstRows.add(new FDSimpleVariable(frame,"Row","["+r+"]",values));
			}
		}
	}


	@Override
	public List getChildren() {
		return children;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Query(Columns:"+qry.getColumns().length+", Rows:"+qry.getRecordcount()+")";
	}

}
