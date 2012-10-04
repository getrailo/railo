package railo.transformer.bytecode.visitor;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;

public interface OnFinally {
	
	public void writeOut(BytecodeContext bc) throws BytecodeException;
}
