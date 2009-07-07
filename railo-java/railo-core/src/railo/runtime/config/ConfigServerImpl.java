package railo.runtime.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import railo.commons.collections.HashTable;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.loader.engine.CFMLEngine;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;

/**
 * config server impl
 */
public final class ConfigServerImpl extends ConfigImpl implements ConfigServer {
    
    private CFMLEngineImpl engine;
    private Map initContextes;
    private Map contextes;
    private SecurityManager defaultSecurityManager;
    private Map managers=new HashTable();
    private String defaultPassword;
    private Resource rootDir;
    private URL updateLocation;
    private String updateType="";
	private ConfigListener configListener;
	private static ConfigServerImpl instance;
	
    /**
	 * @return the configListener
	 */
	public ConfigListener getConfigListener() {
		return configListener;
	}

	/**
	 * @param configListener the configListener to set
	 */
	public void setConfigListener(ConfigListener configListener) {
		this.configListener = configListener;
	}

	/**
     * @param engine 
     * @param initContextes
     * @param contextes
     * @param configDir
     * @param configFile
     */
    protected ConfigServerImpl(CFMLEngineImpl engine,Map initContextes, Map contextes, Resource configDir, Resource configFile) {
    	//super(configDir, configFile, "<root>");
    	super(null,configDir, configFile);
        this.engine=engine;
        this.initContextes=initContextes;
        this.contextes=contextes;
        this.rootDir=configDir;
        instance=this;
    }

    /**
     * @see railo.runtime.config.ConfigImpl#getConfigServerImpl()
     */
    public ConfigServerImpl getConfigServerImpl() {
        return this;
    }

    /**
     * @see railo.runtime.config.ConfigImpl#getConfigServer(java.lang.String)
     */
    public ConfigServer getConfigServer(String password) {
        return this;
    }
    

    public ConfigServer getConfigServer() {
        return this;
    }

    /**
     * @see railo.runtime.config.ConfigServer#getConfigWebs()
     */
    public ConfigWeb[] getConfigWebs() {
        Iterator it = contextes.keySet().iterator();
        ConfigWeb[] webs=new ConfigWeb[contextes.size()];
        int index=0;        
        while(it.hasNext()) {
            webs[index++]=((CFMLFactoryImpl)contextes.get(it.next())).getConfig();
        }
        return webs;
    }
    
    /**
     * @see railo.runtime.config.ConfigServer#getConfigWeb(java.lang.String)
     */
    public ConfigWeb getConfigWeb(String realpath) {
        return getConfigWebImpl(realpath);
    }
    
    /**
     * returns CongigWeb Implementtion
     * @param realpath
     * @return ConfigWebImpl
     */
    protected ConfigWebImpl getConfigWebImpl(String realpath) {
        Iterator it = contextes.keySet().iterator();
          
        while(it.hasNext()) {
            ConfigWebImpl cw=((CFMLFactoryImpl)contextes.get(it.next())).getConfigWebImpl();
            if(cw.getServletContext().getRealPath("/").equals(realpath))
                return cw;
        }
        return null;
    }
    
    public ConfigWebImpl getConfigWebById(String id) {
        Iterator it = contextes.keySet().iterator();
          
        while(it.hasNext()) {
            ConfigWebImpl cw=((CFMLFactoryImpl)contextes.get(it.next())).getConfigWebImpl();
            if(cw.getId().equals(id))
                return cw;
        }
        return null;
    }
    
    /**
     * @return JspFactoryImpl array
     */
    public CFMLFactoryImpl[] getJSPFactories() {
        Iterator it = initContextes.keySet().iterator();
        CFMLFactoryImpl[] factories=new CFMLFactoryImpl[initContextes.size()];
        int index=0;        
        while(it.hasNext()) {
            factories[index++]=(CFMLFactoryImpl)initContextes.get(it.next());
        }
        return factories;
    }
    /**
     * @see railo.runtime.config.ConfigServer#getJSPFactoriesAsMap()
     */
    public Map getJSPFactoriesAsMap() {
        return contextes;
    }

