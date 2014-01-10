package railo.transformer.bytecode.cast;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExprFloat;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitFloat;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * cast a Expression to a Double
 */
public final class CastFloat extends ExpressionBase implements ExprFloat,Cast {
    
    private Expression expr;
    
    private CastFloat(Expression expr) {
        super(expr.getStart(),expr.getEnd());
    	this.expr=expr;
    }
    
    /**
     * Create a String expression from a Expression
     * @param expr
     * @return String expression
     * @throws TemplateException 
     */
    public static ExprFloat toExprFloat(Expression expr)  {
        if(expr instanceof ExprFloat) return (ExprFloat) expr;
        if(expr instanceof Literal) {
            Double dbl = ((Literal)expr).getDouble(null);
            if(dbl!=null) return new LitFloat((float)dbl.doubleValue(),expr.getStart(),expr.getEnd());
        }
        return new CastFloat(expr);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {

    	GeneratorAdapter adapter = bc.getAdapter();

    	if(expr instanceof OpDouble) {
            ((OpDouble)expr).writeOutDouble(bc,MODE_VALUE);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE_FROM_DOUBLE);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_DOUBLE);
        }
    	else if(expr instanceof ExprBoolean) {
            expr.writeOut(bc,MODE_VALUE);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE_FROM_BOOLEAN);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_BOOLEAN);
            
        }
        else if(expr instanceof ExprFloat) {
            expr.writeOut(bc,mode);
        }
        else if(expr instanceof ExprDouble) {
            expr.writeOut(bc,MODE_VALUE);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE_FROM_DOUBLE);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_DOUBLE);
        }
        else if(expr instanceof ExprString) {
            expr.writeOut(bc,MODE_REF);
            if(mode==MODE_VALUE)adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE_FROM_STRING);
            else adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_STRING);
        }
        else {
        	Type rtn = expr.writeOut(bc,mode);
        	if(mode==MODE_VALUE) {
        		if(!Types.isPrimitiveType(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE);
        		}
        		else if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.cast(Types.DOUBLE_VALUE, Types.FLOAT_VALUE);
        		}
        		else if(Types.FLOAT_VALUE.equals(rtn))	{}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE_FROM_BOOLEAN);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_VALUE);
        		}
        		return Types.FLOAT_VALUE;
        	}
        	else if(Types.isPrimitiveType(rtn))	{
        		if(Types.DOUBLE_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_DOUBLE);
        		}
        		else if(Types.FLOAT_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_FLOAT);
        		}
        		else if(Types.BOOLEAN_VALUE.equals(rtn))	{
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT_FROM_BOOLEAN);
        		}
        		else {
        			adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtn),new Type[]{rtn}));
        			adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT);
        		}
        		return Types.FLOAT;
        	}
        	//else {
        	if(!Types.FLOAT.equals(rtn)) adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_FLOAT);
        	return Types.FLOAT;
        	//}
        }
        

        if(mode==MODE_VALUE)return Types.FLOAT_VALUE;
        return Types.FLOAT;
    }

	@Override
	public Expression getExpr() {
		return expr;
	}
}




