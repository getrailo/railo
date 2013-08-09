package railo.transformer.bytecode.expression;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.Types;

public class FailSafeExpression  extends ExpressionBase implements Opcodes {

	private Expression expr;
	private Expression defaultValue;
	

	
	public FailSafeExpression(Expression expr,Expression defaultValue) {
		super(expr.getStart(), expr.getEnd());
		this.expr=expr;
		this.defaultValue=defaultValue;
	}


	
	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter mv = bc.getAdapter();
		
		int local = mv.newLocal(Types.OBJECT);
		
		
		
		{
			Label begin = new Label();
			Label onSuccess = new Label();
			Label onFail = new Label();
			Label end = new Label();
			
			mv.visitTryCatchBlock(begin, onSuccess, onFail, "java/lang/Throwable");
			mv.visitLabel(begin);
			
				expr.writeOut(bc, MODE_REF);
				mv.storeLocal(local);
			
			mv.visitLabel(onSuccess);
			mv.visitJumpInsn(GOTO, end);
			
			mv.visitLabel(onFail);
			//mv.visitVarInsn(ASTORE, 2);
			
				defaultValue.writeOut(bc, MODE_REF);
				mv.storeLocal(local);
			
			
			mv.visitLabel(end);
			mv.loadLocal(local);

			//mv.visitLocalVariable("e", "Ljava/lang/Throwable;", null, l4, l3, 3);
			
		
			}

		
		
		return Types.OBJECT;
	}

}
