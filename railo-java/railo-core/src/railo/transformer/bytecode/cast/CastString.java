package railo.transformer.bytecode.cast;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.ExprDouble;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.Literal;

/**
 * Cast to a String
 */
public final class CastString extends ExpressionBase implements ExprString,Cast {
    
    private Expression expr;

    /**
     * constructor of the class
     * @param expr
     */
    private CastString(Expression expr) {
        super(expr.getFactory(),expr.getStart(),expr.getEnd());
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
        if(expr instanceof Literal) return expr.getFactory().createLitString(((Literal)expr).getString(),expr.getStart(),expr.getEnd());
        return new CastString(expr);
    }

    /**
     * @see railo.transformer.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {

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

	@Override
	public Expression getExpr() {
		return expr;
	}

}
