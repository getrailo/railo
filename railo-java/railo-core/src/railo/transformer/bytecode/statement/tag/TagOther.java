package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.FlowControlFinalImpl;

public class TagOther extends TagBase {

	private FlowControlFinalImpl fcf;
	
	public TagOther(Position start, Position end) {
		super(start, end);
	}
	
	@Override
	public FlowControlFinal getFlowControlFinal(){
		if(fcf==null && getTagLibTag().handleException())
			fcf=new FlowControlFinalImpl();
		return fcf;
	}
	
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		_writeOut(bc,true,getFlowControlFinal());
	}

}
