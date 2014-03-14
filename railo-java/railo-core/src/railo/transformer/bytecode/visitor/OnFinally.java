package railo.transformer.bytecode.visitor;

import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;

public interface OnFinally {
	
	public void writeOut(BytecodeContext bc) throws TransformerException;
}
