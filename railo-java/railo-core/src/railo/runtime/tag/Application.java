package railo.runtime.tag;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.listener.ClassicApplicationContext;
import railo.runtime.listener.ModernApplicationContext;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Scope;

/**
* Defines scoping for a CFML application, enables or disables storing client variables, 
* 			and specifies a client variable storage mechanism. 
* 			By default, client variables are disabled. Also, enables session variables and sets timeouts 
* 			for session and application variables. Session and application variables are stored in memory.
*
*
*
**/
public final class Application extends TagImpl {

	

	private static final int ACTION_CREATE = 0;
	private static final int ACTION_UPDATE = 1;
    
	private Boolean setClientCookies;
	private Boolean setDomainCookies;
	private Boolean setSessionManagement;
	private String clientstorage;
	private String sessionstorage;
	private Boolean setClientManagement;
	private TimeSpan sessionTimeout;
	private TimeSpan clientTimeout;
	private TimeSpan applicationTimeout;
	private Mapping[] mappings;
	private Mapping[] customTagMappings;
	private Mapping[] componentMappings;
	private String secureJsonPrefix;
	private Boolean bufferOutput;
	private Boolean secureJson;
	private String scriptrotect;
	private Object datasource;
	private Object defaultdatasource;
	private int loginstorage=Scope.SCOPE_UNDEFINED;
	
	//ApplicationContextImpl appContext;
    private String name="";
	private int action=ACTION_CREATE;
	private int localMode=-1;
	private Locale locale;
	private TimeZone timeZone;
	private short sessionType=-1;
	private boolean sessionCluster;
	private boolean clientCluster;

	private boolean ormenabled;
	private Struct ormsettings;
	private Struct s3;
	
	private Boolean triggerDataMember=null;
	private String cacheFunction;
	private String cacheQuery;
	private String cacheTemplate;
	private String cacheObject;
	private String cacheResource;
	private Struct datasources;
	private UDF onmissingtemplate;
	
     
    @Override
    public void release() {
        super.release();
        setClientCookies=null;
        setDomainCookies=null;
        setSessionManagement=null;
        clientstorage=null;
        sessionstorage=null;
        setClientManagement=null;
        sessionTimeout=null;
        clientTimeout=null;
        applicationTimeout=null;
        mappings=null;
        customTagMappings=null;
        componentMappings=null;
        bufferOutput=null;
        secureJson=null;
        secureJsonPrefix=null;
        loginstorage=Scope.SCOPE_UNDEFINED;
        scriptrotect=null;
        datasource=null;
        defaultdatasource=null;
        datasources=null;
        this.name="";
        action=ACTION_CREATE;
        localMode=-1;
        locale=null;
        timeZone=null;
        sessionType=-1;
        sessionCluster=false;
        clientCluster=false;
        
        ormenabled=false;
        ormsettings=null;
        s3=null;
        //appContext=null;
        
        triggerDataMember=null;

    	cacheFunction=null;
    	cacheQuery=null;
    	cacheTemplate=null;
    	cacheObject=null;
    	cacheResource=null;
    	onmissingtemplate=null;
    }
    
    /** set the value setclientcookies
	*  Yes or No. Yes enables client cookies. Default is Yes. If you set this attribute to 
	* 		"No", CFML does not automatically send the CFID and CFTOKEN cookies to the client browser;
	* 		you must manually code CFID and CFTOKEN on the URL for every page that uses Session or Client variables.
	* @param setClientCookies value to set
	**/
	public void setSetclientcookies(boolean setClientCookies)	{
		this.setClientCookies=setClientCookies?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSetClientCookies(setClientCookies);
	}

    /** set the value setdomaincookies
	*  Yes or No. Sets the CFID and CFTOKEN cookies for a domain, not just a single host. 
	* 		Applications that are running on clusters must set this value to Yes. The default is No.
	* @param setDomainCookies value to set
	**/
	public void setSetdomaincookies(boolean setDomainCookies)	{
		this.setDomainCookies=setDomainCookies?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSetDomainCookies(setDomainCookies);
	}

