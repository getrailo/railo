package railo.runtime.listener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.DataSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.net.s3.Properties;
import railo.runtime.net.s3.PropertiesImpl;
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.rest.RestSettings;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.ArrayUtil;

/**
 * 
 */
public class ClassicApplicationContext extends ApplicationContextSupport {

	private static final long serialVersionUID = 940663152793150953L;

	private String name;
    private boolean setClientCookies;
    private boolean setDomainCookies;
    private boolean setSessionManagement;
    private boolean setClientManagement;
    private TimeSpan sessionTimeout=null; 
	private TimeSpan clientTimeout;
    private TimeSpan applicationTimeout=null;
    private int loginStorage=-1;
    private String clientstorage;
    private String sessionstorage;
	private int scriptProtect;
	private Mapping[] mappings;
	private Mapping[] ctmappings;
	private Mapping[] cmappings;
	private boolean bufferOutput;
	private boolean secureJson;
	private String secureJsonPrefix="//";
	private boolean isDefault;
	private Object defaultDataSource;
	private boolean ormEnabled;
	private Object ormdatasource;
	private ORMConfiguration config;
	private Properties s3;
	

	private int localMode;
	private Locale locale; 
	private TimeZone timeZone; 
	private short sessionType;
    private boolean sessionCluster;
    private boolean clientCluster;
	private Resource source;
	private boolean triggerComponentDataMember;
	private Map<Integer,String> defaultCaches=new HashMap<Integer, String>();
	private Map<Integer,Boolean> sameFieldAsArrays=new HashMap<Integer, Boolean>();

	private RestSettings restSettings;

	private Resource[] restCFCLocations;

	private JavaSettingsImpl javaSettings;

	private DataSource[] dataSources;

	private UDF onMissingTemplate;

    
    /**
     * constructor of the class
     * @param config
     */
    public ClassicApplicationContext(Config config,String name,boolean isDefault, Resource source) {
    	this.name=name;
    	setClientCookies=config.isClientCookies();
        setDomainCookies=config.isDomainCookies();
        setSessionManagement=config.isSessionManagement();
        setClientManagement=config.isClientManagement();
        sessionTimeout=config.getSessionTimeout();
        clientTimeout=config.getClientTimeout();
        applicationTimeout=config.getApplicationTimeout();
        loginStorage=Scope.SCOPE_COOKIE;
        scriptProtect=config.getScriptProtect();
        this.isDefault=isDefault;
        this.defaultDataSource=config.getDefaultDataSource();
        this.localMode=config.getLocalMode();
        this.locale=config.getLocale();
        this.timeZone=config.getTimeZone();

        this.bufferOutput=((ConfigImpl)config).getBufferOutput();
        this.sessionType=config.getSessionType();
        this.sessionCluster=config.getSessionCluster();
        this.clientCluster=config.getClientCluster();
        this.clientstorage=((ConfigImpl)config).getClientStorage();
        this.sessionstorage=((ConfigImpl)config).getSessionStorage();
        
        this.source=source;
        this.triggerComponentDataMember=config.getTriggerComponentDataMember();
        this.restSettings=config.getRestSetting();
        this.javaSettings=new JavaSettingsImpl();
        
    }
    
    /**
     * Constructor of the class, only used by duplicate method
     */
    private ClassicApplicationContext() {
    	
    }
    

	public ApplicationContext duplicate() {
		ClassicApplicationContext dbl = new ClassicApplicationContext();
		
		
		dbl.name=name;
		dbl.setClientCookies=setClientCookies;
		dbl.setDomainCookies=setDomainCookies;
		dbl.setSessionManagement=setSessionManagement;
		dbl.setClientManagement=setClientManagement;
		dbl.sessionTimeout=sessionTimeout;
		dbl.clientTimeout=clientTimeout;
		dbl.applicationTimeout=applicationTimeout;
		dbl.loginStorage=loginStorage;
		dbl.clientstorage=clientstorage;
		dbl.sessionstorage=sessionstorage;
		dbl.scriptProtect=scriptProtect;
		dbl.mappings=mappings;
		dbl.dataSources=dataSources;
		dbl.ctmappings=ctmappings;
		dbl.cmappings=cmappings;
		dbl.bufferOutput=bufferOutput;
		dbl.secureJson=secureJson;
		dbl.secureJsonPrefix=secureJsonPrefix;
		dbl.isDefault=isDefault;
		dbl.defaultDataSource=defaultDataSource;
		dbl.applicationtoken=applicationtoken;
		dbl.cookiedomain=cookiedomain;
		dbl.idletimeout=idletimeout;
		dbl.localMode=localMode;
		dbl.locale=locale;
		dbl.timeZone=timeZone;
		dbl.sessionType=sessionType;
		dbl.triggerComponentDataMember=triggerComponentDataMember;
		dbl.restSettings=restSettings;
		dbl.defaultCaches=Duplicator.duplicateMap(defaultCaches, new HashMap<Integer, String>(),false );
		dbl.sameFieldAsArrays=Duplicator.duplicateMap(sameFieldAsArrays, new HashMap<Integer, Boolean>(),false );
		
		dbl.ormEnabled=ormEnabled;
		dbl.config=config;
		dbl.ormdatasource=ormdatasource;
		dbl.sessionCluster=sessionCluster;
		dbl.clientCluster=clientCluster;
		dbl.source=source;
		
		return dbl;
	}
    
    
    @Override
    public TimeSpan getApplicationTimeout() {
        return applicationTimeout;
    }
    /**
     * @param applicationTimeout The applicationTimeout to set.
     */
    public void setApplicationTimeout(TimeSpan applicationTimeout) {
        this.applicationTimeout = applicationTimeout;
    }
    @Override
    public int getLoginStorage() {
        return loginStorage;
    }
    /**
     * @param loginStorage The loginStorage to set.
     */
    public void setLoginStorage(int loginStorage) {
        this.loginStorage = loginStorage;
    }
    
