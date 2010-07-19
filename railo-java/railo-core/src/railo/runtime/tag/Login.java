package railo.runtime.tag;

import railo.runtime.coder.Base64Coder;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.security.Credential;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

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
                        //print.ln("encoded:"+encoded);
                        //print.ln("decoded:"+Base64Util.decodeBase64(encoded));
                        Array arr=List.listToArray(Base64Coder.decodeBase64(encoded),":");
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
}