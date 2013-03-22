package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;

/**
* Controls various aspects of page processing, such as the output of HTML code in pages. One 
*   benefit of this option is managing whitespace that can occur in output pages served by ColdFusion.
*
*
*
**/
public final class Setting extends BodyTagImpl {

	private boolean hasBody;

    /** set the value requesttimeout
	*  Integer; number of seconds. Time limit, after which ColdFusion processes the page as an unresponsive thread. Overrides the timeout set in the ColdFusion Administrator.
	* @param requesttimeout value to set
	**/
	public void setRequesttimeout(double requesttimeout)	{
		pageContext.setRequestTimeout((long)(requesttimeout*1000));
	}

	/** set the value showdebugoutput
	*  Yes or No. When set to No, showDebugOutput suppresses debugging information that would 
	* 		otherwise display at the end of the generated page.Default is Yes.
	* @param showdebugoutput value to set
	**/
	public void setShowdebugoutput(boolean showdebugoutput)	{
		pageContext.getDebugger().setOutput(showdebugoutput);
	}

	/** set the value enablecfoutputonly
	*  Yes or No. When set to Yes, cfsetting blocks output of HTML that resides outside cfoutput tags.
	* @param enablecfoutputonly value to set
	 * @throws PageException 
	**/
    public void setEnablecfoutputonly(Object enablecfoutputonly) throws PageException   {
        if(enablecfoutputonly instanceof String && 
                Caster.toString(enablecfoutputonly).trim().equalsIgnoreCase("reset")) {
            pageContext.setCFOutputOnly((short)0);
        }
        else {
            pageContext.setCFOutputOnly(Caster.toBooleanValue(enablecfoutputonly));
        }
    }
    
    /**
     * @deprecated this method is replaced by the method <code>setEnablecfoutputonly(Object enablecfoutputonly)</code>
     * @param enablecfoutputonly
     */
    public void setEnablecfoutputonly(boolean enablecfoutputonly)   {
        pageContext.setCFOutputOnly(enablecfoutputonly);
    }

	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_INCLUDE;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

    /**
     * sets if tag has a body or not
     * @param hasBody
     */
    public void hasBody(boolean hasBody) {
        this.hasBody=hasBody;
    }

}