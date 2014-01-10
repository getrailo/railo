package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;

// TODO tag invokeargument
// attr omit

/**
* Required for cfhttp POST operations, cfhttpparam is used to specify the parameters necessary to 
* 	 build a cfhttp POST.
*
*
*
**/
public final class InvokeArgument extends TagImpl {
	
	/** A variable name for the data being passed. */
	private String name;

	/** Specifies the value of the variable being passed. */
	private Object value;
	private boolean omit;

	


	/** set the value value
	* @param value value to set
	**/
	public void setValue(Object value)	{
		this.value=value;
	}

	/** set the value name
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}

	/**
	 * @param omit the omit to set
	 */
	public void setOmit(boolean omit) {
		this.omit = omit;
	}

	@Override
	public int doStartTag() throws PageException	{
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Invoke)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof Invoke) {
			Invoke invoke = (Invoke)parent;
			invoke.setArgument(name,value);
		}
		else {
			throw new ApplicationException("Wrong Context, tag InvokeArgument must be inside a Invoke tag");	
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		value=null;
		name=null;
		omit=false;
	}
}