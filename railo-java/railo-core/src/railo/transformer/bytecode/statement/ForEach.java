package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.util.ForEachUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.expression.var.VariableRef;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;

public final class ForEach extends StatementBase implements FlowControlBreak,FlowControlContinue,HasBody {

	
	private Body body;
	private VariableRef key;
	private Variable value;

	private final static Method HAS_NEXT = 		new Method("hasNext",Types.BOOLEAN_VALUE,new Type[]{});
	private final static Method NEXT = 			new Method("next",Types.OBJECT,new Type[]{});
	private final static Method SET = 			new Method("set",Types.OBJECT,new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});
	public static final Method TO_ITERATOR = new Method("toIterator",Types.ITERATOR,new Type[]{Types.OBJECT});
	private static final Type FOR_EACH_UTIL = Type.getType(ForEachUtil.class);
	protected static final Method RESET = new Method("reset",Types.VOID,new Type[]{Types.ITERATOR});

    //private static final Type COLLECTION_UTIL = Type.getType(CollectionUtil.class);

	private Label begin = new Label();
	private Label end = new Label();
	private FlowControlFinal fcf;
	private String label;

	/**
	 * Constructor of the class
	 * @param key
	 * @param value
	 * @param body
	 * @param line
	 */
	public ForEach(Variable key,Variable value,Body body,Position start, Position end,String label) {
		super(start,end);
		this.key=new VariableRef(key);
		this.value=value;
		this.body=body;
		this.label=label;
		body.setParent(this);
		
	}
	
	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		final int it=adapter.newLocal(Types.ITERATOR);
		final int item=adapter.newLocal(Types.REFERENCE);
		
		//Value
			// ForEachUtil.toIterator(value)
			value.writeOut(bc, Expression.MODE_REF);
			adapter.invokeStatic(FOR_EACH_UTIL, TO_ITERATOR);
			//adapter.invokeStatic(COLLECTION_UTIL, TO_ITERATOR);
			// Iterator it=...
			adapter.storeLocal(it);
			TryFinallyVisitor tfv=new TryFinallyVisitor(new OnFinally() {
				
				@Override
				public void writeOut(BytecodeContext bc) throws BytecodeException {
					GeneratorAdapter a = bc.getAdapter();
					//if(fcf!=null && fcf.getAfterFinalGOTOLabel()!=null)ASMUtil.visitLabel(a,fcf.getFinalEntryLabel());
					a.loadLocal(it);
					a.invokeStatic(FOR_EACH_UTIL, RESET);
					/*if(fcf!=null){
						Label l=fcf.getAfterFinalGOTOLabel();
						if(l!=null)a.visitJumpInsn(Opcodes.GOTO, l);
					}*/
				}
			},getFlowControlFinal());
			tfv.visitTryBegin(bc);
			// Key
				// new VariableReference(...)
				key.writeOut(bc, Expression.MODE_REF);
				// VariableReference item=...
				adapter.storeLocal(item);
			
			// while
				ExpressionUtil.visitLine(bc, getStart());
				adapter.visitLabel(begin);
				
				// hasNext
				adapter.loadLocal(it);
				adapter.invokeInterface(Types.ITERATOR, HAS_NEXT);
				adapter.ifZCmp(Opcodes.IFEQ, end);
				
				// item.set(pc,it.next());
				adapter.loadLocal(item);
				adapter.loadArg(0);
				adapter.loadLocal(it);
				adapter.invokeInterface(Types.ITERATOR, NEXT);
				adapter.invokeInterface(Types.REFERENCE, SET);
				adapter.pop();
				
				// Body
				body.writeOut(bc);
				adapter.visitJumpInsn(Opcodes.GOTO, begin);
				adapter.visitLabel(end);
			tfv.visitTryEnd(bc);
		
	}

	@Override
	public Label getBreakLabel() {
		return end;
	}

	@Override
	public Label getContinueLabel() {
		return begin;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public FlowControlFinal getFlowControlFinal() {
		if(fcf==null) fcf=new FlowControlFinalImpl();
		return fcf;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
