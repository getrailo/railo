package railo.commons.io.res.type.ram;

import railo.commons.io.res.type.cache.CacheResourceProvider;
import railo.commons.io.res.util.ResourceProviderWrapper;

// FUTURE this is just a wrapper for the cacheprovider, replace this with the real cacheprovider
public class RamResourceProvider  extends ResourceProviderWrapper {

	private CacheResourceProvider provider;

	public RamResourceProvider(){
		super(new CacheResourceProvider());
	}
}
