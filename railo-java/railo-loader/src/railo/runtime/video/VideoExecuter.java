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

public interface VideoExecuter {
	
	public void test(ConfigWeb config) throws IOException;
	public VideoInfo[] convert(ConfigWeb config, VideoInput[] inputs, VideoOutput output, VideoProfile quality) throws IOException;
	public VideoInfo info(ConfigWeb config, VideoInput input)  throws IOException;
	public void install(ConfigWeb config,Struct data)  throws IOException;
	public void uninstall(Config config)  throws IOException;
	
}
