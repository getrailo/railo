package railo.transformer.bytecode.statement;

import railo.runtime.exp.TemplateException;
import railo.transformer.Context;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;

/**
 * A single Statement
 */
public abstract class StatementBase implements Statement {

	private Position start;
	private Position end;
	private Statement parent;
	private int hasReturnChild=-1;
	private Factory factory;
	
	/**
     * constructor of the class
     * @param line
     */
    public StatementBase(Factory factory,Position start, Position end) {
       this.factory=factory;
    	this.start=start;
        this.end=end;
    }

	@Override
    public Statement getParent() {
		return parent;
	}

	@Override
    public Factory getFactory() {
		return factory;
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
    public final void writeOut(Context c) throws TransformerException {
    	BytecodeContext bc=(BytecodeContext) c;
    	ExpressionUtil.visitLine(bc, start);
        _writeOut(bc);
    	ExpressionUtil.visitLine(bc, end);
    	
    }
    

    /**
     * write out the stament to the adater
     * @param adapter
     * @throws TransformerException 
     */
    public abstract void _writeOut(BytecodeContext bc) throws TransformerException;



    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setStart(Position start) {
        this.start = start;
    }

    /**
     * sets the line value.
     * @param line The line to set.
     */
    public void setEnd(Position end) {
        this.end = end;
    }
    
    @Override
	public Position getStart() {
		return start;
	}
	
	@Override
	public Position getEnd() {
		return end;
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
