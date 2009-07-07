package railo.runtime.video;

import java.io.IOException;

import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.type.Struct;

public interface VideoExecuter {
	
	public void test(ConfigWeb config) throws IOException;
	public VideoInfo[] convert(ConfigWeb config, VideoInput[] inputs, VideoOutput output, VideoProfile quality) throws IOException;
	public VideoInfo info(ConfigWeb config, VideoInput input)  throws IOException;
	public void install(ConfigWeb config,Struct data)  throws IOException;
	public void uninstall(Config config)  throws IOException;
	
}
