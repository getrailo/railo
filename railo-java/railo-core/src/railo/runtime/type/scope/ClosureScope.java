package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public class ClosureScope extends ScopeSupport implements Variables {
	
	private static final Object NULL = new Object();
	private Argument arg;
	private Local local;
	private Variables var;
	private boolean debug;
	private Undefined und; 

	public ClosureScope(PageContext pc,Argument arg, Local local,Variables var ){
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

	@Override
	public boolean isInitalized() {
		return true;
	}

	@Override
	public void initialize(PageContext pc) {	
	}

	@Override
	public void release() {
	}

	@Override
	public void release(PageContext pc) {
	}

	@Override
	public int getType() {
		return SCOPE_VARIABLES;
	}

	@Override
	public String getTypeAsString() {
		return "variables";
	}

	@Override
	public int size() {
		return var.size();
	}

	@Override
	public Key[] keys() {
		return var.keys();
	}

	@Override
	public Object remove(Key key) throws PageException {
		if(local.containsKey(key))
			return local.remove(key);
		return var.remove(key);
	}

	@Override
	public Object removeEL(Key key) {
		if(local.containsKey(key))
			return local.removeEL(key);
		return var.removeEL(key);
	}

	@Override
	public void clear() {
		var.clear();
	}

	@Override
	public Object get(Key key) throws PageException {
		Object value = local.get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()) return value;
		value=arg.get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()) {
			if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
			return value;
		}
		
		value= var.get(key);
		if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return value;
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		Object value = local.get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()) return value;
		value=arg.get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()) {
			if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
			return value;
		}
		value= var.get(key,NullSupportHelper.NULL());
		if(value!=NullSupportHelper.NULL()){
			if(debug) UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
			return value;
		}
		return defaultValue;
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		if(und.getLocalAlways() || local.containsKey(key))     return local.set(key,value);
	    if(arg.containsKey(key))  {
	    	if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
	    	return arg.set(key,value);
	    }
	    if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return var.set(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
	    if(und.getLocalAlways() || local.containsKey(key))     return local.setEL(key,value);
        if(arg.containsKey(key))  {
        	if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),arg.getTypeAsString(), key);
        	return arg.setEL(key,value);
        }
	    	
		if(debug)UndefinedImpl.debugCascadedAccess(ThreadLocalPageContext.get(),var.getTypeAsString(), key);
		return var.setEL(key,value);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return new ClosureScope(ThreadLocalPageContext.get(),(Argument)Duplicator.duplicate(arg,deepCopy), (Local)Duplicator.duplicate(local,deepCopy), (Variables)Duplicator.duplicate(var,deepCopy));
	}

	@Override
	public boolean containsKey(Key key) {
		return get(key,NULL)!=NULL;
	}

	@Override
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
	
	@Override
	public void setBind(boolean bind) {}

	@Override
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
