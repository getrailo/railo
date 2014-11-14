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
package railo.commons.io.compress;

import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import railo.commons.cli.Command;
import railo.commons.io.CompressUtil;
import railo.commons.io.res.Resource;

public final class ZipUtil {

	public static void unzip(Resource zip, Resource dir) throws IOException {
		if(zip.length()>0 && (dir.exists() || dir.mkdirs())) {
			if("Mac OS X".equalsIgnoreCase(System.getProperty("os.name"))) {
				try {
					//Command.execute("unzip "+zip+" -d "+dir);
					Command.execute("unzip",new String[]{"-o",zip.getAbsolutePath(),"-d",dir.getAbsolutePath()});
				} 
				catch (InterruptedException e) {}
				return;
			}
			CompressUtil.extract(CompressUtil.FORMAT_ZIP, zip, dir);
		}
	}

	public static void close(ZipOutputStream zos) {
		if(zos==null) return;
		try {
			zos.close();
		} 
		catch (IOException e) {}
	}

	public static void close(ZipFile file) {
		if(file==null) return;
		try {
			file.close();
		} 
		catch (IOException e) {}
	}
}
