package railo.runtime.type.scope.storage.clean;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.scope.storage.StorageScopeCleaner;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;

public abstract class StorageScopeCleanerSupport implements StorageScopeCleaner {
	
	protected StorageScopeEngine engine;
	protected int type;
	protected StorageScopeListener listener;
	private String application;
	protected String strType;
	
	
	public StorageScopeCleanerSupport(int type, StorageScopeListener listener) {
		this.type=type;
		this.listener=listener;
		this.strType=VariableInterpreter.scopeInt2String(type);
		application=strType+" storage";
	}

	/**
	 * @see railo.runtime.type.scope.storage.StorageScopeCleaner#init(railo.runtime.type.scope.storage.StorageScopeEngine)
	 */
	public void init(StorageScopeEngine engine){
		this.engine=engine;
	}
	
	/**
	 * @return the log
	 */
	public void info(String msg) {
		 engine.getFactory().getScopeContext().info(msg);
	}
	public void error(String msg) {
		engine.getFactory().getScopeContext().error(msg);
		engine._getLog().error(application, msg);
	}
	
	public void error(Throwable t) {
		engine.getFactory().getScopeContext().error(t);
		engine._getLog().error(application,ExceptionUtil.getStacktrace(t, true));
	}
}
