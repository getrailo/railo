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
