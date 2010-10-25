package railo.runtime.tag;


import java.io.IOException;

import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.mail.EmailAttachment;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
/**
* Can either attach a file or add a header to a message. It is nested within a cfmail tag. You can 
*   use more than one cfmailparam tag within a cfmail tag.
*
*
*
**/
public final class MailParam extends TagImpl {

	/** Indicates the value of the header. */
	private String value="";

	/** Attaches the specified file to the message. This attribute is mutually exclusive with the 
	** 		name attribute. */
	private String file;

	/** Specifies the name of the header. Header names are case insensitive. This attribute is mutually 
	** 		exclusive with the file attribute. */
	private String name;
	
	private String type="";
    private String disposition=null;
    private String contentID=null;
    private Boolean remove=false;
    private byte[] content=null;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		value="";
		file=null;
		name=null;
		type="";
        disposition=null;
        contentID=null;
        remove=null;
        content=null;
	}
	
	/**
	 * @param remove the remove to set
	 */
	public void setRemove(boolean remove) {
		this.remove = Caster.toBoolean(remove);
	}


	/**
	 * @param content the content to set
	 * @throws ExpressionException 
	 */
	public void setContent(Object content) throws ExpressionException {
		if(content instanceof String)this.content = ((String)content).getBytes();
		else this.content = Caster.toBinary(content);
	}

	/**
	 * @param type
	 */
	public void setType(String type)	{
		type=type.toLowerCase().trim();
		
		if(type.equals("text"))type="text/plain";
		else if(type.equals("plain"))type="text/plain";
		else if(type.equals("html"))type="text/html";
		
		this.type=type;
	}

	/** set the value value
	*  Indicates the value of the header.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value file
	*  Attaches the specified file to the message. This attribute is mutually exclusive with the 
	* 		name attribute.
	* @param strFile value to set
	 * @throws PageException 
	**/
	public void setFile(String strFile) throws PageException	{
		this.file=strFile;
	}

	/** set the value name
	*  Specifies the name of the header. Header names are case insensitive. This attribute is mutually 
	* 		exclusive with the file attribute.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

    /**
     * @param disposition The disposition to set.
     * @throws ApplicationException 
     */
    public void setDisposition(String disposition) throws ApplicationException {
        disposition=disposition.trim().toLowerCase();
        if(disposition.equals("attachment")) this.disposition=EmailAttachment.ATTACHMENT;
        else if(disposition.equals("inline"))this.disposition=EmailAttachment.INLINE;
        else 
        throw new ApplicationException("disposition must have one of the following values (attachment,inline)");
        
    }
    /**
     * @param contentID The contentID to set.
     */
    public void setContentid(String contentID) {
        this.contentID = contentID;
    }


	/**
	* @throws PageException 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		
		if(content!=null){
			required("mailparam", "file", file);
			String filename = List.last(file, "/\\");
			Resource res = SystemUtil.getTempDirectory().getRealResource(filename);
			if(res.exists())ResourceUtil.removeEL(res, true);
			try {
				IOUtil.write(res, content);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			} 
			this.file=ResourceUtil.getCanonicalPathEL(res);
			remove=true;
		}
		else if(!StringUtil.isEmpty(this.file)) {
			Resource res=ResourceUtil.toResourceNotExisting(pageContext,this.file);
	        if(res!=null) {
	            if(res.exists())pageContext.getConfig().getSecurityManager().checkFileLocation(res);
	            this.file=ResourceUtil.getCanonicalPathEL(res);
	        } 
		}
		
		
		
		
		// check attributes
		boolean hasFile=!StringUtil.isEmpty(file);
		boolean hasName=!StringUtil.isEmpty(name);
		// both attributes
		if(hasName && hasFile) {
			throw new ApplicationException("Wrong Context for tag MailParam, you cannot use attribute file and name together");
		}
		// no attributes
		if(!hasName && !hasFile) {
			throw new ApplicationException("Wrong Context for tag MailParam, you must use attribute file or attribute name for this tag");
		}
		
		// get Mail Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Mail)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof Mail) {
			Mail mail = (Mail)parent;
			mail.setParam(type,file,name,value,disposition,contentID,remove);
		}
		else {
			throw new ApplicationException("Wrong Context, tag MailParam must be inside a Mail tag");	
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