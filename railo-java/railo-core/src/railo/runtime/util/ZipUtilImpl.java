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
package railo.runtime.util;

import java.io.IOException;

import railo.commons.io.res.Resource;

public class ZipUtilImpl implements ZipUtil {

	private static ZipUtil instance=new ZipUtilImpl();
	private ZipUtilImpl(){}
	public static ZipUtil getInstance(){
		return instance;
	}
		
	public void unzip(Resource zip, Resource dir) throws IOException {
		railo.commons.io.compress.ZipUtil.unzip(zip, dir);
	}

}
