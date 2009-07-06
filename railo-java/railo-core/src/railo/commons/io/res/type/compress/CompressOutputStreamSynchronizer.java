package railo.commons.io.res.type.compress;

import java.io.IOException;
import java.io.OutputStream;

public final class CompressOutputStreamSynchronizer extends OutputStream {

	private final OutputStream os;
	private final Compress zip;
	private final boolean async;

	public CompressOutputStreamSynchronizer(OutputStream os, Compress zip,boolean async) {
		this.os=os;
		this.zip=zip;
		this.async=async;
	}

	/**
	 *
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		os.close();
		zip.synchronize(async);
	}

	/**
	 *
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		os.write(b);
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

}
