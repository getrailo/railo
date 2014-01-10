package railo.runtime.tag;

import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;

public final class Setting extends BodyTagImpl {

	private boolean hasBody;

    /** set the value requesttimeout
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
		if(pageContext.getConfig().debug())pageContext.getDebugger().setOutput(showdebugoutput);
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

	@Override
	public int doStartTag()	{
		return EVAL_BODY_INCLUDE;
	}

	@Override
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