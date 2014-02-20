package railo.transformer.bytecode;

import railo.runtime.exp.TemplateException;
import railo.transformer.Context;
import railo.transformer.Factory;
import railo.transformer.bytecode.statement.FlowControlFinal;



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
     * @param c
     * @throws TemplateException
     */
    public void writeOut(Context c) throws BytecodeException;    

    
    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setStart(Position startLine);

    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setEnd(Position endLine);
    
    /**
	 * @return the startLine
	 */
	public Position getStart();
	
	/**
	 * @return the endLine
	 */
	public Position getEnd();
	

	/**
	 * @return return the label where the finally block of this tags starts, IF there is a finally block, otherwise return null; 
	 */
	public FlowControlFinal getFlowControlFinal();

	public Factory getFactory();
}
