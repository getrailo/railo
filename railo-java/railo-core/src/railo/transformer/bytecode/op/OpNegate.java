package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.exp.TemplateException;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.Literal;

public final class OpNegate extends ExpressionBase implements ExprBoolean {

	private ExprBoolean expr;

	private OpNegate(Expression expr, Position start, Position end)  {
        super(expr.getFactory(),start,end);
        this.expr=expr.getFactory().toExprBoolean(expr);
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
        		return expr.getFactory().createLitBoolean(!b.booleanValue(),start,end);
        	}
        }
        return new OpNegate(expr,start,end);
    }
	
	
	/**
	 *
	 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
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
