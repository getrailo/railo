

package railo.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.server.ComponentController;
import railo.runtime.net.rpc.server.RPCServer;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.StructUtil;

/**
 * A Page that can produce Components
 */
public abstract class ComponentPage extends Page  {
	
	private static final railo.runtime.type.Collection.Key FIELDNAMES = KeyImpl.getInstance("fieldnames");
	private static final railo.runtime.type.Collection.Key METHOD = KeyImpl.getInstance("method");
	private long lastCheck=-1;
	
	
	public abstract ComponentImpl newInstance(PageContext pc,String callPath,boolean isRealPath)
	throws railo.runtime.exp.PageException; 
	
	/**
	 * @see railo.runtime.Page#call(railo.runtime.PageContext)
	 */
	public synchronized void call(PageContext pc) throws PageException {
        try {
            pc.setSilent();
            ComponentWrap component;
            try {
                component=new ComponentWrap(Component.ACCESS_REMOTE,newInstance(pc,getPageSource().getComponentName(),false));
            }
            finally {
                pc.unsetSilent();
            }
            
			String qs=pc. getHttpServletRequest().getQueryString();
            if(pc.getBasePageSource()==this.getPageSource())
            	pc.getDebugger().setOutput(false);
            boolean isPost=pc. getHttpServletRequest().getMethod().equalsIgnoreCase("POST");
            Object method;
            
            // POST
            if(isPost) {
            	// Soap
            	if(isSoap(pc)) { 
            		callWebservice(pc,component);
            		//close(pc);
                    return;
            	}
    			// WDDX
                else if((method=pc.urlFormScope().get("method",null))!=null) {
                    callWDDX(pc,component,Caster.toString(method));
            		//close(pc);
                    return;
                }
            	
            }
            
            // GET
            else {
            	// WSDL
                if(qs!=null && qs.trim().equalsIgnoreCase("wsdl")) {
                    callWSDL(pc,component);
            		//close(pc);
                    return;
                } 
    			// WDDX
                else if((method=pc.urlFormScope().get("method",null))!=null) {
                    callWDDX(pc,component,Caster.toString(method));
                    //close(pc);
                    return;
                } 
            }
            
            
            // Include MUST
            Array path = pc.getTemplatePath();
            
            if(path.size()>1) {
            	Key[] keys = component.keys();
            	Object el;
            	Scope var = pc.variablesScope();
            	for(int i=0;i<keys.length;i++) {
            		el=component.get(keys[i]);
            		if(el instanceof UDF) 
            			var.set(keys[i], el);
            	}
            	return;
            }
            
			// DUMP
			//TODO component.setAccess(pc,Component.ACCESS_PUBLIC);
			String cdf = pc.getConfig().getComponentDumpTemplate();
			if(cdf!=null && cdf.trim().length()>0) {
			    pc.variablesScope().set("component",component);
			    pc.doInclude(pc.getRelativePageSource(cdf));
			}
			else pc.write(pc.getConfig().getDefaultDumpWriter().toString(pc,component.toDumpData(pc,9999,DumpUtil.toDumpProperties() ),true));
			
		}
		catch(Throwable t) {
			throw Caster.toPageException(t);//Exception Handler.castAnd Stack(t, this, pc);
		}
	}
	
	/*private void close(PageContext pc) {
		//pc.close();
	}*/

