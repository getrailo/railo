package railo.runtime.orm.hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.w3c.dom.Document;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.sql.SQLUtil;
import railo.runtime.Component;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Caster;
import railo.runtime.config.Constants;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMUtil;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ArrayUtil;

public class HibernateSessionFactory {
	
	
	public static final String HIBERNATE_3_PUBLIC_ID = "-//Hibernate/Hibernate Mapping DTD 3.0//EN";
	public static final String HIBERNATE_3_SYSTEM_ID = "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd";
	public static final String HIBERNATE_3_ENCODING = "UTF-8";
	public static final String HIBERNATE_3_DOCTYPE_DEFINITION = "<!DOCTYPE hibernate-mapping PUBLIC \""+HIBERNATE_3_PUBLIC_ID+"\" \""+HIBERNATE_3_SYSTEM_ID+"\">";
	

	public static Configuration createConfiguration(HibernateORMEngine engine,String mappings, DatasourceConnection dc, ORMConfiguration ormConf) throws SQLException, IOException, PageException {
		/*
		 autogenmap
		 cacheconfig
		 cacheprovider
		 cfclocation
		 datasource
		 dbcreate
		 eventHandling
		 flushatrequestend
		 ormconfig
		 sqlscript
		 useDBForMapping
		 */
		
		// dialect
		DataSource ds = dc.getDatasource();
		String dialect=Dialect.getDialect(ormConf.getDialect());
		if(StringUtil.isEmpty(dialect)) dialect=Dialect.getDialect(ds);
		if(StringUtil.isEmpty(dialect))
			throw new ORMException(engine,"A valid dialect definition inside the "+Constants.APP_CFC+"/"+Constants.CFAPP_NAME+" is missing. The dialect cannot be determinated automatically");
		
		// Cache Provider
		String cacheProvider = ormConf.getCacheProvider();
		if(StringUtil.isEmpty(cacheProvider) || "EHCache".equalsIgnoreCase(cacheProvider)) 			
																cacheProvider="org.hibernate.cache.EhCacheProvider";
		else if("JBossCache".equalsIgnoreCase(cacheProvider)) 	cacheProvider="org.hibernate.cache.TreeCacheProvider";
		else if("HashTable".equalsIgnoreCase(cacheProvider)) 	cacheProvider="org.hibernate.cache.HashtableCacheProvider";
		else if("SwarmCache".equalsIgnoreCase(cacheProvider)) 	cacheProvider="org.hibernate.cache.SwarmCacheProvider";
		else if("OSCache".equalsIgnoreCase(cacheProvider)) 		cacheProvider="org.hibernate.cache.OSCacheProvider";
	
		Resource cacheConfig = ormConf.getCacheConfig();
		Configuration configuration = new Configuration();
		
		// ormConfig
		Resource conf = ormConf.getOrmConfig();
		if(conf!=null){
			try {
				Document doc = XMLUtil.parse(XMLUtil.toInputSource(conf), null, false);
				configuration.configure(doc);
			} 
			catch (Throwable t) {
				ORMUtil.printError(t, engine);
				
			}
		}
		
		try{
			configuration.addXML(mappings);
		}
		catch(MappingException me){
			throw HibernateException.toPageException(engine, me);
		}
		
		configuration
        
        // Database connection settings
        .setProperty("hibernate.connection.driver_class", ds.getClazz().getName())
    	.setProperty("hibernate.connection.url", ds.getDsnTranslated())
    	.setProperty("hibernate.connection.username", ds.getUsername())
    	.setProperty("hibernate.connection.password", ds.getPassword())
    	//.setProperty("hibernate.connection.release_mode", "after_transaction")
    	.setProperty("hibernate.transaction.flush_before_completion", "false")
    	.setProperty("hibernate.transaction.auto_close_session", "false")
    	
    	// JDBC connection pool (use the built-in)
    	//.setProperty("hibernate.connection.pool_size", "2")//MUST
    	
    	
    	// SQL dialect
    	.setProperty("hibernate.dialect", dialect)
    	// Enable Hibernate's current session context
    	.setProperty("hibernate.current_session_context_class", "thread")
    	
    	// Echo all executed SQL to stdout
    	.setProperty("hibernate.show_sql", Caster.toString(ormConf.logSQL()))
    	.setProperty("hibernate.format_sql", Caster.toString(ormConf.logSQL()))
    	// Specifies whether secondary caching should be enabled
    	.setProperty("hibernate.cache.use_second_level_cache", Caster.toString(ormConf.secondaryCacheEnabled()))
		// Drop and re-create the database schema on startup
    	.setProperty("hibernate.exposeTransactionAwareSessionFactory", "false")
		//.setProperty("hibernate.hbm2ddl.auto", "create")
		.setProperty("hibernate.default_entity_mode", "dynamic-map");
		
		if(!StringUtil.isEmpty(ormConf.getCatalog()))
			configuration.setProperty("hibernate.default_catalog", ormConf.getCatalog());
		if(!StringUtil.isEmpty(ormConf.getSchema()))
			configuration.setProperty("hibernate.default_schema",ormConf.getSchema());
		
		if(ormConf.secondaryCacheEnabled()){
			if(cacheConfig!=null && cacheConfig.isFile())
				configuration.setProperty("hibernate.cache.provider_configuration_file_resource_path",cacheConfig.getAbsolutePath());
			configuration.setProperty("hibernate.cache.provider_class", cacheProvider);
	    	
	    	configuration.setProperty("hibernate.cache.use_query_cache", "true");
		}
		
		/*
		<!ELEMENT tuplizer EMPTY> 
	    <!ATTLIST tuplizer entity-mode (pojo|dom4j|dynamic-map) #IMPLIED>   <!-- entity mode for which tuplizer is in effect --> 
	    <!ATTLIST tuplizer class CDATA #REQUIRED>                           <!-- the tuplizer class to use --> 
		*/
        
		schemaExport(engine,configuration,ormConf,dc);
		
		return configuration;
	}

