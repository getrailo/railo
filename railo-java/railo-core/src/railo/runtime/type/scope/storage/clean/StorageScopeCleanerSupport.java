package railo.runtime.type.scope.storage.clean;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.type.scope.storage.StorageScopeCleaner;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;

public abstract class StorageScopeCleanerSupport implements StorageScopeCleaner {
	

	protected static final int INTERVALL_MINUTE = 60*1000; 
	protected static final int INTERVALL_HOUR = 60*60*1000; 
	protected static final int INTERVALL_DAY = 24*60*60*1000; 
	
	protected StorageScopeEngine engine;
	protected int type;
	protected StorageScopeListener listener;
	private String application;
	protected String strType;
	private final int intervall;
	private long lastClean;
	
	public StorageScopeCleanerSupport(int type, StorageScopeListener listener, int intervall) {
		this.type=type;
		this.listener=listener;
		this.strType=VariableInterpreter.scopeInt2String(type);
		application=strType+" storage";
		this.intervall=intervall;
		
	}

	@Override
	public void init(StorageScopeEngine engine){
		this.engine=engine;
	}
	
	public final void clean() {
		if(lastClean+intervall<System.currentTimeMillis()) {
			//info("cleaning "+application);
			_clean();
			lastClean=System.currentTimeMillis();
			//info("next cleaning intervall in "+(intervall/1000)+" seconds");
		}
	}
	
	protected abstract void _clean();
	
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
