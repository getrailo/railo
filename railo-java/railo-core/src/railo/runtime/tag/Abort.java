package railo.runtime.tag;

import railo.runtime.exp.AbortException;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;

/**
* Stops processing of a page at the tag location. Railo returns everything that was processed before the cfabort tag. The cfabort tag is often used with conditional logic to stop processing a page when a condition occurs.
*
*
*
**/
public final class Abort extends TagImpl {

	/** The error to display when cfabort executes. 
	** 				The error message displays in the standard CFML error page. */
	private String showerror;
    private int type=railo.runtime.exp.Abort.SCOPE_REQUEST;

    /** set the value showerror
    *  The error to display when cfabort executes. 
    *               The error message displays in the standard CFML error page.
    * @param showerror value to set
    **/
    public void setShowerror(String showerror)  {
        this.showerror=showerror;
    }
    

    /**
     * sets the type of the abort (page,request)
     * @param type
     * @throws ApplicationException
     */
    public void setType(String type) throws ApplicationException  {
        type=type.toLowerCase().trim();
        if(type.equals("page"))this.type=railo.runtime.exp.Abort.SCOPE_PAGE;
        else if(type.equals("request"))this.type=railo.runtime.exp.Abort.SCOPE_REQUEST;
        else throw new ApplicationException("attribute type has an invalid value ["+type+"], valid values are [page,request]");
    }


	@Override
	public int doStartTag() throws PageException	{
		if(showerror!=null) throw new AbortException(showerror);
		throw new railo.runtime.exp.Abort(type);
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		showerror=null;
        this.type=railo.runtime.exp.Abort.SCOPE_REQUEST;
	}
}