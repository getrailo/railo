package railo.runtime.functions.closure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import railo.commons.lang.Pair;
import railo.runtime.PageContext;
import railo.runtime.concurrency.Data;
import railo.runtime.concurrency.UDFCaller2;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StringListData;

public class Filter extends BIF {

	private static final long serialVersionUID = -5940580562772523622L;

	public static Object call(PageContext pc , Object obj, UDF udf) throws PageException {
		return _call(pc, obj, udf, false,20);
	}
	public static Object call(PageContext pc , Object obj, UDF udf, boolean parallel) throws PageException {
		return _call(pc, obj, udf, parallel, 20);
	}
	public static Object call(PageContext pc , Object obj, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, obj, udf, parallel, (int)maxThreads);
	}
	
	public static Collection _call(PageContext pc , Object obj, UDF udf, boolean parallel, int maxThreads) throws PageException { 
		
		ExecutorService execute=null;
		List<Future<Data<Pair<Object, Object>>>> futures=null;
		if(parallel) {
			execute = Executors.newFixedThreadPool(maxThreads);
			futures=new ArrayList<Future<Data<Pair<Object, Object>>>>();
		}
		
		Collection coll;

		// Array
		if(obj instanceof Array) {
			coll=invoke(pc, (Array)obj, udf,execute,futures);
		}
		// Query
		else if(obj instanceof Query) {
			coll=invoke(pc, (Query)obj, udf,execute,futures);
		}
		// Struct
		else if(obj instanceof Struct) {
			coll=invoke(pc, (Struct)obj, udf,execute,futures);
		}
		// other Iteratorable
		else if(obj instanceof Iteratorable) {
			coll=invoke(pc, (Iteratorable)obj, udf,execute,futures);
		}
		// Map
		else if(obj instanceof java.util.Map) {
			coll=invoke(pc, (java.util.Map)obj, udf,execute,futures);
		}
		//List
		else if(obj instanceof List) {
			coll=invoke(pc, (List)obj, udf,execute,futures);
		}
		// Iterator
		else if(obj instanceof Iterator) {
			coll=invoke(pc, (Iterator)obj, udf,execute,futures);
		}
		// Enumeration
		else if(obj instanceof Enumeration) {
			coll=invoke(pc, (Enumeration)obj, udf,execute,futures);
		}
		// String List
		else if(obj instanceof StringListData) {
			coll=invoke(pc, (StringListData)obj, udf,execute,futures);
		}
		else
			throw new FunctionException(pc, "Filter", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		if(parallel) afterCall(pc,coll,futures);
		
		return coll;
	}

	private static Collection invoke(PageContext pc, Array arr, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws CasterException, PageException {
		Array rtn=new ArrayImpl();
		Iterator<Entry<Key, Object>> it = arr.entryIterator();
		Entry<Key, Object> e;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			e = it.next();
			res=_inv(pc, udf, new Object[]{e.getValue(),Caster.toDoubleValue(e.getKey().getString()),arr},e.getKey(),e.getValue(), es, futures);
			if(!async && Caster.toBooleanValue(res)) {
				rtn.append(e.getValue());
			}
		}
		return rtn;
	}
	
	private static Collection invoke(PageContext pc, StringListData sld, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws CasterException, PageException {
		Array arr = sld.includeEmptyFields?ListUtil.listToArray(sld.list, sld.delimiter):
			ListUtil.listToArrayRemoveEmpty(sld.list, sld.delimiter);
		
		Array rtn=new ArrayImpl();
		Iterator<Entry<Key, Object>> it = arr.entryIterator();
		Entry<Key, Object> e;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			e = it.next();
			res=_inv(pc, udf, new Object[]{e.getValue(),Caster.toDoubleValue(e.getKey().getString()),sld.list,sld.delimiter},e.getKey(),e.getValue(), es, futures);
			if(!async && Caster.toBooleanValue(res)) {
				rtn.append(e.getValue());
			}
		}
		return rtn;
	}

	private static Collection invoke(PageContext pc, Query qry, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws CasterException, PageException {
		Key[] colNames = qry.getColumnNames();
		Query rtn=new QueryImpl(colNames,0,qry.getName());
		final int pid=pc.getId();
		ForEachQueryIterator it=new ForEachQueryIterator(qry, pid);
		int rowNbr;
		Object row;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			row = it.next();
			rowNbr = qry.getCurrentrow(pid);
			
			res=_inv(pc, udf, new Object[]{row,Caster.toDoubleValue(rowNbr),qry},rowNbr,qry, es, futures);
			if(!async && Caster.toBooleanValue(res)) {
				addRow(qry,rtn,rowNbr);
			}
		}
		return rtn;
	}
	

	private static void addRow(Query src, Query trg, int srcRow) {
		Key[] colNames=src.getColumnNames();
		int trgRow = trg.addRow();
		for(int c=0;c<colNames.length;c++){
			trg.setAtEL(colNames[c], trgRow, src.getAt(colNames[c], srcRow,null));
		}
	}
	private static Collection invoke(PageContext pc, List list, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws CasterException, PageException {
		Array rtn=new ArrayImpl();
		ListIterator it = list.listIterator();
		boolean async=es!=null;
		Object res,v;
		int index;
		ArgumentIntKey k;
		while(it.hasNext()){
			index = it.nextIndex();
			k = ArgumentIntKey.init(index);
            v = it.next();
			res=_inv(pc, udf, new Object[]{v,Caster.toDoubleValue(k.getString()),list},k,v, es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.append(v);
		}
		return rtn;
	}

	private static Struct invoke(PageContext pc, Struct sct, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		Struct rtn=new StructImpl();
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			e = it.next();
			res=_inv(pc, udf, new Object[]{e.getKey().getString(),e.getValue(),sct},e.getKey(),e.getValue(), es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.set(e.getKey(),e.getValue());
		}
		return rtn;
	}
	
	private static Struct invoke(PageContext pc, java.util.Map map, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		Struct rtn=new StructImpl();
		Iterator<Entry> it = map.entrySet().iterator();
		Entry e;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			e = it.next();
			res=_inv(pc, udf, new Object[]{e.getKey(),e.getValue(),map},e.getKey(),e.getValue(), es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.set(KeyImpl.toKey(e.getKey()),e.getValue());
		}
		return rtn;
	}
	
	private static Struct invoke(PageContext pc, Iteratorable i, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		Iterator<Entry<Key, Object>> it = i.entryIterator();
		
		Struct rtn=new StructImpl();
		Entry<Key, Object> e;
		boolean async=es!=null;
		Object res;
		while(it.hasNext()){
			e = it.next();
			res=_inv(pc, udf, new Object[]{e.getKey().getString(),e.getValue()},e.getKey(),e.getValue(), es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.set(e.getKey(),e.getValue());
		}
		return rtn;
	}
	
	private static Array invoke(PageContext pc, Iterator it, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		
		Array rtn=new ArrayImpl();
		Object v;
		boolean async=es!=null;
		Object res;
		int count=0;
		ArgumentIntKey k;
		while(it.hasNext()){
			v = it.next();
			k = ArgumentIntKey.init(++count);
			res=_inv(pc, udf, new Object[]{v},k,v, es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.append(v);
		}
		return rtn;
	}
	
	private static Array invoke(PageContext pc, Enumeration e, UDF udf, ExecutorService es, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		
		Array rtn=new ArrayImpl();
		Object v;
		boolean async=es!=null;
		Object res;
		int count=0;
		ArgumentIntKey k;
		while(e.hasMoreElements()){
			v = e.nextElement();
			k = ArgumentIntKey.init(++count);
			res=_inv(pc, udf, new Object[]{v},k,v, es, futures);
			if(!async && Caster.toBooleanValue(res)) rtn.append(v);
		}
		return rtn;
	}
	
	private static Object _inv(PageContext pc, UDF udf, Object[] args,Object key,Object value,ExecutorService es,List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		if(es==null) {
			return udf.call(pc, args, true);
		}
		futures.add(es.submit(new UDFCaller2<Pair<Object, Object>>(pc, udf, args, new Pair<Object, Object>(key,value),true)));
		return null;
	}
	
	public static void afterCall(PageContext pc, Collection coll, List<Future<Data<Pair<Object, Object>>>> futures) throws PageException {
		try{
			boolean isArray=false;
			boolean isQuery=false;
			if(coll instanceof Array) isArray=true;
			else if(coll instanceof Query) isQuery=true;
			
			
			Iterator<Future<Data<Pair<Object, Object>>>> it = futures.iterator();
			Data<Pair<Object, Object>> d;
			while(it.hasNext()){
				d = it.next().get();
				
				if(Caster.toBooleanValue(d.result)) { 
					if(isArray) 
						((Array)coll).append(d.passed.getValue());
					else if(isQuery) 
						addRow(
								(Query)d.passed.getValue()
								,(Query)coll
								,Caster.toIntValue(d.passed.getName()));
					else 
						coll.set(KeyImpl.toKey(d.passed.getName()),d.passed.getValue());
				}
				
				pc.write(d.output);
			}
		}
		catch(Exception e){
			throw Caster.toPageException(e);
		}
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		
		if(args.length==2)
			return call(pc, (args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, (args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, (args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc, "Filter", 2, 4, args.length);
	}
}
