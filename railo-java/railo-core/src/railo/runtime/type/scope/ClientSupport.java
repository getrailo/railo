package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

public abstract class ClientSupport extends StructSupport implements Client,Sizeable {
	

	private static Set FIX_KEYS=new HashSet();
	static {
		FIX_KEYS.add("cfid");
		FIX_KEYS.add("cftoken");
		FIX_KEYS.add("urltoken");
		FIX_KEYS.add("lastvisit");
		FIX_KEYS.add("hitcount");
		FIX_KEYS.add("timecreated");
	}
	public static Collection.Key CFID=KeyImpl.getInstance("cfid");
	public static Collection.Key CFTOKEN=KeyImpl.getInstance("cftoken");
	public static Collection.Key URLTOKEN=KeyImpl.getInstance("urltoken");
	public static Collection.Key LASTVISIT=KeyImpl.getInstance("lastvisit");
	public static Collection.Key HITCOUNT=KeyImpl.getInstance("hitcount");
	public static Collection.Key TIMECREATED=KeyImpl.getInstance("timecreated");
	

	protected static Set ignoreSet=new HashSet();
	static {
		ignoreSet.add("cfid");
		ignoreSet.add("cftoken");
		ignoreSet.add("urltoken");
	}
	
	
	protected boolean isinit;
	protected Struct sct;
	protected long lastvisit;
	protected DateTime _lastvisit;
	protected int hitcount=0;
	protected DateTime timecreated;
	private boolean hasChanges=false;
	
	/**
	 * Constructor of the class
	 * @param sct
	 * @param timecreated
	 * @param _lastvisit
	 * @param lastvisit
	 * @param hitcount
	 */
	public ClientSupport(Struct sct, DateTime timecreated, DateTime _lastvisit, long lastvisit, int hitcount) {
		this.sct=sct;
		this.timecreated=timecreated;
		if(_lastvisit==null)	this._lastvisit=timecreated;
		else 					this._lastvisit=_lastvisit;
		
		if(lastvisit==-1) 		this.lastvisit=this._lastvisit.getTime();
		else 					this.lastvisit=lastvisit;
		
		this.hitcount=hitcount;
	}
	
	/**
	 * Constructor of the class
	 * @param other
	 * @param deepCopy
	 */
	public ClientSupport(ClientSupport other, boolean deepCopy) {
		this.sct=(Struct)other.sct.duplicate(deepCopy);
		this.timecreated=other.timecreated;
		this._lastvisit=other._lastvisit;
		this.hitcount=other.hitcount;
		this.isinit=other.isinit;
		this.lastvisit=other.lastvisit;
		this.timecreated=other.timecreated;
	}

	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		isinit=true;
		hasChanges=false;
		//lastvisit=System.currentTimeMillis();
		if(sct==null) sct=new StructImpl();
		sct.setEL(CFID, pc.getCFID());
		sct.setEL(CFTOKEN, pc.getCFToken());
		sct.setEL(URLTOKEN, pc.getURLToken());
		sct.setEL(LASTVISIT, _lastvisit);
		_lastvisit=new DateTimeImpl(pc.getConfig());
		lastvisit=System.currentTimeMillis();
		
		sct.setEL(HITCOUNT, new Double(hitcount++));
		sct.setEL(TIMECREATED, timecreated);
	}

	/**
	 * @see railo.runtime.type.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return isinit;
	}
	

	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
		isinit=false;
		sct.setEL(HITCOUNT, new Double(hitcount));
		sct.setEL(TIMECREATED, timecreated);
		sct.setEL(LASTVISIT, _lastvisit);
	}
	
	/**
	 * @return returns if the scope is empty or not, this method ignore the "constant" entries of the scope (cfid,cftoken,urltoken)
	 */
	public boolean hasContent() {
		if(sct.size()==6 && sct.containsKey(URLTOKEN) && sct.containsKey(CFTOKEN) && sct.containsKey(CFID)) {
			return false;
		}
		return true;
	}
	

	/**
	 *
	 * @see railo.runtime.type.Scope#getType()
	 */
	public int getType() {
		return SCOPE_CLIENT;
	}

	/**
	 *
	 * @see railo.runtime.type.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "client";
	}

	

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
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

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return sct.keyIterator();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return sct.keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public railo.runtime.type.Collection.Key[] keys() {
		return sct.keys();
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
	 * @see railo.runtime.type.scope.Client#lastVisit()
	 */
	public long lastVisit() {
		return lastvisit;
	}

	public String[] pureKeys() {
		List keys=new ArrayList();
		Iterator it = keyIterator();
		String key;
		while(it.hasNext()){
			key=Caster.toString(it.next(),"").toLowerCase();
			if(!FIX_KEYS.contains(key))keys.add(key);
		}
		return (String[]) keys.toArray(new String[keys.size()]);
	}
	
	/**
	 * this is a supporting method for implemetation of method "getDumpData" of the child cﾚasses
	 * @param pageContext
	 * @param maxlevel
	 * @return
	 */
	protected DumpTable toDumpTable(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "Client Scope", pageContext, maxlevel, dp);
		/*
		maxlevel--;
		Iterator it=keyIterator();
		
		DumpTable table = new DumpTable("#9999ff","#ccccff","#000000");
		
		int maxkeys=dp.getMaxKeys();
		int index=0;
		while(it.hasNext()) {
			String key=it.next().toString();
			
			if(DumpUtil.keyValid(dp,maxlevel, key)){
				if(maxkeys<=index++)break;
				table.appendRow(1,new SimpleDumpData(key),DumpUtil.toDumpData(get(key,null), pageContext,maxlevel,dp));
			}
		}
		return table;*/
	}
	
	public void store(){
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
}
