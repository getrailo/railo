package railo.runtime.net.rpc.server;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Scope;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.JavaProvider;
import org.apache.axis.providers.java.RPCProvider;

import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.exp.PageException;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ComponentUtil;


/**
 * Handle Component as Webservice
 */
public final class ComponentHandler extends BasicHandler {
    
    private static Map soapServices = new WeakHashMap();

    /**
     * Just set up the service, the inner service will do the rest...
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        try {
            setupService(msgContext);
        } 
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }
    
    /**
     * Just set up the service, the inner service will do the rest...
     * @see org.apache.axis.Handler#generateWSDL(org.apache.axis.MessageContext)
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        try {
            setupService(msgContext);
        } 
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }
    
    /**
     * handle all the work necessary set
     * up the "proxy" RPC service surrounding it as the MessageContext's
     * active service.
     *
     */ 
    protected void setupService(MessageContext msgContext) throws Exception {
        RefBoolean isnew=new RefBooleanImpl(false);
        ComponentWrap cfc=(ComponentWrap) msgContext.getProperty(Constants.COMPONENT);
        
        Class clazz = ComponentUtil.getComponentJavaAccess(cfc.getComponentAccess(), isnew,true,false,"cf."+cfc.getName());
        
        
        String clazzName=clazz.getName();
        
        ClassLoader classLoader=clazz.getClassLoader();
        Pair pair;
        SOAPService rpc=null;
        if(!isnew.toBooleanValue() && (pair = (Pair)soapServices.get(clazzName))!=null) {
        	if(classLoader==pair.classloader)
        		rpc=pair.rpc;
        }
        //else classLoader = clazz.getClassLoader();
        
        //print.out("cl:"+classLoader);
        msgContext.setClassLoader(classLoader);
        
        if (rpc == null) {
            rpc = new SOAPService(new RPCProvider());
            rpc.setName(clazzName);
            rpc.setOption(JavaProvider.OPTION_CLASSNAME, clazzName );
            rpc.setEngine(msgContext.getAxisEngine());
            
            rpc.setOption(JavaProvider.OPTION_ALLOWEDMETHODS, "*");
            rpc.setOption(JavaProvider.OPTION_SCOPE, Scope.REQUEST.getName());
            rpc.getInitializedServiceDesc(msgContext);
            soapServices.put(clazzName, new Pair(classLoader,rpc));                
        }
        
        rpc.setEngine(msgContext.getAxisEngine());
        rpc.init();   // ??
        msgContext.setService( rpc );
        
    }
    
    class Pair {
    	private ClassLoader classloader;
    	private SOAPService rpc;
		public Pair(ClassLoader classloader, SOAPService rpc) {
			this.classloader = classloader;
			this.rpc = rpc;
		}
    }
}
