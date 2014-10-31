package railo.commons.net.http.httpclient4;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.HTTPResponseSupport;
import railo.commons.net.http.Header;

public class HTTPResponse4Impl extends HTTPResponseSupport implements HTTPResponse {

	HttpResponse rsp;
	HttpUriRequest req;
	private URL url;
	private HttpContext context;
	private PipedInputStream is = null;

	public HTTPResponse4Impl(URL url,HttpContext context, HttpUriRequest req,HttpResponse rsp) {
		this.url=url;
		this.context=context;
		this.req=req;
		this.rsp=rsp;
		if( HTTPEngine4Impl.CONNECTION_POOLING_ENABLED ) {
			// create thread to push content to a surrogate input stream, so that this class can appropriately close the underlying content inputstream and
			// release connections back to the connection pool
			HttpEntity e = rsp.getEntity();
			if(e != null) {
				is = new PipedInputStream();
				PipedOutputStream os = null;
				try {
					os = new PipedOutputStream( is );
					new Thread( new HTTPContentPiper( e.getContent(), os ) ).start();
				}
				catch (IOException e1) {
					// is will be null
				}
			}
		} 
	}
	
	@Override
	public String getContentAsString() throws IOException {
		return getContentAsString(null);
	}
	

	@Override
	public String getContentAsString(String charset) throws IOException {
		InputStream is=null;
		if(StringUtil.isEmpty(charset,true))charset=getCharset();
		try{
			return IOUtil.toString(is=getContentAsStream(), charset);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	@Override
	public InputStream getContentAsStream() throws IOException {
		HttpEntity e = rsp.getEntity();
		if(e==null) return  null;
		if( HTTPEngine4Impl.CONNECTION_POOLING_ENABLED ) {		
			return is;
		} 
		return e.getContent();
		
	}
	
	@Override
	public byte[] getContentAsByteArray() throws IOException {
		HttpEntity entity = rsp.getEntity();
		InputStream is=null;
		if(entity==null) return new byte[0];
		try{
			return IOUtil.toBytes(is=getContentAsStream());
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	@Override
	public Header getLastHeader(String name) {
		org.apache.http.Header header = rsp.getLastHeader(name);
		if(header!=null) return new HeaderWrap(header);
		return null;
	}
	
	@Override
	public Header getLastHeaderIgnoreCase(String name) {
		return getLastHeaderIgnoreCase(rsp, name);
	}
	public static Header getLastHeaderIgnoreCase(HttpResponse rsp,String name) {
		org.apache.http.Header header = rsp.getLastHeader(name);
		if(header!=null) return new HeaderWrap(header);
		
		org.apache.http.Header[] headers = rsp.getAllHeaders();
		for(int i=headers.length-1;i>=0;i--){
			if(name.equalsIgnoreCase(headers[i].getName())){
				return new HeaderWrap(headers[i]);
			}
		}
		return null;
	}

	@Override
	public URL getURL() {
		try {
			return req.getURI().toURL();
		} catch (MalformedURLException e) {
			return url;
		}
	}
	
	public URL getTargetURL() {
		URL start = getURL(); 	
		
		HttpUriRequest req = (HttpUriRequest) context.getAttribute(
	                ExecutionContext.HTTP_REQUEST);
		 	URI uri = req.getURI();
		 	String path=uri.getPath();
		 	String query=uri.getQuery();
		 	if(!StringUtil.isEmpty(query)) path+="?"+query;
		 	
		 	URL _url=start;
			try {
				_url = new URL(start.getProtocol(),start.getHost(),start.getPort(),path);
			} 
			catch (MalformedURLException e) {}
	        
	        
		return _url;
	}
	
	


	@Override
	public int getStatusCode() {
		return rsp.getStatusLine().getStatusCode();
	}
	
	@Override
	public String getStatusText() {
		return rsp.getStatusLine().getReasonPhrase();
	}

	@Override
	public String getProtocolVersion() {
		return rsp.getStatusLine().getProtocolVersion().toString();
	}

	@Override
	public String getStatusLine() {
		return rsp.getStatusLine().toString();
	}

	@Override
	public Header[] getAllHeaders() {
		org.apache.http.Header[] src = rsp.getAllHeaders();
		if(src==null) return new Header[0];
		Header[] trg=new Header[src.length];
		for(int i=0;i<src.length;i++){
			trg[i]=new HeaderWrap(src[i]);
		}
		return trg;
	}

	/**
	 * consumes an http inputstream and closes it
	 */
	private class HTTPContentPiper implements Runnable {
		
		private InputStream is;
		private PipedOutputStream os;
		
		public HTTPContentPiper( InputStream is, PipedOutputStream os ) {
			this.os = os;
			this.is = is;
		}
		@Override
		public void run() {
			try {
				IOUtil.copy( is, os,false,false );
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			IOUtil.closeEL( is );
			IOUtil.closeEL( os );
		}
		
	}
}
