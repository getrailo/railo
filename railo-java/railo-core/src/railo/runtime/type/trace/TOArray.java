package railo.runtime.type.trace;

import java.util.List;

import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;

public class TOArray extends TOCollection implements Array {

	private static final long serialVersionUID = 5130217962217368552L;
	
	private final Array arr;

	protected TOArray(Debugger debugger,Array arr,int type,String category,String text) {
		super(debugger,arr, type, category, text);
		this.arr=arr;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		log();
		return arr.getDimension();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int key, Object defaultValue) {
		log(""+key);
		return arr.get(key, defaultValue);
		//return TraceObjectSupport.toTraceObject(debugger,arr.get(key, defaultValue),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object getE(int key) throws PageException {
		log(""+key);
		return arr.getE(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.getE(key),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int key, Object value) {
		log(""+key,value);
		return arr.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,arr.setEL(key, value),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
	public Object setE(int key, Object value) throws PageException {
		log(""+key,value);
		return arr.setEL(key, value);
		//return TraceObjectSupport.toTraceObject(debugger,arr.setEL(key, value),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public int[] intKeys() {
		log();
		return arr.intKeys();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
	public boolean insert(int key, Object value) throws PageException {
		log(""+key);
		return arr.insert(key, value);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
	public Object append(Object o) throws PageException {
		log(o.toString());
		return arr.append(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.append(o),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#appendEL(java.lang.Object)
	 */
	public Object appendEL(Object o) {
		log(o.toString());
		return arr.appendEL(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.appendEL(o),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object o) throws PageException {
		log();
		return arr.prepend(o);
		//return TraceObjectSupport.toTraceObject(debugger,arr.prepend(o),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws PageException {
		log();
		arr.resize(to);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder) throws PageException {
		log();
		arr.sort(sortType, sortOrder);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		log();
		return arr.toArray();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#toList()
	 */
	public List toList() {
		log();
		return arr.toList();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int key) throws PageException {
		log(""+key);
		return arr.removeE(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.removeE(key),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int key) {
		log(""+key);
		return arr.removeEL(key);
		//return TraceObjectSupport.toTraceObject(debugger,arr.removeEL(key),type,category,text);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#containsKey(int)
	 */
	public boolean containsKey(int key) {
		log(""+key);
		return arr.containsKey(key);
	}

	public Collection duplicate(boolean deepCopy) {
		log();
		return new TOArray(debugger,(Array)arr.duplicate(deepCopy),type,category,text);
	}

}
