package railo.commons.io.reader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
import railo.commons.io.IOUtil;

/**
 * InputStream Reader for byte arrays, support mark
 */
public final class ByteArrayInputStreamReader extends InputStreamReader {

	private final BufferedReader br;
	private final Charset charset;

	
	public ByteArrayInputStreamReader(ByteArrayInputStream bais, Charset charset) throws IOException {
		super(bais, charset);
		this.br=IOUtil.toBufferedReader(IOUtil.getReader(bais, charset));
		this.charset=charset;
	}
	
	public ByteArrayInputStreamReader(byte[] barr, Charset charset) throws IOException {
		this(new ByteArrayInputStream(barr), charset);
	}

	public ByteArrayInputStreamReader(String str, Charset charset) throws IOException {
		this(new ByteArrayInputStream(str.getBytes(charset)), charset);
	}
	
	/**
	 * @deprecated use instead <code>{@link #ByteArrayInputStreamReader(ByteArrayInputStream, Charset)}</code>
	 * @param bais
	 * @param charsetName
	 * @throws IOException
	 */
	public ByteArrayInputStreamReader(ByteArrayInputStream bais, String charsetName) throws IOException {
		this(bais, CharsetUtil.toCharset(charsetName));
	}
	
	/**
	 * @deprecated use instead <code>{@link #ByteArrayInputStreamReader(byte[], Charset)}</code>
	 * @param barr
	 * @param charsetName
	 * @throws IOException
	 */
	public ByteArrayInputStreamReader(byte[] barr, String charsetName) throws IOException {
		this(new ByteArrayInputStream(barr), CharsetUtil.toCharset(charsetName));
	}

	/**
	 * @deprecated use instead <code>{@link #ByteArrayInputStreamReader(String, Charset)}</code>
	 * @param str
	 * @param charsetName
	 * @throws IOException
	 */
	public ByteArrayInputStreamReader(String str, String charsetName) throws IOException {
		this(str, CharsetUtil.toCharset(charsetName));
	}

	@Override
	public void close() throws IOException {
		br.close();
	}

	@Override
	public String getEncoding() {
		return charset.name();
	}
	public Charset getCharset() {
		return charset;
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
