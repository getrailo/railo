package railo.runtime.net.smtp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public final class StringDataSource implements DataSource {

	private String text;
	private String ct;
	private String charset;

	public StringDataSource(String text, String ct, String charset) {
		this.text=text;
		this.ct=ct;
		this.charset=charset;
	}

	@Override
	public String getContentType() {
		return ct;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(text.getBytes(charset));
	}

	@Override
	public String getName() {
		return "StringDataSource";
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("no access to write");
	}

}
