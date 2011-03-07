package railo.runtime.type.trace;

import java.util.List;

import railo.commons.io.log.LogResource;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;

public class TOArray extends TOCollection implements Array {

	private static final long serialVersionUID = 5130217962217368552L;
	
	private final Array arr;

	protected TOArray(Array arr, String label, LogResource log) {
		super(arr, label, log);
		this.arr=arr;
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		log(null);
		return arr.getDimension();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int key, Object defaultValue) {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.get(key, defaultValue), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object getE(int key) throws PageException {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.getE(key), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int key, Object value) {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.setEL(key, value), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
	public Object setE(int key, Object value) throws PageException {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.setEL(key, value), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public int[] intKeys() {
		log(null);
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
		log(null);
		return TraceObjectSupport.toTraceObject(arr.append(o), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#appendEL(java.lang.Object)
	 */
	public Object appendEL(Object o) {
		log(null);
		return TraceObjectSupport.toTraceObject(arr.appendEL(o), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object o) throws PageException {
		log(null);
		return TraceObjectSupport.toTraceObject(arr.prepend(o), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws PageException {
		log(null);
		arr.resize(to);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder) throws PageException {
		log(null);
		arr.sort(sortType, sortOrder);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		log(null);
		return arr.toArray();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#toList()
	 */
	public List toList() {
		log(null);
		return arr.toList();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int key) throws PageException {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.removeE(key), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int key) {
		log(""+key);
		return TraceObjectSupport.toTraceObject(arr.removeEL(key), label, log);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Array#containsKey(int)
	 */
	public boolean containsKey(int key) {
		log(""+key);
		return arr.containsKey(key);
	}

	public Collection duplicate(boolean deepCopy) {
		log(null);
		return new TOArray((Array)arr.duplicate(deepCopy), label, log);
	}

}
