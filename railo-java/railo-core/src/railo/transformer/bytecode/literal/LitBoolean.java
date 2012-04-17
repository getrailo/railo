package railo.transformer.bytecode.literal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

/**
 * Literal Boolean
 */
public final class LitBoolean extends ExpressionBase implements Literal,ExprBoolean {
    
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return b+"";
	}

	private boolean b;

	public static final LitBoolean TRUE=new LitBoolean(true,-1);
	public static final LitBoolean FALSE=new LitBoolean(false,-1);

	public static ExprBoolean toExprBoolean(boolean b, int line) {
		return new LitBoolean(b,line);
	}

	public static ExprBoolean toExprBoolean(boolean b) {
		return new LitBoolean(b,-1);
	}
	
    /**
     * constructor of the class
     * @param b 
     * @param line 
     */
    public LitBoolean(boolean b, int line) {
        super(line);
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
     * @see railo.transformer.bytecode.Literal#getString()
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

    /* *
     * @see railo.transformer.bytecode.expression.Expression#getType()
     * /
    public int getType() {
        return Types._BOOLEAN;
    }*/
}
