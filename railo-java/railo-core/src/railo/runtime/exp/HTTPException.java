package railo.runtime.exp;

import java.net.URL;

import railo.runtime.config.Config;
import railo.runtime.type.util.KeyConstants;

/**
 * Exception class for the HTTP Handling
 */
public final class HTTPException extends ApplicationException {

    private int statusCode;
    private String statusText;
	private URL url;

    
    /**
     * Constructor of the class
     * @param message
     * @param detail
     * @param statusCode
     */
    public HTTPException(String message, String detail, int statusCode,String statusText,URL url) {
        super(message,detail);
        this.statusCode=statusCode;
        this.statusText=statusText;
        this.url=url;

        setAdditional(KeyConstants._statuscode, new Double(statusCode));
		setAdditional(KeyConstants._statustext, statusText);
		if(url!=null)setAdditional(KeyConstants._url, url.toExternalForm());
    }

	/**
     * @return Returns the statusCode.
     */
    public int getStatusCode() {
        return statusCode;
    }

	/**
     * @return Returns the status text.
     */
    public String getStatusText() {
        return statusText;
    }
    
    public URL getURL(){
    	return url;
    }

    @Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock sct = super.getCatchBlock(config);
        sct.setEL("statusCode",statusCode+"");
        sct.setEL("statusText",statusText);
        if(url!=null)sct.setEL("url",url.toExternalForm());
        return sct;
    }
}