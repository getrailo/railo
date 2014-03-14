package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;

public abstract class TagBaseNoFinal extends TagBase { 

	public TagBaseNoFinal(Factory factory,Position start, Position end) {
		super(factory,start, end);
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
