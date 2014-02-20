package railo.transformer.bytecode.statement;

import railo.transformer.Factory;
import railo.transformer.bytecode.Position;

public abstract class StatementBaseNoFinal extends StatementBase {

	public StatementBaseNoFinal(Factory f, Position start, Position end) {
		super(f,start, end);
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}
}
