package railo.runtime.rest;

import railo.runtime.PageSource;
import railo.runtime.rest.path.Path;

public class Source {

	private Mapping mapping;
	private Path[] path;
	private PageSource pageSource;

	public Source(Mapping mapping, PageSource pageSource, String path) {
		this.mapping=mapping;
		this.pageSource=pageSource;
		this.path=Path.init(path); 
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

}
