package railo.runtime.orm.hibernate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import railo.commons.db.DBUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPro;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceManager;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.hibernate.tuplizer.AbstractEntityTuplizerImpl;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.CastableStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.util.ApplicationContext;
import railo.runtime.util.ApplicationContextImpl;

public class HibernateORMEngine implements ORMEngine {


	private static final Collection.Key INIT = KeyImpl.getInstance("init");

	private Configuration configuration;

	private SessionFactory _factory;
	private String datasource;
	//private Map<String,Long> _cfcids=new HashMap<String, Long>();
	//private Map<String,String> _cfcs=new HashMap<String, String>();
	private Map<String,CFCInfo> cfcs=new HashMap<String, CFCInfo>();

	private Struct tableInfo=new StructImpl();

	
	private QueryPlanCache _queryPlanCache;

	private DataSource ds;

	private List<Component> arr;

	private Object hash;

	public HibernateORMEngine() {}

	void checkExistent(PageContext pc,Component cfc) throws ORMException {
		if(!cfcs.containsKey(id(HibernateCaster.getEntityName(pc, cfc))))
            throw new ORMException(this,"there is no mapping definition for component ["+cfc.getAbsName()+"]");
	}

	
	
	
	/**
	 * @see railo.runtime.orm.ORMEngine#init(railo.runtime.PageContext)
	 */
	public void init(PageContext pc) throws PageException{
		getSessionFactory(pc,true);
	}
		
