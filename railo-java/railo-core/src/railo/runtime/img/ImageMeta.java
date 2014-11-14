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
package railo.runtime.img;


import java.io.PrintWriter;

import railo.commons.io.res.Resource;
import railo.commons.lang.SystemOut;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Struct;

public class ImageMeta {

	/**
	 */
	/**
	 * adds information about a image to the given struct
	 * @param format
	 * @param res
	 * @param info
	 */
	public static void addInfo(String format, Resource res, Struct info)  {
		try{
			ImageMetaDrew.test();
		}
		catch(Throwable t) {
			PrintWriter pw = ThreadLocalPageContext.getConfig().getErrWriter();
			SystemOut.printDate(pw, "cannot load addional pic info, library metadata-extractor.jar is missed"); 
		}
		try{
			ImageMetaDrew.addInfo(format, res, info);
		}
		catch(Throwable t) {}
	}

	

}
