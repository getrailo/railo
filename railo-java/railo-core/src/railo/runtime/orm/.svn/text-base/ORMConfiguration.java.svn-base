package railo.runtime.orm;

import org.w3c.dom.Element;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;

public class ORMConfiguration {
	public static final int DBCREATE_NONE=0;
	public static final int DBCREATE_UPDATE=1;
	public static final int DBCREATE_DROP_CREATE=2;
	
	public static final Collection.Key AUTO_GEN_MAP = KeyImpl.getInstance("autogenmap");
	public static final Collection.Key CATALOG = KeyImpl.getInstance("catalog");
	public static final Collection.Key CFC_LOCATION = KeyImpl.getInstance("cfclocation");
	public static final Collection.Key DB_CREATE = KeyImpl.getInstance("dbcreate");
	public static final Collection.Key DIALECT = KeyImpl.getInstance("dialect");
	public static final Collection.Key EVENT_HANDLING = KeyImpl.getInstance("eventHandling");
	public static final Collection.Key FLUSH_AT_REQUEST_END = KeyImpl.getInstance("flushatrequestend");
	public static final Collection.Key LOG_SQL = KeyImpl.getInstance("logSQL");
	public static final Collection.Key SAVE_MAPPING = KeyImpl.getInstance("savemapping");
	public static final Collection.Key SCHEMA = KeyImpl.getInstance("schema");
	public static final Collection.Key SECONDARY_CACHE_ENABLED = KeyImpl.getInstance("secondarycacheenabled");
	public static final Collection.Key SQL_SCRIPT = KeyImpl.getInstance("sqlscript");
	public static final Collection.Key USE_DB_FOR_MAPPING = KeyImpl.getInstance("useDBForMapping");
	public static final Collection.Key CACHE_CONFIG = KeyImpl.getInstance("cacheconfig");
	public static final Collection.Key CACHE_PROVIDER = KeyImpl.getInstance("cacheProvider");
	public static final Collection.Key ORM_CONFIG = KeyImpl.getInstance("ormConfig");
	
	
	private boolean autogenmap=true;
	private String catalog;
	private Resource cfcLocation;
	private int dbCreate=DBCREATE_NONE;
	private String dialect;
	private boolean eventHandling;
	private boolean flushAtRequestEnd=true;
	private boolean logSQL;
	private boolean saveMapping;
	private String schema;
	private boolean secondaryCacheEnabled;
	private Resource sqlScript;
	private boolean useDBForMapping=true;
	private Resource cacheConfig;
	private String cacheProvider;
	private Resource ormConfig;

	private ORMConfiguration(){
		autogenmap=true;
		dbCreate=DBCREATE_NONE;
		flushAtRequestEnd=true;
		useDBForMapping=true;		
	}
	
	
	
	


	public static ORMConfiguration load(Config config, Element el, Resource defaultCFCLocation,ORMConfiguration defaultConfig) {
		return _load(config, new _GetElement(el),defaultCFCLocation,defaultConfig);
	}
	
	public static ORMConfiguration load(Config config,Struct settings, Resource defaultCFCLocation,ORMConfiguration defaultConfig) {
		return _load(config, new _GetStruct(settings),defaultCFCLocation,defaultConfig);
	}

