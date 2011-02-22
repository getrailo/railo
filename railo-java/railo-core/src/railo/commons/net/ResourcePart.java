package railo.commons.net;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.PartSource;

import railo.commons.lang.StringUtil;

public class ResourcePart extends FilePart {

	public ResourcePart(String name, PartSource partSource, String contentType, String charset) {
		super(name, partSource, contentType, charset==null?"":charset);
		
	}
	
	public ResourcePart(String name, PartSource partSource, String contentType) {
		super(name, partSource, contentType, "");
		
	}

	/**
	 * @see org.apache.commons.httpclient.methods.multipart.PartBase#getCharSet()
	 */
	public String getCharSet() {
		String cs = super.getCharSet();
		if(StringUtil.isEmpty(cs)) return null;
		return cs;
	}

}
