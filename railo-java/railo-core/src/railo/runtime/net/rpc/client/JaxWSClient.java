package railo.runtime.net.rpc.client;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;

import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.rpc.RPCException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

public class JaxWSClient extends WSClient {
	
	private URL url;
	private Definition wsdl;
	private Service service;
	private String wsdlUrl;
	
	public static void main(String[] args) throws PageException {
		JaxWSClient client = new JaxWSClient("http://www.getrailo.org/railo-context/Admin.cfc?wsdl", null, null, null);
	}
	
	public JaxWSClient( String wsdlUrl, String username, String password, ProxyData proxyData) throws PageException {
		try {
			this.wsdlUrl=wsdlUrl;
			url = HTTPUtil.toURL(wsdlUrl, true);
			wsdl=loadWSDL(url);
			
			
			// get service
			{
			Iterator<Service> it = wsdl.getServices().values().iterator();
			while(it.hasNext()){
				if(service!=null)throw new ApplicationException("cannot handle more than one service");
				service=it.next();
			}
			}
			
			
			//Port port = WSUtil.getWSDLPort(service);
	        //Binding binding = port.getBinding();
	        //List<BindingOperation> operations = binding.getBindingOperations();
	        
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
    }

	private static Definition loadWSDL(URL url) throws WSDLException { 
		WSDLFactory factory = WSDLFactory.newInstance();
		
		// create an object to read the WSDL file
	    WSDLReader reader = factory.newWSDLReader();

	    // pass the URL to the reader for parsing and get back a WSDL definiton
	    return reader.readWSDL(url.toExternalForm());
	   
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String castToString() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String castToString(String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean castToBoolean(Boolean defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double castToDoubleValue() throws PageException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double castToDoubleValue(double defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DateTime castToDateTime() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime castToDateTime(DateTime defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(String str) throws PageException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(double d) throws PageException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<Key> keyIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> keysAsStringIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Object> valueIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addHeader(SOAPHeaderElement header) throws PageException {
		// TODO Auto-generated method stub

	}

	@Override
	public Call getLastCall() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object callWithNamedValues(Config config, Key methodName, Struct arguments) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct arguments) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
    	
            try {
				return _toDumpData(pageContext,maxlevel,dp);
			}
			catch (RPCException e) {
				throw new PageRuntimeException(Caster.toPageException(e));
			}
        /*try {} catch (Exception e) {
            DumpTable table = new DumpTable("webservice","#99cc99","#ccffcc","#000000");
            table.appendRow(1,new SimpleDumpData("webservice"),new SimpleDumpData(wsdlUrl));
            return table;
        }*/
    }
    private DumpData _toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) throws RPCException {
                
    	DumpTable functions = new DumpTable("webservice","#99cc99","#ccffcc","#000000");
    	functions.setTitle("Web Service (JAX WS)");
        if(dp.getMetainfo())functions.setComment(wsdlUrl);
        
        Port port = WSUtil.getSoapPort(service);
        Binding binding = port.getBinding();
        List<BindingOperation> operations = binding.getBindingOperations();
        
        Iterator<BindingOperation> it = operations.iterator();
        BindingOperation bo;
        while(it.hasNext()){
        	bo=it.next();
        	functions.appendRow(1, new SimpleDumpData(bo.getName()), toDumpData(bo));
        }
        
        return functions;
    }
    
    private DumpData toDumpData(BindingOperation bo) {
    	Map<QName,Message> messages = wsdl.getMessages();
    	
    	DumpTable table = new DumpTable("#99cc99","#ccffcc","#000000");
    	DumpTable attributes = new DumpTable("#99cc99","#ccffcc","#000000");
    	String returns = "void";
        attributes.appendRow(3,new SimpleDumpData("name"),new SimpleDumpData("type"));
        
        Operation op = bo.getOperation();
        
        // attributes
        Input in = op.getInput();
        Message msg = in.getMessage();
        
        
        //msg=WSUtil.getMessageByLocalName(messages,bo.getBindingInput().getName());
        //print.e(msg.getQName());
        
        List<Part> parts = msg.getOrderedParts(null);
        Iterator<Part> it = parts.iterator();
        Part p;
        QName en;
        QName type;
        while(it.hasNext()){
        	p=it.next();
        	en=p.getElementName();
        	
        	if(en!=null) {
        		type=en;
        		Types types = wsdl.getTypes();
        	}
        	else 
        		type= p.getTypeName();
        	
        	attributes.appendRow(0,
            		new SimpleDumpData(en+":"+p.getName()),
            		new SimpleDumpData(toRailoType(type)));
            
        }
        
        // return
        msg = bo.getOperation().getOutput().getMessage();
        msg=wsdl.getMessage(msg.getQName());
        parts = msg.getOrderedParts(null);
        it = parts.iterator();
        while(it.hasNext()){
        	p=it.next();
        	returns=toRailoType(p.getTypeName());
            
        }
        
        // TODO msg.getDocumentationElement();
        
        
        
        /*
        for(int j = 0; j < parameters.list.size(); j++) {
            Parameter p = (Parameter)parameters.list.get(j);
            
            QName paramType = org.apache.axis.wsdl.toJava.Utils.getXSIType(p);
            String strType=paramType.getLocalPart();
                        
            switch(p.getMode()) {
            case Parameter.IN:
                attributes.appendRow(0,new SimpleDumpData(p.getName()),new SimpleDumpData(toRailoType(strType)));
            break;
            case Parameter.OUT:
                returns=toRailoType(strType);
            break;
            case Parameter.INOUT:
                attributes.appendRow(0,new SimpleDumpData(p.getName()),new SimpleDumpData(toRailoType(strType)));
                returns=toRailoType(strType);
                
            break;
            }
        }
        Parameter rtn = parameters.returnParam;
        if(rtn!=null) {
            QName paramType = org.apache.axis.wsdl.toJava.Utils.getXSIType(rtn);
            String strType=paramType.getLocalPart();
            returns=toRailoType(strType);
        }
        table.appendRow(1,new SimpleDumpData("arguments"),attributes);
        table.appendRow(1,new SimpleDumpData("return type"),new SimpleDumpData(returns));
        if(doc.length()>0)table.appendRow(1,new SimpleDumpData("hint"),new SimpleDumpData(doc));
        
        */
        
        table.appendRow(1,new SimpleDumpData("arguments"),attributes);
        table.appendRow(1,new SimpleDumpData("return type"),new SimpleDumpData(returns));
        // if(doc.length()>0)table.appendRow(1,new SimpleDumpData("hint"),new SimpleDumpData(doc));
        
        
        return table;
        
    }

    private String toRailoType(QName typeName) {
    	if(typeName==null) return "";
    	
    	String strType = typeName.getLocalPart();
    	strType=strType.toLowerCase();
    	
    	if(strType.startsWith("array"))strType="array";
    	else if(strType.equals("map"))strType="struct";
    	else if(strType.startsWith("query"))strType="query";
    	else if(strType.equals("double"))strType="numeric";
    	else if(strType.startsWith("any"))strType="any";
    	else if(strType.equals("date"))strType="date";
        return strType;
	}
}
