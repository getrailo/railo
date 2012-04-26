package railo.runtime.net.rpc.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JComboBox.KeySelectionManager;
import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.transport.http.CommonsHTTPSender;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.net.proxy.Proxy;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.net.rpc.Pojo;
import railo.runtime.net.rpc.RPCException;
import railo.runtime.net.rpc.TypeMappingUtil;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.ObjectsIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.util.ArrayIterator;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMPropertyImpl;

/**
 * Wrapper for a Webservice
 */
public final class RPCClient implements Objects, Iteratorable{

    
    

	private static final long serialVersionUID = 1L;
	private Parser parser = new Parser();
	//private Map properties=new HashTable();
    private String wsdlUrl;
	private ProxyData proxyData;
	private String username;
	private String password;
	private Call last;
	private List<SOAPHeaderElement> headers;
	
	static {
		EngineConfiguration engine = EngineConfigurationFactoryFinder.newFactory().getClientEngineConfig();
		SimpleProvider provider = new SimpleProvider(engine);
		provider.deployTransport("http", new CommonsHTTPSender());	
	}
	

    /**
     * @param wsdlUrl
     * @param username 
     * @param password 
     * @throws PageException
     */
    public RPCClient( String wsdlUrl, String username, String password) throws PageException {
		this(wsdlUrl,username,password,null);
    }

    public RPCClient( String wsdlUrl) throws PageException {
       this(wsdlUrl,null,null,null);
    }
    
	public RPCClient(String wsdlUrl, ProxyData proxyData) throws PageException {
		this(wsdlUrl,null,null,proxyData);
	}

	public RPCClient(String wsdlUrl, String username, String password, ProxyData proxyData) throws PageException {
		if(!StringUtil.isEmpty(username)) {
			if(password==null)password="";
			parser.setUsername(username);
	        parser.setPassword(password);
	        //parser.setTimeout(1000);
	        this.username=username;
	        this.password=password;
	        
		}
		this.proxyData=proxyData;
		run(wsdlUrl);
	}

	private void run(String wsdlUrl) throws PageException {
        this.wsdlUrl=wsdlUrl;
        try {
            parser.run(wsdlUrl);
        }
        catch(Throwable e) {
            throw Caster.toPageException(e);
        }
    }

