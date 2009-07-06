package railo.intergral.fusiondebug.server.type.qry;

import java.util.ArrayList;
import java.util.List;

import railo.intergral.fusiondebug.server.type.FDValueNotMutability;
import railo.intergral.fusiondebug.server.type.FDVariable;
import railo.intergral.fusiondebug.server.type.simple.FDSimpleVariable;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDQuery extends FDValueNotMutability {

	private ArrayList children=new ArrayList();
	private Query qry;
	
	public FDQuery(IFDStackFrame frame,Query qry){
		this.qry=qry;
		
		// columns
		String[] strColumns = qry.getColumns();
		List lstColumns=new ArrayList();
		String type;
		for(int i=0;i<strColumns.length;i++){
			if(qry instanceof QueryImpl){
				type=((QueryImpl)qry).getColumn(strColumns[i],null).getTypeAsString();
			}
			else type="";
			lstColumns.add(new FDSimpleVariable(frame,strColumns[i],type,null));
		}
		children.add(new FDSimpleVariable(frame,"Columns",Caster.toString(strColumns.length),lstColumns));
		
		// rows
		int rowcount=qry.getRowCount();
		List lstRows=new ArrayList(),values;
		StringBuffer sb;
		for(int r=1;r<=rowcount;r++){
			values=new ArrayList();
			sb=new StringBuffer();
			for(int c=0;c<strColumns.length;c++){
				if(c>0)sb.append(" - ");
				sb.append(qry.getData(r, c+1));
				values.add(new FDVariable(frame,strColumns[c],new FDQueryNode(frame,qry,r,strColumns[c])));
			}
			lstRows.add(new FDSimpleVariable(frame,"["+r+"]",sb.toString(),values));
		}
		children.add(new FDSimpleVariable(frame,"Rows",Caster.toString(rowcount),lstRows));
	}
	
	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#getChildren()
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#hasChildren()
	 */
	public boolean hasChildren() {
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return FDCaster.serialize(qry);
	}

}
