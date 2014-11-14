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
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public class ImageDrawLines {

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords) throws PageException {
		return call(pc, name, xcoords, ycoords, false, false);
	}

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords, boolean isPolygon) throws PageException {
		return call(pc, name, xcoords, ycoords, isPolygon, false);
	}

	public static String call(PageContext pc, Object name, Array xcoords, Array ycoords, boolean isPolygon, boolean filled) throws PageException {
		//if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(pc,name);
		
		if(xcoords.size()!=ycoords.size())
			throw new ExpressionException("xcoords and ycoords has not the same size");
		img.drawLines(toIntArray(xcoords), toIntArray(ycoords), isPolygon, filled);
		return null;
	}

	private static int[] toIntArray(Array arr) throws PageException {
		int[] iarr=new int[arr.size()];
		for(int i=0;i<iarr.length;i++) {
			iarr[i]=Caster.toIntValue(arr.getE(i+1));
		}
		return iarr;
	}
	
}
