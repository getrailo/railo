package railo.runtime.type.trace;

import java.util.Comparator;
import java.util.List;

import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;

public class TOArray extends TOCollection implements Array {

	private static final long serialVersionUID = 5130217962217368552L;
	
	private final Array arr;

	protected TOArray(Debugger debugger,Array arr,int type,String category,String text) {
		super(debugger,arr, type, category, text);
		this.arr=arr;
	}

	@Override
	public int getDimension() {
		log();
		return arr.getDimension();
	}

	@Override
	public Object get(int key, Object defaultValue) {
		log(""+key);
		return arr.get(key, defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,arr.get(key, defaultValue),type,category,text);
	}

	@Override
	public Object getE(int key) throws PageException {
		log(""+key);
		return arr.getE(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.getE(key),type,category,text);
	}

	@Override
	public Object setEL(int key, Object value) {
		log(""+key,value);
		return arr.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,arr.setEL(key, value),type,category,text);
	}

	@Override
	public Object setE(int key, Object value) throws PageException {
		log(""+key,value);
		return arr.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,arr.setEL(key, value),type,category,text);
	}

	@Override
	public int[] intKeys() {
		log();
		return arr.intKeys();
	}

	@Override
	public boolean insert(int key, Object value) throws PageException {
		log(""+key);
		return arr.insert(key, value);
	}

	@Override
	public Object append(Object o) throws PageException {
		log(o.toString());
		return arr.append(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.append(o),type,category,text);
	}

	@Override
	public Object appendEL(Object o) {
		log(o.toString());
		return arr.appendEL(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.appendEL(o),type,category,text);
	}

	@Override
	public Object prepend(Object o) throws PageException {
		log();
		return arr.prepend(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.prepend(o),type,category,text);
	}

	@Override
	public void resize(int to) throws PageException {
		log();
		arr.resize(to);
	}

	@Override
	public void sort(String sortType, String sortOrder) throws PageException {
		log();
		arr.sort(sortType, sortOrder);
	}

	@Override
	public void sort(Comparator comp) throws PageException {
		log();
		arr.sort(comp);
	}

	@Override
	public Object[] toArray() {
		log();
		return arr.toArray();
	}

	@Override
	public List toList() {
		log();
		return arr.toList();
	}

	@Override
	public Object removeE(int key) throws PageException {
		log(""+key);
		return arr.removeE(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.removeE(key),type,category,text);
	}

	@Override
	public Object removeEL(int key) {
		log(""+key);
		return arr.removeEL(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.removeEL(key),type,category,text);
	}

	@Override
	public boolean containsKey(int key) {
		log(""+key);
		return arr.containsKey(key);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		log();
		return new TOArray(debugger,(Array)Duplicator.duplicate(arr,deepCopy),type,category,text);
	}

	@Override
	public java.util.Iterator<Object> getIterator() {
    	return valueIterator();
    } 

}
