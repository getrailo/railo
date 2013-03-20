package railo.commons.lang.mimetype;

import railo.commons.lang.StringUtil;

public class ContentType {
	private String mimeType;
	private String charset;

	public ContentType(String mimeType){
		this.mimeType=mimeType;
	}
	public ContentType(String mimeType, String charset){
		this.mimeType=mimeType;
		setCharset(charset);
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		if(!StringUtil.isEmpty(charset,true)){
			this.charset=charset.trim();
		}
		else this.charset=null;
	}
	
	public String toString(){
		if(charset==null) return mimeType.toString();
		return mimeType+"; charset="+charset;
	}
}
