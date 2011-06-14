package railo.runtime.util;

import java.io.Serializable;

import railo.runtime.Mapping;
import railo.runtime.type.dt.TimeSpan;

/**
 * DTO Interface for Application Context data (defined by tag cfapplication)
 */
public interface ApplicationContext extends Serializable {

    public static final int SCRIPT_PROTECT_NONE = 0;
    public static final int SCRIPT_PROTECT_FORM = 1;
    public static final int SCRIPT_PROTECT_URL = 2;
    public static final int SCRIPT_PROTECT_CGI = 4;
    public static final int SCRIPT_PROTECT_COOKIE = 8;
    public static final int SCRIPT_PROTECT_ALL = SCRIPT_PROTECT_CGI+SCRIPT_PROTECT_COOKIE+SCRIPT_PROTECT_FORM+SCRIPT_PROTECT_URL;

	/**
     * @return Returns the applicationTimeout.
     */
    public abstract TimeSpan getApplicationTimeout();

    /**
     * @return Returns the loginStorage.
     */
    public abstract int getLoginStorage();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @return Returns the sessionTimeout.
     */
    public abstract TimeSpan getSessionTimeout();

    /**
     * @return Returns the setClientCookies.
     */
    public abstract boolean isSetClientCookies();

    /**
     * @return Returns the setClientManagement.
     */
    public abstract boolean isSetClientManagement();

    /**
     * @return Returns the setDomainCookies.
     */
    public abstract boolean isSetDomainCookies();

    /**
     * @return Returns the setSessionManagement.
     */
    public abstract boolean isSetSessionManagement();

    /**
     * @return Returns the clientstorage.
     */
    public abstract String getClientstorage();

    /**
     * @return if application context has a name
     */
    public abstract boolean hasName();
    
    /**
     * @return return script protect setting
     */
    public int getScriptProtect();

    
    public Mapping[] getMappings();
    
    public Mapping[] getCustomTagMappings();
    

	public String getSecureJsonPrefix() ;

	public boolean getSecureJson();

	public abstract String getDefaultDataSource();

}