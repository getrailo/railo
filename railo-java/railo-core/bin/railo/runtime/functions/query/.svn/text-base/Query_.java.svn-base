/**
 * creates a Cold Fusion query Column
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

public final class Query_ implements Function {
	public static Query call(PageContext pc , Object[] arr) throws DatabaseException {
		String[] names=new String[arr.length];
		Array[] columns=new Array[arr.length];
		int count=0;
		
		for(int i=0;i<arr.length;i++) {
			if(arr[i] instanceof FunctionValue) {
				FunctionValue vf = (FunctionValue)arr[i];
				if(vf.getValue() instanceof Array) {
					names[count]=vf.getName();
					columns[count]=(Array) vf.getValue();
					count++;
				}
				else throw new DatabaseException("invalid argument for function query, only array as value are allowed","example: query(column1:array(1,2,3))",null,null,null);
			}
			else throw new DatabaseException("invalid argument for function query, only named argument are allowed","example: query(column1:array(1,2,3))",null,null,null);
		}
		Query query=new QueryImpl(names,columns,"query");
		return query;
	}
}