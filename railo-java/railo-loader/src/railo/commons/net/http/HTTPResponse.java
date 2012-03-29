package railo.commons.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import railo.commons.io.res.ContentType;

public interface HTTPResponse {

	public String getContentAsString() throws IOException;

	public String getContentAsString(String charset) throws IOException;
	
	public InputStream getContentAsStream() throws IOException;
	
	public byte[] getContentAsByteArray() throws IOException;
	
	public ContentType getContentType();
	
	public Header getLastHeader(String name);
	
	public Header getLastHeaderIgnoreCase(String name);
	
	public String getCharset();
	
	public long getContentLength() throws IOException;
	
	public URL getURL();

	public int getStatusCode();
	
	public String getStatusText();

	public String getProtocolVersion();

	public String getStatusLine();

	public Header[] getAllHeaders();
}
