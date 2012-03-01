package railo.runtime.rest;

import railo.runtime.PageSource;

public class Source {

	private Mapping mapping;
	private String path;
	private PageSource pageSource;

	public Source(Mapping mapping, PageSource pageSource, String path) {
		this.mapping=mapping;
		this.pageSource=pageSource;
		this.path=path; 
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
	public String getPath() {
		return path;
	}

}
