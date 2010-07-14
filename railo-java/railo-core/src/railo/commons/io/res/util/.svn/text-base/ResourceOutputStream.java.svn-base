package railo.commons.io.res.util;

import java.io.IOException;
import java.io.OutputStream;

import railo.commons.io.res.Resource;

public class ResourceOutputStream extends OutputStream {

	private final Resource res;
	private final OutputStream os;

	/**
	 * Constructor of the class
	 * @param res
	 * @param os
	 */
	public ResourceOutputStream(Resource res, OutputStream os) {
		this.res=res;
		this.os=os;
	}
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		os.write(b);
	}

	/**
	 *
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		try {
			os.close();
		}
		finally {
			res.getResourceProvider().unlock(res);
		}
	}

	/**
	 *
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		os.write(b);
	}

	/**
	 * @return the os
	 */
	public OutputStream getOutputStream() {
		return os;
	}

	/**
	 * @return the res
	 */
	public Resource getResource() {
		return res;
	}

}
