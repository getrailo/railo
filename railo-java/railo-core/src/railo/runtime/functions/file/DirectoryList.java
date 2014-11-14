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
package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceAndResourceNameFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.UDFFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.tag.Directory;

public class DirectoryList {
	
	public static Object call(PageContext pc , String path) throws PageException {
		return call(pc, path, false, null, null, null);
	}
	
	public static Object call(PageContext pc , String path,boolean recurse) throws PageException {
		return call(pc, path, recurse, null, null, null);
	}
	
	public static Object call(PageContext pc , String path,boolean recurse,String strListInfo) throws PageException {
		return call(pc, path, recurse, strListInfo, null, null);
	}
	
	public static Object call(PageContext pc , String path,boolean recurse,String strListInfo,Object oFilter) throws PageException {
		return call(pc, path, recurse, strListInfo, oFilter, null);
	}
	
	public static Object call(PageContext pc , String path,boolean recurse,String strListInfo,Object oFilter, String sort) throws PageException {
		Resource dir=ResourceUtil.toResourceNotExisting(pc, path);
		ResourceAndResourceNameFilter filter = UDFFilter.createResourceAndResourceNameFilter(oFilter);
		
		int listInfo=Directory.LIST_INFO_ARRAY_PATH;
		if(!StringUtil.isEmpty(strListInfo,true)){
			strListInfo=strListInfo.trim().toLowerCase();
			if("name".equalsIgnoreCase(strListInfo)){
				listInfo=Directory.LIST_INFO_ARRAY_NAME;
			}
			else if("query".equalsIgnoreCase(strListInfo)){
				listInfo=Directory.LIST_INFO_QUERY_ALL;
			}
		}
		
		return Directory.actionList(pc, dir, null, Directory.TYPE_ALL, filter, filter, listInfo, recurse, sort);

		
		//public static Object actionList(PageContext pageContext,Resource directory, String serverPassword, int type,ResourceFilter filter,ResourceAndResourceNameFilter nameFilter, 
		//		int listInfo,boolean recurse,String sort) throws PageException {
	    
	}
}
