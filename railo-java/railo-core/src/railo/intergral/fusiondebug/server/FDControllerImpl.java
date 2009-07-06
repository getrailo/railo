package railo.intergral.fusiondebug.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import railo.commons.io.SystemUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Info;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

import com.intergral.fusiondebug.server.IFDController;
import com.intergral.fusiondebug.server.IFDThread;

/**
 * 
 */
public class FDControllerImpl implements IFDController {


	private List exceptionTypes;
	private CFMLEngineImpl engine;
	
	
	public FDControllerImpl(CFMLEngineImpl engine){
		
		this.engine=engine;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getEngineName()
	 */
	public String getEngineName() {
		return "Railo";
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getEngineVersion()
	 */
	public String getEngineVersion() {
		return Info.getVersionAsString();
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getExceptionTypes()
	 */
	public List getExceptionTypes() {
		if(exceptionTypes==null){
			exceptionTypes=new ArrayList();
			exceptionTypes.add("application");
			exceptionTypes.add("expression");
			exceptionTypes.add("database");
			exceptionTypes.add("custom_type");
			exceptionTypes.add("lock");
			exceptionTypes.add("missinginclude");
			exceptionTypes.add("native");
			exceptionTypes.add("security");
			exceptionTypes.add("template");
		}
		return exceptionTypes;
	}

	/**
	 * @deprecated use instead <code>{@link #getLicenseInformation(String)}</code>
	 */
	public String getLicenseInformation() {
		throw new RuntimeException("please replace your fusiondebug-api-server-1.0.xxx-SNAPSHOT.jar with a newer version");
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getLicenseInformation(java.lang.String)
	 */
	public String getLicenseInformation(String key) {
		return FDLicense.getLicenseInformation(key);
	}


	/**
	 * @see com.intergral.fusiondebug.server.IFDController#output(java.lang.String)
	 */
	public void output(String message) {
		Config config = ThreadLocalConfig.get();
		PrintWriter out=config==null?SystemUtil.PRINTWRITER_OUT:((ConfigWebImpl)config).getOutWriter();
		SystemOut.print(out, message);
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#pause()
	 */
	public List pause() {
		List threads = new ArrayList();
		Iterator it = engine.getCFMLFactories().entrySet().iterator();
		Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			pause((String) entry.getKey(),(CFMLFactoryImpl) entry.getValue(), threads);
		}
		
		return threads;
	}
	
	private void pause(String name,CFMLFactoryImpl factory,List threads) {
		Struct pcs = factory.getRunningPageContextes();
		Iterator it = pcs.entrySet().iterator();
		PageContextImpl pc;
		
		while(it.hasNext()){
			pc=(PageContextImpl) ((Entry) it.next()).getValue();
			try {
				pc.getThread().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threads.add(new FDThreadImpl(this,factory,name,pc));
		}
	}
	
	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getCaughtStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public boolean getCaughtStatus(
			String exceptionType,
			String executionUnitName,
            String executionUnitPackage,
            String sourceFilePath,
            String sourceFileName,
            int lineNumber) {
		// TODO [007]
		return true;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getByNativeIdentifier(java.lang.String)
	 */
	public IFDThread getByNativeIdentifier(String id) {
		Iterator it = engine.getCFMLFactories().entrySet().iterator();
		Entry entry;
		FDThreadImpl thread;
		while(it.hasNext()){
			entry=(Entry) it.next();
			thread = getByNativeIdentifier((String) entry.getKey(),(CFMLFactoryImpl) entry.getValue(),id);
			if(thread!=null) return thread;
		}
		return null;
	}
	
	/**
	 * checks a single CFMLFactory for the thread
	 * @param name
	 * @param factory
	 * @param id
	 * @return matching thread or null
	 */
	private FDThreadImpl getByNativeIdentifier(String name,CFMLFactoryImpl factory,String id) {
		Struct pcs = factory.getRunningPageContextes();
		Iterator it = pcs.entrySet().iterator();
		PageContextImpl pc;
		
		while(it.hasNext()){
			pc=(PageContextImpl) ((Entry) it.next()).getValue();
			if(equals(pc,id)) return new FDThreadImpl(this,factory,name,pc);
		}
		return null;
	}

	/**
	 * check if thread of PageContext match given id
	 * @param pc
	 * @param id
	 * @return match the id the pagecontext
	 */
	private boolean equals(PageContextImpl pc, String id) {
		Thread thread = pc.getThread();
		if(Caster.toString(FDThreadImpl.id(pc)).equals(id)) return true;
		if(Caster.toString(thread.getId()).equals(id)) return true;
		if(Caster.toString(thread.hashCode()).equals(id)) return true;
		return false;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getCompletionMethod()
	 */
	public String getCompletionMethod() {
		return "serviceCFML";
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#getCompletionType()
	 */
	public String getCompletionType() {
		return CFMLEngineImpl.class.getName();
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDController#release()
	 */
	public void release() {
		this.engine.allowRequestTimeout(true);
	}
}
