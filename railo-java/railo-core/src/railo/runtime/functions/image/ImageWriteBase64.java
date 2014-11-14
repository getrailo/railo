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

import java.io.IOException;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageWriteBase64 {
	
	public static String call(PageContext pc, Object name, String destination, String format) throws PageException {
		return call(pc, name, destination, format,false);
	}
	
	public static String call(PageContext pc, Object name, String destination, String format, boolean inHTMLFormat) throws PageException {
		//if(name instanceof String)name=pc.getVariable(Caster.toString(name));
		Image image=Image.toImage(pc,name);
		try {
			return image.writeBase64(ResourceUtil.toResourceNotExisting(pc, destination), format, inHTMLFormat);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		
	}
}
