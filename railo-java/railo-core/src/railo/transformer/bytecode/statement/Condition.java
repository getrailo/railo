package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.visitor.ConditionVisitor;

public final class Condition extends StatementBase implements HasBodies {
    
    private ArrayList ifs=new ArrayList();
    private Pair _else;

    /**
     * Constructor of the class
     * @param condition
     * @param body
     * @param line
     */
    public Condition(int line) {
        super(line);
    }
    
    /**
     * Constructor of the class
     * @param condition
     * @param body
     * @param line
     */
    public Condition(ExprBoolean condition, Statement body, int line) {
        super(line);
        addElseIf(condition,body,line);
        body.setParent(this);
    }
    
    public Condition(boolean b, Statement body, int line) {
		this(LitBoolean.toExprBoolean(b, -1),body,line);
	}

	/**
     * adds a else statment
     * @param condition
     * @param body
     */
    public void addElseIf(ExprBoolean condition, Statement body, int line) {
        ifs.add(new Pair(condition,body,line));
        body.setParent(this);
    }
    
    /**
     * sets the else Block of the condition
     * @param body
     */
    public void setElse(Statement body, int line) {
    	_else=new Pair(null,body,line);
    	body.setParent(this);
    }
    
    public final class Pair {
        private ExprBoolean condition;
        private Statement body;
        private int line;

        public Pair(ExprBoolean condition, Statement body, int line) {
            this.condition=condition;
            this.body=body;
            this.line=line;
        }
    }
    
    

    public void _writeOut(BytecodeContext bc) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
        Iterator it = ifs.iterator();
        Pair pair;
        ConditionVisitor cv=new ConditionVisitor();
        cv.visitBefore();
        	// ifs
        	while(it.hasNext()) {
        		pair=(Pair) it.next();
        		ExpressionUtil.visitLine(bc, pair.line);
        		cv.visitWhenBeforeExpr();
        			pair. condition.writeOut(bc,Expression.MODE_VALUE);
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
