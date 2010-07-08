package railo.runtime;

import java.util.HashMap;
import java.util.Map;

import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;

/**
 * A Page that can produce Components
 */
public abstract class InterfacePage extends PagePlus  {
	
	//private static final railo.runtime.type.Collection.Key FIELDNAMES = KeyImpl.getInstance("fieldnames");
	private static final railo.runtime.type.Collection.Key METHOD = KeyImpl.getInstance("method");
	private static final railo.runtime.type.Collection.Key COMPONENT = KeyImpl.getInstance("component");
	//private InterfaceImpl interfaceimpl;
	

	
	/**
	 * @see railo.runtime.Page#call(railo.runtime.PageContext)
	 */
	public void call(PageContext pc) throws PageException {
        try {
            pc.setSilent();
            InterfaceImpl interf = null;
            try {
                interf = newInstance(getPageSource().getComponentName(),false,new HashMap());
            }
            finally {
                pc.unsetSilent();
            }
            
			String qs=pc. getHttpServletRequest().getQueryString();
            if(pc.getBasePageSource()==this.getPageSource())
            	pc.getDebugger().setOutput(false);
            boolean isPost=pc. getHttpServletRequest().getMethod().equalsIgnoreCase("POST");
            
            // POST
            if(isPost) {
            	// Soap
            	if(ComponentPage.isSoap(pc)) 
            		throw new ApplicationException("can not instantiate interface ["+this.getPageSource().getComponentName()+"] as a component");
            }
            // GET
            else if(qs!=null && qs.trim().equalsIgnoreCase("wsdl")) 
                	throw new ApplicationException("can not instantiate interface ["+this.getPageSource().getComponentName()+"] as a component");	
            
            // WDDX
            if(pc.urlFormScope().containsKey(METHOD)) 
            	throw new ApplicationException("can not instantiate interface ["+this.getPageSource().getComponentName()+"] as a component");
            
            // invoking via include
            if(pc.getTemplatePath().size()>1) {
            	throw new ApplicationException("can not invoke interface ["+this.getPageSource().getComponentName()+"] as a page");
            }
            
			// DUMP
			//TODO component.setAccess(pc,Component.ACCESS_PUBLIC);
			String cdf = pc.getConfig().getComponentDumpTemplate();
			if(cdf!=null && cdf.trim().length()>0) {
			    pc.variablesScope().set(COMPONENT,interf);
			    pc.doInclude(pc.getRelativePageSource(cdf));
			}
			else pc.write(pc.getConfig().getDefaultDumpWriter().toString(pc,interf.toDumpData(pc, 9999,DumpUtil.toDumpProperties()),true));
			
		}
		catch(Throwable t) {
			throw Caster.toPageException(t);//Exception Handler.castAnd Stack(t, this, pc);
		}
	}
	
    public abstract void initInterface(InterfaceImpl i) 
    	throws PageException;

	public abstract InterfaceImpl newInstance(String callPath,boolean isRealPath,Map interfaceUDFs)
		throws railo.runtime.exp.PageException;

}