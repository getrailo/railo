/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.closure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.runtime.PageContext;
import railo.runtime.concurrency.UDFCaller;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.UDF;


public final class Each extends BIF {

	private static final long serialVersionUID = 1955185705863596525L;

	public static String call(PageContext pc , Object obj, UDF udf) throws PageException {
		return _call(pc, obj, udf, false,20);
	}
	public static String call(PageContext pc , Object obj, UDF udf, boolean parallel) throws PageException {
		return _call(pc, obj, udf, parallel, 20);
	}
	public static String call(PageContext pc , Object obj, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, obj, udf, parallel, (int)maxThreads);
	}
	
	private static String _call(PageContext pc , Object obj, UDF udf, boolean parallel, int maxThreads) throws PageException {
		ExecutorService execute=null;
		List<Future<String>> futures=null;
		if(parallel) {
			execute = Executors.newFixedThreadPool(maxThreads);
			futures=new ArrayList<Future<String>>();
		}
		// Array
		if(obj instanceof Array) {
			invoke(pc, (Array)obj, udf,execute,futures);
		}
		// other Iteratorable
		else if(obj instanceof Iteratorable) {
			invoke(pc, (Iteratorable)obj, udf,execute,futures);
		}
		// Map
		else if(obj instanceof Map) {
			Iterator it = ((Map)obj).entrySet().iterator();
			Entry e;
			while(it.hasNext()){
				e = (Entry) it.next();
				_call(pc,udf,new Object[]{e.getKey(),e.getValue()},execute,futures);
				//udf.call(pc, new Object[]{e.getKey(),e.getValue()}, true);
			}
		}
		//List
		else if(obj instanceof List) {
			Iterator it = ((List)obj).iterator();
			while(it.hasNext()){
				_call(pc,udf,new Object[]{it.next()},execute,futures);
				//udf.call(pc, new Object[]{it.next()}, true);
			}
		}
		// Iterator
		else if(obj instanceof Iterator) {
			Iterator it = (Iterator)obj;
			while(it.hasNext()){
				_call(pc,udf, new Object[]{it.next()},execute,futures);
				//udf.call(pc, new Object[]{it.next()}, true);
			}
		}
		// Enumeration
		else if(obj instanceof Enumeration) {
			Enumeration e = (Enumeration)obj;
			while(e.hasMoreElements()){
				_call(pc,udf,new Object[]{e.nextElement()},execute,futures);
				//udf.call(pc, new Object[]{e.nextElement()}, true);
			}
		}
		else
			throw new FunctionException(pc, "Each", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		if(parallel) afterCall(pc,futures);
			
		
		return null;
	}
	

	public static void afterCall(PageContext pc, List<Future<String>> futures) throws PageException {
		try{
			Iterator<Future<String>> it = futures.iterator();
			//Future<String> f;
			while(it.hasNext()){
				pc.write(it.next().get());
			}
		}
		catch(Exception e){
			throw Caster.toPageException(e);
		}
	}
	public static void invoke(PageContext pc , Array array, UDF udf,ExecutorService execute,List<Future<String>> futures) throws PageException {
		Iterator<Object> it = array.valueIterator();
		while(it.hasNext()){
			_call(pc,udf,new Object[]{it.next()},execute,futures);
			//udf.call(pc, new Object[]{it.next()}, true);
		}
	}

	public static void invoke(PageContext pc , Iteratorable coll, UDF udf,ExecutorService execute,List<Future<String>> futures) throws PageException {
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			_call(pc,udf,new Object[]{e.getKey().getString(),e.getValue()},execute,futures);
			//udf.call(pc, new Object[]{e.getKey().getString(),e.getValue()}, true);
		}
	}
	
	private static void _call(PageContext pc, UDF udf, Object[] args,ExecutorService es,List<Future<String>> futures) throws PageException {
		if(es==null) {
			udf.call(pc, args, true);
			return;
		}
		futures.add(es.submit(new UDFCaller(pc, udf, args, true)));
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==4) return call(pc,args[0],Caster.toFunction(args[1]),Caster.toBooleanValue(args[2]),Caster.toDoubleValue(args[3]));
		if(args.length==3) return call(pc,args[0],Caster.toFunction(args[1]),Caster.toBooleanValue(args[2]));
		return call(pc,args[0],Caster.toFunction(args[1]));
	}
}