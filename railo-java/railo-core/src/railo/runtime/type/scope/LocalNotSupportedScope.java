package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

/**
 * 
 */
public final class LocalNotSupportedScope extends StructSupport implements Scope,Local {
	
	private static final long serialVersionUID = 6670210379924188569L;
	
	private static LocalNotSupportedScope instance=new LocalNotSupportedScope();
	private boolean bind;
	
	private LocalNotSupportedScope(){}
	
	public static LocalNotSupportedScope getInstance() {
		return instance;
	}
	
	@Override
	public int size() {
		return 0;
	}
	
	@Override
	public Collection.Key[] keys() {
		return null;
	}
	
	@Override
	public Object removeEL(Key key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object remove(Key key) throws PageException {
	    throw new ExpressionException("Unsupported Context for Local Scope","Can't invoke key "+key+", Local Scope can only invoked inside a Function");
	}
	
	@Override
	public void clear() {
	}
	@Override
	public Object get(Collection.Key key) throws ExpressionException {
		throw new ExpressionException("Unsupported Context for Local Scope","Can't invoke key "+key.getString()+", Local Scope can only invoked inside a Function");
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return defaultValue;
	}

	@Override
	public Object set(Key key, Object value) throws ExpressionException {
		throw new ExpressionException("Unsupported Context for Local Scope","Can't invoke key "+key.getString()+", Local Scope can only invoked inside a Function");
	}
	
    @Override
    public Object setEL(Collection.Key key, Object value) {
		return null;
	}
	
	@Override
	public Iterator<Collection.Key> keyIterator() {
		throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope","Local Scope can only invoked inside a Function"));
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope","Local Scope can only invoked inside a Function"));
	}

	
	@Override
	public Iterator<Object> valueIterator() {
		throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope","Local Scope can only invoked inside a Function"));
	}

	@Override
	public boolean isInitalized() {
		return false;
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
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		throw new PageRuntimeException(new ExpressionException("Unsupported Context for Local Scope"));
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
	    return new LocalNotSupportedScope();
	}
	

	@Override
	public boolean containsKey(Collection.Key key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public java.util.Collection values() {
		return null;
	}
	
    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Unsupported Context for Local Scope");
    }
    
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Unsupported Context for Local Scope");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Unsupported Context for Local Scope");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Unsupported Context for Local Scope");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }
    
    public int getType() {
        return SCOPE_LOCAL;
    }
    public String getTypeAsString() {
        return "local";
    }
	public int compareTo(String str) throws PageException {
        throw new ExpressionException("Unsupported Context for Local Scope");
	}
	public int compareTo(boolean b) throws PageException {
        throw new ExpressionException("Unsupported Context for Local Scope");
	}
	public int compareTo(double d) throws PageException {
        throw new ExpressionException("Unsupported Context for Local Scope");
	}
	public int compareTo(DateTime dt) throws PageException {
        throw new ExpressionException("Unsupported Context for Local Scope");
	}
	@Override
	public boolean isBind() {
		return bind;
	}

	@Override
	public void setBind(boolean bind) {
		this.bind=bind;
	}
}