package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.security.Credential;
import railo.runtime.security.CredentialImpl;
import railo.runtime.type.Scope;
import railo.runtime.util.ApplicationContext;

/**
 * 
 */
public final class Loginuser extends TagImpl {
    private String name;
    private String password;
    private String[] roles;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		name=null;
		password=null;
		roles=null;
	}

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @param oRoles The roles to set.
     * @throws PageException
     */
    public void setRoles(Object oRoles) throws PageException {
        roles=CredentialImpl.toRole(oRoles);
    }
    
	/**
	* @throws PageException
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
	    Credential login = new CredentialImpl(name,password,roles);
	    pageContext.setRemoteUser(login);
	    
	    Tag parent=getParent();
		while(parent!=null && !(parent instanceof Login)) {
			parent=parent.getParent();
		}
		ApplicationContext appContext = pageContext.getApplicationContext();
		if(parent!=null) {
		    int loginStorage = appContext.getLoginStorage();
		    if(loginStorage==Scope.SCOPE_SESSION && pageContext.getApplicationContext().isSetSessionManagement())
		        pageContext.sessionScope().set("cfauthorization",login.encode());
		    else  {//if(loginStorage==Scope.SCOPE_COOKIE)
		        pageContext.cookieScope().setCookie("cfauthorization_"+appContext.getName(),login.encode(),
		                -1,false,"/",null);
		    }
		}
		
	    
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}