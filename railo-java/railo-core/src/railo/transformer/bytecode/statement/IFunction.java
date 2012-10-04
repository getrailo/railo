package railo.transformer.bytecode.statement;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;

public interface IFunction {

	public static final int PAGE_TYPE_REGULAR=0;
	public static final int PAGE_TYPE_COMPONENT=1;
	public static final int PAGE_TYPE_INTERFACE=2;
	

	public static final int ARRAY_INDEX=0;
	public static final int VALUE_INDEX=1;
	
	public void writeOut(BytecodeContext bc, int type)
			throws BytecodeException;

}