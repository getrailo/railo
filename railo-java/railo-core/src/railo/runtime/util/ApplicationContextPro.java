package railo.runtime.util;

import railo.runtime.Component;
import railo.runtime.net.s3.Properties;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.type.Collection;

// FUTURE move all this to ApplicationContext and delete this interface
public interface ApplicationContextPro extends ApplicationContext {
	
	public String getDefaultDataSource();
	
	public boolean isORMEnabled();

	public String getORMDatasource();

	public ORMConfiguration getORMConfiguration();
	
	public Component getComponent();
	
	public Properties getS3();

	/**
	 * returns a custom setting (only supported for application.cfc)
	 * @param string
	 */
	public Object getCustom(Collection.Key key);
}