	/** set the value sessionmanagement
	*  Yes or No. Yes enables session variables. Default is No.
	* @param setSessionManagement value to set
	**/
	public void setSessionmanagement(boolean setSessionManagement)	{
		this.setSessionManagement=setSessionManagement?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSetSessionManagement(setSessionManagement);
	}

    
	/**
	 * @param datasource the datasource to set
	 * @throws PageException 
	 */
	public void setDatasource(Object datasource) throws PageException {
		this.datasource = ModernApplicationContext.toDefaultDatasource(datasource);
	}
	
	public void setDefaultdatasource(Object defaultdatasource) throws PageException {
		this.defaultdatasource =  ModernApplicationContext.toDefaultDatasource(defaultdatasource);
	}
	
	public void setDatasources(Struct datasources) {
		this.datasources = datasources;
	}
	
	public void setLocalmode(String strLocalMode) throws ApplicationException {
		this.localMode = AppListenerUtil.toLocalMode(strLocalMode);
		
	}
	
	public void setTimezone(String strTimeZone) throws ExpressionException {
		if(StringUtil.isEmpty(strTimeZone)) return;
		this.timeZone = TimeZoneUtil.toTimeZone(strTimeZone);
		
	}
	
	public void setLocale(String strLocale) throws ExpressionException {
		if(StringUtil.isEmpty(strLocale)) return;
		this.locale = LocaleFactory.getLocale(strLocale);
		
	}

	/** set the value clientstorage
	*  Specifies how Railo stores client variables
	* @param clientstorage value to set
	**/
	public void setClientstorage(String clientstorage)	{
		this.clientstorage=clientstorage;
	}

	public void setSessionstorage(String sessionstorage)	{
		this.sessionstorage=sessionstorage;
	}

	/** set the value clientmanagement
	*  Yes or No. Enables client variables. Default is No.
	* @param setClientManagement value to set
	**/
	public void setClientmanagement(boolean setClientManagement)	{
		this.setClientManagement=setClientManagement?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSetClientManagement(setClientManagement);
	}

	/** set the value sessiontimeout
	*  Enter the CreateTimeSpan function and values in days, hours, minutes, and seconds, separated 
	* 		by commas, to specify the lifespan of session variables.
	* @param sessionTimeout value to set
	**/
	public void setSessiontimeout(TimeSpan sessionTimeout)	{
		this.sessionTimeout=sessionTimeout;
	}
	public void setSessiontype(String sessionType) throws ApplicationException	{
		this.sessionType=AppListenerUtil.toSessionType(sessionType);
	}
	public void setClientcluster(boolean clientCluster) {
		this.clientCluster=clientCluster;
	}
	public void setSessioncluster(boolean sessionCluster) {
		this.sessionCluster=sessionCluster;
	}
	
	public void setClienttimeout(TimeSpan clientTimeout)	{
		this.clientTimeout=clientTimeout;
	}
	

	public void setCachefunction(String cacheFunction)	{
		if(StringUtil.isEmpty(cacheFunction,true)) return;
		this.cacheFunction=cacheFunction.trim();
	}
	public void setCachequery(String cacheQuery)	{
		if(StringUtil.isEmpty(cacheQuery,true)) return;
		this.cacheQuery=cacheQuery.trim();
	}
	public void setCachetemplate(String cacheTemplate)	{
		if(StringUtil.isEmpty(cacheTemplate,true)) return;
		this.cacheTemplate=cacheTemplate.trim();
	}
	public void setCacheobject(String cacheObject)	{
		if(StringUtil.isEmpty(cacheObject,true)) return;
		this.cacheObject=cacheObject.trim();
	}
	public void setCacheresource(String cacheResource)	{
		if(StringUtil.isEmpty(cacheResource,true)) return;
		this.cacheResource=cacheResource.trim();
	}
	

	public void setTriggerdatamember(boolean triggerDataMember)	{
		this.triggerDataMember=triggerDataMember?Boolean.TRUE:Boolean.FALSE;
	}
	public void setInvokeimplicitaccessor(boolean invokeimplicitaccessor)	{
		setTriggerdatamember(invokeimplicitaccessor);
	}

	/**
	 * @param ormenabled the ormenabled to set
	 */
	public void setOrmenabled(boolean ormenabled) {
		this.ormenabled = ormenabled;
	}