	private static void schemaExport(HibernateORMEngine engine,Configuration configuration, ORMConfiguration ormConf,DatasourceConnection dc) throws PageException,ORMException, SQLException, IOException {
		if(ORMConfiguration.DBCREATE_NONE==ormConf.getDbCreate()) {
			return;
		}
		else if(ORMConfiguration.DBCREATE_DROP_CREATE==ormConf.getDbCreate()) {
			SchemaExport export = new SchemaExport(configuration);
			export.setHaltOnError(true);
	            
			export.execute(false,true,false,false);
            printError(engine,export.getExceptions(),false);
            executeSQLScript(ormConf,dc);
		}
		else if(ORMConfiguration.DBCREATE_UPDATE==ormConf.getDbCreate()) {
			SchemaUpdate update = new SchemaUpdate(configuration);
            update.setHaltOnError(true);
            update.execute(false, true);
            printError(engine,update.getExceptions(),false);
        }
	}

	private static void printError(HibernateORMEngine engine, List<Exception> exceptions,boolean throwException) throws PageException {
		if(ArrayUtil.isEmpty(exceptions)) return;
		Iterator<Exception> it = exceptions.iterator();
        if(!throwException || exceptions.size()>1){
			while(it.hasNext()) {
	        	ORMUtil.printError(it.next(), engine);
	        } 
        }
        if(!throwException) return;
        
        it = exceptions.iterator();
        while(it.hasNext()) {
        	throw HibernateException.toPageException(engine, it.next());
        } 
	}

	private static void executeSQLScript(ORMConfiguration ormConf,DatasourceConnection dc) throws SQLException, IOException {
        Resource sqlScript = ormConf.getSqlScript();
        if(sqlScript!=null && sqlScript.isFile()) {
            BufferedReader br = IOUtil.toBufferedReader(IOUtil.getReader(sqlScript,null));
            String line;
            StringBuilder sql=new StringBuilder();
            String str;
            Statement stat = dc.getConnection().createStatement();
        	try{
	        	while((line=br.readLine())!=null){
	            	line=line.trim();
	            	if(line.startsWith("//") || line.startsWith("--")) continue;
	            	if(line.endsWith(";")){
	            		sql.append(line.substring(0,line.length()-1));
	            		str=sql.toString().trim();
	            		if(str.length()>0)stat.execute(str);
	            		sql=new StringBuilder();
	            	}
	            	else {
	            		sql.append(line).append(" ");
	            	}	
	            }
	        	str=sql.toString().trim();
        		if(str.length()>0){
        			stat.execute(str);
	            }
        	}
    		finally {
    			SQLUtil.closeEL(stat);
    		}
        }
    }


	public static String createMappings(HibernateORMEngine engine, Map<String, CFCInfo> cfcs) {
		
		Set<String> done=new HashSet<String>();
		StringBuffer mappings=new StringBuffer();
		mappings.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		mappings.append(HIBERNATE_3_DOCTYPE_DEFINITION+"\n");
		mappings.append("<hibernate-mapping>\n");
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		Entry<String, CFCInfo> entry;
		while(it.hasNext()){
			entry = it.next();
			createMappings(engine,cfcs,entry.getKey(),entry.getValue(),done,mappings);
			
		}
		mappings.append("</hibernate-mapping>");
		return mappings.toString();
	}

