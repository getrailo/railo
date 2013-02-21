package railo.runtime.orm.hibernate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import railo.commons.db.DBUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourcePro;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.orm.hibernate.event.AllEventListener;
import railo.runtime.orm.hibernate.event.EventListener;
import railo.runtime.orm.hibernate.event.InterceptorImpl;
import railo.runtime.orm.hibernate.event.PostDeleteEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostInsertEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostLoadEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostUpdateEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreDeleteEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreInsertEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreLoadEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreUpdateEventListenerImpl;
import railo.runtime.orm.hibernate.tuplizer.AbstractEntityTuplizerImpl;
import railo.runtime.orm.naming.CFCNamingStrategy;
import railo.runtime.orm.naming.DefaultNamingStrategy;
import railo.runtime.orm.naming.NamingStrategy;
import railo.runtime.orm.naming.SmartNamingStrategy;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.CastableStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;

public class HibernateORMEngine implements ORMEngine {


	private static final Collection.Key INIT = KeyImpl.intern("init");

	private Configuration configuration;

	private SessionFactory _factory;
	private String _datasource;
	//private Map<String,Long> _cfcids=new HashMap<String, Long>();
	//private Map<String,String> _cfcs=new HashMap<String, String>();
	private Map<String,CFCInfo> cfcs=new HashMap<String, CFCInfo>();

	private Struct tableInfo=new StructImpl();

	
	private QueryPlanCache _queryPlanCache;

	private DataSource ds;

	private List<Component> arr;

	private Object hash;

	private ORMConfiguration ormConf;

	private NamingStrategy namingStrategy=DefaultNamingStrategy.INSTANCE;

	public HibernateORMEngine() {}

	void checkExistent(PageContext pc,Component cfc) throws ORMException {
		if(!cfcs.containsKey(id(HibernateCaster.getEntityName(cfc))))
            throw new ORMException(this,"there is no mapping definition for component ["+cfc.getAbsName()+"]");
	}

	
	
	
	@Override
	public void init(PageContext pc) throws PageException{
		getSessionFactory(pc,true);
	}
		
	@Override
	public ORMSession createSession(PageContext pc) throws PageException {
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext();
		Object o=appContext.getORMDataSource();
		
		DataSource ds=o instanceof DataSource?(DataSource)o:((PageContextImpl)pc).getDataSource(Caster.toString(o));
		
		DatasourceConnection dc = ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().getDatasourceConnection(pc,ds,null,null);
		try{
			
			return new HibernateORMSession(this,getSessionFactory(pc),dc);
		}
		catch(PageException pe){
			//manager.releaseConnection(pc, dc);// connection is closed when session ends
			throw pe;
		}
	}
	

	QueryPlanCache getQueryPlanCache(PageContext pc) throws PageException {
		SessionFactory _old = _factory;
		SessionFactory _new = getSessionFactory(pc);
		
		if(_queryPlanCache==null || _old!=_new){
			_queryPlanCache=new QueryPlanCache((SessionFactoryImplementor) _new);
		}
		return _queryPlanCache;
	}

	@Override
	public SessionFactory getSessionFactory(PageContext pc) throws PageException{
		return getSessionFactory(pc,false);
	}
	
	public boolean reload(PageContext pc, boolean force) throws PageException {
		if(force) {
			if(_factory!=null){
				_factory.close();
				_factory=null;
				configuration=null;
			}
		}
		else {
			Object h = hash(pc);
			if(this.hash.equals(h))return false;
		}
		
		getSessionFactory(pc,true);
		return true;
	}


