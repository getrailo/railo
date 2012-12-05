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

	@Override
	public void close() throws IOException {
		br.close();
	}

	@Override
	public String getEncoding() {
		return charsetName;
	}

	@Override
	public int read() throws IOException {
		return br.read();
	}

	@Override
	public int read(char[] cbuf, int offset, int length) throws IOException {
		return br.read(cbuf, offset, length);
	}

	@Override
	public boolean ready() throws IOException {
		return br.ready();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		br.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return br.markSupported();
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		return br.read(target.array());
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		return br.read(cbuf);
	}

	@Override
	public void reset() throws IOException {
		br.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return br.skip(n);
	}

}
