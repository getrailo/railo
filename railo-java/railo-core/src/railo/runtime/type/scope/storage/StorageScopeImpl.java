package railo.runtime.type.scope.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.RandomUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

public abstract class StorageScopeImpl extends StructSupport implements StorageScope,Sizeable {

	public static Collection.Key CFID=KeyConstants._cfid;
	public static Collection.Key CFTOKEN=KeyConstants._cftoken;
	public static Collection.Key URLTOKEN=KeyConstants._urltoken;
	public static Collection.Key LASTVISIT=KeyConstants._lastvisit;
	public static Collection.Key HITCOUNT=KeyConstants._hitcount;
	public static Collection.Key TIMECREATED=KeyConstants._timecreated;
	public static Collection.Key SESSION_ID=KeyConstants._sessionid;


	private static int _id=0;
	private int id=0;

	private static final long serialVersionUID = 7874930250042576053L;
	private static Set<Collection.Key> FIX_KEYS=new HashSet<Collection.Key>();
	static {
		FIX_KEYS.add(CFID);
		FIX_KEYS.add(CFTOKEN);
		FIX_KEYS.add(URLTOKEN);
		FIX_KEYS.add(LASTVISIT);
		FIX_KEYS.add(HITCOUNT);
		FIX_KEYS.add(TIMECREATED);
	}
	

	protected static Set<Collection.Key> ignoreSet=new HashSet<Collection.Key>();
	static {
		ignoreSet.add(CFID);
		ignoreSet.add(CFTOKEN);
		ignoreSet.add(URLTOKEN);
	}
	
	
	protected boolean isinit=true;
	protected Struct sct;
	protected long lastvisit;
	protected DateTime _lastvisit;
	protected int hitcount=0;
	protected DateTime timecreated;
	private boolean hasChanges=false;
	private String strType;
	private int type;
	private long timeSpan=-1;
	private String storage;
	private Map<String, String> tokens; 
	
	
	/**
	 * Constructor of the class
	 * @param sct
	 * @param timecreated
	 * @param _lastvisit
	 * @param lastvisit
	 * @param hitcount
	 */
	public StorageScopeImpl(Struct sct, DateTime timecreated, DateTime _lastvisit, long lastvisit, int hitcount,String strType,int type) {
		this.sct=sct;
		this.timecreated=timecreated;
		if(_lastvisit==null)	this._lastvisit=timecreated;
		else 					this._lastvisit=_lastvisit;
		
		if(lastvisit==-1) 		this.lastvisit=this._lastvisit.getTime();
		else 					this.lastvisit=lastvisit;

		this.hitcount=hitcount;
		this.strType=strType;
		this.type=type;
        id=++_id;
	}
	
