

package railo.transformer.bytecode;

import railo.runtime.exp.TemplateException;



/**
 * A single Statement
 */
public interface Statement {
	
    /**
     * sets parent statement to statement
     * @param parent
     */
    public void setParent(Statement parent);

    public boolean hasFlowController();
    public void setHasFlowController(boolean has);
    
    /**
     * @return returns the parent statement
     */
    public Statement getParent();
    
    /**
     * write out the stament to adapter
     * @param adapter
     * @throws TemplateException
     */
    public void writeOut(BytecodeContext bc) throws BytecodeException;    

    /**
     * Returns the value of line.
     * @return value line
     */
    public int getLine();
}
