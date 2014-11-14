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
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;

public class ImageSetAntialiasing {

	public static String call(PageContext pc, Object name) throws PageException {
		return call(pc, name,"on");
	}
	public static String call(PageContext pc, Object name, String strAntialias) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		strAntialias=strAntialias.trim().toLowerCase();
		boolean antialias;
		if("on".equals(strAntialias))antialias=true;
		else if("off".equals(strAntialias))antialias=false;
		else antialias=Caster.toBooleanValue(strAntialias);
		
		img.setAntiAliasing(antialias);
		return null;
	}
}
