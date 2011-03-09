package railo.commons.net;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.PartSource;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

public class ResourcePart extends FilePart {

	private Resource resource;

	public ResourcePart(String name, ResourcePartSource partSource, String contentType, String charset) {
		super(name, partSource, contentType, charset==null?"":charset);
		this.resource=partSource.getResource();
	}
	
	public ResourcePart(String name, ResourcePartSource partSource, String contentType) {
		super(name, partSource, contentType, "");
		this.resource=partSource.getResource();
		
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
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
