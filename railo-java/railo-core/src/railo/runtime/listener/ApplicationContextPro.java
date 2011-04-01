package railo.runtime.listener;

import railo.runtime.Mapping;
import railo.runtime.net.s3.Properties;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.util.ApplicationContext;

// FUTURE move all this to ApplicationContext and delete this interface
public interface ApplicationContextPro extends ApplicationContext {
	
	public String getDefaultDataSource();
	
	public boolean isORMEnabled();

	public String getORMDatasource();

	public ORMConfiguration getORMConfiguration();
	
	public Properties getS3();
	
	public int getLocalMode();
	
	public String getSessionstorage();

	public TimeSpan getClientTimeout();
	
	public short getSessionType();
	
	public boolean getSessionCluster();

	public boolean getClientCluster();

	public Mapping[] getComponentMappings();
	
	
	
	
	
	

	public void setApplicationTimeout(TimeSpan applicationTimeout);
	public void setSessionTimeout(TimeSpan sessionTimeout);
	public void setClientTimeout(TimeSpan clientTimeout);
	public void setClientstorage(String clientstorage);
	public void setSessionstorage(String sessionstorage);
	public void setCustomTagMappings(Mapping[] customTagMappings);
	public void setComponentMappings(Mapping[] componentMappings);
	public void setMappings(Mapping[] mappings);
	public void setLoginStorage(int loginstorage);
	public void setDefaultDataSource(String datasource);
	public void setScriptProtect(int scriptrotect);
	public void setSecureJson(boolean secureJson);
	public void setSecureJsonPrefix(String secureJsonPrefix);
	public void setSetClientCookies(boolean setClientCookies);
	public void setSetClientManagement(boolean setClientManagement);
	public void setSetDomainCookies(boolean setDomainCookies);
	public void setSetSessionManagement(boolean setSessionManagement);
	public void setLocalMode(int localMode);
	public void setSessionType(short sessionType);
	public void setClientCluster(boolean clientCluster);
	public void setSessionCluster(boolean sessionCluster);
	public void setS3(Properties s3);
	public void setORMEnabled(boolean ormenabled);
	public void setORMConfiguration(ORMConfiguration ormConf);
	public void setORMDatasource(String string);

	public String getSecurityApplicationToken();
	public String getSecurityCookieDomain();
	public int getSecurityIdleTimeout();
	public void setSecuritySettings(String applicationtoken,String cookiedomain, int idletimeout);
}
