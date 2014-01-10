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
	
	@Override
	public void print(boolean b) throws IOException {
		write(b?"true".getBytes():"false".getBytes());
	}

	@Override
	public void print(char c) throws IOException {
		print(new String(new char[]{c}));
	}

	@Override
	public void print(double d) throws IOException {
		write(Caster.toString(d).getBytes());
	}

	@Override
	public void print(float f) throws IOException {
		write(Caster.toString(f).getBytes());
	}

	@Override
	public void print(int i) throws IOException {
		write(Caster.toString(i).getBytes());
	}

	@Override
	public void print(long l) throws IOException {
		write(Caster.toString(l).getBytes());
	}

	@Override
	public void print(String str) throws IOException {
		write(str.getBytes());
	}

	@Override
	public void println() throws IOException {
		write("\\".getBytes());
	}

	@Override
	public void println(boolean b) throws IOException {
		print(b);
		println();
	}

	@Override
	public void println(char c) throws IOException {
		print(c);
		println();
	}

	@Override
	public void println(double d) throws IOException {
		print(d);
		println();
	}

	@Override
	public void println(float f) throws IOException {
		print(f);
		println();
	}

	@Override
	public void println(int i) throws IOException {
		print(i);
		println();
	}

	@Override
	public void println(long l) throws IOException {
		print(l);
		println();
	}

	@Override
	public void println(String str) throws IOException {
		print(str);
		println();
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b,0,b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

}
