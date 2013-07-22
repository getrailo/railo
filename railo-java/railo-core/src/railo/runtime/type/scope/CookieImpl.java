package railo.runtime.type.scope;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.date.DateTimeUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.date.DateCaster;
import railo.runtime.security.ScriptProtect;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.util.KeyConstants;

/**
 * Implementation of the Cookie scope
 */
public final class CookieImpl extends ScopeSupport implements Cookie,ScriptProtected {
	
	
	private static final long serialVersionUID = -2341079090783313736L;

	public static final int NEVER = 946626690;
	
	private HttpServletResponse rsp;
    private int scriptProtected=ScriptProtected.UNDEFINED;
	private Map<String,String> raw=new HashMap<String,String>();
	private String charset;

	
	private static final Class[] IS_HTTP_ONLY_ARGS_CLASSES = new Class[]{};
	private static final Object[] IS_HTTP_ONLY_ARGS = new Object[]{}; 

	private static final Class[] SET_HTTP_ONLY_ARGS_CLASSES = new Class[]{boolean.class};
	private static final Object[] SET_HTTP_ONLY_ARGS = new Object[]{Boolean.TRUE};

	private static final int EXPIRES_NULL = -1; 
	private static Method isHttpOnly; 
	private static Method setHttpOnly;
    
	/**
	 * constructor for the Cookie Scope
	 */
	public CookieImpl() {
		super(false,"cookie",SCOPE_COOKIE);		
	}
	

    @Override
    public Object setEL(Collection.Key key, Object value) {
        try {
            return set(key,value);
        } catch (PageException e) {
           return null;
        }
    }

	public Object set(Collection.Key key, Object value) throws PageException {
		raw.remove(key.getLowerString());
    	
		if(Decision.isStruct(value)) {
			Struct sct = Caster.toStruct(value);
			int expires=Caster.toIntValue(sct.get(KeyConstants._expires,null),EXPIRES_NULL);
			Object val=sct.get(KeyConstants._value,null);
			boolean secure=Caster.toBooleanValue(sct.get(KeyConstants._secure,null),false);
			boolean httpOnly=Caster.toBooleanValue(sct.get(KeyConstants._httponly,null),false);
			String domain=Caster.toString(sct.get(KeyConstants._domain,null),null);
			String path=Caster.toString(sct.get(KeyConstants._path,null),null);
			boolean preserveCase=Caster.toBooleanValue(sct.get(KeyConstants._preservecase,null),false);
			Boolean encode=Caster.toBoolean(sct.get(KeyConstants._encode,null),null);
			if(encode==null)encode=Caster.toBoolean(sct.get(KeyConstants._encodevalue,Boolean.TRUE),Boolean.TRUE);
			
			setCookie(key, val, expires, secure, path, domain,httpOnly,preserveCase,encode.booleanValue());
		}
		else setCookie(key,value,null,false,"/",null,false,false,true);
		return value;
	}
	
	private void set(Config config,javax.servlet.http.Cookie cookie) throws PageException {
		
		String name=StringUtil.toLowerCase(ReqRspUtil.decode(cookie.getName(),charset,false));
		raw.put(name,cookie.getValue());
    	if(isScriptProtected())	super.set (KeyImpl.init(name),ScriptProtect.translate(dec(cookie.getValue())));
        else super.set (KeyImpl.init(name),dec(cookie.getValue()));
	}
	
    @Override
    public void clear() {
    	raw.clear();
        Collection.Key[] keys = keys();
        for(int i=0;i<keys.length;i++) {
        	removeEL(keys[i],false);
        }
    }

    @Override

    public Object remove(Collection.Key key) throws PageException {
    	raw.remove(key.getLowerString());
    	return remove(key, true);
    }

    public Object remove(Collection.Key key, boolean alsoInResponse) throws PageException {
    	raw.remove(key.getLowerString());
    	Object obj=super.remove (key);
        if(alsoInResponse)removeCookie(key);
        return obj;
    }

    public Object removeEL(Collection.Key key) {
        return removeEL(key, true);
    }
    
    private Object removeEL(Collection.Key key, boolean alsoInResponse) {
    	raw.remove(key.getLowerString());
    	Object obj=super.removeEL (key);
        if(obj!=null && alsoInResponse) removeCookie(key);
        return obj;
    }
    
