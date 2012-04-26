package railo.runtime.type.scope;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.StructSupport;

public class Closure extends StructSupport implements Variables {
	
	private static final Object NULL = new Object();
	private Argument arg;
	private Local local;
	private Variables var;

	public Closure(Argument arg, Local local,Variables var ){
		arg.setBind(true);
		local.setBind(true);
		var.setBind(true);
		
		this.arg=arg;
		this.local=local;
		this.var=var;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return true;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {	
	}

	/**
	 * @see railo.runtime.type.scope.Scope#release()
	 */
	public void release() {
	}

	/**
	 * @see railo.runtime.type.scope.Scope#getType()
	 */
	public int getType() {
		return SCOPE_VARIABLES;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "variables";
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		return var.size();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return var.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return var.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return var.removeEL(key);
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		var.clear();
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Object value = local.get(key,NULL);
		if(value!=NULL) return value;
		value=arg.get(key,NULL);
		if(value!=NULL) return value;
		return var.get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object value = local.get(key,NULL);
		if(value!=NULL) return value;
		value=arg.get(key,NULL);
		if(value!=NULL) return value;
		return var.get(key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return var.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return var.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new Closure((Argument)arg.duplicate(deepCopy), (Local)local.duplicate(deepCopy), (Variables)var.duplicate(deepCopy));
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return get(key,NULL)!=NULL;
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return var.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return var.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return var.entryIterator();
	}
	
	/**
	 * 
	 * @see railo.runtime.type.scope.Variables#setBind(boolean)
	 */
	public void setBind(boolean bind) {}

	/**
	 * @see railo.runtime.type.scope.Variables#isBind()
	 * return always true because this scope is always bind to a closure
	 */
	public boolean isBind() {
		return true;
	}

}
