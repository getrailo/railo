/**
 * Implements the Cold Fusion Function arraymin
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

public final class QuerySlice implements Function {
	
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

	private static Query get(Query qry, int from, int to) throws PageException {
		String[] columns;
		//print.out(from+"::"+to);
		QueryImpl nq=new QueryImpl(columns=qry.getColumns(),0,qry.getName());
		
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