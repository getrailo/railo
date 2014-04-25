package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprInt;
import railo.transformer.expression.literal.LitInteger;

/**
 * Literal Double Value
 */
public final class LitIntegerImpl extends ExpressionBase implements LitInteger,ExprInt {
    
    private int i;
    
    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitIntegerImpl(Factory f,int i, Position start,Position end) {
        super(f,start,end);
        this.i=i;
    }

	/**
     * @return return value as int
     */ 
    @Override
	public int geIntValue() {
        return i;
    }
    
    /**
     * @return return value as Double Object
     */
    @Override
	public Integer getInteger() {
        return new Integer(i);
    }
    
    /**
     * @see railo.transformer.expression.literal.Literal#getString()
     */
    @Override
	public String getString() {
        return Caster.toString(i);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(i);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return Caster.toBooleanValue(i);
    }

    /**
     * @see railo.transformer.expression.Expression#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    @Override
	public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        adapter.push(i);
        if(mode==MODE_REF) {
            adapter.invokeStatic(Types.CASTER,Methods.METHOD_TO_INTEGER_FROM_INT);
            return Types.INTEGER;
        }
        return Types.INT_VALUE;
    }

    /**
     * @see railo.transformer.expression.literal.Literal#getDouble(java.lang.Double)
     */
    @Override
	public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    private Double getDouble() {
		return new Double(i);
	}

	/**
     * @see railo.transformer.expression.literal.Literal#getBoolean(java.lang.Boolean)
     */
    @Override
	public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}