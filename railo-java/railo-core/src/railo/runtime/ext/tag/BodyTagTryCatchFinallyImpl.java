package railo.runtime.ext.tag;

import javax.servlet.jsp.tagext.TryCatchFinally;

import railo.runtime.exp.AbortException;
import railo.runtime.exp.PageServletException;


/**
 * extends Body Support Tag eith TryCatchFinally Functionality
 */
public abstract class BodyTagTryCatchFinallyImpl extends BodyTagImpl implements TryCatchFinally{

	@Override
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

	@Override
	public void doFinally() {
		
	}
	
}