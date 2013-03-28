package railo.runtime.functions.video;


import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.video.VideoExecuter;
import railo.runtime.video.VideoInputImpl;
import railo.runtime.video.VideoUtilImpl;

public class IsVideoFile {

	public static boolean call(PageContext pc, String path) throws PageException {
		try {
			ConfigWeb config = pc.getConfig();
			VideoExecuter ve = VideoUtilImpl.createVideoExecuter(config);
			ve.info(config,new VideoInputImpl(Caster.toResource(pc,path, true)));
		} 
		catch (Exception e) {
			
			if(StringUtil.contains(e.getMessage(),"missing ffmpeg installation"))
				throw Caster.toPageException(e);
			return false;
		}
		return true;
	}
}
