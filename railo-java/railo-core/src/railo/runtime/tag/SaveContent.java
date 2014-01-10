package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.Caster;

/**
* Saves the generated content inside the tag body in a variable.
*
*
*
**/
public final class SaveContent extends BodyTagTryCatchFinallyImpl {

	/** The name of the variable in which to save the generated content inside the tag. */
	private String variable;
	private boolean trim;
	private boolean append;
	
	@Override
	public void release()	{
		super.release();
		variable=null;
		trim=false;
		append=false;
	}


	/** set the value variable
	*  The name of the variable in which to save the generated content inside the tag.
	* @param variable value to set
	**/
	public void setVariable(String variable)	{
		this.variable=variable;
	}
	

	public void setTrim(boolean trim)	{
		this.trim=trim;
	}
	
	/**
	* if true, and a variable with the passed name already exists, the content will be appended to the variable instead of overwriting it
	*/
	public void setAppend(boolean append)	{
		this.append=append;
	}
	
	@Override
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}


	@Override
	public int doAfterBody() throws PageException	{
	
		String value = trim ? bodyContent.getString().trim() : bodyContent.getString();
		
		if ( append ) {
		
			value = Caster.toString( VariableInterpreter.getVariableEL( pageContext, variable, "" ), "" ) + value;	// prepend the current variable or empty-string if not found
		}
		
		pageContext.setVariable( variable, value );
		bodyContent.clearBody();
		
		return SKIP_BODY;
	}

	
}