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
	
	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void close() throws IOException {
		try {
			os.close();
		}
		finally {
			res.getResourceProvider().unlock(res);
		}
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
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
