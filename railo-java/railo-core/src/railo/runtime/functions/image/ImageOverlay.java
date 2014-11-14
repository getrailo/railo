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

public class ImageOverlay {
	
	public static String call(PageContext pc, Object src1, Object src2) throws PageException {
		//if(src1 instanceof String) src1=pc.getVariable(Caster.toString(src1));
		//if(src2 instanceof String) src2=pc.getVariable(Caster.toString(src2));
		
		Image.toImage(pc,src1).overlay(Image.createImage(pc, src2,true,false,true,null));
		return null;
	}
}
