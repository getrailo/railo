package railo.commons.io.auto;

import java.io.IOException;
import java.io.Writer;

/**
 * Close the Writer automaticlly when object will destroyed by the garbage
 */
public final class AutoCloseWriter extends Writer {
	
	private final Writer writer;

	/**
	 * constructor of the class
	 * @param writer
	 */
	public AutoCloseWriter(Writer writer) {
		this.writer=writer;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf,off,len);
	}

	@Override
	public void write(char[] cbuf) throws IOException {
		writer.write(cbuf);
	}

	@Override
	public void write(int c) throws IOException {
		writer.write(c);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		writer.write(str,off,len);
	}

	@Override
	public void write(String str) throws IOException {
		writer.write(str);
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		try {
			writer.close();
		}
		catch(Exception e) {}
	}
	

}
