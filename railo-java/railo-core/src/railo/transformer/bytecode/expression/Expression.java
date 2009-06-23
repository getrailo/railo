

package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;


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
    public Type writeOut(BytecodeContext bc, int mode) throws BytecodeException;

    /**
     * Returns the value of line.
     * @return value line
     */
    public int getLine();
    
    /* *T ODO entfernen/ersetzten durch return type von writeOut();
     * @return return type as String (Types.XYZ)
     */
    //public char getType();


	public void setLine(int l);
}
