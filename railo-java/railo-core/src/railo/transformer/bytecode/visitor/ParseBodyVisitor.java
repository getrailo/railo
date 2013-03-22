package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.Types;

public final class ParseBodyVisitor {

	// void outputStart()
	public final static Method OUTPUT_START = new Method("outputStart",Types.VOID,new Type[]{});
	
	// void outputEnd()
	public final static Method OUTPUT_END = new Method("outputEnd",Types.VOID,new Type[]{});
	private TryFinallyVisitor tfv;

	public void visitBegin(BytecodeContext bc) {
		GeneratorAdapter adapter = bc.getAdapter();

		tfv=new TryFinallyVisitor(new OnFinally() {
			public void writeOut(BytecodeContext bc) {
				//ExpressionUtil.visitLine(bc, line);
				bc.getAdapter().loadArg(0);
				bc.getAdapter().invokeVirtual(Types.PAGE_CONTEXT,OUTPUT_END);
			}
		},null);

		//ExpressionUtil.visitLine(bc, line);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT,OUTPUT_START);
		tfv.visitTryBegin(bc);


	}
	public void visitEnd(BytecodeContext bc) throws BytecodeException {
		
		tfv.visitTryEnd(bc);

	}
}
