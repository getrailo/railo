package railo.runtime.functions.closure;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StringListData;

public class Reduce extends BIF {

	private static final long serialVersionUID = -5940580562772523622L;

	public static Object call(PageContext pc , Object obj, UDF udf) throws PageException {
		return _call(pc, obj, udf,null);
	}
	
	public static Object call(PageContext pc , Object obj, UDF udf, Object initalValue) throws PageException {
		return _call(pc, obj, udf,initalValue);
	}
	
	public static Object _call(PageContext pc , Object obj, UDF udf, Object initalValue) throws PageException { 
		
		
		Object value;
		
		// Array
		if(obj instanceof Array) {
			value=invoke(pc, (Array)obj, udf,initalValue);
		}
		// Query
		else if(obj instanceof Query) {
			value=invoke(pc, (Query)obj, udf,initalValue);
		}
		// Struct
		else if(obj instanceof Struct) {
			value=invoke(pc, (Struct)obj, udf,initalValue);
		}
		// other Iteratorable
		else if(obj instanceof Iteratorable) {
			value=invoke(pc, (Iteratorable)obj, udf,initalValue);
		}
		// Map
		else if(obj instanceof java.util.Map) {
			value=invoke(pc, (java.util.Map)obj, udf,initalValue);
		}
		//List
		else if(obj instanceof List) {
			value=invoke(pc, (List)obj, udf,initalValue);
		}
		// Iterator
		else if(obj instanceof Iterator) {
			value=invoke(pc, (Iterator)obj, udf,initalValue);
		}
		// Enumeration
		else if(obj instanceof Enumeration) {
			value=invoke(pc, (Enumeration)obj, udf,initalValue);
		}
		else if(obj instanceof StringListData) {
			value=invoke(pc, (StringListData)obj, udf,initalValue);
		}
		else
			throw new FunctionException(pc, "Filter", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		
		return value;
	}

	private static Object invoke(PageContext pc, Array arr, UDF udf, Object initalValue) throws CasterException, PageException {
		Iterator<Entry<Key, Object>> it = arr.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			initalValue=udf.call(pc, new Object[]{initalValue,e.getValue(),Caster.toDoubleValue(e.getKey().getString()),arr}, true);
		}
		return initalValue;
	}

	private static Object invoke(PageContext pc, StringListData sld, UDF udf, Object initalValue) throws CasterException, PageException {
		Array arr = ListUtil.listToArray(sld.list, sld.delimiter,sld.includeEmptyFieldsx,sld.multiCharacterDelimiter);
		
		Iterator<Entry<Key, Object>> it = arr.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			initalValue=udf.call(pc, new Object[]{initalValue,e.getValue(),Caster.toDoubleValue(e.getKey().getString()),sld.list,sld.delimiter}, true);
		}
		return initalValue;
	}

	private static Object invoke(PageContext pc, Query qry, UDF udf, Object initalValue) throws CasterException, PageException {
		final int pid=pc.getId();
		ForEachQueryIterator it=new ForEachQueryIterator(qry, pid);
		int rowNbr;

		Object row;
		while(it.hasNext()){
			row = it.next();
			rowNbr = qry.getCurrentrow(pid);
			initalValue=udf.call(pc, new Object[]{initalValue,row,Caster.toDoubleValue(rowNbr),qry}, true);
		}
		return initalValue;
	}
	

	private static Object invoke(PageContext pc, List list, UDF udf, Object initalValue) throws CasterException, PageException {
		ListIterator it = list.listIterator();
		Object v;
		int index;
		ArgumentIntKey k;
		while(it.hasNext()){
			index = it.nextIndex();
			k = ArgumentIntKey.init(index);
            v = it.next();
            initalValue=udf.call(pc, new Object[]{initalValue,v,Caster.toDoubleValue(k.getString()),list}, true);
		}
		return initalValue;
	}

	private static Object invoke(PageContext pc, Struct sct, UDF udf, Object initalValue) throws PageException {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			initalValue=udf.call(pc, new Object[]{initalValue,e.getKey().getString(),e.getValue(),sct}, true);
		}
		return initalValue;
	}
	
	private static Object invoke(PageContext pc, java.util.Map map, UDF udf, Object initalValue) throws PageException {
		Iterator<Entry> it = map.entrySet().iterator();
		Entry e;
		while(it.hasNext()){
			e = it.next();
			initalValue=udf.call(pc, new Object[]{initalValue,e.getKey(),e.getValue(),map}, true);
		}
		return initalValue;
	}
	
	private static Object invoke(PageContext pc, Iteratorable i, UDF udf, Object initalValue) throws PageException {
		Iterator<Entry<Key, Object>> it = i.entryIterator();
		
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			initalValue=udf.call(pc, new Object[]{initalValue,e.getKey().getString(),e.getValue()}, true);
		}
		return initalValue;
	}
	
	private static Object invoke(PageContext pc, Iterator it, UDF udf, Object initalValue) throws PageException {
		
		Object v;
		int count=0;
		ArgumentIntKey k;
		while(it.hasNext()){
			v = it.next();
			k = ArgumentIntKey.init(++count);
			initalValue=udf.call(pc, new Object[]{initalValue,v}, true);
		}
		return initalValue;
	}
	
	private static Object invoke(PageContext pc, Enumeration e, UDF udf, Object initalValue) throws PageException {
		
		Object v;
		int count=0;
		ArgumentIntKey k;
		while(e.hasMoreElements()){
			v = e.nextElement();
			k = ArgumentIntKey.init(++count);
			initalValue=udf.call(pc, new Object[]{initalValue,v}, true);
		}
		return initalValue;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, (args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, (args[0]), Caster.toFunction(args[1]), args[2]);
		
		throw new FunctionException(pc, "Reduce", 2, 3, args.length);
	}

}
