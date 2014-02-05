package railo.runtime.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import railo.commons.io.res.Resource;

public interface IO {
	/**
	 * close stream silently (no Exception)
	 * @param is 
	 * @param os 
	 */
	public void closeSilent(InputStream is);

	/**
	 * close stream silently (no Exception)
	 * @param is 
	 * @param os 
	 */
	public void closeSilent(OutputStream os);

	/**
	 * close streams silently (no Exception)
	 * @param is 
	 * @param os 
	 */
	public void closeSilent(InputStream is, OutputStream os);

	/**
	 * close streams silently (no Exception)
	 * @param r
	 */
	public void closeSilent(Reader r);

	/**
	 * close streams silently (no Exception)
	 * @param r
	 */
	public void closeSilent(Writer w);

	/**
	 * close any object with a close method silently
	 * @param r
	 */
	public void closeSilent(Object o);
	
	/**
	 * converts a InputStream to a String
	 * @param is
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String toString(InputStream is, Charset charset) throws IOException;
	
	/**
	 * reads the content of a Resource
	 * @param res
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String toString(Resource res, Charset charset) throws IOException;
	
	/**
	 * converts a byte array to a String
	 * @param barr
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String toString(byte[] barr, Charset charset) throws IOException;
	
	/**
	 * reads readers data as String
	 * @param r
	 * @return
	 * @throws IOException
	 */
	public String toString(Reader r) throws IOException;
	
	/**
	 * copy data from input stream to output stream
	 * @param in
	 * @param out
	 * @param closeIS close input stream when done
	 * @param closeOS close output stream when done
	 * @throws IOException
	 */
	public void copy(InputStream in, OutputStream out, boolean closeIS, boolean closeOS) throws IOException;

	/**
	 * copy data from reader to writer
	 * @param in
	 * @param out
	 * @param closeR close the reader when done
	 * @param closeW close the writer when done
	 * @throws IOException
	 */
	public void copy(Reader in, Writer out, boolean closeR, boolean closeW) throws IOException;
	
	/**
	 * copy content from source to target
	 * @param src
	 * @param trg
	 * @throws IOException
	 */
	public void copy(Resource src, Resource trg) throws IOException;

	public BufferedInputStream toBufferedInputStream(InputStream is);

	public BufferedOutputStream toBufferedOutputStream(OutputStream os);
	
}
