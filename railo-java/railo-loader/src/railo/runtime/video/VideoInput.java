package railo.runtime.video;

import railo.commons.io.res.Resource;

// TODO doc
public interface VideoInput {

	/**
	 * @return the res
	 */
	public Resource getResource();
	
	public void setCommand(String path,java.util.List args);
	
	public void setCommand(String path,String[] args);
	
	public String getCommandAsString();
}