	/**
	 * @param ormsettings the ormsettings to set
	 */
	public void setOrmsettings(Struct ormsettings) {
		this.ormsettings = ormsettings;
	}

	/**
	 * @param s3 the s3 to set
	 */
	public void setS3(Struct s3) {
		this.s3 = s3;
	}

	/** set the value applicationtimeout
	*  Enter the CreateTimeSpan function and values in days, hours, minutes, and seconds, separated 
	* 		by commas, to specify the lifespan of application variables. 
	* @param applicationTimeout value to set
	**/
	public void setApplicationtimeout(TimeSpan applicationTimeout)	{
		this.applicationTimeout=applicationTimeout;
	    //getAppContext().setApplicationTimeout(applicationTimeout);
	}

	/** set the value name
	*  The name of your application. This name can be up to 64 characters long.
	*    		Required for application and session variables, optional for client variables
	* @param name value to set
	**/
	public void setName(String name)	{
	    this.name=name;
	}
	
	public void setAction(String strAction) throws ApplicationException	{
		strAction=strAction.toLowerCase();
        if(strAction.equals("create"))action=ACTION_CREATE;
        else if(strAction.equals("update")) action=ACTION_UPDATE;
        else throw new ApplicationException("invalid action definition ["+strAction+"] for tag application, valid values are [create,update]");
    
	}
	
	public void setMappings(Struct mappings) throws PageException	{
	    this.mappings=AppListenerUtil.toMappings(pageContext.getConfig(), mappings,getSource());
		//getAppContext().setMappings(AppListenerUtil.toMappings(pageContext, mappings));
	}
	
	public void setCustomtagpaths(Object mappings) throws PageException	{
	    this.customTagMappings=AppListenerUtil.toCustomTagMappings(pageContext.getConfig(), mappings);
	}
	
	public void setComponentpaths(Object mappings) throws PageException	{
	    this.componentMappings=AppListenerUtil.toCustomTagMappings(pageContext.getConfig(), mappings);
		//getAppContext().setCustomTagMappings(AppListenerUtil.toCustomTagMappings(pageContext, mappings));
	}
	

	public void setSecurejsonprefix(String secureJsonPrefix) 	{
		this.secureJsonPrefix=secureJsonPrefix;
	    //getAppContext().setSecureJsonPrefix(secureJsonPrefix);
	}
	public void setSecurejson(boolean secureJson) 	{
		this.secureJson=secureJson?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSecureJson(secureJson);
	}
	public void setBufferoutput(boolean bufferOutput) 	{
		this.bufferOutput=bufferOutput?Boolean.TRUE:Boolean.FALSE;
	    //getAppContext().setSecureJson(secureJson);
	}
	
    /**
     * @param loginstorage The loginstorage to set.
     * @throws ApplicationException
     */
    public void setLoginstorage(String loginstorage) throws ApplicationException {
        loginstorage=loginstorage.toLowerCase();
        if(loginstorage.equals("session"))this.loginstorage=Scope.SCOPE_SESSION;
        else if(loginstorage.equals("cookie"))this.loginstorage=Scope.SCOPE_COOKIE;
        else throw new ApplicationException("invalid loginStorage definition ["+loginstorage+"] for tag application, valid values are [session,cookie]");
    }
	/**
	 * @param scriptrotect the scriptrotect to set
	 */			
	public void setScriptprotect(String strScriptrotect) {
		this.scriptrotect=strScriptrotect;
		//getAppContext().setScriptProtect(strScriptrotect);
	}

	public void setOnmissingtemplate(Object oUDF) throws PageException {
		this.onmissingtemplate=Caster.toFunction(oUDF);
	}

	@Override
	public int doStartTag() throws PageException	{
        
        ApplicationContextPro ac;
        boolean initORM;
        if(action==ACTION_CREATE){
        	ac=new ClassicApplicationContext(pageContext.getConfig(),name,false,
        			pageContext.getCurrentPageSource().getResourceTranslated(pageContext));
        	initORM=set(ac);
        	pageContext.setApplicationContext(ac);
        }
        else {
        	ac=(ApplicationContextPro) pageContext.getApplicationContext();
        	initORM=set(ac);
        }
        
        if(initORM) ORMUtil.resetEngine(pageContext,false);
        
        return SKIP_BODY; 
	}

