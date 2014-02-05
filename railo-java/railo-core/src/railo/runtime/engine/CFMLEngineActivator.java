package railo.runtime.engine;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import railo.loader.engine.CFMLEngine;

public class CFMLEngineActivator  {
    
private ServiceRegistration<?> registration;

	//@Override
    public void start(BundleContext bundleContext) throws Exception {
        registration = bundleContext.registerService(
                CFMLEngine.class.getName(),
                CFMLEngineImpl.getInstance(),
                null);
    }
  
    //@Override
    public void stop(BundleContext bundleContext) throws Exception {
        registration.unregister();
    }
}
