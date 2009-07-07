package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.tag.BodyTagImpl;

/**
* Specifies one part of a multipart e-mail message. Can only be used in the cfmail tag. 
* You can use more than one cfmailpart tag within a cfmail tag
*
*
*
**/
public final class MailPart extends BodyTagImpl {

	railo.runtime.net.mail.MailPart part=new railo.runtime.net.mail.MailPart();
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
        // do not clear because spooler
		part=new railo.runtime.net.mail.MailPart();
	}
	
	/**
     * @param type The type to set.
	 * @throws ApplicationException
     */
    public void setType(String type) throws ApplicationException	{
		type=type.toLowerCase().trim();
		if(type.equals("text/plain") || type.equals("plain") || type.equals("text"))
			part.isHTML(false);
		else if(type.equals("text/html") || type.equals("html") || type.equals("htm"))
		    part.isHTML(true);
		else
			throw new ApplicationException("attribute type of tag mailpart has a invalid values","valid values are [plain,text,html] but value is now ["+type+"]");
	}


    /**
     * @param charset The charset to set.
     */
    public void setCharset(String charset) {
        part.setCharset(charset);
    }
    
    /**
     * @param wraptext The wraptext to set.
     */
    public void setWraptext(double wraptext) {
        part.setWraptext((int)wraptext);
    }
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
	    
		return EVAL_BODY_BUFFERED;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
        part.setBody(bodyContent.getString());
		return SKIP_BODY;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws ApplicationException	{
	    
		// get Mail Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Mail)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof Mail) {
			Mail mail = (Mail)parent;
			mail.setBodyPart(part);
		}
		else {
			throw new ApplicationException("Wrong Context, tag MailPart must be inside a Mail tag");	
		}
		return EVAL_PAGE;
	}
}