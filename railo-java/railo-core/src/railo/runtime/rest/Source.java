package railo.runtime.rest;

import railo.runtime.PageSource;
import railo.runtime.rest.path.Path;

public class Source {

	private Mapping mapping;
	private Path[] path;
	private String rawPath;
	private PageSource pageSource;

	public Source(Mapping mapping, PageSource pageSource, String path) {
		this.mapping=mapping;
		this.pageSource=pageSource;
		this.path=Path.init(path); 
		this.rawPath=path;
	}

	/**
	 * @return the pageSource
	 */
	public PageSource getPageSource() {
		return pageSource;
	}

	/**
	 * @return the mapping
	 */
	public Mapping getMapping() {
		return mapping;
	}

	/**
	 * @return the path
	 */
	public Path[] getPath() {
		return path;
	}
	public String getRawPath() {
		return rawPath;
	}

}