	/**
	 * @see railo.runtime.orm.ORMEngine#getSession(railo.runtime.PageContext)
	 */
	public ORMSession createSession(PageContext pc) throws PageException {
		ApplicationContextImpl appContext = ((ApplicationContextImpl)pc.getApplicationContext());
		String dsn=appContext.getORMDatasource();
		
		DataSourceManager manager = pc.getDataSourceManager();
		DatasourceConnection dc=manager.getConnection(pc,dsn, null, null);
		try{
			
			return new HibernateORMSession(this,getSessionFactory(pc),dc);
		}
		catch(PageException pe){
			manager.releaseConnection(pc, dc);
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

	/**
	 * @see railo.runtime.orm.ORMEngine#getSessionFactory(railo.runtime.PageContext)
	 */
	public SessionFactory getSessionFactory(PageContext pc) throws PageException{
		return getSessionFactory(pc,false);
	}
	
	public boolean reload(PageContext pc) throws PageException {
		Object h = hash((ApplicationContextImpl)pc.getApplicationContext());
		if(this.hash.equals(h))return false;
		getSessionFactory(pc,true);
		return true;
	}


	private synchronized SessionFactory getSessionFactory(PageContext pc,boolean init) throws PageException {
		
		ApplicationContextImpl appContext = ((ApplicationContextImpl)pc.getApplicationContext());
		if(!appContext.isORMEnabled())
			throw new ORMException(this,"ORM is not enabled in application.cfc/cfapplication");
		
		ConfigWeb config = pc.getConfig();
		
		this.hash=hash(appContext);
		
		// datasource
		String dsn=appContext.getORMDatasource();
		if(StringUtil.isEmpty(dsn))
			throw new ORMException(this,"missing datasource defintion in application.cfc/cfapplication");
		if(!dsn.equalsIgnoreCase(datasource)){
			configuration=null;
			datasource=dsn.toLowerCase();
		}
		
		// config
		ORMConfiguration ormConf = appContext.getORMConfiguration();
		
		//List<Component> arr = null;
		arr=null;
		if(init && ormConf.autogenmap()){
			arr = HibernateSessionFactory.loadComponents(pc, this, ormConf);	
		}
		
		// load entities
		if(!ArrayUtil.isEmpty(arr)) {
			DataSourceManager manager = pc.getDataSourceManager();
			DatasourceConnection dc=manager.getConnection(pc,dsn, null, null);
			this.ds=dc.getDatasource();
			try {
				Iterator<Component> it = arr.iterator();
				while(it.hasNext()){
					createMapping(pc,it.next(),dc,ormConf);
					/*try {
						createMapping(pc,it.next(),dc,ormConf);
					}
					catch(Throwable t){
						ORMUtil.printError(t, this);
						
					}*/
				}
			}
			finally {
				manager.releaseConnection(pc,dc);
			}
		}
		arr=null;
		
		
		if(configuration!=null) return _factory;
		

		DataSource ds = config.getDataSource(dsn);
		
		
		//MUST
		//cacheconfig
		//cacheprovider
		//...
		
		//print.err(railo.runtime.type.List.arrayToList(cfcs.keySet().toArray(new String[cfcs.size()]), ","));
		
		String mappings=HibernateSessionFactory.createMappings(cfcs);
		
		/*ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		try {
			mappings=IOUtil.toString(frp.getResource("/Users/mic/mapping.xml"),null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		print.o(cfcs.keySet());
		print.o(mappings);*/
		
		DataSourceManager manager = pc.getDataSourceManager();
		DatasourceConnection dc=manager.getConnection(pc,dsn, null, null);
		try{
			configuration = HibernateSessionFactory.createConfiguration(this,mappings,dc,ormConf);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		finally {
			manager.releaseConnection(pc,dc);
		}
		
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

	private Object hash(ApplicationContextImpl appContext) {
		String hash=appContext.getORMDatasource()+":"+appContext.getORMConfiguration().hash();
		//print.ds(hash);
		return hash;
	}

	public void createMapping(PageContext pc,Component cfc, DatasourceConnection dc, ORMConfiguration ormConf) throws PageException {
		String id=id(HibernateCaster.getEntityName(pc, cfc));
		CFCInfo info=cfcs.get(id);
		//Long modified=cfcs.get(id);
		String xml;
		long cfcCompTime = ComponentUtil.getCompileTime(pc,((ComponentPro)cfc).getPageSource());
		if(info==null || (info.getCFC().equals(cfc) && info.getModified()!=cfcCompTime))	{
			StringBuilder sb=new StringBuilder();
			
			long xmlLastMod = loadMapping(sb,ormConf, cfc);
			Element root;
			// create maaping
			if(true || xmlLastMod< cfcCompTime) {
				configuration=null;
				Document doc=null;
				try {
					doc=XMLUtil.newDocument();
				}catch(Throwable t){t.printStackTrace();}
				
				root=doc.createElement("hibernate-mapping");
				doc.appendChild(root);
				pc.addPageSource(ComponentUtil.toComponentPro(cfc).getPageSource(), true);
				try{
					HBMCreator.createXMLMapping(pc,dc,cfc,ormConf,root, this);
				}
				finally{
					pc.removeLastPageSource(true);
				}
				xml=XMLCaster.toString(root.getChildNodes(),true);
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
			
			
			cfcs.put(id, new CFCInfo(ComponentUtil.getCompileTime(pc,((ComponentPro)cfc).getPageSource()),xml,cfc));
			
		}
		
	}

	private static void saveMapping(ORMConfiguration ormConf, Component cfc, Element hm) throws ExpressionException {
		if(ormConf.saveMapping()){
			Resource res=ComponentUtil.toComponentPro(cfc).getPageSource().getPhyscalFile();
			if(res!=null){
				res=res.getParentResource().getRealResource(res.getName()+".hbm.xml");
				try{
				IOUtil.write(res, XMLCaster.toString(hm), "UTF-8", false);
				}
				catch(Exception e){} 
			}
		}
	}
	
	private static long loadMapping(StringBuilder sb,ORMConfiguration ormConf, Component cfc) throws ExpressionException {
		
		Resource res=ComponentUtil.toComponentPro(cfc).getPageSource().getPhyscalFile();
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

	/**
	 * @see railo.runtime.orm.ORMEngine#getMode()
	 */
	public int getMode() {
		//MUST impl
		return MODE_LAZY;
	}
	
	public DataSource getDataSource(){
		return ds;
	}

	/**
	 * @see railo.runtime.orm.ORMEngine#getLabel()
	 */
	public String getLabel() {
		return "Hibernate";
	}

	public Struct getTableInfo(DatasourceConnection dc, String tableName,ORMEngine engine) throws PageException {
		//print.out("getTableInfo:"+tableName);
		Struct columnsInfo = (Struct) tableInfo.get(tableName,null);
		if(columnsInfo!=null) return columnsInfo;
		
		columnsInfo = checkTable(dc,tableName,engine);
    	tableInfo.setEL(tableName,columnsInfo);
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
		Struct rows=new CastableStruct(tableName);
		ResultSet columns = md.getColumns(dbName, null, tableName, null);
		
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

	/**
	 * @see railo.runtime.orm.ORMEngine#getConfiguration(railo.runtime.PageContext)
	 */
	public ORMConfiguration getConfiguration(PageContext pc) {
		ApplicationContext ac = pc.getApplicationContext();
		if(!(ac instanceof ApplicationContextImpl))
			return null;
		ApplicationContextImpl aci=(ApplicationContextImpl) ac;
		if(!aci.isORMEnabled())
			return null;
		return  aci.getORMConfiguration();
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
		ComponentImpl cfc = _create(pc,entityName,unique);
		if(cfc!=null)return cfc;
		
		// reinit ORMEngine
		SessionFactory _old = _factory;
		SessionFactory _new = getSessionFactory(pc,true);
		if(_old!=_new){
			session.resetSession(_new);
			cfc = _create(pc,entityName,unique);
			if(cfc!=null)return cfc;
		}
		
		
		
		ApplicationContextImpl appContext = ((ApplicationContextImpl)pc.getApplicationContext());
		ORMConfiguration ormConf = appContext.getORMConfiguration();
		Resource[] locations = ormConf.getCfcLocations();
		
		
		/* / get key list
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		Entry<String, CFCInfo> entry;
		String name;
		StringBuilder keys=new StringBuilder();
		while(it.hasNext()){
			entry=it.next();
			name=entry.getValue().getCFC().getName();
			if(keys.length()>0) keys.append(", ");
			keys.append(name);
		}*/
		
		
		throw new ORMException(
				"No entity (persitent component) with name ["+entityName+"] found, available entities are ["+railo.runtime.type.List.arrayToList(getEntityNames(), ", ")+"] ",
				"component are searched in the following directories ["+toString(locations)+"]");
		
		/*
		// try to load "new" entity
		ComponentImpl cfc;
		try{
			cfc = (ComponentImpl) HibernateCaster.toComponent(pc, entityName);
		}
		catch(PageException pe){
			throw new ORMException("No entity (persitent component) with name ["+entityName+"] found, available entities are ["+railo.runtime.type.List.arrayToList(cfcs.keySet().toArray(new String[cfcs.size()]),", ")+"] ");
		}
		if(cfc.isPersistent()) {
			if(unique){
				cfc=(ComponentImpl)cfc.duplicate(false);
				if(cfc.contains(pc,INIT))cfc.call(pc, "init",new Object[]{});
			}
			
			SessionFactory of=session.getSessionFactory();
			SessionFactory nf = getSessionFactory(pc, cfc,false);
			
			// reset Session
			if(of!=nf){
				session.resetSession(nf);
			}
			return unique?(Component)cfc.duplicate(false):cfc;
		}

		throw new ORMException("No entity (persitent component) with name ["+entityName+"] found, available entities are ["+railo.runtime.type.List.arrayToList(cfcs.keySet().toArray(new String[cfcs.size()]),", ")+"] ");
		*/
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

	private ComponentImpl _create(PageContext pc, String entityName, boolean unique) throws PageException {
		CFCInfo info = cfcs.get(id(entityName));
		if(info!=null) {
			ComponentImpl cfc = (ComponentImpl) info.getCFC();
			if(unique){
				cfc=(ComponentImpl)cfc.duplicate(false);
				if(cfc.contains(pc,INIT))cfc.call(pc, "init",new Object[]{});
			}
			return cfc;
		}
		return null;
	}

	public static String id(String id) {
		return id.toLowerCase().trim();
	}

	public Component getEntityByCFCName(String cfcname,boolean unique) throws PageException {
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
				//print.e(names[index-1]+":"+cfcname);
				if(cfc.instanceOf(cfcname))
					return unique?(Component)cfc.duplicate(false):cfc;
			}
			
			
		}
		else {
			// search cfcs
			Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
			Entry<String, CFCInfo> entry;
			while(it.hasNext()){
				entry=it.next();
				cfc=entry.getValue().getCFC();
				if(cfc.instanceOf(cfcname))
					return unique?(Component)cfc.duplicate(false):cfc;
				
				if(cfcname.equalsIgnoreCase(HibernateCaster.getEntityName(null, cfc)))
					return cfc;
			}
			names=cfcs.keySet().toArray(new String[cfcs.size()]);
		}
		
		// search by entityname //TODO is this ok?
		CFCInfo info = cfcs.get(cfcname.toLowerCase());
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)cfc.duplicate(false):cfc;
		}
		
		throw new ORMException(this,"entity ["+cfcname+"] does not exist, existing  entities are ["+railo.runtime.type.List.arrayToList(names, ", ")+"]");
		
	}
	

	public String[] getEntityNames() {
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		String[] names=new String[cfcs.size()];
		int index=0;
		while(it.hasNext()){
			names[index++]=it.next().getValue().getCFC().getName();
		}
		return names;
		
		//return cfcs.keySet().toArray(new String[cfcs.size()]);
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

