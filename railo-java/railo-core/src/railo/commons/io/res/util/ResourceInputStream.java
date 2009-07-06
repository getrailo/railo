package railo.commons.io.res.util;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.Resource;

public class ResourceInputStream extends InputStream {

	private final Resource res;
	private final InputStream is;

	public ResourceInputStream(Resource res, InputStream is) {
		this.res=res;
		this.is=is; 
	}
	
	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return is.read();
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return is.available();
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		try {
			is.close();
		}
		finally {
			res.getResourceProvider().unlock(res);
		}
	}

	/**
	 * @see java.io.InputStream#mark(int)
	 */
	public void mark(int readlimit) {
		is.mark(readlimit);
	}

	/**
	 *
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return is.markSupported();
	}

	/**
	 *
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	/**
	 *
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	/**
	 *
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		is.reset();
	}

	/**
	 *
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	/**
	 * @return the InputStream
	 */
	public InputStream getInputStream() {
		return is;
	}

	/**
	 * @return the Resource
	 */
	public Resource getResource() {
		return res;
	}

}
