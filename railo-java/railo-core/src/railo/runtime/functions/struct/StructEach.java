/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.closure.Each;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class StructEach implements Function {

	private static final long serialVersionUID = 5795152568391831373L;

	public static String call(PageContext pc , Struct sct, UDF udf) throws PageException {
		return _call(pc, sct, udf, false, 20);
	}
	public static String call(PageContext pc , Struct sct, UDF udf, boolean parallel) throws PageException {
		return _call(pc, sct, udf, parallel, 20);
	}

	public static String call(PageContext pc , Struct sct, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, sct, udf, parallel, (int)maxThreads);
	}
	private static String _call(PageContext pc , Struct sct, UDF udf, boolean parallel, int maxThreads) throws PageException {
		ExecutorService execute=null;
		List<Future<String>> futures=null;
		if(parallel) {
			execute = Executors.newFixedThreadPool(maxThreads);
			futures=new ArrayList<Future<String>>();
		}
		Each.invoke(pc, sct, udf,execute,futures);
		
		if(parallel) Each.afterCall(pc,futures);
		
		return null;
	}
}