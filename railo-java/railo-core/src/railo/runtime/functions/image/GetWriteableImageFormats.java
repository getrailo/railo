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
package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.img.ImageUtil;

public class GetWriteableImageFormats {

	public static String call(PageContext pc) {
		return GetReadableImageFormats.format(add(ImageUtil.getWriterFormatNames(),"gif"));
	}

	private static String[] add(String[] formats, String value) {
		String[] rtn=new String[formats.length+1];
		for(int i=0;i<formats.length;i++) {
			rtn[i]=formats[i];
		}
		rtn[formats.length]=value;
		return rtn;
	}
}
