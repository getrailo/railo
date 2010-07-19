package railo.commons.io.res.util;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;

public class ResourceProviderWrapper  implements ResourceProvider {

	private ResourceProvider provider;

	public ResourceProviderWrapper(ResourceProvider provider){
		this.provider=provider;
	}
	
	/**
	 * @see railo.commons.io.res.ResourceProvider#getArguments()
	 */
	public Map getArguments() {
		return provider.getArguments();
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String path) {
		return provider.getResource(path);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#getScheme()
	 */
	public String getScheme() {
		return provider.getScheme();
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#init(java.lang.String, java.util.Map)
	 */
	public ResourceProvider init(String scheme, Map arguments) {
		return provider.init(scheme, arguments);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return provider.isAttributesSupported();
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return provider.isCaseSensitive();
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return provider.isModeSupported();
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#lock(railo.commons.io.res.Resource)
	 */
	public void lock(Resource res) throws IOException {
		provider.lock(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#read(railo.commons.io.res.Resource)
	 */
	public void read(Resource res) throws IOException {
		provider.read(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#setResources(railo.commons.io.res.Resources)
	 */
	public void setResources(Resources resources) {
		provider.setResources(resources);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#unlock(railo.commons.io.res.Resource)
	 */
	public void unlock(Resource res) {
		provider.unlock(res);
	}

}
