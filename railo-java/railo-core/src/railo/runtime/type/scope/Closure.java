package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public class Closure extends ScopeSupport implements Variables {
	
	private static final Object NULL = new Object();
	private Argument arg;
	private Local local;
	private Variables var;
	private boolean debug;
	private Undefined und; 

	public Closure(PageContext pc,Argument arg, Local local,Variables var ){
		super("variables",SCOPE_VARIABLES,Struct.TYPE_REGULAR);
		arg.setBind(true);
		local.setBind(true);
		var.setBind(true);
		und = pc.undefinedScope();
		this.arg=arg;
		this.local=local;
		this.var=var;
		this.debug=pc.getConfig().debug();
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

	@Override
	public void release() {
	}

	@Override
	public void release(PageContext pc) {
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
		if(local.containsKey(key))
			return local.remove(key);
		return var.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		if(local.containsKey(key))
			return local.removeEL(key);
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
		Object value = local.get(key,null);
		if(value!=null) return value;
		value=arg.get(key,null);
		if(value!=null) {
			if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
			return value;
		}
		
		value= var.get(key);
		if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object value = local.get(key,null);
		if(value!=null) return value;
		value=arg.get(key,null);
		if(value!=null) {
			if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
			return value;
		}
		value= var.get(key,defaultValue);
		if(value!=null && debug && value!=defaultValue) {
			UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		}
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		if(und.getLocalAlways() || local.containsKey(key))     return local.set(key,value);
	    if(arg.containsFunctionArgumentKey(key))  {
	    	if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
	    	return arg.set(key,value);
	    }
	    if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return var.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
	    if(und.getLocalAlways() || local.containsKey(key))     return local.setEL(key,value);
        if(arg.containsFunctionArgumentKey(key))  {
        	if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
        	return arg.setEL(key,value);
        }
	    	
		if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return var.setEL(key,value);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new Closure(ThreadLocalPageContext.get(),(Argument)Duplicator.duplicate(arg,deepCopy), (Local)Duplicator.duplicate(local,deepCopy), (Variables)Duplicator.duplicate(var,deepCopy));
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
	
	@Override
	public Iterator<Object> valueIterator() {
		return var.valueIterator();
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
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		
		DumpTable dt= (DumpTable) super.toDumpData(pageContext, maxlevel, properties);
		dt.setTitle("Closure Variable Scope");
		return dt;
	}


}