	private synchronized SessionFactory getSessionFactory(PageContext pc,boolean init) throws PageException {
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext();
		if(!appContext.isORMEnabled())
			throw new ORMException(this,"ORM is not enabled in "+Constants.APP_CFC+"/"+Constants.CFAPP_NAME);
		
		this.hash=hash(pc);
		
		// datasource
		Object o=appContext.getORMDataSource();
		if(StringUtil.isEmpty(o))
			throw new ORMException(this,"missing datasource defintion in "+Constants.APP_CFC+"/"+Constants.CFAPP_NAME);
		
		DataSource _ds = o instanceof DataSource?(DataSource)o:((PageContextImpl)pc).getDataSource(Caster.toString(o));
		
		
		if(ds==null || !ds.equals(_ds)){
			configuration=null;
			if(_factory!=null) _factory.close();
			_factory=null;
			ds=_ds;
		}
		
		// config
		ormConf = appContext.getORMConfiguration();
		
		//List<Component> arr = null;
		arr=null;
		if(init){
			if(ormConf.autogenmap()){
				arr = HibernateSessionFactory.loadComponents(pc, this, ormConf);
				cfcs.clear();
			}
			else 
				throw new HibernateException(this,"orm setting autogenmap=false is not supported yet");
		}
		
		// load entities
		if(!ArrayUtil.isEmpty(arr)) {
			loadNamingStrategy(ormConf);
			
			
			DatasourceConnectionPool pool = ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool();
			DatasourceConnection dc = pool.getDatasourceConnection(pc,ds,null,null);
			//DataSourceManager manager = pc.getDataSourceManager();
			//DatasourceConnection dc=manager.getConnection(pc,dsn, null, null);
			//this.ds=dc.getDatasource();
			try {
				Iterator<Component> it = arr.iterator();
				while(it.hasNext()){
					createMapping(pc,it.next(),dc,ormConf);
				}
			}
			finally {
				pool.releaseDatasourceConnection(dc);
				//manager.releaseConnection(pc,dc);
			}
			if(arr.size()!=cfcs.size()){
				Component cfc;
				String name,lcName;
				Map<String,String> names=new HashMap<String,String>();
				Iterator<Component> it = arr.iterator();
				while(it.hasNext()){
					cfc=it.next();
					name=HibernateCaster.getEntityName(cfc);
					lcName=name.toLowerCase();
					if(names.containsKey(lcName))
						throw new ORMException(this,"Entity Name ["+name+"] is ambigous, ["+names.get(lcName)+"] and ["+cfc.getPageSource().getDisplayPath()+"] use the same entity name."); 
					names.put(lcName,cfc.getPageSource().getDisplayPath());
				}	
			}
		}
		arr=null;		
		if(configuration!=null) return _factory;

		//MUST
		//cacheconfig
		//cacheprovider
		//...
		
		String mappings=HibernateSessionFactory.createMappings(this,cfcs);
		
		DatasourceConnectionPool pool = ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool();
		DatasourceConnection dc = pool.getDatasourceConnection(pc,ds,null,null);
		try{
			configuration = HibernateSessionFactory.createConfiguration(this,mappings,dc,ormConf);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		finally {
			pool.releaseDatasourceConnection(dc);
		}
		
		addEventListeners(pc, configuration,ormConf,cfcs);
		
		EntityTuplizerFactory tuplizerFactory = configuration.getEntityTuplizerFactory();
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, CFCEntityTuplizer.class);
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, MapEntityTuplizer.class);
		tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, AbstractEntityTuplizerImpl.class);
		tuplizerFactory.registerDefaultTuplizerClass(EntityMode.POJO, AbstractEntityTuplizerImpl.class);
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.POJO, AbstractEntityTuplizerImpl.class);
		
		//configuration.setEntityResolver(new CFCEntityResolver());
		//configuration.setEntityNotFoundDelegate(new EntityNotFoundDelegate());
		
		
		
		return _factory = configuration.buildSessionFactory();
	}
	
	private void loadNamingStrategy(ORMConfiguration ormConf) throws PageException {
		String strNamingStrategy=ormConf.namingStrategy();
		if(StringUtil.isEmpty(strNamingStrategy,true)) {
			namingStrategy=DefaultNamingStrategy.INSTANCE;
		}
		else {
			strNamingStrategy=strNamingStrategy.trim();
			if("default".equalsIgnoreCase(strNamingStrategy)) 
				namingStrategy=DefaultNamingStrategy.INSTANCE;
			else if("smart".equalsIgnoreCase(strNamingStrategy)) 
				namingStrategy=SmartNamingStrategy.INSTANCE;
			else 
				namingStrategy=new CFCNamingStrategy(strNamingStrategy);
		}
	}

	private static void addEventListeners(PageContext pc, Configuration config,ORMConfiguration ormConfig, Map<String, CFCInfo> cfcs) throws PageException {
		if(!ormConfig.eventHandling()) return;
		String eventHandler = ormConfig.eventHandler();
		AllEventListener listener=null;
		if(!StringUtil.isEmpty(eventHandler,true)){
			//try {
				Component c = pc.loadComponent(eventHandler.trim());
				
				listener = new AllEventListener(c);
		        //config.setInterceptor(listener);
			//}catch (PageException e) {e.printStackTrace();}
		}
		config.setInterceptor(new InterceptorImpl(listener));
        EventListeners listeners = config.getEventListeners();
        
        // post delete
		List<EventListener> 
		list=merge(listener,cfcs,EventListener.POST_DELETE);
		listeners.setPostDeleteEventListeners(list.toArray(new PostDeleteEventListener[list.size()]));
		
        // post insert
		list=merge(listener,cfcs,EventListener.POST_INSERT);
		listeners.setPostInsertEventListeners(list.toArray(new PostInsertEventListener[list.size()]));
		
		// post update
		list=merge(listener,cfcs,EventListener.POST_UPDATE);
		listeners.setPostUpdateEventListeners(list.toArray(new PostUpdateEventListener[list.size()]));
		
		// post load
		list=merge(listener,cfcs,EventListener.POST_LOAD);
		listeners.setPostLoadEventListeners(list.toArray(new PostLoadEventListener[list.size()]));
		
		// pre delete
		list=merge(listener,cfcs,EventListener.PRE_DELETE);
		listeners.setPreDeleteEventListeners(list.toArray(new PreDeleteEventListener[list.size()]));
		
		// pre insert
		//list=merge(listener,cfcs,EventListener.PRE_INSERT);
		//listeners.setPreInsertEventListeners(list.toArray(new PreInsertEventListener[list.size()]));
		
		// pre load
		list=merge(listener,cfcs,EventListener.PRE_LOAD);
		listeners.setPreLoadEventListeners(list.toArray(new PreLoadEventListener[list.size()]));
		
		// pre update
		//list=merge(listener,cfcs,EventListener.PRE_UPDATE);
		//listeners.setPreUpdateEventListeners(list.toArray(new PreUpdateEventListener[list.size()]));
	}

	private static List<EventListener> merge(EventListener listener, Map<String, CFCInfo> cfcs, Collection.Key eventType) {
		List<EventListener> list=new ArrayList<EventListener>();
			
		
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		Entry<String, CFCInfo> entry;
		Component cfc;
		while(it.hasNext()){
			entry = it.next();
			cfc = entry.getValue().getCFC();
			if(EventListener.hasEventType(cfc,eventType)) {
				if(EventListener.POST_DELETE.equals(eventType))
					list.add(new PostDeleteEventListenerImpl(cfc));
				if(EventListener.POST_INSERT.equals(eventType))
					list.add(new PostInsertEventListenerImpl(cfc));
				if(EventListener.POST_LOAD.equals(eventType))
					list.add(new PostLoadEventListenerImpl(cfc));
				if(EventListener.POST_UPDATE.equals(eventType))
					list.add(new PostUpdateEventListenerImpl(cfc));
				
				if(EventListener.PRE_DELETE.equals(eventType))
					list.add(new PreDeleteEventListenerImpl(cfc));
				if(EventListener.PRE_INSERT.equals(eventType))
					list.add(new PreInsertEventListenerImpl(cfc));
				if(EventListener.PRE_LOAD.equals(eventType))
					list.add(new PreLoadEventListenerImpl(cfc));
				if(EventListener.PRE_UPDATE.equals(eventType))
					list.add(new PreUpdateEventListenerImpl(cfc));
			}
		}
		
		// general listener
		if(listener!=null && EventListener.hasEventType(listener.getCFC(),eventType))
			list.add(listener);
		
		return list;
	}

	private Object hash(PageContext pc) throws PageException {
		ApplicationContextPro appContext=(ApplicationContextPro) pc.getApplicationContext();
		Object o=appContext.getORMDataSource();
		DataSource ds;
		if(o instanceof DataSource) ds=(DataSource) o;
		else ds=((PageContextImpl)pc).getDataSource(Caster.toString(o));
		if(ds instanceof DataSourcePro)
			return hash=((DataSourcePro)ds).id()+":"+appContext.getORMConfiguration().hash();
		
		return ds.getClazz()+":"+ds.getDsnTranslated()+":"+appContext.getORMConfiguration().hash();
	}

	public void createMapping(PageContext pc,Component cfc, DatasourceConnection dc, ORMConfiguration ormConf) throws PageException {
		String id=id(HibernateCaster.getEntityName(cfc));
		CFCInfo info=cfcs.get(id);
		//Long modified=cfcs.get(id);
		String xml;
		long cfcCompTime = ComponentUtil.getCompileTime(pc,cfc.getPageSource());
		if(info==null || (ORMUtil.equals(info.getCFC(),cfc) ))	{//&& info.getModified()!=cfcCompTime
			StringBuilder sb=new StringBuilder();
			
			long xmlLastMod = loadMapping(sb,ormConf, cfc);
			Element root;
			// create maaping
			if(true || xmlLastMod< cfcCompTime) {//MUSTMUST
				configuration=null;
				Document doc=null;
				try {
					doc=XMLUtil.newDocument();
				}catch(Throwable t){t.printStackTrace();}
				
				root=doc.createElement("hibernate-mapping");
				doc.appendChild(root);
				pc.addPageSource(cfc.getPageSource(), true);
				try{
					HBMCreator.createXMLMapping(pc,dc,cfc,ormConf,root, this);
				}
				finally{
					pc.removeLastPageSource(true);
				}
				xml=XMLCaster.toString(root.getChildNodes(),true,true);
				saveMapping(ormConf,cfc,root);
			}
			// load
			else {
				xml=sb.toString();
				root=Caster.toXML(xml).getOwnerDocument().getDocumentElement();
				/*print.o("1+++++++++++++++++++++++++++++++++++++++++");
				print.o(xml);
				print.o("2+++++++++++++++++++++++++++++++++++++++++");
				print.o(root);
				print.o("3+++++++++++++++++++++++++++++++++++++++++");*/
				
			}
			cfcs.put(id, new CFCInfo(ComponentUtil.getCompileTime(pc,cfc.getPageSource()),xml,cfc));
		}
		
	}

	private static void saveMapping(ORMConfiguration ormConf, Component cfc, Element hm) {
		if(ormConf.saveMapping()){
			Resource res=cfc.getPageSource().getPhyscalFile();
			if(res!=null){
				res=res.getParentResource().getRealResource(res.getName()+".hbm.xml");
				try{
				IOUtil.write(res, 
						XMLCaster.toString(hm,false,true,
								HibernateSessionFactory.HIBERNATE_3_PUBLIC_ID,
								HibernateSessionFactory.HIBERNATE_3_SYSTEM_ID,
								HibernateSessionFactory.HIBERNATE_3_ENCODING), HibernateSessionFactory.HIBERNATE_3_ENCODING, false);
				}
				catch(Exception e){} 
			}
		}
	}
	
	private static long loadMapping(StringBuilder sb,ORMConfiguration ormConf, Component cfc) {
		
		Resource res=cfc.getPageSource().getPhyscalFile();
		if(res!=null){
			res=res.getParentResource().getRealResource(res.getName()+".hbm.xml");
			try{
				sb.append(IOUtil.toString(res, "UTF-8"));
				return res.lastModified();
			}
			catch(Exception e){} 
		}
		return 0;
	}

	@Override
	public int getMode() {
		//MUST impl
		return MODE_LAZY;
	}
	
	public DataSource getDataSource(){
		return ds;
	}

	@Override
	public String getLabel() {
		return "Hibernate";
	}

	public Struct getTableInfo(DatasourceConnection dc, String tableName,ORMEngine engine) throws PageException {
		Collection.Key keyTableName=KeyImpl.init(tableName);
		Struct columnsInfo = (Struct) tableInfo.get(keyTableName,null);
		if(columnsInfo!=null) return columnsInfo;
		
		columnsInfo = checkTable(dc,tableName,engine);
    	tableInfo.setEL(keyTableName,columnsInfo);
    	return columnsInfo;
	}
	
	private static Struct checkTable(DatasourceConnection dc, String tableName, ORMEngine engine) throws PageException {
		String dbName=dc.getDatasource().getDatabase();
		try {
			
			DatabaseMetaData md = dc.getConnection().getMetaData();
			Struct rows=checkTableFill(md,dbName,tableName);
			if(rows.size()==0)	{
				String tableName2 = checkTableValidate(md,dbName,tableName);
				if(tableName2!=null)rows=checkTableFill(md,dbName,tableName2);
			}
			
			if(rows.size()==0)	{
				//ORMUtil.printError("there is no table with name  ["+tableName+"] defined", engine);
				return null;
			}
			return rows;
		} catch (SQLException e) {
			throw Caster.toPageException(e);
		}
	}
	


	private static Struct checkTableFill(DatabaseMetaData md, String dbName, String tableName) throws SQLException, PageException {
		Struct rows=new CastableStruct(tableName,Struct.TYPE_LINKED);
		ResultSet columns = md.getColumns(dbName, null, tableName, null);
		//print.o(new QueryImpl(columns,""));
		try{
			String name;
			Object nullable;
			while(columns.next()) {
				name=columns.getString("COLUMN_NAME");
				
				nullable=columns.getObject("IS_NULLABLE");
				rows.setEL(KeyImpl.init(name),new ColumnInfo(
						name,
						columns.getInt("DATA_TYPE"),
						columns.getString("TYPE_NAME"),
						columns.getInt("COLUMN_SIZE"),
						Caster.toBooleanValue(nullable)	
				));
			}
		}
		finally {
			DBUtil.closeEL(columns);
		}// Table susid defined for cfc susid does not exist.
		
		return rows;
	}

	private static String checkTableValidate(DatabaseMetaData md, String dbName,String tableName) {

		ResultSet tables=null;
        try{
        	tables = md.getTables(dbName, null, null, null);
			String name;
			while(tables.next()) {
				name=tables.getString("TABLE_NAME");
				if(name.equalsIgnoreCase(tableName) && StringUtil.indexOfIgnoreCase(tables.getString("TABLE_TYPE"), "SYSTEM")==-1)
				return name;	
			}
		}
        catch(Throwable t){}
		finally {
			DBUtil.closeEL(tables);
		}
        return null;
        
        
		
	}

	@Override
	public ORMConfiguration getConfiguration(PageContext pc) {
		ApplicationContext ac = pc.getApplicationContext();
		if(!ac.isORMEnabled())
			return null;
		return  ac.getORMConfiguration();
	}

	/**
	 * @param pc
	 * @param session
	 * @param entityName name of the entity to get
	 * @param unique create a unique version that can be manipulated
	 * @param init call the nit method of the cfc or not
	 * @return
	 * @throws PageException
	 */
	public Component create(PageContext pc, HibernateORMSession session,String entityName, boolean unique) throws PageException {
		
		// get existing entity
		Component cfc = _create(pc,entityName,unique);
		if(cfc!=null)return cfc;
		
		// reinit ORMEngine
		SessionFactory _old = _factory;
		SessionFactory _new = getSessionFactory(pc,true);
		if(_old!=_new){
			session.resetSession(_new);
			cfc = _create(pc,entityName,unique);
			if(cfc!=null)return cfc;
		}
		
		
		
		ORMConfiguration ormConf = pc.getApplicationContext().getORMConfiguration();
		Resource[] locations = ormConf.getCfcLocations();
		
		throw new ORMException(
				"No entity (persitent component) with name ["+entityName+"] found, available entities are ["+railo.runtime.type.List.arrayToList(getEntityNames(), ", ")+"] ",
				"component are searched in the following directories ["+toString(locations)+"]");
		
	}
	
	
	private String toString(Resource[] locations) {
		if(locations==null) return "";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<locations.length;i++){
			if(i>0) sb.append(", ");
			sb.append(locations[i].getAbsolutePath());
		}
		return sb.toString();
	}

	private Component _create(PageContext pc, String entityName, boolean unique) throws PageException {
		CFCInfo info = cfcs.get(id(entityName));
		if(info!=null) {
			Component cfc = info.getCFC();
			if(unique){
				cfc=(Component)Duplicator.duplicate(cfc,false);
				if(cfc.contains(pc,INIT))cfc.call(pc, "init",new Object[]{});
			}
			return cfc;
		}
		return null;
	}

	public static String id(String id) {
		return id.toLowerCase().trim();
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
		String[] names=null;
		// search array (array exist when cfcs is in generation)
		
		if(arr!=null){
			names=new String[arr.size()];
			int index=0;
			Iterator<Component> it2 = arr.iterator();
			while(it2.hasNext()){
				cfc=it2.next();
				names[index++]=cfc.getName();
				if(isEntity(cfc,cfcName,name)) //if(cfc.equalTo(name))
					return unique?(Component)Duplicator.duplicate(cfc,false):cfc;
			}
		}
		else {
			// search cfcs
			Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
			Entry<String, CFCInfo> entry;
			while(it.hasNext()){
				entry=it.next();
				cfc=entry.getValue().getCFC();
				if(isEntity(cfc,cfcName,name)) //if(cfc.instanceOf(name))
					return unique?(Component)Duplicator.duplicate(cfc,false):cfc;
				
				//if(name.equalsIgnoreCase(HibernateCaster.getEntityName(cfc)))
				//	return cfc;
			}
			names=cfcs.keySet().toArray(new String[cfcs.size()]);
		}
		
		// search by entityname //TODO is this ok?
		CFCInfo info = cfcs.get(name.toLowerCase());
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)Duplicator.duplicate(cfc,false):cfc;
		}
		
		throw new ORMException(this,"entity ["+name+"] "+(StringUtil.isEmpty(cfcName)?"":"with cfc name ["+cfcName+"] ")+"does not exist, existing  entities are ["+railo.runtime.type.List.arrayToList(names, ", ")+"]");
		
	}
	

	private boolean isEntity(Component cfc, String cfcName, String name) {
		if(!StringUtil.isEmpty(cfcName)) {
			if(cfc.equalTo(cfcName)) return true;

			if(cfcName.indexOf('.')!=-1) {
				String path=cfcName.replace('.', '/')+".cfc";
				Resource[] locations = ormConf.getCfcLocations();
				for(int i=0;i<locations.length;i++){
					if(locations[i].getRealResource(path).equals(cfc.getPageSource().getResource()))
						return true;
				}
				return false;
			}
		}
		
		if(cfc.equalTo(name)) return true;
		return name.equalsIgnoreCase(HibernateCaster.getEntityName(cfc));
	}

	public Component getEntityByEntityName(String entityName,boolean unique) throws PageException {
		Component cfc;
		
		
		CFCInfo info = cfcs.get(entityName.toLowerCase());
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)Duplicator.duplicate(cfc,false):cfc;
		}
		
		if(arr!=null){
			Iterator<Component> it2 = arr.iterator();
			while(it2.hasNext()){
				cfc=it2.next();
				if(HibernateCaster.getEntityName(cfc).equalsIgnoreCase(entityName))
					return unique?(Component)Duplicator.duplicate(cfc,false):cfc;
			}
		}
		
		
		
		throw new ORMException(this,"entity ["+entityName+"] does not exist");
		
	}
	
	

	public String[] getEntityNames() {
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		String[] names=new String[cfcs.size()];
		int index=0;
		while(it.hasNext()){
			names[index++]=HibernateCaster.getEntityName(it.next().getValue().getCFC());
			//names[index++]=it.next().getValue().getCFC().getName();
		}
		return names;
		
		//return cfcs.keySet().toArray(new String[cfcs.size()]);
	}

	public String convertTableName(String tableName) {
		if(tableName==null) return null;
		//print.o("table:"+namingStrategy.getType()+":"+tableName+":"+namingStrategy.convertTableName(tableName));
		return namingStrategy.convertTableName(tableName);
	}

	public String convertColumnName(String columnName) {
		if(columnName==null) return null;
		//print.o("column:"+namingStrategy.getType()+":"+columnName+":"+namingStrategy.convertTableName(columnName));
		return namingStrategy.convertColumnName(columnName);
	}
	
	
	
}
class CFCInfo {
	private String xml;
	private long modified;
	private Component cfc;
	
	public CFCInfo(long modified, String xml, Component cfc) {
		this.modified=modified;
		this.xml=xml;
		this.cfc=cfc;
	}
	/**
	 * @return the cfc
	 */
	public Component getCFC() {
		return cfc;
	}
	/**
	 * @return the xml
	 */
	public String getXML() {
		return xml;
	}
	/**
	 * @return the modified
	 */
	public long getModified() {
		return modified;
	}
	
}

