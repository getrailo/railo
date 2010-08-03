package railo.runtime.ext.tag;

import javax.servlet.jsp.tagext.TryCatchFinally;

import railo.runtime.exp.AbortException;
import railo.runtime.exp.PageServletException;


/**
 * extends Body Support Tag eith TryCatchFinally Functionality
 */
public abstract class BodyTagTryCatchFinallyImpl extends BodyTagImpl implements TryCatchFinally{

	/**
	 * @see javax.servlet.jsp.tagext.TryCatchFinally#doCatch(java.lang.Throwable)
	 */
	public void doCatch(Throwable t) throws Throwable {
		if(t instanceof PageServletException) {
		    PageServletException pse=(PageServletException)t;
		    t=pse.getPageException();
		}
	    if(bodyContent!=null) {
			if(t instanceof AbortException){
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			}
			bodyContent.clearBuffer();
		}
		throw t;
	}

	/**
	 * @see javax.servlet.jsp.tagext.TryCatchFinally#doFinally()
	 */
	public void doFinally() {
		
	}
	
}