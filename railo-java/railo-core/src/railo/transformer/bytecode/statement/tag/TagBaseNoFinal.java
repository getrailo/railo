package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;

public abstract class TagBaseNoFinal extends TagBase { 

	public TagBaseNoFinal(Position start, Position end) {
		super(start, end);
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
