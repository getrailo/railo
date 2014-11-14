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
/**
 * Implements the CFML Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSourceImpl;
import railo.runtime.PageSourcePool;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class PagePoolList implements Function {

	private static final long serialVersionUID = 7743072823224800862L;

	public static Array call(PageContext pc) throws PageException {
		ArrayImpl arr = new ArrayImpl();
		fill(arr,ConfigWebUtil.getAllMappings(pc));
		return arr;
	}

	private static void fill(Array arr, Mapping[] mappings) throws PageException {
		if(mappings==null) return;
		MappingImpl mapping;
		for(int i=0;i<mappings.length;i++)	{
			mapping=(MappingImpl) mappings[i];
			toArray(arr,mapping.getPageSourcePool());
		}		
	}

	private static Array toArray(Array arr, PageSourcePool psp) throws PageException {
		Object[] keys = psp.keys();
		
		PageSourceImpl ps;
		for(int y=0;y<keys.length;y++)	{
			ps = (PageSourceImpl) psp.getPageSource(keys[y], false);
			if(ps.isLoad())
				arr.append(ps.getDisplayPath());
		}
		return arr;
	}

	public static String removeStartingSlash(String virtual) {
		virtual=virtual.trim();
		if(StringUtil.startsWith(virtual, '/'))virtual=virtual.substring(1);
		if(StringUtil.isEmpty(virtual)) return "root";
		return virtual;
	}
}