	private static ORMConfiguration _load(Config config,_Get settings, Resource defaultCFCLocation,ORMConfiguration dc) {
		if(dc==null)dc=new ORMConfiguration();
		ORMConfiguration c = dc.duplicate();
		
		// autogenmap
		c.autogenmap=Caster.toBooleanValue(settings.get(AUTO_GEN_MAP,dc.autogenmap()),dc.autogenmap());
		
		// catalog
		c.catalog=StringUtil.trim(Caster.toString(settings.get(CATALOG,dc.getCatalog()),dc.getCatalog()),dc.getCatalog());
		
		// cfclocation
		Object obj = settings.get(CFC_LOCATION,null);
		if(obj!=null){
			try {
				c.cfcLocation=Caster.toResource(config, obj, true);
			} catch (ExpressionException e) {
				//print.printST(e);
			}
		}
		else {
			c.cfcLocation=defaultCFCLocation;
			//print.out("www:"+pc.getCurrentPageSource());
			//c.cfcLocation=pc.getCurrentPageSource().getPhyscalFile().getParentResource();
		}
		
		// dbcreate
		obj = settings.get(DB_CREATE,null);
		if(obj!=null){
			String str = Caster.toString(obj,"").trim().toLowerCase();
			c.dbCreate=dbCreateAsInt(str);
		}
		
		// dialect
		c.dialect = StringUtil.trim(Caster.toString(settings.get(DIALECT,dc.getDialect()),dc.getDialect()),dc.getDialect());
		
		// eventHandling
		c.eventHandling=Caster.toBooleanValue(settings.get(EVENT_HANDLING,dc.eventHandling()),dc.eventHandling());
		
		// flushatrequestend
		c.flushAtRequestEnd=Caster.toBooleanValue(settings.get(FLUSH_AT_REQUEST_END,dc.flushAtRequestEnd()),dc.flushAtRequestEnd());
		
		// logSQL
		c.logSQL=Caster.toBooleanValue(settings.get(LOG_SQL,dc.logSQL()),dc.logSQL());
		
		// savemapping
		c.saveMapping=Caster.toBooleanValue(settings.get(SAVE_MAPPING,dc.saveMapping()),dc.saveMapping());
		
		// schema
		c.schema=StringUtil.trim(Caster.toString(settings.get(SCHEMA,dc.getSchema()),dc.getSchema()),dc.getSchema());
		
		// secondarycacheenabled
		c.secondaryCacheEnabled=Caster.toBooleanValue(settings.get(SECONDARY_CACHE_ENABLED,dc.secondaryCacheEnabled()),dc.secondaryCacheEnabled());
		
		// sqlscript
		obj = settings.get(SQL_SCRIPT,null);
		if(obj!=null){
			try {
				c.sqlScript=Caster.toResource(config, obj, true);
			} catch (ExpressionException e) {
				//print.printST(e);
			}
		}
		
		// useDBForMapping
		c.useDBForMapping=Caster.toBooleanValue(settings.get(USE_DB_FOR_MAPPING,dc.useDBForMapping()),dc.useDBForMapping());
		
		// cacheconfig
		obj = settings.get(CACHE_CONFIG,null);
		if(obj!=null){
			try {
				c.cacheConfig=Caster.toResource(config, obj, true);
			} catch (ExpressionException e) {
				//print.printST(e);
			}
		}
		
		// cacheprovider
		c.cacheProvider=StringUtil.trim(Caster.toString(settings.get(CACHE_PROVIDER,dc.getCacheProvider()),dc.getCacheProvider()),dc.getCacheProvider());
		
		// ormconfig
		obj = settings.get(ORM_CONFIG,null);
		if(obj!=null){
			try {
				c.ormConfig=Caster.toResource(config, obj, true);
			} catch (ExpressionException e) {
				//print.printST(e);
			}
		}
		
		return c;
	}	

	private ORMConfiguration duplicate() {
		
		ORMConfiguration other = new ORMConfiguration();
		
		
		
		other.autogenmap=autogenmap;
		other.catalog=catalog;
		other.cfcLocation=cfcLocation;
		other.dbCreate=dbCreate;
		other.dialect=dialect;
		other.eventHandling=eventHandling;
		other.flushAtRequestEnd=flushAtRequestEnd;
		other.logSQL=logSQL;
		other.saveMapping=saveMapping;
		other.schema=schema;
		other.secondaryCacheEnabled=secondaryCacheEnabled;
		other.sqlScript=sqlScript;
		other.useDBForMapping=useDBForMapping;
		other.cacheConfig=cacheConfig;
		other.cacheProvider=cacheProvider;
		other.ormConfig=ormConfig;
		
		return other;
	}






	/**
	 * @return the autogenmap
	 */
	public boolean autogenmap() {
		return autogenmap;
	}

	/**
	 * @return the catalog
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @return the cfcLocation
	 */
	public Resource getCfcLocation() {
		return cfcLocation;
	}

