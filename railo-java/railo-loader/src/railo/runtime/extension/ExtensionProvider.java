package railo.runtime.extension;

import java.net.MalformedURLException;
import java.net.URL;

public interface ExtensionProvider {


	/**
	 * return the url of the extension
	 * @return url
	 */
	public URL getUrl() throws MalformedURLException;

	/**
	 * returns the url of the extension as a string
	 * @return url
	 */
	public String getUrlAsString();

	/**
	 * is the extension readonly
	 * @return is readonly
	 */
	public boolean isReadOnly();
}