    private void removeCookie(Collection.Key key) {
        javax.servlet.http.Cookie cookie=new javax.servlet.http.Cookie(key.getUpperString(),"");
		cookie.setMaxAge(0);
		cookie.setSecure(false);
		cookie.setPath("/");
		rsp.addCookie(cookie);
    }
    
    @Override
	public void setCookie(Collection.Key key, Object value, Object expires, boolean secure, String path, String domain) throws PageException {
    	setCookie(key, value, expires, secure, path, domain, false, false, true);
    }
    
    @Override
	public void setCookie(Collection.Key key, Object value, int expires, boolean secure, String path, String domain) throws PageException {
    	setCookie(key, value, expires, secure, path, domain, false, false, true);
    }
    
    @Override
	public void setCookieEL(Collection.Key key, Object value, int expires, boolean secure, String path, String domain) {
    	setCookieEL(key, value, expires, secure, path, domain, false, false, true);
    }
    
    @Override
	public void setCookie(Collection.Key key, Object value, Object expires, boolean secure, String path, String domain, 
			boolean httpOnly, boolean preserveCase, boolean encode) throws PageException {
		int exp=EXPIRES_NULL;
		
		// expires
		if(expires==null) {
			exp=EXPIRES_NULL;
		}
		else if(expires instanceof Date) {
			exp=toExpires((Date)expires);
		}
		else if(expires instanceof TimeSpan) {
			exp=toExpires((TimeSpan)expires);
		}
		else if(expires instanceof Number) {
			exp=toExpires((Number)expires);
		}
		else if(expires instanceof String) {
			exp=toExpires((String)expires);
			
		}
		else {
			throw new ExpressionException("invalid type ["+Caster.toClassName(expires)+"] for expires");
		}
		
		setCookie(key, value, exp, secure, path, domain,httpOnly,preserveCase,encode);
	}
    
    @Override
    public void setCookie(Collection.Key key, Object value, int expires, boolean secure, String path, String domain, 
    		boolean httpOnly, boolean preserveCase, boolean encode) throws PageException {
    	
    	_addCookie(key,Caster.toString(value),expires,secure,path,domain,httpOnly,preserveCase,encode);
        super.set (key, value);
    }


	@Override
    public void setCookieEL(Collection.Key key, Object value, int expires, boolean secure, String path, String domain, 
    		boolean httpOnly, boolean preserveCase, boolean encode) {
		
		_addCookie(key,Caster.toString(value,""),expires,secure,path,domain,httpOnly,preserveCase,encode);
        super.setEL (key, value);
    }
    
    private void _addCookie(Key key, String value, int expires, boolean secure, String path, String domain, 
    		boolean httpOnly, boolean preserveCase, boolean encode) {
    	String name=preserveCase?key.getString():key.getUpperString();
    	
    	// build the value
    	StringBuilder sb=new StringBuilder();
		/*Name*/ sb.append(enc(name)).append('=').append(enc(value));
		/*Path*/sb.append(";Path=").append(enc(path));
		/*Domain*/if(!StringUtil.isEmpty(domain))sb.append(";Domain=").append(enc(domain));
		/*Expires*/if(expires!=EXPIRES_NULL)sb.append(";Expires=").append(DateTimeUtil.toHTTPTimeString(System.currentTimeMillis()+(expires*1000L),false));
		/*Secure*/if(secure)sb.append(";Secure");
		/*HTTPOnly*/if(httpOnly)sb.append(";HTTPOnly");
		
		rsp.addHeader("Set-Cookie", sb.toString());
        
	}
    
    /*private void _addCookieOld(Key key, String value, int expires, boolean secure, String path, String domain, 
    		boolean httpOnly, boolean preserveCase, boolean encode) {
    	String name=preserveCase?key.getString():key.getUpperString();
    	if(encode) {
    		name=enc(name);
    		value=enc(value);
    	}
    	 
    	javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name,value);
        cookie.setMaxAge(expires);
        cookie.setSecure(secure);
        cookie.setPath(path);
        if(!StringUtil.isEmpty(domain,true))cookie.setDomain(domain);
        if(httpOnly) setHTTPOnly(cookie);
        rsp.addCookie(cookie);
        
	}*/


