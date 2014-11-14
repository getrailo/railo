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
package railo.commons.net.http;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.ContentType;
import railo.commons.io.res.ContentTypeImpl;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;

public abstract class HTTPResponseSupport implements HTTPResponse {
	


	@Override
	public final long getContentLength() throws IOException {
		Header ct = getLastHeaderIgnoreCase("Content-Length");
		if(ct!=null) return Caster.toLongValue(ct.getValue(),-1);
		
		InputStream is=null;
		long length=0;
		try{
			is=getContentAsStream();

            if ( is == null )
                return 0;

			byte[] buffer = new byte[1024];
		    int len;
		    while((len = is.read(buffer)) !=-1){
		      length+=len;
		    }
			return length;
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	@Override
	public final ContentType getContentType() {
		Header header = getLastHeaderIgnoreCase("Content-Type");
		if(header==null) return null;
		
		String[] mimeCharset = HTTPUtil.splitMimeTypeAndCharset(header.getValue(), null);
		if(mimeCharset==null) return null;
		
		String[] typeSub = HTTPUtil.splitTypeAndSubType(mimeCharset[0]);
		return new ContentTypeImpl(typeSub[0],typeSub[1],mimeCharset[1]);
	}

	@Override
	public final String getCharset() {
		ContentType ct = getContentType();
		String charset=null;
		if(ct!=null)charset=ct.getCharset();
		if(!StringUtil.isEmpty(charset)) return charset;
		
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) return ((PageContextImpl)pc).getWebCharset().name();
		return "ISO-8859-1";
	}

}
