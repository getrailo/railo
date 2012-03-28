package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.commons.color.ConstantsDouble;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;

/**
 * Literal Double Value
 */
public final class LitDouble extends ExpressionBase implements Literal,ExprDouble {

	private static final Type CONSTANTS_DOUBLE = Type.getType(ConstantsDouble.class);
	public static final LitDouble ZERO=new LitDouble(0,-1);
	
    private double d;

	public static ExprDouble toExprDouble(double d, int line) {
		return new LitDouble(d,line);
	}
    
    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitDouble(double d, int line) {
        super(line);
        
        this.d=d;
    }

	/**
     * @return return value as double value
     */ 
    public double getDoubleValue() {
        return d;
    }
    
    /**
     * @return return value as Double Object
     */
    public Double getDouble() {
        return new Double(d);
    }
    
    /**
     * @see railo.transformer.bytecode.Literal#getString()
     */
    public String getString() {
        return Caster.toString(d);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(d);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return Caster.toBooleanValue(d);
    }

    /**
     * @see railo.transformer.bytecode.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        if(mode==MODE_REF) {
        	String str=ConstantsDouble.getFieldName(d);
        	if(str!=null) {
				bc.getAdapter().getStatic(CONSTANTS_DOUBLE, str, Types.DOUBLE);
			}
			else {
				adapter.push(d);
		        adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_DOUBLE_FROM_DOUBLE);
			}
            return Types.DOUBLE;
        }
        adapter.push(d);
        
        return Types.DOUBLE_VALUE;
    }

    /**
     * @see railo.transformer.bytecode.Literal#getDouble(java.lang.Double)
     */
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    /**
     * @see railo.transformer.bytecode.Literal#getBoolean(java.lang.Boolean)
     */
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
