package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.dt.DateTime;

// MUST change behavor of mltiple headers now is a array, it das so?

/**
* Lets you execute HTTP POST and GET operations on files. Using cfhttp, you can execute standard 
*   GET operations and create a query object from a text file. POST operations lets you upload MIME file 
*   types to a server, or post cookie, formfield, URL, file, or CGI variables directly to a specified server.
*
*
*
* 
**/
public final class Surveillance extends TagImpl {
	
	private static final long FUTURE=50000000000000L;
	private static final int ACTION_MEMORY = 0;
	
	private int action;
	private long from;
	private long to=FUTURE;
	private int slotSize;
	private String returnVariable="cfsurveillance";
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		action=ACTION_MEMORY;
		from=0;
		to=FUTURE;
		returnVariable="cfsurveillance";
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(String strAction) throws ApplicationException {
		String lcAction = strAction.trim().toLowerCase();
		
		if("memory".equals(lcAction)) 			this.action=ACTION_MEMORY;
		else 
			throw new ApplicationException("invalid value ["+strAction+"] for attribute action","values for attribute action are:memory");
	}

	public void setFrom(DateTime from) {
		this.from=from.getTime();
	}
	public void setTo(DateTime to) {
		this.to=to.getTime();
	}
	public void setSlotsize(double slotSize) {
		this.slotSize=(int) slotSize;
	}

	public void setReturnvariable(String returnVariable) {
		if(StringUtil.isEmpty(returnVariable,true)) return;
		this.returnVariable = returnVariable;
	}


	/**
	 * @throws PageException 
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws PageException	{
		switch(action) {
			case ACTION_MEMORY:	
				doMemory();
			break;
			
		}
		return SKIP_BODY;
	}

	private void doMemory() throws PageException {
		// MUST do better
		ConfigServerImpl cs=(ConfigServerImpl) pageContext.getConfig().getConfigServer();
		pageContext.setVariable(returnVariable, cs.getMemoryMonitor().getData(from, to, slotSize*1000));
	}
	
}