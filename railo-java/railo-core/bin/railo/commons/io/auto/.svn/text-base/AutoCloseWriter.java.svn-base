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

	/**
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		writer.close();
	}

	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		writer.flush();
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf,off,len);
	}

	/**
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) throws IOException {
		writer.write(cbuf);
	}

	/**
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) throws IOException {
		writer.write(c);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
		writer.write(str,off,len);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
		writer.write(str);
	}
	
	/**
	 * @throws Throwable 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() throws Throwable {
		super.finalize();
		try {
			writer.close();
		}
		catch(Exception e) {}
	}
	

}
