package railo.runtime.osgi;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngineFactory;

public class OSGiUtil {
	
	/**
	 * only installs a bundle, if the bundle does not already exist, if the bundle exists the existing bundle is unloaded first.
	 * @param factory
	 * @param context
	 * @param bundle
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public static Bundle installBundle(Log log,BundleContext context,Resource bundle) throws IOException, BundleException {
		if(log!=null)log.info("OSGi", "add bundle:"+bundle);
		
		//context.getBundle(arg0);
		
    	InputStream is = bundle.getInputStream();
		try {
			Bundle b = context.installBundle(bundle.getAbsolutePath(),is);
			//if(start)start(factory,b);
        	return b;
        }
        finally {
        	CFMLEngineFactory.closeEL(is);
        }
	}
}
