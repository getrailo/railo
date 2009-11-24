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
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
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
import railo.runtime.net.rpc.RPCConstants;
import railo.runtime.net.rpc.RPCException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.ObjectsIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.util.ArrayIterator;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMPropertyImpl;
import coldfusion.xml.rpc.QueryBean;

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
        catch (Exception e) {
        	throw Caster.toPageException(e);
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
		axisService.getTypeMappingRegistry().getDefaultTypeMapping().register(QueryBean.class, 
                RPCConstants.QUERY_QNAME,
                new BeanSerializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME),
                new BeanDeserializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME));
		
		
		
		
		
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
        	//print.out(username+":"+password);
        	call.setUsername(username);
	        call.setPassword(password);
	       	        
        }
        /*/ Array
        call.registerTypeMapping(
                ArrayBean.class, 
                XMLType.SOAP_ARRAY,
                new BeanSerializerFactory(ArrayBean.class,XMLType.SOAP_ARRAY),
                new BeanDeserializerFactory(ArrayBean.class,XMLType.SOAP_ARRAY));
*/
        /*/ Struct
        call.registerTypeMapping(
                StructBean.class, 
                XMLType.SOAP_MAP,
                new BeanSerializerFactory(StructBean.class,XMLType.SOAP_MAP),
                new BeanDeserializerFactory(StructBean.class,XMLType.SOAP_MAP),true);

        // Query
        call.registerTypeMapping(
                QueryBean.class, 
                RPCConstants.QUERY_QNAME,
                new BeanSerializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME),
                new BeanDeserializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME));
        
        org.apache.axis.encoding.TypeMapping tm = (org.apache.axis.encoding.TypeMapping) 
        axisService.getTypeMappingRegistry().getDefaultTypeMapping();
        Class[] classes = tm.getAllClasses();
        for(int i=0;i<classes.length;i++){
        	if(classes[i].getName().equals("jm.addr")){
        		print.ln(":::	"+classes[i].getName());
        		print.ln(":::	"+tm.getTypeQName(classes[i]));
            	//tm.removeDeserializer(classes[i], tm.getTypeQName(classes[i]));
        	}
            
        }*/
        
        /* DateTime
        call.registerTypeMapping(
                QueryBean.class, 
                XMLType.XSD_DATETIME,
                new BeanSerializerFactory(QueryBean.class,XMLType.XSD_DATETIME),
                new BeanDeserializerFactory(QueryBean.class,XMLType.XSD_DATETIME));
        */
        /* Component
        call.registerTypeMapping(
                ComponentBean.class, 
                XMLType.XSD_ANY,
                new BeanSerializerFactory(ComponentBean.class,qName),
                new BeanDeserializerFactory(ComponentBean.class,qName));
        */
        
		/*
        call.registerTypeMapping(
                org.w3c.dom.Element.class, 
                parameters.returnParam.getType().getQName(),
                new ElementSerializerFactory(),
                new ElementDeserializerFactory());
        */

        org.apache.axis.encoding.TypeMapping tm = (org.apache.axis.encoding.TypeMapping) 
        	axisService.getTypeMappingRegistry().getDefaultTypeMapping();
        Vector inNames = new Vector();
		Vector inTypes = new Vector();
		Vector outNames = new Vector();
		Vector outTypes = new Vector();
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
        
        Iterator it = outTypes.iterator();
       
        // check arguments
        Object[] inputs = new Object[inNames.size()];
        if(arguments!=null) {
    		if(inNames.size() != arguments.length)
    			throw new RPCException("Invalid arguments count for operation " + methodName+" ("+arguments.length+" instead of "+inNames.size()+")");
    		
            for(int pos = 0; pos < inNames.size(); pos++) {
    			p = (Parameter)inTypes.get(pos);
                inputs[pos]=getArgumentData(axisService.getTypeMappingRegistry().getDefaultTypeMapping(),ThreadLocalPageContext.getTimeZone(config), p, arguments[pos]);
    		}
        }
        else {
            UDFImpl.argumentCollection(namedArguments);
            if(inNames.size() != namedArguments.size())
                throw new RPCException("Invalid arguments count for operation " + methodName+" ("+namedArguments.size()+" instead of "+inNames.size()+")");
            
            
            Object arg;
            String name;
            for(int pos = 0; pos < inNames.size(); pos++) {
                p = (Parameter)inTypes.get(pos);
                name=p.getName();
                arg=namedArguments.get(name,null);
                
                if(arg==null) {
                    throw new RPCException("Invalid arguments for operation " + methodName,
                            getErrorDetailForArguments((String[])inNames.toArray(new String[inNames.size()]),namedArguments.keysAsString()));
                }
                inputs[pos]=getArgumentData(axisService.getTypeMappingRegistry().getDefaultTypeMapping(),ThreadLocalPageContext.getTimeZone(config), p, arg);
            }
        }
        
        Object ret=null;
        if(proxyData!=null && !StringUtil.isEmpty(proxyData.getServer(),true)) {
        	try {
	        	Proxy.start(proxyData);
	    		//print.out("err:"+call.getSOAPActionURI());
	    		ret = call.invoke(inputs);
	        	
	        }
	        finally {
	        	Proxy.end();
	        }
        }
        else {
        	//print.out(call.getUsername());
        	ret = call.invoke(inputs);
        	
        }
		
		if(outNames.size()<=1) return AxisCaster.toRailoType(null,ret);
        //getParamData((org.apache.axis.client.Call)call,parameters.returnParam,ret);
		Map outputs = call.getOutputParams();
		
		StructImpl sct = new StructImpl();
		for(int pos = 0; pos < outNames.size(); pos++) {
			String name = (String)outNames.get(pos);
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
        	Class rtnClass=tm.getClassForQName(type.getQName());
        	if(rtnClass!=null && rtnClass.getName().equals(getClientClassName(type))) return;
        	ClassLoader clb=null;
			try {
				clb = config.getRPCClassLoader(false);
			} catch (IOException e) {}
			
        	Class cls = mapComplex(config,call,tm, type,els);   
    		
        	// TODO make a better impl; this is not the fastest way to make sure all pojos use the same classloader
    		if(cls!=null && clb!=cls.getClassLoader()){
    			mapComplex(config,call,tm, type,els);  
        		//print.out("loader1:"+clb);
        		//print.out("loader2:"+cls.getClassLoader());
        	}
    		
        }
	}


	private Class mapComplex(Config config,Call call, org.apache.axis.encoding.TypeMapping tm, TypeEntry type, Vector children) throws PageException {
		Iterator it = children.iterator();
		ElementDecl el;
		ArrayList properties=new ArrayList();
		Class clazz;
		TypeEntry t;
		String name;
		while(it.hasNext()){
			clazz=null;
        	el=(ElementDecl) it.next();
        	t=el.getType();
        	Vector els = t.getContainedElements();
            if(els!=null) {
            	clazz=mapComplex(config, call, tm, t, els);
            }
        	name=railo.runtime.type.List.last(el.getQName().getLocalPart(), '>');
        	
        	if(clazz==null)clazz=tm.getClassForQName(t.getQName());
        	if(clazz==null)clazz=Object.class;
        	
        	properties.add(new ASMPropertyImpl(clazz,name));
        }
		
		String clientClassName=getClientClassName(type);
    	Pojo obj = (Pojo) ComponentUtil.getClientComponentPropertiesObject(config,clientClassName,(ASMProperty[])properties.toArray(new ASMProperty[properties.size()]));
    	
    	
    	// TODO not alwys register
    	call.registerTypeMapping(
        		obj.getClass(), 
        		type.getQName(), 
    			new BeanSerializerFactory(obj.getClass(), type.getQName()), 
    			new BeanDeserializerFactory(obj.getClass(), type.getQName()),false);
    	return obj.getClass();
	}

	private String getClientClassName(TypeEntry type) {
		
		String url=urlToClass(wsdlUrl);
		
		String className=type.getQName().getLocalPart();
		
		String ns = type.getQName().getNamespaceURI();
		//print.out("ns:"+ns);
		if(ns!=null){
			ns=StringUtil.replace(ns, "http://", "", true);
			ns=toClassName(ns,true);
			if(!StringUtil.isEmpty(ns))
				return url+"."+ns+"."+className;
		}
		return url+"."+className;
	} 

	private static String urlToClass(String wsdlUrl) {
		
		StringBuffer sb=new StringBuffer();
		try {
			URL url = new URL(wsdlUrl);
			
			sb.append(toClassName(url.getHost(), true));
			if(!StringUtil.isEmpty(url.getPath())){
				sb.append('.');
				sb.append(toClassName(url.getPath(), false));
			}
			if(!StringUtil.isEmpty(url.getQuery()) && !"wsdl".equals(url.getQuery())){
				sb.append('.');
				sb.append(toClassName(url.getQuery(), false));
			}
			if("http".equalsIgnoreCase(url.getProtocol())){}
			else if("https".equalsIgnoreCase(url.getProtocol())){
				sb.append(".secure");
			}
			else{
				sb.append('.');
				sb.append(toClassName(url.getProtocol(), false));
			}
			
			if(url.getPort()>0){
				sb.append(".p");
				sb.append(url.getPort());
			}
			
			return sb.toString();
		} 
		catch (MalformedURLException e) {e.printStackTrace();
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
				sb.insert(0,StringUtil.toVariableName(arr[i]));
				
			}
			else {
				if(sb.length()>0)sb.append('.');
				sb.append(StringUtil.toVariableName(arr[i]));
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
		Object o = AxisCaster.toAxisType(tm,tz,paramType,arg);
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
            DumpTable table = new DumpTable("#BEDAFD","#C6D800","#000000");
            table.appendRow(1,new SimpleDumpData("webservice"),new SimpleDumpData(wsdlUrl));
            return table;
        }
    }
    private DumpData _toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) throws RPCException {
                
    	DumpTable box = new DumpTable("#BEDAFD","#C6D800","#000000");
        box.setTitle("Web Service");
        box.appendRow(1,new SimpleDumpData("url"),new SimpleDumpData(wsdlUrl));
        DumpTable functions = new DumpTable("#BEDAFD","#C6D800","#000000");
        
        
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
            //parameters = (Parameters)bEntry.getParameters().get(tmpOp);
            functions.appendRow(1,
            		new SimpleDumpData(tmpOp.getName()),
            		_toHTMLOperation((Parameters)bEntry.getParameters().get(tmpOp)));
        }
        
        box.appendRow(1,new SimpleDumpData(""),functions);
        return box;
    }

    private DumpData _toHTMLOperation(Parameters parameters) {
    	DumpTable table = new DumpTable("#BEDAFD","#E1E97C","#000000");
    	DumpTable attributes = new DumpTable("#BEDAFD","#E1E97C","#000000");
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
    public Iterator keyIterator() {
    	List list=new ArrayList();
    	javax.wsdl.Service service = null;
        Port port = null;
    	try {
    		service = getWSDLService();
            port = getWSDLPort(service);
    	}
    	catch(Exception e) {
    		return new ArrayIterator(ArrayUtil.OBJECT_EMPTY);
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
            list.add(tmpOp.getName());
            
        }
        return new ArrayIterator(list.toArray());
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
}