package railo.runtime.exp;

import railo.runtime.PageContext;

public class RequestTimeoutException extends Abort implements Stop {

	private PageContext pc;
	private StackTraceElement[] stacktrace;

	public RequestTimeoutException(PageContext pc,String msg) {
		super(SCOPE_REQUEST,msg);
		this.pc=pc;
		this.stacktrace=pc!=null?pc.getThread().getStackTrace():null;
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return stacktrace;
	}

}
