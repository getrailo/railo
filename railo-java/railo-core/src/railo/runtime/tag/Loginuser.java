package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.security.CredentialImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.scope.Scope;

/**
 * 
 */
public final class Loginuser extends TagImpl {
    private String name;
    private String password;
    private String[] roles;

	@Override
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
    
	@Override
	public int doStartTag() throws PageException	{
		Resource rolesDir = pageContext.getConfig().getConfigDir().getRealResource("roles");
	    CredentialImpl login = new CredentialImpl(name,password,roles,rolesDir);
	    pageContext.setRemoteUser(login);
	    
	    Tag parent=getParent();
		while(parent!=null && !(parent instanceof Login)) {
			parent=parent.getParent();
		}
		ApplicationContext appContext = pageContext.getApplicationContext();
		if(parent!=null) {
		    int loginStorage = appContext.getLoginStorage();
		    String name=Login.getApplicationName(appContext);
		    
		    if(loginStorage==Scope.SCOPE_SESSION && pageContext.getApplicationContext().isSetSessionManagement())
		        pageContext.sessionScope().set(KeyImpl.init(name),login.encode());
		    else  {//if(loginStorage==Scope.SCOPE_COOKIE)
		        pageContext.cookieScope().setCookie(KeyImpl.init(name),login.encode(),
		        		-1,false,"/",Login.getCookieDomain(appContext));
		    }
		}
		
	    
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}