    /**
     * @see railo.runtime.config.ConfigServer#getSecurityManager(java.lang.String)
     */
    public SecurityManager getSecurityManager(String id) {
        Object o=managers.get(id);
        if(o!=null) return (SecurityManager) o;
        return defaultSecurityManager.cloneSecurityManager();
    }
    
    /**
     * @see railo.runtime.config.ConfigServer#hasIndividualSecurityManager(java.lang.String)
     */
    public boolean hasIndividualSecurityManager(String id) {
        return managers.containsKey(id);
    }

    /**
     * @param defaultSecurityManager
     */
    protected void setDefaultSecurityManager(SecurityManager defaultSecurityManager) {
        this.defaultSecurityManager=defaultSecurityManager;
    }

    /**
     * @param id
     * @param securityManager
     */
    protected void setSecurityManager(String id, SecurityManager securityManager) {
        managers.put(id,securityManager);
    }

    /**
     * @param id
     */
    protected void removeSecurityManager(String id) {
        managers.remove(id);
    }
    
    /**
     * @see railo.runtime.config.ConfigServer#getDefaultSecurityManager()
     */
    public SecurityManager getDefaultSecurityManager() {
        return defaultSecurityManager;
    }
    /**
     * @return Returns the defaultPassword.
     */
    protected String getDefaultPassword() {
        return defaultPassword;
    }
    /**
     * @param defaultPassword The defaultPassword to set.
     */
    protected void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    /**
     * @see railo.runtime.config.ConfigServer#getCFMLEngine()
     */
    public CFMLEngine getCFMLEngine() {
        return engine;
    }
    public CFMLEngineImpl getCFMLEngineImpl() {
        return engine;
    }


    /**
     * @return Returns the rootDir.
     */
    public Resource getRootDirectory() {
        return rootDir;
    }

    /**
     * @see railo.runtime.config.Config#getUpdateType()
     */
    public String getUpdateType() {
        return updateType;
    }

    /**
     * @see railo.runtime.config.ConfigServer#setUpdateType(java.lang.String)
     */
    public void setUpdateType(String updateType) {
        if(!StringUtil.isEmpty(updateType))
            this.updateType = updateType;
    }

    /**
     * @see railo.runtime.config.Config#getUpdateLocation()
     */
    public URL getUpdateLocation() {
        return updateLocation;
    }

    /**
     * @see railo.runtime.config.ConfigServer#setUpdateLocation(java.net.URL)
     */
    public void setUpdateLocation(URL updateLocation) {
        this.updateLocation = updateLocation;
    }

    /**
     * @see railo.runtime.config.ConfigServer#setUpdateLocation(java.lang.String)
     */
    public void setUpdateLocation(String strUpdateLocation) throws MalformedURLException {
        setUpdateLocation(new URL(strUpdateLocation));
    }

    /**
     * @see railo.runtime.config.ConfigServer#setUpdateLocation(java.lang.String, java.net.URL)
     */
    public void setUpdateLocation(String strUpdateLocation, URL defaultValue) {
        try {
            setUpdateLocation(strUpdateLocation);
        } catch (MalformedURLException e) {
            setUpdateLocation(defaultValue);
        }
    }

    /**
     * @see railo.runtime.config.Config#getSecurityManager()
     */
    public SecurityManager getSecurityManager() {
        SecurityManagerImpl sm = (SecurityManagerImpl) getDefaultSecurityManager();//.cloneSecurityManager();
        //sm.setAccess(SecurityManager.TYPE_ACCESS_READ,SecurityManager.ACCESS_PROTECTED);
        //sm.setAccess(SecurityManager.TYPE_ACCESS_WRITE,SecurityManager.ACCESS_PROTECTED);
        return sm;
    }

	/**
	 * @return the instance
	 */
	public static ConfigServerImpl getInstance() {
		return instance;
	}


}
