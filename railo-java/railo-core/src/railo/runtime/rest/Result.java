package railo.runtime.rest;


import railo.runtime.type.Struct;

public class Result {

	private Source source;
	private String[] path;
	private Struct variables;
	private int format;

	public Result(Source source, Struct variables, String[] path, int format) {
		this.source=source;
		this.variables=variables;
		this.path=path;
		this.format=format;
	}

	/**
	 * @return the variables
	 */
	public Struct getVariables() {
		return variables;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the path
	 */
	public String[] getPath() {
		return path;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

}
