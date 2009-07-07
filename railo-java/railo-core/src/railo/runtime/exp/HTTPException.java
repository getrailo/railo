package railo.runtime.exp;

import railo.runtime.PageContext;
import railo.runtime.type.Struct;

/**
 * Exception class for the HTTP Handling
 */
public final class HTTPException extends ApplicationException {

    private int statusCode;

    /**
     * constructor of the class
     * @param message
     * @param statusCode
     */
    public HTTPException(String message, int statusCode) {
        super(message);
        this.statusCode=statusCode;
    }
    
    
    /**
     * @return Returns the statusCode.
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     *
     * @see railo.runtime.exp.PageExceptionImpl#getCatchBlock(railo.runtime.PageContext)
     */
    public Struct getCatchBlock(PageContext pc) {
        Struct sct = super.getCatchBlock(pc);
        sct.setEL("statusCode",statusCode+"");
        return sct;
    }
}