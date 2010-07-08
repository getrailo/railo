package railo.transformer.bytecode.op;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.cast.CastDouble;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Methods_Operator;
import railo.transformer.bytecode.util.Types;

public final class OpDouble extends ExpressionBase implements ExprDouble {

    private ExprDouble left;
    private ExprDouble right;
    private int operation;


    public static final int PLUS=GeneratorAdapter.ADD;
    public static final int MINUS=GeneratorAdapter.SUB;
    public static final int MODULUS=GeneratorAdapter.REM;
    public static final int DIVIDE=GeneratorAdapter.DIV;
    public static final int MULTIPLY=GeneratorAdapter.MUL;
	public static final int EXP = 2000;
	public static final int INTDIV = 2001;
    // TODO weitere operatoren
    
    private OpDouble(Expression left, Expression right, int operation)  {
        super(left.getLine());
        this.left=	CastDouble.toExprDouble(left);
        this.right=	CastDouble.toExprDouble(right);   
        this.operation=operation;
    }
    
    /**
     * Create a String expression from a Expression
     * @param left 
     * @param right 
     * @param operation 
     * 
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprDouble toExprDouble(Expression left, Expression right,int operation)  {
    	
        if(left instanceof Literal && right instanceof Literal) {
        	Double l = ((Literal)left).getDouble(null);
            Double r = ((Literal)right).getDouble(null);
        	
            if(l!=null && r !=null) {
                switch(operation) {
                case PLUS: return toLitDouble(		l.doubleValue()+r.doubleValue(),left.getLine());
                case MINUS: return toLitDouble(		l.doubleValue()-r.doubleValue(),left.getLine());
                case MODULUS: return toLitDouble(	l.doubleValue()%r.doubleValue(),left.getLine());
                case DIVIDE: {
                	if(r.doubleValue()!=0d)
                		return toLitDouble(	l.doubleValue()/r.doubleValue(),left.getLine());
                	break;
                }
                case MULTIPLY: return toLitDouble(	l.doubleValue()*r.doubleValue(),left.getLine());
                case EXP: return new LitDouble(Operator.exponent(l.doubleValue(),r.doubleValue()),left.getLine());
                case INTDIV: return new LitDouble(l.intValue()/r.intValue(),left.getLine());
                
                }
            }
        }
        return new OpDouble(left,right,operation);
    }
    
    
    private static ExprDouble toLitDouble(double d, int line) {
    	return new LitDouble(Caster.toDoubleValue(Caster.toString(d),0),line);
	}

	/**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	writeOutDouble(bc);
        if(mode==MODE_REF) {
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_DOUBLE);
            return Types.DOUBLE;
        }
        //adapter.cast(Types.FLOAT_VALUE, Types.DOUBLE_VALUE);
        return Types.DOUBLE_VALUE;
    }

    public void writeOutDouble(BytecodeContext bc) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        left.writeOut(bc,MODE_VALUE);
        right.writeOut(bc,MODE_VALUE);
        if(operation==EXP) {
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_EXP_DOUBLE);
        }
        else if(operation==DIVIDE) {
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_DIV);
        }
        else if(operation==INTDIV) {
        	adapter.invokeStatic(Types.OPERATOR,Methods_Operator.OPERATOR_INTDIV_DOUBLE);
        }
        else {
        	adapter.math(operation,Type.DOUBLE_TYPE);
        }
    }
}
