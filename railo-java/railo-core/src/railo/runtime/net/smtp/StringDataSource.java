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

	/**
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return ct;
	}

	/**
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(text.getBytes(charset));
	}

	/**
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return "StringDataSource";
	}

	/**
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("no access to write");
	}

}
