package railo.runtime.net.rpc;

import railo.runtime.exp.ExpressionException;

/**
 * 
 */
public final class RPCException extends ExpressionException {

    /**
     * @param message
     */
    public RPCException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param detail
     */
    public RPCException(String message, String detail) {
        super(message, detail);
    }


}