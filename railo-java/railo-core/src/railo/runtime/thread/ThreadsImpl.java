package railo.runtime.thread;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.Thread.State;
import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.tag.Http;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.StructSupport;

public class ThreadsImpl extends StructSupport implements railo.runtime.type.scope.Threads {

	private static final Key KEY_ERROR = KeyImpl.intern("ERROR");
	private static final Key KEY_ELAPSEDTIME = KeyImpl.intern("ELAPSEDTIME");
	private static final Key KEY_OUTPUT = KeyImpl.intern("OUTPUT");
	private static final Key KEY_PRIORITY = KeyImpl.intern("PRIORITY");
	private static final Key KEY_STARTTIME = KeyImpl.intern("STARTTIME");
	private static final Key KEY_STATUS = KeyImpl.intern("STATUS");
	private static final Key KEY_STACKTRACE = KeyImpl.intern("STACKTRACE");
	
	private static final Key[] DEFAULT_KEYS=new Key[]{
		KEY_ELAPSEDTIME,
		KeyImpl.NAME_UC,
		KEY_OUTPUT,
		KEY_PRIORITY,
		KEY_STARTTIME,
		KEY_STATUS,
		KEY_STACKTRACE
	};
	
	private ChildThreadImpl ct;
	
	public ThreadsImpl(ChildThreadImpl ct) {
		this.ct=ct;
	}
	

	public ChildThread getChildThread() {
		return ct;
	}

