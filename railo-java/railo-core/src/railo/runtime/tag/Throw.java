package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.CustomTypeException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.ObjectWrap;

/**
* The cfthrow tag raises a developer-specified exception that can be caught with cfcatch tag 
*   having any of the following type specifications - cfcatch type = 'custom_type', cfcatch type = 'Application'
*   'cfcatch' type = 'Any'
*
*
*
**/
public final class Throw extends TagImpl {

	/** A custom error code that you supply. */
	private String extendedinfo=null;

	private String type="application";

	private String detail="";

	/** A message that describes the exceptional event. */
	private Object message;

	/** A custom error code that you supply. */
	private String errorcode="";

	private Object object;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		extendedinfo=null;
		type="application";
		detail="";
		message=null;
		errorcode="";
		object=null;
	}



	/** set the value extendedinfo
	*  A custom error code that you supply.
	* @param extendedinfo value to set
	**/
	public void setExtendedinfo(String extendedinfo)	{
		this.extendedinfo=extendedinfo;
	}

	/** set the value type
	* @param type value to set
	**/
	public void setType(String type)	{
		this.type=type;
	}

	/** set the value detail
	* @param detail value to set
	**/
	public void setDetail(String detail)	{
		this.detail=detail;
	}

	/** set the value message
	*  A message that describes the exceptional event.
	* @param message value to set
	**/
	public void setMessage(Object message)	{
		this.message=message;
	}
	
	/**
	 * @deprecated this method should no longer be used.
	 * */
	public void setMessage(String message)	{
		this.message=message;
	}

	/** set the value errorcode
	*  A custom error code that you supply.
	* @param errorcode value to set
	**/
	public void setErrorcode(String errorcode)	{
		this.errorcode=errorcode;
	}

	/** set the value object
	*  a native java exception Object, if this attribute is defined all other will be ignored.
	* @param object object to set
	 * @throws PageException
	**/
	public void setObject(Object object) throws PageException	{
		this.object=object;	
	}


	private PageException toPageException(Object object, PageException defaultValue) throws PageException {
		if((object instanceof ObjectWrap))
			return toPageException(((ObjectWrap)object).getEmbededObject(),defaultValue);
		
		
		if(object instanceof CatchBlock) {
			CatchBlock cb = (CatchBlock)object;
			return cb.getPageException();
		}
		if(object instanceof PageException) return (PageException)object;
		if(object instanceof Throwable) {
			Throwable t=(Throwable)object;
			return new CustomTypeException(t.getMessage(),"","",t.getClass().getName(),"");
		}
		return defaultValue;
		
	}



	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		
		_doStartTag(message);
		_doStartTag(object);
		
		throw new CustomTypeException( "",detail,errorcode,type,extendedinfo);
	}

	private void _doStartTag(Object obj) throws PageException {
		if(!StringUtil.isEmpty(obj)){
			PageException pe = toPageException(obj,null);
			if(pe!=null) throw pe;
			
			CustomTypeException exception = new CustomTypeException(Caster.toString(obj),detail,errorcode,type,extendedinfo);
			throw exception;
		}
	}



	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}