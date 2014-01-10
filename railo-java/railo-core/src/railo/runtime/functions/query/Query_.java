/**
 * creates a CFML query Column
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

public final class Query_ extends BIF {

	private static final long serialVersionUID = -3496695992298284984L;

	public static Query call(PageContext pc , Object[] arr) throws DatabaseException {
		String[] names=new String[arr.length];
		Array[] columns=new Array[arr.length];
		int count=0;
		
		for(int i=0;i<arr.length;i++) {
			if(arr[i] instanceof FunctionValue) {
				FunctionValue vf = (FunctionValue)arr[i];
				if(vf.getValue() instanceof Array) {
					names[count]=vf.getNameAsString();
					columns[count]=(Array) vf.getValue();
					count++;
				}
				else throw new DatabaseException("invalid argument for function query, only array as value are allowed","example: query(column1:array(1,2,3))",null,null);
			}
			else throw new DatabaseException("invalid argument for function query, only named argument are allowed","example: query(column1:array(1,2,3))",null,null);
		}
		Query query=new QueryImpl(names,columns,"query");
		return query;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,(Object[])args[0]);
	}
}