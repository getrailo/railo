package railo.runtime.type.scope;

import java.util.Iterator;
import java.util.Map;

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
	
	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return 0;
	}
	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return null;
	}
	
	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return null;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return null;
	}
	
	/**
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 */
	public Object remove(String key) throws ExpressionException {
	    throw new ExpressionException(NOT_SUPPORTED);
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return remove(key.getString());
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
	}
	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(Collection.Key key) throws ExpressionException {
		throw new ExpressionException(NOT_SUPPORTED);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return defaultValue;
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws ExpressionException {
		throw new ExpressionException(NOT_SUPPORTED);
	}
	
    /**
     * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public Object setEL(Collection.Key key, Object value) {
		return null;
	}
	
	/**
	 * @see railo.runtime.type.scope.Cluster#setEntry(railo.runtime.type.scope.ClusterEntry)
	 */
	public void setEntry(ClusterEntry entry) {
	}
	
	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return null;
	}

	/**
	 * @see railo.runtime.type.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return false;
	}
	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
	}
	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
	}
	public void release(PageContext pc) {
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
        throw new PageRuntimeException(new ExpressionException(NOT_SUPPORTED));
		//return new SimpleDumpData(NOT_SUPPORTED);
	}
	
	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
	    return new ClusterNotSupported();
	}
	

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return false;
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return false;
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return null;
	}
	
    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException(NOT_SUPPORTED);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
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

	/**
	 * @see railo.runtime.type.scope.Cluster#broadcast()
	 */
	public void broadcast() {
		//print.out("Cluster#broadcast()");
	}

	/**
	 * @see railo.runtime.type.scope.Cluster#init(railo.runtime.config.ConfigServer)
	 */
	public void init(ConfigServer configServer) {
	}

}