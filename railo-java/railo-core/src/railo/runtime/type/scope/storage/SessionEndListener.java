package railo.runtime.type.scope.storage;

import java.io.Serializable;

import railo.runtime.CFMLFactoryImpl;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;

public class SessionEndListener implements StorageScopeListener,Serializable {

	private static final long serialVersionUID = -3868545140988347285L;

	@Override
	public void doEnd(StorageScopeEngine engine,StorageScopeCleaner cleaner,String appName, String cfid) {
		CFMLFactoryImpl factory = engine.getFactory();
		ApplicationListener listener = factory.getConfig().getApplicationListener();
		try {
			cleaner.info("call onSessionEnd for "+appName+"/"+cfid);
			listener.onSessionEnd(factory, appName, cfid);
		} 
		catch (Throwable t) {
			ExceptionHandler.log(factory.getConfig(),Caster.toPageException(t));
		}
	}

}
