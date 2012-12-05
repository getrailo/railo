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
	
	@Override
	public Map getArguments() {
		return provider.getArguments();
	}

	@Override
	public Resource getResource(String path) {
		return provider.getResource(path);
	}

	@Override
	public String getScheme() {
		return provider.getScheme();
	}

	@Override
	public ResourceProvider init(String scheme, Map arguments) {
		return provider.init(scheme, arguments);
	}

	@Override
	public boolean isAttributesSupported() {
		return provider.isAttributesSupported();
	}

	@Override
	public boolean isCaseSensitive() {
		return provider.isCaseSensitive();
	}

	@Override
	public boolean isModeSupported() {
		return provider.isModeSupported();
	}

	@Override
	public void lock(Resource res) throws IOException {
		provider.lock(res);
	}

	@Override
	public void read(Resource res) throws IOException {
		provider.read(res);
	}

	@Override
	public void setResources(Resources resources) {
		provider.setResources(resources);
	}

	@Override
	public void unlock(Resource res) {
		provider.unlock(res);
	}

}
