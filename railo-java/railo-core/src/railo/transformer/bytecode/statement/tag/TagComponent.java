package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;

public class TagComponent extends TagBase{

	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagComponent(Position start,Position end) {
		super(start, end);
	}

	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