	public static  boolean isSoap(PageContext pc) {
		HttpServletRequest req = pc.getHttpServletRequest();
		InputStream is=null;
		try {
			is=req.getInputStream();
			
			String input = IOUtil.toString(is,"iso-8859-1");
			return StringUtil.indexOfIgnoreCase(input, "soap")!=-1;
		} 
		catch (IOException e) {
			return false;
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	
    private void callWDDX(PageContext pc, Component component, String method) throws PageException, IOException, ConverterException {
        Struct url = StructUtil.duplicate(pc.urlFormScope(),true);
        
        url.removeEL(FIELDNAMES);
        url.removeEL(METHOD);
        Object args=url.get("argumentCollection",null);
        Object returnFormat=url.get("returnFormat",null);
        Object rtn=null;
        if(args==null){
        	args=pc.getHttpServletRequest().getAttribute("argumentCollection");
        }
        
        
        // call
        if(args==null){
        	url=translate(component,method,url);
        	rtn = component.callWithNamedValues(pc, method, url);
        }
        else if(Decision.isCastableToArray(args))
        	rtn = component.call(pc, method.toString(), Caster.toNativeArray(args));
        else if(Decision.isCastableToStruct(args))
        	rtn = component.callWithNamedValues(pc, method.toString(), Caster.toStruct(args,false));
        else {
        	Object[] ac=new Object[1];
        	ac[0]=args;
        	rtn = component.call(pc, method.toString(), ac);
        }
        
        
        if(rtn!=null){
        	if(pc.getHttpServletRequest().getHeader("AMF-Forward")!=null) {
        		pc.variablesScope().setEL("AMF-Forward", rtn);
        		//ThreadLocalWDDXResult.set(rtn);
        	}
        	else {
        		Object o = component.get(method.toString(),null);
        		int format=UDF.RETURN_FORMAT_WDDX;
        		int type=CFTypes.TYPE_ANY;
        		String strType="any";
        		boolean secureJson=pc.getApplicationContext().getSecureJson();
        		if(o instanceof UDF) {
        			UDF udf = ((UDF)o);
        			format=udf.getReturnFormat();
        			type=udf.getReturnType();
        			strType=udf.getReturnTypeAsString();
        			if(udf.getSecureJson()!=null)secureJson=udf.getSecureJson().booleanValue();
        		}
        		if(!StringUtil.isEmpty(returnFormat)){
        			format=UDFImpl.toReturnFormat(Caster.toString(returnFormat));
        		}
        		
        		// return type XML ignore WDDX
        		if(type==CFTypes.TYPE_XML) {
        			if(UDF.RETURN_FORMAT_WDDX==format)
        				format=UDF.RETURN_FORMAT_PLAIN;
        			rtn=Caster.toString(Caster.toXML(rtn));
        		}
        		// function dooes no real cast, only check it
        		else rtn=Caster.castTo(pc, (short)type, strType, rtn);
        		
        		

        		// WDDX
        		if(UDF.RETURN_FORMAT_WDDX==format) {
	        		WDDXConverter converter = new WDDXConverter(pc.getTimeZone(),false);
	                converter.setTimeZone(pc.getTimeZone());
	        		pc.forceWrite(converter.serialize(rtn));
        		}
        		// JSON
        		else if(UDF.RETURN_FORMAT_JSON==format) {
	        		JSONConverter converter = new JSONConverter();
	        		if(secureJson)
	        			pc.forceWrite(pc.getApplicationContext().getSecureJsonPrefix());
	                pc.forceWrite(converter.serialize(rtn,false));
        		}
        		// Serialize
        		else if(UDF.RETURN_FORMAT_SERIALIZE==format) {
        			ScriptConverter converter = new ScriptConverter();
        			pc.forceWrite(converter.serialize(rtn));
        		}
        		// Plain
        		else if(UDF.RETURN_FORMAT_PLAIN==format) {
	        		pc.forceWrite(Caster.toString(rtn));
        		}
        		
        		
        	}
        }
        
    }
    
    private Struct translate(Component c, String strMethod, Struct params) {
		Key[] keys = params.keys();
		FunctionArgument[] args=null;
		int index=-1;
		Object value;
    	for(int i=0;i<keys.length;i++){
    		index=Caster.toIntValue(keys[i].getString(),0);
    		if(index>0)	{
    			if(args==null)args=_getArgs(c,strMethod);
    			if(args!=null && index<=args.length) {
    				value=params.removeEL(keys[i]);
    				if(value!=null)params.setEL(args[index-1].getName(), value);
    			}
    		}
    		
    	}
    	return params;
	}

	private FunctionArgument[] _getArgs(Component c, String strMethod) {
		Object o=c.get(strMethod,null);
		if(o instanceof UDF) return ((UDF) o).getFunctionArguments();
		return null;
	}

	private void callWSDL(PageContext pc, ComponentWrap component) throws ServletException, IOException, ExpressionException {
    	// take wsdl file defined by user
    	String wsdl = component.getWSDLFile();
    	if(!StringUtil.isEmpty(wsdl)) {
    		
    		OutputStream os=null;// FUTURE add to interface
    		Resource input = ResourceUtil.toResourceExisting(pc, wsdl);
    		try {
    			os=pc.getServletOutputStream();
				pc.getResponse().setContentType("text/xml; charset=utf-8");
    			IOUtil.copy(input, os, false);
    			
    		}
    		finally {
    			IOUtil.flushEL(os);
                IOUtil.closeEL(os);
                ((PageContextImpl)pc).getRootOut().setClosed(true);
    		}
    	}
    	// create a wsdl file
    	else {
	    	RPCServer.getInstance(pc.getId(),pc.getServletContext())
	        	.doGet(pc.getHttpServletRequest(), pc. getHttpServletResponse(), component);
    	}
    }
    
    private void callWebservice(PageContext pc, Component component) throws IOException, ServletException {
    	ComponentController.set(pc, component);
    	try {
        	RPCServer.getInstance(pc.getId(),pc.getServletContext())
        		.doPost(pc.getHttpServletRequest(), pc. getHttpServletResponse(), component);
    	}
    	finally {
    		ComponentController.release();
    	}
    }
    

    public abstract void initComponent(PageContext pc,ComponentImpl c) throws PageException;

	public void ckecked() {
		lastCheck=System.currentTimeMillis();
	}

	public long lastCheck() {
		return lastCheck;
	}
    
}