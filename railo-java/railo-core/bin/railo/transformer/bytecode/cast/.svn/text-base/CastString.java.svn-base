package railo.transformer.bytecode.cast;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * Cast to a String
 */
public final class CastString extends ExpressionBase implements ExprString {
    
    private Expression expr;

    /**
     * constructor of the class
     * @param expr
     */
    private CastString(Expression expr) {
        super(expr.getLine());
        this.expr=expr;
    }
    
    /**
     * Create a String expression from a Expression
     * @param expr
     * @param pos 
     * @return String expression
     */
    public static ExprString toExprString(Expression expr) {
        if(expr instanceof ExprString) return (ExprString) expr;
        if(expr instanceof Literal) return new LitString(((Literal)expr).getString(),expr.getLine());
        return new CastString(expr);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {

    	GeneratorAdapter adapter = bc.getAdapter();
    	if(expr instanceof ExprBoolean) {
            expr.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_STRING_FROM_BOOLEAN);
        }
        else if(expr instanceof ExprDouble) {
            expr.writeOut(bc,MODE_VALUE);
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_STRING_FROM_DOUBLE);
        }
        else {
            Type rtn = expr.writeOut(bc,MODE_REF);
            if(rtn.equals(Types.STRING)) return Types.STRING;
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_STRING);
        }

        return Types.STRING;
    }

	/**
	 * @return the expr
	 */
	public Expression getExpr() {
		return expr;
	}

}