	/**
	 * @return the dbCreate
	 */
	public int getDbCreate() {
		return dbCreate;
	}

	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}

	/**
	 * @return the eventHandling
	 */
	public boolean eventHandling() {
		return eventHandling;
	}

	/**
	 * @return the flushAtRequestEnd
	 */
	public boolean flushAtRequestEnd() {
		return flushAtRequestEnd;
	}

	/**
	 * @return the logSQL
	 */
	public boolean logSQL() {
		return logSQL;
	}

	/**
	 * @return the saveMapping
	 */
	public boolean saveMapping() {
		return saveMapping;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @return the secondaryCacheEnabled
	 */
	public boolean secondaryCacheEnabled() {
		return secondaryCacheEnabled;
	}

	/**
	 * @return the sqlScript
	 */
	public Resource getSqlScript() {
		return sqlScript;
	}

	/**
	 * @return the useDBForMapping
	 */
	public boolean useDBForMapping() {
		return useDBForMapping;
	}

	/**
	 * @return the cacheConfig
	 */
	public Resource getCacheConfig() {
		return cacheConfig;
	}

	/**
	 * @return the cacheProvider
	 */
	public String getCacheProvider() {
		return cacheProvider;
	}

	/**
	 * @return the ormConfig
	 */
	public Resource getOrmConfig() {
		return ormConfig;
	}






	public Object toStruct() {
		
		Struct sct=new StructImpl();
		sct.setEL(AUTO_GEN_MAP,this.autogenmap());
		sct.setEL(CATALOG,StringUtil.toStringEmptyIfNull(getCatalog()));
		sct.setEL(CFC_LOCATION,getAbsolutePath(getCfcLocation()));
		sct.setEL(DB_CREATE,dbCreateAsString(getDbCreate()));
		sct.setEL(DIALECT,StringUtil.toStringEmptyIfNull(getDialect()));
		sct.setEL(EVENT_HANDLING,eventHandling());
		sct.setEL(FLUSH_AT_REQUEST_END,flushAtRequestEnd());
		sct.setEL(LOG_SQL,logSQL());
		sct.setEL(SAVE_MAPPING,saveMapping());
		sct.setEL(SCHEMA,StringUtil.toStringEmptyIfNull(getSchema()));
		sct.setEL(SECONDARY_CACHE_ENABLED,secondaryCacheEnabled());
		sct.setEL(SQL_SCRIPT,StringUtil.toStringEmptyIfNull(getSqlScript()));
		sct.setEL(USE_DB_FOR_MAPPING,useDBForMapping());
		sct.setEL(CACHE_CONFIG,getAbsolutePath(getCacheConfig()));
		sct.setEL(CACHE_PROVIDER,StringUtil.toStringEmptyIfNull(getCacheProvider()));
		sct.setEL(ORM_CONFIG,getAbsolutePath(getOrmConfig()));
		
		
		return sct;
	}


	private static String getAbsolutePath(Resource res) {
		if(res==null )return "";
		return res.getAbsolutePath();
	}






	public static int dbCreateAsInt(String dbCreate) {
		if(dbCreate==null)dbCreate="";
		else dbCreate=dbCreate.trim().toLowerCase();
		
		if("update".equals(dbCreate))return DBCREATE_UPDATE;
		if("dropcreate".equals(dbCreate))return DBCREATE_DROP_CREATE;
		if("drop-create".equals(dbCreate))return DBCREATE_DROP_CREATE;
		
		return DBCREATE_NONE;
	}
	



	public static String dbCreateAsString(int dbCreate) {
		
		switch(dbCreate){
		case DBCREATE_DROP_CREATE: return "dropcreate";
		case DBCREATE_UPDATE: return "update";
		}
		
		return "none";
	}
	
	
	
}

interface _Get {
	public Object get(Collection.Key name,Object defaultValue);
}

class _GetStruct implements _Get {
	
	private Struct sct;
	public _GetStruct(Struct sct){
		this.sct=sct;
	}
	
	public Object get(Collection.Key name,Object defaultValue){
		return sct.get(name,defaultValue);
	}
}

class _GetElement implements _Get {
	
	private Element el;
	public _GetElement(Element el){
		this.el=el;
	}
	public Object get(Collection.Key name,Object defaultValue){
		String value = el.getAttribute(name.getString());
		if(value==null)value = el.getAttribute(StringUtil.camelToHypenNotation(name.getString()));
		if(value==null)value = el.getAttribute(name.getLowerString());
		if(value==null) return defaultValue;
		return value;
	}
}