    public void setLoginStorage(String strLoginStorage) throws ApplicationException {
    	setLoginStorage(AppListenerUtil.translateLoginStorage(strLoginStorage));
    }
    
    
    
    @Override
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public TimeSpan getSessionTimeout() {
        return sessionTimeout;
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    public void setSessionTimeout(TimeSpan sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }


    public TimeSpan getClientTimeout() {
        return clientTimeout;
    }
    
    /**
     * @param sessionTimeout The sessionTimeout to set.
     */
    public void setClientTimeout(TimeSpan clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    
    @Override
    public boolean isSetClientCookies() {
        return setClientCookies;
    }
    /**
     * @param setClientCookies The setClientCookies to set.
     */
    public void setSetClientCookies(boolean setClientCookies) {
        this.setClientCookies = setClientCookies;
    }
    @Override
    public boolean isSetClientManagement() {
        return setClientManagement;
    }
    /**
     * @param setClientManagement The setClientManagement to set.
     */
    public void setSetClientManagement(boolean setClientManagement) {
        this.setClientManagement = setClientManagement;
    }
    @Override
    public boolean isSetDomainCookies() {
        return setDomainCookies;
    }
    /**
     * @param setDomainCookies The setDomainCookies to set.
     */
    public void setSetDomainCookies(boolean setDomainCookies) {
        this.setDomainCookies = setDomainCookies;
    }
    @Override
    public boolean isSetSessionManagement() {
        return setSessionManagement;
    }
    /**
     * @param setSessionManagement The setSessionManagement to set.
     */
    public void setSetSessionManagement(boolean setSessionManagement) {
        this.setSessionManagement = setSessionManagement;
    }
    @Override
    public String getClientstorage() {
        return clientstorage;
    }
    public String getSessionstorage() {
        return sessionstorage;
    }
    /**
     * @param clientstorage The clientstorage to set.
     */
    public void setClientstorage(String clientstorage) {
    	if(StringUtil.isEmpty(clientstorage,true)) return;
        this.clientstorage = clientstorage;
    }
    public void setSessionstorage(String sessionstorage) {
    	if(StringUtil.isEmpty(sessionstorage,true)) return;
        this.sessionstorage = sessionstorage;
    }

    @Override
    public boolean hasName() {
        return name!=null;
    }
    
    /**
     * @param scriptProtect The scriptProtect to set.
     */
    public void setScriptProtect(int scriptProtect) {
		this.scriptProtect=scriptProtect;
	}

	@Override
	public int getScriptProtect() {
		//if(isDefault)print.err("get:"+scriptProtect);
		return scriptProtect;
	}

	


	public void setMappings(Mapping[] mappings) {
		if(mappings.length>0)this.mappings=mappings;
	}

	/**
	 * @return the mappings
	 */
	public Mapping[] getMappings() {
		return mappings;
	}

	public void setCustomTagMappings(Mapping[] ctmappings) {
		this.ctmappings=ctmappings;
	}

	public Mapping[] getCustomTagMappings() {
		return ctmappings;
	}

	public void setComponentMappings(Mapping[] cmappings) {
		this.cmappings=cmappings;
	}

	public Mapping[] getComponentMappings() {
		return cmappings;
	}

	public void setSecureJson(boolean secureJson) {
		this.secureJson=secureJson;
	}

	/**
	 * @return the secureJson
	 */
	public boolean getSecureJson() {
		return secureJson;
	}
	
	public boolean getBufferOutput(){
		return bufferOutput;
	}
	
	public void setBufferOutput(boolean bufferOutput){
		this.bufferOutput= bufferOutput;
	}
	
	public void setSecureJsonPrefix(String secureJsonPrefix) {
		this.secureJsonPrefix=secureJsonPrefix;
	}

	/**
	 * @return the secureJsonPrefix
	 */
	public String getSecureJsonPrefix() {
		return secureJsonPrefix;
	}

	@Override
	public String getDefaultDataSource() {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported!"));
	}
	
	@Override
	public Object getDefDataSource() {
		return defaultDataSource;
	}

	@Override
	public void setDefaultDataSource(String defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}

	@Override
	public void setDefDataSource(Object defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}

	public boolean isORMEnabled() {
		return ormEnabled;
	}

	public String getORMDatasource() {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported!"));
	}

	public Object getORMDataSource() {
		return ormdatasource;
	}

	public ORMConfiguration getORMConfiguration() {
		return config;
	}
	public void setORMConfiguration(ORMConfiguration config) {
		this.config= config;
	}

	public void setORMEnabled(boolean ormEnabled) {
		this.ormEnabled=ormEnabled;
	}

	/**
	 * @return the s3
	 */
	public Properties getS3() {
		if(s3==null) s3=new PropertiesImpl();
		return s3;
	}

	@Override
	public int getLocalMode() {
		return localMode;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}
	


	/**
	 * @param localMode the localMode to set
	 */
	public void setLocalMode(int localMode) {
		this.localMode = localMode;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}



    /**
	 * @return the sessionType
	 */
	public short getSessionType() {
		return sessionType;
	}

    /**
	 * @return the sessionType
	 */
	public void setSessionType(short sessionType) {
		this.sessionType= sessionType;
	}


	/**
	 * @return the sessionCluster
	 */
	public boolean getSessionCluster() {
		return sessionCluster;
	}


	/**
	 * @param sessionCluster the sessionCluster to set
	 */
	public void setSessionCluster(boolean sessionCluster) {
		this.sessionCluster = sessionCluster;
	}


	/**
	 * @return the clientCluster
	 */
	public boolean getClientCluster() {
		return clientCluster;
	}


	/**
	 * @param clientCluster the clientCluster to set
	 */
	public void setClientCluster(boolean clientCluster) {
		this.clientCluster = clientCluster;
	}


	public void setS3(Properties s3) {
		this.s3=s3;
	}

	@Override
	public void setORMDatasource(String ormdatasource) {
		this.ormdatasource=ormdatasource;
	}

	@Override
	public void setORMDataSource(Object ormdatasource) {
		this.ormdatasource=ormdatasource;
	}

	@Override
	public void reinitORM(PageContext pc) throws PageException {
		// do nothing
	}

	@Override
	public Resource getSource() {
		return source;
	}

	@Override
	public boolean getTriggerComponentDataMember() {
		return triggerComponentDataMember;
	}

	@Override
	public void setTriggerComponentDataMember(boolean triggerComponentDataMember) {
		this.triggerComponentDataMember=triggerComponentDataMember;
	}

	@Override
	public void setDefaultCacheName(int type,String name) {
		if(StringUtil.isEmpty(name,true)) return;
		defaultCaches.put(type, name.trim());
	}
	
	@Override
	public String getDefaultCacheName(int type) {
		return defaultCaches.get(type);
	}

	public void setSameFieldAsArray(int scope, boolean sameFieldAsArray) {
		sameFieldAsArrays.put(scope, sameFieldAsArray);
	}
	
	
	@Override
	public boolean getSameFieldAsArray(int scope) {
		Boolean b= sameFieldAsArrays.get(scope);
		if(b==null) return false;
		return b.booleanValue();
	}

	@Override
	public RestSettings getRestSettings() {
		return restSettings;
	}

	public void setRestSettings(RestSettings restSettings) {
		this.restSettings=restSettings;
	}
	

	public void setRestCFCLocations(Resource[] restCFCLocations) {
		this.restCFCLocations = restCFCLocations;
	}

	@Override
	public Resource[] getRestCFCLocations() {
		return restCFCLocations;
	}

	@Override
	public JavaSettings getJavaSettings() {
		return javaSettings;
	}

	@Override
	public DataSource[] getDataSources() {
		return dataSources;
	}

	@Override
	public void setDataSources(DataSource[] dataSources) {
		if(!ArrayUtil.isEmpty(dataSources))this.dataSources=dataSources;
	}

	public void setOnMissingTemplate(UDF onMissingTemplate) {
		this.onMissingTemplate=onMissingTemplate;
	}

	public UDF getOnMissingTemplate() { 
		return onMissingTemplate;
	}
}