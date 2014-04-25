package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.Expression;

public final class Switch extends StatementBaseNoFinal implements FlowControlBreak,HasBodies {
    
	private static final Type ARRAY_IMPL=Type.getType(railo.runtime.type.ArrayImpl.class);

	// Object append(Object o)
	private static final Method APPEND = new Method(
											"append",
											Types.OBJECT,
											new Type[]{Types.OBJECT}
	);

	private static final Method INIT = new Method(
			"<init>",
			Types.VOID,
			new Type[]{}
    		);

	// int find(Array array, Object object)
	private static final Method FIND = new Method(
			"find",
			Types.INT_VALUE,
			new Type[]{Types.ARRAY,Types.OBJECT}
    		);	
	
	private List<Case> cases=new ArrayList<Case>();
    private Body defaultCase;
	private Expression expr;

	private NativeSwitch ns;


    public Switch(Expression expr,Position start, Position end) {
		super(expr.getFactory(),start, end);
		this.expr=expr;
	}
    
    public void addCase(Expression expr, Body body) {
        addCase(expr, body, null, null);
    }
    public void addCase(Expression expr, Body body,Position start,Position end) {
        //if(cases==null) cases=new ArrayList();
        cases.add(new Case(expr,body,start,end));
        body.setParent(this);
    }
    public void setDefaultCase(Body body) {
        defaultCase=body;
        body.setParent(this);
    }

    
    public final class Case {
        private Expression expression;
        private Body body;
		private Position startPos;
		private Position endPos;

        public Case(Expression expression, Body body,Position start,Position end) {
            this.expression=expression;
            this.body=body;
            this.startPos=start;
            this.endPos=end;
        }
    }

    @Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();
		
	// Array cases=new ArrayImpl();
		int array=adapter.newLocal(Types.ARRAY);
		adapter.newInstance(ARRAY_IMPL);
		adapter.dup();
		adapter.invokeConstructor(ARRAY_IMPL, INIT);
		
		adapter.storeLocal(array);
		
	// cases.append(case.value);
		Iterator<Case> it = cases.iterator();
		Case c;
		while(it.hasNext()) {
			c=it.next();
			

			adapter.loadLocal(array);
			c.expression.writeOut(bc, Expression.MODE_REF);
			adapter.invokeVirtual(ARRAY_IMPL, APPEND);
			adapter.pop();
		}
		
		// int result=ArrayUtil.find(array,expression);
		int result=adapter.newLocal(Types.INT_VALUE);
		adapter.loadLocal(array);
		expr.writeOut(bc, Expression.MODE_REF);
		adapter.invokeStatic(Types.ARRAY_UTIL, FIND);
		adapter.storeLocal(result);
		
		// switch(result)
		ns=new NativeSwitch(bc.getFactory(),result,NativeSwitch.LOCAL_REF,getStart(),getEnd());
		it = cases.iterator();
		int count=1;
		while(it.hasNext()) {
			c=it.next();
			ns.addCase(count++, c.body,c.startPos,c.endPos,false);
		}
		if(defaultCase!=null)ns.addDefaultCase(defaultCase);
		
		ns.writeOut(bc);

	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	@Override
	public Label getBreakLabel() {
		return ns.getBreakLabel();
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return ns.getContinueLabel();
	}


	/**
	 * @see railo.transformer.bytecode.statement.HasBodies#getBodies()
	 */
	@Override
	public Body[] getBodies() {
		if(cases==null) {
			if(defaultCase!=null) return new Body[]{defaultCase};
			return new Body[]{};
		}
		
		int len=cases.size(),count=0;
		if(defaultCase!=null)len++;
		Body[] bodies=new Body[len];
		Case c;
		Iterator<Case> it = cases.iterator();
		while(it.hasNext()) {
			c=it.next();
			bodies[count++]=c.body;
		}
		if(defaultCase!=null)bodies[count++]=defaultCase;
		
		return bodies;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
