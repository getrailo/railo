package railo.commons.io.res;

public interface Resources {

	/**
	 * adds a default factory, this factory is used, when shemecan't be mapped to a other factory
	 * @param provider
	 */
	public void registerDefaultResourceProvider(ResourceProvider provider);
	
	/**
	 * adds a additional resource to System
	 * @param provider
	 */
	public void registerResourceProvider(ResourceProvider provider);
	
	/**
	 * returns a resource that matching the given path
	 * @param path
	 * @return matching resource
	 */
	public Resource getResource(String path);


	/**
	 * @return the defaultResource
	 */
	public ResourceProvider getDefaultResourceProvider();

	public ResourceProvider[] getResourceProviders();

	public ResourceLock createResourceLock(long timeout,boolean caseSensitive);

	public void reset();
}
