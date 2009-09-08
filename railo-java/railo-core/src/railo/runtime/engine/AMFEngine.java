package railo.runtime.engine;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openamf.AMFBody;
import org.openamf.AMFError;
import org.openamf.AMFMessage;
import org.openamf.ServiceRequest;
import org.openamf.io.AMFDeserializer;
import org.openamf.io.AMFSerializer;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.Pair;
import railo.commons.lang.SystemOut;
import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.net.amf.CFMLProxy;
import railo.runtime.net.amf.OpenAMFCaster;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.HttpUtil;
import railo.runtime.op.Caster;


/**
 * AMF Engine
 */
public final class AMFEngine {
    


    /**
     * Main entry point for the servlet
     * @param servlet 
     * @param req 
     * @param rsp 
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp) throws IOException {
    	
    	AMFMessage requestMessage = null;
        AMFMessage responseMessage = null;
        requestMessage = deserializeAMFMessage(req);
        responseMessage = processMessage(servlet, req, rsp, requestMessage);
        serializeAMFMessage(rsp, responseMessage);
    }

    /**
     * Uses the AMFDeserializer to deserialize the request
     * 
     * @see org.openamf.io.AMFDeserializer
     */
    private AMFMessage deserializeAMFMessage(HttpServletRequest req) throws IOException {
        DataInputStream dis = null;
       	try {
       		dis = new DataInputStream(req.getInputStream());
       		AMFDeserializer deserializer = new AMFDeserializer(dis);
       		AMFMessage message = deserializer.getAMFMessage();
       		return message;
       	}
       	finally {
       		IOUtil.closeEL(dis);
       	}
    }

    /**
     * Uses the AMFSerializer to serialize the request
     *
     * @see org.openamf.io.AMFSerializer
     */
    private void serializeAMFMessage(HttpServletResponse resp, AMFMessage message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        AMFSerializer serializer = new AMFSerializer(dos);
        serializer.serializeMessage(message);
        resp.setContentType("application/x-amf");
        resp.setContentLength(baos.size());
        ServletOutputStream sos = resp.getOutputStream(); 
        baos.writeTo(sos);
        sos.flush();
    }

    /**
     * Iterates through the request message's bodies, invokes each body and
     * then, builds a message to send as the results
     * @param req 
     * @param rsp 
     * @param message 
     * @return AMFMessage
     * @throws IOException 
     * @throws ServletException 
     */
    private AMFMessage processMessage(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp, AMFMessage message)  {
        AMFMessage responseMessage = new AMFMessage();
        for (Iterator bodies = message.getBodies(); bodies.hasNext();) {
            AMFBody requestBody = (AMFBody) bodies.next();
            // invoke
            Object serviceResult = invokeBody(servlet,req, rsp, requestBody);
            String target = getTarget(requestBody, serviceResult);
            AMFBody responseBody = new AMFBody(target, "null", serviceResult);
            responseMessage.addBody(responseBody);
        }
        return responseMessage;
    }

    
    private Object invokeBody(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp, AMFBody requestBody) { 
    	try {
	    	ServiceRequest request = new ServiceRequest(requestBody);
	        rsp.getOutputStream();// MUST muss das sein?
	       
	        return new CFMLProxy().invokeBody(OpenAMFCaster.getInstance(),null,servlet.getServletContext(),servlet.getServletConfig(), req, rsp, request.getServiceName(), request.getServiceMethodName(), request.getParameters());
		} 
    	catch (Exception e) {
    		e.printStackTrace();
            rsp.setStatus(200);
            AMFError error=new AMFError();
            e.setStackTrace(e.getStackTrace());
            error.setDescription(e.getMessage());
			
			if(e instanceof PageException){
				PageException pe = (PageException)e;
	            error.setCode(pe.getErrorCode());
	            error.setCode(pe.getErrorCode());
	            error.setDetails(pe.getDetail());
			}
			
			return error;
		} 
    }
    
