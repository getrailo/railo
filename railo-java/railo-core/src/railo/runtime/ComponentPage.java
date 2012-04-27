package railo.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.CFTypes;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.converter.XMLConverter;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.net.rpc.server.ComponentController;
import railo.runtime.net.rpc.server.RPCServer;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.rest.RestUtil;
import railo.runtime.rest.Result;
import railo.runtime.rest.path.Path;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructUtil;

/**
 * A Page that can produce Components
 */
public abstract class ComponentPage extends Page  {
	
	private static final long serialVersionUID = -3483642653131058030L;
	
	public static final railo.runtime.type.Collection.Key METHOD = KeyImpl.intern("method");
	public static final railo.runtime.type.Collection.Key QUERY_FORMAT = KeyImpl.intern("queryFormat");
	//public static final railo.runtime.type.Collection.Key REMOTE_PERSISTENT = KeyImpl.intern("remotePersistent");
	public static final railo.runtime.type.Collection.Key REMOTE_PERSISTENT_ID = KeyImpl.intern("Id16hohohh");

	//public static final short REMOTE_PERSISTENT_REQUEST = 1;
	//public static final short REMOTE_PERSISTENT_SESSION = 2;
	//public static final short REMOTE_PERSISTENT_APPLICATION = 4;
	//public static final short REMOTE_PERSISTENT_SERVER = 8;
	
	
	private long lastCheck=-1;
	
	
	public abstract ComponentImpl newInstance(PageContext pc,String callPath,boolean isRealPath)
		throws railo.runtime.exp.PageException; 
	
