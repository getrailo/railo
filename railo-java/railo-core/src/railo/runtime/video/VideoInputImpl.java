package railo.runtime.video;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.util.ListUtil;

public class VideoInputImpl implements VideoInput {

	private Resource resource;
	private String args="";
	private String path;

	/**
	 * Constructor of the class
	 * @param resource
	 */
	public VideoInputImpl(Resource resource) {
		this.resource=resource;
	}

	/**
	 * @see railo.runtime.video.VideoInput#getResource()
	 */
	public Resource getResource() {
		return resource;
	}
	
	/**
	 * @see railo.runtime.video.VideoInput#setCommand(java.lang.String, java.util.List)
	 */
	public void setCommand(String path,java.util.List args) {
		this.path=path;
		try {
			addArgs(ListUtil.listToList(args, " "));
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	
	public void setCommand(String path,String[] args) {
		this.path=path;
		addArgs(ListUtil.arrayToList(args, " "));
	}
	
	/**
	 * @see railo.runtime.video.VideoInput#getCommandAsString()
	 */
	public String getCommandAsString() {
		return path+" "+args;
	}
	
	private void addArgs(String args) {
		if(StringUtil.isEmpty(this.args,true))
			this.args=args;
		else 
			this.args+="; "+args;
		
	}
}