	private int toExpires(String expires) throws ExpressionException {
    	String str=StringUtil.toLowerCase(expires.toString());
		if(str.equals("now"))return 0;
		else if(str.equals("never"))return NEVER;
		else {
			DateTime dt = DateCaster.toDateAdvanced(expires,false,null,null);
			if(dt!=null) {
		        return toExpires(dt);
		    }
		    return toExpires(Caster.toIntValue(expires));
		}
	}


	private int toExpires(Number expires) {
		return toExpires(expires.intValue());
	}
	private int toExpires(int expires) {
		return expires*24*60*60;
	}


	private int toExpires(Date expires) {
    	double diff = expires.getTime()-System.currentTimeMillis();
    	return (int)Math.round(diff/1000D);
	}
	private int toExpires(TimeSpan span) {
    	return (int)span.getSeconds();
	}



    
	

	@Override
	public void initialize(PageContext pc) {
		Config config = ThreadLocalPageContext.getConfig(pc);
		charset = pc.getConfig().getWebCharset();
		if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
        super.initialize(pc);
		
		HttpServletRequest req = pc. getHttpServletRequest();
		this.rsp=pc. getHttpServletResponse();
		javax.servlet.http.Cookie[] cookies=ReqRspUtil.getCookies(config,req);
		try {
			for(int i=0;i<cookies.length;i++) {
				set(config,cookies[i]);
			}
		} 
		catch (Exception e) {}
	}
	
	@Override
	public void release() {
		raw.clear();
		scriptProtected=ScriptProtected.UNDEFINED;
		super.release();
	}
	
	@Override
	public void release(PageContext pc) {
		raw.clear();
		scriptProtected=ScriptProtected.UNDEFINED;
		super.release(pc);
	}
	
	

	@Override
	public boolean isScriptProtected() {
		return scriptProtected==ScriptProtected.YES;
	}

	@Override
	public void setScriptProtecting(ApplicationContext ac,boolean scriptProtected) {
		int _scriptProtected = scriptProtected?ScriptProtected.YES:ScriptProtected.NO;
		if(isInitalized() && _scriptProtected!=this.scriptProtected) {
			Iterator<Entry<String, String>> it = raw.entrySet().iterator();
			Entry<String, String>  entry;
			String key,value;
			
			while(it.hasNext()){
				entry = it.next();
				key=entry.getKey().toString();
				value=dec(entry.getValue().toString());
				super.setEL(KeyImpl.init(key), scriptProtected?ScriptProtect.translate(value):value);
			}
		}
		this.scriptProtected=_scriptProtected;
	}


    public String dec(String str) {
    	return ReqRspUtil.decode(str,charset,false);
	}
    public String enc(String str) {
    	if(ReqRspUtil.needEncoding(str, true))
    		return ReqRspUtil.encode(str,charset);
    	return str;
	}


	@Override
	public void resetEnv(PageContext pc) {
	}


	@Override
	public void touchBeforeRequest(PageContext pc) {
	}


	@Override
	public void touchAfterRequest(PageContext pc) {
	}
	
	public static void setHTTPOnly(javax.servlet.http.Cookie cookie) {
    	try {
	    	if(setHttpOnly==null) {
				  setHttpOnly=cookie.getClass().getMethod("setHttpOnly", SET_HTTP_ONLY_ARGS_CLASSES);
			}
	    	setHttpOnly.invoke(cookie, SET_HTTP_ONLY_ARGS);
    	}
		catch (Throwable t) {
			// HTTPOnly is not supported in this enviroment
		}
	}
	
	public static boolean isHTTPOnly(javax.servlet.http.Cookie cookie) {
    	try {
	    	if(isHttpOnly==null) {
	    		isHttpOnly=cookie.getClass().getMethod("isHttpOnly", IS_HTTP_ONLY_ARGS_CLASSES);
			}
	    	return Caster.toBooleanValue(isHttpOnly.invoke(cookie, IS_HTTP_ONLY_ARGS));
    	}
		catch (Throwable t) {
			return false;
		}
	}
}