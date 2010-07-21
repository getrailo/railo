package railo.runtime.video;

import java.io.IOException;

import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.type.Struct;

public class VideoExecuterNotSupported implements VideoExecuter {

	/**
	 * @see railo.runtime.video.VideoExecuter#convertRaw(railo.runtime.config.ConfigWeb, railo.runtime.video.VideoInput[], railo.runtime.video.VideoOutput, railo.runtime.video.VideoProfile)
	 */
	public VideoInfo[] convert(ConfigWeb config, VideoInput[] inputs, VideoOutput output,VideoProfile quality) throws IOException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.video.VideoExecuter#infoRaw(railo.runtime.config.ConfigWeb, railo.runtime.video.VideoInput)
	 */
	public VideoInfo info(ConfigWeb config, VideoInput input) throws IOException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.video.VideoExecuter#test(railo.runtime.config.ConfigWeb)
	 */
	public void test(ConfigWeb config) throws IOException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.video.VideoExecuter#uninstall(railo.runtime.config.Config)
	 */
	public void uninstall(Config config) throws IOException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.video.VideoExecuter#install(railo.runtime.config.ConfigWeb, railo.runtime.type.Struct)
	 */
	public void install(ConfigWeb config,Struct data) throws IOException {
		throw notSupported();
	}

	private VideoException notSupported() {
		return new VideoException("The video components are not installed, please go to the Railo Server Administrator in order to install the video extension");
	}

}