	private Resource getSource() throws PageException {
		return ResourceUtil.getResource(pageContext,pageContext.getCurrentPageSource());
	}

	private boolean set(ApplicationContextPro ac) throws PageException {
		if(applicationTimeout!=null)			ac.setApplicationTimeout(applicationTimeout);
		if(sessionTimeout!=null)				ac.setSessionTimeout(sessionTimeout);
		if(clientTimeout!=null)				ac.setClientTimeout(clientTimeout);
		if(clientstorage!=null)	{
			ac.setClientstorage(clientstorage);
		}
		if(sessionstorage!=null)	{
			ac.setSessionstorage(sessionstorage);
		}
		if(customTagMappings!=null)				ac.setCustomTagMappings(customTagMappings);
		if(componentMappings!=null)				ac.setComponentMappings(componentMappings);
		if(mappings!=null)						ac.setMappings(mappings);
		if(loginstorage!=Scope.SCOPE_UNDEFINED)	ac.setLoginStorage(loginstorage);
		if(!StringUtil.isEmpty(datasource))		{
			ac.setDefDataSource(datasource);
			ac.setORMDataSource(datasource);
		}
		if(!StringUtil.isEmpty(defaultdatasource))ac.setDefDataSource(defaultdatasource);
		if(datasources!=null){
			try {
				ac.setDataSources(AppListenerUtil.toDataSources(datasources));
			} 
			catch (ClassException e) {
				throw Caster.toPageException(e);
			}
		}
		
		if(onmissingtemplate!=null && ac instanceof ClassicApplicationContext){
			((ClassicApplicationContext)ac).setOnMissingTemplate(onmissingtemplate);
		}
		
		
		if(scriptrotect!=null)					ac.setScriptProtect(AppListenerUtil.translateScriptProtect(scriptrotect));
		if(bufferOutput!=null)					ac.setBufferOutput(bufferOutput.booleanValue());
		if(secureJson!=null)					ac.setSecureJson(secureJson.booleanValue());
		if(secureJsonPrefix!=null)				ac.setSecureJsonPrefix(secureJsonPrefix);
		if(setClientCookies!=null)				ac.setSetClientCookies(setClientCookies.booleanValue());
		if(setClientManagement!=null)			ac.setSetClientManagement(setClientManagement.booleanValue());
		if(setDomainCookies!=null)				ac.setSetDomainCookies(setDomainCookies.booleanValue());
		if(setSessionManagement!=null)			ac.setSetSessionManagement(setSessionManagement.booleanValue());
		if(localMode!=-1) 						ac.setLocalMode(localMode);
		if(locale!=null) 						ac.setLocale(locale);
		if(timeZone!=null) 						ac.setTimeZone(timeZone);
		if(sessionType!=-1) 					ac.setSessionType(sessionType);
		if(triggerDataMember!=null) 			ac.setTriggerComponentDataMember(triggerDataMember.booleanValue());
		
		if(cacheFunction!=null) 				ac.setDefaultCacheName(Config.CACHE_DEFAULT_FUNCTION, cacheFunction);
		if(cacheObject!=null) 					ac.setDefaultCacheName(Config.CACHE_DEFAULT_OBJECT, cacheObject);
		if(cacheQuery!=null) 					ac.setDefaultCacheName(Config.CACHE_DEFAULT_QUERY, cacheQuery);
		if(cacheResource!=null) 				ac.setDefaultCacheName(Config.CACHE_DEFAULT_RESOURCE, cacheResource);
		if(cacheTemplate!=null) 				ac.setDefaultCacheName(Config.CACHE_DEFAULT_TEMPLATE, cacheTemplate);
		
		ac.setClientCluster(clientCluster);
		ac.setSessionCluster(sessionCluster);
		if(s3!=null) 							ac.setS3(AppListenerUtil.toS3(s3));
		
		// ORM
		boolean initORM=false;
		ac.setORMEnabled(ormenabled);
		if(ormenabled) {
			initORM=true;
			AppListenerUtil.setORMConfiguration(pageContext, ac, ormsettings);
		}
		
		
		return initORM;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

}