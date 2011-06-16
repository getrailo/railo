package railo.runtime.tag;

import java.io.IOException;

import railo.runtime.coder.Base64Coder;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Caster;
import railo.runtime.security.Credential;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.util.ApplicationContext;

/**
 * 
 */
public final class Login extends BodyTagImpl {
    
    private static final Key CFLOGIN = KeyImpl.getInstance("cflogin");
	private static final Key NAME = KeyImpl.getInstance("name");
	private static final Key PASSWORD = KeyImpl.getInstance("password");
	private int idletimeout=1800;
    private String applicationtoken;
    private String cookiedomain;
    
    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        super.release();
        idletimeout=1800;
        applicationtoken=null;
        cookiedomain=null;
    }
    
    /**
     * @param applicationtoken The applicationtoken to set.
     */
    public void setApplicationtoken(String applicationtoken) {
        this.applicationtoken = applicationtoken;
    }
    /**
     * @param cookiedomain The cookiedomain to set.
     */
    public void setCookiedomain(String cookiedomain) {
        this.cookiedomain = cookiedomain;
    }
    /**
     * @param idletimeout The idletimout to set.
     */
    public void setIdletimeout(double idletimeout) {
        this.idletimeout = (int) idletimeout;
    }
    

    /**
     * @throws PageException
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws PageException  {
    	
    	if(pageContext.getApplicationContext() instanceof ApplicationContextPro){
    		ApplicationContextPro ac=(ApplicationContextPro) pageContext.getApplicationContext();
    		ac.setSecuritySettings(applicationtoken,cookiedomain,idletimeout);
    	}
    	
        Credential remoteUser = pageContext.getRemoteUser();
        if(remoteUser==null) {
            
            // Form
            Object name=pageContext.formScope().get("j_username",null);
            Object password=pageContext.formScope().get("j_password",null);
            if(name!=null) {
                setCFLogin(name,password);
                return EVAL_BODY_INCLUDE;
            }
            // Header
            String strAuth = pageContext. getHttpServletRequest().getHeader("authorization");
            if(strAuth!=null) {
                int pos=strAuth.indexOf(' ');
                if(pos!=-1) {
                    String format=strAuth.substring(0,pos).toLowerCase();
                    if(format.equals("basic")) {
                        String encoded=strAuth.substring(pos+1);
                        String dec;
                        try {
							dec=Base64Coder.decodeToString(encoded,"UTF-8");
						} catch (IOException e) {
							throw Caster.toPageException(e);
						}
                        
                        //print.ln("encoded:"+encoded);
                        //print.ln("decoded:"+Base64Util.decodeBase64(encoded));
                        Array arr=List.listToArray(dec,":");
                        if(arr.size()<3) {
                            if(arr.size()==1) setCFLogin(arr.get(1,null),"");
                            else setCFLogin(arr.get(1,null),arr.get(2,null));
                        }
                    }
                    
                }
            }
            return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }
    
    /**
     * @param username
     * @param password
     */
    private void setCFLogin(Object username, Object password) {
        if(username==null) return;
        if(password==null) password="";
        
        Struct sct=new StructImpl();
        sct.setEL(NAME,username);
        sct.setEL(PASSWORD,password);
        pageContext.undefinedScope().setEL(CFLOGIN,sct);
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() {
        pageContext.undefinedScope().removeEL(CFLOGIN);
        return EVAL_PAGE;
    }

	public static String getApplicationName(ApplicationContext appContext) {
		if(appContext instanceof ApplicationContextPro) {
	    	return "cfauthorization_"+((ApplicationContextPro) appContext).getSecurityApplicationToken();
	    }
	    return "cfauthorization_"+appContext.getName();
	}

	public static String getCookieDomain(ApplicationContext appContext) {
		if(appContext instanceof ApplicationContextPro) {
			((ApplicationContextPro) appContext).getSecurityCookieDomain();
	    }
	    return null;
	}

	public static int getIdleTimeout(ApplicationContext appContext) {
		if(appContext instanceof ApplicationContextPro) {
	    	return ((ApplicationContextPro) appContext).getSecurityIdleTimeout();
	    }
	    return 1800;
	}
}