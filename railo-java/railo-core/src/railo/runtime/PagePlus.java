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
package railo.runtime;

import java.io.IOException;
import java.io.Reader;

import railo.commons.io.CharsetUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public abstract class PagePlus extends Page {
	
	private Resource staticTextLocation;

	public Object udfDefaultValue(PageContext pc, int functionIndex, int argumentIndex) {
		return udfDefaultValue(pc, functionIndex, argumentIndex, null);
	}
	
	public Object udfDefaultValue(PageContext pc, int functionIndex, int argumentIndex, Object defaultValue) {
		return null;
	}
	
	public String str(PageContext pc, int off, int len) throws IOException{
		if(staticTextLocation==null) {
			PageSource ps = getPageSource();
			Mapping m = ps.getMapping();
			staticTextLocation=m.getClassRootDirectory();
			staticTextLocation=staticTextLocation.getRealResource(ps.getJavaName()+".txt");
		}
		
		Reader reader = IOUtil.getReader(staticTextLocation, CharsetUtil.UTF8);
		char[] carr=new char[len];
		try {
			if(off>0)reader.skip(off);
			reader.read(carr);
		}
		finally {
			IOUtil.closeEL(reader);
		}
		
		//print.e(carr);
		return new String(carr);
	}
}
