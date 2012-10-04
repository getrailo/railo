package railo.runtime.type.scope;

import railo.runtime.thread.ChildThread;

public interface Threads extends Scope {
	
	public ChildThread getChildThread();
}
