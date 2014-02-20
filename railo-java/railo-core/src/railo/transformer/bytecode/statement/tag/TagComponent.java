package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;

public class TagComponent extends TagBase{

	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagComponent(Factory f, Position start,Position end) {
		super(f,start, end);
	}

	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
