package railo.runtime.video;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface VideoUtil {

	public VideoProfile createVideoProfile(); 
	public VideoOutput createVideoOutput(Resource output); 
	public VideoInput createVideoInput(Resource input); 	

	public long toBytes(String byt) throws PageException;
	
	public long toHerz(String byt) throws PageException;
	
	public long toMillis(String time) throws PageException;
	
	public int[] calculateDimension(PageContext pc,VideoInput[] sources,int width, String strWidth,int height, String strHeight) throws PageException;
}
