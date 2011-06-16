/**
 * Implements the Cold Fusion Function queryaddcolumn
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryPro;

public final class QuerySetColumn implements Function {
	public static String call(PageContext pc , Query query, String columnName,String newColumnName) throws PageException {
		columnName=columnName.trim();
		newColumnName=newColumnName.trim();
		Collection.Key src=KeyImpl.init(columnName);
		Collection.Key trg=KeyImpl.init(newColumnName);
		
		QueryPro qp = Caster.toQueryPro(query,null);
		if(qp!=null) qp.rename(src, trg);
		else {
			QueryColumn qc = query.removeColumn(src);
			Array content=new ArrayImpl();
			int len=qc.size();
			for(int i=1;i<=len;i++){
				content.setE(i, qc.get(i));
			}
			query.addColumn(trg, content, qc.getType());
		}
		return null;
	}
}