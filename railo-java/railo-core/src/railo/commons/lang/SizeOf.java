package railo.commons.lang;

import railo.commons.management.MemoryInfo;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.instrumentation.InstrumentationFactory;


/**
 * Calculation of object size.
 */
public class SizeOf {
	

	public static long size(Object object) {
		if (object==null)return 0;
		return MemoryInfo.deepMemoryUsageOf(InstrumentationFactory.getInstrumentation(ThreadLocalPageContext.getConfig()),object);
	}

}
