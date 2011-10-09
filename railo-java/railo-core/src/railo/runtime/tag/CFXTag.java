package railo.runtime.tag;

import railo.runtime.cfx.CFXTagException;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.cfx.RequestImpl;
import railo.runtime.cfx.ResponseImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.AppendixTag;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

/**
* Creates a CFML CFX Tag
*
*
*
**/
public final class CFXTag extends TagImpl implements DynamicAttributes,AppendixTag {

	private Struct attributes=new StructImpl();
	private String appendix;
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		attributes.clear();
		appendix=null;
	}

    /**
     * @param appendix The appendix to set.
     */
    public void setAppendix(String appendix) {
    	//print.out(appendix);
        this.appendix = appendix;
    }
	
	/**
	 * @see railo.runtime.ext.tag.DynamicAttributes#setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void setDynamicAttribute(String domain, String key, Object value) {
		attributes.setEL(key,value);
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws PageException {
	    // RR SerialNumber sn = pageContext.getConfig().getSerialNumber();
	    // if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
	    //     throw new SecurityException("no access to this functionality with the "+sn.getStringVersion()+" version of railo");
	    
		
		CFXTagPool pool=pageContext.getConfig().getCFXTagPool();
		CustomTag ct;
		try {
			ct = pool.getCustomTag(appendix);
		} 
        catch (CFXTagException e) {
			throw Caster.toPageException(e);
		}
		Request req=new RequestImpl(pageContext,attributes);
		Response rsp=new ResponseImpl(pageContext,req.debug());
		try {
			ct.processRequest(req,rsp);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		pool.releaseCustomTag(ct);
		
		return SKIP_BODY;
	}

    public String getAppendix() {
        return appendix;
    }
}