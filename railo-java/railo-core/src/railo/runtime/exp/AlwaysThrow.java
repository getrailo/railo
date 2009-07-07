package railo.runtime.exp;

public class AlwaysThrow extends PageRuntimeException {

	public AlwaysThrow(PageException pe) {
		super(pe);
	}
	public AlwaysThrow(String message) {
		super(message);
	}
	public AlwaysThrow(String message, String detail) {
		super(message, detail);
	}



}
