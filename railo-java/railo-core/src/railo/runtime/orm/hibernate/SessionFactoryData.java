package railo.runtime.orm.hibernate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.orm.hibernate.naming.CFCNamingStrategy;
import railo.runtime.orm.hibernate.naming.DefaultNamingStrategy;
import railo.runtime.orm.naming.NamingStrategy;
import railo.runtime.orm.hibernate.naming.SmartNamingStrategy;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

public class SessionFactoryData {

	public List<Component> tmpList;
	//private final Map<String, CFCInfo> cfcs=new HashMap<String, CFCInfo>();
	private final Map<DataSource,Map<String, CFCInfo>> cfcs=new HashMap<DataSource, Map<String,CFCInfo>>();
	private final Map<DataSource,Configuration> configurations=new HashMap<DataSource,Configuration>();
	private final Map<DataSource,SessionFactory> factories=new HashMap<DataSource,SessionFactory>();
	private final Map<DataSource,QueryPlanCache> queryPlanCaches=new HashMap<DataSource,QueryPlanCache>();
	
	private final ORMConfiguration ormConf;
	//private final DataSource datasource;
	//private Configuration configuration=null;
	//private SessionFactory factory;
	
	//private QueryPlanCache queryPlanCache;
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
	
	public QueryPlanCache getQueryPlanCache(DataSource ds) {
		QueryPlanCache qpc = queryPlanCaches.get(ds);
		if(qpc==null){
			queryPlanCaches.put(ds, qpc=new QueryPlanCache((SessionFactoryImplementor) getFactory(ds)));
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
		return configurations.get(ds);
	}

	public void setConfiguration(Log log,String mappings, DatasourceConnection dc) throws PageException, SQLException, IOException {
		configurations.put(dc.getDatasource(),HibernateSessionFactory.createConfiguration(log,mappings,dc,this));
	}


	public void buildSessionFactory(DataSource ds) {
		Configuration conf = getConfiguration(ds);
		if(conf==null) throw new RuntimeException("cannot build factory because there is no configuration"); // this should never happen
		factories.put(ds, conf.buildSessionFactory());
	}

	public SessionFactory getFactory(DataSource ds){
		SessionFactory factory = factories.get(ds);
		if(factory==null && getConfiguration(ds)!=null) buildSessionFactory(ds);// this should never be happen
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
		Map<String, CFCInfo> map = cfcs.get(ds);
		if(map==null) cfcs.put(ds, map=new HashMap<String, CFCInfo>());
		map.put(HibernateUtil.id(entityName), info);
	}
	

	CFCInfo getCFC(String entityName, CFCInfo defaultValue) {
		Iterator<Map<String, CFCInfo>> it = cfcs.values().iterator();
		while(it.hasNext()){
			CFCInfo info = it.next().get(HibernateUtil.id(entityName));
			if(info!=null) return info;
		}
		return defaultValue;
	}

	public Map<DataSource, Map<String, CFCInfo>> getCFCs() {
		return cfcs;
	}

	public Map<String, CFCInfo> getCFCs(DataSource ds) {
		Map<String, CFCInfo> rtn = cfcs.get(ds);
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
		return cfcs.keySet().toArray(new DataSource[cfcs.size()]);
	}

	public void init() {
		Iterator<DataSource> it = cfcs.keySet().iterator();
		while(it.hasNext()){
			getFactory(it.next());
		}
	}
	
	public Map<DataSource, SessionFactory> getFactories() {
		Iterator<DataSource> it = cfcs.keySet().iterator();
		Map<DataSource,SessionFactory> map=new HashMap<DataSource, SessionFactory>();
		DataSource ds;
		while(it.hasNext()){
			ds = it.next();
			map.put(ds, getFactory(ds));
		}
		return map;
	}
}
