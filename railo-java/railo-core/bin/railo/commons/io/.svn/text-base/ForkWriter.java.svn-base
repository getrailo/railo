package railo.commons.io;

import java.io.IOException;
import java.io.Writer;

public class ForkWriter extends Writer {

	private final Writer w1;
	private final Writer w2;

	public ForkWriter(Writer w1, Writer w2) {
		this.w1=w1;
		this.w2=w2;
	}

	/**
	 *
	 * @see java.io.Writer#append(char)
	 */
	public Writer append(char c) throws IOException {
		try {
			w1.write(c);
		}
		finally {
			w2.write(c);
		}
		return this;
	}

	/**
	 *
	 * @see java.io.Writer#append(java.lang.CharSequence, int, int)
	 */
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		try {
			w1.write(csq.toString(), start, end);
		}
		finally {
			w2.write(csq.toString(), start, end);
		}
		return this;
	}

	/**
	 *
	 * @see java.io.Writer#append(java.lang.CharSequence)
	 */
	public Writer append(CharSequence csq) throws IOException {
		try {
			w1.write(csq.toString());
		}
		finally {
			w2.write(csq.toString());
		}
		return this;
	}

	/**
	 *
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) throws IOException {
		try {
			w1.write(cbuf);
		}
		finally {
			w2.write(cbuf);
		}
	}

	/**
	 *
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) throws IOException {
		try {
			w1.write(c);
		}
		finally {
			w2.write(c);
		}
	}

	/**
	 *
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
		try {
			w1.write(str, off, len);
		}
		finally {
			w2.write(str, off, len);
		}
	}

	/**
	 *
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
		try {
			w1.write(str);
		}
		finally {
			w2.write(str);
		}
	}

	/**
	 *
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		try {
			w1.close();
		}
		finally {
			w2.close();
		}
	}

	/**
	 *
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {

		try {
			w1.flush();
		}
		finally {
			w2.flush();
		}
	}

	/**
	 *
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {

		try {
			w1.write(cbuf, off, len);
		}
		finally {
			w2.write(cbuf, off, len);
		}
	}

}
