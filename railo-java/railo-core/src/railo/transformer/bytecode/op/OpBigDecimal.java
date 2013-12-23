package railo.transformer.bytecode.op;

import java.math.BigDecimal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public final class OpBigDecimal extends ExpressionBase {

	private static final Method TO_BIG_DECIMAL = new Method("toBigDecimal",Types.BIG_DECIMAL,new Type[]{Types.OBJECT});
	
	private static final Method _ADD = new Method("add",Types.BIG_DECIMAL,new Type[]{Types.BIG_DECIMAL});
	private static final Method _SUBSTRACT = new Method("subtract",Types.BIG_DECIMAL,new Type[]{Types.BIG_DECIMAL});
	private static final Method _DIVIDE= new Method("divide",Types.BIG_DECIMAL,new Type[]{Types.BIG_DECIMAL,Types.INT_VALUE,Types.INT_VALUE});
	private static final Method _MULTIPLY= new Method("multiply",Types.BIG_DECIMAL,new Type[]{Types.BIG_DECIMAL});
	private static final Method _REMAINER= new Method("remainder",Types.BIG_DECIMAL,new Type[]{Types.BIG_DECIMAL});
	
    private int operation;
	private Expression left;
	private Expression right;

    public OpBigDecimal(Expression left, Expression right, int operation)  {
        super(left.getStart(),right.getEnd());
        this.left=	left;
        this.right=	right;   
        this.operation=operation;
    }
    

	/**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
    	return writeOutDouble(bc, mode) ;
    }
    
    public Type writeOutDouble(BytecodeContext bc, int mode) throws BytecodeException {

        if(operation==OpDouble.EXP) {
        	return new OpDouble(left, right, operation).writeOutDouble(bc, mode);
        }
    	
    	GeneratorAdapter adapter = bc.getAdapter();
    	
    	
    	toBigDecimal(bc,left);
    	toBigDecimal(bc,right);
    	
    	
    	//Caster.toBigDecimal("1").add(Caster.toBigDecimal("1"));
        if(operation==OpDouble.PLUS) {
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _ADD);
        }
        else if(operation==OpDouble.MINUS) {
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _SUBSTRACT);
        }
        else if(operation==OpDouble.DIVIDE) {
        	adapter.push(34);
        	adapter.push( BigDecimal.ROUND_HALF_EVEN);
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _DIVIDE);
        }
        else if(operation==OpDouble.INTDIV) {
        	adapter.push(0);
        	adapter.push( BigDecimal.ROUND_DOWN);
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _DIVIDE);
        }
        else if(operation==OpDouble.MULTIPLY) {
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _MULTIPLY);
        }
        
        else if(operation==OpDouble.MODULUS) {
        	adapter.invokeVirtual(Types.BIG_DECIMAL, _REMAINER);
        }
        return Types.BIG_DECIMAL;
    }
    


	private static void toBigDecimal(BytecodeContext bc, Expression expr) throws BytecodeException {
		expr.writeOut(bc,MODE_REF);
    	if(expr instanceof OpBigDecimal) return;
    	bc.getAdapter().invokeStatic(Types.CASTER,TO_BIG_DECIMAL);
	}


	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}


}