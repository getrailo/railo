package railo.commons.lock;

import railo.runtime.exp.NativeException;

public class LockInterruptedException extends NativeException {

	private static final long serialVersionUID = -3450411938137674552L;
	private InterruptedException e;

	public LockInterruptedException(InterruptedException e) {
		super(e);
		this.e=e;
	}

	public InterruptedException getLockInterruptedException() {
		return e;
	}

}
