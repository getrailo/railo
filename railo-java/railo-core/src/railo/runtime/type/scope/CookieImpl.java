package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import railo.commons.lang.StringUtil;
import railo.commons.lang.URLEncoder;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.security.ScriptProtect;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.util.ApplicationContext;

/**
 * Implementation of the Cookie scope
 */
public final class CookieImpl extends ScopeSupport implements Cookie,ScriptProtected {
	
	
	public static final int NEVER = 946626690;
	
	
	
	private javax.servlet.http.Cookie[] cookies;
	private HttpServletRequest req;
	private HttpServletResponse rsp;
	private URLCodec codec=new URLCodec();
    private String charset;
    private int scriptProtected=ScriptProtected.UNDEFINED;
	private Map raw=new HashMap();
    
	/**
	 * constructor for the Cookie Scope
	 */
	public CookieImpl() {
		super(false,"cookie",SCOPE_COOKIE);		
	}
	

    /**
     * @see railo.runtime.type.StructImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public Object setEL(Collection.Key key, Object value) {
        try {
            return set(key,value);
        } catch (PageException e) {
           return null;
        }
    }

	public Object set(Collection.Key key, Object value) throws PageException {
		raw.remove(key.getLowerString());
    	setCookie(key,value,-1,false,"/",null);
		return value;
	}
	
	private void set(javax.servlet.http.Cookie cookie) throws PageException {
	    try {
	    	String name=StringUtil.toLowerCase(cookie.getName());
	    	raw.put(name,cookie.getValue());
	    	if(isScriptProtected())	super.set (KeyImpl.init(name),ScriptProtect.translate(codec.decode(cookie.getValue())));
            else 				super.set (KeyImpl.init(name),codec.decode(cookie.getValue()));
        } catch (DecoderException e) {
            throw Caster.toPageException(e);
        }
	}
	
    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public void clear() {
    	raw.clear();
        Collection.Key[] keys = keys();
        for(int i=0;i<keys.length;i++) {
        	removeEL(keys[i],false);
        }
    }

    /**
     * @see railo.runtime.type.Collection#remove(java.lang.String)
     */
    public Object remove(String key) throws PageException {
    	return remove(KeyImpl.init(key), true);
    }

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
	
	/**
     * @see railo.runtime.type.scope.Cookie#setCookie(java.lang.String, java.lang.Object, java.lang.Object, boolean, java.lang.String, java.lang.String)
     */
    public void setCookie(String name, Object value, Object expires, boolean secure, String path, String domain) throws PageException {
    	setCookie(KeyImpl.init(name), value, expires, secure, path, domain);
    }
	public void setCookie(Collection.Key key, Object value, Object expires, boolean secure, String path, String domain) throws PageException {
		int exp=-1;
		
		// expires
		if(expires instanceof Date) {
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
			throw new ExpressionException("invalid type ["+expires.getClass().getName()+"] for expires");
		}
		
		setCookie(key, value, exp, secure, path, domain);
	}

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


	/**
     * @see railo.runtime.type.scope.Cookie#setCookie(java.lang.String, java.lang.Object, int, boolean, java.lang.String, java.lang.String)
     */
	public void setCookie(String name, Object value, int expires, boolean secure, String path, String domain) throws PageException {
		setCookie(KeyImpl.init(name), value, expires, secure, path, domain); 
	}
	
    /**
     * @see railo.runtime.type.scope.Cookie#setCookie(railo.runtime.type.Collection.Key, java.lang.Object, int, boolean, java.lang.String, java.lang.String)
     */
    public void setCookie(Collection.Key key, Object value, int expires, boolean secure, String path, String domain) 
        throws PageException {
    	
        javax.servlet.http.Cookie cookie;
        try {
            cookie = new javax.servlet.http.Cookie(
            		key.getUpperString(),
            		URLEncoder.encode(Caster.toString(value),charset));
        } 
        catch (Exception e) {
            throw ExpressionException.newInstance(e);
        }
        cookie.setMaxAge(expires);
        cookie.setSecure(secure);
        cookie.setPath(path);
        if(domain!=null && domain.trim().length()>0)cookie.setDomain(domain);
        rsp.addCookie(cookie);
        super.set (key, value);
    }

    /**
     * @see railo.runtime.type.scope.Cookie#setCookieEL(java.lang.String, java.lang.Object, int, boolean, java.lang.String, java.lang.String)
     */
    public void setCookieEL(String name, Object value, int expires, boolean secure, String path, String domain) {
    	setCookieEL(KeyImpl.init(name), value, expires, secure, path, domain);
    }
    
    public void setCookieEL(Collection.Key key, Object value, int expires, boolean secure, String path, String domain) {
        javax.servlet.http.Cookie cookie;
        try {
            cookie = new javax.servlet.http.Cookie(key.getUpperString(),URLEncoder.encode(Caster.toString(value,"")));
        } catch (UnsupportedEncodingException e) {
            return;
        }
        cookie.setMaxAge(	expires);
        cookie.setSecure(secure);
        cookie.setPath(path);
        if(domain!=null && domain.trim().length()>0)cookie.setDomain(domain);
        rsp.addCookie(cookie);
        super.setEL (key, value);
    }
    
	

	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		charset="ISO-8859-1";
		if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
        super.initialize(pc);
		
		this.req=pc. getHttpServletRequest();
		this.rsp=pc. getHttpServletResponse();
		cookies=req.getCookies();
		try {
			for(int i=0;i<cookies.length;i++) {
				set(cookies[i]);
			}
		} 
		//catch (ExpressionException e) {}
		catch (Exception e) {}
	}
	
	/**
	 *
	 * @see railo.runtime.type.scope.ScopeSupport#release()
	 */
	public void release() {
		raw.clear();
		scriptProtected=ScriptProtected.UNDEFINED;
		super.release();
	}

	/**
	 *
	 * @see railo.runtime.type.scope.ScriptProtected#isScriptProtected()
	 */
	public boolean isScriptProtected() {
		return scriptProtected==ScriptProtected.YES;
	}


	/**
	 *
	 * @see railo.runtime.type.scope.ScriptProtected#setScriptProtecting(boolean)
	 */
	public void setScriptProtecting(boolean scriptProtected) {
		int _scriptProtected = scriptProtected?ScriptProtected.YES:ScriptProtected.NO;
		if(isInitalized() && _scriptProtected!=this.scriptProtected) {
			Iterator it = raw.entrySet().iterator();
			Map.Entry entry;
			String key,value;
			
			while(it.hasNext()){
				entry=(Entry) it.next();
				key=entry.getKey().toString();
				value=entry.getValue().toString();
				try {
					value=codec.decode(value);
				} catch (DecoderException e) {}
				super.setEL(KeyImpl.init(key), scriptProtected?ScriptProtect.translate(value):value);
			}
		}
		this.scriptProtected=_scriptProtected;
	}
}