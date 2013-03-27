package railo.runtime.type.scope;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
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
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructUtil;

/**
 *
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class CGIImpl extends ReadOnlyStruct implements CGI,ScriptProtected {
	
	private static final long serialVersionUID = 5219795840777155232L;

	private static final Collection.Key[] keys={
		KeyConstants._auth_password, KeyConstants._auth_type, KeyConstants._auth_user, KeyConstants._cert_cookie, KeyConstants._cert_flags, 
		KeyConstants._cert_issuer, KeyConstants._cert_keysize, KeyConstants._cert_secretkeysize, KeyConstants._cert_serialnumber, 
		KeyConstants._cert_server_issuer, KeyConstants._cert_server_subject, KeyConstants._cert_subject,KeyConstants._cf_template_path, 
		KeyConstants._content_length, KeyConstants._content_type, KeyConstants._gateway_interface, KeyConstants._http_accept, 
		KeyConstants._http_accept_encoding, KeyConstants._http_accept_language, KeyConstants._http_connection, KeyConstants._http_cookie, 
		KeyConstants._http_host, KeyConstants._http_user_agent, KeyConstants._http_referer, KeyConstants._https, KeyConstants._https_keysize, 
		KeyConstants._https_secretkeysize, KeyConstants._https_server_issuer, KeyConstants._https_server_subject, KeyConstants._path_info,
		KeyConstants._path_translated, KeyConstants._query_string, KeyConstants._remote_addr, KeyConstants._remote_host, KeyConstants._remote_user, 
		KeyConstants._request_method, KeyConstants._request_url, KeyConstants._script_name, KeyConstants._server_name, KeyConstants._server_port, KeyConstants._server_port_secure,
		KeyConstants._server_protocol, KeyConstants._server_software, KeyConstants._web_server_api, KeyConstants._context_path, KeyConstants._local_addr, 
		KeyConstants._local_host
	};
	private static Struct staticKeys=new StructImpl();
	static{
		for(int i=0;i<keys.length;i++){
			staticKeys.setEL(keys[i],"");
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
	

	@Override
	public boolean containsKey(Key key) {
		return staticKeys.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return super.containsValue(value);
	}
	@Override
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImpl();
		copy(this,sct,deepCopy);
		return sct;
	}
	
	
	@Override
	public int size() {
		return keys.length;
	}

	public Collection.Key[] keys() {
		return keys;
	}
	
	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		
		if(req==null) {
			req=pc.getHttpServletRequest();
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
            	if(key.equals(KeyConstants._auth_type)) return toString(req.getAuthType());
            }
            else if(first=='c')	{
            	if(key.equals(KeyConstants._context_path))return toString(req.getContextPath());
            	if(key.equals(KeyConstants._cf_template_path)) {
					try {
						return toString(ResourceUtil.getResource(pc, pc.getBasePageSource()));
					} catch (Throwable t) {
						return "";
					}
            	}
            }
            else if(first=='h')	{
            	if(lkey.startsWith("http_")){
        	    	Object o = https.get(key,NullSupportHelper.NULL());
                    if(o==NullSupportHelper.NULL() && key.equals(KeyConstants._http_if_modified_since))
                    	o = https.get(KeyConstants._last_modified,NullSupportHelper.NULL());
                    if(o!=NullSupportHelper.NULL())return doScriptProtect((String)o);
            }
            }
            else if(first=='r') {
                if(key.equals(KeyConstants._remote_user))		return toString(req.getRemoteUser());
                if(key.equals(KeyConstants._remote_addr))		return toString(req.getRemoteAddr());
                if(key.equals(KeyConstants._remote_host))		return toString(req.getRemoteHost());
                if(key.equals(KeyConstants._request_method))		return req.getMethod();
                if(key.equals(KeyConstants._request_url))		return ReqRspUtil.getRequestURL( req, true );
                if(key.equals(KeyConstants._request_uri))		return toString(req.getAttribute("javax.servlet.include.request_uri"));
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
                if(key.equals(KeyConstants._local_addr))		return toString(localAddress);
                if(key.equals(KeyConstants._local_host))		return toString(localHost);
            }
            else if(first=='s') {
            	if(key.equals(KeyConstants._script_name)) 
            		return ReqRspUtil.getScriptName(req);
        			//return StringUtil.emptyIfNull(req.getContextPath())+StringUtil.emptyIfNull(req.getServletPath());
        		if(key.equals(KeyConstants._server_name))		return toString(req.getServerName());
                if(key.equals(KeyConstants._server_protocol))	return toString(req.getProtocol());
                if(key.equals(KeyConstants._server_port))		return Caster.toString(req.getServerPort());
                if(key.equals(KeyConstants._server_port_secure))return req.isSecure()?"1":"0";
                
            }
            else if(first=='p') {
            	if(key.equals(KeyConstants._path_info)) {
            		String pathInfo = Caster.toString(req.getAttribute("javax.servlet.include.path_info"),null);
            		if(StringUtil.isEmpty(pathInfo)) pathInfo = Caster.toString(req.getHeader("xajp-path-info"),null);
            		if(StringUtil.isEmpty(pathInfo)) pathInfo = req.getPathInfo();
            		if(StringUtil.isEmpty(pathInfo)) {
            			pathInfo = Caster.toString(req.getAttribute("requestedPath"),null);
            			if(!StringUtil.isEmpty(pathInfo,true)) {
            				String scriptName = ReqRspUtil.getScriptName(req);
            				if ( pathInfo.startsWith(scriptName) )
                				pathInfo = pathInfo.substring(scriptName.length());
            			}
            		}
            	    
            		if(!StringUtil.isEmpty(pathInfo,true)) return pathInfo;
            	    return "";
            	}
                if(key.equals(KeyConstants._path_translated))	{
            		try {
						return toString(ResourceUtil.getResource(pc, pc.getBasePageSource()));
					} catch (Throwable t) {
						return "";
					}//toString(req.getServletPath());
            	}
            }
            else if(first=='q') {
            	if(key.equals(KeyConstants._query_string))return doScriptProtect(toString(ReqRspUtil.getQueryString(req)));
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
	
	@Override
	public Object get(Collection.Key key) {
		Object value=get(key,"");
		if(value==null)value= "";
		return value;
	}
	
	@Override
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
	
	@Override
	public boolean isInitalized() {
		return isInit;
	}
	
	@Override
	public void initialize(PageContext pc) {
		isInit=true;
		this.pc=pc;
		
        if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_CGI)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
	}
	
	@Override
	public void release() {
		isInit=false;
		this.req=null;
		scriptProtected=ScriptProtected.UNDEFINED; 
		pc=null;
		https=null;
		headers=null;
	}
	
	@Override
	public void release(PageContext pc) {
		isInit=false;
		this.req=null;
		scriptProtected=ScriptProtected.UNDEFINED; 
		pc=null;
		https=null;
		headers=null;
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this, "CGI Scope", pageContext, maxlevel, dp);
	}
    
    @Override
    public int getType() {
        return SCOPE_CGI;
    }
    
    @Override
    public String getTypeAsString() {
        return "cgi";
    }
    
	public boolean isScriptProtected() {
		return scriptProtected==ScriptProtected.YES;
	}
	
	@Override
	public void setScriptProtecting(ApplicationContext ac,boolean scriptProtecting) {
		scriptProtected=scriptProtecting?ScriptProtected.YES:ScriptProtected.NO;
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