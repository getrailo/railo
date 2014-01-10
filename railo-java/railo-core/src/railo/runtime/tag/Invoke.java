package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.net.rpc.client.RPCClient;
import railo.runtime.op.Caster;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;

/*
 * FUTURE tag invoke
 * Attributes: servicePort,timeout
 * */


/**
* Invokes component methods from within a page or component. 
* 			You use this tag to reference a WSDL file and consume a web service from within a block of CFML code.
*
*
*
**/
public final class Invoke  extends BodyTagImpl implements DynamicAttributes {

	private Struct data=new StructImpl(StructImpl.TYPE_LINKED);
	//private Map attributes = new HashTable();
	//private HashSet keys = new HashSet();
	
	private boolean hasBody;
	
	private Object component;
	private String method;
	private String returnvariable;
	private String username;
	private String password;
	private String webservice;
	private int timeout=-1;
	private String serviceport;
	private ProxyData proxy=new ProxyDataImpl();


	@Override
	public void release()	{
		super.release();
		data.clear();
		component=null;
		method=null;
		returnvariable=null;
		username=null;
		password=null;
		webservice=null;
		timeout=-1;
		serviceport=null;
		proxy.release();
	}


	/**
	 * @param component the component to set
	 */
	public void setComponent(Object component) {
		this.component = component;
	}


	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param proxyserver the proxyserver to set
	 */
	public void setProxyserver(String proxyserver) {
		proxy.setServer(proxyserver);
	}

	/**
	 * @param proxyport the proxyport to set
	 */
	public void setProxyport(double proxyport) {
		proxy.setPort((int)proxyport);
	}

	/**
	 * @param proxyuser the proxyuser to set
	 */
	public void setProxyuser(String proxyuser) {
		proxy.setUsername(proxyuser);
	}

	/**
	 * @param proxypassword the proxypassword to set
	 */
	public void setProxypassword(String proxypassword) {
		proxy.setPassword(proxypassword);
	}

	/**
	 * @param returnvariable the returnvariable to set
	 */
	public void setReturnvariable(String returnvariable) {
		this.returnvariable = returnvariable.trim();
	}


	/**
	 * @param serviceport the serviceport to set
	 */
	public void setServiceport(String serviceport) {
		this.serviceport = serviceport;
	}


	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(double timeout) {
		this.timeout = (int) timeout;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * @param webservice the webservice to set
	 */
	public void setWebservice(String webservice) {
		this.webservice = webservice.trim();
	}
	
	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) {
		setDynamicAttribute(uri, KeyImpl.init(localName), value);
	}
	
	@Override
	public void setDynamicAttribute(String uri, railo.runtime.type.Collection.Key localName, Object value) {
		data.setEL(localName, value);
	}


	@Override
	public int doStartTag() throws PageException	{
	    return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws PageException	{
		// CFC
		if(component!=null){
			doComponent(component);
		}
		// Webservice
		else if(!StringUtil.isEmpty(webservice)){
			doWebService(webservice);
		}
		// call active cfc or component
		else {
            doFunction(pageContext);
        }
		return EVAL_PAGE;
	}



	/**
	 * @param oComponent
	 * @throws PageException
	 */
	private void doComponent(Object oComponent) throws PageException {
		railo.runtime.Component component=null;
		if(oComponent instanceof railo.runtime.Component)
			component=(railo.runtime.Component)oComponent;
		else
			component=pageContext.loadComponent(Caster.toString(oComponent));
			
		// execute
		Object rtn=component.callWithNamedValues(pageContext,method,data);
		
		// return 
		if(!StringUtil.isEmpty(returnvariable)) pageContext.setVariable(returnvariable,rtn);
	}
	
	private void doFunction(PageContext pc) throws PageException {
			
		// execute
		if(StringUtil.isEmpty(method,true))
			throw new ApplicationException("Attribute [method] for tag [invoke] is required in this context.");
		
		Object oUDF=pc.getVariable(method);
		if(!(oUDF instanceof UDF))throw new ApplicationException("there is no function with name "+method); 
		Object rtn = ((UDF)oUDF).callWithNamedValues(pageContext, data, false);
		
		
		// return 
		if(!StringUtil.isEmpty(returnvariable)) pageContext.setVariable(returnvariable,rtn);
	}

	/**
	 * @param webservice
	 * @throws PageException
	 */
	private void doWebService(String webservice) throws PageException {
        if(username!=null)   {
            if(password==null)password = "";
        }
        ProxyData pd=StringUtil.isEmpty(proxy.getServer())?null:proxy;
        RPCClient ws = username!=null?new RPCClient(webservice,username,password,pd):new RPCClient(webservice,pd);
        Object rtn = ws.callWithNamedValues(pageContext,method,data);
        
        // return
        if(!StringUtil.isEmpty(returnvariable)) pageContext.setVariable(returnvariable,rtn);
        
        //throw new ApplicationException("type webservice is not yet implemented for tag invoke");
	}

	/**
	 * @param name
	 * @param value
	 * @throws PageException
	 */
	public void setArgument(String name,Object value) throws PageException {
		data.set(name,value);		
	}
	
	/**
	 * sets if taf has a body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
		this.hasBody=hasBody;
	}

}