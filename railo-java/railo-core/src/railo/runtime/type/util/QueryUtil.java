package railo.runtime.type.util;

import railo.commons.lang.SizeOf;
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
}
