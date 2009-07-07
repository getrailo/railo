package railo.runtime.type.scope;

import java.io.Serializable;

import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;

public class ClusterWrap extends ScopeSupport implements Cluster,Sizeable {

	private ClusterRemote core;
	private int offset;
	private ConfigServer configServer;

	public ClusterWrap(ConfigServer cs,ClusterRemote core) {
		this(cs,core,false);
	}
	
	private ClusterWrap(ConfigServer configServer,ClusterRemote core,boolean duplicate) {
		super(true, "cluster", Struct.TYPE_SYNC);
		this.configServer=configServer;
		if(duplicate) this.core=core.duplicate();
		else this.core=core;
		this.core.init(configServer,this);
	}
	
	public void init(ConfigServer configServer) {
		// for the custer wrap this method is not invoked, but it is part of the interface
	}

	/**
	 * @see railo.runtime.type.StructImpl#remove(java.lang.String)
	 */
	public Object remove(String key) throws PageException {
		return remove(KeyImpl.init(key));
	}

	
	
	/**
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return ((ClusterEntry)super.get(key)).getValue();
	}

	/**
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object res = super.get(key,defaultValue);
		if(res instanceof ClusterEntry) return  ((ClusterEntry)res).getValue();
		return res;
	}
	
	/**
	 * @see railo.runtime.type.StructImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		core.addEntry(new ClusterEntryImpl(key,null,offset));
		return ((ClusterEntry)super.remove (key)).getValue();
		
	}

	public Object removeEL(Key key) {
		core.addEntry(new ClusterEntryImpl(key,null,offset));
		ClusterEntry entry = (ClusterEntry) super.removeEL (key);
		if(entry!=null) return entry.getValue();
		return null;
	}

	/**
	 * @see railo.runtime.type.StructImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		if(core.checkValue(value)) {
			ClusterEntry entry;
			core.addEntry(entry=new ClusterEntryImpl(key,(Serializable)value,offset));
			super.setEL (key, entry);
		}
		return value;
	}

	public void setEntry(ClusterEntry newEntry) {
		ClusterEntry existingEntry=(ClusterEntry)super.get(newEntry.getKey(),null);
		// add
		if(existingEntry==null || existingEntry.getTime()<newEntry.getTime()) {
			if(newEntry.getValue()==null)removeEL (newEntry.getKey());
			else {
				core.addEntry(newEntry);
				super.setEL (newEntry.getKey(), newEntry);
			}
		}
	}
	
	/**
	 * @see railo.runtime.type.StructImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		ClusterEntry entry;
		core.addEntry(entry=new ClusterEntryImpl(key,(Serializable)value,offset));
		super.setEL (key, entry);
		return value;
	}
	
	/**
	 *
	 * @see railo.runtime.type.scope.ScopeSupport#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return super.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ScopeSupport#getType()
	 */
	public int getType() {
		return SCOPE_CLUSTER;
	} 

	/**
	 *
	 * @see railo.runtime.type.scope.ScopeSupport#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "cluster";
	}
	
	/**
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new ClusterWrap(configServer,core,true);
	}

	/**
	 * @see railo.runtime.type.scope.Cluster#broadcast()
	 */
	public void broadcast() {
		core.broadcastEntries();
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(getMap());
	}
}