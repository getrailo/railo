package railo.runtime.net.rpc.server;

import java.lang.reflect.Method;

import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.MessageContext;
import org.apache.axis.providers.java.RPCProvider;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisCaster;

public final class ComponentProvider extends RPCProvider {

	public static final String PAGE_CONTEXT = PageContext.class.getName();
	public static final String COMPONENT = Component.class.getName();
 
	
	@Override
	protected Object invokeMethod(MessageContext mc, Method method, Object trg, Object[] args) throws Exception {
		PageContext pc=(PageContext) mc.getProperty(Constants.PAGE_CONTEXT);
		Component c= (Component) mc.getProperty(Constants.COMPONENT);
        
		RPCServer server = RPCServer.getInstance(pc.getId(),pc.getServletContext());
		TypeMapping tm = server.getEngine().getTypeMappingRegistry().getDefaultTypeMapping();
		
		return AxisCaster.toAxisType(tm,c.call(pc,method.getName(),toRailoType(pc,args)),null);
	}

	private Object[] toRailoType(PageContext pc,Object[] args) throws PageException {
		Object[] trgs=new Object[args.length];
		for(int i=0;i<trgs.length;i++) {
			trgs[i]=AxisCaster.toRailoType(pc,args[i]);
		}
		return trgs;
	}
	

}
