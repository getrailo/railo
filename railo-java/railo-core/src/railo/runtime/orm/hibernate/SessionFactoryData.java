package railo.runtime.orm.hibernate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;

import railo.commons.io.log.Log;
import railo.loader.util.Util;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.orm.hibernate.naming.CFCNamingStrategy;
import railo.runtime.orm.hibernate.naming.DefaultNamingStrategy;
import railo.runtime.orm.hibernate.naming.SmartNamingStrategy;
import railo.runtime.orm.naming.NamingStrategy;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

public class SessionFactoryData {

	public List<Component> tmpList;
	
	private final Map<Key,DataSource> sources=new HashMap<Key, DataSource>();
	private final Map<Key,Map<String, CFCInfo>> cfcs=new HashMap<Key, Map<String,CFCInfo>>();
	private final Map<Key,Configuration> configurations=new HashMap<Key,Configuration>();
	private final Map<Key,SessionFactory> factories=new HashMap<Key,SessionFactory>();
	private final Map<Key,QueryPlanCache> queryPlanCaches=new HashMap<Key,QueryPlanCache>();
	
	private final ORMConfiguration ormConf;
	private NamingStrategy namingStrategy;
	private final HibernateORMEngine engine;
	private Struct tableInfo=CommonUtil.createStruct();
	private String cfcNamingStrategy;

	
	
	public SessionFactoryData(HibernateORMEngine engine,ORMConfiguration ormConf) {
		this.engine=engine;
		this.ormConf=ormConf;
	}
	
	public ORMConfiguration getORMConfiguration(){
		return ormConf;
	}
	public HibernateORMEngine getEngine(){
		return engine;
	}
	
	public QueryPlanCache getQueryPlanCache(Key datasSourceName) {
		QueryPlanCache qpc = queryPlanCaches.get(datasSourceName);
		if(qpc==null){
			queryPlanCaches.put(datasSourceName, qpc=new QueryPlanCache((SessionFactoryImplementor) getFactory(datasSourceName)));
		}
		return qpc;
	}

	
	public NamingStrategy getNamingStrategy() throws PageException {
		if(namingStrategy==null) {
			String strNamingStrategy=ormConf.namingStrategy();
			if(Util.isEmpty(strNamingStrategy,true)) {
				namingStrategy=DefaultNamingStrategy.INSTANCE;
			}
			else {
				strNamingStrategy=strNamingStrategy.trim();
				if("default".equalsIgnoreCase(strNamingStrategy)) 
					namingStrategy=DefaultNamingStrategy.INSTANCE;
				else if("smart".equalsIgnoreCase(strNamingStrategy)) 
					namingStrategy=SmartNamingStrategy.INSTANCE;
				else {
					CFCNamingStrategy cfcNS = new CFCNamingStrategy(cfcNamingStrategy==null?strNamingStrategy:cfcNamingStrategy);
					cfcNamingStrategy=cfcNS.getComponent().getPageSource().getComponentName();
					namingStrategy=cfcNS;
					
				}
			}
		}
		if(namingStrategy==null) return DefaultNamingStrategy.INSTANCE;
		return namingStrategy;
	}
	
	
	public CFCInfo checkExistent(PageContext pc,Component cfc) throws PageException {
		CFCInfo info = getCFC(HibernateCaster.getEntityName(cfc), null);
		if(info!=null) return info;
		
		throw ExceptionUtil.createException(this,null,"there is no mapping definition for component ["+cfc.getAbsName()+"]","");
	}
	
	public List<String> getEntityNames() {
		Iterator<Map<String, CFCInfo>> it = cfcs.values().iterator();
		List<String> names=new ArrayList<String>();
		Iterator<CFCInfo> _it;
		while(it.hasNext()){
			_it = it.next().values().iterator();
			while(_it.hasNext()){
				names.add(HibernateCaster.getEntityName(_it.next().getCFC()));
			}
		}
		return names;
	}

