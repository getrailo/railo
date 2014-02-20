package railo.transformer.bytecode.literal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.literal.LitBoolean;

/**
 * Literal Boolean
 */
public final class LitBooleanImpl extends ExpressionBase implements LitBoolean,ExprBoolean {
    
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return b+"";
	}

	private boolean b;

    /**
     * constructor of the class
     * @param b 
     * @param line 
     */
    public LitBooleanImpl(Factory f,boolean b, Position start,Position end) {
        super(f,start,end);
        this.b=b;
    }

    /**
     * @return return value as double value
     */ 
    public double getDoubleValue() {
        return Caster.toDoubleValue(b);
    }
    
    /**
     * @return return value as Double Object
     */
    public Double getDouble() {
        return Caster.toDouble(b);
    }
    
    /**
     * @see railo.transformer.expression.literal.Literal#getString()
     */
    public String getString() {
        return Caster.toString(b);
    }
    
    /**
     * @return return value as a Boolean Object
     */
    public Boolean getBoolean() {
        return Caster.toBoolean(b);
    }
    
    /**
     * @return return value as a boolean value
     */
    public boolean getBooleanValue() {
        return b;
    }

    /**
     *
     * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
     */
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        
    	if(mode==MODE_REF) {
    		adapter.getStatic(Types.BOOLEAN, b?"TRUE":"FALSE", Types.BOOLEAN);
    		return Types.BOOLEAN;
    	}
    	adapter.visitInsn(b?Opcodes.ICONST_1:Opcodes.ICONST_0);
    	return Types.BOOLEAN_VALUE;
    }

    /**
     * @see railo.transformer.expression.literal.Literal#getDouble(java.lang.Double)
     */
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    /**
     * @see railo.transformer.expression.literal.Literal#getBoolean(java.lang.Boolean)
     */
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
