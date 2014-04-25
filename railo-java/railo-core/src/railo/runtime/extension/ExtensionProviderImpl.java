package railo.runtime.extension;

import java.net.MalformedURLException;
import java.net.URL;

public class ExtensionProviderImpl implements ExtensionProvider {


	//private String name;
	private URL url;
	private String strUrl;
	private boolean readOnly;
	
	public ExtensionProviderImpl(URL url, boolean readOnly) {
		//this.name = name;
		this.url = url;
		this.readOnly=readOnly;
	}
	
	public ExtensionProviderImpl(String strUrl, boolean readOnly) {
		//this.name = name;
		this.strUrl=strUrl;
		this.readOnly=readOnly;
	}

	/**
	 * @return the url
	 * @throws MalformedURLException 
	 */
	@Override
	public URL getUrl() throws MalformedURLException {
		if(url==null)url=new URL(strUrl);
		return url;
	}

	@Override
	public String getUrlAsString() {
		if(strUrl!=null) return strUrl;
		return url.toExternalForm();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String toString() {
		return "url:"+getUrlAsString()+";";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		//if(!(obj instanceof ExtensionProvider))return false;
		
		return toString().equals(obj.toString());
	}

}
