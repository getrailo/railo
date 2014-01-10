package railo.runtime.tag;

import railo.commons.io.SystemUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;

/**
* Pauses the execution of the page for a given interval
*
*
*
**/
public final class Sleep extends TagImpl {

	/** Expressed in milli seconds. */
	private long time;

	@Override
	public void release()	{
		super.release();
		time=0;
	}

	/** set the value interval
	*  Expressed in milli seconds.
	* @param time value to set
	**/
	public void setTime(double time)	{
		this.time=(long)time;
	}


	@Override
	public int doStartTag() throws PageException	{
		if(time>=0) {
			SystemUtil.sleep(time);
		}
		else throw new ExpressionException("attribute interval must be greater or equal to 0, now ["+(time)+"]");
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}