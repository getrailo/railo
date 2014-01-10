package railo.runtime.tag;

import java.io.IOException;

import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

/**
* Flushes currently available data to the client.
*
*
*
**/
public final class Flush extends TagImpl {

	/** Flush the output each time at least the specified number of bytes become available. HTML 
	** 		headers, and any data that is already available when you make this call, are not included in 
	** 		the count. */
	private double interval=-1;

	@Override
	public void release()	{
		super.release();
		interval=-1;
	}

	/** set the value interval
	*  Flush the output each time at least the specified number of bytes become available. HTML 
	* 		headers, and any data that is already available when you make this call, are not included in 
	* 		the count.
	* @param interval value to set
	**/
	public void setInterval(double interval)	{
		this.interval=interval;
	}


	@Override
	public int doStartTag() throws PageException	{
        try {
			if(interval==-1)((PageContextImpl)pageContext).getRootOut().flush();
			else ((PageContextImpl)pageContext).getRootOut().setBufferConfig((int)interval,true);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}