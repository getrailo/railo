package railo.runtime.instrumentation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;


import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.SystemOut;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.InfoImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import sun.instrument.InstrumentationImpl;
import sun.management.VMManagement;

public class InstrumentationFactory {
	
	private static Instrumentation inst;
	private static boolean doInit=true;
	
	public static synchronized Instrumentation getInstance() {
		
		//System.loadLibrary("instrument");
		
		//if(true) return new InstrumentationImpl(0,true,true);
		
		
		if(doInit) {
			doInit=false;
			
			
			
			
			/*Class agent = ClassUtil.loadClass("railo.runtime.instrumentation.Agent",null);
			if(agent==null) {
				SystemOut.printDate("missing class railo.runtime.instrumentation.Agent");
				return null;
			}*/
			
			// if Agent was loaded at startup there is already a Instrumentation
			inst=Agent.getInstrumentation(null);
			//inst=getInstrumentation(agent);
			
			
			// try to load directly
			if(inst==null) {
				print.e(new Date(140697380388720L));
				try{
					Constructor<InstrumentationImpl> constr = InstrumentationImpl.class.getDeclaredConstructor(new Class[]{long.class,boolean.class,boolean.class});
					constr.setAccessible(true);
					inst=constr.newInstance(new Object[]{140697380388721L,true,true});
				
				}
				catch(Throwable t){
					print.e(t);
				}
			}

			// try to load Agent
			if(inst==null) {
				
				SystemOut.printDate("class railo.runtime.instrumentation.Agent.getInstrumentation() is not returning a Instrumentation");
				try {
					String id=getPid();
					String path=getResourcFromLib(ThreadLocalPageContext.getConfig()).getAbsolutePath();
					
					print.e("agent:"+path);
					
					Class vmClass = ClassUtil.loadClass("com.sun.tools.attach.VirtualMachine");
					Object vmObj=attach(vmClass,id);
					loadAgent(vmClass,vmObj,path);
					detach(vmClass,vmObj);
				} 
				catch (Throwable t) {
					if(t instanceof InvocationTargetException)
						print.e(((InvocationTargetException)t).getTargetException());
					else 
						print.e(t);
					return null;
				}
				inst=Agent.getInstrumentation(null);
			}
			
			if(inst!=null) {
				try{
					print.e(inst.getClass().getName());
					print.e(inst.getClass().getDeclaredFields());
					
					Field f = inst.getClass().getDeclaredField("mNativeAgent");
					f.setAccessible(true);
					print.e("mNativeAgent:"+f.get(inst));
				}
				catch(Throwable t){
					print.e(t);
				}
				SystemOut.printDate("java.lang.instrument.Instrumentation is used to reload class files");
		}
				
		}
		return inst;
	}

	/*private static Instrumentation getInstrumentation(Class agent) {
		try {
			Method getInstrumentation = agent.getMethod("getInstrumentation", new Class[0]);
			return (Instrumentation) getInstrumentation.invoke(null, new Object[0]);
		} 
		catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}*/

	private static Object attach(Class vmClass, String id) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method attach = vmClass.getMethod("attach", new Class[]{String.class});
		return attach.invoke(null, new Object[]{id});
	}
	
	private static void loadAgent(Class vmClass, Object vmObj, String path) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method loadAgent = vmClass.getMethod("loadAgent", new Class[]{String.class});
		loadAgent.invoke(vmObj, new Object[]{path});
	}
	
	private static void detach(Class vmClass, Object vmObj) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method detach = vmClass.getMethod("detach", new Class[]{});
		detach.invoke(vmObj, new Object[]{});
	}
	
	/*private static Resource getResourcFromLib() {
		Resource[] pathes = SystemUtil.getClassPathes();
		Resource res = null;
		String name=null;
		if(pathes!=null)for(int i=0;i<pathes.length;i++){
			name=pathes[i].getName();
			if(name.equalsIgnoreCase("railo.jar")) {
				res=pathes[i];
				break;
			}
		}
		
		if(res==null) {
			res=getResourcFromLib( Agent.class);
			
		}
		return res;
	}*/
	
	private static Resource getResourcFromLib(Config c) throws IOException {
		Resource dir=ConfigWebUtil.getConfigServerDirectory(c);
		if(dir==null) dir= ResourceUtil.toResource(CFMLEngineFactory.getClassLoaderRoot(TP.class.getClassLoader()));
		
		Resource trg = dir.getRealResource("railo-external-agent.jar");
		if(!trg.exists()) {
			InputStream jar = InfoImpl.class.getResourceAsStream("/resource/lib/railo-external-agent.jar");
			IOUtil.copy(jar, trg,true);
		}
		return trg;
	}
	

	
	

	/*private static Resource getResourcFromLib(Class clazz) {
		String path=clazz.getClassLoader().getResource(".").getFile();
		Resource dir = ResourcesImpl.getFileResourceProvider().getResource(path);
		Resource res = dir.getRealResource("railo-instrumentation.jar");
		if(!res.exists())res=dir.getRealResource("railo-inst.jar");
		if(!res.exists())res=null;
		return res;
	}*/
	
	public static String getPid() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
	    Field jvmField = mxbean.getClass().getDeclaredField("jvm");

	    jvmField.setAccessible(true);
	    VMManagement management = (VMManagement) jvmField.get(mxbean);
	    Method method = management.getClass().getDeclaredMethod("getProcessId");
	    method.setAccessible(true);
	    Integer processId = (Integer) method.invoke(management);

	    return processId.toString();
	}
}