	private static void createMappings(HibernateORMEngine engine, Map<String, CFCInfo> cfcs,String key, CFCInfo value,Set<String> done,StringBuffer mappings) {
		if(done.contains(key)) return;
		CFCInfo v;
		String ext = value.getCFC().getExtends();
		if(!StringUtil.isEmpty(ext)){
			try {
				Component base = engine.getEntityByCFCName(ext, false);
				ext=HibernateCaster.getEntityName(base);
			} catch (Throwable t) {}
			
			
			ext=HibernateORMEngine.id(railo.runtime.type.List.last(ext, '.').trim());
			if(!done.contains(ext)) {
				v = cfcs.get(ext);
				if(v!=null)createMappings(engine,cfcs, ext, v, done, mappings);
			}
		}
		
		mappings.append(value.getXML());
		done.add(key);
	}

	public static List<Component> loadComponents(PageContext pc,HibernateORMEngine engine, ORMConfiguration ormConf) throws PageException {
		ExtensionResourceFilter filter = new ExtensionResourceFilter(pc.getConfig().getCFCExtension(),true);
		List<Component> components=new ArrayList<Component>();
		loadComponents(pc,engine,components,ormConf.getCfcLocations(),filter,ormConf);
		return components;
	}
	
	private static void loadComponents(PageContext pc, HibernateORMEngine engine,List<Component> components,Resource[] reses,ExtensionResourceFilter filter,ORMConfiguration ormConf) throws PageException {
		Mapping[] mappings = createMappings(pc, reses);
		ApplicationContext ac=pc.getApplicationContext();
		Mapping[] existing = ac.getComponentMappings();
		if(existing==null) existing=new Mapping[0];
		try{
			Mapping[] tmp = new Mapping[existing.length+1];
			for(int i=1;i<tmp.length;i++){
				tmp[i]=existing[i-1];
			}
			ac.setComponentMappings(tmp);
			for(int i=0;i<reses.length;i++){
				if(reses[i]!=null && reses[i].isDirectory()){
					tmp[0] = mappings[i];
					ac.setComponentMappings(tmp);
					loadComponents(pc,engine,mappings[i],components,reses[i], filter,ormConf);
				}
			}
		}
		finally {
			ac.setComponentMappings(existing);
		}
	}
	
	private static void loadComponents(PageContext pc, HibernateORMEngine engine,Mapping cfclocation,List<Component> components,Resource res,ExtensionResourceFilter filter,ORMConfiguration ormConf) throws PageException {
		if(res==null) return;

		if(res.isDirectory()){
			Resource[] children = res.listResources(filter);
			
			// first load all files
			for(int i=0;i<children.length;i++){
				if(children[i].isFile())loadComponents(pc,engine,cfclocation,components,children[i], filter,ormConf);
			}
			
			// and then invoke subfiles
			for(int i=0;i<children.length;i++){
				if(children[i].isDirectory())loadComponents(pc,engine,cfclocation,components,children[i], filter,ormConf);
			}
		}
		else if(res.isFile()){
			if(!res.getName().equalsIgnoreCase(Constants.APP_CFC))	{
				try {
					
					// MUST still a bad solution
					PageSource ps = pc.toPageSource(res,null);
					if(ps==null || ps.getComponentName().indexOf("..")!=-1) {
						PageSource ps2=null;
						Resource root = cfclocation.getPhysical();
		                String path = ResourceUtil.getPathToChild(res, root);
		                if(!StringUtil.isEmpty(path,true)) {
		                	ps2=cfclocation.getPageSource(path);
		                }
		                if(ps2!=null)ps=ps2;
					}
					
					
					
					//Page p = ps.loadPage(pc.getConfig());
					String name=res.getName();
					name=name.substring(0,name.length()-4);
					ComponentAccess cfc = ComponentLoader.loadComponent(pc, null, ps, name, true,true);
					if(cfc.isPersistent()){
						components.add(cfc);
					}
				} 
				catch (PageException e) {
					if(!ormConf.skipCFCWithError())throw e;
					//e.printStackTrace();
				}
			}
		}
	}

	
	public static Mapping[] createMappings(PageContext pc,Resource[] resources) {
			
			MappingImpl[] mappings=new MappingImpl[resources.length];
			ConfigImpl config=(ConfigImpl) pc.getConfig();
			for(int i=0;i<mappings.length;i++) {
				mappings[i]=new MappingImpl(config,
						"/",
						resources[i].getAbsolutePath(),
						null,false,true,false,false,false,true,true
						);
			}
			return mappings;
		}
}