	/**
	 * Constructor of the class
	 * @param other
	 * @param deepCopy
	 */
	public StorageScopeImpl(StorageScopeImpl other, boolean deepCopy) {
		this.sct=(Struct)Duplicator.duplicate(other.sct,deepCopy);
		this.timecreated=other.timecreated;
		this._lastvisit=other._lastvisit;
		this.hitcount=other.hitcount;
		this.isinit=other.isinit;
		this.lastvisit=other.lastvisit;
		this.strType=other.strType;
		this.type=other.type;
		this.timeSpan=other.timeSpan;
        id=++_id;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void touchBeforeRequest(PageContext pc) {
		
		hasChanges=false;
		setTimeSpan(pc);
		
		
		//lastvisit=System.currentTimeMillis();
		if(sct==null) sct=new StructImpl();
		sct.setEL(KeyConstants._cfid, pc.getCFID());
		sct.setEL(KeyConstants._cftoken, pc.getCFToken());
		sct.setEL(URLTOKEN, pc.getURLToken());
		sct.setEL(LASTVISIT, _lastvisit);
		_lastvisit=new DateTimeImpl(pc.getConfig());
		lastvisit=System.currentTimeMillis();
		
		if(type==SCOPE_CLIENT){
			sct.setEL(HITCOUNT, new Double(hitcount++));
		}
		else {
			sct.setEL(SESSION_ID, pc.getApplicationContext().getName()+"_"+pc.getCFID()+"_"+pc.getCFToken());
		}
		sct.setEL(TIMECREATED, timecreated);
	}

	public void resetEnv(PageContext pc){
		_lastvisit=new DateTimeImpl(pc.getConfig());
		timecreated=new DateTimeImpl(pc.getConfig());
		touchBeforeRequest(pc);
		
	}
	
	void setTimeSpan(PageContext pc) {
		ApplicationContext ac=pc.getApplicationContext();
		this.timeSpan=getType()==SCOPE_SESSION?
				ac.getSessionTimeout().getMillis():
				ac.getClientTimeout().getMillis();
	}
	
	@Override
	public void setMaxInactiveInterval(int interval) {
		this.timeSpan=interval*1000L;
	}

	@Override
	public int getMaxInactiveInterval() {
		return (int)(this.timeSpan/1000L);
	}
	
	@Override
	public final boolean isInitalized() {
		return isinit;
	}
	
	@Override
	public final void initialize(PageContext pc) {
		// StorageScopes need only request initialisation no global init, they are not reused;
	}
	
	@Override
	public void touchAfterRequest(PageContext pc) {
		
		sct.setEL(LASTVISIT, _lastvisit);
		sct.setEL(TIMECREATED, timecreated);
		
		if(type==SCOPE_CLIENT){
			sct.setEL(HITCOUNT, new Double(hitcount));
		}
	}
	
	@Override
	public final void release() {
		clear();
		isinit=false;
	}
	
	@Override
	public final void release(PageContext pc) {
		clear();
		isinit=false;
	}
	
	
	/**
	 * @return returns if the scope is empty or not, this method ignore the "constant" entries of the scope (cfid,cftoken,urltoken)
	 */
	public boolean hasContent() {
		if(sct.size()==(type==SCOPE_CLIENT?6:5) && sct.containsKey(URLTOKEN) && sct.containsKey(KeyConstants._cftoken) && sct.containsKey(KeyConstants._cfid)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void  clear() {
		sct.clear();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return sct.containsKey(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return sct.get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return sct.get(key, defaultValue);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return sct.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return sct.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return sct.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return sct.valueIterator();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public railo.runtime.type.Collection.Key[] keys() {
		return CollectionUtil.keys(this);
	}


	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		hasChanges=true;
		return sct.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		hasChanges=true;
		return sct.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		hasChanges=true;
		return sct.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		hasChanges=true;
		return sct.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return sct.size();
	}


	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return sct.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return sct.castToBoolean(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return sct.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return sct.castToDateTime(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return sct.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return sct.castToDoubleValue(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return sct.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return sct.castToString(defaultValue);
	}

	/**
	 * @throws PageException 
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return sct.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return sct.compareTo(dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return sct.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return sct.compareTo(str);
	}

	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#lastVisit()
	 */
	public long lastVisit() {
		return lastvisit;
	}

	public Collection.Key[] pureKeys() {
		List<Collection.Key> keys=new ArrayList<Collection.Key>();
		Iterator<Key> it = keyIterator();
		Collection.Key key;
		while(it.hasNext()){
			key=it.next();
			if(!FIX_KEYS.contains(key))keys.add(key);
		}
		return keys.toArray(new Collection.Key[keys.size()]);
	}
	
	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#store(railo.runtime.config.Config)
	 */
	public void store(Config config){
		//do nothing
	}

	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#unstore(railo.runtime.config.Config)
	 */
	public void unstore(Config config){
		//do nothing
	}

	/**
	 * @return the hasChanges
	 */
	public boolean hasChanges() {
		return hasChanges;
	}
	

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return sct.containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return sct.values();
	}
	
	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(sct);
	}
	
	public final int getType() {
		return type;
	}

	public final String getTypeAsString() {
		return strType;
	}
	
	

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public final DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, StringUtil.ucFirst(getTypeAsString())+" Scope ("+getStorageType()+")", pageContext, maxlevel, dp);
	}
	

	public long getLastAccess() { return lastvisit;}
	public long getTimeSpan() { return timeSpan;}
	
	
	public void touch() {
		lastvisit=System.currentTimeMillis();
		_lastvisit=new DateTimeImpl(ThreadLocalPageContext.getConfig());
	}
	
	public boolean isExpired() {
	    return (getLastAccess()+getTimeSpan())<System.currentTimeMillis();
    }


	
	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#setStorage(java.lang.String)
	 */
	public void setStorage(String storage) {
		this.storage=storage;
	}

	/**
	 * @see railo.runtime.type.scope.storage.StorageScope#getStorage()
	 */
	public String getStorage() {
		return storage;
	}
	
	public static String encode(String input) {
		int len=input.length();
		StringBuilder sb=new StringBuilder();
		char c;
		for(int i=0;i<len;i++){
			c=input.charAt(i);
			if((c>='0' && c<='9') || (c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_' || c=='-')
				sb.append(c);
			else {
				sb.append('$');
				sb.append(Integer.toString((c),Character.MAX_RADIX));
				sb.append('$');
			}
		}
		
		return sb.toString();
	}

	public static String decode(String input) {
		int len=input.length();
		StringBuilder sb=new StringBuilder();
		char c;
		int ni;
		for(int i=0;i<len;i++){
			c=input.charAt(i);
			if(c=='$') {
				ni=input.indexOf('$',i+1);
				sb.append((char)Integer.parseInt(input.substring(i+1,ni),Character.MAX_RADIX));
				i=ni;
			}
				
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
    public int _getId() {
        return id;
    }
    

	
	public long getCreated() {
		return timecreated==null?0:timecreated.getTime();
	}
	
	@Override
	public synchronized String generateToken(String key, boolean forceNew) {
        if(tokens==null) 
        	tokens = new HashMap<String,String>();
        
        // get existing
        String token;
        if(!forceNew) {
        	token = tokens.get(key);
        	if(token!=null) return token;
        }
        
        // create new one
        token = RandomUtil.createRandomStringLC(40);
        tokens.put(key, token);
        return token;
    }
	
	@Override
	public synchronized boolean verifyToken(String token, String key) {
		if(tokens==null) return false;
        String _token = tokens.get(key);
        return _token!=null && _token.equalsIgnoreCase(token);
    }
}
