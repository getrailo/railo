package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.dynamicEvaluation.Evaluate;
import railo.runtime.op.Caster;

/**
* Outputs variables for debugging purposes. Using cfdump, you can display the contents of simple variables, queries, 
*   arrays, structures, and WDDX variables created with cfwddx. if no var attribute defined it dump the hole site information
*
*
*
**/
public final class Dump extends TagImpl {


	/** Variable to display. Enclose a variable name in pound signs */
	private Object var;

	/** Name of Variable to display */
	private Object eval;

	/** string; header for the dump output. */
	private String label;
	private String format;
	private String output;
	//private double maxlevel=Integer.MAX_VALUE;

	private boolean expand=true;

	private int top=9999;
	private String hide;
	private String show;

	private double keys=9999;
	private boolean showUDFs=true;

	private boolean metainfo=true;
	private boolean abort=false;


	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
	    super.release();
		var=null;
		eval=null;
		label=null;
		//maxlevel=Integer.MAX_VALUE;
		format=null;
		output=null;
		expand=true;
		top=9999;
		hide=null;
		show=null;
		keys=9999;
		metainfo=true;
		showUDFs=true;
		abort=false;
	}
	
	
	
	/**
	 * @param top the top to set
	 */
	public void setTop(double top) {
		this.top = (int) top+1;
	}
	public void setHide(String hide) {
		this.hide = hide;
	}
	public void setShow(String show) {
		this.show = show;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public void setKeys(double keys) {
		this.keys = keys;
	}
	public void setMetainfo(boolean metainfo) {
		this.metainfo = metainfo;
	}



	/** set the value expand
	*  not supported at the moment
	* @param expand value to set
	**/
	public void setExpand(boolean expand)	{
		this.expand=expand;
	}

	/** set the value var
	*  Variable to display. Enclose a variable name in pound signs
	* @param var value to set
	**/
	public void setVar(Object var)	{
		this.var=var;
	}
	
	/** set the value eval
	*  Variable to display. Enclose a variable name in pound signs
	* @param eval value to set
	**/
	public void setEval(Object eval)	{
		this.eval=eval;
	}

	/** set the value label
	*  string; header for the dump output.
	* @param label value to set
	**/
	public void setLabel(String label)	{
		this.label=label;
	}

	/**
	 * @param maxlevel the maxlevel to set
	 */
	public void setMaxlevel(double maxlevel) {
		this.top = (int) maxlevel;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.format = type;
	}
	public void setFormat(String format) {
		this.format = format;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
	    if(var==null && eval!=null) {
	        var=Evaluate.call(pageContext,new Object[]{eval});
	        if(label==null)label=Caster.toString(eval);
	    }
	    
		railo.runtime.functions.other.Dump.call(pageContext,var,label,expand,top,show,hide,output,format,keys,metainfo,showUDFs);
		if(abort)throw new railo.runtime.exp.Abort(railo.runtime.exp.Abort.SCOPE_REQUEST);
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}



	/**
	 * @param showUDFs the showUDFs to set
	 */
	public void setShowudfs(boolean showUDFs) {
		this.showUDFs = showUDFs;
	}



	/**
	 * @param abort the abort to set
	 */
	public void setAbort(boolean abort) {
		this.abort = abort;
	}

}