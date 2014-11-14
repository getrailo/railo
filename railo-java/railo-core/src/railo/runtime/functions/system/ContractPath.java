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
 * Implements the CFML Function expandpath
 */
package railo.runtime.functions.system;


import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.ext.function.Function;

public final class ContractPath implements Function {
	public static String call(PageContext pc , String absPath) {
		return call(pc, absPath,false);
	}
	
	public static String call(PageContext pc , String absPath, boolean placeHolder) {
		Resource res = ResourceUtil.toResourceNotExisting(pc, absPath);
		if(!res.exists()) return absPath;
		
		if(placeHolder){
			String cp = SystemUtil.addPlaceHolder(res, null);
			if(!StringUtil.isEmpty(cp))return cp;
		}
		
		//Config config=pc.getConfig();
		PageSource ps = pc.toPageSource(res,null);
		if(ps==null) return absPath;
		
		String relPath = ps.getRealpath();
		relPath=relPath.replace('\\', '/');
		if(StringUtil.endsWith(relPath,'/'))relPath=relPath.substring(0,relPath.length()-1);
		
		String mapping=ps.getMapping().getVirtual();
		mapping=mapping.replace('\\', '/');
		if(StringUtil.endsWith(mapping,'/'))mapping=mapping.substring(0,mapping.length()-1);
		
		return mapping+relPath;
	}
}