	public Component getEntityByEntityName(String entityName,boolean unique) throws PageException {
		Component cfc;
		
		// first check cfcs for this entity
		CFCInfo info = getCFC(entityName,null);
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)cfc.duplicate(false):cfc;
		}
		
		// if parsing is in progress, the cfc can be found here
		if(tmpList!=null){
			Iterator<Component> it = tmpList.iterator();
			while(it.hasNext()){
				cfc=it.next();
				if(HibernateCaster.getEntityName(cfc).equalsIgnoreCase(entityName))
					return unique?(Component)cfc.duplicate(false):cfc;
			}
		}
		throw ExceptionUtil.createException((ORMSession)null,null,"entity ["+entityName+"] does not exist","");
	}
	


	public Component getEntityByCFCName(String cfcName,boolean unique) throws PageException {
		String name=cfcName;
		int pointIndex=cfcName.lastIndexOf('.');
		if(pointIndex!=-1) {
			name=cfcName.substring(pointIndex+1);
		}
		else 
			cfcName=null;
		
		
		
		Component cfc;
		List<String> names=new ArrayList<String>();
		
		List<Component> list = tmpList;
		if(list!=null){
			int index=0;
			Iterator<Component> it2 = list.iterator();
			while(it2.hasNext()){
				cfc=it2.next();
				names.add(cfc.getName());
				if(HibernateUtil.isEntity(ormConf,cfc,cfcName,name)) //if(cfc.equalTo(name))
					return unique?(Component)cfc.duplicate(false):cfc;
			}
		}
		else {
			// search cfcs
			Iterator<Map<String, CFCInfo>> it = cfcs.values().iterator();
			Map<String, CFCInfo> _cfcs;
			while(it.hasNext()){
				_cfcs = it.next();
				Iterator<CFCInfo> _it = _cfcs.values().iterator();
				while(_it.hasNext()){
					cfc=_it.next().getCFC();
					names.add(cfc.getName());
					if(HibernateUtil.isEntity(ormConf,cfc,cfcName,name)) //if(cfc.instanceOf(name))
						return unique?(Component)cfc.duplicate(false):cfc;
				}
			}
		}
		
		CFCInfo info = getCFC(name,null);
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)cfc.duplicate(false):cfc;
		}
		
		throw ExceptionUtil.createException((ORMSession)null,null,"entity ["+name+"] "+(Util.isEmpty(cfcName)?"":"with cfc name ["+cfcName+"] ")+"does not exist, existing  entities are ["+ListUtil.listToList(names, ", ")+"]","");
		
	}

	// Datasource specific
	public Configuration getConfiguration(DataSource ds){
		return configurations.get(KeyImpl.init(ds.getName()));
	}
	public Configuration getConfiguration(Key key){
		return configurations.get(key);
	}

	public void setConfiguration(Log log,String mappings, DatasourceConnection dc) throws PageException, SQLException, IOException {
		configurations.put(KeyImpl.init(dc.getDatasource().getName()),HibernateSessionFactory.createConfiguration(log,mappings,dc,this));
	}


	public void buildSessionFactory(Key datasSourceName) {
		//Key key=KeyImpl.init(ds.getName());
		Configuration conf = getConfiguration(datasSourceName);
		if(conf==null) throw new RuntimeException("cannot build factory because there is no configuration"); // this should never happen
		factories.put(datasSourceName, conf.buildSessionFactory());
	}

	public SessionFactory getFactory(Key datasSourceName){
		SessionFactory factory = factories.get(datasSourceName);
		if(factory==null && getConfiguration(datasSourceName)!=null) buildSessionFactory(datasSourceName);// this should never be happen
		return factory;
	}
	

	public void reset() {
		configurations.clear();
		Iterator<SessionFactory> it = factories.values().iterator();
		while(it.hasNext()){
			it.next().close();
		}
		factories.clear();
		//namingStrategy=null; because the ormconf not change, this has not to change as well
		tableInfo=CommonUtil.createStruct();
	}
	

	public Struct getTableInfo(DatasourceConnection dc, String tableName) throws PageException {
		Collection.Key keyTableName=CommonUtil.createKey(tableName);
		Struct columnsInfo = (Struct) tableInfo.get(keyTableName,null);
		if(columnsInfo!=null) return columnsInfo;
		
		columnsInfo = HibernateUtil.checkTable(dc,tableName,this);
    	tableInfo.setEL(keyTableName,columnsInfo);
    	return columnsInfo;
	}

	
	// CFC methods
	public void addCFC(String entityName, CFCInfo info) {
		DataSource ds = info.getDataSource();
		Key dsn=KeyImpl.init(ds.getName());
				
		Map<String, CFCInfo> map = cfcs.get(dsn);
		if(map==null) cfcs.put(dsn, map=new HashMap<String, CFCInfo>());
		map.put(HibernateUtil.id(entityName), info);
		sources.put(dsn, ds);
	}
	

	CFCInfo getCFC(String entityName, CFCInfo defaultValue) {
		Iterator<Map<String, CFCInfo>> it = cfcs.values().iterator();
		while(it.hasNext()){
			CFCInfo info = it.next().get(HibernateUtil.id(entityName));
			if(info!=null) return info;
		}
		return defaultValue;
	}

	public Map<Key, Map<String, CFCInfo>> getCFCs() {
		return cfcs;
	}

	/*public Map<String, CFCInfo> getCFCs(DataSource ds) {
		Key key=KeyImpl.init(ds.getName());
		Map<String, CFCInfo> rtn = cfcs.get(key);
		if(rtn==null) return new HashMap<String, CFCInfo>();
		return rtn;
	}*/
	
	public Map<String, CFCInfo> getCFCs(Key datasSourceName) {
		Map<String, CFCInfo> rtn = cfcs.get(datasSourceName);
		if(rtn==null) return new HashMap<String, CFCInfo>();
		return rtn;
	}
	
	public void clearCFCs() {
		cfcs.clear();
	}

	public int sizeCFCs() {
		Iterator<Map<String, CFCInfo>> it = cfcs.values().iterator();
		int size=0;
		while(it.hasNext()){
			size+=it.next().size();
		}
		return size;
	}

	public DataSource[] getDataSources() {
		return sources.values().toArray(new DataSource[sources.size()]);
	}

	public void init() {
		Iterator<Key> it = cfcs.keySet().iterator();
		while(it.hasNext()){
			getFactory(it.next());
		}
	}
	
	public Map<Key, SessionFactory> getFactories() {
		Iterator<Key> it = cfcs.keySet().iterator();
		Map<Key,SessionFactory> map=new HashMap<Key, SessionFactory>();
		Key key;
		while(it.hasNext()){
			key = it.next();
			map.put(key, getFactory(key));
		}
		return map;
	}

	public DataSource getDataSource(Key datasSourceName) {
		return sources.get(datasSourceName);
	}
}
