package railo.transformer.bytecode.statement;

import railo.transformer.bytecode.Position;

public abstract class StatementBaseNoFinal extends StatementBase {

	public StatementBaseNoFinal(Position start, Position end) {
		super(start, end);
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}
}
