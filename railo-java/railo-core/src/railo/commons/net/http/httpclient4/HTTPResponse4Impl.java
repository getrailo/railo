package railo.commons.net.http.httpclient4;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.HTTPResponseSupport;
import railo.commons.net.http.Header;

public class HTTPResponse4Impl extends HTTPResponseSupport implements HTTPResponse {

	HttpResponse rsp;
	HttpUriRequest req;
	private URL url; 

	public HTTPResponse4Impl(URL url,HttpUriRequest req,HttpResponse rsp) {
		this.url=url;
		this.req=req;
		this.rsp=rsp;
	}
	
	@Override
	public String getContentAsString() throws IOException {
		return getContentAsString(null);
	}
	

	@Override
	public String getContentAsString(String charset) throws IOException {
		HttpEntity entity = rsp.getEntity();
		InputStream is=null;
		if(StringUtil.isEmpty(charset,true))charset=getCharset();
		try{
			return IOUtil.toString(is=entity.getContent(), charset);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	@Override
	public InputStream getContentAsStream() throws IOException {
		return rsp.getEntity().getContent();
	}
	
	@Override
	public byte[] getContentAsByteArray() throws IOException {
		HttpEntity entity = rsp.getEntity();
		InputStream is=null;
		try{
			return IOUtil.toBytes(is=entity.getContent());
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	/*public ContentType getContentType() {
		Header header = getLastHeaderIgnoreCase("Content-Type");
		if(header==null) return null;
		
		String[] mimeCharset = HTTPUtil.splitMimeTypeAndCharset(header.getValue());
		String[] typeSub = HTTPUtil.splitTypeAndSubType(mimeCharset[0]);
		return new ContentTypeImpl(typeSub[0],typeSub[1],mimeCharset[1]);
	}*/
	
	@Override
	public Header getLastHeader(String name) {
		org.apache.http.Header header = rsp.getLastHeader(name);
		if(header!=null) return new HeaderWrap(header);
		return null;
	}
	
	@Override
	public Header getLastHeaderIgnoreCase(String name) {
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
	
	/*public String getCharset() {
		ContentType ct = getContentType();
		String charset=null;
		if(ct!=null)charset=ct.getCharset();
		if(!StringUtil.isEmpty(charset)) return charset;
		
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) return pc.getConfig().getWebCharset();
		return "ISO-8859-1";
	}*/
	
	/*public long getContentLength() throws IOException {
		
		
		Header ct = getLastHeaderIgnoreCase("Content-Length");
		if(ct!=null) return Caster.toLongValue(ct.getValue(),-1);
		
		
		HttpEntity entity = rsp.getEntity();
		InputStream is=null;
		long length=0;
		try{
			is=entity.getContent();
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
	}*/
	
	@Override
	public URL getURL() {
		try {
			return req.getURI().toURL();
		} catch (MalformedURLException e) {
			return url;
		}
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
}
