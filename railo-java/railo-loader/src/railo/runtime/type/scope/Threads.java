package railo.runtime.type.scope;

import railo.runtime.thread.ChildThread;
import railo.runtime.type.Scope;

public interface Threads extends Scope {
	
	public ChildThread getChildThread();
}
