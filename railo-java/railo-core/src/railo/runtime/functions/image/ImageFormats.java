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

import java.util.HashSet;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.img.ImageUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ImageFormats {

	private static Collection.Key DECODER=KeyImpl.getInstance("decoder");
	private static Collection.Key ENCODER=KeyImpl.getInstance("encoder");
	
	public static Struct call(PageContext pc) throws PageException {
		Struct sct=new StructImpl();
		sct.set(DECODER, toArray(ImageUtil.getReaderFormatNames()));
		sct.set(ENCODER, toArray(ImageUtil.getWriterFormatNames()));
		
		return sct;
	}

	private static Object toArray(String[] arr) {
		HashSet set=new HashSet();
		for(int i=0;i<arr.length;i++) {
			set.add(arr[i].toUpperCase());
		}
		
		return set.toArray(new String[set.size()]);
	}
}
