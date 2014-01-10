package railo.runtime.tag;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import railo.runtime.exp.PageException;
import railo.runtime.exp.TemplateException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

/**
* Generates custom HTTP response headers to return to the client.
*
*
*
**/
public final class Header extends TagImpl {

	/** A value for the HTTP header. This attribute is used in conjunction with the name attribute. */
	private String value="";

	/** Text that explains the status code. This attribute is used in conjunction with the 
	** 	statusCode attribute. */
	private String statustext;

	/** A name for the header. */
	private String name;

	/** A number that sets the HTTP status code. */
	private int statuscode;
	
	private boolean hasStatucCode;

    private String charset;
    
	@Override
	public void release()	{
		super.release();
		value="";
		statustext=null;
		name=null;
		statuscode=0;
		hasStatucCode=false;
        charset=null;
	}


	/** set the value value
	*  A value for the HTTP header. This attribute is used in conjunction with the name attribute.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value statustext
	*  Text that explains the status code. This attribute is used in conjunction with the 
	* 	statusCode attribute.
	* @param statustext value to set
	**/
	public void setStatustext(String statustext)	{
		this.statustext=statustext;
	}

	/** set the value name
	*  A name for the header.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

	/** set the value statuscode
	*  A number that sets the HTTP status code.
	* @param statuscode value to set
	**/
	public void setStatuscode(double statuscode)	{
		this.statuscode=(int) statuscode;
		hasStatucCode=true;
	}
    
    /**
     * @param charset The charset to set.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }


	@Override
	public int doStartTag() throws PageException	{
		
		HttpServletResponse rsp = pageContext. getHttpServletResponse();
		if(rsp.isCommitted())
			throw new TemplateException("can't assign value to header, header is alredy committed");
		
		// set name value
		if(name != null) {
            if(charset==null && name.equalsIgnoreCase("content-disposition")) {
                    charset=pageContext.getConfig().getWebCharset();
            }
            try {
                if(charset!=null) {
                    name = new String(name.getBytes(charset), "ISO-8859-1");
                    value = new String(value.getBytes(charset), "ISO-8859-1");
                }
                else {
                    name = new String(name.getBytes(), "ISO-8859-1");
                    value = new String(value.getBytes(), "ISO-8859-1");
                }
            } 
            catch (UnsupportedEncodingException e) {
                throw Caster.toPageException(e);
            }
            
			if(name.toLowerCase().equals("content-type") && value.length()>0) {
                rsp.setContentType(value);
            }
			else {
                rsp.addHeader(name, value);
            }
		}
		// set status
		if(hasStatucCode) {
    		if(statustext != null) {
    			//try {
    				///rsp.sendError(statuscode, statustext);
    				rsp.setStatus(statuscode,statustext);
    			/*} 
                catch (IOException e) {
    				throw new TemplateException("can't assign value to header, header is alredy committed",e.getMessage());
    			} */
            }
    		else {
                rsp.setStatus(statuscode);
            }
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}