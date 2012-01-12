package railo.runtime.exp;

import org.apache.commons.httpclient.HttpMethod;

import railo.commons.net.HTTPUtil;
import railo.runtime.config.Config;

/**
 * Exception class for the HTTP Handling
 */
public final class HTTPException extends ApplicationException {

    private int statusCode;

    
    /**
     * Constructor of the class
     * @param message
     * @param detail
     * @param statusCode
     */
    public HTTPException(String message, String detail, int statusCode) {
        super(message,detail);
        this.statusCode=statusCode;
    }
    

    
    /**
     * Constructor of the class
     * @param httpMethod
     */
    public HTTPException(HttpMethod httpMethod) {
		super(httpMethod.getStatusCode()+" "+httpMethod.getStatusText());
		setAdditional(httpMethod);
	}
    

	private void setAdditional(HttpMethod httpMethod) {
		this.statusCode=httpMethod.getStatusCode();
		setAdditional("statuscode", new Double(httpMethod.getStatusCode()));
		setAdditional("url", HTTPUtil.toURL(httpMethod).toExternalForm());
	}


	/**
     * @return Returns the statusCode.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
	 * @see railo.runtime.exp.PageExceptionImpl#getCatchBlock(railo.runtime.config.Config)
	 */
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock sct = super.getCatchBlock(config);
        sct.setEL("statusCode",statusCode+"");
        return sct;
    }
}