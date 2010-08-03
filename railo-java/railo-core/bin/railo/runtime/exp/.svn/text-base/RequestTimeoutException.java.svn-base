package railo.runtime.exp;

import railo.runtime.PageContext;

public class RequestTimeoutException extends ApplicationException {

	private PageContext pc;
	private StackTraceElement[] stacktrace;

	public RequestTimeoutException(PageContext pc,String msg) {
		super(msg);
		this.pc=pc;
		this.stacktrace=pc.getThread().getStackTrace();
	}
	
	/**
	 * @see java.lang.Throwable#getStackTrace()
	 */
	public StackTraceElement[] getStackTrace() {
		return stacktrace;
	}

}
