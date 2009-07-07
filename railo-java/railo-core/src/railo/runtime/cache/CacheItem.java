package railo.runtime.cache;

import railo.commons.io.res.Resource;

 
public class CacheItem {
	
	private Resource res,directory;
	private String name,raw;
	/**
	 * @return the res
	 */
	public Resource getResource() {
		return res;
	}
	/**
	 * @param res the res to set
	 */
	public void setResource(Resource res) {
		this.res = res;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the raw
	 */
	public String getRaw() {
		return raw;
	}
	/**
	 * @param raw the raw to set
	 */
	public void setRaw(String raw) {
		this.raw = raw;
	}
	/**
	 * @return the directory
	 */
	public Resource getDirectory() {
		return directory;
	}
	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(Resource directory) {
		this.directory = directory;
	}
	
}