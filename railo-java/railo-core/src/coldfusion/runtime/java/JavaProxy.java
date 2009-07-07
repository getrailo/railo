package coldfusion.runtime.java;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.java.JavaObject;

public class JavaProxy extends JavaObject {

	public JavaProxy(Class clazz) {
		super(ThreadLocalPageContext.get().getVariableUtil(),clazz);
	}
	public JavaProxy(Object obj) {
		super(ThreadLocalPageContext.get().getVariableUtil(),obj);
	}

    public Object invoke(String methodName, Object args[], PageContext pc) throws Exception {
       return call(pc, methodName, args);
    }
}
