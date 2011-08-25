package railo.commons.io.res;

import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.type.List;

public final class ContentTypeImpl implements ContentType {

	public final static ContentType APPLICATION_UNKNOW=new ContentTypeImpl("application","unknow");
	
	private final String type;
	private final String subtype;
	private String charset;

	/**
	 * Constructor of the class
	 * @param type
	 * @param subtype
	 * @param charset
	 */
	public ContentTypeImpl(String type,String subtype, String charset) {
		this.type=type.trim().toLowerCase();
		this.subtype=subtype.trim().toLowerCase();
		this.charset=charset.trim().toLowerCase();
	}
	
	/**
	 * Constructor of the class
	 * @param type
	 * @param subtype
	 */
	public ContentTypeImpl(String type,String subtype) {
		this.type=type.trim().toLowerCase();
		this.subtype=subtype.trim().toLowerCase();
	}

	public ContentTypeImpl(InputStream is) {
		String raw=IOUtil.getMymeType(is, null);
		String[] arr = List.listToStringArray(raw, '/');
		this.type=arr[0];
		this.subtype=arr[1];
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if((other instanceof ContentType)) return false;
		return toString().equals(other.toString());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(type==null)return APPLICATION_UNKNOW.toString();
		if(this.charset==null) return type+"/"+subtype;
		return type+"/"+subtype+" charset="+charset;
	}
	
	// FUTURE add to interface
	/**
	 * @return the mime type
	 */
	public String getMimeType() {
		if(type==null)return APPLICATION_UNKNOW.toString();
		return type+"/"+subtype;
	}

	// FUTURE add to interface
	/**
	 * @return the charset
	 */
	public String getCharset() {
		if(StringUtil.isEmpty(charset,true)) return null;
		return charset;
	}
	
}
