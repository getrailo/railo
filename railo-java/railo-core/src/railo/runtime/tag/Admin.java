package railo.runtime.tag;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.collections.HashTable;
import railo.commons.io.CompressUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogResource;
import railo.commons.io.log.LogUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.OrResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Mapping;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.cfx.customtag.CFXTagClass;
import railo.runtime.cfx.customtag.JavaCFXTagClass;
import railo.runtime.config.AdminSync;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.config.RemoteClient;
import railo.runtime.config.RemoteClientImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.db.DataSourceManager;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.DumpWriter;
import railo.runtime.engine.Surveillance;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.extension.Extension;
import railo.runtime.extension.ExtensionImpl;
import railo.runtime.extension.ExtensionProvider;
import railo.runtime.functions.other.GetAuthUser;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.net.mail.SMTPException;
import railo.runtime.net.mail.SMTPVerifier;
import railo.runtime.net.mail.Server;
import railo.runtime.net.mail.ServerImpl;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Constants;
import railo.runtime.op.date.DateCaster;
import railo.runtime.reflection.Reflector;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTask;
import railo.runtime.spooler.remote.RemoteClientTask;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.ClusterEntryImpl;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.util.ApplicationContextImpl;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;

/**
 * 
 */
public final class Admin extends TagImpl implements DynamicAttributes {
    
    private static final short TYPE_WEB=0;
    private static final short TYPE_SERVER=1;

    private static final short ACCESS_FREE=0;
    private static final short ACCESS_NOT_WHEN_WEB=1;
    private static final short ACCESS_NOT_WHEN_SERVER=2;
    private static final short ACCESS_NEVER=3;

    private static final short ACCESS_READ=10;
    private static final short ACCESS_WRITE=11;
    private static final short CHECK_PW=12;

	private static final Collection.Key TYPE = KeyImpl.getInstance("type");
	private static final Collection.Key PASSWORD = KeyImpl.getInstance("password");
	//private static final Collection.Key ATTRIBUTE_COLLECTION = KeyImpl.getInstance("attributeCollection");
	private static final Collection.Key RETURN_VARIABLE = KeyImpl.getInstance("returnvariable");
	private static final Collection.Key CALLER_ID = KeyImpl.getInstance("callerId");
	private static final Collection.Key PROVIDED_CALLER_IDS = KeyImpl.getInstance("providedCallerIds");
	
	//private static final String USAGE_SYNC = "synchronisation";
	//private static final String USAGE_CLUSTER = "cluster";
	private static final Collection.Key ACTION = KeyImpl.getInstance("action");
	private static final Collection.Key KEY = KeyImpl.getInstance("key");
	private static final Collection.Key VALUE = KeyImpl.getInstance("value");
	private static final Collection.Key TIME = KeyImpl.getInstance("time");
    
    
    private Struct attributes=new StructImpl();
    private String action=null;
    private short type;
    private String password;
    private ConfigWebAdmin admin;
    private ConfigImpl config;
    