    /*private Object OldinvokeBody(HttpServlet servlet, HttpServletRequest req, HttpServletResponse rsp, AMFBody requestBody) { 
        try { 
        	ServiceRequest request = new ServiceRequest(requestBody);
            rsp.getOutputStream();// MUST muss das sein?
            
            //new Test().invokeBody(servlet, req, rsp, request.getServiceName(), request.getServiceMethodName(), request.getParameters());
            
            
        	// Params
            Object parameter=OpenAMFCaster.getInstance().toCFMLObject(request.getParameters());
            
            // Forward
            CFMLFactory factory = CFMLEngineImpl.getInstance().getCFMLFactory(servlet.getServletContext(), servlet.getServletConfig(), req);
            PageContext pc=null;
            
            // CFML Files
            String cfml;
            try {
                cfml="/"+(request.getServiceName()+'/'+request.getServiceMethodName()).replace('.','/')+".cfm";
                pc=createPageContext(factory,cfml,"",null,req,rsp);
                print(pc,cfml);
                PageSource source = pc.getPageSource(cfml);
                
                if(source.exists()) {
                	// Before
                    Struct params=new StructImpl();
                    pc.variablesScope().setEL("flash",params);
                    params.setEL("params",parameter);
                    
                    // Execute
                    pc.execute(cfml,true);
                    
                    // write back response
                    writeBackResponse(pc,rsp);
                    
                    // After
                    Object flash=pc.variablesScope().get("flash",null);
                    if(flash instanceof Struct) {
                    	return OpenAMFCaster.getInstance().toAMFObject(((Struct)flash).get("result",null));
                    }
                    return null;
                }
            }
	        finally {
	            if(pc!=null)factory.releaseRailoPageContext(pc);
	        }

            // CFC Files
	        String cfc;
            try {
                cfc="/"+(request.getServiceName()).replace('.','/')+".cfc";
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pc=createPageContext(factory,cfc,"method="+request.getServiceMethodName(),baos,req,rsp);
                PageSource source = pc.getPageSource(cfc);
                print(pc,cfc+"?method="+request.getServiceMethodName());
                
            	if(source.exists()) {
                	// Before
                	Form form = pc.urlFormScope();

            		// Map
                	if(parameter instanceof Map){
                		Map map = ((Map)parameter);
                		Iterator it = map.keySet().iterator();
                		Object key;
                		while(it.hasNext()) {
                			key=it.next();
                			form.setEL(key.toString(), map.get(key));
                		}
                	}
            		// List
                	else if(parameter instanceof List){
                		form.setEL("argumentCollection", parameter);
                		List list = ((List)parameter);
                		Iterator it = list.iterator();
                		int count=1;
                		while(it.hasNext()) {
                			form.setEL(Caster.toString(count++), it.next());
                		}
                	}
                	else {
                		ArrayList list = new ArrayList();
                		list.add(parameter);
                		form.setEL("argumentCollection", list);
                	}
                    
                    // Execute
                    pc.execute(cfc,true);

                    // write back response
                    writeBackResponse(pc,rsp);
                    
                    // After
                    //String charset=pc.getConfig().getWebCharset();
    	            return OpenAMFCaster.getInstance().toAMFObject(pc.variablesScope().get("AMF-Forward",null));
    	            
                }
            }
	        finally {
	            if(pc!=null)factory.releaseRailoPageContext(pc);
	        }
	        
	        AMFError error = new AMFError();
	        
        	error.setDescription("can't find cfml ("+cfml+") or cfc ("+cfc+") matching the amf request");
        	return error;
        	          
        }
        catch (Throwable t) {
        	t.printStackTrace();
        	rsp.setStatus(200);
        	AMFError error = new AMFError();
        	error.setDescription(t.getMessage());
        	return error;
        }
    }*/

	private void print(PageContext pc, String str) {
		if(pc.getConfig() instanceof ConfigWebImpl)
			SystemOut.printDate(((ConfigWebImpl)pc.getConfig()).getOutWriter(), str);
        
	}

	private void writeBackResponse(PageContext pc, HttpServletResponse rsp) {
		HttpServletResponseDummy hsrd=(HttpServletResponseDummy) pc.getHttpServletResponse();
        
		// Cookie
		Cookie[] cookies = hsrd.getCookies();
        if(cookies!=null) {
        	for(int i=0;i<cookies.length;i++) {
        		rsp.addCookie(cookies[i]);
        	}
        }
        
        // header
        Pair[] headers = hsrd.getHeaders();
        Pair header ;
        Object value;
        if(headers!=null) {
        	for(int i=0;i<headers.length;i++) {
        		header=headers[i];
        		value=header.getValue();
        		if(value instanceof Long)rsp.addDateHeader(header.getName(), ((Long)value).longValue());
        		else if(value instanceof Integer)rsp.addDateHeader(header.getName(), ((Integer)value).intValue());
        		else rsp.addHeader(header.getName(), Caster.toString(header.getValue(),""));
        	}
        }
	}

	/**
	 * @param factory
	 * @param scriptName
	 * @param queryString
	 * @param os
	 * @param formerReq
	 * @param formerRsp
	 * @return
	 */
	private PageContext createPageContext(CFMLFactory factory,String scriptName,String queryString, OutputStream os, HttpServletRequest formerReq, HttpServletResponse formerRsp) {
		Resource root = factory.getConfig().getRootDirectory();
		if(os==null)os=DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		
		// Request
		HttpServletRequestDummy req = new HttpServletRequestDummy(
				root,"localhost",scriptName,queryString,
				formerReq.getCookies(),
				HttpUtil.cloneHeaders(formerReq),
				HttpUtil.cloneParameters(formerReq),
				HttpUtil.getAttributesAsStruct(formerReq),null);
				req.addHeader("AMF-Forward", "true");
		HttpServletResponseDummy rsp = new HttpServletResponseDummy(os);

		return  factory.getRailoPageContext(factory.getServlet(), req, rsp, null, false, -1, false);
	}

    

    private String getTarget(AMFBody requestBody, Object serviceResult) {
        String target = "/onResult";
        if (serviceResult instanceof AMFError) {
            target = "/onStatus";
        }
        return requestBody.getResponse() + target;
    }
}