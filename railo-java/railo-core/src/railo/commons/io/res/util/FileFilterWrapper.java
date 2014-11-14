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
package railo.commons.io.res.util;

import java.io.File;
import java.io.FileFilter;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResourceFilter;

public final class FileFilterWrapper implements FileResourceFilter {

	private final FileFilter filter;

	public FileFilterWrapper(FileFilter fileFilter) {
		this.filter=fileFilter;
	}
	public boolean accept(Resource res) {
		if(res instanceof File) return accept(((File)res));
		return accept(FileWrapper.toFile(res));
	}

	@Override
	public boolean accept(File pathname) {
		return filter.accept(pathname);
	}

}
