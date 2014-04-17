package railo.runtime.tag;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;

public class ZipParamSource implements ZipParamAbstr {

	private Resource source;
	private String entryPath;
	private ResourceFilter filter;
	private String prefix;
	private boolean recurse;

	public ZipParamSource(Resource source, String entryPath, ResourceFilter filter, String prefix, boolean recurse) {

		this.source=source;
		this.entryPath=entryPath;
		this.filter=filter;
		this.prefix=prefix;
		this.recurse=recurse;
	}

	/**
	 * @return the source
	 */
	public Resource getSource() {
		return source;
	}

	/**
	 * @return the entryPath
	 */
	public String getEntryPath() {
		return entryPath;
	}

	/**
	 * @return the filter
	 */
	public ResourceFilter getFilter(){
		return filter;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return the recurse
	 */
	public boolean isRecurse() {
		return recurse;
	}

}
