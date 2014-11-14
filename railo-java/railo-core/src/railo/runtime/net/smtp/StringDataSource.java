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
package railo.runtime.net.smtp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public final class StringDataSource implements DataSource {

	private String text;
	private String ct;
	private String charset;

	public StringDataSource(String text, String ct, String charset) {
		this.text=text;
		this.ct=ct;
		this.charset=charset;
	}

	@Override
	public String getContentType() {
		return ct;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(text.getBytes(charset));
	}

	@Override
	public String getName() {
		return "StringDataSource";
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("no access to write");
	}

}
