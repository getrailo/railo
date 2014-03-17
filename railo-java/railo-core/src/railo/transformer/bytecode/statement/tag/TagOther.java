package railo.transformer.bytecode.statement.tag;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.FlowControlFinalImpl;

public class TagOther extends TagBase {

	private FlowControlFinalImpl fcf;
	
	public TagOther(Factory f, Position start, Position end) {
		super(f,start, end);
	}
	
	@Override
	public FlowControlFinal getFlowControlFinal(){
		if(fcf==null && getTagLibTag().handleException())
			fcf=new FlowControlFinalImpl();
		return fcf;
	}
	
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		_writeOut(bc,true,getFlowControlFinal());
	}

}
