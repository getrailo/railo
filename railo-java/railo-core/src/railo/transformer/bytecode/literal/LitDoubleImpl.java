package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.commons.color.ConstantsDouble;
import railo.runtime.op.Caster;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Methods;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprDouble;
import railo.transformer.expression.literal.LitDouble;

/**
 * Literal Double Value
 */
public final class LitDoubleImpl extends ExpressionBase implements LitDouble,ExprDouble {

	//public static final LitDouble ZERO=new LitDouble(0,null,null);
	
    private double d;
    
    /**
     * constructor of the class
     * @param d
     * @param line 
     */
	public LitDoubleImpl(Factory f,double d, Position start,Position end) {
        super(f,start,end);
        
        this.d=d;
    }

	/**
     * @return return value as double value
     */ 
    @Override
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
     * @see railo.transformer.expression.literal.Literal#getString()
     */
    @Override
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

    @Override
    public Type _writeOut(BytecodeContext bc, int mode) {
    	GeneratorAdapter adapter = bc.getAdapter();
        if(mode==MODE_REF) {
        	String str=ConstantsDouble.getFieldName(d);
        	if(str!=null) {
				bc.getAdapter().getStatic(Types.CONSTANTS_DOUBLE, str, Types.DOUBLE);
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

    @Override
    public Double getDouble(Double defaultValue) {
        return getDouble();
    }

    @Override
    public Boolean getBoolean(Boolean defaultValue) {
        return getBoolean();
    }
}
