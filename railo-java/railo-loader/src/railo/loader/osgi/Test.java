package railo.loader.osgi;



import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.util.Cast;

public class Test {

	/**
	 * @param args
	 * @throws BundleException 
	 */
	public static void main(String[] args) throws Exception {
		Cast caster = CFMLEngineFactory.getInstance().getCastUtil();
		Resource res = caster.toResource("/Users/mic/Tmp2/test.zip");
		
		Map config = new HashMap();
        config.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
	        
		
		Felix felix = new Felix(config);
        felix.start();
        BundleContext bc = felix.getBundleContext();
        
        Bundle b = BundleUtil.addBundle(bc, res, true);
        long id = b.getBundleId();
        
        print(BundleUtil.bundleState(b.getState(),"UNKNOW"));
        Class<?> clazz = b.loadClass("antlr.Alternative");
        print(clazz);
        print(clazz.getClassLoader().getClass().getName());
        
        print(b.getClass().getName());
        print(b.getLastModified());
        print(b.getSymbolicName());
        
        print(bc.getBundle(id));
        b.uninstall();
        print(bc.getBundle(id));
        
        
	}

	private static void print(Object o) {
		System.out.println(o);
	}

}
