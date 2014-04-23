package railo.runtime.instrumentation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.InfoImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageRuntimeException;
import sun.management.VMManagement;

class InstrFactory {
	private static final String VIRTUAL_MACHINE_CLASSNAME = "com.sun.tools.attach.VirtualMachine"; 
	private static Instrumentation inst;
	

	private static Instrumentation getInstance(Config config,Instrumentation defaultValue) {
		try {
			return getInstance(config);
		}
		catch (Throwable e) {
			return defaultValue;
		}
	}
	
	private static synchronized Instrumentation getInstance(Config config)  {
		if(inst==null) {
			
			//Log null;
			inst=InstrumentationFactory.getInstrumentation(config);
			print.e("INST?:"+(inst!=null));
			if(inst!=null) return inst;
			
			
			
			// try to load the Agent
			Class vmClass = ClassUtil.loadClass(config.getClassLoader(),VIRTUAL_MACHINE_CLASSNAME,null);
			

			String javaSpecVersion = System.getProperty("java.specification.version");
			//   static final boolean jdk6OrLater = "1.6".equals(javaSpecVersion) || "1.7".equals(javaSpecVersion);
			print.e(javaSpecVersion);
			print.e("VirtualMachine?:"+(vmClass!=null));
			
			try {
				print.e(providers());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			inst=Agent.getInstrumentation(null);
			if(inst!=null) return inst;
			
			// 
			
			
			
			
				
		}
		if(inst!=null) return inst;
		throw noAgent();
	}
	

	private static PageRuntimeException noAgent() {
		return new PageRuntimeException("Railo was no able to load a Agent");// TODO improve message
	}

	private static void loadAgent(Class<? extends Object> vmClass) {
		try {
			
			String id=getPid();
			String path=getResourcFromLib(ThreadLocalPageContext.getConfig()).getAbsolutePath();
			print.e("agent:"+path);
			
			Object vmObj=attach(vmClass,id);
			loadAgent(vmClass,vmObj,path);
			detach(vmClass,vmObj);
		} 
		catch (Throwable t) {
			if(t instanceof InvocationTargetException)
				print.e(((InvocationTargetException)t).getTargetException());
			else 
				print.e(t);
		}
		
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
	
	private static List<?> providers() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassException {
		Class pClass = ClassUtil.loadClass("com.sun.tools.attach.spi.AttachProvider");
		Method m = pClass.getMethod("providers", new Class[]{});
		return (List<?>) m.invoke(null, new Object[]{});
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
	
	private static String getPid() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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