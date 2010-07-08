package railo.transformer.bytecode.statement.tag;

public class TagImport extends TagBase {


	private String path;


	/**
	 * Constructor of the class
	 * @param startLine
	 */
	public TagImport(int startLine) {
		super(startLine);
	}
	
	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagImport(int startLine, int endLine) {
		super(startLine, endLine);
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}



}
