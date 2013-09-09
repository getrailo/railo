package railo.runtime.tag;

public class ZipParamContent implements ZipParamAbstr {

	private Object content;
	private String entryPath;
	private String charset;

	public ZipParamContent(Object content, String entryPath, String charset) {
		this.content=content;
		this.entryPath=entryPath;
		this.charset=charset;
	}

	/**
	 * @return the content
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @return the entryPath
	 */
	public String getEntryPath() {
		return entryPath;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}


}
