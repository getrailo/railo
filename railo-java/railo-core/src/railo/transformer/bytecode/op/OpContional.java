package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class OpContional extends ExpressionBase {

    private ExprBoolean cont;
    private Expression left;
    private Expression right;

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	
    	Label yes = new Label();
    	Label end = new Label();
    	
    	// cont
    	ExpressionUtil.visitLine(bc, cont.getStart());
    	cont.writeOut(bc, MODE_VALUE);
    	ExpressionUtil.visitLine(bc, cont.getEnd());
    	adapter.visitJumpInsn(Opcodes.IFEQ, yes);
    	
    	// left
    	ExpressionUtil.visitLine(bc, left.getStart());
    	left.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, left.getEnd());
    	adapter.visitJumpInsn(Opcodes.GOTO, end);
    	
    	// right
    	ExpressionUtil.visitLine(bc, right.getStart());
    	adapter.visitLabel(yes);
    	right.writeOut(bc, MODE_REF);
    	ExpressionUtil.visitLine(bc, right.getEnd());
    	adapter.visitLabel(end);
    	
    	return Types.OBJECT;
    	
    }
    

    
    
    
    
    
    private OpContional(Expression cont, Expression left, Expression right) {
        super(left.getStart(),right.getEnd());
        this.cont=CastBoolean.toExprBoolean(cont);
        this.left=left;  
        this.right=right;  
    }
    

    public static Expression toExpr(Expression cont, Expression left, Expression right) {
        return new OpContional(cont,left,right);
    }
    

    /* *
     * @see railo.transformer.bytecode.expression.Expression#getType()
     * /
    public int getType() {
        return Types._BOOLEAN;
    }*/
}

