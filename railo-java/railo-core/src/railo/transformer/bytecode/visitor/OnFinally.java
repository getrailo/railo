package railo.transformer.bytecode.visitor;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;

public abstract class OnFinally {

	public final void writeOut(BytecodeContext bc) throws BytecodeException {
		try{
			bc.finallyPush(this);
			_writeOut(bc);
		}
		finally {
			bc.finallyPop();
		}
	}
	public abstract void _writeOut(BytecodeContext bc) throws BytecodeException;
}
