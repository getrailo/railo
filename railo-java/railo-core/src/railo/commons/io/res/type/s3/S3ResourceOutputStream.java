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
package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import railo.commons.io.TemporaryStream;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.http.httpclient3.HTTPEngine3Impl;

public final class S3ResourceOutputStream extends OutputStream {
	
	private final S3 s3;
	
	private final String contentType="application";
	private final String bucketName;
	private final String objectName;
	private final int acl;

	private TemporaryStream ts;
	
	public S3ResourceOutputStream(S3 s3,String bucketName,String objectName,int acl) {
		this.s3=s3;
		this.bucketName=bucketName;
		this.objectName=objectName;
		this.acl=acl;
		
		ts = new TemporaryStream();
	}
	
	@Override
	public void close() throws IOException {
		ts.close();
		
		//InputStream is = ts.getInputStream();
		try {
			s3.put(bucketName, objectName, acl, HTTPEngine3Impl.getTemporaryStreamEntity(ts,contentType));
		} 

		catch (SocketException se) {
			String msg = StringUtil.emptyIfNull(se.getMessage());
			if(StringUtil.indexOfIgnoreCase(msg, "Socket closed")==-1)
				throw se;
		}
		catch (Exception e) {
			throw ExceptionUtil.toIOException(e);
		}
	}

	@Override
	public void flush() throws IOException {
		ts.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		ts.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		ts.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		ts.write(b);
	}
}
