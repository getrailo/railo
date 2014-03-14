package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.visitor.ConditionVisitor;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class Condition extends StatementBaseNoFinal implements HasBodies {
    
    private ArrayList<Pair> ifs=new ArrayList<Pair>();
    private Pair _else;

    /**
     * Constructor of the class
     * @param condition
     * @param body
     * @param line
     */
    public Condition(Factory f, Position start,Position end) {
        super(f,start,end);
    }
    
    /**
     * Constructor of the class
     * @param condition
     * @param body
     * @param line
     */
    public Condition(Factory f, ExprBoolean condition, Statement body, Position start,Position end) {
        super(condition.getFactory(),start,end);
        addElseIf(condition,body,start,end);
        
        body.setParent(this);
    }
    
    public Condition(boolean b, Statement body, Position start,Position end) {
		this(body.getFactory(),body.getFactory().createLitBoolean(b),body,start,end);
	}

	/**
     * adds a else statement
     * @param condition
     * @param body
     */
    public Pair addElseIf(ExprBoolean condition, Statement body, Position start,Position end) {
    	Pair pair;
    	ifs.add(pair=new Pair(condition,body,start,end));
        body.setParent(this);
        return pair;
    }
    
    /**
     * sets the else Block of the condition
     * @param body
     */
    public Pair setElse(Statement body, Position start,Position end) {
    	_else=new Pair(null,body,start,end);
    	body.setParent(this);
    	return _else;
    }
    
    public final class Pair {
        private ExprBoolean condition;
        private Statement body;
        private Position start;
        public Position end;

        public Pair(ExprBoolean condition, Statement body, Position start,Position end) {
            this.condition=condition;
            this.body=body;
            this.start=start;
            this.end=end;
        }
    }
    
    

    public void _writeOut(BytecodeContext bc) throws TransformerException {
    	Iterator<Pair> it = ifs.iterator();
        Pair pair;
        ConditionVisitor cv=new ConditionVisitor();
        cv.visitBefore();
        	// ifs
        	while(it.hasNext()) {
        		pair=it.next();
        		ExpressionUtil.visitLine(bc, pair.start);
        		cv.visitWhenBeforeExpr();
        			pair. condition.writeOut(bc,Expression.MODE_VALUE);
        		cv.visitWhenAfterExprBeforeBody(bc);
        			pair.body.writeOut(bc);
        		cv.visitWhenAfterBody(bc);
        		if(pair.end!=null)ExpressionUtil.visitLine(bc, pair.end);
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
		Iterator<Pair> it = ifs.iterator();
		while(it.hasNext()) {
			p=it.next();
			bodies[count++]=(Body) p.body;
		}
		if(_else!=null)bodies[count++]=(Body) _else.body;
		
		return bodies;
	}
}
