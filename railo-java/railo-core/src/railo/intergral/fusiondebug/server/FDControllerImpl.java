package railo.intergral.fusiondebug.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import railo.commons.io.SystemUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.CFMLFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Info;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.security.SerialNumber;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

import com.intergral.fusiondebug.server.IFDController;
import com.intergral.fusiondebug.server.IFDThread;

/**
 * 
 */
public class FDControllerImpl implements IFDController {


	private List exceptionTypes;
	private CFMLEngineImpl engine;
	private boolean isEnterprise;
	
	
	public FDControllerImpl(CFMLEngineImpl engine,String serial){
		this.isEnterprise=SerialNumber.isEnterprise(serial);
		this.engine=engine;
	}

	@Override
	public String getEngineName() {
		return "Railo";
	}

	@Override
	public String getEngineVersion() {
		return Info.getVersionAsString();
	}

	@Override
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

	@Override
	public String getLicenseInformation(String key) {
		if(!isEnterprise) {
			SystemOut.print(new PrintWriter(System.err),"FD Server Licensing does not work with the Open Source Version of Railo or Enterprise Version of Railo that is not enabled");
			return null;
		}
		return FDLicense.getLicenseInformation(key);
	}


	@Override
	public void output(String message) {
		Config config = ThreadLocalPageContext.getConfig();
		PrintWriter out=config==null?SystemUtil.getPrintWriter(SystemUtil.OUT):((ConfigWebImpl)config).getOutWriter();
		SystemOut.print(out, message);
	}

	@Override
	public List pause() {
		List<IFDThread> threads = new ArrayList<IFDThread>();
		Iterator<Entry<String, CFMLFactory>> it = engine.getCFMLFactories().entrySet().iterator();
		Entry<String, CFMLFactory> entry;
		while(it.hasNext()){
			entry = it.next();
			pause(entry.getKey(),(CFMLFactoryImpl) entry.getValue(), threads);
		}
		
		return threads;
	}
	
	private void pause(String name,CFMLFactoryImpl factory,List<IFDThread> threads) {
		Struct pcs = factory.getRunningPageContexts();
		Iterator<Entry<Key, Object>> it = pcs.entryIterator();
		PageContextImpl pc;
		
		while(it.hasNext()){
			pc=(PageContextImpl) it.next().getValue();
			try {
				pc.getThread().wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threads.add(new FDThreadImpl(this,factory,name,pc));
		}
	}
	
	@Override
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

	@Override
	public IFDThread getByNativeIdentifier(String id) {
		Iterator<Entry<String, CFMLFactory>> it = engine.getCFMLFactories().entrySet().iterator();
		Entry<String, CFMLFactory> entry;
		FDThreadImpl thread;
		while(it.hasNext()){
			entry = it.next();
			thread = getByNativeIdentifier( entry.getKey(),(CFMLFactoryImpl) entry.getValue(),id);
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
		Struct pcs = factory.getRunningPageContexts();
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

	@Override
	public String getCompletionMethod() {
		return "serviceCFML";
	}

	@Override
	public String getCompletionType() {
		return CFMLEngineImpl.class.getName();
	}

	@Override
	public void release() {
		this.engine.allowRequestTimeout(true);
	}
}