	/**
	 * @see railo.runtime.Page#call(railo.runtime.PageContext)
	 */
	public void call(PageContext pc) throws PageException {
		// remote persistent (only type server is supported)
		String strRemotePersisId = Caster.toString(pc.urlFormScope().get(REMOTE_PERSISTENT_ID,null),null);
		if(!StringUtil.isEmpty(strRemotePersisId,true)) {
			strRemotePersisId=strRemotePersisId.trim();
		}
		else strRemotePersisId=null;
		
		HttpServletRequest req = pc.getHttpServletRequest();
		// client
		String client = Caster.toString(req.getAttribute("client"),null);
		// call type (invocation, store-only)
		String callType = Caster.toString(req.getAttribute("call-type"),null);
		boolean fromGateway="railo-gateway-1-0".equals(client);
		boolean fromRest="railo-rest-1-0".equals(client);
		Component component;
        try {
            pc.setSilent();
            // load the cfc
            try {
	            if(fromGateway && strRemotePersisId!=null) {
	            	ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
	            	GatewayEngineImpl engine = config.getGatewayEngine();
	            	component=engine.getPersistentRemoteCFC(strRemotePersisId);
	            	
	            	if(component==null) {
	            		component=newInstance(pc,getPageSource().getComponentName(),false);
	            		if(!fromGateway)component=ComponentWrap.toComponentWrap(Component.ACCESS_REMOTE,component);
	            		
	            		engine.setPersistentRemoteCFC(strRemotePersisId,component);
	            	}
	            	
	            }
	            else {
	            	component=newInstance(pc,getPageSource().getComponentName(),false);
            		if(!fromGateway)component=ComponentWrap.toComponentWrap(Component.ACCESS_REMOTE,component);
	            }
            }
            finally {
                pc.unsetSilent();
            }
            
            // Only get the Component, no invocation
            if("store-only".equals(callType)) {
            	req.setAttribute("component", component);
            	return;
            }
            
            
            
            // METHOD INVOCATION
			String qs=ReqRspUtil.getQueryString(pc.getHttpServletRequest());
            if(pc.getBasePageSource()==this.getPageSource())
            	pc.getDebugger().setOutput(false);
            boolean isPost=pc.getHttpServletRequest().getMethod().equalsIgnoreCase("POST");
            
            boolean suppressContent = ((ConfigImpl)pc.getConfig()).isSuppressContent();
            if(suppressContent)pc.clear();
            Object method;
            
            if(fromRest){ 
            	
            	callRest(pc,component,Caster.toString(req.getAttribute("rest-path"),""),(Result)req.getAttribute("rest-result"),suppressContent);
            	return;
            }
            
            
            
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
                    callWDDX(pc,component,KeyImpl.toKey(method),suppressContent);
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
                    callWDDX(pc,component,KeyImpl.toKey(method),suppressContent);
                    //close(pc); 
                    return;
                } 
            }
            
            
            // Include MUST
            Array path = pc.getTemplatePath();
            //if(path.size()>1 ) {
            if(path.size()>1 && !(path.size()==3 && List.last(path.getE(2).toString(),"/\\",true).equalsIgnoreCase(Constants.APP_CFC)) ) {// MUSTMUST bad impl -> check with and without application.cfc
            	
            	ComponentWrap c = ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE,ComponentUtil.toComponentAccess(component));
            	Key[] keys = c.keys();
            	Object el;
            	Scope var = pc.variablesScope();
            	for(int i=0;i<keys.length;i++) {
            		el=c.get(keys[i],null);
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
			    pc.doInclude(cdf);
			}
			else pc.write(pc.getConfig().getDefaultDumpWriter().toString(pc,component.toDumpData(pc,9999,DumpUtil.toDumpProperties() ),true));
			
		}
		catch(Throwable t) {
			throw Caster.toPageException(t);//Exception Handler.castAnd Stack(t, this, pc);
		}
	}
	
	private void callRest(PageContext pc, Component component, String path, Result result, boolean suppressContent) throws IOException, ConverterException {
		String method = pc.getHttpServletRequest().getMethod();
		String[] subPath = result.getPath();
		
		Iterator<Entry<Key, Object>> it = component.entryIterator();
		Entry<Key, Object> e;
		Object value;
		UDF udf;
		Struct meta;
		boolean hasFunction=false;
		while(it.hasNext()){
			e = it.next();
			value=e.getValue();
			if(value instanceof UDF){
				udf=(UDF)value;
				try {
					meta = udf.getMetaData(pc);
					String httpMethod = Caster.toString(meta.get(RestUtil.HTTP_METHOD,null),null);
					if(StringUtil.isEmpty(httpMethod) || !httpMethod.equalsIgnoreCase(method)) continue;
					
					String restPath = Caster.toString(meta.get(RestUtil.REST_PATH,null),null);
					
					// no rest path
					if(StringUtil.isEmpty(restPath)){
						if(ArrayUtil.isEmpty(subPath)) {
							hasFunction=true;
							_callRest(pc, component, udf, path, result.getVariables(),result, suppressContent,e.getKey());
							break;
						}
					}
					else {
						Struct var = result.getVariables();
						int index=RestUtil.matchPath(var, Path.init(restPath)/*TODO cache this*/, result.getPath());
						if(index>=0 && index+1==result.getPath().length) {
							hasFunction=true;
							_callRest(pc, component, udf, path, var,result, suppressContent,e.getKey());
							break;
						}
					}
				} 
				catch (PageException pe) {}
			}
		}
		
		if(!hasFunction)
			RestUtil.setStatus(pc,404,"no rest service for ["+path+"] found");
    	
	}

	private void _callRest(PageContext pc, Component component, UDF udf,String path, Struct variables, Result result, boolean suppressContent, Key methodName) throws PageException, IOException, ConverterException {
		FunctionArgument[] fa=udf.getFunctionArguments();
		Struct args=new StructImpl();
		Key name;
		String restArgSource;
		for(int i=0;i<fa.length;i++){
			name = fa[i].getName();
			restArgSource=Caster.toString(fa[i].getMetaData().get(RestUtil.REST_ARG_SOURCE,""),"");
			if("path".equalsIgnoreCase(restArgSource))
				args.setEL(name, variables.get(name,null));
			if("query".equalsIgnoreCase(restArgSource) || "url".equalsIgnoreCase(restArgSource))
				args.setEL(name, pc.urlScope().get(name,null));
			if("form".equalsIgnoreCase(restArgSource))
				args.setEL(name, pc.formScope().get(name,null));
			if("cookie".equalsIgnoreCase(restArgSource))
				args.setEL(name, pc.cookieScope().get(name,null));
			if("header".equalsIgnoreCase(restArgSource) || "head".equalsIgnoreCase(restArgSource))
				args.setEL(name, ReqRspUtil.getHeaderIgnoreCase(pc, name.getString(), null));
			if("matrix".equalsIgnoreCase(restArgSource))
				args.setEL(name, result.getMatrix().get(name,null));
			
			// TODO matrix, header, else
		}
		Object rtn=null;
		try{
    		if(suppressContent)pc.setSilent();
			rtn = component.callWithNamedValues(pc, methodName, args);
		} 
		catch (PageException e) {
			RestUtil.setStatus(pc, 500, ExceptionUtil.getMessage(e));
		}
    	finally {
    		if(suppressContent)pc.unsetSilent();
    	}
    	
    	// custom response
		Struct sct = result.getCustomResponse();
    	if(sct!=null){
			HttpServletResponse rsp = pc.getHttpServletResponse();
    		// status
    		int status = Caster.toIntValue(sct.get(KeyConstants._status,null),200);
    		rsp.setStatus(status);
			
    		// content
    		String content=Caster.toString(sct.get(KeyConstants._content,null),null);
    		if(content!=null) {
				try {
					pc.forceWrite(content);
				} 
				catch (IOException e) {}
    		}
    		
    		// headers
    		Struct headers=Caster.toStruct(sct.get(KeyConstants._headers,null),null);
    		if(headers!=null){
    			//Key[] keys = headers.keys();
    			Iterator<Entry<Key, Object>> it = headers.entryIterator();
    			Entry<Key, Object> e;
    			String n,v;
    			Object tmp;
    			while(it.hasNext()){
    				e = it.next();
    				n=e.getKey().getString();
    				tmp=e.getValue();
    				v=Caster.toString(tmp,null);
    				if(tmp!=null && v==null) v=tmp.toString();
    				rsp.setHeader(n, v);
    			}	
    		}
		}
    	// convert result
		else if(rtn!=null){
        	Props props = new Props();
        	props.format=result.getFormat();
        	
        	pc.forceWrite(convertResult(pc, props, null, rtn));
        }
		
	}

	public static  boolean isSoap(PageContext pc) {
		HttpServletRequest req = pc.getHttpServletRequest();
		InputStream is=null;
		try {
			is=req.getInputStream();
			
			String input = IOUtil.toString(is,"iso-8859-1");
			return 
			StringUtil.indexOfIgnoreCase(input, "soap:Envelope")!=-1 || 
			StringUtil.indexOfIgnoreCase(input, "soapenv:Envelope")!=-1 || 
				StringUtil.indexOfIgnoreCase(input, "SOAP-ENV:Envelope")!=-1;
		} 
		catch (IOException e) {
			return false;
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	
    private void callWDDX(PageContext pc, Component component, Collection.Key methodName, boolean suppressContent) throws IOException, ConverterException, PageException {
    	Struct url = StructUtil.duplicate(pc.urlFormScope(),true);

        // define args
        url.removeEL(KeyImpl.FIELD_NAMES);
        url.removeEL(METHOD);
        Object args=url.get(KeyImpl.ARGUMENT_COLLECTION,null);
        Object returnFormat=url.get(KeyImpl.RETURN_FORMAT,null);
        Object queryFormat=url.get(QUERY_FORMAT,null);
        
        if(args==null){
        	args=pc.getHttpServletRequest().getAttribute("argumentCollection");
        }
        
      //content-type
        Object o = component.get(methodName,null);
        Props props = getProps(pc, o, returnFormat);
        if(!props.output) setFormat(pc.getHttpServletResponse(),props.format);
        	
        
        Object rtn=null;
        try{
    		if(suppressContent)pc.setSilent();
        
	        
	        if(args==null){
	        	url=translate(component,methodName.getString(),url);
	        	rtn = component.callWithNamedValues(pc, methodName, url);
	        }
	        else if(args instanceof String){
	        	try {
					args=new JSONExpressionInterpreter().interpret(pc, (String)args);
					
				} catch (PageException e) {}
	        }
	        
	        // call
	        if(args!=null) {
	        	if(Decision.isCastableToStruct(args)){
		        	rtn = component.callWithNamedValues(pc, methodName, Caster.toStruct(args,false));
		        }
		        else if(Decision.isCastableToArray(args)){
		        	rtn = component.call(pc, methodName, Caster.toNativeArray(args));
		        }
		        else {
		        	Object[] ac=new Object[1];
		        	ac[0]=args;
		        	rtn = component.call(pc, methodName, ac);
		        }
	        }
        }
    	finally {
    		if(suppressContent)pc.unsetSilent();
    	}
        // convert result
        if(rtn!=null){
        	if(pc.getHttpServletRequest().getHeader("AMF-Forward")!=null) {
        		pc.variablesScope().setEL("AMF-Forward", rtn);
        	}
        	else {
        		pc.forceWrite(convertResult(pc, props, queryFormat, rtn));
        	}
        }
        
    }
    
    private void setFormat(HttpServletResponse rsp, int format) {
    	switch(format){
        case UDF.RETURN_FORMAT_WDDX:
        	rsp.setContentType("text/xml; charset=UTF-8");
        	rsp.setHeader("Return-Format", "wddx");
        break;
        case UDF.RETURN_FORMAT_JSON:
        	rsp.setContentType("application/json");
        	rsp.setHeader("Return-Format", "json");
        break;
        case UDF.RETURN_FORMAT_PLAIN:
        	rsp.setContentType("text/plain; charset=UTF-8");
        	rsp.setHeader("Return-Format", "plain");
        break;
        case UDF.RETURN_FORMAT_SERIALIZE:
        	rsp.setContentType("text/plain; charset=UTF-8");
        	rsp.setHeader("Return-Format", "serialize");
        break;
        }
	}

	private static Props getProps(PageContext pc, Object o,Object returnFormat) throws PageException {
    	Props props = new Props();
    	
		props.strType="any";
		props.secureJson=pc.getApplicationContext().getSecureJson();
		if(o instanceof UDF) {
			UDF udf = ((UDF)o);
			props.format=udf.getReturnFormat();
			props.type=udf.getReturnType();
			props.strType=udf.getReturnTypeAsString();
			props.output=udf.getOutput();
			if(udf.getSecureJson()!=null)props.secureJson=udf.getSecureJson().booleanValue();
		}
		if(!StringUtil.isEmpty(returnFormat)){
			props.format=UDFImpl.toReturnFormat(Caster.toString(returnFormat));
		}
    	
		// return type XML ignore WDDX
		if(props.type==CFTypes.TYPE_XML) {
			if(UDF.RETURN_FORMAT_WDDX==props.format)
				props.format=UDF.RETURN_FORMAT_PLAIN;
		}
    	
    	
    	
    	return props;
    }
    
    public static String convertResult(PageContext pc,Component component, String methodName,Object returnFormat,Object queryFormat,Object rtn) throws ConverterException, PageException {
    	Object o = component.get(methodName,null);
    	Props p = getProps(pc, o, returnFormat);
    	return convertResult(pc, p, queryFormat, rtn);
    }
    
    private static String convertResult(PageContext pc,Props props,Object queryFormat,Object rtn) throws ConverterException, PageException {

    	
		// return type XML ignore WDDX
		if(props.type==CFTypes.TYPE_XML) {
			//if(UDF.RETURN_FORMAT_WDDX==format) format=UDF.RETURN_FORMAT_PLAIN;
			rtn=Caster.toString(Caster.toXML(rtn));
		}
		// function does no real cast, only check it
		else rtn=Caster.castTo(pc, (short)props.type, props.strType, rtn);
    	
    	// WDDX
		if(UDF.RETURN_FORMAT_WDDX==props.format) {
			WDDXConverter converter = new WDDXConverter(pc.getTimeZone(),false,false);
            converter.setTimeZone(pc.getTimeZone());
    		return converter.serialize(rtn);
		}
		// JSON
		else if(UDF.RETURN_FORMAT_JSON==props.format) {
			boolean byColumn = false;
    		if(queryFormat instanceof String){
    			String strQF=((String) queryFormat).trim();
    			if(strQF.equalsIgnoreCase("row"));
    			else if(strQF.equalsIgnoreCase("column"))byColumn=true;
    			else throw new ApplicationException("invalid queryformat definition ["+strQF+"], valid formats are [row,column]");
    		}
    		JSONConverter converter = new JSONConverter(false);
    		String prefix="";
    		if(props.secureJson) {
    			prefix=pc.getApplicationContext().getSecureJsonPrefix();
    			if(prefix==null)prefix="";
    		}
            return prefix+converter.serialize(pc,rtn,byColumn);
		}
		// Serialize
		else if(UDF.RETURN_FORMAT_SERIALIZE==props.format) {
			ScriptConverter converter = new ScriptConverter(false);
			return converter.serialize(rtn);
		}
    	// XML
		if(UDF.RETURN_FORMAT_XML==props.format) {
			XMLConverter converter = new XMLConverter(pc.getTimeZone(),false);
            converter.setTimeZone(pc.getTimeZone());
    		return converter.serialize(rtn);
		}
		// Plain
		else if(UDF.RETURN_FORMAT_PLAIN==props.format) {
    		return Caster.toString(rtn);
		}
		return null;
	}

	public static Struct translate(Component c, String strMethod, Struct params) {
		Key[] keys = CollectionUtil.keys(params);
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

	private static FunctionArgument[] _getArgs(Component c, String strMethod) {
		Object o=c.get(strMethod,null);
		if(o instanceof UDF) return ((UDF) o).getFunctionArguments();
		return null;
	}

	private void callWSDL(PageContext pc, Component component) throws ServletException, IOException, ExpressionException {
    	// take wsdl file defined by user
    	String wsdl = component.getWSDLFile();
    	if(!StringUtil.isEmpty(wsdl)) {
    		
    		OutputStream os=null;
    		Resource input = ResourceUtil.toResourceExisting(pc, wsdl);
    		try {
    			os=pc.getResponseStream();
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
	class Props {

		public String strType="any";
		public boolean secureJson;
		public int type=CFTypes.TYPE_ANY;
		public int format=UDF.RETURN_FORMAT_WDDX;
		public boolean output=true;
		
	}