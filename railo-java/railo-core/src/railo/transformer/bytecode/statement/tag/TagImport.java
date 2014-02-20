package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.bytecode.Position;

public class TagImport extends TagBaseNoFinal {


	private String path;

	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagImport(Factory f, Position start,Position end) {
		super(f,start, end);
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
