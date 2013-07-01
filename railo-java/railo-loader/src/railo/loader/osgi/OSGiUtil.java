package railo.loader.osgi;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class OSGiUtil {

	public static Felix loadFelix() throws BundleException {
		Map config = new HashMap();
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
	        
		
		Felix felix = new Felix(config);
        felix.start();
        
		return felix;
	}

}
