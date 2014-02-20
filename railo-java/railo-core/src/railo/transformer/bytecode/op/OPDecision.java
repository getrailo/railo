package railo.transformer.bytecode.op;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Methods_Operator;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class OPDecision extends ExpressionBase implements ExprBoolean {
    
    private Expression right;
    private Expression left;
    private int operation;

    public static final int LT=GeneratorAdapter.LT;
    public static final int LTE=GeneratorAdapter.LE;
    public static final int GTE=GeneratorAdapter.GE;
    public static final int GT=GeneratorAdapter.GT;
    public static final int EQ=GeneratorAdapter.EQ;
    public static final int NEQ=GeneratorAdapter.NE; 
    public static final int CT = 1000;
    public static final int NCT = 1001;
    public static final int EEQ = 1002;
    public static final int NEEQ = 1003;
    // int compare (Object, Object)
    final public static Method METHOD_COMPARE = new Method("compare",
			Types.INT_VALUE,
			new Type[]{Types.OBJECT,Types.OBJECT});
	
    private OPDecision(Expression left, Expression right, int operation) {
        super(left.getFactory(),left.getStart(),right.getEnd());
        this.left=left;
        this.right=right;  
        this.operation=operation;
    }
    
    /**
     * Create a String expression from a operation
     * @param left 
     * @param right 
     * 
     * @return String expression
     */
    public static ExprBoolean toExprBoolean(Expression left, Expression right, int operation) {
        return new OPDecision(left,right,operation);
    }
    
    
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        if(mode==MODE_REF) {
            _writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_BOOLEAN_FROM_BOOLEAN);
            return Types.BOOLEAN;
        }

        if(operation==CT)	{
            left.writeOut(bc,MODE_REF);
            right.writeOut(bc,MODE_REF);
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_CT);
        }
        else if(operation==NCT)	{
            left.writeOut(bc,MODE_REF);
            right.writeOut(bc,MODE_REF);
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_NCT);
        }
        else if(operation==EEQ)	{
            left.writeOut(bc,MODE_REF);
            right.writeOut(bc,MODE_REF);
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_EEQ);
        }
        else if(operation==NEEQ)	{
            left.writeOut(bc,MODE_REF);
            right.writeOut(bc,MODE_REF);
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_NEEQ);
        }
        else {
            int iLeft = Types.getType(left.writeOut(bc,MODE_VALUE));
            int iRight = Types.getType(right.writeOut(bc,MODE_VALUE));
            
            adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATORS[iLeft][iRight]);
            
	    
	        adapter.visitInsn(Opcodes.ICONST_0);
	        
	        Label l1 = new Label();
	        Label l2 = new Label();
	        adapter.ifCmp(Type.INT_TYPE,operation,l1);
	        //adapter.visitJumpInsn(Opcodes.IF_ICMPEQ, l1);
	        adapter.visitInsn(Opcodes.ICONST_0);
	        adapter.visitJumpInsn(Opcodes.GOTO, l2);
	        adapter.visitLabel(l1);
	        adapter.visitInsn(Opcodes.ICONST_1);
	        adapter.visitLabel(l2);
        }
        return Types.BOOLEAN_VALUE;
    }

}
