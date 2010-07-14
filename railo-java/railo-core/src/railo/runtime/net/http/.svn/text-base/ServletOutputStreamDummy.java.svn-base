package railo.runtime.net.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import railo.runtime.op.Caster;

public final class ServletOutputStreamDummy extends ServletOutputStream {

	private OutputStream os;

	//private HttpServletResponseDummy rsp;
	//private ByteArrayOutputStream baos;

	public ServletOutputStreamDummy(OutputStream os) {
		this.os=os;
	}
	
	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(boolean)
	 */
	public void print(boolean b) throws IOException {
		write(b?"true".getBytes():"false".getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(char)
	 */
	public void print(char c) throws IOException {
		print(new String(new char[]{c}));
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(double)
	 */
	public void print(double d) throws IOException {
		write(Caster.toString(d).getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(float)
	 */
	public void print(float f) throws IOException {
		write(Caster.toString(f).getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(int)
	 */
	public void print(int i) throws IOException {
		write(Caster.toString(i).getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(long)
	 */
	public void print(long l) throws IOException {
		write(Caster.toString(l).getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#print(java.lang.String)
	 */
	public void print(String str) throws IOException {
		write(str.getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println()
	 */
	public void println() throws IOException {
		write("\\".getBytes());
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(boolean)
	 */
	public void println(boolean b) throws IOException {
		print(b);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(char)
	 */
	public void println(char c) throws IOException {
		print(c);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(double)
	 */
	public void println(double d) throws IOException {
		print(d);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(float)
	 */
	public void println(float f) throws IOException {
		print(f);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(int)
	 */
	public void println(int i) throws IOException {
		print(i);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(long)
	 */
	public void println(long l) throws IOException {
		print(l);
		println();
	}

	/**
	 *
	 * @see javax.servlet.ServletOutputStream#println(java.lang.String)
	 */
	public void println(String str) throws IOException {
		print(str);
		println();
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		write(b,0,b.length);
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
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
		os.close();
	}

	/**
	 *
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

}
