package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.Position;

public class TagComponent extends TagBase{

	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagComponent(Position start,Position end) {
		super(start, end);
	}


}
