package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

public final class OpNegate extends ExpressionBase implements ExprBoolean {

	private ExprBoolean expr;

	private OpNegate(Expression expr, Position start, Position end)  {
        super(start,end);
        this.expr=CastBoolean.toExprBoolean(expr);
    }
    
    /**
     * Create a String expression from a Expression
     * @param left 
     * @param right 
     * 
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprBoolean toExprBoolean(Expression expr, Position start, Position end) {
        if(expr instanceof Literal) {
        	Boolean b=((Literal) expr).getBoolean(null);
        	if(b!=null) {
        		return new LitBoolean(!b.booleanValue(),start,end);
        	}
        }
        return new OpNegate(expr,start,end);
    }
	
	
	/**
	 *
	 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
    	if(mode==MODE_REF) {
            _writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_BOOLEAN);
            return Types.BOOLEAN;
        }
    	
        
        Label l1 = new Label();
        Label l2 = new Label();
        
        expr.writeOut(bc, MODE_VALUE);
        adapter.ifZCmp(Opcodes.IFEQ,l1);
        
        adapter.visitInsn(Opcodes.ICONST_0);
        adapter.visitJumpInsn(Opcodes.GOTO, l2);
        adapter.visitLabel(l1);
        adapter.visitInsn(Opcodes.ICONST_1);
        adapter.visitLabel(l2);

        return Types.BOOLEAN_VALUE;

	}

	/*public int getType() {
		return Types._BOOLEAN;
	}*/

}
