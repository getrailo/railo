package railo.commons.io.reader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import railo.commons.io.IOUtil;

/**
 * InputStream Reader for byte arrays, support mark
 */
public final class ByteArrayInputStreamReader extends InputStreamReader {

	private final BufferedReader br;
	private final String charsetName;

	public ByteArrayInputStreamReader(ByteArrayInputStream bais, String charsetName) throws IOException {
		super(bais, charsetName);
		this.br=IOUtil.toBufferedReader(IOUtil.getReader(bais, charsetName));
		this.charsetName=charsetName;
	}
	
	public ByteArrayInputStreamReader(byte[] barr, String charsetName) throws IOException {
		this(new ByteArrayInputStream(barr), charsetName);
	}

	public ByteArrayInputStreamReader(String str, String charsetName) throws IOException {
		this(new ByteArrayInputStream(str.getBytes(charsetName)), charsetName);
	}

	/**
	 *
	 * @see java.io.InputStreamReader#close()
	 */
	public void close() throws IOException {
		br.close();
	}

	/**
	 *
	 * @see java.io.InputStreamReader#getEncoding()
	 */
	public String getEncoding() {
		return charsetName;
	}

	/**
	 *
	 * @see java.io.InputStreamReader#read()
	 */
	public int read() throws IOException {
		return br.read();
	}

	/**
	 *
	 * @see java.io.InputStreamReader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int offset, int length) throws IOException {
		return br.read(cbuf, offset, length);
	}

	/**
	 *
	 * @see java.io.InputStreamReader#ready()
	 */
	public boolean ready() throws IOException {
		return br.ready();
	}

	/**
	 *
	 * @see java.io.Reader#mark(int)
	 */
	public void mark(int readAheadLimit) throws IOException {
		br.mark(readAheadLimit);
	}

	/**
	 *
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported() {
		return br.markSupported();
	}

	/**
	 *
	 * @see java.io.Reader#read(java.nio.CharBuffer)
	 */
	public int read(CharBuffer target) throws IOException {
		return br.read(target.array());
	}

	/**
	 *
	 * @see java.io.Reader#read(char[])
	 */
	public int read(char[] cbuf) throws IOException {
		return br.read(cbuf);
	}

	/**
	 *
	 * @see java.io.Reader#reset()
	 */
	public void reset() throws IOException {
		br.reset();
	}

	/**
	 *
	 * @see java.io.Reader#skip(long)
	 */
	public long skip(long n) throws IOException {
		return br.skip(n);
	}

}
