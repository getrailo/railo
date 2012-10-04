package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class Switch extends StatementBase implements FlowControlBreak,HasBodies {
    
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
	
	private ArrayList cases=new ArrayList();
    private Body defaultCase;
	private Expression expr;

	private NativeSwitch ns;


    public Switch(Expression expr,Position start, Position end) {
		super(start, end);
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

	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		
	// Array cases=new ArrayImpl();
		int array=adapter.newLocal(Types.ARRAY);
		adapter.newInstance(ARRAY_IMPL);
		adapter.dup();
		adapter.invokeConstructor(ARRAY_IMPL, INIT);
		
		adapter.storeLocal(array);
		
	// cases.append(case.value);
		Iterator it = cases.iterator();
		Case c;
		while(it.hasNext()) {
			c=(Case) it.next();
			

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
		ns=new NativeSwitch(result,NativeSwitch.LOCAL_REF,getStart(),getEnd());
		it = cases.iterator();
		int count=1;
		while(it.hasNext()) {
			c=(Case) it.next();
			ns.addCase(count++, c.body,c.startPos,c.endPos,false);
		}
		if(defaultCase!=null)ns.addDefaultCase(defaultCase);
		
		ns.writeOut(bc);

	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
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
	public Body[] getBodies() {
		if(cases==null) {
			if(defaultCase!=null) return new Body[]{defaultCase};
			return new Body[]{};
		}
		
		int len=cases.size(),count=0;
		if(defaultCase!=null)len++;
		Body[] bodies=new Body[len];
		Case c;
		Iterator it = cases.iterator();
		while(it.hasNext()) {
			c=(Case) it.next();
			bodies[count++]=c.body;
		}
		if(defaultCase!=null)bodies[count++]=defaultCase;
		
		return bodies;
	}

}
