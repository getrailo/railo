package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.visitor.ConditionVisitor;

public final class Contition extends StatementBase implements HasBodies {
    
    private ArrayList ifs=new ArrayList();
    private Pair _else;

    /**
     * Constructor of the class
     * @param contition
     * @param body
     * @param line
     */
    public Contition(int line) {
        super(line);
    }
    
    /**
     * Constructor of the class
     * @param contition
     * @param body
     * @param line
     */
    public Contition(ExprBoolean contition, Statement body, int line) {
        super(line);
        addElseIf(contition,body,line);
        body.setParent(this);
    }
    
    public Contition(boolean b, Statement body, int line) {
		this(LitBoolean.toExprBoolean(b, -1),body,line);
	}

	/**
     * adds a else statment
     * @param contition
     * @param body
     */
    public void addElseIf(ExprBoolean contition, Statement body, int line) {
        ifs.add(new Pair(contition,body,line));
        body.setParent(this);
    }
    
    /**
     * sets the else Block of the Contition
     * @param body
     */
    public void setElse(Statement body, int line) {
    	_else=new Pair(null,body,line);
    	body.setParent(this);
    }
    
    public final class Pair {
        private ExprBoolean contition;
        private Statement body;
        private int line;

        public Pair(ExprBoolean contition, Statement body, int line) {
            this.contition=contition;
            this.body=body;
            this.line=line;
        }
    }
    
    

    public void _writeOut(BytecodeContext bc) throws BytecodeException {
    	Iterator it = ifs.iterator();
        Pair pair;
        ConditionVisitor cv=new ConditionVisitor();
        cv.visitBefore();
        	// ifs
        	while(it.hasNext()) {
        		pair=(Pair) it.next();
        		ExpressionUtil.visitLine(bc, pair.line);
        		cv.visitWhenBeforeExpr();
        			pair.contition.writeOut(bc,Expression.MODE_VALUE);
        		cv.visitWhenAfterExprBeforeBody(bc);
        			pair.body.writeOut(bc);
        		cv.visitWhenAfterBody(bc);
        	}
        	// else
        	if(_else!=null && _else.body!=null) {
        		cv.visitOtherviseBeforeBody();
        			_else.body.writeOut(bc);
        		cv.visitOtherviseAfterBody();
        	}
        	
        cv.visitAfter(bc);
    }

	/**
	 * @see railo.transformer.bytecode.statement.HasBodies#getBodies()
	 */
	public Body[] getBodies() {
		int len=ifs.size(),count=0;
		if(_else!=null)len++;
		Body[] bodies=new Body[len];
		Pair p;
		Iterator it = ifs.iterator();
		while(it.hasNext()) {
			p=(Pair) it.next();
			bodies[count++]=(Body) p.body;
		}
		if(_else!=null)bodies[count++]=(Body) _else.body;
		
		return bodies;
	}
}
