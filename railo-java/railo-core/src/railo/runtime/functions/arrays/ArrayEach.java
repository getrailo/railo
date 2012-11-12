/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.closure.Each;
import railo.runtime.type.Array;
import railo.runtime.type.UDF;


public final class ArrayEach implements Function {
	
	private static final long serialVersionUID = -2271260656749514177L;


	public static String call(PageContext pc , Array array, UDF udf) throws PageException {
		return _call(pc, array, udf, false, 20);
	}
	public static String call(PageContext pc , Array array, UDF udf, boolean parallel) throws PageException {
		return _call(pc, array, udf, parallel, 20);
	}

	public static String call(PageContext pc , Array array, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, array, udf, parallel, (int)maxThreads);
	}
	private static String _call(PageContext pc , Array array, UDF udf, boolean parallel, int maxThreads) throws PageException {
		ExecutorService execute=null;
		List<Future<String>> futures=null;
		if(parallel) {
			execute = Executors.newFixedThreadPool(maxThreads);
			futures=new ArrayList<Future<String>>();
		}
		Each.invoke(pc, array, udf,execute,futures);
		
		if(parallel) Each.afterCall(pc,futures);
		
		return null;
	}
}