    private ResourceFilter filter=
        new OrResourceFilter(new ResourceFilter[]{
                new DirectoryResourceFilter(),
                new ExtensionResourceFilter("cfm"),
                new ExtensionResourceFilter("cfc"),
                new ExtensionResourceFilter("cfml")
        });
	private AdminSync adminSync;
	
    
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        attributes.clear();
    }
    
    /**
     * @see railo.runtime.ext.tag.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void setDynamicAttribute(String uri, String localName, Object value) {
        attributes.setEL(KeyImpl.init(localName),value);
    }
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws PageException {
    	//adminSync = pageContext.getAdminSync();
    	
        // Action
        Object objAction=attributes.get(ACTION);
        if(objAction==null)throw new ApplicationException("missing attrbute action for tag admin");
        action=StringUtil.toLowerCase(Caster.toString(objAction)).trim();
        
        // Generals
        if(action.equals("getlocales")) {
            doGetLocales();
            return SKIP_BODY;
        }
        if(action.equals("gettimezones")) {
            doGetTimeZones();
            return SKIP_BODY;
        }
        if(action.equals("printdebug")) {
            doPrintDebug();
            return SKIP_BODY;
        }
        if(action.equals("getdebugdata")) {
            doGetDebugData();
            return SKIP_BODY;
        }
        
        
        
        
        // Type
        type=toType(getString("admin",action,"type"),true);
        
        // has Password
        if(action.equals("haspassword")) {
           //long start=System.currentTimeMillis();
            boolean hasPassword=type==TYPE_WEB?
                    pageContext.getConfig().hasPassword():
                    pageContext.getConfig().hasServerPassword();
                    
            pageContext.setVariable(getString("admin",action,"returnVariable"),Caster.toBoolean(hasPassword));
            return SKIP_BODY;
        }
        
        // update Password
        else if(action.equals("updatepassword")) {
            try {
                ConfigWebAdmin.setPassword((ConfigImpl)pageContext.getConfig(),type!=TYPE_WEB,
                        getString("oldPassword",null),getString("admin",action,"newPassword"));
            } 
            catch (Exception e) {
                throw Caster.toPageException(e);
            }
            return SKIP_BODY;
        }
        

        try {
            // Password
            password = getString("password","");
            // Config
            config=(ConfigImpl)pageContext.getConfig();
            if(type==TYPE_SERVER)
                config=(ConfigImpl)config.getConfigServer(password);
            
            adminSync = config.getAdminSync();
        	
            
            
            //config=(type==TYPE_WEB)?((ConfigImpl)pageContext.getConfig()):((ConfigImpl)pageContext.getConfig()).getConfigServer(password);
            // Admin
            admin = ConfigWebAdmin.newInstance(config,password);
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
        //int version=config.getSerialNumber().getVersion();
        /*if(type==TYPE_SERVER && version!=SerialNumber.VERSION_ENTERPRISE && version!=SerialNumber.VERSION_DEVELOP)
            throw new SecurityException("can't access server settings with "+config.getSerialNumber().getStringVersion()+
                    " version of Railo");
                    
        */
               
        try {
			_doStartTag();
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
        
        return Tag.SKIP_BODY;
    }

    private short toType(String strType, boolean throwError) throws ApplicationException {
    	strType=StringUtil.toLowerCase(strType).trim();
    	if("web".equals(strType))return TYPE_WEB;
        else if("server".equals(strType))return TYPE_SERVER;
        if(throwError)
        	throw new ApplicationException("invalid value for attribute type ["+strType+"] of tag admin","valid values are web, server");
        return TYPE_WEB;
	}

	private void doTagSchedule() throws PageException {
		Schedule schedule=new Schedule();
		try {
			
			
			schedule.setPageContext(pageContext);
			schedule.setAction(getString("admin",action,"scheduleAction"));
			schedule.setTask(getString("task",null));
			schedule.setHidden(getBool("hidden",false));
			schedule.setReadonly(getBool("readonly",false));
			schedule.setOperation(getString("operation",null));
			schedule.setFile(getString("file",null));
			schedule.setPath(getString("path",null));
			schedule.setStartdate(getObject("startDate",null));
			schedule.setStarttime(getObject("startTime",null));
			schedule.setUrl(getString("url",null));
			schedule.setPublish(getBool("publish",false));
			schedule.setEnddate(getObject("endDate",null));
			schedule.setEndtime(getObject("endTime",null));
			schedule.setInterval(getString("interval",null));
			schedule.setRequesttimeout(new Double(getDouble("requestTimeOut",-1)));
			schedule.setUsername(getString("username",null));
			schedule.setPassword(getString("schedulePassword",null));
			schedule.setProxyserver(getString("proxyServer",null));
			schedule.setProxyuser(getString("proxyuser",null));
			schedule.setProxypassword(getString("proxyPassword",null));
			schedule.setResolveurl(getBool("resolveURL",false));
			schedule.setPort(new Double(getDouble("port",-1)));
			schedule.setProxyport(new Double(getDouble("proxyPort",80)));
			schedule.setReturnvariable(getString("returnvariable","cfschedule"));
			
			schedule.doStartTag();    
		} 
		finally {
		    schedule.release();
		    adminSync.broadcast(attributes, config);
		    adminSync.broadcast(attributes, config);
		}
	}
    
    /*private void doTagSearch() throws PageException {
		Search search=new Search();
		try {
			
			search.setPageContext(pageContext);
			
			search.setName(getString("admin",action,"name"));
			search.setCollection(getString("admin",action,"collection"));
			search.setType(getString("type",null));
			search.setMaxrows(getDouble("maxRows",-1));
			search.setStartrow(getDouble("startRow",1));
			search.setCategory(getString("category",null));
			search.setCategorytree(getString("categoryTree",null));
			search.setStatus(getString("status",null));
			search.setSuggestions(getString("suggestions",null));
			
			search.doStartTag();    
		} 
		finally {
		    search.release();
		}
	}*/
    
    private void doTagIndex() throws PageException {
		Index index=new Index();
		try {
			
			index.setPageContext(pageContext);
			
			index.setCollection(getString("admin",action,"collection"));
			index.setAction(getString("admin",action,"indexAction"));
			index.setType(getString("indexType",null));
			index.setTitle(getString("title",null));
			index.setKey(getString("key",null));
			index.setBody(getString("body",null));
			index.setCustom1(getString("custom1",null));
			index.setCustom2(getString("custom2",null));
			index.setCustom3(getString("custom3",null));
			index.setCustom4(getString("custom4",null));
			index.setUrlpath(getString("URLpath",null));
			index.setExtensions(getString("extensions",null));
			index.setQuery(getString("query",null));
			index.setRecurse(getBool("recurse",false));
			index.setLanguage(getString("language",null));
			index.setCategory(getString("category",null));
			index.setCategorytree(getString("categoryTree",null));
			index.setStatus(getString("status",null));
			index.setPrefix(getString("prefix",null));
			
			index.doStartTag();    
		} 
		finally {
		    index.release();
		    adminSync.broadcast(attributes, config);
		}
	}
    

    private void doTagCollection() throws PageException {
    	railo.runtime.tag.Collection coll=new railo.runtime.tag.Collection();
		try {
			
			coll.setPageContext(pageContext);
			
			//coll.setCollection(getString("admin",action,"collection"));
			coll.setAction(getString("collectionAction",null));
			coll.setCollection(getString("collection",null));
			coll.setPath(getString("path",null));
			coll.setLanguage(getString("language",null));
			coll.setName(getString("name",null));
			
			
			coll.doStartTag();    
		} 
		finally {
		    coll.release();
		    adminSync.broadcast(attributes, config);
		}
	}
    
	/**
     * @throws PageException
     * 
     */
    private void _doStartTag() throws PageException,IOException {
        

    	// getToken
    	if(action.equals("gettoken")) {
            doGetToken();
            return;
        }
        

        // schedule
        if(action.equals("schedule")) {
        	doTagSchedule();
            return;
        }
        // search
        if(action.equals("collection")) {
        	doTagCollection();
            return;
        }
        // index
        if(action.equals("index")) {
        	doTagIndex();
            return;
        }
        // cluster
        if(action.equals("setcluster")) {
        	doSetCluster();
            return;
        }
        if(action.equals("getcluster")) {
        	doGetCluster();
            return;
        }
        
        
    	
    	if(check("connect",ACCESS_FREE) && check2(CHECK_PW)) {/*do nothing more*/}
    	else if(check("surveillance",           ACCESS_FREE) && check2(ACCESS_READ  )) doSurveillance();
    	else if(check("getRegional",            ACCESS_FREE) && check2(ACCESS_READ  )) doGetRegional();
        else if(check("getApplicationListener", ACCESS_FREE) && check2(ACCESS_READ  )) doGetApplicationListener();
        else if(check("getProxy",            	ACCESS_FREE) && check2(ACCESS_READ  )) doGetProxy();
        else if(check("getCharset",            	ACCESS_FREE) && check2(ACCESS_READ  )) doGetCharset();
        else if(check("getComponent",           ACCESS_FREE) && check2(ACCESS_READ  )) doGetComponent();
        else if(check("getScope",               ACCESS_FREE) && check2(ACCESS_READ  )) doGetScope();
        else if(check("getApplicationSetting",	ACCESS_FREE) && check2(ACCESS_READ  )) doGetApplicationSetting();
        else if(check("getOutputSetting",		ACCESS_FREE) && check2(ACCESS_READ  )) doGetOutputSetting();
        else if(check("getDatasourceSetting",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetDatasourceSetting();
        else if(check("getCustomTagSetting",	ACCESS_FREE) && check2(ACCESS_READ  )) doGetCustomTagSetting();
        else if(check("getDatasource",          ACCESS_FREE) && check2(ACCESS_READ  )) doGetDatasource();
        else if(check("getDatasources",         ACCESS_FREE) && check2(ACCESS_READ  )) doGetDatasources();
        else if(check("getRemoteClients",       ACCESS_FREE) && check2(ACCESS_READ  )) doGetRemoteClients();
        else if(check("getRemoteClient",       	ACCESS_FREE) && check2(ACCESS_READ  )) doGetRemoteClient();
        else if(check("getRemoteClientUsage",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetRemoteClientUsage();
        else if(check("getSpoolerTasks",   		ACCESS_FREE) && check2(ACCESS_READ  )) doGetSpoolerTasks();
        else if(check("getPerformanceSettings", ACCESS_FREE) && check2(ACCESS_READ  )) doGetPerformanceSettings();
        else if(check("updatePerformanceSettings",ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdatePerformanceSettings();
        
    	
    	
    	// alias for getSpoolerTasks
        else if(check("getRemoteClientTasks",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetSpoolerTasks();
        else if(check("getDatasourceDriverList",ACCESS_FREE) && check2(ACCESS_READ  )) doGetDatasourceDriverList();
        else if(check("getDebuggingList",		ACCESS_FREE) && check2(ACCESS_READ  )) doGetDebuggingList();
        else if(check("getPluginDirectory",		ACCESS_FREE) && check2(ACCESS_READ  )) doGetPluginDirectory();
        else if(check("getPlugins",		ACCESS_FREE) && check2(ACCESS_READ  )) doGetPlugins();
        else if(check("updatePlugin",		ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdatePlugin();
        else if(check("removePlugin",		ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemovePlugin();
        
        else if(check("getJars",                ACCESS_FREE) && check2(ACCESS_READ  )) doGetJars();
        else if(check("getFlds",                ACCESS_FREE) && check2(ACCESS_READ  )) doGetFLDs();
        else if(check("getTlds",                ACCESS_FREE) && check2(ACCESS_READ  )) doGetTLDs();
        else if(check("getMailSetting",         ACCESS_FREE) && check2(ACCESS_READ  )) doGetMailSetting();
        else if(check("getMailServers",         ACCESS_FREE) && check2(ACCESS_READ  )) doGetMailServers();
        else if(check("getMapping",             ACCESS_FREE) && check2(ACCESS_READ  )) doGetMapping();
        else if(check("getMappings",            ACCESS_FREE) && check2(ACCESS_READ  )) doGetMappings();
        else if(check("getExtensions",			ACCESS_FREE) && check2(ACCESS_READ  )) doGetExtensions();
        else if(check("getExtensionProviders",	ACCESS_FREE) && check2(ACCESS_READ  )) doGetExtensionProviders();
        else if(check("getExtensionInfo",		ACCESS_FREE) && check2(ACCESS_READ  )) doGetExtensionInfo();
        
        else if(check("getCustomTagMappings",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetCustomTagMappings();
        else if(check("getCfxTags",             ACCESS_FREE) && check2(ACCESS_READ  )) doGetCFXTags();
        else if(check("getJavaCfxTags",         ACCESS_FREE) && check2(ACCESS_READ  )) doGetJavaCFXTags();
        else if(check("getDebug",               ACCESS_FREE) && check2(ACCESS_READ  )) doGetDebug();
        else if(check("getError",               ACCESS_FREE) && check2(ACCESS_READ  )) doGetError();
        else if(check("verifyremoteclient",     ACCESS_FREE) && check2(ACCESS_READ  )) doVerifyRemoteClient();
        else if(check("verifyDatasource",       ACCESS_FREE) && check2(ACCESS_READ  )) doVerifyDatasource();
        else if(check("verifyMailServer",       ACCESS_FREE) && check2(ACCESS_READ  )) doVerifyMailServer();
        else if(check("verifyExtensionProvider",ACCESS_FREE) && check2(ACCESS_READ  )) doVerifyExtensionProvider();
        else if(check("verifyJavaCFX",			ACCESS_FREE) && check2(ACCESS_READ  )) doVerifyJavaCFX();
        
        else if(check("resetId",				ACCESS_FREE) && check2(ACCESS_WRITE  )) doResetId();
        else if(check("updateJar",         		ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateJar();
        else if(check("updateTLD",         		ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateTLD();
        else if(check("updateFLD",         		ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateFLD();
        else if(check("updateregional",         ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateRegional();
        else if(check("updateApplicationListener",ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateApplicationListener();
        else if(check("updateproxy",         	ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateProxy();
        else if(check("updateCharset",         ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateCharset();
        else if(check("updatecomponent",        ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateComponent();
        else if(check("updatescope",            ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateScope();
        else if(check("updateApplicationSetting",ACCESS_FREE) && check2(ACCESS_WRITE  ))doUpdateApplicationSettings();
        else if(check("updateOutputSetting",	ACCESS_FREE) && check2(ACCESS_WRITE  ))doUpdateOutputSettings();
        else if(check("updatepsq",              ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdatePSQ();
        else if(check("updatedatasource",       ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateDatasource();
        else if(check("updateremoteclient",     ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateRemoteClient();
        else if(check("updateRemoteClientUsage",ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateRemoteClientUsage();
    	else if(check("updatemailsetting",      ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateMailSetting();
        else if(check("updatemailserver",       ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateMailServer();
        else if(check("updatemapping",          ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateMapping();
        else if(check("updatecustomtag",        ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateCustomTag();
        else if(check("updatejavacfx",          ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateJavaCFX();
        else if(check("updatedebug",            ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateDebug();
        else if(check("updateerror",            ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateError();
        else if(check("updateCustomTagSetting",	ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateCustomTagSetting();
        else if(check("updateExtension",		ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateExtension();
        else if(check("updateExtensionProvider",ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateExtensionProvider();
        else if(check("updateExtensionInfo",	ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateExtensionInfo();
        
    	
        //else if(check("removeproxy",       		ACCESS_NOT_WHEN_SERVER  )) doRemoveProxy();
        else if(check("removejar",       		ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveJar();
        else if(check("removeTLD",       		ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveTLD();
        else if(check("removeFLD",       		ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveFLD();
        else if(check("removedatasource",       ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveDatasource();
        else if(check("removeremoteclient",     ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveRemoteClient();
        else if(check("removeRemoteClientUsage",ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveRemoteClientUsage();
    	else if(check("removeSpoolerTask", 		ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveSpoolerTask();
        // alias for executeSpoolerTask
        else if(check("removeRemoteClientTask", ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveSpoolerTask();
        else if(check("executeSpoolerTask",		ACCESS_FREE) && check2(ACCESS_WRITE  )) doExecuteSpoolerTask();
        // alias for executeSpoolerTask
        else if(check("executeRemoteClientTask",ACCESS_FREE) && check2(ACCESS_WRITE  )) doExecuteSpoolerTask();
        else if(check("removemailserver",       ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveMailServer();
        else if(check("removemapping",          ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveMapping();
        else if(check("removecustomtag",        ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveCustomTag();
        else if(check("removecfx",              ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveCFX();
        else if(check("removeExtension",        ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveExtension();
        else if(check("removeExtensionProvider",ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveExtensionProvider();
        else if(check("removeDefaultPassword",  ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveDefaultPassword();
        
        else if(check("storageGet",             ACCESS_FREE) && check2(ACCESS_READ  )) doStorageGet();
        else if(check("storageSet",             ACCESS_FREE) && check2(ACCESS_WRITE  )) doStorageSet();
        
        else if(check("getdefaultpassword",     ACCESS_FREE) && check2(ACCESS_READ            )) doGetDefaultPassword();
        else if(check("getContextes",           ACCESS_FREE) && check2(ACCESS_READ            )) doGetContextes();
        else if(check("updatedefaultpassword",  ACCESS_FREE) && check2(ACCESS_WRITE            )) doUpdateDefaultPassword();
        else if(check("hasindividualsecurity",  ACCESS_FREE) && check2(ACCESS_READ            )) doHasIndividualSecurity();
        else if(check("resetpassword",          ACCESS_FREE) && check2(ACCESS_WRITE            )) doResetPassword();
        
        else if(check("createsecuritymanager",  ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE            )) doCreateSecurityManager();
        else if(check("getsecuritymanager",     ACCESS_NOT_WHEN_WEB) && check2(ACCESS_READ            )) doGetSecurityManager();
        else if(check("removesecuritymanager",  ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE            )) doRemoveSecurityManager();
        else if(check("getdefaultsecuritymanager",ACCESS_NOT_WHEN_WEB) && check2(ACCESS_READ          )) doGetDefaultSecurityManager();
        else if(check("updatesecuritymanager",  ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE            )) doUpdateSecurityManager();
        else if(check("updatedefaultsecuritymanager",ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE       )) doUpdateDefaultSecurityManager();
        else if(check("compileMapping",         ACCESS_FREE) && check2(ACCESS_WRITE             )) doCompileMapping();
        else if(check("createArchive",         ACCESS_FREE) && check2(ACCESS_WRITE             )) doCreateArchive();
        else if(check("reload",  		        ACCESS_FREE) && check2(ACCESS_WRITE            )) doReload();
    	

        else if(check("getResourceProviders",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetResourceProviders();
        else if(check("updateResourceProvider", ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateResourceProvider();
        else if(check("updateDefaultResourceProvider", ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateDefaultResourceProvider();
        else if(check("removeResourceProvider", ACCESS_FREE) && check2(ACCESS_WRITE  )) doRemoveResourceProvider();
        
        else if(check("getClusterClass",   		ACCESS_FREE) && check2(ACCESS_READ  )) doGetClusterClass();
        else if(check("updateClusterClass", 	ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateClusterClass();
        
        else if(check("getAdminSyncClass",   		ACCESS_FREE) && check2(ACCESS_READ  )) doGetAdminSyncClass();
        else if(check("updateAdminSyncClass", 	ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateAdminSyncClass();
        
        else if(check("getVideoExecuterClass",   ACCESS_FREE) && check2(ACCESS_READ  )) doGetVideoExecuterClass();
        else if(check("updateVideoExecuterClass",ACCESS_FREE) && check2(ACCESS_WRITE  )) doUpdateVideoExecuterClass();
        
        else if(check("restart",                ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doRestart();
        else if(check("runUpdate",              ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doRunUpdate();
        else if(check("removeUpdate",           ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doRemoveUpdate();
        else if(check("getUpdate",              ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doGetUpdate();
        else if(check("updateupdate",           ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doUpdateUpdate();
        else if(check("getSerial",              ACCESS_FREE) && check2(ACCESS_READ     )) doGetSerial();
        else if(check("updateSerial",           ACCESS_NOT_WHEN_WEB) && check2(ACCESS_WRITE     )) doUpdateSerial();
       
        else if(check("securitymanager",        ACCESS_FREE) && check2(ACCESS_READ             )) doSecurityManager();
        
        
    	
        else throw new ApplicationException("invalid action ["+action+"] for tag admin");
            
    }

	

	private boolean check2(short accessRW) throws SecurityException {
    	if(accessRW==ACCESS_READ) ConfigWebUtil.checkGeneralReadAccess(config,password);
		else if(accessRW==ACCESS_WRITE) ConfigWebUtil.checkGeneralWriteAccess(config,password);
		else if(accessRW==CHECK_PW) {
			ConfigWebUtil.checkGeneralReadAccess(config,password);
			ConfigWebUtil.checkPassword(config,password);
		}
    	return true;
    }
   	private boolean check(String action, short access) throws ApplicationException {
    	if( this.action.equalsIgnoreCase(action)) {
            if(access==ACCESS_FREE) {
            }
            else if(access==ACCESS_NOT_WHEN_SERVER) {
                throwNoAccessWhenServer();
            }
            
            else if(access==ACCESS_NOT_WHEN_WEB) {
                throwNoAccessWhenWeb();
            }
            else if(access==ACCESS_NEVER) {
                throwNoAccessWhenServer();
                throwNoAccessWhenServer();
            }
            return true;
        }
        return false;
    }
    

    private void doRunUpdate() throws PageException {
        admin.runUpdate();
        adminSync.broadcast(attributes, config);
    }
    
    private void doRemoveUpdate() throws PageException {
        admin.removeUpdate();
        adminSync.broadcast(attributes, config);
    }
    
    private void doRestart() throws PageException {
        admin.restart();
        adminSync.broadcast(attributes, config);
    }
    
    private void doCreateArchive() throws PageException {
    	String virtual = getString("admin",action,"virtual").toLowerCase();
    	String strFile = getString("admin",action,"file");
    	Resource file = ResourceUtil.toResourceNotExisting(pageContext, strFile);
    	
    	boolean secure = getBool("secure", false);
    	
    	// compile
    	Mapping mapping = doCompileMapping(virtual, true);
        
    	// class files 
    	if(mapping==null)throw new ApplicationException("there is no mapping for ["+virtual+"]");
    	if(!mapping.hasPhysical())throw new ApplicationException("mapping ["+virtual+"] has no physical directory");
    	
    	Resource classRoot = mapping.getClassRootDirectory();
    	
    	try {
    		if(file.exists())file.delete();
    		if(!file.exists())file.createFile(true);
        	//Resource ra = ResourceUtil.toResourceNotExisting(pageContext, "zip://"+file.getPath());
        	//ResourceUtil.copyRecursive(classRoot, ra);
    		filter=new ExtensionResourceFilter(new String[]{"class","cfm","cfml","cfc"},true,true);
			
		// source files
			if(!secure) {
				Resource physical = mapping.getPhysical();
				// ResourceUtil.copyRecursive(physical, ra,new ExtensionResourceFilter(new String[]{"cfm","cfml","cfc"},true));
				CompressUtil.compressZip(ResourceUtil.listResources(new Resource[]{physical,classRoot},filter), file, filter);
			}
			else {
				CompressUtil.compressZip(classRoot.listResources(filter), file, filter);
			}
			
			if(getBool("append", false)) {
				admin.updateMapping(
						mapping.getVirtual(),
		                mapping.getStrPhysical(),
		                strFile,
		                mapping.isPhysicalFirst()?"physical":"archive",
		                mapping.isTrusted(),
		                mapping.isTopLevel()
		        );
		        store();
			}
			
			
		}
    	catch (IOException e) {
			throw Caster.toPageException(e); 
		}
    	adminSync.broadcast(attributes, config);
    }
    private void doCompileMapping() throws PageException {
        doCompileMapping(getString("admin",action,"virtual").toLowerCase(), getBool("stoponerror", true));
        adminSync.broadcast(attributes, config);
    }
    
    private Mapping doCompileMapping(String virtual, boolean stoponerror) throws PageException {
        
        if(StringUtil.isEmpty(virtual))return null;
        
        if(!StringUtil.startsWith(virtual,'/'))virtual='/'+virtual;
        if(!StringUtil.endsWith(virtual,'/'))virtual+='/';
        
        Mapping[] mappings = config.getMappings();
        for(int i=0;i<mappings.length;i++) {
            Mapping mapping = mappings[i];
            if(mapping.getVirtualLowerCaseWithSlash().equals(virtual)) {
            	Map errors = stoponerror?null:new HashTable();
                doCompileFile(mapping,mapping.getPhysical(),"",errors);
                if(errors!=null && errors.size()>0) {
                	StringBuffer sb=new StringBuffer();
                	Iterator it = errors.keySet().iterator();
                	Object key;
                	while(it.hasNext()) {
                		key=it.next();
                		if(sb.length()>0)sb.append("\n\n");
                		sb.append(errors.get(key));
                		sb.append("\nError Occurred in File ");
                		sb.append("["+key+"]");
                	}
                	throw new ApplicationException(sb.toString());
                }
                return mapping;
            }
        }
        return null;
    }

    private void doCompileFile(Mapping mapping,Resource file,String path,Map errors) throws PageException {
        if(ResourceUtil.exists(file)) {
            if(file.isDirectory()) {
            	Resource[] files = file.listResources(filter);
                for(int i=0;i<files.length;i++) {
                    String p=path+'/'+files[i].getName();
                    //print.ln(files[i]+" - "+p);
                    doCompileFile(mapping,files[i],p,errors);
                }
            }
            else if(file.isFile()) {
                PageSource ps=mapping.getPageSource(path);
                
                
                try {
                    ((PageSourceImpl)ps).clear();
                    ps.loadPage(pageContext.getConfig()); 
                    //pageContext.compile(ps);
                } catch (PageException pe) {
                	//PageException pe = pse.getPageException();
                    
                    String template=ps.getDisplayPath();
                    //if(!StringUtil.isEmpty(pe.getLine())) template+=":"+pe.getLine();
                    
                    if(errors!=null) errors.put(template,pe.getMessage());
                    else throw new ApplicationException(pe.getMessage(),"Error Occurred in File ["+template+"]");
                
                }
            }
        }
    }

    /**
     * @throws PageException
     * 
     */
    private void doResetPassword() throws PageException {
        
        try {
            admin.setPassword(getString("contextPath",null),null);
        }catch (Exception e) {} 
        store();
    }

    /**
     * @throws PageException
     */
    private void doGetContextes() throws PageException {
        
        if(config instanceof ConfigServerImpl) {
            ConfigServerImpl cs=(ConfigServerImpl) config;
            CFMLFactoryImpl[] factories = cs.getJSPFactories(); 
            
            railo.runtime.type.Query qry=
            	new QueryImpl(
            			new String[]{"path","id","label","hasOwnSecContext","url","config_file"},
            			factories.length,getString("admin",action,"returnVariable"));
            pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
            
            for(int i=0;i<factories.length;i++) {
                int row=i+1;
                CFMLFactoryImpl factory = factories[i];
                qry.setAtEL("path",row,factory.getConfigWebImpl().getServletContext().getRealPath("/"));
                
                qry.setAtEL("config_file",row,factory.getConfigWebImpl().getConfigFile().getAbsolutePath());
                if(factory.getURL()!=null)qry.setAtEL("url",row,factory.getURL().toExternalForm());
                
                
                qry.setAtEL("id",row,factory.getConfig().getId());
                qry.setAtEL("label",row,factory.getLabel());
                qry.setAtEL("hasOwnSecContext",row,Caster.toBoolean(cs.hasIndividualSecurityManager(factory.getConfig().getId())));
            }
        }
    }

    private void doHasIndividualSecurity() throws PageException {
        pageContext.setVariable(
                 getString("admin",action,"returnVariable"),
                 Caster.toBoolean(
                         config.getConfigServer(password).hasIndividualSecurityManager(
                                 getString("admin",action,"id")
                                 
                         )
                 )
        );
    }

    private void doUpdateUpdate() throws PageException {
        admin.updateUpdate(getString("admin",action,"updatetype"),getString("admin",action,"updatelocation"));
        store();
        adminSync.broadcast(attributes, config);
    }
    
    /**
     * @throws PageException
     * 
     */
    private void doUpdateSerial() throws PageException {
        admin.updateSerial(getString("admin",action,"serial"));
        store();
        pageContext.serverScope().reload();
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetSerial() throws PageException {
       pageContext.setVariable(
                getString("admin",action,"returnVariable"),
                config.getSerialNumber());
    }
    

    private Resource getPluginDirectory() throws PageException {
    	return config.getConfigDir().getRealResource(config instanceof ConfigServer?"admin/plugin":"context/admin/plugin");//MUST more dynamic
    }
    private void doGetPluginDirectory() throws PageException {
        pageContext.setVariable(
                 getString("admin",action,"returnVariable"),
                 getPluginDirectory().getAbsolutePath());
     }

    private void doUpdatePlugin() throws PageException, IOException {
        String strSrc = getString("admin",action,"source");
        Resource src = ResourceUtil.toResourceExisting(pageContext, strSrc);
        Resource srcDir = ResourceUtil.toResourceExisting(pageContext, "zip://"+src.getAbsolutePath());
        String name=ResourceUtil.getName(src.getName());
        
        if(!PluginFilter.doAccept(srcDir))
        	throw new ApplicationException("plugin ["+srcDir.getName()+"] is invalid, missing one of the following files [Action.cfc,language.xml] in root");
        
        Resource dir = getPluginDirectory();
        Resource trgDir = dir.getRealResource(name);
        if(trgDir.exists()){
        	trgDir.remove(true);
        }
        
        ResourceUtil.copyRecursive(srcDir, trgDir);
        
        store();
    }
    private void doRemovePlugin() throws PageException, IOException {
        Resource dir = getPluginDirectory();
        String name = getString("admin",action,"name");
        Resource trgDir = dir.getRealResource(name);
        trgDir.remove(true);
        
        store();
    }
    
    private void doGetPlugins() throws PageException {
        Resource dir = getPluginDirectory();
    	
    	String[] list = dir.list(new PluginFilter());
    	railo.runtime.type.Query qry=
        	new QueryImpl(
        			new String[]{"name"},
        			list.length,getString("admin",action,"returnVariable"));
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
        
        for(int i=0;i<list.length;i++) {
            int row=i+1;
            qry.setAtEL("name",row,list[i]);
        }
     }
    
    private void doStorageSet() throws PageException {
    	try {
			admin.storageSet(config,getString("admin",action,"key"),getObject("admin", action, "value"));
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	private void doStorageGet() throws PageException {
       try {
		pageContext.setVariable(
		            getString("admin",action,"returnVariable"),
		            admin.storageGet(config,getString("admin",action,"key")));
		} 
       catch (Exception e) {
    	   throw Caster.toPageException(e);
		} 
	}

    /**
     * @throws PageException
     * 
     */
    private void doGetDefaultPassword() throws PageException {
        String password = admin.getDefaultPassword();
        if(password==null) password="";
        
        pageContext.setVariable(
                getString("admin",action,"returnVariable"),
                password);
        
    }
    
    /**
     * @throws PageException
     * 
     */
    private void doUpdateDefaultPassword() throws PageException {
        admin.updateDefaultPassword(getString("admin",action,"newPassword"));
        store();
    }
    private void doRemoveDefaultPassword() throws PageException {
        admin.removeDefaultPassword();
        store();
    }
    

    /* *
     * @throws PageException
     * 
     * /
    private void doUpdatePassword() throws PageException {
        try {
            ConfigWebAdmin.setPassword(config,password==null?null:Caster.toString(password),getString("admin",action,"newPassword"));
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
        //store();
    }*/
    
    /**
     * @throws PageException
     * 
     */
    private void doGetDebug() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        
        String src = config.intDebug()==ConfigImpl.SERVER_BOOLEAN_TRUE || config.intDebug()==ConfigImpl.SERVER_BOOLEAN_FALSE?"server":"web";
        
        sct.set("debug",Caster.toBoolean(config.debug()));
        sct.set("debugSrc",src);
        sct.set("debugTemplate",config.getDebugTemplate());
        
        
        try {
            PageSource ps = pageContext.getPageSource(config.getDebugTemplate());
            if(ps.exists()) sct.set("debugTemplate",ps.getDisplayPath());
            else sct.set("debugTemplate","");
        } catch (PageException e) {
            sct.set("debugTemplate","");
        }
        sct.set("strdebugTemplate",config.getDebugTemplate());
    }
    
    private void doGetError() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        //sct.set("errorTemplate",config.getErrorTemplate());
        
        Struct templates=new StructImpl();
        Struct str=new StructImpl();
        sct.set("templates", templates);
        sct.set("str", str);
        sct.set("doStatusCode", Caster.toBoolean(config.getErrorStatusCode()));
        
        // 500
        String template=config.getErrorTemplate(500);
        try {
            PageSource ps = pageContext.getPageSource(template);
            if(ps.exists()) templates.set("500",ps.getDisplayPath());
            else templates.set("500","");
        } catch (PageException e) {
        	templates.set("500","");
        }
        str.set("500",template);

        // 404
        template=config.getErrorTemplate(404);
        try {
            PageSource ps = pageContext.getPageSource(template);
            if(ps.exists()) templates.set("404",ps.getDisplayPath());
            else templates.set("404","");
        } catch (PageException e) {
        	templates.set("404","");
        }
        str.set("404",template);

        
        
        
        
        
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetDebugData() throws PageException {
        pageContext.setVariable(
                getString("admin",action,"returnVariable"),
                pageContext.getDebugger().getDebuggingData());
    }

    /**
     * 
     */
    private void doPrintDebug() {
        try {
        	DumpWriter writer = pageContext.getConfig().getDefaultDumpWriter();
        	DumpData data = pageContext.getDebugger().toDumpData(pageContext, 9999,DumpUtil.toDumpProperties());
            pageContext.forceWrite(writer.toString(pageContext,data,true));
        } catch (IOException e) {}
    }

    /**
     * @throws PageException
     * 
     */
    private void doCreateSecurityManager() throws  PageException {
        admin.createSecurityManager(getString("admin",action,"id"));
        store();
    }
    
    private void doRemoveSecurityManager() throws  PageException {
        admin.removeSecurityManager(getString("admin",action,"id"));
        store();
    }
    
    

    private short fb(String key) throws PageException {
        return getBool("admin",action,key)?SecurityManager.VALUE_YES:SecurityManager.VALUE_NO;
    }
    private short fb2(String key) throws PageException {
        return SecurityManagerImpl.toShortAccessRWValue(getString("admin",action,key));
    }

    private void doUpdateDefaultSecurityManager() throws  PageException {
    	
    	admin.updateDefaultSecurity(
                fb("setting"),
                SecurityManagerImpl.toShortAccessValue(getString("admin",action,"file")),
                getFileAcces(),
                fb("direct_java_access"),
                fb("mail"),
                SecurityManagerImpl.toShortAccessValue(getString("admin",action,"datasource")),
                fb("mapping"),
                fb("remote"),
                fb("custom_tag"),
                fb("cfx_setting"),
                fb("cfx_usage"),
                fb("debugging"),
                fb("search"),
                fb("scheduled_task"),
                fb("tag_execute"),
                fb("tag_import"),
                fb("tag_object"),
                fb("tag_registry"),
                fb2("access_read"),
                fb2("access_write")
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    private Resource[] getFileAcces() throws PageException {
    	Object value=attributes.get("file_access",null);
        if(value==null) return null;
        Array arr = Caster.toArray(value);
        List rtn = new ArrayList();
        Iterator it = arr.valueIterator();
        String path;
        Resource res;
        while(it.hasNext()){
        	path=Caster.toString(it.next());
        	if(StringUtil.isEmpty(path))continue;
        	
        	res=config.getResource(path);
        	if(!res.exists())
        		throw new ApplicationException("path ["+path+"] does not exist");
        	if(!res.isDirectory())
        		throw new ApplicationException("path ["+path+"] is not a directory");
        	rtn.add(res);
        }
        return (Resource[])rtn.toArray(new Resource[rtn.size()]);
	}

	private void doUpdateSecurityManager() throws  PageException {
        admin.updateSecurity(
                getString("admin",action,"id"),
                fb("setting"),
                SecurityManagerImpl.toShortAccessValue(getString("admin",action,"file")),
                getFileAcces(),
                fb("direct_java_access"),
                fb("mail"),
                SecurityManagerImpl.toShortAccessValue(getString("admin",action,"datasource")),
                fb("mapping"),
                fb("remote"),
                fb("custom_tag"),
                fb("cfx_setting"),
                fb("cfx_usage"),
                fb("debugging"),
                fb("search"),
                fb("scheduled_task"),
                fb("tag_execute"),
                fb("tag_import"),
                fb("tag_object"),
                fb("tag_registry"),
                fb2("access_read"),
                fb2("access_write")
        );
        store();
    }

    
    
    /**
     * @throws PageException
     * 
     */
    private void doGetDefaultSecurityManager() throws PageException {
        SecurityManager dsm = config.getConfigServer(password).getDefaultSecurityManager();
        _fillSecData(dsm);
    }

    private void doGetSecurityManager() throws PageException {
        SecurityManager sm = config.getConfigServer(password).getSecurityManager(getString("admin",action,"id"));
        _fillSecData(sm);
    }
    
    private void _fillSecData(SecurityManager sm) throws PageException {
        
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("cfx_setting",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_CFX_SETTING)==SecurityManager.VALUE_YES));
        sct.set("cfx_usage",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_CFX_USAGE)==SecurityManager.VALUE_YES));
        sct.set("custom_tag",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_CUSTOM_TAG)==SecurityManager.VALUE_YES));
        sct.set("datasource",_fillSecDataDS(sm.getAccess(SecurityManager.TYPE_DATASOURCE)));
        sct.set("debugging",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_DEBUGGING)==SecurityManager.VALUE_YES));
        sct.set("direct_java_access",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES));
        sct.set("mail",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_MAIL)==SecurityManager.VALUE_YES));
        sct.set("mapping",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_MAPPING)==SecurityManager.VALUE_YES));
        sct.set("remote",Caster.toBoolean(sm.getAccess(SecurityManagerImpl.TYPE_REMOTE)==SecurityManager.VALUE_YES));
        sct.set("setting",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_SETTING)==SecurityManager.VALUE_YES));
        sct.set("search",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_SEARCH)==SecurityManager.VALUE_YES));
        sct.set("scheduled_task",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_SCHEDULED_TASK)==SecurityManager.VALUE_YES));
        
        sct.set("tag_execute",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_TAG_EXECUTE)==SecurityManager.VALUE_YES));
        sct.set("tag_import",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_TAG_IMPORT)==SecurityManager.VALUE_YES));
        sct.set("tag_object",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_TAG_OBJECT)==SecurityManager.VALUE_YES));
        sct.set("tag_registry",Caster.toBoolean(sm.getAccess(SecurityManager.TYPE_TAG_REGISTRY)==SecurityManager.VALUE_YES));
        sct.set("access_read",SecurityManagerImpl.toStringAccessRWValue(sm.getAccess(SecurityManager.TYPE_ACCESS_READ)));
        sct.set("access_write",SecurityManagerImpl.toStringAccessRWValue(sm.getAccess(SecurityManager.TYPE_ACCESS_WRITE)));
        short accessFile = sm.getAccess(SecurityManager.TYPE_FILE);
        String str = SecurityManagerImpl.toStringAccessValue(accessFile);
        if(str.equals("yes"))str="all";
        sct.set("file",str);
        
    	ArrayImpl arr=new ArrayImpl();
    	if(accessFile!=SecurityManager.VALUE_ALL){
        	Resource[] reses = ((SecurityManagerImpl)sm).getCustomFileAccess();
        	for(int i=0;i<reses.length;i++){
        		arr.add(reses[i].getAbsolutePath());
    		}
    	}
    	sct.set("file_access",arr);
    
    }

	private Double _fillSecDataDS(short access) {
		switch(access) {
		case SecurityManager.VALUE_YES: return Caster.toDouble(-1);
		case SecurityManager.VALUE_NO: return Caster.toDouble(0);
		case SecurityManager.VALUE_1: return Caster.toDouble(1);
		case SecurityManager.VALUE_2: return Caster.toDouble(2);
		case SecurityManager.VALUE_3: return Caster.toDouble(3);
		case SecurityManager.VALUE_4: return Caster.toDouble(4);
		case SecurityManager.VALUE_5: return Caster.toDouble(5);
		case SecurityManager.VALUE_6: return Caster.toDouble(6);
		case SecurityManager.VALUE_7: return Caster.toDouble(7);
		case SecurityManager.VALUE_8: return Caster.toDouble(8);
		case SecurityManager.VALUE_9: return Caster.toDouble(9);
		case SecurityManager.VALUE_10: return Caster.toDouble(10);
		}
		return Caster.toDouble(-1);
	}

	/**
     * @throws PageException
     * 
     */
    private void doUpdateDebug() throws PageException {
    	admin.updateDebug(Caster.toBoolean(getString("debug",""),null));
        admin.updateDebugTemplate(getString("admin",action,"debugTemplate"));
        store();
        adminSync.broadcast(attributes, config);
    }
    private void doUpdateError() throws PageException {

        admin.updateErrorTemplate(500,getString("admin",action,"template500"));
        admin.updateErrorTemplate(404,getString("admin",action,"template404"));
        admin.updateErrorStatusCode(getBool("admin",action,"statuscode"));
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateJavaCFX() throws PageException {
        String name=getString("admin",action,"name");
        if(StringUtil.startsWithIgnoreCase(name,"cfx_"))name=name.substring(4);
        admin.updateJavaCFX(
                name,
                getString("admin",action,"class")
        );
        store();
        adminSync.broadcast(attributes, config);
    }
    
    private void doVerifyJavaCFX() throws PageException {
        String name=getString("admin",action,"name");
        admin.verifyJavaCFX(
                name,
                getString("admin",action,"class")
        );
    }
    
    

    /**
     * @throws PageException
     * 
     */
    private void doRemoveCFX() throws PageException {
        admin.removeCFX(
                getString("admin",action,"name")
        );
        store();
        adminSync.broadcast(attributes, config);
    }
    private void doRemoveExtension() throws PageException {
        admin.removeExtension(
                getString("admin",action,"provider"),
                getString("admin",action,"id")
        );
        store();
        //adminSync.broadcast(attributes, config);
    }
    
    

    /**
     * @throws PageException
     * 
     */
    private void doGetJavaCFXTags() throws PageException {
        Map map = config.getCFXTagPool().getClasses();
        QueryImpl qry=new QueryImpl(new String[]{"displayname","sourcename","readonly","class","name","isvalid"},0,"query");
        Iterator it = map.keySet().iterator();
        
        int row=0;
        while(it.hasNext()) {
            CFXTagClass tag=(CFXTagClass) map.get(it.next());
            if(tag instanceof JavaCFXTagClass) {
                row++;
                qry.addRow(1);
                JavaCFXTagClass jtag =(JavaCFXTagClass) tag;
                qry.setAt("displayname",row,tag.getDisplayType());
                qry.setAt("sourcename",row,tag.getSourceName());
                qry.setAt("readonly",row,Caster.toBoolean(tag.isReadOnly()));
                qry.setAt("isvalid",row,Caster.toBoolean(tag.isValid()));
                qry.setAt("name",row,jtag.getName());
                qry.setAt("class",row,jtag.getStrClass());
            }
            
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }
    /**
     * @throws PageException
     * 
     */
    private void doGetCFXTags() throws PageException {
        Map map = config.getCFXTagPool().getClasses();
        QueryImpl qry=new QueryImpl(new String[]{"displayname","sourcename","readonly","isvalid"},map.size(),"query");
        Iterator it = map.keySet().iterator();
        
        int row=0;
        while(it.hasNext()) {
            row++;
            CFXTagClass tag=(CFXTagClass) map.get(it.next());
            
            qry.setAt("displayname",row,tag.getDisplayType());
            qry.setAt("sourcename",row,tag.getSourceName());
            qry.setAt("readonly",row,Caster.toBoolean(tag.isReadOnly()));
            qry.setAt("isvalid",row,Caster.toBoolean(tag.isValid()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    } 

    /**
     * @throws PageException
     */
    private void doUpdateCustomTag() throws PageException {
        admin.updateCustomTag(
                getString("admin",action,"virtual"),
                getString("admin",action,"physical"),
                getString("admin",action,"archive"),
                getString("admin",action,"primary"),
                Caster.toBooleanValue(getString("admin",action,"trusted"))
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doRemoveCustomTag() throws PageException {
        admin.removeCustomTag(
                getString("admin",action,"virtual")
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetCustomTagMappings() throws PageException {
        Mapping[] mappings = config.getCustomTagMappings();
        QueryImpl qry=new QueryImpl(new String[]{"archive","strarchive","physical","strphysical","virtual","hidden","physicalFirst","readonly","trusted"},mappings.length,"query");
        
        
        for(int i=0;i<mappings.length;i++) {
            Mapping m=mappings[i];
            int row=i+1;
            qry.setAt("archive",row,m.getArchive());
            qry.setAt("strarchive",row,m.getStrArchive());
            qry.setAt("physical",row,m.getPhysical());
            qry.setAt("strphysical",row,m.getStrPhysical());
            qry.setAt("virtual",row,m.getVirtual());
            qry.setAt("hidden",row,Caster.toBoolean(m.isHidden()));
            qry.setAt("physicalFirst",row,Caster.toBoolean(m.isPhysicalFirst()));
            qry.setAt("readonly",row,Caster.toBoolean(m.isReadonly()));
            qry.setAt("trusted",row,Caster.toBoolean(m.isTrusted()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    /**
     * @throws PageException
     * 
     */
    private void doRemoveMapping() throws PageException {
        admin.removeMapping(
                getString("admin",action,"virtual")
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateMapping() throws PageException {
        admin.updateMapping(
                getString("admin",action,"virtual"),
                getString("admin",action,"physical"),
                getString("admin",action,"archive"),
                getString("admin",action,"primary"),
                Caster.toBooleanValue(getString("admin",action,"trusted")),
                Caster.toBooleanValue(getString("toplevel","true"))
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetMapping() throws PageException {
        
    	
        Mapping[] mappings = config.getMappings();
        Struct sct=new StructImpl();
        String virtual=getString("admin",action,"virtual");
        
        for(int i=0;i<mappings.length;i++) {
            Mapping m=mappings[i];
            if(!m.getVirtual().equals(virtual)) continue;
            
            sct.set("archive",m.getArchive());
            sct.set("strarchive",m.getStrArchive());
            sct.set("physical",m.getPhysical());
            sct.set("strphysical",m.getStrPhysical());
            sct.set("virtual",m.getVirtual());
            sct.set("hidden",Caster.toBoolean(m.isHidden()));
            sct.set("physicalFirst",Caster.toBoolean(m.isPhysicalFirst()));
            sct.set("readonly",Caster.toBoolean(m.isReadonly()));
            sct.set("trusted",Caster.toBoolean(m.isTrusted()));
            sct.set("toplevel",Caster.toBoolean(m.isTopLevel()));

            pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
            return;
        }
        throw new ApplicationException("there is no mapping with virtual ["+virtual+"]");
    }
    

    private void doGetExtensionProviders() throws PageException {
    	ExtensionProvider[] providers = config.getExtensionProviders();
        QueryImpl qry=new QueryImpl(new String[]{"url","isReadOnly"},providers.length,"query");
        
        ExtensionProvider provider;
        for(int i=0;i<providers.length;i++) {
        	provider=providers[i];
            int row=i+1;
            //qry.setAt("name",row,provider.getName());
            qry.setAt("url",row,provider.getUrlAsString());
            qry.setAt("isReadOnly",row,Caster.toBoolean(provider.isReadOnly()));
            //qry.setAt("cacheTimeout",row,Caster.toDouble(provider.getCacheTimeout()/1000));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    private void doGetExtensionInfo() throws PageException {
    	Resource ed = config.getExtensionDirectory();
    	Struct sct=new StructImpl();
    	sct.set("directory", ed.getPath());
    	sct.set("enabled", Caster.toBoolean(config.isExtensionEnabled()));
    	
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
    }
    
    
    
    private void doGetExtensions() throws PageException {
    	Extension[] extensions = config.getExtensions();
        QueryImpl qry=new QueryImpl(new String[]{
        		"type","provider","id","config","version","category","description","image","label","name",
        		"author","codename","video","support","documentation","forum","mailinglist","network","created"},0,"query");
        
        
        
        String provider=getString("provider",null);
        String id=getString("id",null);
    	Extension extension;
        String extProvider,extId;
        int row=0;
        for(int i=0;i<extensions.length;i++) {
        	extension=extensions[i];
        	
        	if(toType(extension.getType(), false)!=type)
        		continue;

        	extProvider=extension.getProvider();
        	extId=extension.getId();
        	if(provider!=null && !provider.equalsIgnoreCase(extProvider)) continue;
        	if(id!=null && !id.equalsIgnoreCase(extId)) continue;

            qry.addRow();
        	row++;
            qry.setAt("provider",row,extProvider);
            qry.setAt("id",row,extId);
            qry.setAt("config",row,extension.getConfig(pageContext));
            qry.setAt("version",row,extension.getVersion());
            
            qry.setAt("category",row,extension.getCategory());
            qry.setAt("description",row,extension.getDescription());
            qry.setAt("image",row,extension.getImage());
            qry.setAt("label",row,extension.getLabel());
            qry.setAt("name",row,extension.getName());

            qry.setAt("author",row,extension.getAuthor());
            qry.setAt("codename",row,extension.getCodename());
            qry.setAt("video",row,extension.getVideo());
            qry.setAt("support",row,extension.getSupport());
            qry.setAt("documentation",row,extension.getDocumentation());
            qry.setAt("forum",row,extension.getForum());
            qry.setAt("mailinglist",row,extension.getMailinglist());
            qry.setAt("network",row,extension.getNetwork());
            qry.setAt("created",row,extension.getCreated());
            qry.setAt("type",row,extension.getType());
            
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }
    
    
    
    
private void doGetMappings() throws PageException {
        

        Mapping[] mappings = config.getMappings();
        QueryImpl qry=new QueryImpl(new String[]{"archive","strarchive","physical","strphysical","virtual","hidden","physicalFirst","readonly","trusted","toplevel"},mappings.length,"query");
        
        
        for(int i=0;i<mappings.length;i++) {
            Mapping m=mappings[i];
            int row=i+1;
            qry.setAt("archive",row,m.getArchive());
            qry.setAt("strarchive",row,m.getStrArchive());
            qry.setAt("physical",row,m.getPhysical());
            qry.setAt("strphysical",row,m.getStrPhysical());
            qry.setAt("virtual",row,m.getVirtual());
            qry.setAt("hidden",row,Caster.toBoolean(m.isHidden()));
            qry.setAt("physicalFirst",row,Caster.toBoolean(m.isPhysicalFirst()));
            qry.setAt("readonly",row,Caster.toBoolean(m.isReadonly()));
            qry.setAt("trusted",row,Caster.toBoolean(m.isTrusted()));
            qry.setAt("toplevel",row,Caster.toBoolean(m.isTopLevel()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

	private void doGetResourceProviders() throws PageException {
        
		pageContext.setVariable(getString("admin",action,"returnVariable"),admin.getResourceProviders());
    }
	
	private void doGetClusterClass() throws PageException {
		pageContext.setVariable(getString("admin",action,"returnVariable"),config.getClusterClass().getName());
    }

    
    private void doUpdateClusterClass() throws PageException {
    	admin.updateClusterClass(getString("admin",action,"class"));
        store();
    }
    
	private void doUpdateAdminSyncClass() throws PageException {
		admin.updateAdminSyncClass(getString("admin",action,"class"));
        store();
	}

	private void doGetAdminSyncClass() throws PageException {
		pageContext.setVariable(getString("admin",action,"returnVariable"),config.getAdminSyncClass().getName());
	}

	private void doUpdateVideoExecuterClass() throws PageException {
		admin.updateVideoExecuterClass(getString("admin",action,"class"));
        store();
	}

	private void doGetVideoExecuterClass() throws PageException {
		pageContext.setVariable(getString("admin",action,"returnVariable"),config.getVideoExecuterClass().getName());
	}
    
	

    /**
     * @throws PageException
     * 
     */
    private void doRemoveMailServer() throws PageException {
        admin.removeMailServer(getString("admin",action,"hostname"));
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateMailServer() throws PageException {
        
        admin.updateMailServer(
                getString("admin",action,"hostname"),
                getString("admin",action,"dbusername"),
                getString("admin",action,"dbpassword"),
                Caster.toIntValue(getString("admin",action,"port")),
                getBool("tls", false),
                getBool("ssl", false)
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateMailSetting() throws PageException {
        admin.setMailLog(getString("admin",action,"logfile"),getString("loglevel","ERROR"));
        
        //print.ln("----------------------------------");
        admin.setMailSpoolEnable(getBool("admin",action,"spoolenable"));
        admin.setMailSpoolInterval(Caster.toIntValue(getString("admin",action,"spoolinterval")));
        admin.setMailTimeout(Caster.toIntValue(getString("admin",action,"timeout")));
		admin.setMailDefaultCharset(getString("admin", action, "defaultencoding"));
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetMailServers() throws PageException {
        
        

        Server[] servers = config.getMailServers();
        QueryImpl qry=new QueryImpl(new String[]{"hostname","password","username","port","authentication","readonly","tls","ssl"},servers.length,"query");
        
        
        for(int i=0;i<servers.length;i++) {
            Server s= servers[i];
            int row=i+1;
            qry.setAt("hostname",row,s.getHostName());
            qry.setAt("password",row,s.isReadOnly()?"":s.getPassword());
            qry.setAt("username",row,s.isReadOnly()?"":s.getUsername());
            qry.setAt("port",row,Caster.toInteger(s.getPort()));
            qry.setAt("readonly",row,Caster.toBoolean(s.isReadOnly()));
            qry.setAt("authentication",row,Caster.toBoolean(s.hasAuthentication()));
            if(s instanceof ServerImpl) {
            	ServerImpl si = (ServerImpl)s;
	            qry.setAt("ssl",row,Caster.toBoolean(si.isSSL()));
	            qry.setAt("tls",row,Caster.toBoolean(si.isTLS()));
            }
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetMailSetting() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        
        LogAndSource ls=config.getMailLogger();
        railo.commons.io.log.Log log=ls.getLog();
        String logFile="";
        //String logLevel="";
        if(log instanceof LogResource)logFile=((LogResource)log).getResource().toString();
        String logLevel=LogUtil.toStringType(log.getLogLevel(),"ERROR");
        sct.set("strlogfile",ls.getSource());
        sct.set("logfile",logFile);
        sct.set("loglevel",logLevel);
        
        sct.set("spoolEnable",Caster.toBoolean(config.isMailSpoolEnable()));
        sct.set("spoolInterval",Caster.toInteger(config.getMailSpoolInterval()));
        sct.set("timeout",Caster.toInteger(config.getMailTimeout()));
		sct.set("defaultencoding", config.getMailDefaultEncoding());
        
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetTLDs() throws PageException {
    	railo.runtime.type.Query qry=new QueryImpl(
    			new String[]{"displayname","namespace","namespaceseparator","shortname","type","description","uri","elclass","source"},
    			new String[]{"varchar","varchar","varchar","varchar","varchar","varchar","varchar","varchar","varchar"},
    			0,"tlds");
       

        TagLib[] libs = config.getTLDs();
        for(int i=0;i<libs.length;i++) {
        	qry.addRow();
        	qry.setAt("displayname", i+1, libs[i].getDisplayName());
        	qry.setAt("namespace", i+1, libs[i].getNameSpace());
        	qry.setAt("namespaceseparator", i+1, libs[i].getNameSpaceSeparator());
        	qry.setAt("shortname", i+1, libs[i].getShortName());
        	qry.setAt("type", i+1, libs[i].getType());
        	qry.setAt("description", i+1, libs[i].getDescription());
        	qry.setAt("uri", i+1, Caster.toString(libs[i].getUri()));
        	qry.setAt("elclass", i+1, libs[i].getELClass());
        	qry.setAt("source", i+1, StringUtil.toStringEmptyIfNull(libs[i].getSource()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetFLDs() throws PageException {
        railo.runtime.type.Query qry=new QueryImpl(
    			new String[]{"displayname","namespace","namespaceseparator","shortname","description","uri","source"},
    			new String[]{"varchar","varchar","varchar","varchar","varchar","varchar","varchar"},
    			0,"tlds");
       

        FunctionLib[] libs = config.getFLDs();
        for(int i=0;i<libs.length;i++) {
        	qry.addRow();
        	qry.setAt("displayname", i+1, libs[i].getDisplayName());
        	qry.setAt("namespace", i+1, "");// TODO support for namespace
        	qry.setAt("namespaceseparator", i+1, "");
        	qry.setAt("shortname", i+1, libs[i].getShortName());
        	qry.setAt("description", i+1, libs[i].getDescription());
        	qry.setAt("uri", i+1, Caster.toString(libs[i].getUri()));
        	qry.setAt("source", i+1, StringUtil.toStringEmptyIfNull(libs[i].getSource()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    private void doGetRemoteClientUsage() throws PageException {
        railo.runtime.type.Query qry=new QueryImpl(
    			new String[]{"code","displayname"},
    			new String[]{"varchar","varchar"},
    			0,"usage");
       

        Struct usages = config.getRemoteClientUsage();
        Key[] keys = usages.keys();
        for(int i=0;i<keys.length;i++) {
        	qry.addRow();
        	qry.setAt("code", i+1, keys[i].getString());
        	qry.setAt("displayname", i+1, usages.get(keys[i]));
        	//qry.setAt("description", i+1, usages[i].getDescription());
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }
    
    
    
    private void doGetJars() throws PageException {
    	Resource lib = config.getConfigDir().getRealResource("lib");
		railo.runtime.type.Query qry=new QueryImpl(new String[]{"name","source"},new String[]{"varchar","varchar"},0,"jars");
       
		if(lib.isDirectory()){
			Resource[] children = lib.listResources(new ExtensionResourceFilter(new String[]{".jar",".zip"},false,true));
			for(int i=0;i<children.length;i++){
				qry.addRow();
				qry.setAt("name", i+1, children[i].getName());
				qry.setAt("source", i+1, children[i].getAbsolutePath());
			}
		}
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateDatasource() throws PageException {
        int allow=
        (getBool("allowed_select",false)?DataSource.ALLOW_SELECT:0)+
        (getBool("allowed_insert",false)?DataSource.ALLOW_INSERT:0)+
        (getBool("allowed_update",false)?DataSource.ALLOW_UPDATE:0)+
        (getBool("allowed_delete",false)?DataSource.ALLOW_DELETE:0)+
        (getBool("allowed_alter",false)?DataSource.ALLOW_ALTER:0)+
        (getBool("allowed_drop",false)?DataSource.ALLOW_DROP:0)+
        (getBool("allowed_revoke",false)?DataSource.ALLOW_REVOKE:0)+
        (getBool("allowed_grant",false)?DataSource.ALLOW_GRANT:0)+
        (getBool("allowed_create",false)?DataSource.ALLOW_CREATE:0);
        if(allow==0)allow=DataSource.ALLOW_ALL;
        String classname=getString("admin",action,"classname");
        /*Class clazz=null;
        try {
        	clazz = ClassUtil.loadClass(config.getClassLoader(),classname);//Class.orName(classname);
        } 
        catch (ClassException e) {
        	throw new DatabaseException("can't find class ["+classname+"] for jdbc driver, check if driver (jar file) is inside lib folder",e.getMessage(),null,null,null);
        }*/
        
        String dsn=getString("admin",action,"dsn");
        String name=getString("admin",action,"name");
        String username=getString("admin",action,"dbusername");
        String password=getString("admin",action,"dbpassword");
        String host=getString("host","");
        String database=getString("database","");
        int port=getInt("port",-1);
        int connLimit=getInt("connectionLimit",-1);
        int connTimeout=getInt("connectionTimeout",-1);
        boolean blob=getBool("blob",false);
        boolean clob=getBool("clob",false);
        Struct custom=getStruct("custom",new StructImpl());
        
        config.getDatasourceConnectionPool().remove(name);
        //config.getConnectionPool().remove(name);
        DataSource ds=null;
		try {
			ds = new DataSourceImpl(name,classname,host,dsn,database,port,username,password,connLimit,connTimeout,blob,clob,allow,custom,false);
		} catch (ClassException e) {
			throw new DatabaseException("can't find class ["+classname+"] for jdbc driver, check if driver (jar file) is inside lib folder",e.getMessage(),null,null,null);
		}
        
        _doVerifyDatasource(classname,ds.getDsnTranslated(),username,password);
        //print.out("limit:"+connLimit);
        admin.updateDataSource(
                name,
                classname,
                dsn,
                username,
                password,
                host,
                database,
                port,
                connLimit,
                connTimeout,
                blob,
                clob,
                allow,
                custom
                
        );
        store();
        adminSync.broadcast(attributes, config);
    }

    private void doRemoveResourceProvider() throws PageException {
    	String classname=getString("admin",action,"class");
    	
    	Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
        
        admin.removeResourceProvider(clazz);
        
        store();
        adminSync.broadcast(attributes, config);
    }
    
    
    private void doUpdateResourceProvider() throws PageException {
    	String classname=getString("admin",action,"class");
    	
    	Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
    	String scheme=getString("admin",action,"scheme");
    	
    	Struct sctArguments = getStruct("arguments", null);
    	if(sctArguments!=null) {
    		
    		admin.updateResourceProvider(scheme,clazz,sctArguments);
    	}
    	else {
    		String strArguments=getString("admin",action,"arguments");
        	admin.updateResourceProvider(scheme,clazz,strArguments);
    	}
        
        
        
        //admin.updateResourceProvider(scheme,clazz,arguments);
        store();
        adminSync.broadcast(attributes, config);
    }
    
    private void doUpdateDefaultResourceProvider() throws PageException {
    	String classname=getString("admin",action,"class");
    	Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
        String arguments=getString("admin",action,"arguments");
    	
        admin.updateDefaultResourceProvider(clazz,arguments);
        store();
        adminSync.broadcast(attributes, config);
    }
    
    

    private void doVerifyMailServer() throws PageException {
        _doVerifyMailServer(
                    getString("admin",action,"hostname"),
                    getInt("admin",action,"port"),
                    getString("admin",action,"mailusername"),
                    getString("admin",action,"mailpassword")
        );
    }
    
    private void _doVerifyMailServer(String host, int port, String user, String pass) throws PageException {
           try {
            SMTPVerifier.verify(host,user,pass,port);
        } catch (SMTPException e) {
            throw Caster.toPageException(e);
        }
    }

    /**
     * @throws PageException
     * 
     */
    private void doVerifyDatasource() throws PageException {
        String classname=(String) attributes.get("classname",null);
        String dsn=(String) attributes.get("dsn",null);
        if(classname!=null && dsn!=null) {
            _doVerifyDatasource(classname,dsn,
                    getString("admin",action,"dbusername"),
                    getString("admin",action,"dbpassword"));
        }
        else {
            _doVerifyDatasource(
                    getString("admin",action,"name"),
                    getString("admin",action,"dbusername"),
                    getString("admin",action,"dbpassword"));
        }
    }
    
    private void doVerifyRemoteClient() throws PageException {
    	// SNSN
    	/*SerialNumber sn = config.getSerialNumber();
        if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
            throw new SecurityException("can not verify remote client with "+sn.getStringVersion()+" version of railo");
	    */
    	ProxyData pd=null;
    	String proxyServer=getString("proxyServer",null);
    	if(!StringUtil.isEmpty(proxyServer)) {
	    	String proxyUsername=getString("proxyUsername",null);
	    	String proxyPassword=getString("proxyPassword",null);
	    	int proxyPort = getInt("proxyPort",-1);
	    	pd=new ProxyDataImpl();
	    		pd.setServer(proxyServer);
		    	if(!StringUtil.isEmpty(proxyUsername))pd.setUsername(proxyUsername);
		    	if(!StringUtil.isEmpty(proxyPassword))pd.setPassword(proxyPassword);
		    	if(proxyPort!=-1)pd.setPort(proxyPort);
    	}	
    	RemoteClient client = new RemoteClientImpl(
    			getString("admin",action,"label"),
    			type==TYPE_WEB?"web":"server",
    			getString("admin",action,"url"),
    			getString("serverUsername",null),
    			getString("serverPassword",null),
    			getString("admin",action,"adminPassword"),
    			pd,
    			getString("admin",action,"securityKey"),
    			getString("admin",action,"usage")
    			
    	);
    	
    	Struct attrColl=new StructImpl();
    	attrColl.setEL("action", "connect");
    	try {
			new RemoteClientTask(null,client,attrColl,getCallerId(),"synchronisation").execute(config);
		} 
    	catch (Throwable t) {
			throw Caster.toPageException(t);
		}
    }
    

    private void _doVerifyDatasource(String classname, String dsn, String username, String password) throws PageException {
        try {
        	Class clazz=null;
        	try {
        		clazz=Class.forName(classname);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(clazz==null)clazz=ClassUtil.loadClass(config.getClassLoader(),classname);
            _doVerifyDatasource(clazz, dsn, username, password);
        } 
        catch (ClassException e) {
            throw Caster.toPageException(e);
        }
    }

    private void _doVerifyDatasource(Class clazz, String dsn, String username, String password) throws PageException {
            if(!Reflector.isInstaneOf(clazz,Driver.class))
                throw new DatabaseException("class ["+clazz.getName()+"] is not a JDBC Driver","class must implement interface [java.sql.Driver]",null,null,null);
        getConnection(dsn, username, password);
    }

    private void _doVerifyDatasource(String name, String username, String password) throws PageException {
    	DataSourceManager manager = pageContext.getDataSourceManager();
    	manager.releaseConnection(pageContext,manager.getConnection(pageContext,name, username, password));
        //config.getConnection(name, username, password);
    }
    
    
    private Connection getConnection(String dsn, String user, String pass) throws DatabaseException  {
        Connection conn=null;
        try {
            if(dsn.indexOf('?')==-1) {
                conn = DriverManager.getConnection(dsn, user, pass);
            }
            else{
                String connStr=dsn+"&user="+user+"&password="+pass;
                conn = DriverManager.getConnection(connStr, user, pass);
            }
            conn.setAutoCommit(true);
        } 
        catch (SQLException e) {
            throw new DatabaseException(e,null);
        }
        return conn;
    }
    
    

    /**
     * @throws PageException
     * 
     */
    private void doUpdatePSQ() throws PageException {
        admin.updatePSQ(getBool("admin",action,"psq"));
        store();
        adminSync.broadcast(attributes, config);
    }
    
    private void doReload() throws PageException {
        store();
    }

    /**
     * @throws PageException
     * 
     */
    private void doRemoveDatasource() throws PageException {
        admin.removeDataSource(getString("admin",action,"name"));
        store();
        adminSync.broadcast(attributes, config);
    }
    private void doRemoveRemoteClient() throws PageException {
        admin.removeRemoteClient(getString("admin",action,"url"));
        store();
    }
    private void doRemoveSpoolerTask() throws PageException {
    	config.getSpoolerEngine().remove(getString("admin",action,"id"));
    }
    private void doExecuteSpoolerTask() throws PageException {
    	PageException pe = config.getSpoolerEngine().execute(getString("admin",action,"id"));
		if(pe!=null) throw pe;
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetDatasourceSetting() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("psq",Caster.toBoolean(config.getPSQL()));
    }

    private void doUpdatePerformanceSettings() throws SecurityException, PageException {
    	admin.updateInspectTemplate(getString("admin",action,"inspectTemplate"));
        store();
        adminSync.broadcast(attributes, config);
	}

	private void doGetPerformanceSettings() throws ApplicationException, PageException {
		Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        
        short it = config.getInspectTemplate();
        String str="once";
        if(it==ConfigImpl.INSPECT_ALWAYS)str="always";
        else if(it==ConfigImpl.INSPECT_NEVER)str="never";
        sct.set("inspectTemplate",str);
	}
    
    
    private void doGetCustomTagSetting() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("customTagDeepSearch",Caster.toBoolean(config.doCustomTagDeepSearch()));// deprecated
        sct.set("customTagLocalSearch",Caster.toBoolean(config.doLocalCustomTag()));// deprecated
        
        sct.set("deepSearch",Caster.toBoolean(config.doCustomTagDeepSearch()));
        sct.set("localSearch",Caster.toBoolean(config.doLocalCustomTag()));
        sct.set("extensions",new ArrayImpl(config.getCustomTagExtensions()));
    }

    private void doGetDatasourceDriverList() throws PageException {
    	Resource railoContext = ResourceUtil.toResourceExisting(pageContext ,"/railo-context/admin/dbdriver/");
    	Resource[] children = railoContext.listResources(new ExtensionResourceFilter("cfc"));
    	String rtnVar=getString("admin",action,"returnVariable");
    	
    	QueryImpl qry=new QueryImpl(new String[]{"name"},children.length,rtnVar);
         
    	
    	for(int i=0;i<children.length;i++) {
    		qry.setAt("name", i+1, children[i].getName());
    	}
    	pageContext.setVariable(rtnVar,qry);
        
    }

    /*private String getContextPath() {
		String cp = pageContext. getHttpServletRequest().getContextPath();
		if(cp==null)return "";
		return cp;
	}*/

	private void doGetDebuggingList() throws PageException {
    	Resource railoContext = ResourceUtil.toResourceExisting(pageContext ,"/railo-context/templates/debugging/");
    	Resource[] children = railoContext.listResources(new ExtensionResourceFilter("cfm"));
    	String rtnVar=getString("admin",action,"returnVariable");
    	
    	QueryImpl qry=new QueryImpl(new String[]{"name"},children.length,rtnVar);
         
    	
    	for(int i=0;i<children.length;i++) {
    		qry.setAt("name", i+1, children[i].getName());
    	}
    	pageContext.setVariable(rtnVar,qry);
        
    }
    

    /**
     * @throws PageException
     * 
     */
    private void doGetDatasource() throws PageException {
        
        String name=getString("admin",action,"name");
        Map ds = config.getDataSourcesAsMap();
        Iterator it = ds.keySet().iterator();
        
        while(it.hasNext()) {
            String key=(String)it.next();
            if(key.equalsIgnoreCase(name)) {
                DataSource d=(DataSource) ds.get(key);
                Struct sct=new StructImpl();
                
                sct.setEL("name",key);
                sct.setEL("host",d.getHost());
                sct.setEL("classname",d.getClazz().getName());
                sct.setEL("dsn",d.getDsnOriginal());
                sct.setEL("database",d.getDatabase());
                sct.setEL("port",d.getPort()<1?"":Caster.toString(d.getPort()));
                sct.setEL("dsnTranslated",d.getDsnTranslated());
                sct.setEL("password",d.getPassword());
                sct.setEL("username",d.getUsername());
                sct.setEL("readonly",Caster.toBoolean(d.isReadOnly()));
                sct.setEL("select",Constants.Boolean(d.hasAllow(DataSource.ALLOW_SELECT)));
                sct.setEL("delete",Constants.Boolean(d.hasAllow(DataSource.ALLOW_DELETE)));
                sct.setEL("update",Constants.Boolean(d.hasAllow(DataSource.ALLOW_UPDATE)));
                sct.setEL("insert",Constants.Boolean(d.hasAllow(DataSource.ALLOW_INSERT)));
                sct.setEL("create",Constants.Boolean(d.hasAllow(DataSource.ALLOW_CREATE)));
                sct.setEL("insert",Constants.Boolean(d.hasAllow(DataSource.ALLOW_INSERT)));
                sct.setEL("drop",Constants.Boolean(d.hasAllow(DataSource.ALLOW_DROP)));
                sct.setEL("grant",Constants.Boolean(d.hasAllow(DataSource.ALLOW_GRANT)));
                sct.setEL("revoke",Constants.Boolean(d.hasAllow(DataSource.ALLOW_REVOKE)));
                sct.setEL("alter",Constants.Boolean(d.hasAllow(DataSource.ALLOW_ALTER)));
    
                sct.setEL("connectionLimit",d.getConnectionLimit()<1?"":Caster.toString(d.getConnectionLimit()));
                sct.setEL("connectionTimeout",d.getConnectionTimeout()<1?"":Caster.toString(d.getConnectionTimeout()));
                sct.setEL("custom",d.getCustoms());
                sct.setEL("blob",Constants.Boolean(d.isBlob()));
                sct.setEL("clob",Constants.Boolean(d.isClob()));
                pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
                return;
            }
        }
        throw new ApplicationException("there is no datasource with name ["+name+"]");
    }
    private void doGetRemoteClient() throws PageException {
        
        String url=getString("admin",action,"url");
        RemoteClient[] clients = config.getRemoteClients();
        RemoteClient client;
        for(int i=0;i<clients.length;i++) {
        	client=clients[i];
            
            if(client.getUrl().equalsIgnoreCase(url)) {
                Struct sct=new StructImpl();
                ProxyData pd = client.getProxyData();
                sct.setEL("label",client.getLabel());
                sct.setEL("usage",client.getUsage());
                sct.setEL("securityKey",client.getSecurityKey());
                sct.setEL("adminPassword",client.getAdminPassword());
                sct.setEL("ServerUsername",client.getServerUsername());
                sct.setEL("ServerPassword",client.getServerPassword());
                sct.setEL("type",client.getType());
                sct.setEL("url",client.getUrl());
                sct.setEL("proxyServer",pd==null?"":StringUtil.toStringEmptyIfNull(pd.getServer()));
                sct.setEL("proxyUsername",pd==null?"":StringUtil.toStringEmptyIfNull(pd.getUsername()));
                sct.setEL("proxyPassword",pd==null?"":StringUtil.toStringEmptyIfNull(pd.getPassword()));
                sct.setEL("proxyPort",pd==null?"":(pd.getPort()==-1?"":Caster.toString(pd.getPort())));
                
                
                pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
                return;
            }
        }
        throw new ApplicationException("there is no remote client with url ["+url+"]");
    }
    
    private void doGetSpoolerTasks() throws PageException {
    	
    	
			SpoolerTask[] open = config.getSpoolerEngine().getOpenTasks();
			SpoolerTask[] closed = config.getSpoolerEngine().getClosedTasks();
			String v="VARCHAR";
			String d="DATE";
			QueryImpl qry=new QueryImpl(
					new String[]{"type","name","detail","id","lastExecution","nextExecution","closed","tries","exceptions","triesmax"},
					new String[]{v,v,"object",v,d,d,"boolean","int","object","int"},
					open.length+closed.length,"query");

	        int row=0;
			row=doGetRemoteClientTasks(qry,open,row);
			doGetRemoteClientTasks(qry,closed,row);
	        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    	       
    	
    }
    
    private int doGetRemoteClientTasks(QueryImpl qry, SpoolerTask[] tasks, int row) throws PageException {
    	SpoolerTask task;
		for(int i=0;i<tasks.length;i++) {
			row++;
			task=tasks[i];
			qry.setAt("type", row, task.getType());
			qry.setAt("name", row, task.subject());
			qry.setAt("detail", row, task.detail());
			qry.setAt("id", row, task.getId());//print.out("fill:"+task.getId());

			
			qry.setAt("lastExecution", row,new DateTimeImpl(pageContext,task.lastExecution(),true));
			qry.setAt("nextExecution", row,new DateTimeImpl(pageContext,task.nextExecution(),true));
			qry.setAt("closed", row,Caster.toBoolean(task.closed()));
			qry.setAt("tries", row,Caster.toDouble(task.tries()));
			qry.setAt("triesmax", row,Caster.toDouble(task.tries()));
			qry.setAt("exceptions", row,translateTime(task.getExceptions()));
			
			int triesMax=0;
			ExecutionPlan[] plans = task.getPlans();
			for(int y=0;y<plans.length;y++) {
				triesMax+=plans[y].getTries();
			}
			qry.setAt("triesmax", row,Caster.toDouble(triesMax));
		}
    	return row;
	}

	private Array translateTime(Array exp) {
		exp=(Array) exp.duplicate(true);
		Iterator it = exp.iterator();
		Struct sct;
		while(it.hasNext()) {
			sct=(Struct) it.next();
			sct.setEL("time",new DateTimeImpl(pageContext,Caster.toLongValue(sct.get("time",null),0),true));
		}
		return exp;
	}

	private void doGetRemoteClients() throws PageException {
        RemoteClient[] clients = config.getRemoteClients();
        RemoteClient client;
        ProxyData pd;
        QueryImpl qry=new QueryImpl(new String[]{"label","usage","securityKey","adminPassword","serverUsername","serverPassword","type","url",
        		"proxyServer","proxyUsername","proxyPassword","proxyPort"},clients.length,"query");
        
        int row=0;

        for(int i=0;i<clients.length;i++) {
            client=clients[i];
            pd=client.getProxyData();
            row=i+1;
            qry.setAt("label",row,client.getLabel());
            qry.setAt("usage",row,client.getUsage());
            qry.setAt("securityKey",row,client.getSecurityKey());
            qry.setAt("adminPassword",row,client.getAdminPassword());
            qry.setAt("ServerUsername",row,client.getServerUsername());
            qry.setAt("ServerPassword",row,client.getServerPassword());
            qry.setAt("type",row,client.getType());
            qry.setAt("url",row,client.getUrl());
            qry.setAt("proxyServer",row,pd==null?"":pd.getServer());
            qry.setAt("proxyUsername",row,pd==null?"":pd.getUsername());
            qry.setAt("proxyPassword",row,pd==null?"":pd.getPassword());
            qry.setAt("proxyPort",row,pd==null?"":Caster.toString(pd.getPort()));
            
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

	private void doSetCluster()  {// MUST remove this
		try {
			_doSetCluster();
		} catch (Throwable t) {
			//print.printST(t);
		}
	}
	
	private void _doSetCluster() throws PageException {
		
		Struct entries = Caster.toStruct(getObject("admin",action,"entries"));
		Struct entry;
		Key[] keys = entries.keys();
		Cluster cluster = pageContext.clusterScope();
		for(int i=0;i<keys.length;i++) {
			entry=Caster.toStruct(entries.get(keys[i]));
			
			cluster.setEntry(
				new ClusterEntryImpl(
						KeyImpl.init(Caster.toString(entry.get(KEY))),
						Caster.toSerializable(entry.get(VALUE,null),null),
						Caster.toLongValue(entry.get(TIME))
				)
			);
		}
		cluster.broadcast();
		
		//updateRemote(USAGE_CLUSTER);
	}
	
	
	private void doGetCluster() throws PageException {
		pageContext.setVariable(
				getString("admin",action,"returnVariable"),
				((PageContextImpl)pageContext).clusterScope(false)
			);
	}
	
	private void doGetToken() throws PageException {
		pageContext.setVariable(
				getString("admin",action,"returnVariable"),
				config.getSecurityToken()
			);
	}
	
	
	
	
    /**
     * @throws PageException
     * 
     */
    private void doGetDatasources() throws PageException {
        
        
        Map ds = config.getDataSourcesAsMap();
        Iterator it = ds.keySet().iterator();
        QueryImpl qry=new QueryImpl(new String[]{"name","host","classname","dsn","DsnTranslated","database","port",
                "username","password","readonly"
                ,"grant","drop","create","revoke","alter","select","delete","update","insert"
                ,"connectionLimit","connectionTimeout","clob","blob","customSettings"},ds.size(),"query");
        
        int row=0;

        while(it.hasNext()) {
            Object key=it.next();
            DataSource d=(DataSource) ds.get(key);
            row++;
            qry.setAt("name",row,key);
            qry.setAt("host",row,d.getHost());
            qry.setAt("classname",row,d.getClazz().getName());
            //qry.setAt("driverversion",row,getDriverVersion(d.getClazz())); 
            qry.setAt("dsn",row,d.getDsnOriginal());
            qry.setAt("database",row,d.getDatabase());
            qry.setAt("port",row,d.getPort()<1?"":Caster.toString(d.getPort()));
            qry.setAt("dsnTranslated",row,d.getDsnTranslated());
            qry.setAt("password",row,d.getPassword());
            qry.setAt("username",row,d.getUsername());
            qry.setAt("readonly",row,Caster.toBoolean(d.isReadOnly()));
            qry.setAt("select",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_SELECT)));
            qry.setAt("delete",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_DELETE)));
            qry.setAt("update",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_UPDATE)));
            qry.setAt("insert",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_INSERT)));
            qry.setAt("create",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_CREATE)));
            qry.setAt("insert",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_INSERT)));
            qry.setAt("drop",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_DROP)));
            qry.setAt("grant",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_GRANT)));
            qry.setAt("revoke",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_REVOKE)));
            qry.setAt("alter",row,Constants.Boolean(d.hasAllow(DataSource.ALLOW_ALTER)));

            qry.setAt("connectionLimit",row,d.getConnectionLimit()<1?"":Caster.toString(d.getConnectionLimit()));
            qry.setAt("connectionTimeout",row,d.getConnectionTimeout()<1?"":Caster.toString(d.getConnectionTimeout()));
            qry.setAt("customSettings",row,d.getCustoms());
            qry.setAt("blob",row,Constants.Boolean(d.isBlob()));
            qry.setAt("clob",row,Constants.Boolean(d.isClob()));
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }


    /**
     * @throws PageException
     * 
     */
    private void doUpdateScope() throws PageException {
        
        admin.updateScopeCascadingType(getString("admin",action,"scopeCascadingType"));
        admin.updateAllowImplicidQueryCall(getBool("admin",action,"allowImplicidQueryCall"));
        admin.updateMergeFormAndUrl(getBool("admin",action,"mergeFormAndUrl"));
        admin.updateSessionManagement(getBool("admin",action,"sessionManagement"));
        admin.updateClientManagement(getBool("admin",action,"clientManagement"));
        admin.updateDomaincookies(getBool("admin",action,"domainCookies"));
        admin.updateClientCookies(getBool("admin",action,"clientCookies"));
        //admin.updateRequestTimeout(getTimespan("admin",action,"requestTimeout"));
        admin.updateSessionTimeout(getTimespan("admin",action,"sessionTimeout"));
        admin.updateApplicationTimeout(getTimespan("admin",action,"applicationTimeout"));
        admin.updateSessionType(getString("admin",action,"sessionType"));
        admin.updateLocalMode(getString("admin",action,"localMode"));
        store();
        adminSync.broadcast(attributes, config);
    }

    private void doUpdateApplicationSettings() throws PageException {
        admin.updateRequestTimeout(getTimespan("admin",action,"requestTimeout"));
    	admin.updateScriptProtect(getString("admin",action,"scriptProtect"));
    	admin.updateAllowURLRequestTimeout(getBool("admin",action,"allowURLRequestTimeout")); // DIFF 23
        store();
        adminSync.broadcast(attributes, config);
    }

    private void doUpdateOutputSettings() throws PageException {
        admin.updateSupressWhitespace(getBool("admin",action, "supressWhitespace"));
        admin.updateShowVersion(getBool("admin",action, "showVersion"));
        store();
        adminSync.broadcast(attributes, config);
    }
    
    private void doUpdateCustomTagSetting() throws PageException {
    	admin.updateCustomTagDeepSearch(getBool("admin", action, "deepSearch"));
    	admin.updateCustomTagLocalSearch(getBool("admin", action, "localSearch"));
    	admin.updateCustomTagExtensions(getString("admin", action, "extensions"));
        store();
        adminSync.broadcast(attributes, config);
    }
    
    private void doUpdateExtension() throws PageException {
    	
    		
			admin.updateExtension(new ExtensionImpl(
						getStruct("config", null),
			    		getString("admin", "UpdateExtensions", "id"),
			    		getString("admin", "UpdateExtensions","provider"),
			    		getString("admin", "UpdateExtensions","version"),
			    		
			    		getString("admin", "UpdateExtensions","name"),
			    		getString("admin", "UpdateExtensions","label"),
			    		getString("admin", "UpdateExtensions","description"),
			    		getString("admin", "UpdateExtensions","category"),
			    		getString("admin", "UpdateExtensions","image"),
			    		getString("admin", "UpdateExtensions","author"),
			    		getString("admin", "UpdateExtensions","codename"),
			    		getString("admin", "UpdateExtensions","video"),
			    		getString("admin", "UpdateExtensions","support"),
			    		getString("admin", "UpdateExtensions","documentation"),
			    		getString("admin", "UpdateExtensions","forum"),
			    		getString("admin", "UpdateExtensions","mailinglist"),
			    		getString("admin", "UpdateExtensions","network"),
			    		getDateTime("created",null),
			    		getString("admin", "UpdateExtensions","_type")
			    	));
		
    	

    	
    	store();
        //adminSync.broadcast(attributes, config);
    }
    
    private void doUpdateExtensionProvider() throws PageException {
    	admin.updateExtensionProvider(
    			getString("admin", "UpdateExtensionProvider","url")
    			);
    	store();
    }
    
    private void doUpdateExtensionInfo() throws PageException {
    	admin.updateExtensionInfo(
    			getBool("admin", "UpdateExtensionInfo","enabled")
    			);
    	store();
    }

    private void doVerifyExtensionProvider() throws PageException {
    	admin.verifyExtensionProvider(getString("admin", "VerifyExtensionProvider","url"));
		
    }
    private void doResetId() throws PageException {
    	admin.resetId();
    	store();
		
    }
    
    private void doRemoveExtensionProvider() throws PageException {
    	admin.removeExtensionProvider(getString("admin", "RemoveExtensionProvider","url"));
    	store();
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetApplicationSetting() throws PageException {
        
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("scriptProtect",ApplicationContextImpl.translateScriptProtect(config.getScriptProtect()));
        
        // request timeout
        sct.set("requestTimeout",config.getRequestTimeout());
        sct.set("requestTimeout_day",Caster.toInteger(config.getRequestTimeout().getDay()));
        sct.set("requestTimeout_hour",Caster.toInteger(config.getRequestTimeout().getHour()));
        sct.set("requestTimeout_minute",Caster.toInteger(config.getRequestTimeout().getMinute()));
        sct.set("requestTimeout_second",Caster.toInteger(config.getRequestTimeout().getSecond()));
        
        // AllowURLRequestTimeout
        sct.set("AllowURLRequestTimeout",Caster.toBoolean(config.isAllowURLRequestTimeout()));// DIF 23
    }
    
    private void doGetOutputSetting() throws PageException {
        
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("supressWhitespace",Caster.toBoolean(config.isSuppressWhitespace()));
        sct.set("showVersion",Caster.toBoolean(config.isShowVersion()));
        
        
    }
    
    /**
     * @throws PageException
     * 
     */
    private void doGetScope() throws PageException {
        String sessionType=config.getSessionType()==Config.SESSION_TYPE_J2EE?"j2ee":"cfml";
        String localMode="update";
        if(config.getLocalMode()==Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS)localMode="always";
        
        
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("allowImplicidQueryCall",Caster.toBoolean(config.allowImplicidQueryCall()));
        sct.set("mergeFormAndUrl",Caster.toBoolean(config.mergeFormAndURL()));
        
        sct.set("sessiontype",sessionType);
        sct.set("localmode",localMode);
        sct.set("sessionManagement",Caster.toBoolean(config.isSessionManagement()));
        sct.set("clientManagement",Caster.toBoolean(config.isClientManagement()));
        sct.set("domainCookies",Caster.toBoolean(config.isDomainCookies()));
        sct.set("clientCookies",Caster.toBoolean(config.isClientCookies()));

        sct.set("sessionTimeout",config.getSessionTimeout());
        sct.set("sessionTimeout_day",Caster.toInteger(config.getSessionTimeout().getDay()));
        sct.set("sessionTimeout_hour",Caster.toInteger(config.getSessionTimeout().getHour()));
        sct.set("sessionTimeout_minute",Caster.toInteger(config.getSessionTimeout().getMinute()));
        sct.set("sessionTimeout_second",Caster.toInteger(config.getSessionTimeout().getSecond()));

        sct.set("applicationTimeout",config.getApplicationTimeout());
        sct.set("applicationTimeout_day",Caster.toInteger(config.getApplicationTimeout().getDay()));
        sct.set("applicationTimeout_hour",Caster.toInteger(config.getApplicationTimeout().getHour()));
        sct.set("applicationTimeout_minute",Caster.toInteger(config.getApplicationTimeout().getMinute()));
        sct.set("applicationTimeout_second",Caster.toInteger(config.getApplicationTimeout().getSecond()));
        
        
        // scope cascading type
        if(config.getScopeCascadingType()==Config.SCOPE_STRICT) sct.set("scopeCascadingType","strict");
        else if(config.getScopeCascadingType()==Config.SCOPE_SMALL) sct.set("scopeCascadingType","small");
        else if(config.getScopeCascadingType()==Config.SCOPE_STANDARD) sct.set("scopeCascadingType","standard");
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateComponent() throws PageException {
        admin.updateBaseComponent(getString("admin",action,"baseComponentTemplate"));
        admin.updateComponentDumpTemplate(getString("admin",action,"componentDumpTemplate"));
        admin.updateComponentDataMemberDefaultAccess(ComponentUtil.toIntAccess(getString("admin",action,"componentDataMemberDefaultAccess")));
        admin.updateTriggerDataMember(getBool("admin",action,"triggerDataMember"));
        admin.updateComponentUseShadow(getBool("admin",action,"useShadow"));
        store();
        adminSync.broadcast(attributes, config);
    }

    /**
     * @throws PageException 
     * @throws PageException
     * 
     */
    private void doGetComponent() throws PageException {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        // Base Component
        try {
            PageSource ps = pageContext.getPageSource(config.getBaseComponentTemplate());
            if(ps.exists()) sct.set("baseComponentTemplate",ps.getDisplayPath());
            else sct.set("baseComponentTemplate","");
        } catch (PageException e) {
            sct.set("baseComponentTemplate","");
        }
        sct.set("strBaseComponentTemplate",config.getBaseComponentTemplate());
        
        // dump template
        try {
            PageSource ps = pageContext.getPageSource(config.getComponentDumpTemplate());
            if(ps.exists()) sct.set("componentDumpTemplate",ps.getDisplayPath());
            else sct.set("componentDumpTemplate","");
        } catch (PageException e) {
            sct.set("componentDumpTemplate","");
        }
        sct.set("strComponentDumpTemplate",config.getComponentDumpTemplate());

        sct.set("componentDataMemberDefaultAccess",ComponentUtil.toStringAccess(config.getComponentDataMemberDefaultAccess()));
        sct.set("triggerDataMember",Caster.toBoolean(config.getTriggerComponentDataMember()));
        sct.set("useShadow",Caster.toBoolean(config.useComponentShadow()));
    }

    /**
     * @throws PageException
     * 
     */
    private void doUpdateRegional() throws PageException {
        try{
        	admin.updateLocale(getString("admin",action,"locale"));
	    	admin.updateTimeZone(getString("admin",action,"timezone"));
	    	admin.updateTimeServer(getString("admin",action,"timeserver"));
	    	admin.updateTimeZone(getString("admin",action,"timezone"));
        }
        finally {
        	 store();
        }
        adminSync.broadcast(attributes, config);
    }

    private void doUpdateTLD() throws PageException {
    	try {
    		String jar = getString("jar",null);
    		if(!StringUtil.isEmpty(jar,true)){
	    		Resource resJar = ResourceUtil.toResourceExisting(pageContext, jar);
	    		admin.updateJar(resJar);
    		}
    		Resource resTld = ResourceUtil.toResourceExisting(pageContext, getString("admin",action,"tld"));
        	admin.updateTLD(resTld);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }
    private void doUpdateFLD() throws PageException {
    	try {
    		String jar = getString("jar",null);
    		if(!StringUtil.isEmpty(jar,true)){
	    		Resource resJar = ResourceUtil.toResourceExisting(pageContext, jar);
	    		admin.updateJar(resJar);
    		}
    		Resource resFld = ResourceUtil.toResourceExisting(pageContext, getString("admin",action,"fld"));
        	admin.updateFLD(resFld);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }
    
    private void doUpdateJar() throws PageException {
    	try {
    		Resource resJar = ResourceUtil.toResourceExisting(pageContext, getString("admin",action,"jar"));
    		admin.updateJar(resJar);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }

    private void doRemoveJar() throws PageException {
    	try {
    		String name = getString("jar",null);
    		if(StringUtil.isEmpty(name))name=getString("admin",action,"name");
    		
    		admin.removeJar(name);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }
    private void doRemoveTLD() throws PageException {
    	try {
    		String name = getString("tld",null);
    		if(StringUtil.isEmpty(name))name=getString("admin",action,"name");
    		admin.removeTLD(name);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }
    

    
    private void doRemoveFLD() throws PageException {
    	try {
    		String name = getString("fld",null);
    		if(StringUtil.isEmpty(name))name=getString("admin",action,"name");
    		admin.removeFLD(name);
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
        store();
    }
    
    private void doUpdateRemoteClient() throws PageException {
        
    	
    	
    	admin.updateRemoteClient(
        		getString("admin",action,"label"),
        		getString("admin",action,"url"),
        		getString("admin",action,"remotetype"),
        		getString("admin",action,"securityKey"),
        		getString("admin",action,"usage"),
        		getString("admin",action,"adminPassword"),
        		getString("ServerUsername",""),
        		getString("ServerPassword",""),
        		getString("proxyServer",""),
        		getString("proxyUsername",""),
        		getString("proxyPassword",""),
        		getString("proxyPort","")
        		
        );
        
        store();
    }
    private void doUpdateRemoteClientUsage() throws PageException {
        admin.updateRemoteClientUsage(
     		   getString("admin",action,"code"),
     		   getString("admin",action,"displayname")
         		
         );
         store();
     }
    private void doRemoveRemoteClientUsage() throws PageException {
        admin.removeRemoteClientUsage(
     		   getString("admin",action,"code")
         		
         );
         store();
     }
    
    /*private void updateRemote(String usage) throws PageException {
    	RemoteClient[] clients=config.getRemoteClients();
		if(clients.length==0) return;
    	
		long start=System.currentTimeMillis();
	    
    	// create attribute collection struct
    	Struct attrColl=(Struct) attributes.duplicate(false);
    	attrColl.removeEL(PASSWORD);
    	attrColl.removeEL(TYPE);
    	attrColl.removeEL(RETURN_VARIABLE);
    	
    	String callerId;
    	String providedCallerId=(String) attrColl.removeEL(PROVIDED_CALLER_IDS);
    	if(StringUtil.isEmpty(providedCallerId))providedCallerId=(String) attrColl.removeEL(CALLER_ID);
    	Set setProvidedCallerId=null;
    	print.out(System.currentTimeMillis()-start);
	    
    	try{
	    	if(StringUtil.isEmpty(providedCallerId))callerId=getCallerId();
	    	else {
	    		callerId=providedCallerId+","+getCallerId();
	    		setProvidedCallerId = List.listToSet(providedCallerId, ",",true);
	    	}
    	}
    	catch(IOException ioe){
    		throw Caster.toPageException(ioe);
    	}
    	if(setProvidedCallerId==null)setProvidedCallerId=new HashSet();
    	print.out(System.currentTimeMillis()-start);
	    
    	if(setProvidedCallerId.isEmpty())return;
    		
    	SpoolerEngine spoolerEngine= config.getSpoolerEngine();
		String cid;
    	for(int i=0;i<clients.length;i++) {
    		if(clients[i].hasUsage(usage)){
    			cid=clients[i].getId(config);
	    		if(!StringUtil.isEmpty(cid) && !setProvidedCallerId.contains(cid))	{
	    			spoolerEngine.add(
    					new RemoteClientTask(
    							new ExecutionPlan[]{new ExecutionPlanImpl(3,5),new ExecutionPlanImpl(3,60),new ExecutionPlanImpl(24,60*60)},
    							clients[i],
    							attrColl,
    							callerId+getSisterCallerId(config,clients,clients[i]),
    							"synchronisation"));
	    		}
    		}
    		print.out(clients[i].getLabel()+":"+(System.currentTimeMillis()-start));
    	}
    	print.out((System.currentTimeMillis()-start));
	}*/


	/*public static String getSisterCallerId(Config config,RemoteClient[] others, RemoteClient client) {
		StringBuffer sb=new StringBuffer();
		String id;
		for(int i=0;i<others.length;i++) {
			if(others[i]==client) continue;
			id=others[i].getId(config);
			if(StringUtil.isEmpty(id)) continue;
			
    		sb.append(',');
    		sb.append(id);
    	}
    	return sb.toString();
	}*/

	private String getCallerId() throws IOException {
		if(type==TYPE_WEB) {
			return config.getId();
			//return GetRailoId.createId(config.getId(), config.getConfigDir());
		}
		if(config instanceof ConfigWebImpl){
			ConfigWebImpl cwi = (ConfigWebImpl)config;
			return cwi.getServerId();
			//return GetRailoId.createId(cwi.getServerId(), cwi.getServerConfigDir());
		}
		if(config instanceof ConfigServer){
			return config.getId();
			//return GetRailoId.createId(config.getId(), config.getConfigDir());
		}
		throw new IOException("can not create id");
	}

	private void doUpdateApplicationListener() throws PageException {
        admin.updateApplicationListener(
        		getString("admin",action,"listenerType"),
        		getString("admin",action,"listenerMode")
        );
       
        store();
        adminSync.broadcast(attributes, config);
    }
    
    
    
    private void doUpdateProxy() throws PageException {
        admin.updateProxy(
        		getBool("admin",action,"proxyenabled"), 
        		getString("admin",action,"proxyserver"), 
        		getInt("admin",action,"proxyport"), 
        		getString("admin",action,"proxyusername"), 
        		getString("admin",action,"proxypassword")
        	);
        store();
    }
    

	private void doUpdateCharset() throws PageException {
        admin.updateResourceCharset(getString("admin",action,"resourceCharset"));
        admin.updateTemplateCharset(getString("admin",action,"templateCharset"));
        admin.updateWebCharset(getString("admin",action,"webCharset"));
		store();
        adminSync.broadcast(attributes, config);
	}

    /**
     * @throws PageException
     * 
     */
    private void doSecurityManager() throws PageException {
        String rtnVar = getString("admin",action,"returnVariable");
    	String secType = getString("admin",action,"sectype");
    	String secValue = getString("secvalue",null);
    	boolean isServer=config instanceof ConfigServer;
    	
    	if(secValue==null) {
    		if(isServer)	{
    			pageContext.setVariable(
    	                rtnVar,
    	                SecurityManagerImpl.toStringAccessValue(SecurityManager.VALUE_YES)
    	        );
    		}
    		else {
    			pageContext.setVariable(
    	                rtnVar,
    	                SecurityManagerImpl.toStringAccessValue(config.getSecurityManager().getAccess(secType))
    	        );
    		}
    		return;
    	}
        pageContext.setVariable(
                rtnVar,
                Caster.toBoolean(
                        isServer ||
                        config.getSecurityManager().getAccess(secType) == SecurityManagerImpl.toShortAccessValue(secValue)
                )
        );
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetTimeZones() throws PageException {
        
        
        
        String strLocale=getString("locale","english (united kingdom)");
        Locale locale = LocaleFactory.getLocale(strLocale);
        
        String[] timeZones = TimeZone.getAvailableIDs();
        QueryImpl qry=new QueryImpl(new String[]{"id","display"},new String[]{"varchar","varchar"},timeZones.length,"timezones");
        
        TimeZone timeZone;
        for(int i=0;i<timeZones.length;i++) {
            timeZone=TimeZone.getTimeZone(timeZones[i]);
            qry.setAt("id",i+1,timeZones[i]);
            qry.setAt("display",i+1,timeZone.getDisplayName(locale));
            
        }
        pageContext.setVariable(getString("admin",action,"returnVariable"),qry);
    }

    /**
     * @throws PageException
     * 
     */
    private void doGetLocales() throws PageException {
        Struct sct=new StructImpl(StructImpl.TYPE_LINKED);
        //Array arr=new ArrayImpl();
        String strLocale=getString("locale","english (united kingdom)");
        Locale locale = LocaleFactory.getLocale(strLocale);
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        Map locales = LocaleFactory.getLocales();
        Iterator it = locales.keySet().iterator();
       
        String key;
        Locale l;
        while(it.hasNext()) {
            key=(String)it.next();
            l=(Locale) locales.get(key);
            sct.setEL(l.toString(),l.getDisplayName(locale));
            //arr.append(locale.getDisplayName());
        }
        //arr.sort("textnocase","asc");
    }
    
    
    private void doGetApplicationListener() throws PageException  {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        ApplicationListener appListener = config.getApplicationListener();
        sct.set("type",AppListenerUtil.toStringType(appListener));
        sct.set("mode",AppListenerUtil.toStringMode(appListener.getMode()));
		// replaced with encoding outputsct.set("defaultencoding", config.get DefaultEncoding());
    }
    /**
     * @throws PageException
     * 
     */
    private void doGetRegional() throws PageException  {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("locale",Caster.toString(config.getLocale()));
        sct.set("timezone",pageContext.getTimeZone().getID());
        sct.set("timeserver",config.getTimeServer());
		// replaced with encoding outputsct.set("defaultencoding", config.get DefaultEncoding());
    }
    

	private void doSurveillance() throws PageException {
		pageContext.setVariable(getString("admin",action,"returnVariable"),Surveillance.getInfo(config));//31
	}
    
    private void doGetProxy() throws PageException  {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        //sct.set("enabled",Caster.toBoolean(config.isProxyEnable()));
        sct.set("port",Caster.toString(config.getProxyPort()));
        sct.set("server",emptyIfNull(config.getProxyServer()));
        sct.set("username",emptyIfNull(config.getProxyUsername()));
        sct.set("password",emptyIfNull(config.getProxyPassword()));
    }


	private void doGetCharset() throws PageException {
		Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        sct.set("resourceCharset",config.getResourceCharset());
        sct.set("templateCharset",config.getTemplateCharset());
        sct.set("webCharset",config.getWebCharset());
	}

    /**
     * @throws PageException
     * 
     */
    private void doGetUpdate() throws PageException  {
        Struct sct=new StructImpl();
        pageContext.setVariable(getString("admin",action,"returnVariable"),sct);
        URL location = config.getUpdateLocation();
        if(location==null) {
            try {
                location=new URL("http://www.getrailo.org");
            } 
            catch (MalformedURLException e) {}
        }
        String type=config.getUpdateType();
        if(StringUtil.isEmpty(type))type="manual";
        
        sct.set("location",location.toExternalForm());
        sct.set("type",type);
        
    }

    /**
     * @throws PageException
     * 
     */
    private void store() throws PageException {
        try {
            admin.store();
        } catch (Exception e) {
            throw Caster.toPageException(e);
        } 
    }

    private String getString(String tagName, String actionName, String attributeName) throws ApplicationException {
        String value=getString(attributeName,null);
        if(value==null) throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return value;
    }

    private double getDouble(String tagName, String actionName, String attributeName) throws ApplicationException {
        double value=getDouble(attributeName,Double.NaN);
        if(Double.isNaN(value)) throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return value;
    }

    private String getString(String attributeName, String defaultValue)  {
        Object value=attributes.get(attributeName,null);
        if(value==null)return defaultValue;
        return Caster.toString(value,null);
    }

    private DateTime getDateTime(String attributeName, DateTime defaultValue)  {
        Object value=attributes.get(attributeName,null);
        if(value==null)return defaultValue;
        return DateCaster.toDateAdvanced(value, null, defaultValue);
    }
    
    private Object getObject(String attributeName, Object defaultValue)  {
        return attributes.get(attributeName,defaultValue);
    }

    private boolean getBool(String tagName, String actionName, String attributeName) throws PageException {
        Object value=attributes.get(attributeName,null);
        if(value==null)
            throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return Caster.toBooleanValue(value);
    }
    
    private Object getObject(String tagName, String actionName, String attributeName) throws PageException {
        Object value=attributes.get(attributeName,null);
        if(value==null)
            throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return value;
    }

    private boolean getBool(String attributeName, boolean defaultValue) {
        Object value=attributes.get(attributeName,null);
        if(value==null) return defaultValue;
        return Caster.toBooleanValue(value,defaultValue);
    }
    
    private Struct getStruct(String attributeName, Struct defaultValue) {
        Object value=attributes.get(attributeName,null);
        if(value==null) return defaultValue;
        try {
            return Caster.toStruct(value);
        } catch (PageException e) {
            return defaultValue;
        }
    }
   
    private int getInt(String tagName, String actionName, String attributeName) throws PageException {
        Object value=attributes.get(attributeName,null);
        if(value==null)
            throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return (int)Caster.toDoubleValue(value);
    }
    
    private int getInt(String attributeName, int defaultValue) {
        Object value=attributes.get(attributeName,null);
        if(value==null) return defaultValue;
        return (int)Caster.toDoubleValue(value,defaultValue);
    }
    
    private double getDouble(String attributeName, double defaultValue) {
        Object value=attributes.get(attributeName,null);
        if(value==null) return defaultValue;
        return Caster.toDoubleValue(value,defaultValue);
    }
    
    private TimeSpan getTimespan(String tagName, String actionName, String attributeName) throws PageException {
        Object value=attributes.get(attributeName,null);
        if(value==null)
            throw new ApplicationException("Attribute ["+attributeName+"] for tag ["+tagName+"] is required if attribute action has the value ["+actionName+"]");
        return Caster.toTimespan(value);
    }

	private Object emptyIfNull(String str) {
		if(str==null) return "";
		return str;
	}

    private void throwNoAccessWhenWeb() throws ApplicationException {
        if(type==TYPE_WEB)throw new ApplicationException(
                "you have no access for action [web."+action+"]");
    }

    private void throwNoAccessWhenServer() throws ApplicationException {
        if(type==TYPE_SERVER) {
            throw new ApplicationException(
                    "you have no access for action [server."+action+"]");
        }
    }
}





final class PluginFilter implements ResourceFilter {
	/**
     *
     * @see railo.commons.io.res.filter.ResourceFilter#accept(railo.commons.io.res.Resource)
     */
	public boolean accept(Resource res) {
    	return doAccept(res);
    }
	
	public static boolean doAccept(Resource res) {
    	return res.isDirectory() && res.getRealResource("/Action.cfc").isFile() && res.getRealResource("/language.xml").isFile();
    }

}