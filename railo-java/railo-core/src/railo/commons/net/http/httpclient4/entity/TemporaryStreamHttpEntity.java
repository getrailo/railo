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
package railo.commons.net.http.httpclient4.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

import railo.commons.io.IOUtil;
import railo.commons.io.TemporaryStream;


public class TemporaryStreamHttpEntity extends AbstractHttpEntity implements Entity4 {

	private final TemporaryStream ts;
	private String ct;

	public TemporaryStreamHttpEntity(TemporaryStream ts,String contentType) {
		this.ts=ts;
		setContentType(contentType);
		this.ct=contentType;
	}
	
	@Override
	public long getContentLength() {
		return ts.length();
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		IOUtil.copy(ts.getInputStream(), os,true,false);
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return ts.getInputStream();
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public long contentLength() {
		return getContentLength();
	}

	@Override
	public String contentType() {
		return ct;
	}
}