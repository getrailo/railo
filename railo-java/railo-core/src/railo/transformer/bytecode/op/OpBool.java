package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Methods_Operator;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.Literal;

public final class OpBool extends ExpressionBase implements ExprBoolean {
    
    private ExprBoolean left;
    private ExprBoolean right;
    private int operation;

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	if(mode==MODE_REF) {
            _writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_BOOLEAN);
            return Types.BOOLEAN;
        }
    	
    	
    	Label doFalse = new Label();
    	Label end = new Label();

    	if(operation==Factory.OP_BOOL_AND) {
    		left.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFEQ, doFalse);
        	
        	right.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFEQ, doFalse);
        	adapter.push(true);
        	
        	adapter.visitJumpInsn(Opcodes.GOTO, end);
        	adapter.visitLabel(doFalse);

        	adapter.push(false);
        	adapter.visitLabel(end);
    	}
    	if(operation==Factory.OP_BOOL_OR) {
    		left.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFNE, doFalse);

        	right.writeOut(bc, MODE_VALUE);
        	adapter.ifZCmp(Opcodes.IFNE, doFalse);

        	adapter.push(false);
        	adapter.visitJumpInsn(Opcodes.GOTO, end);
        	adapter.visitLabel(doFalse);

        	adapter.push(true);
        	adapter.visitLabel(end);
    	}
    	else if(operation==Factory.OP_BOOL_XOR) {
    		left.writeOut(bc, MODE_VALUE);
    		right.writeOut(bc, MODE_VALUE);
    		adapter.visitInsn(Opcodes.IXOR);
    	}
    	else if(operation==Factory.OP_BOOL_EQV) {

            left.writeOut(bc,MODE_VALUE);
            right.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_EQV_BV_BV);
    	}
    	else if(operation==Factory.OP_BOOL_IMP) {

            left.writeOut(bc,MODE_VALUE);
            right.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_IMP_BV_BV);
    	}
    	return Types.BOOLEAN_VALUE;
    	
    }
    

    
    
    
    
    
    private OpBool(Expression left, Expression right, int operation) {
        super(left.getFactory(),left.getStart(),right.getEnd());
        this.left=left.getFactory().toExprBoolean(left);
        this.right=left.getFactory().toExprBoolean(right);  
        this.operation=operation;
    }
    
    /**
     * Create a String expression from a Expression
     * @param left 
     * @param right 
     * 
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprBoolean toExprBoolean(Expression left, Expression right,int operation) {
        if(left instanceof Literal && right instanceof Literal) {
        	Boolean l=((Literal) left).getBoolean(null);
        	Boolean r=((Literal) right).getBoolean(null);
        	
        	
        	if(l!=null && r!=null) {
        		switch(operation) {
        		case Factory.OP_BOOL_AND:	return left.getFactory().createLitBoolean(l.booleanValue()&&r.booleanValue(),left.getStart(),right.getEnd());
        		case Factory.OP_BOOL_OR:	return left.getFactory().createLitBoolean(l.booleanValue()||r.booleanValue(),left.getStart(),right.getEnd());
        		case Factory.OP_BOOL_XOR:	return left.getFactory().createLitBoolean(l.booleanValue()^r.booleanValue(),left.getStart(),right.getEnd());
        		}
        	}
        }
        return new OpBool(left,right,operation);
    }
    
    public String toString(){
    	return left+" "+toStringOperation()+" "+right;
    }

	private String toStringOperation() {
		if(Factory.OP_BOOL_AND==operation)	return "and";
		if(Factory.OP_BOOL_OR==operation) 	return "or";
		if(Factory.OP_BOOL_XOR==operation) 	return "xor";
		if(Factory.OP_BOOL_EQV==operation) 	return "eqv";
		if(Factory.OP_BOOL_IMP==operation) 	return "imp";
		return operation+"";
	}
}

