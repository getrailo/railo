package railo.runtime.functions.file;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

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
import railo.runtime.type.util.KeyConstants;
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
			info.setEL(KeyConstants._mode, getMode());
			info.setEL(KeyConstants._name, res.getName());
			info.setEL(KeyConstants._path, res.getParent());
			info.setEL(KeyConstants._status, getStatus());
			info.setEL(KeyConstants._size, getSize()+" bytes");
			info.setEL(KeyConstants._lastmodified, getLastmodified());
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

	@Override
	public String toString() {
		return res.getAbsolutePath();
	}

	@Override
	public void clear() {
		throw new RuntimeException("can't clear struct, struct is readonly");
		
	}
	
	@Override
	public boolean containsKey(Key key) {
		return info().containsKey(key);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		throw new RuntimeException("can't duplicate File Object, Object depends on File Stream");
	}
	
	
	@Override
	public Object get(Key key) throws PageException {
		return info().get(key);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return info().get(key, defaultValue);
	}

	@Override
	public Key[] keys() {
		return info().keys();
	}

	public Object remove(Key key) throws PageException {
		throw new PageRuntimeException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
	}

	@Override
	public Object removeEL(Key key) {
		throw new PageRuntimeException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		throw new ExpressionException("can't set key ["+key.getString()+"] to struct, struct is readonly");
	}

	@Override
	public Object setEL(Key key, Object value) {
		throw new PageRuntimeException("can't set key ["+key.getString()+"] to struct, struct is readonly");
	}

	@Override
	public int size() {
		return info().size();
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return info().toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return info().keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return info().keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return info().entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return info().valueIterator();
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return info().castToBooleanValue();
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return info().castToBoolean(defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return info().castToDateTime();
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return info().castToDateTime(defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return info().castToDoubleValue();
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return info().castToDoubleValue(defaultValue); 
    }

	@Override
	public String castToString() throws PageException {
		return info().castToString();
	}
	@Override
	public String castToString(String defaultValue) {
		return info().castToString(defaultValue);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return info().compareTo(str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return info().compareTo(b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return info().compareTo(d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return info.compareTo(dt);
	}

	@Override
	public boolean containsValue(Object value) {
		return info().containsValue(value);
	}

	@Override
	public java.util.Collection values() {
		return info().values();
	}

	public abstract void skip(int len) throws PageException;

	public abstract void seek(long pos) throws PageException;
}