	/**
	 * @see railo.runtime.type.StructImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}

	/////////////////////////////////////////////////////////////
	
	
	public int getType() {
		return -1;
	}

	/**
	 * @see railo.runtime.type.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "thread";
	}

	public void initialize(PageContext pc) {
		
	}

	public boolean isInitalized() {
		return true;
	}

	public void release() {
		
	}

	public void clear() {
		ct.content.clear();
	}


	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		StructImpl sct=new StructImpl();
		ThreadLocalDuplication.set(this, sct);
		try{
			Key[] keys = keys();
			Object value;
			for(int i=0;i<keys.length;i++) {
				value=get(keys[i],null);
				sct.setEL(keys[i],deepCopy?Duplicator.duplicate(value, deepCopy):value);
			}
		}
		finally {
			ThreadLocalDuplication.remove(this);
		}
		return sct;
	}
	

	private Object getMeta(Key key) {
		if(KEY_ELAPSEDTIME.equalsIgnoreCase(key)) return new Double(System.currentTimeMillis()-ct.getStartTime());
		if(KeyImpl.NAME_UC.equalsIgnoreCase(key)) return ct.getTagName();
		if(KEY_OUTPUT.equalsIgnoreCase(key)) return getOutput();
		if(KEY_PRIORITY.equalsIgnoreCase(key)) return ThreadUtil.toStringPriority(ct.getPriority());
		if(KEY_STARTTIME.equalsIgnoreCase(key)) return new DateTimeImpl(ct.getStartTime(),true);
		if(KEY_STATUS.equalsIgnoreCase(key)) return getState();
		if(KEY_ERROR.equalsIgnoreCase(key)) return ct.catchBlock;
		if(KEY_STACKTRACE.equalsIgnoreCase(key)) return getStackTrace();
		return null;
	}

	private String getStackTrace() {
		StringBuilder sb=new StringBuilder();
		try{
			StackTraceElement[] trace = ct.getStackTrace();
			if(trace!=null)for (int i=0; i < trace.length; i++) {
	            sb.append("\tat ");
	            sb.append(trace[i]);
	            sb.append("\n");
			}
		}
		catch(Throwable t){}
		return sb.toString();
	}


	private Object getOutput() {
		if(ct.output==null)return "";

		InputStream is = new ByteArrayInputStream(ct.output.toByteArray());
		return Http.getOutput(is, ct.contentType, ct.contentEncoding,true);
		
	}


	private Object getState() {
		/*
		  	

The current status of the thread; one of the following values:
    
		*/
		try {
			State state = ct.getState();
			if(State.NEW.equals(state)) return "NOT_STARTED";
			if(State.WAITING.equals(state)) return "WAITING";
			if(State.TERMINATED.equals(state)) {
				if(ct.terminated || ct.catchBlock!=null)return "TERMINATED";
				return "COMPLETED";
			}
			
			return "RUNNING";
		}
		// java 1.4 execution
		catch(Throwable t) {
			if(ct.terminated || ct.catchBlock!=null)return "TERMINATED";
			if(ct.completed)return "COMPLETED";
			if(!ct.isAlive())return "WAITING";
			return "RUNNING";
			
			
		}
	}


	/**
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object meta = getMeta(key);
		if(meta!=null) return meta;
		return ct.content.get(key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Object meta = getMeta(key);
		if(meta!=null) return meta;
		return ct.content.get(key);
	}


	/**
	 * @see railo.runtime.type.StructImpl#keys()
	 */
	public Key[] keys() {
		Key[] skeys = ct.content.keys();
		
		if(skeys.length==0 && ct.catchBlock==null) return DEFAULT_KEYS;
		
		Key[] rtn=new Key[skeys.length+(ct.catchBlock!=null?1:0)+DEFAULT_KEYS.length];
		int index=0;
		for(;index<DEFAULT_KEYS.length;index++) {
			rtn[index]=DEFAULT_KEYS[index];
		}
		if(ct.catchBlock!=null) {
			rtn[index]=KEY_ERROR;
			index++;
		}
		
		for(int i=0;i<skeys.length;i++) {
			rtn[index++]=skeys[i];
		}
		return rtn;
	}

	/**
	 * @see railo.runtime.type.StructImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		if(isReadonly())throw errorOutside();
		Object meta = getMeta(key);
		if(meta!=null) throw errorMeta(key);
		return ct.content.remove(key);
	}




	/**
	 * @see railo.runtime.type.StructImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		if(isReadonly())return null;
		return ct.content.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.StructImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		
		
		if(isReadonly())throw errorOutside();
		Object meta = getMeta(key);
		if(meta!=null) throw errorMeta(key);
		return ct.content.set(key, value);
	}

	/**
	 * @see railo.runtime.type.StructImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		if(isReadonly()) return null;
		Object meta = getMeta(key);
		if(meta!=null) return null;
		return ct.content.setEL(key, value);
	}


	/**
	 * @see railo.runtime.type.StructImpl#size()
	 */
	public int size() {
		return ct.content.size()+DEFAULT_KEYS.length+(ct.catchBlock==null?0:1);
	}

	/**
	 * @see railo.runtime.type.StructImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		Key[] keys = keys();
		DumpTable table = new DumpTablePro("struct","#9999ff","#ccccff","#000000");
		table.setTitle("Struct");
		maxlevel--;
		int maxkeys=dp.getMaxKeys();
		int index=0;
		for(int i=0;i<keys.length;i++) {
			Key key=keys[i];
			if(maxkeys<=index++)break;
			if(DumpUtil.keyValid(dp,maxlevel, key))
				table.appendRow(1,new SimpleDumpData(key.getString()),DumpUtil.toDumpData(get(key,null), pageContext,maxlevel,dp));
		}
		return table;
	}

	public Iterator keyIterator() {
		return new railo.runtime.type.it.KeyIterator(keys());
	}
	

	public String[] keysAsString() {
		Key[] keys = keys();
		String[] strKeys=new String[keys.length];
		for(int i=0;i<keys.length;i++) {
			strKeys[i]=keys[i].getString();
		}
		return null;
	}


	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return ct.content.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return ct.content.castToBoolean(defaultValue);
    }


	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return ct.content.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return ct.content.castToDateTime(defaultValue);
    }


	/**
	 *
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return ct.content.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return ct.content.castToDoubleValue(defaultValue);
    }


	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return ct.content.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return ct.content.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return ct.content.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return ct.content.compareTo(b);
	}


	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return ct.content.compareTo(d);
	}


	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return ct.content.compareTo(dt);
	}

	private boolean isReadonly() {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null) return true;
		return pc.getThread()!=ct;
	}

	private ApplicationException errorOutside() {
		return new ApplicationException("the thread scope cannot be modified from outside the owner thread");
	}

	private ApplicationException errorMeta(Key key) {
		return new ApplicationException("the metadata "+key.getString()+" of the thread scope are readonly");
	}

}
