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
import railo.commons.lang.mimetype.MimeType;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.Constants;
import railo.runtime.converter.BinaryConverter;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.converter.JavaConverter;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.converter.XMLConverter;
import railo.runtime.converter.bin.ImageConverter;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.DumpWriter;
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
	
	public static final railo.runtime.type.Collection.Key METHOD = KeyConstants._method;
	public static final railo.runtime.type.Collection.Key REMOTE_PERSISTENT_ID = KeyImpl.intern("Id16hohohh");
	
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
                else if((method=pc.urlFormScope().get(KeyConstants._method,null))!=null) {
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
                else if((method=pc.urlFormScope().get(KeyConstants._method,null))!=null) {
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
			    pc.variablesScope().set(KeyConstants._component,component);
			    pc.doInclude(cdf);
			}
			else pc.write(pc.getConfig().getDefaultDumpWriter(DumpWriter.DEFAULT_RICH).toString(pc,component.toDumpData(pc,9999,DumpUtil.toDumpProperties() ),true));
			
		}
		catch(Throwable t) {
			throw Caster.toPageException(t);//Exception Handler.castAnd Stack(t, this, pc);
		}
	}
	
	private void callRest(PageContext pc, Component component, String path, Result result, boolean suppressContent) throws IOException, ConverterException {
		String method = pc.getHttpServletRequest().getMethod();
		String[] subPath = result.getPath();
		Struct cMeta;
		try {
			cMeta = component.getMetaData(pc);
		} catch (PageException pe) {
			throw ExceptionUtil.toIOException(pe);
		}
		

		// Consumes
		MimeType[] cConsumes=null;
		String strMimeType = Caster.toString(cMeta.get(RestUtil.CONSUMES,null),null);
		if(!StringUtil.isEmpty(strMimeType,true)){
			cConsumes = MimeType.getInstances(strMimeType,',');
		}
		
		// Produces
		MimeType[] cProduces=null;
		strMimeType = Caster.toString(cMeta.get(RestUtil.PRODUCES,null),null);
		if(!StringUtil.isEmpty(strMimeType,true)){
			cProduces = MimeType.getInstances(strMimeType,',');
		}
		
		
		
		Iterator<Entry<Key, Object>> it = component.entryIterator();
		Entry<Key, Object> e;
		Object value;
		UDF udf;
		Struct meta;
		int status=404;
		MimeType bestP,bestC;
		while(it.hasNext()){
			e = it.next();
			value=e.getValue();
			if(value instanceof UDF){
				udf=(UDF)value;
				try {
					meta = udf.getMetaData(pc);
					
					// check if http method match
					String httpMethod = Caster.toString(meta.get(RestUtil.HTTP_METHOD,null),null);
					if(StringUtil.isEmpty(httpMethod) || !httpMethod.equalsIgnoreCase(method)) continue;
					

					// get consumes mimetype
					MimeType[] consumes;
					strMimeType = Caster.toString(meta.get(RestUtil.CONSUMES,null),null);
					if(!StringUtil.isEmpty(strMimeType,true)){
						consumes = MimeType.getInstances(strMimeType,',');
					}
					else
						consumes=cConsumes;
					
					
					// get produces mimetype
					MimeType[] produces;
					strMimeType = Caster.toString(meta.get(RestUtil.PRODUCES,null),null);
					if(!StringUtil.isEmpty(strMimeType,true)){
						produces = MimeType.getInstances(strMimeType,',');
					}
					else
						produces=cProduces;
					
					
					
					
					String restPath = Caster.toString(meta.get(RestUtil.REST_PATH,null),null);
					
					// no rest path
					if(StringUtil.isEmpty(restPath)){
						if(ArrayUtil.isEmpty(subPath)) {
							bestC = best(consumes,result.getContentType());
							bestP = best(produces,result.getAccept());
							if(bestC==null) status=405;
							else if(bestP==null) status=406;
							else {
								status=200;
								_callRest(pc, component, udf, path, result.getVariables(),result,bestP,produces, suppressContent,e.getKey());
								break;
							}
						}
					}
					else {
						Struct var = result.getVariables();
						int index=RestUtil.matchPath(var, Path.init(restPath)/*TODO cache this*/, result.getPath());
						if(index>=0 && index+1==result.getPath().length) {
							bestC = best(consumes,result.getContentType());
							bestP = best(produces,result.getAccept());
							
							if(bestC==null) status=405;
							else if(bestP==null) status=406;
							else {
								status=200;
								_callRest(pc, component, udf, path, var,result,bestP,produces, suppressContent,e.getKey());
								break;
							}
						}
					}
				} 
				catch (PageException pe) {}
			}
		}
		if(status==404)
			RestUtil.setStatus(pc,404,"no rest service for ["+path+"] found");
		else if(status==405)
			RestUtil.setStatus(pc,405,"Unsupported Media Type");
		else if(status==406)
			RestUtil.setStatus(pc,406,"Not Acceptable");
		
    	
	}

	private MimeType best(MimeType[] produces, MimeType... accept) {
		if(ArrayUtil.isEmpty(produces)){
			if(accept.length>0) return accept[0];
			return MimeType.ALL;
		}
		
		MimeType best=null,tmp;
		
		for(int a=0;a<accept.length;a++){
			tmp=accept[a].bestMatch(produces);
			if(tmp!=null && !accept[a].hasWildCards() && tmp.hasWildCards()){
				tmp=accept[a];
			}
			if(tmp!=null && 
					(best==null || 
					 best.getQuality()<tmp.getQuality() || 
					 (best.getQuality()==tmp.getQuality() && best.hasWildCards() && !tmp.hasWildCards())))
				best=tmp;
		}
		
		
		
		return best;
	}

	private void _callRest(PageContext pc, Component component, UDF udf,String path, Struct variables, Result result, MimeType best,MimeType[] produces, boolean suppressContent, Key methodName) throws PageException, IOException, ConverterException {
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
        	
        	if(result.hasFormatExtension()){
        		pc.forceWrite(convertResult(pc, props, null, rtn));
        	}
        	else {
        		if(best!=null && !MimeType.ALL.same(best)) {
            		int f = MimeType.toFormat(best, -1);
            		if(f!=-1) {
            			props.format=f;
            			pc.forceWrite(convertResult(pc, props, null, rtn));
            		}
            		else {
            			writeOut(pc,props,rtn,best);
            		}
            	}
        		else pc.forceWrite(convertResult(pc, props, null, rtn));
        	}
        	
        	
        }
		
	}

	private void writeOut(PageContext pc, Props props, Object obj, MimeType mt) throws PageException, IOException, ConverterException {
		// TODO miemtype mapping with converter defintion from external file
		
		// Images
		if(mt.same(MimeType.IMAGE_GIF)) writeOut(pc,obj,mt,new ImageConverter("gif"));
		else if(mt.same(MimeType.IMAGE_JPG)) writeOut(pc,obj,mt,new ImageConverter("jpeg"));
		else if(mt.same(MimeType.IMAGE_PNG)) writeOut(pc,obj,mt,new ImageConverter("png"));
		else if(mt.same(MimeType.IMAGE_TIFF)) writeOut(pc,obj,mt,new ImageConverter("tiff"));
		else if(mt.same(MimeType.IMAGE_BMP)) writeOut(pc,obj,mt,new ImageConverter("bmp"));
		else if(mt.same(MimeType.IMAGE_WBMP)) writeOut(pc,obj,mt,new ImageConverter("wbmp"));
		else if(mt.same(MimeType.IMAGE_FBX)) writeOut(pc,obj,mt,new ImageConverter("fbx"));
		else if(mt.same(MimeType.IMAGE_FBX)) writeOut(pc,obj,mt,new ImageConverter("fbx"));
		else if(mt.same(MimeType.IMAGE_PNM)) writeOut(pc,obj,mt,new ImageConverter("pnm"));
		else if(mt.same(MimeType.IMAGE_PGM)) writeOut(pc,obj,mt,new ImageConverter("pgm"));
		else if(mt.same(MimeType.IMAGE_PBM)) writeOut(pc,obj,mt,new ImageConverter("pbm"));
		else if(mt.same(MimeType.IMAGE_ICO)) writeOut(pc,obj,mt,new ImageConverter("ico"));
		else if(mt.same(MimeType.IMAGE_PSD)) writeOut(pc,obj,mt,new ImageConverter("psd"));
		else if(mt.same(MimeType.IMAGE_ASTERIX)) writeOut(pc,obj,MimeType.IMAGE_PNG,new ImageConverter("png"));
		
		// Application
		else if(mt.same(MimeType.APPLICATION_JAVA)) writeOut(pc,obj,mt,new JavaConverter());
		//if("application".equalsIgnoreCase(mt.getType()))
		
		
		else pc.forceWrite(convertResult(pc, props, null, obj));
	}

	private void writeOut(PageContext pc, Object obj, MimeType mt,BinaryConverter converter) throws ConverterException, IOException {
		pc.getResponse().setContentType(mt.toString());
		
		OutputStream os=null;
		try{
			converter.writeOut(pc, obj, os=pc.getResponseStream());
		}
		finally{
			IOUtil.closeEL(os);
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
        Object returnFormat=url.get(KeyConstants._returnFormat,null);
        Object queryFormat=url.get(KeyConstants._queryFormat,null);
        
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
        case UDF.RETURN_FORMAT_XML:
        	rsp.setContentType("text/xml; charset=UTF-8");
        	rsp.setHeader("Return-Format", "xml");
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