    /**
     * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
     */
	public Object callWithNamedValues(PageContext pc, String methodName, Struct arguments) throws PageException {
        try {
            return (_callMethod(pc.getConfig(),methodName,arguments,null));
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }
	
	public Object callWithNamedValues(Config config, String methodName, Struct arguments) throws PageException {
        try {
            return (_callMethod(config,methodName,arguments,null));
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return callWithNamedValues(pc, methodName.getString(), args);
	}

    /**
     * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
     */
    public Object call(PageContext pc, String methodName,Object[] arguments) throws PageException {
        try {
            return _callMethod(pc.getConfig(),methodName,null,arguments);
        } 
        catch (Throwable t) {
        	throw Caster.toPageException(t);
		} 
    }

    public Object call(Config config, String methodName,Object[] arguments) throws PageException {
        try {
            return (_callMethod(config,methodName,null,arguments));
        } 
        catch (Exception e) {
        	throw Caster.toPageException(e);
		} 
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return call(pc, methodName.getString(), arguments);
	}

    private Object _callMethod(Config config,String methodName, Struct namedArguments,Object[] arguments) throws PageException, ServiceException, RemoteException {
        
		javax.wsdl.Service service = getWSDLService();
		
		Service axisService = null;
		axisService = new Service(parser, service.getQName());
		TypeMappingUtil.registerDefaults(axisService.getTypeMappingRegistry());
		
		
		
		
		
		
		Port port = getWSDLPort(service);
		
		Binding binding = port.getBinding();
        
        Parameters parameters = null;
		Parameter p = null;
		SymbolTable symbolTable = parser.getSymbolTable();
		BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
		
		Iterator itr = bEntry.getParameters().keySet().iterator();
		Operation tmpOp = null;
		Operation operation = null;
		while(itr.hasNext())  {
			tmpOp = (Operation)itr.next();	
            if(tmpOp.getName().equalsIgnoreCase(methodName)) {
				operation = tmpOp;
				parameters = (Parameters)bEntry.getParameters().get(tmpOp);
                break;
			}
		}
		if(operation == null || parameters == null)
			throw new RPCException("Cannot locate method " + methodName + " in webservice " + wsdlUrl);
		
        org.apache.axis.client.Call call = (Call)axisService.createCall(QName.valueOf(port.getName()), QName.valueOf(tmpOp.getName()));
        
        if(!StringUtil.isEmpty(username,true)){
        	call.setUsername(username);
	        call.setPassword(password);
        }
        
        org.apache.axis.encoding.TypeMapping tm = (org.apache.axis.encoding.TypeMapping) 
        	axisService.getTypeMappingRegistry().getDefaultTypeMapping();
        //TypeMappingRegistry reg=(TypeMappingRegistry) axisService.getTypeMappingRegistry();
        
        //tm=reg.getOrMakeTypeMapping("http://schemas.xmlsoap.org/soap/encoding/");
        tm=call.getMessageContext().getTypeMapping();
        
        Vector<String> inNames = new Vector<String>();
		Vector<Parameter> inTypes = new Vector<Parameter>();
		Vector<String> outNames = new Vector<String>();
		Vector<Parameter> outTypes = new Vector<Parameter>();
		for(int j = 0; j < parameters.list.size(); j++) {
			p = (Parameter)parameters.list.get(j);
			map(config,call,tm,p.getType());
			switch(p.getMode()) {
            case Parameter.IN:
                inNames.add(p.getQName().getLocalPart());
                inTypes.add(p);
            break;
            case Parameter.OUT:
                outNames.add(p.getQName().getLocalPart());
                outTypes.add(p);
            break;
            case Parameter.INOUT:
                inNames.add(p.getQName().getLocalPart());
                inTypes.add(p);
                outNames.add(p.getQName().getLocalPart());
                outTypes.add(p);
            break;
            }
		}

		// set output type
		if (parameters.returnParam != null) {
        	QName rtnQName = parameters.returnParam.getQName();
        	TypeEntry rtnType = parameters.returnParam.getType();
        	map(config,call,tm,rtnType);
            outNames.add(rtnQName.getLocalPart());
            outTypes.add(parameters.returnParam);
            
        }
        
        
        
        //Iterator it = outTypes.iterator();
       
        // check arguments
        Object[] inputs = new Object[inNames.size()];
        if(arguments!=null) {
    		if(inNames.size() != arguments.length)
    			throw new RPCException("Invalid arguments count for operation " + methodName+" ("+arguments.length+" instead of "+inNames.size()+")");
    		
            for(int pos = 0; pos < inNames.size(); pos++) {
    			p = inTypes.get(pos);
                inputs[pos]=getArgumentData(tm,ThreadLocalPageContext.getTimeZone(config), p, arguments[pos]);
    		}
        }
        else {
            UDFImpl.argumentCollection(namedArguments);
            if(inNames.size() != namedArguments.size())
                throw new RPCException("Invalid arguments count for operation " + methodName+" ("+namedArguments.size()+" instead of "+inNames.size()+")");
            
            
            Object arg;
            String name;
            for(int pos = 0; pos < inNames.size(); pos++) {
                p = inTypes.get(pos);
                name=p.getName();
                arg=namedArguments.get(name,null);
                
                if(arg==null) {
                    throw new RPCException("Invalid arguments for operation " + methodName,
                            getErrorDetailForArguments(inNames.toArray(new String[inNames.size()]),namedArguments.keysAsString()));
                }
                inputs[pos]=getArgumentData(tm,ThreadLocalPageContext.getTimeZone(config), p, arg);
            }
        }
        
        Object ret=null;
        
     // add header
        if(headers!=null && !headers.isEmpty()) {
        	Iterator<SOAPHeaderElement> it = headers.iterator();
        	while(it.hasNext()){
        		call.addHeader(it.next());
        	}
        }
        
        if(proxyData!=null && !StringUtil.isEmpty(proxyData.getServer(),true)) {
        	try {
	        	Proxy.start(proxyData);
	    		ret = call.invoke(inputs);
	    		//ret = invoke(call,inputs);
	        	
	        }
	        finally {
	        	Proxy.end();
	        }
        }
        else {
        	ret = call.invoke(inputs);
        }
		last=call;
		
		if(outNames.size()<=1) return AxisCaster.toRailoType(null,ret);
        //getParamData((org.apache.axis.client.Call)call,parameters.returnParam,ret);
		Map outputs = call.getOutputParams();
		
		Struct sct = new StructImpl();
		for(int pos = 0; pos < outNames.size(); pos++) {
			String name = outNames.get(pos);
            //print.ln(name);
			Object value = outputs.get(name);
			if(value == null && pos == 0) {
				sct.setEL(name, AxisCaster.toRailoType(null,ret));
			}
			else {
				sct.setEL(name, AxisCaster.toRailoType(null,value));
			}
		}
		return sct;
	}
    
	private void map(Config config,Call call, org.apache.axis.encoding.TypeMapping tm, TypeEntry type) throws PageException {
		Vector els = type.getContainedElements();
		
		if(els==null) mapSimple(tm, type);
        else {
        	// class is already registed
        	Class rtnClass=tm.getClassForQName(type.getQName());
        	if(rtnClass!=null && rtnClass.getName().equals(getClientClassName(type))) return;
        	
        	
        	ClassLoader cl=null;
			try {
				cl = config.getRPCClassLoader(false);
			} catch (IOException e) {}
			
        	Class cls = mapComplex(config,call,tm, type);   
        	// TODO make a better impl; this is not the fastest way to make sure all pojos use the same classloader
    		if(cls!=null && cl!=cls.getClassLoader()){
    			mapComplex(config,call,tm, type); 
        	}
    		
        }
	}
	
	private Class mapComplex(Config config,Call call, org.apache.axis.encoding.TypeMapping tm, TypeEntry type) throws PageException {
		Vector children = type.getContainedElements();
		TypeEntry ref=type.getRefType();
		if(ref==null) return _mapComplex(config, call, tm, type);
		children = ref.getContainedElements();
		
		if(children==null) {
			mapSimple(tm, ref);
			return null;
		}
		Class clazz = mapComplex(config, call, tm, ref);
		if(clazz==null) return null;
		Class arr = ClassUtil.toArrayClass(clazz);
		TypeMappingUtil.registerBeanTypeMapping(tm, arr, type.getQName());
		return arr;
	}

	private Class _mapComplex(Config config,Call call, org.apache.axis.encoding.TypeMapping tm, TypeEntry type) throws PageException {
		Vector children = type.getContainedElements();
		ArrayList<ASMPropertyImpl> properties=new ArrayList<ASMPropertyImpl>();
		if(children!=null) {
			Iterator it = children.iterator();
			ElementDecl el;
			Class clazz;
			TypeEntry t;
			String name;
			while(it.hasNext()){
				clazz=null;
	        	el=(ElementDecl) it.next();
	        	t=el.getType();
	        	Vector els = t.getContainedElements();
	            if(els!=null) {
	            	clazz=mapComplex(config, call, tm, t);
	            }
	        	name=railo.runtime.type.List.last(el.getQName().getLocalPart(), '>');
	        	
	        	if(clazz==null)clazz=tm.getClassForQName(t.getQName());
	        	if(clazz==null)clazz=Object.class;
	        	
	        	properties.add(new ASMPropertyImpl(clazz,name));
	        }
		}
		ASMProperty[] props = properties.toArray(new ASMProperty[properties.size()]);
		String clientClassName=getClientClassName(type);
		Pojo pojo = (Pojo) ComponentUtil.getClientComponentPropertiesObject(config,clientClassName,props);
		
		TypeMappingUtil.registerBeanTypeMapping(tm,
    			pojo.getClass(), 
        		type.getQName());
		
    	return pojo.getClass();
	}
	
	private String getClientClassName(TypeEntry type) {
		String className=StringUtil.toVariableName(type.getQName().getLocalPart());
		
		String url=urlToClass(wsdlUrl);
		String ns = type.getQName().getNamespaceURI();
		//if(props!=null){String p = ASMUtil.createMD5(props);
		//print.e("p:"+p);}
		// has namespace 
		if(ns!=null && !"http://DefaultNamespace".equalsIgnoreCase(ns)){
			ns=StringUtil.replace(ns, "http://", "", true);
			ns=toClassName(ns,true);
			if(!StringUtil.isEmpty(ns)) return ns+"."+className;
		}
		return url+"."+className;
	} 

	private static String urlToClass(String wsdlUrl) {
		
		StringBuffer sb=new StringBuffer();
		try {
			URL url = new URL(wsdlUrl);
			
			// protocol
			if("http".equalsIgnoreCase(url.getProtocol())){}
			else{
				sb.append(toClassName(url.getProtocol(), false));
				sb.append('.');
			}
			
			// host
			sb.append(toClassName(url.getHost(), true));
			
			// port
			if(url.getPort()>0 && url.getPort()!=80){
				sb.append(".p");
				sb.append(url.getPort());
			}
			
			// path
			if(!StringUtil.isEmpty(url.getPath())){
				sb.append('.');
				sb.append(toClassName(url.getPath(), false));
			}
			
			// query
			if(!StringUtil.isEmpty(url.getQuery()) && !"wsdl".equals(url.getQuery())){
				sb.append('.');
				sb.append(toClassName(url.getQuery(), false));
			}
			
			
			return sb.toString();
		} 
		catch (MalformedURLException e) {
			return StringUtil.toVariableName(wsdlUrl);
		}
	}

	private static String toClassName(String raw,boolean reverse) {
		raw=raw.trim();
		if(raw.endsWith("/"))raw=raw.substring(0,raw.length()-1);
		StringBuffer sb=new StringBuffer();
		String[] arr=null;
		try {
			arr = railo.runtime.type.List.toStringArray(railo.runtime.type.List.listToArray(raw, "./&="));
		} catch (PageException e) {}
		String el;
		for(int i=0;i<arr.length;i++){
			el=arr[i].trim();
			if(el.length()==0)continue;
			if(reverse){
				if(sb.length()>0)sb.insert(0,'.');
				sb.insert(0,StringUtil.lcFirst(StringUtil.toVariableName(arr[i])));
				
			}
			else {
				if(sb.length()>0)sb.append('.');
				sb.append(StringUtil.lcFirst(StringUtil.toVariableName(arr[i])));
			}
		}
		return sb.toString();
	}

	private void mapSimple(org.apache.axis.encoding.TypeMapping tm, TypeEntry type) {
		//print.out("simple");
		//print.out(tm);
		//print.out(type);
	}

	private String getErrorDetailForArguments(String[] names, String[] argKeys) {
        String name;
        boolean found;
        
        for(int i=0;i<names.length;i++) {
            name=names[i];
            found=false;
            for(int y=0;y<argKeys.length;y++) {
                if(name.equalsIgnoreCase(argKeys[y]))found=true;
            }
            if(!found) {
                if(names.length>1)
                    return "missing argument with name ["+name+"], needed argument are ["+railo.runtime.type.List.arrayToList(names,", ")+"]";
                return "missing argument with name ["+name+"]";
            }
        }
        return "";
    }

    /**
     * returns the WSDL Service for this Object
	 * @return WSDL Service
	 * @throws RPCException
	 */
	public javax.wsdl.Service getWSDLService() throws RPCException {
		SymTabEntry symTabEntry = null;
		Map.Entry entry = null;
		Vector v = null;
		for(Iterator iterator = parser.getSymbolTable().getHashMap().entrySet().iterator(); iterator.hasNext();) {
			entry = (Map.Entry)iterator.next();
			v = (Vector)entry.getValue();
			for(int i = 0; i < v.size(); i++) {
				if(!(org.apache.axis.wsdl.symbolTable.ServiceEntry.class).isInstance(v.elementAt(i)))
					continue;
				symTabEntry = (SymTabEntry)v.elementAt(i);
				break;
			}

		}

		if(symTabEntry == null)
			throw new RPCException("Can't locate service entry in WSDL");
		return ((ServiceEntry)symTabEntry).getService();
	}

	/**
     * returns the WSDL Port
	 * @param service
	 * @return WSDL Port
	 * @throws RPCException
	 */
	public Port getWSDLPort(javax.wsdl.Service service) throws RPCException {
		String name = null;
		Port port = null;
		List list = null;
		Map ports = service.getPorts();
		for(Iterator itr = ports.keySet().iterator(); itr.hasNext();) {
			name = (String)itr.next();
			port = (Port)ports.get(name);
			list = port.getExtensibilityElements();
			if(list != null) {
				for(int i = 0; i < list.size(); i++)
					if(list.get(i) instanceof SOAPAddress)
						return port;

			}
		}
		throw new RPCException("Can't locate port entry for service " + service.getQName().toString() + " WSDL");
	}

	private Object getArgumentData(TypeMapping tm,TimeZone tz, Parameter p, Object arg) throws PageException {
		QName paramType = Utils.getXSIType(p);
		Object o = AxisCaster.toAxisType(tm,tz,paramType,arg,null);
        return o;
	}

    /**
     * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String propertyName) throws PageException {
        return call(pc,"get"+propertyName, ArrayUtil.OBJECT_EMPTY);
    }

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return get(pc, key.getString());
	}

    /**
     *
     * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object get(PageContext pc, String propertyName, Object defaultValue) {
        try {
            return call(pc,"get"+StringUtil.ucFirst(propertyName), ArrayUtil.OBJECT_EMPTY);
        } catch (PageException e) {
            return defaultValue;
        }
    }

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return get(pc, key.getString(), defaultValue); 
	}

    /**
     * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object set(PageContext pc, String propertyName, Object value) throws PageException {
        return call(pc,"set"+propertyName, new Object[]{value});
    }

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return set(pc, propertyName.toString(), value);
	}

    /**
     * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object setEL(PageContext pc, String propertyName, Object value) {
        try {
            return call(pc,"set"+propertyName, new Object[]{value});
        } catch (PageException e) {
            return null;
        }
    }

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return setEL(pc, propertyName.toString(), value);
	}

    /**
     * @see railo.runtime.type.Objects#isInitalized()
     */
    public boolean isInitalized() {
        return true;
    }

    /**
     *
     * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
     */
    public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
    	try {
            return _toDumpData(pageContext,maxlevel,dp);
        } catch (Exception e) {
            DumpTable table = new DumpTable("webservice","#ccccff","#cccc00","#000000");
            table.appendRow(1,new SimpleDumpData("webservice"),new SimpleDumpData(wsdlUrl));
            return table;
        }
    }
    private DumpData _toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) throws RPCException {
                
    	DumpTable box = new DumpTable("webservice","#ccccff","#cccc00","#000000");
        box.setTitle("Web Service");
        if(dp.getMetainfo())box.appendRow(1,new SimpleDumpData("url"),new SimpleDumpData(wsdlUrl));
        DumpTable functions = new DumpTable("#ccccff","#cccc00","#000000");
        
        
        javax.wsdl.Service service = getWSDLService();
        Port port = getWSDLPort(service);
        Binding binding = port.getBinding();
        
     
        //Parameters parameters = null;
        //Parameter p = null;
        SymbolTable symbolTable = parser.getSymbolTable();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        Iterator itr = bEntry.getParameters().keySet().iterator();
        Operation tmpOp = null;
        //Operation operation = null;
        while(itr.hasNext())  {
            tmpOp = (Operation)itr.next();
            Element el = tmpOp.getDocumentationElement();
            StringBuffer doc=new StringBuffer();
            if(el!=null){
            	NodeList children = XMLUtil.getChildNodes(el, Node.TEXT_NODE);
            	int len=children.getLength();
            	Text text;
            	for(int i=0;i<len;i++){
            		text=(Text) children.item(i);
            		doc.append(text.getData());
            	}
            }
            //parameters = (Parameters)bEntry.getParameters().get(tmpOp);
            functions.appendRow(1,
            		new SimpleDumpData(tmpOp.getName()),
            		_toHTMLOperation(doc.toString(),(Parameters)bEntry.getParameters().get(tmpOp)));
        }
        
        box.appendRow(1,new SimpleDumpData(""),functions);
        return box;
    }

