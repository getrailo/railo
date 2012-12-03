package railo.runtime.net.rpc.server;

import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.AxisFault;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;

/**
 * 
 */
public final class ComponentController {

	private static ThreadLocal<Component> component=new ThreadLocal<Component>();
	private static ThreadLocal<PageContext> pagecontext=new ThreadLocal<PageContext>();

	/**
	 * invokes thread local component
	 * @param name
	 * @param args
	 * @return
	 * @throws AxisFault 
	 * @throws PageException
	 */
	public static Object invoke(String name, Object[] args) throws AxisFault {
		try {
			return _invoke(name, args);
		} 
		catch (Throwable t) {
			throw AxisFault.makeFault((Caster.toPageException(t)));
		}
	}
	public static Object _invoke(String name, Object[] args) throws PageException {
		Key key = Caster.toKey(name);
		Component c=component.get();
		PageContext p=pagecontext.get();
		if(c==null) throw new ApplicationException("missing component");
		if(p==null) throw new ApplicationException("missing pagecontext");
		
		for(int i=0;i<args.length;i++) {
			args[i]=AxisCaster.toRailoType(p,args[i]);
		}
		
		Object udf = c.get(p,key,null);
		String rt="any";
		if(udf instanceof UDF) {
			rt=((UDF)udf).getReturnTypeAsString();
		}
		Object rv = c.call(p, key, args);
		
		try {
			RPCServer server = RPCServer.getInstance(p.getId(),p.getServletContext());
			TypeMapping tm = server.getEngine().getTypeMappingRegistry().getDefaultTypeMapping();
			rv=Caster.castTo(p, rt, rv, false);
			Class clazz = Caster.cfTypeToClass(rt);
			return AxisCaster.toAxisType(tm,rv,clazz.getComponentType()!=null?clazz:null);
		} 
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
	}

	/**
	 * removes PageContext and Component
	 * sets component and pageContext to invoke
	 * @param p
	 * @param c
	 */
	public static void set(PageContext p,Component c) {
		pagecontext.set(p);
		component.set(c);
	}
	
	/**
	 * 
	 */
	public static void release() {
		pagecontext.set(null);
		component.set(null);
	}
}
