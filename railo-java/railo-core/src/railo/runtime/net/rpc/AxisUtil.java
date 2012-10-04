package railo.runtime.net.rpc;

import java.io.StringReader;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import railo.runtime.PageContext;
import railo.runtime.net.rpc.client.RPCClient;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;

public class AxisUtil {

	public static boolean isSOAPRequest() {
		MessageContext context = MessageContext.getCurrentContext();
        return context != null && !context.isClient();
	}
	
	public static Object getSOAPRequestHeader(PageContext pc, String namespace, String name, boolean asXML) throws Exception {
		MessageContext context = MessageContext.getCurrentContext();
        if(context==null || context.isClient()) throw new AxisFault("not inside a Soap Request");
        
        SOAPEnvelope env = context.getRequestMessage().getSOAPEnvelope();
        SOAPHeaderElement header = env.getHeaderByName(namespace, name);
        return toValue(header,asXML);
	}

	public static Object getSOAPResponseHeader(PageContext pc, RPCClient client, String namespace, String name, boolean asXML) throws Exception {
		MessageContext context = getMessageContext(client);
	    
		SOAPEnvelope env = context.getResponseMessage().getSOAPEnvelope();
	    SOAPHeaderElement header = env.getHeaderByName(namespace, name);
	    return toValue(header,asXML);
	}
	
	public static Node getSOAPRequest(RPCClient client) throws Exception {
		MessageContext context=getMessageContext(client);
        SOAPEnvelope env = context.getRequestMessage().getSOAPEnvelope();
        return XMLCaster.toXMLStruct(env.getAsDocument(),true);
    }
	
	public static Node getSOAPResponse(RPCClient client) throws Exception {
		Call call = client.getLastCall();
		if(call==null) throw new AxisFault("web service was not invoked yet");
    	SOAPEnvelope env = call.getResponseMessage().getSOAPEnvelope();
		return XMLCaster.toXMLStruct(env.getAsDocument(),true);
    }
	
	public static void addSOAPResponseHeader(String namespace, String name, Object value, boolean mustUnderstand) throws AxisFault {
        MessageContext context = MessageContext.getCurrentContext();
        if(context==null || context.isClient()) throw new AxisFault("not inside a Soap Request");
        
        SOAPEnvelope env = context.getResponseMessage().getSOAPEnvelope();
    	SOAPHeaderElement header=toSOAPHeaderElement(namespace,name,value);
        header.setMustUnderstand(mustUnderstand);
        env.addHeader(header);
    }

	public static void addSOAPRequestHeader(RPCClient client, String namespace, String name, Object value, boolean mustUnderstand)  {
    	SOAPHeaderElement header=toSOAPHeaderElement(namespace,name,value);
        header.setMustUnderstand(mustUnderstand);
        client.addHeader(header);
    }
	
	
	private static SOAPHeaderElement toSOAPHeaderElement(String namespace, String name, Object value) {
		Element el=XMLCaster.toRawElement(value,null);
        if(el!=null) return new SOAPHeaderElement(el);
        return new SOAPHeaderElement(namespace, name, value);
	}


	
	private static Object toValue(SOAPHeaderElement header, boolean asXML) throws Exception {
		if(header==null) return "";
    	if(asXML) {
    		String strXML = header.toString();
			InputSource is = new InputSource(new StringReader(strXML.trim()));
			return XMLCaster.toXMLStruct(XMLUtil.parse(is,null,false),true);
        } 
        
        Object value=header.getObjectValue();
        if(value == null){
        	value = header.getObjectValue(String.class);
        }
        return value;
	}
	

	
	private static MessageContext getMessageContext(RPCClient client) throws AxisFault {
		if(client!=null) {
			Call call = client.getLastCall();
        	if(call==null) throw new AxisFault("web service was not invoked yet");
        	return call.getMessageContext();
        } 
        MessageContext context = MessageContext.getCurrentContext();
        if(context == null) throw new AxisFault("not inside a Soap Request");
        return context;
	}
	
	
}
