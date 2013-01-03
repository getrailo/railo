package railo.runtime.instrumentation;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class InstrumentationUtil {

	/**
	 * redefine the class with the given byte array
	 * @param clazz
	 * @param barr
	 * @return
	 */
	public static boolean redefineClassEL(Class clazz, byte[] barr){
		Instrumentation inst = InstrumentationFactory.getInstance();
	    if(inst!=null && inst.isRedefineClassesSupported()) {
	    	try {
	        	inst.redefineClasses(new ClassDefinition(clazz,barr));
				return true;
			} 
	    	catch (Throwable t) {t.printStackTrace();}
	    }
	    return false;
	}

	public static void redefineClass(Class clazz, byte[] barr) throws ClassNotFoundException, UnmodifiableClassException{
		Instrumentation inst = InstrumentationFactory.getInstance();
	    inst.redefineClasses(new ClassDefinition(clazz,barr));
	}

	public static boolean isSupported() {
		Instrumentation inst = InstrumentationFactory.getInstance();
		return (inst!=null && inst.isRedefineClassesSupported());
	} 
}