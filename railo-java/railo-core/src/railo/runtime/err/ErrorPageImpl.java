package railo.runtime.err;

import railo.runtime.PageSource;
/**
 * 
 */
public final class ErrorPageImpl implements ErrorPage {
	
	/** Type of exception. Required if type = "exception" or "monitor". */
	private String exception="any";

	/** The relative path to the custom error page. */
	private PageSource template;

	/** The e-mail address of the administrator to notify of the error. The value
	** 	is available to your custom error page in the MailTo property of the error object. */
	private String mailto="";

	private short type;
	
	

	
	/**
     * @see railo.runtime.err.ErrorPage#setMailto(java.lang.String)
     */
	public void setMailto(String mailto) {
		this.mailto = mailto;
	}
	
	/**
     * @see railo.runtime.err.ErrorPage#setTemplate(railo.runtime.PageSource)
     */
	public void setTemplate(PageSource template) {
		this.template = template;
	}
	
	/**
     * @see railo.runtime.err.ErrorPage#setTypeAsString(java.lang.String)
     */
	public void setTypeAsString(String exception) {
		setException(exception);
	}	
	public void setException(String exception) {
		this.exception = exception;
	}	
	
	/**
     * @see railo.runtime.err.ErrorPage#getMailto()
     */
	public String getMailto() {
		return mailto;
	}
	/**
     * @see railo.runtime.err.ErrorPage#getTemplate()
     */
	public PageSource getTemplate() {
		return template;
	}
	
	/**
     * @see railo.runtime.err.ErrorPage#getTypeAsString()
     */
	public String getTypeAsString() {
		return getException();
	}
	public String getException() {
		return exception;
	}

	/**
	 * @see railo.runtime.err.ErrorPage#setType(short)
	 */
	public void setType(short type) {
		this.type=type;
	}

	/**
	 * @see railo.runtime.err.ErrorPage#getType()
	 */
	public short getType() {
		return type;
	}	
}