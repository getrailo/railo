package railo.runtime.type.scope;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * client scope that store it's data in a datasource
 */
public final class ClientCache extends ClientSupport {

	private static final long serialVersionUID = 6234854552927320080L;

	//private static ScriptConverter serializer=new ScriptConverter();
	private static boolean structOk;
	
	private String cacheName;
	private String appName;
	private String cfid;
	//private PageContext pc;
	
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 * @param b 
	 */
	private ClientCache(PageContext pc,String cacheName, String appName,Struct sct) { 
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1, 
				Caster.toIntValue(sct.get(HITCOUNT,"1"),1));

		//this.isNew=isNew;
		this.appName=appName;
		this.cacheName=cacheName;
		this.cfid=pc.getCFID();
		//this.pc=pc;
		//this.manager = (DatasourceManagerImpl) pc.getDataSourceManager(); 
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientCache(ClientCache other,boolean deepCopy) {
		super(other,deepCopy);
		
		this.appName=other.appName;
		this.cacheName=other.cacheName;
		this.cfid=other.cfid;
		//this.pc=other.pc;
		//this.manager=other.manager;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param cacheName 
	 * @param appName
	 * @param pc
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static Client getInstance(String cacheName, String appName, PageContext pc) throws PageException {
			Struct _sct = _loadData(pc, cacheName, appName, false);
			structOk=true;
			if(_sct==null) _sct=new StructImpl();
			
		return new ClientCache(pc,cacheName,appName,_sct);
	}
	

	public static Client getInstanceEL(String cacheName, String appName, PageContext pc) {
		try {
			return getInstance(cacheName, appName, pc);
		}
		catch (PageException e) {}
		return new ClientCache(pc,cacheName,appName,new StructImpl());
	}
	
	
	private static Struct _loadData(PageContext pc, String cacheName, String appName, boolean mxStyle) throws PageException	{
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			String key=pc.getCFID().concat(":").concat(appName);
			return (Struct) cache.getValue(key,null);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupport#release()
	 */
	public void release() {
		release(ThreadLocalPageContext.get());
	}
	
	/**
	 * @see railo.runtime.type.RequestScope#release(railo.runtime.PageContext)
	 */
	public void release(PageContext pc) {
		structOk=false;
		super.release();
		if(!super.hasContent()) return;
		
		try {
			Cache cache = Util.getCache(pc,cacheName,ConfigImpl.CACHE_DEFAULT_OBJECT);
			String key=pc.getCFID().concat(":").concat(appName);
			cache.put(key, sct, null, null);
		} 
		catch (IOException e) {}
	}

	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = super.toDumpTable(pageContext, maxlevel,dp);
		table.setTitle("Scope Client (Datasource)");
		return table;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientCache(this,deepCopy);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ClientSupport#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		//this.pc=pc;
		//print.out(isNew);
		try {
			if(!structOk)sct=_loadData(pc, cacheName, appName, false);
			
		} catch (PageException e) {
			//
		}
		super.initialize(pc);
	}
}
