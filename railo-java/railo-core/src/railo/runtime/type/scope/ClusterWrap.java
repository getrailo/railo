package railo.runtime.type.scope;

import java.io.Serializable;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;

public final class ClusterWrap extends ScopeSupport implements Cluster,Sizeable {

	private static final long serialVersionUID = -4952656252539755770L;
	
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
	
	@Override
	public void init(ConfigServer configServer) {
		// for the custer wrap this method is not invoked, but it is part of the interface
	}

	@Override
	public Object get(Key key) throws PageException {
		return ((ClusterEntry)super.get(key)).getValue();
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		Object res = super.get(key,defaultValue);
		if(res instanceof ClusterEntry) return  ((ClusterEntry)res).getValue();
		return res;
	}
	
	@Override
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

	@Override
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
	
	@Override
	public Object set(Key key, Object value) throws PageException {
		if(!core.checkValue(value))
			throw new ExpressionException("object from type ["+Caster.toTypeName(value)+"] are not allowed in cluster scope" );
		ClusterEntry entry;
		core.addEntry(entry=new ClusterEntryImpl(key,(Serializable)value,offset));
		super.setEL (key, entry);
		return value;
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return super.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public int getType() {
		return SCOPE_CLUSTER;
	} 

	@Override
	public String getTypeAsString() {
		return "cluster";
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
		return new ClusterWrap(configServer,core,true);
	}

	@Override
	public void broadcast() {
		core.broadcastEntries();
	}

	@Override
	public long sizeOf() {
		return 0;
	}
}