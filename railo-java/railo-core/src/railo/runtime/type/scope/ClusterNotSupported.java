package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

/**d
 * 
 */
public final class ClusterNotSupported extends StructSupport implements Cluster {
	
	private static final String NOT_SUPPORTED="to enable the cluster scope please install a cluster scope impementation with the help of the extenson manager";
	
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
		return null;
	}
	
	@Override
	public Object remove(Key key) throws PageException {
	    throw new ExpressionException(NOT_SUPPORTED);
	}
	
	@Override
	public void clear() {
	}
	@Override
	public Object get(Collection.Key key) throws ExpressionException {
		throw new ExpressionException(NOT_SUPPORTED);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return defaultValue;
	}

	@Override
	public Object set(Key key, Object value) throws ExpressionException {
		throw new ExpressionException(NOT_SUPPORTED);
	}
	
    @Override
    public Object setEL(Collection.Key key, Object value) {
		return null;
	}
	
	@Override
	public void setEntry(ClusterEntry entry) {
	}
	
	@Override
	public Iterator<Collection.Key> keyIterator() {
		return null;
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return null;
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return null;
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return null;
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
        throw new PageRuntimeException(new ExpressionException(NOT_SUPPORTED));
		//return new SimpleDumpData(NOT_SUPPORTED);
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
	    return new ClusterNotSupported();
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
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }
    
    public int getType() {
        return SCOPE_CLUSTER;
    }
    public String getTypeAsString() {
        return "Cluster";
    }
	public int compareTo(String str) throws PageException {
        throw new ExpressionException(NOT_SUPPORTED);
	}
	public int compareTo(boolean b) throws PageException {
        throw new ExpressionException(NOT_SUPPORTED);
	}
	public int compareTo(double d) throws PageException {
        throw new ExpressionException(NOT_SUPPORTED);
	}
	public int compareTo(DateTime dt) throws PageException {
        throw new ExpressionException(NOT_SUPPORTED);
	}

	@Override
	public void broadcast() {
		//print.out("Cluster#broadcast()");
	}

	@Override
	public void init(ConfigServer configServer) {
	}

}