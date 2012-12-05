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
	
	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public void close() throws IOException {
		try {
			is.close();
		}
		finally {
			res.getResourceProvider().unlock(res);
		}
	}

	@Override
	public void mark(int readlimit) {
		is.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public synchronized void reset() throws IOException {
		is.reset();
	}

	@Override
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
