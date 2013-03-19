package railo.runtime.net.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ListUtil;

public class Attachment implements Serializable {
	


	private String absolutePath;
	private URL url;
	private String type;
	private String disposition;
	private String contentID;
	private String fileName;
	private boolean removeAfterSend;

	public Attachment(Resource resource, String type, String disposition,String contentID, boolean removeAfterSend) {
		this.absolutePath=resource.getAbsolutePath();// do not store resource, this is pehrhaps not serialiable
		this.fileName=resource.getName();
		this.removeAfterSend=removeAfterSend;
		this.disposition=disposition;
		this.contentID=contentID;
		
		// type
		this.type=type;
		if(StringUtil.isEmpty(type)) {
			InputStream is=null;
			try {
				type = IOUtil.getMimeType(is=resource.getInputStream(),null);
			} 
			catch (IOException e) {}
			finally {
				IOUtil.closeEL(is);
			}
		}
	}
	
	public Attachment(URL url) {
		this.url=url;
		
		// filename
		this. fileName=ListUtil.last(url.toExternalForm(), '/');
		if(StringUtil.isEmpty(this.fileName))this.fileName = "url.txt";
		
		try {
			type = IOUtil.getMimeType(url.openStream(), null);
		} catch (IOException e) {}	
		
	}
	
	

	/**
	 * @return the url
	 */
	public URL getURL() {
		return url;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/* *
	 * @return the resource
	 * /
	public Resource getResourcex() {
		return resource;
	}*/
	
	public String getAbsolutePath(){
		return absolutePath;
	}
	
	/**
	 * @return the removeAfterSend
	 */
	public boolean isRemoveAfterSend() {
		return removeAfterSend;
	}

	/**
	 * @param removeAfterSend the removeAfterSend to set
	 */
	public void setRemoveAfterSend(boolean removeAfterSend) {
		this.removeAfterSend = removeAfterSend;
	}

	//resource.getAbsolutePath()
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the disposition
	 */
	public String getDisposition() {
		return disposition;
	}

	/**
	 * @return the contentID
	 */
	public String getContentID() {
		return contentID;
	}
}
