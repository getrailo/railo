package railo.runtime.type.scope;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.security.ScriptProtect;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ReadOnlyStruct;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.StructUtil;

/**
 *
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class CGIImpl extends ReadOnlyStruct implements CGI,ScriptProtected {
	
	private static final String[] keys={
			"auth_password", "auth_type", "auth_user", "cert_cookie", "cert_flags", "cert_issuer"
			, "cert_keysize", "cert_secretkeysize", "cert_serialnumber", "cert_server_issuer", "cert_server_subject", "cert_subject"
			, "cf_template_path", "content_length", "content_type", "gateway_interface", "http_accept", "http_accept_encoding"
			, "http_accept_language", "http_connection", "http_cookie", "http_host", "http_user_agent", "http_referer"
			, "https", "https_keysize", "https_secretkeysize", "https_server_issuer", "https_server_subject", "path_info"
			, "path_translated", "query_string", "remote_addr", "remote_host", "remote_user", "request_method"
			, "script_name", "server_name", "server_port", "server_port_secure", "server_protocol", "server_software"
            , "web_server_api", "context_path"
            , "local_addr", "local_host"
	};

	public static final Collection.Key SCRIPT_NAME = KeyImpl.intern("script_name");
	public static final Collection.Key PATH_INFO = KeyImpl.intern("path_info");
	public static final Collection.Key HTTP_IF_MODIFIED_SINCE = KeyImpl.intern("http_if_modified_since");
	public static final Collection.Key AUTH_TYPE = KeyImpl.intern("auth_type");
	public static final Collection.Key CF_TEMPLATE_PATH = KeyImpl.intern("cf_template_path");
	public static final Collection.Key REMOTE_USER = KeyImpl.intern("remote_user");
	public static final Collection.Key REMOTE_ADDR = KeyImpl.intern("remote_addr");
	public static final Collection.Key REMOTE_HOST = KeyImpl.intern("remote_host");
	public static final Collection.Key REQUEST_METHOD = KeyImpl.intern("request_method");
	public static final Collection.Key REQUEST_URI = KeyImpl.intern("request_uri");
	public static final Collection.Key REDIRECT_URL = KeyImpl.intern("REDIRECT_URL");
	public static final Collection.Key REDIRECT_QUERY_STRING = KeyImpl.intern("REDIRECT_QUERY_STRING");
	
	
	
	public static final Collection.Key LOCAL_ADDR = KeyImpl.intern("local_addr");
	public static final Collection.Key LOCAL_HOST = KeyImpl.intern("local_host");
	public static final Collection.Key SERVER_NAME = KeyImpl.intern("server_name");
	public static final Collection.Key SERVER_PROTOCOL = KeyImpl.intern("server_protocol");
	public static final Collection.Key SERVER_PORT = KeyImpl.intern("server_port");
	public static final Collection.Key SERVER_PORT_SECURE = KeyImpl.intern("server_port_secure");
	public static final Collection.Key PATH_TRANSLATED = KeyImpl.intern("path_translated");
	public static final Collection.Key QUERY_STRING = KeyImpl.intern("query_string");
	public static final Collection.Key CONTEXT_PATH = KeyImpl.intern("context_path");
	public static final Collection.Key LAST_MODIFIED = KeyImpl.intern("last_modified");
	
	
	private static Struct staticKeys=new StructImpl();
	static{
		for(int i=0;i<keys.length;i++){
			staticKeys.setEL(KeyImpl.getInstance(keys[i]),"");
		}
	}
	
	private static String localAddress="";
	private static String localHost="";
	
	static {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			localAddress=addr.getHostAddress();
			localHost=addr.getHostName();
		}
		catch(UnknownHostException uhe) {}
	}
	
	private HttpServletRequest req;
	private boolean isInit;
	//private String strBaseFile="fsfd";
	private PageContext pc;
	private Struct https;
	private Struct headers;
	private int scriptProtected;
	
	
	public CGIImpl(){
		this.setReadOnly(true);
	}
	

	/**
	 * @see railo.runtime.type.StructImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return staticKeys.containsKey(key);
	}
	
	/**
	 * @see railo.runtime.type.StructImpl#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return super.containsValue(value);
	}
	/**
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImpl();
		copy(this,sct,deepCopy);
		return sct;
	}
	
	
	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return keys.length;
	}

	public Collection.Key[] keys() {
		return StructUtil.toCollectionKeys(keys);
	}
	
	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {

		if(req==null) {
			req=pc. getHttpServletRequest();
			
			https=new StructImpl();
			headers=new StructImpl();
			String k,v;
			try {
				Enumeration e = req.getHeaderNames();
			
				while(e.hasMoreElements()) {
		    		k = (String)e.nextElement();
		    		v = req.getHeader(k);
		    		//print.err(k.length()+":"+k);
		    		headers.setEL(KeyImpl.init(k),v);
		    		headers.setEL(KeyImpl.init(k=k.replace('-','_')),v);
		    		https.setEL(KeyImpl.init("http_"+k),v);	
		    	}
			}
			catch(Throwable t){t.printStackTrace();}
		}
		String lkey=key.getLowerString();
        
	    
        if(lkey.length()>7) {
            char first=lkey.charAt(0);
            if(first=='a') {
            	if(key.equals(AUTH_TYPE)) return toString(req.getAuthType());
            }
            else if(first=='c')	{
            	if(key.equals(CONTEXT_PATH))return toString(req.getContextPath());
            	if(key.equals(CF_TEMPLATE_PATH)) {
					try {
						return toString(ResourceUtil.getResource(pc, pc.getBasePageSource()));
					} catch (Throwable t) {
						return "";
					}
            	}
            }
            else if(first=='h')	{
            	if(lkey.startsWith("http_")){
        	    	Object o = https.get(key,null);
                    if(o==null && key.equals(HTTP_IF_MODIFIED_SINCE))
                    	o = https.get(LAST_MODIFIED,null);
                    if(o!=null)return doScriptProtect((String)o);
            }
            }
            else if(first=='r') {
                if(key.equals(REMOTE_USER))		return toString(req.getRemoteUser());
                if(key.equals(REMOTE_ADDR))		return toString(req.getRemoteAddr());
                if(key.equals(REMOTE_HOST))		return toString(req.getRemoteHost());
                if(key.equals(REQUEST_METHOD))		return req.getMethod();
                if(key.equals(REQUEST_URI))		return toString(req.getAttribute("javax.servlet.include.request_uri"));
                if(key.getUpperString().startsWith("REDIRECT_")){
                	// from attributes (key sensitive)
                	Object value = req.getAttribute(key.getString());
                	if(!StringUtil.isEmpty(value)) return toString(value);
                	
                	// from attributes (key insensitive)
                	Enumeration<String> names = req.getAttributeNames();
            		String k;
            		while(names.hasMoreElements()){
            			k=names.nextElement();
            			if(k.equalsIgnoreCase(key.getString())) {
            				return toString(req.getAttribute(k));
            			}
            		}
                }
            }
            
            
            else if(first=='l') {
                if(key.equals(LOCAL_ADDR))		return toString(localAddress);
                if(key.equals(LOCAL_HOST))		return toString(localHost);
            }
            else if(first=='s') {
            	if(key.equals(SCRIPT_NAME)) 
            		return ReqRspUtil.getScriptName(req);
        			//return StringUtil.emptyIfNull(req.getContextPath())+StringUtil.emptyIfNull(req.getServletPath());
        		if(key.equals(SERVER_NAME))		return toString(req.getServerName());
                if(key.equals(SERVER_PROTOCOL))	return toString(req.getProtocol());
                if(key.equals(SERVER_PORT))		return Caster.toString(req.getServerPort());
                if(key.equals(SERVER_PORT_SECURE))return req.isSecure()?"1":"0";
                
            }
            else if(first=='p') {
            	if(key.equals(PATH_INFO)) {
            		String pathInfo = Caster.toString(req.getAttribute("javax.servlet.include.path_info"),null);
            	    if(StringUtil.isEmpty(pathInfo)) pathInfo = req.getPathInfo();
            	    if(!StringUtil.isEmpty(pathInfo,true)) return pathInfo;
            	     
            	  //return StringUtil.replace(StringUtil.emptyIfNull(req.getRequestURI()), StringUtil.emptyIfNull(req.getServletPath()),"", true);
            	    return "";
            	}
                //if(lkey.equals(PATH_INFO))		return toString(req.getAttribute("javax.servlet.include.path_info"));
            	if(key.equals(PATH_TRANSLATED))	{
            		try {
						return toString(ResourceUtil.getResource(pc, pc.getBasePageSource()));
					} catch (Throwable t) {
						return "";
					}//toString(req.getServletPath());
            	}
            }
            else if(first=='q') {
            	if(key.equals(QUERY_STRING))return doScriptProtect(toString(ReqRspUtil.getQueryString(req)));
            }
        }
        
        // check header
        String headerValue = (String) headers.get(key,null);//req.getHeader(key.getString());
	    if(headerValue != null) return doScriptProtect(headerValue);
	    
        return other(key,defaultValue);
	}
	
	private Object other(Collection.Key key, Object defaultValue) {
		if(staticKeys.containsKey(key)) return "";
		return defaultValue;
	}
	
	private String doScriptProtect(String value) {
		if(isScriptProtected()) return ScriptProtect.translate(value);
		return value;
	}
	
	private String toString(Object str) {
		return StringUtil.toStringEmptyIfNull(str);
	}
	
	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) {
		Object value=get(key,"");
		if(value==null)value= "";
		return value;
	}
	
	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}
	
	/**
	 * @see railo.runtime.type.scope.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return isInit;
	}
	
	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		isInit=true;
		this.pc=pc;

        if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_CGI)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
	}
	
	/**
	 * @see railo.runtime.type.scope.Scope#release()
	 */
	public void release() {
		isInit=false;
		this.req=null;
		scriptProtected=ScriptProtected.UNDEFINED; 
		pc=null;
		https=null;
		headers=null;
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "CGI Scope", pageContext, maxlevel, dp);
	}
    
    /**
     * @see railo.runtime.type.scope.Scope#getType()
     */
    public int getType() {
        return SCOPE_CGI;
    }
    
    /**
     * @see railo.runtime.type.scope.Scope#getTypeAsString()
     */
    public String getTypeAsString() {
        return "cgi";
    }
    
	public boolean isScriptProtected() {
		return scriptProtected==ScriptProtected.YES;
	}
	
	public void setScriptProtecting(boolean scriptProtecting) {
		scriptProtected=scriptProtecting?ScriptProtected.YES:ScriptProtected.NO;
	}

	public static String getCurrentURL(HttpServletRequest req) { // DIFF 23
		StringBuffer sb=new StringBuffer();
		sb.append(req.isSecure()?"https://":"http://");
		sb.append(req.getServerName());
		sb.append(':');
		sb.append(req.getServerPort());
		if(!StringUtil.isEmpty(req.getContextPath()))sb.append(req.getContextPath());
		sb.append(req.getServletPath());
		return sb.toString();
	}
	
	public static String getDomain(HttpServletRequest req) { // DIFF 23
		StringBuffer sb=new StringBuffer();
		sb.append(req.isSecure()?"https://":"http://");
		sb.append(req.getServerName());
		sb.append(':');
		sb.append(req.getServerPort());
		if(!StringUtil.isEmpty(req.getContextPath()))sb.append(req.getContextPath());
		return sb.toString();
	}
	
	
	
}