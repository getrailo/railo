/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
