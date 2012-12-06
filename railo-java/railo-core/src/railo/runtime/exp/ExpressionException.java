
package railo.runtime.exp;

import railo.runtime.config.Config;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

/**
 *
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ExpressionException extends PageExceptionImpl {

	private static final Collection.Key ERR_NUMBER = KeyImpl.intern("ErrNumber");
	/**
	 * Class Constuctor
	 * @param message error message
	 */
	public ExpressionException(String message) {
		super(message,"expression"); 
	}

	/**
	 * Class Constuctor
	 * @param message error message
	 * @param detail detailed error message
	 */
	public ExpressionException(String message, String detail) {
		super(message,"expression");
		setDetail(detail);
	}
	
	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock sct=super.getCatchBlock(config);
		sct.setEL(ERR_NUMBER,new Double(0));
		return sct;
	}
    /**
     * @param e
     * @return pageException
     */
    public static ExpressionException newInstance(Exception e) {
        if(e instanceof ExpressionException) return (ExpressionException) e;
        else if(e instanceof PageException) {
            PageException pe=(PageException)e;
	        ExpressionException ee = new ExpressionException(pe.getMessage());
	        ee.detail=pe.getDetail();
	        ee.setStackTrace(pe.getStackTrace());
	        return ee;
        }
        else  {
	        ExpressionException ee = new ExpressionException(e.getMessage());
	        ee.setStackTrace(e.getStackTrace());
	        return ee;
        }
    }
}