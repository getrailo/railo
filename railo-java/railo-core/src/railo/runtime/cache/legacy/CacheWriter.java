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
package railo.runtime.cache.legacy;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import railo.commons.io.ForkWriter;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class CacheWriter extends ForkWriter {

	private Writer out;
	private Resource cacheFile;

	public CacheWriter(Writer out, Resource cacheFile) throws IOException {
		super(out, IOUtil.getWriter(cacheFile, (Charset)null));
		this.out=out;
		this.cacheFile=cacheFile;
	}

	/**
	 * @return the cacheFile
	 */
	public Resource getCacheFile() {
		return cacheFile;
	}

	/**
	 * @param cacheFile the cacheFile to set
	 */
	public void setCacheFile(Resource cacheFile) {
		this.cacheFile = cacheFile;
	}

	/**
	 * @return the out
	 */
	public Writer getOut() {
		return out;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(Writer out) {
		this.out = out;
	}

}
