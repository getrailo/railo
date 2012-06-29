package railo.runtime.functions.closure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.commons.io.DevNullOutputStream;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.UDF;


public final class EachParallel implements Function {

	private static final long serialVersionUID = -7844468485291635225L;
	
	private static ExecutorService executorService;
	

	public static String call(PageContext pc , Object data, UDF udf) throws PageException {
		return call(pc, data, udf, 5);
	}
	
	public static String call(PageContext pc , Object data, UDF udf, double nbrOfThreads) throws PageException {
		if(nbrOfThreads<=0)
			throw new FunctionException(pc, "Each", 3, "numberOfThreads", "value must be a number greater or or equal to 1");
		
		// create service when not exist
		if(executorService==null) {
			executorService=Executors.newFixedThreadPool((int)nbrOfThreads);
		}
		
		// build the tasks
		List<Future<Object>> list = build(pc, executorService, data, udf);

		// execute the tasks
		Iterator<Future<Object>> it = list.iterator();
		while(it.hasNext()){
			try {
				it.next().get();
			} catch (Exception e) {
				throw Caster.toPageException(e);
			}
		}
		return null;
	}
	
	public static List<Future<Object>> build(PageContext pc ,ExecutorService service, Object obj, UDF udf) throws FunctionException {
		List<Future<Object>> list=new ArrayList<Future<Object>>();
		// Array
		if(obj instanceof Array) {
			Iterator<Object> it = ((Array)obj).valueIterator();
			while(it.hasNext()){
				list.add(
						service.submit(
								new Task(
										pc,
										udf,
										new Object[]{
												it.next()})));
			}
		}
		// other Iteratorable
		else if(obj instanceof Iteratorable) {
			Iterator<Entry<Key, Object>> it = ((Iteratorable)obj).entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()){
				e = it.next();
				list.add(service.submit(new Task(pc,udf,new Object[]{e.getKey().getString(),e.getValue()})));
			}
		}
		// Map
		else if(obj instanceof Map) {
			Iterator it = ((Map)obj).entrySet().iterator();
			Entry e;
			while(it.hasNext()){
				e = (Entry) it.next();
				list.add(service.submit(new Task(pc,udf,new Object[]{e.getKey(),e.getValue()})));
			}
		}
		//List
		else if(obj instanceof List) {
			Iterator it = ((List)obj).iterator();
			while(it.hasNext()){
				list.add(service.submit(new Task(pc,udf,new Object[]{it.next()})));
			}
		}
		// Iterator
		else if(obj instanceof Iterator) {
			Iterator it = (Iterator)obj;
			while(it.hasNext()){
				list.add(service.submit(new Task(pc,udf,new Object[]{it.next()})));
			}
		}
		// Enumeration
		else if(obj instanceof Enumeration) {
			Enumeration e = (Enumeration)obj;
			while(e.hasMoreElements()){
				list.add(service.submit(new Task(pc,udf,new Object[]{e.nextElement()})));
			}
		}
		else
			throw new FunctionException(pc, "EachParallel", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		return list;
		
	}
	
	static class Task implements Callable<Object> {

		private UDF udf;
		private Object[] args;
		private PageContext parent;

		public Task(PageContext pc,UDF udf, Object[] args) {
			this.parent=pc;
			this.udf=udf;
			this.args=args; 
		}

		@Override
		public Object call() throws Exception {
			try{
			PageContext pc = ThreadUtil.clonePageContext(parent, DevNullOutputStream.DEV_NULL_OUTPUT_STREAM,false,false,true);
				return udf.call(pc, args, true);
			}
			catch(Throwable t){
			}
			return null;
		}
	}
	
}