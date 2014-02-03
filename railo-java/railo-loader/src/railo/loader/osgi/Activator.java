package railo.loader.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import railo.loader.engine.CFMLEngineFactory;

public class Activator implements BundleActivator {
    
	
@Override
    public void start(BundleContext bundleContext) throws Exception {
        registration = bundleContext.registerService(
                CFMLEngineFactory.class.getName(),
                new HelloWorldServiceImpl(),
                null);
    }
  
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        registration.unregister();

    }
}
