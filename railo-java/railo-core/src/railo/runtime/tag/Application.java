package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.util.ApplicationContextImpl;

/**
* Defines scoping for a ColdFusion application, enables or disables storing client variables, 
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
	private String secureJsonPrefix;
	private Boolean secureJson;
	private String scriptrotect;
	private String datasource;
	private int loginstorage=Scope.SCOPE_UNDEFINED;
	
	//ApplicationContextImpl appContext;
    private String name="";
	private int action=ACTION_CREATE;
	private int localMode=-1;
    
     
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
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
        secureJson=null;
        secureJsonPrefix=null;
        loginstorage=Scope.SCOPE_UNDEFINED;
        scriptrotect=null;
        datasource=null;
        this.name="";
        action=ACTION_CREATE;
        localMode=-1;
        //appContext=null;
    }
    
    /** set the value setclientcookies
	*  Yes or No. Yes enables client cookies. Default is Yes. If you set this attribute to 
	* 		"No", ColdFusion does not automatically send the CFID and CFTOKEN cookies to the client browser;
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
		setDefaultdatasource(Caster.toString(datasource));
	}
	public void setDefaultdatasource(String datasource) {
		this.datasource = datasource;
	}
	
	public void setLocalmode(String strLocalMode) throws ApplicationException {
		this.localMode = AppListenerUtil.toLocalMode(strLocalMode);
		
	}

	/** set the value clientstorage
	*  Specifies how ColdFusion stores client variables
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
	* 		by commas, to specify the lifespan of session variables. The default value is specified in the
	* 		Variables page of the ColdFusion Administrator.
	* @param sessionTimeout value to set
	**/
	public void setSessiontimeout(TimeSpan sessionTimeout)	{
		this.sessionTimeout=sessionTimeout;
	}
	
	public void setClienttimeout(TimeSpan clientTimeout)	{
		this.clientTimeout=clientTimeout;
	}

	/** set the value applicationtimeout
	*  Enter the CreateTimeSpan function and values in days, hours, minutes, and seconds, separated 
	* 		by commas, to specify the lifespan of application variables. The default value is specified in
	* 		the Variables page of the ColdFusion Administrator.
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
	    this.mappings=AppListenerUtil.toMappings(pageContext, mappings);
		//getAppContext().setMappings(AppListenerUtil.toMappings(pageContext, mappings));
	}
	
	public void setCustomtagpaths(Object mappings) throws PageException	{
	    this.customTagMappings=AppListenerUtil.toCustomTagMappings(pageContext, mappings);
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


	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
        
        ApplicationContextImpl ac;
		if(action==ACTION_CREATE){
        	ac=createAppContext();
        	ac.setName(name);
        	set(ac);
        	pageContext.setApplicationContext(ac);
        }
        else {
        	ac=(ApplicationContextImpl) pageContext.getApplicationContext();
        	set(ac);
        }
        return SKIP_BODY; 
	}

	private void set(ApplicationContextImpl ac) {
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
		if(mappings!=null)						ac.setMappings(mappings);
		if(loginstorage!=Scope.SCOPE_UNDEFINED)	ac.setLoginStorage(loginstorage);
		if(!StringUtil.isEmpty(datasource))		ac.setDefaultDataSource(datasource);
		if(scriptrotect!=null)					ac.setScriptProtect(scriptrotect);
		if(secureJson!=null)					ac.setSecureJson(secureJson.booleanValue());
		if(secureJsonPrefix!=null)				ac.setSecureJsonPrefix(secureJsonPrefix);
		if(setClientCookies!=null)				ac.setSetClientCookies(setClientCookies.booleanValue());
		if(setClientManagement!=null)			ac.setSetClientManagement(setClientManagement.booleanValue());
		if(setDomainCookies!=null)				ac.setSetDomainCookies(setDomainCookies.booleanValue());
		if(setSessionManagement!=null)			ac.setSetSessionManagement(setSessionManagement.booleanValue());
		if(localMode!=-1) 						ac.setLocalMode(localMode);
		
		
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

    private ApplicationContextImpl createAppContext() {
    	return new ApplicationContextImpl(pageContext.getConfig(),false);
        //if(appContext==null)appContext=new ApplicationContextImpl(pageContext.getConfig());
        //return appContext;
    }

}