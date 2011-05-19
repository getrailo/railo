package railo.runtime.functions.file;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.StructSupport;

public abstract class FileStreamWrapper extends StructSupport implements Struct {

	public static final String STATE_OPEN = "open";
	public static final String STATE_CLOSE = "close";
	
	protected Resource res;
	private String status=STATE_OPEN;
	private Struct info;
	private long lastModifed;
	private long length;
	
	
	public FileStreamWrapper(Resource res) {
		this.res=res;
		this.lastModifed=System.currentTimeMillis();
		this.length=res.length();
	}

	public final String getFilename() {
		return res.getName();
	}

	public final String getLabel(){
		return StringUtil.ucFirst(res.getResourceProvider().getScheme())+": "+getFilename();
	}
	
	
	public final String getFilepath() {
		return res.getAbsolutePath();
	}
	

	public final String getStatus() {
		return status;
	}

	public final void setStatus(String status) {
		this.status=status;
	}
	
	public final Date getLastmodified() {
		return new DateTimeImpl(lastModifed,false);
	}
	
	public Object getMetadata() {
		return info();
	}
	
	public Struct info() {
		if(info==null) {
			info=new StructImpl();
			info.setEL("mode", getMode());
			info.setEL("name", res.getName());
			info.setEL("path", res.getParent());
			info.setEL("status", getStatus());
			info.setEL("size", getSize()+" bytes");
			info.setEL("lastmodified", getLastmodified());
		}
		
		return info;
	}
	
	public boolean isEndOfFile() {
		return false;
	}
	
	public long getSize() {
		return length;
	}
	
	
	public void write(Object obj) throws IOException {
		throw notSupported("write");
	}

	public String readLine() throws IOException {
		throw notSupported("readLine");
	}
	
	public Object read(int len) throws IOException{
		throw notSupported("read");
	}
	
	public abstract String getMode();
	public abstract void close() throws IOException;
	
	private IOException notSupported(String method) {
		return new IOException(method+" can't be called when the file is opened in ["+getMode()+"] mode");
	}

	public Resource getResource() {
		return res;
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return res.getAbsolutePath();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		throw new RuntimeException("can't clear struct, struct is readonly");
		
	}
	
	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return info().containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		throw new RuntimeException("can't duplicate File Object, Object depends on File Stream");
	}
	
	
	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return info().get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return info().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return info.keys();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return info().keysAsString();
	}

	public Object remove(Key key) throws PageException {
		throw new PageRuntimeException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		throw new PageRuntimeException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		throw new ExpressionException("can't set key ["+key.getString()+"] to struct, struct is readonly");
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		throw new PageRuntimeException("can't set key ["+key.getString()+"] to struct, struct is readonly");
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return info().size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return info().toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return info().keyIterator();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return info().castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return info().castToBoolean(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return info().castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return info().castToDateTime(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return info().castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return info().castToDoubleValue(defaultValue); 
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return info().castToString();
	}
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return info().castToString(defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return info().compareTo(str);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return info().compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return info().compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return info.compareTo(dt);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return info().containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return info().values();
	}

	public abstract void skip(int len) throws PageException;

	public abstract void seek(long pos) throws PageException;
}
