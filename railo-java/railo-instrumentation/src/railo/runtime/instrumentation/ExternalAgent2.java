package railo.runtime.instrumentation;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class ExternalAgent2 {

	public static void premain(String agentArgs, Instrumentation inst) {
		setInstrumentation(inst);
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		setInstrumentation(inst);
	}

	private static void setInstrumentation(Instrumentation inst) {
		if(inst!=null) {
			try{
				System.out.println("start set instrumentation");
				System.out.println(Thread.currentThread().getContextClassLoader().getClass().getName());
				System.out.println(ClassLoader.getSystemClassLoader().getClass().getName());
				System.out.println(new ExternalAgent2().getClass().getClassLoader().getClass().getName());
				
				
				
				//railo.loader.engine.CFMLEngineFactory
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("railo.loader.engine.CFMLEngineFactory");
				System.out.println("loaded class:"+clazz);
				
				// getInstance()
				Method m = clazz.getMethod("getInstance", new Class[0]);
				System.out.println("loaded method:"+m);
				Object engine = m.invoke(null, new Object[0]);
				
				// setInstrumentation 
				m=engine.getClass().getMethod("setInstrumentation", new Class[]{Instrumentation.class});
				System.out.println("loaded method:"+m);
				m.invoke(engine, new Object[]{inst});
			}
			catch(Throwable t){
				t.printStackTrace();
			}
		}
	}
}