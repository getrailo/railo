package railo.runtime.exp;

import railo.runtime.PageContext;

public class RequestTimeoutException extends Abort {

	private PageContext pc;
	private StackTraceElement[] stacktrace;

	public RequestTimeoutException(PageContext pc,String msg) {
		super(SCOPE_REQUEST,msg);
		this.pc=pc;
		this.stacktrace=pc.getThread().getStackTrace();
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return stacktrace;
	}

}
