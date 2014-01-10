package railo.runtime.writer;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.jsp.JspWriter;

import railo.runtime.cache.legacy.CacheItem;

public abstract class CFMLWriter extends JspWriter {
	
	protected CFMLWriter(int bufferSize, boolean autoFlush) {
		super(bufferSize, autoFlush);
	}

	public abstract OutputStream getResponseStream() throws IOException;
	
	public abstract void setClosed(boolean b) ;

	public abstract void setBufferConfig(int interval, boolean b) throws IOException ;

	public abstract void appendHTMLHead(String text) throws IOException;
	
	public abstract void writeHTMLHead(String text) throws IOException;
	
	public abstract String getHTMLHead() throws IOException;
	
	public abstract void resetHTMLHead() throws IOException;
	

	/**
	 * write the given string without removing whitespace.
	 * @param str
	 * @throws IOException 
	 */
	public abstract void writeRaw(String str) throws IOException;

	public abstract void setAllowCompression(boolean allowCompression);
	

	public abstract void doCache(railo.runtime.cache.legacy.CacheItem ci);

	/**
	 * @return the cacheResource
	 */
	public abstract CacheItem getCacheItem();

}
