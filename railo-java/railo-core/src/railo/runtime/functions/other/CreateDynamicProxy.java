package railo.runtime.functions.other;

import java.io.IOException;

import railo.commons.lang.ClassUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.util.ListUtil;
import railo.transformer.bytecode.util.JavaProxyFactory;

public class CreateDynamicProxy implements Function {
	
	private static final long serialVersionUID = -1787490871697335220L;

	public static Object call(PageContext pc , Object oCFC,Object oInterfaces) throws PageException {
		try {
			return _call(pc, oCFC, oInterfaces);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	
	public static Object _call(PageContext pc , Object oCFC,Object oInterfaces) throws PageException, IOException {
		
		// Component
		Component cfc;
		if(oCFC instanceof Component)
			cfc= (Component)oCFC;
		else
			cfc=pc.loadComponent(Caster.toString(oCFC));
		
		// interfaces
		//String[] strInterfaces;
		Object[] arr;
		if(Decision.isArray(oInterfaces)) {
			arr=Caster.toNativeArray(oInterfaces);
		}
		else if(oInterfaces instanceof Class){
			arr=new Object[]{oInterfaces};
		}
		else {
			String list = Caster.toString(oInterfaces);
			arr=ListUtil.listToStringArray(list, ',');
		}
		//strInterfaces=List.trimItems(strInterfaces);
		
		
		ClassLoader cl = pc.getConfig().getClassLoader();
		Class[] interfaces=new Class[arr.length];
		for(int i=0;i<arr.length;i++){
			if(arr[i] instanceof Class) interfaces[i]=(Class) arr[i];
			else interfaces[i]=ClassUtil.loadClass(cl, Caster.toString(arr[i]).trim());
			
			if(!interfaces[i].isInterface()) throw new FunctionException(pc, "CreateDynamicProxy", 2, "interfaces", "definition ["+interfaces[i].getName()+"] is a class and not a interface");
		}
		
		return JavaProxyFactory.createProxy(pc.getConfig(),cfc, null,interfaces);
	}
	    
}