    private DumpData _toHTMLOperation(String doc, Parameters parameters) {
    	DumpTable table = new DumpTable("#ccccff","#ccff66","#000000");
    	DumpTable attributes = new DumpTable("#ccccff","#ccff66","#000000");
        String returns = "void";
        attributes.appendRow(3,new SimpleDumpData("name"),new SimpleDumpData("type"));
        
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
        
        
        return table;
        
    }
    private String toRailoType(String strType) {
    	strType=strType.toLowerCase();
    	if(strType.startsWith("array"))strType="array";
    	else if(strType.equals("map"))strType="struct";
    	else if(strType.startsWith("query"))strType="query";
    	else if(strType.equals("double"))strType="numeric";
    	else if(strType.startsWith("any"))strType="any";
    	else if(strType.equals("date"))strType="date";
        return strType;
	}

	/**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a string");
    }

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a boolean");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a number");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws RPCException {
        throw new RPCException("can't cast Webservice to a Date Object");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Webservice Object with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a String");
	}

    /**
     * @see railo.runtime.type.Iteratorable#keyIterator()
     */
    public Iterator<Collection.Key> keyIterator() {
    	List<Collection.Key> list=new ArrayList<Collection.Key>();
    	javax.wsdl.Service service = null;
        Port port = null;
    	try {
    		service = getWSDLService();
            port = getWSDLPort(service);
    	}
    	catch(Exception e) {
    		return new KeyIterator(new Collection.Key[0]);
    	}
    	
        Binding binding = port.getBinding();
        
        SymbolTable symbolTable = parser.getSymbolTable();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        Iterator itr = bEntry.getParameters().keySet().iterator();
        Operation tmpOp = null;
        //Operation operation = null;
        while(itr.hasNext())  {
            tmpOp = (Operation)itr.next();
            //Parameters p = (Parameters)bEntry.getParameters().get(tmpOp);
            list.add(KeyImpl.init(tmpOp.getName()));
            
        }
        return new KeyIterator(list.toArray(new Collection.Key[list.size()]));
    }
	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return keyIterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return new ObjectsIterator(keyIterator(),this);
	}

	public Call getLastCall() {
		return last;
	}

	public void addHeader(SOAPHeaderElement header) {
		if(headers==null)headers=new ArrayList<SOAPHeaderElement>();
		headers.add(header);
	}
}