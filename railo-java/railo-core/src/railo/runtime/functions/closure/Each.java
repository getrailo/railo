/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.closure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.runtime.PageContext;
import railo.runtime.concurrency.Data;
import railo.runtime.concurrency.UDFCaller2;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StringListData;


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
		List<Future<Data<Object>>> futures=null;
		if(parallel) {
			execute = Executors.newFixedThreadPool(maxThreads);
			futures=new ArrayList<Future<Data<Object>>>();
		}
		
		// Array
		if(obj instanceof Array) {
			invoke(pc, (Array)obj, udf,execute,futures);
		}

		// Query
		else if(obj instanceof Query) {
			invoke(pc, (Query)obj, udf,execute,futures);
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
				_call(pc,udf,new Object[]{e.getKey(),e.getValue(),obj},execute,futures);
				//udf.call(pc, new Object[]{e.getKey(),e.getValue()}, true);
			}
		}
		//List
		else if(obj instanceof List) {
			ListIterator it = ((List)obj).listIterator();
			int index;
			while(it.hasNext()){
				index=it.nextIndex();
				_call(pc,udf,new Object[]{it.next(),new Double(index),obj},execute,futures);
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
		// StringListData
		else if(obj instanceof StringListData) {
			invoke(pc, (StringListData)obj, udf, execute, futures);
		}

		else
			throw new FunctionException(pc, "Each", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		if(parallel) afterCall(pc,futures);
			
		
		return null;
	}
	

	public static void afterCall(PageContext pc, List<Future<Data<Object>>> futures) throws PageException {
		try{
			Iterator<Future<Data<Object>>> it = futures.iterator();
			//Future<String> f;
			while(it.hasNext()){
				pc.write(it.next().get().output);
			}
		}
		catch(Exception e){
			throw Caster.toPageException(e);
		}
	}
	public static void invoke(PageContext pc , Array array, UDF udf,ExecutorService execute,List<Future<Data<Object>>> futures) throws PageException {
		Iterator<Entry<Key, Object>> it = array.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e=it.next();
			_call(pc,udf,new Object[]{e.getValue(),Caster.toDoubleValue(e.getKey().getString()),array},execute,futures);
			//udf.call(pc, new Object[]{it.next()}, true);
		}
	}

	public static void invoke(PageContext pc ,Query qry, UDF udf,ExecutorService execute,List<Future<Data<Object>>> futures) throws PageException {
		final int pid=pc.getId();
		ForEachQueryIterator it=new ForEachQueryIterator(qry, pid);
		try{
			Object row;
			//Entry<Key, Object> e;
			while(it.hasNext()){
				row=it.next();
				_call(pc,udf,new Object[]{row,Caster.toDoubleValue(qry.getCurrentrow(pid)),qry},execute,futures);
			}
		}
		finally {
			it.reset();
		}
	}

	public static void invoke(PageContext pc , Iteratorable coll, UDF udf,ExecutorService execute,List<Future<Data<Object>>> futures) throws PageException {
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			_call(pc,udf,new Object[]{e.getKey().getString(),e.getValue(),coll},execute,futures);
			//udf.call(pc, new Object[]{e.getKey().getString(),e.getValue()}, true);
		}
	}
	
	private static void invoke(PageContext pc, StringListData sld, UDF udf, ExecutorService execute, List<Future<Data<Object>>> futures) throws PageException {
		Array arr = sld.includeEmptyFields?ListUtil.listToArray(sld.list, sld.delimiter):
			ListUtil.listToArrayRemoveEmpty(sld.list, sld.delimiter);
		
		Iterator<Entry<Key, Object>> it = arr.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e=it.next();
			_call(pc,udf,new Object[]{e.getValue(),Caster.toDoubleValue(e.getKey().getString()),sld.list,sld.delimiter},execute,futures);
		}
		
	}
	
	private static void _call(PageContext pc, UDF udf, Object[] args,ExecutorService es,List<Future<Data<Object>>> futures) throws PageException {
		if(es==null) {
			udf.call(pc, args, true);
			return;
		}
		futures.add(es.submit(new UDFCaller2<Object>(pc, udf, args,null, true)));
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		
		if(args.length==2)
			return call(pc, args[0], Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, args[0], Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, args[0], Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc, "Each", 2, 4, args.length);
		
		
	}
	
}