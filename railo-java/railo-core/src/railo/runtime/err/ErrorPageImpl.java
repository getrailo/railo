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
	
	

	
	@Override
	public void setMailto(String mailto) {
		this.mailto = mailto;
	}
	
	@Override
	public void setTemplate(PageSource template) {
		this.template = template;
	}
	
	@Override
	public void setTypeAsString(String exception) {
		setException(exception);
	}	
	
	@Override
	public void setException(String exception) {
		this.exception = exception;
	}	
	
	@Override
	public String getMailto() {
		return mailto;
	}
	@Override
	public PageSource getTemplate() {
		return template;
	}
	
	@Override
	public String getTypeAsString() {
		return getException();
	}
	public String getException() {
		return exception;
	}

	@Override
	public void setType(short type) {
		this.type=type;
	}

	@Override
	public short getType() {
		return type;
	}	
}