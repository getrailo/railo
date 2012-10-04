package railo.runtime.engine;

import java.io.IOException;

import railo.runtime.PageContext;

public interface ThreadQueue {
	
	
	public void enter(PageContext pc) throws IOException;
	
	public void exit(PageContext pc);
	
	public void clear();
	
	public int size();	
}
