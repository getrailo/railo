package railo.transformer.expression;

import org.objectweb.asm.Type;

import railo.runtime.exp.TemplateException;
import railo.transformer.Context;
import railo.transformer.Factory;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;


/**
 * A Expression (Operation, Literal aso.)
 */
public interface Expression {

    /**
     * Field <code>MODE_REF</code>
     */
    public static final int MODE_REF=0;
    /**
     * Field <code>MODE_VALUE</code>
     */
    public static final int MODE_VALUE=1;
    
    /**
     * write out the stament to adapter
     * @param adapter
     * @param mode 
     * @return return Type of expression
     * @throws TemplateException
     */
    public Type writeOut(Context bc, int mode) throws BytecodeException;

    public Position getStart();

    public Position getEnd();

    public void setStart(Position start);

    public void setEnd(Position end);
    
	public Factory getFactory();
}
