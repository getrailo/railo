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
package railo.commons.activation;

// Imports
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * File Data Source.
 */
public final class ResourceDataSource implements DataSource {

	/**
	 * File source.
	 */
	private	final Resource _file;

	/**
	 * Constructor of the class
	 * @param res source
	 */
	public ResourceDataSource(Resource res) {
		_file = res;
	}

	/**
	 * Get name.
	 * @returns Name
	 */
	public String getName() {
		return _file.getName();
	} 

	/**
	 * Get Resource.
	 * @returns Resource
	 */
	public Resource getResource() {
		return _file;
	}

	/**
	 * Get input stream.
	 * @returns Input stream
	 * @throws IOException IO exception occurred
	 */
	public InputStream getInputStream() throws IOException {
		return IOUtil.toBufferedInputStream(_file.getInputStream());
	} 

	/**
	 * Get content type.
	 * @returns Content type
	 */
	public String getContentType() {
		InputStream is=null;
		try {
			return IOUtil.getMimeType(is=_file.getInputStream(), "application/unknow");
		} catch (IOException e) {
			return "application/unknow";
		}
		finally {
			IOUtil.closeEL(is);
		}
		
	}

	/**
	 * Get output stream.
	 * @returns Output stream
	 * @throws IOException IO exception occurred
	 */
	public OutputStream getOutputStream() throws IOException {
		if (!_file.isWriteable()) {
			throw new IOException("Cannot write");
		}
		return IOUtil.toBufferedOutputStream(_file.getOutputStream());
	}
}
