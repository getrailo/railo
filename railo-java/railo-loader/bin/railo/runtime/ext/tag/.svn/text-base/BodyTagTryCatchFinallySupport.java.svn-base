package railo.runtime.ext.tag;

import javax.servlet.jsp.tagext.TryCatchFinally;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.exp.PageServletException;
import railo.runtime.util.Excepton;


/**
 * extends Body Support Tag eith TryCatchFinally Functionality
 */
public abstract class BodyTagTryCatchFinallySupport extends BodyTagSupport implements TryCatchFinally {

    /**
     * @see javax.servlet.jsp.tagext.TryCatchFinally#doCatch(java.lang.Throwable)
     */
    public void doCatch(Throwable t) throws Throwable {
        if(t instanceof PageServletException) {
            PageServletException pse=(PageServletException)t;
            t=pse.getPageException();
        }
        if(bodyContent!=null) {
            Excepton util = CFMLEngineFactory.getInstance().getExceptionUtil();
            if(util.isOfType(Excepton.TYPE_ABORT,t)) {
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