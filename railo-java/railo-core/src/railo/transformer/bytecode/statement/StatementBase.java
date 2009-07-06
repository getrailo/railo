package railo.transformer.bytecode.statement;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;

/**
 * A single Statement
 */
public abstract class StatementBase implements Statement {


	private int startLine;
	private int endLine;
	private Statement parent;
	private int hasReturnChild=-1;
	
	/**
     * constructor of the class
     * @param line
     */
    public StatementBase(int startLine, int endLine) {
        this.startLine=startLine;
        this.endLine=endLine;
    }
    public StatementBase(int startLine) {
        this.startLine=startLine;
        this.endLine=-1;
    }
    
    /**
	 * @see railo.transformer.bytecode.Statement#getParent()
	 */
	public Statement getParent() {
		return parent;
	}


	/**
	 * @see railo.transformer.bytecode.Statement#setParent(railo.transformer.bytecode.Statement)
	 */
	public void setParent(Statement parent) {
		this.parent=parent;
		if(hasReturnChild!=-1 && parent!=null)
			parent.setHasFlowController(hasReturnChild==1);
	}


	/**
     * write out the stament to adapter
     * @param adapter
     * @throws TemplateException
     */
    public final void writeOut(BytecodeContext bc) throws BytecodeException {
    	ExpressionUtil.visitLine(bc, startLine);
        _writeOut(bc);
    	ExpressionUtil.visitLine(bc, endLine);
    }
    

    /**
     * write out the stament to the adater
     * @param adapter
     * @throws BytecodeException 
     */
    public abstract void _writeOut(BytecodeContext bc) throws BytecodeException;


    /**
     * Returns the value of line.
     * @return value line
     */
    public int getLine() {
        return startLine;
    }


    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    /**
     * sets the line value.
     * @param line The line to set.
     * @deprecated replaced with "setStartLine"
     */
    public void setLine(int startLine) {
        setStartLine(startLine);
    }

    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
	/**
	 * @return the startLine
	 */
	public int getStartLine() {
		return startLine;
	}
	/**
	 * @return the endLine
	 */
	public int getEndLine() {
		return endLine;
	}
    
    /**
	 * @see railo.transformer.bytecode.Statement#getDescendantCount()
	 */
	public final int getDescendantCount() {
		return 0;
	}

    /**
	 *
	 * @see railo.transformer.bytecode.Statement#hasFlowController()
	 */
	public boolean hasFlowController() {
		return hasReturnChild==1;
	}

	/**
	 * @param hasReturnChild the hasReturnChild to set
	 */
	public void setHasFlowController(boolean hasReturnChild) {
		if(parent!=null)parent.setHasFlowController(hasReturnChild);
		this.hasReturnChild = hasReturnChild?1:0;
	}

}
