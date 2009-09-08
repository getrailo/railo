package railo.runtime.net.rpc.server;

import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.AxisFault;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.op.Caster;
import railo.runtime.type.UDFImpl;

/**
 * 
 */
public final class ComponentController {

	private static ThreadLocal component=new ThreadLocal();
	private static ThreadLocal pagecontext=new ThreadLocal();

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
		catch (PageException e) {
			throw AxisFault.makeFault((e));
		}
	}
	public static Object _invoke(String name, Object[] args) throws PageException {
		Component c=(Component) component.get();
		PageContext p=(PageContext) pagecontext.get();
		if(c==null) throw new ApplicationException("missing component");
		if(p==null) throw new ApplicationException("missing pagecontext");
		
		//print.out(c.getClass().getName()+":"+name);
		for(int i=0;i<args.length;i++) {
			//print.out(" - arg"+i+":"+args[i].getClass().getName());
			args[i]=AxisCaster.toRailoType(args[i]);
		}
		Object udf = c.get(p,name,null);
		String rt="any";
		if(udf instanceof UDFImpl) {
			rt=((UDFImpl)udf).getReturnTypeAsString();
		}
		Object rv = c.call(p, name, args);
		
		try {
			RPCServer server = RPCServer.getInstance(p.getId(),p.getServletContext());
			TypeMapping tm = server.getEngine().getTypeMappingRegistry().getDefaultTypeMapping();
			return AxisCaster.toAxisType(tm,Caster.castTo(p, rt, rv, false));
		} 
		catch (AxisFault af) {
			throw Caster.toPageException(af);
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
