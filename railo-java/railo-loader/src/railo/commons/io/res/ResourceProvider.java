package railo.commons.io.res;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Interface for resource provider, loaded by "Resources",
 * classes that implement a provider that produce resources, that match given path.
 * 
 */
public interface ResourceProvider extends Serializable {
	
	
	/**
	 * this class is called by the "Resources" at startup 
	 * @param scheme of the provider (can be "null")
	 * @param arguments initals argument (can be "null")
	 */
	public ResourceProvider init(String scheme, Map arguments);
	
	/**
	 * return a resource that match given path
	 * @param path 
	 * @return matching resource to path
	 */
	public Resource getResource(String path);

	/**
	 * returns the scheme of the resource
	 * @return scheme
	 */
	public String getScheme();
	
	/**
	 * returns the arguments defined for this resource
	 * @return scheme
	 */
	public Map getArguments();// FUTURE Map<String,String>

	public void setResources(Resources resources);

	public void unlock(Resource res);
	public void lock(Resource res) throws IOException;
	public void read(Resource res) throws IOException;

	/** 
     * returns if the resources of the provider are case-sensitive or not 
     * @return is resource case-sensitive or not 
     */ 
    public boolean isCaseSensitive();

    /** 
     * returns if the resource support mode for his resources 
     * @return is mode supported or not 
     */ 
    public boolean isModeSupported();

    /** 
     * returns if the resource support attributes for his resources 
     * @return is attributes supported or not 
     */ 
    public boolean isAttributesSupported();
}
