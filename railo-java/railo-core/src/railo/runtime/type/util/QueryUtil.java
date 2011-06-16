package railo.runtime.type.util;

import railo.commons.lang.SizeOf;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryColumnImpl;

public class QueryUtil {

	public static long sizeOf(QueryColumn column) {
		if(column instanceof QueryColumnImpl){
			return ((QueryColumnImpl)column).sizeOf();
		}
		int len = column.size();
		long size=0;
		for(int i=1;i<=len;i++){
			size+=SizeOf.size(column.get(i,null));
		}
		return size;
	}

	/**
	 * return column names as Key from a query
	 * 
	 * @param qry
	 * @return
	 */
	public static Key[] getColumnNames(Query qry) {
		Query qp = Caster.toQuery(qry,null);
		
		if(qp!=null) return qp.getColumnNames();
		String[] strNames = qry.getColumns();
		Key[] names=new Key[strNames.length];
		for(int i=0;i<names.length;i++){
			names[i]=KeyImpl.init(strNames[i]);
		}
		return names;
	}

}
