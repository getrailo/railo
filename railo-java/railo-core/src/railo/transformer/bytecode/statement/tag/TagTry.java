package railo.transformer.bytecode.statement.tag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.FlowControlFinalImpl;
import railo.transformer.bytecode.statement.FlowControlRetry;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;

public final class TagTry extends TagBase implements FlowControlRetry {

	private static final ExprString ANY=LitString.toExprString("any");

	private static final Method GET_VARIABLE = new Method(
			"getVariable",
			Types.OBJECT,
			new Type[]{Types.STRING});
	
	private static final Method TO_PAGE_EXCEPTION = new Method(
			"toPageException",
			Types.PAGE_EXCEPTION,
			new Type[]{Types.THROWABLE});
	
	
	public static final Method SET_CATCH_PE = new Method(
			"setCatch",
			Types.VOID,
			new Type[]{Types.PAGE_EXCEPTION});
	
	public static final Method SET_CATCH3 = new Method(
			"setCatch",
			Types.VOID,
			new Type[]{Types.PAGE_EXCEPTION,Types.BOOLEAN_VALUE,Types.BOOLEAN_VALUE});
	
	public static final Method GET_CATCH = new Method(
			"getCatch",
			Types.PAGE_EXCEPTION,
			new Type[]{});

	//  public boolean typeEqual(String type);
	private static final Method TYPE_EQUAL = new Method(
			"typeEqual",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.STRING});

	private FlowControlFinal fcf;

	private boolean checked;
	private Label begin = new Label();

	
	public TagTry(Position start,Position end) {
		super(start,end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.visitLabel(begin);
		Body tryBody=new BodyBase();
		List<Tag> catches=new ArrayList<Tag>();
		Tag tmpFinal=null;

		tryBody.setParent(getBody().getParent());
		
		List<Statement> statements = getBody().getStatements();
		Statement stat;
		Tag tag;
		{
		Iterator<Statement> it = statements.iterator();
		while(it.hasNext()) {
			stat= it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Catch"))	{
					catches.add(tag);
					continue;
				}
				else if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Finally"))	{
					tmpFinal=tag;
					continue;
				}
			}
			tryBody.addStatement(stat);
		};
		}
		final Tag _finally=tmpFinal;
		
		// has no try body, if there is no try body, no catches are executed, only finally 
		if(!tryBody.hasStatements()) {
			
			if(_finally!=null && _finally.getBody()!=null)_finally.getBody().writeOut(bc);
			return;
		}
		TryCatchFinallyVisitor tcfv=new TryCatchFinallyVisitor(new OnFinally() {
			
			public void writeOut(BytecodeContext bc) throws BytecodeException {
				/*GeneratorAdapter ga = bc.getAdapter();
				if(fcf!=null && fcf.getAfterFinalGOTOLabel()!=null)
					ASMUtil.visitLabel(ga,fcf.getFinalEntryLabel());
				*/
				if(_finally!=null) {
					
					ExpressionUtil.visitLine(bc, _finally.getStart());
					_finally.getBody().writeOut(bc);
					
				}
				/*if(fcf!=null){
					Label l=fcf.getAfterFinalGOTOLabel();
					if(l!=null)ga.visitJumpInsn(Opcodes.GOTO, l);
				}*/
			}
		},getFlowControlFinal());
		
		
		// Try
		tcfv.visitTryBegin(bc);
			tryBody.writeOut(bc);
		int e=tcfv.visitTryEndCatchBeging(bc);
			// if(e instanceof railo.runtime.exp.Abort) throw e;
			Label abortEnd=new Label();
			adapter.loadLocal(e);
			// Abort.isAbort(t);
			adapter.invokeStatic(Types.ABORT, TryCatchFinally.IS_ABORT);
			//adapter.instanceOf(Types.ABORT);
			
			
			
	        adapter.ifZCmp(Opcodes.IFEQ, abortEnd);
	        	adapter.loadLocal(e);
	        	adapter.throwException();
	        adapter.visitLabel(abortEnd);
		    

	        // PageExceptionImpl old=pc.getCatch();
	        int old=adapter.newLocal(Types.PAGE_EXCEPTION);
	        adapter.loadArg(0);
	        adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_CATCH);
			adapter.storeLocal(old);
			
	        // PageException pe=Caster.toPageEception(e);
	        int pe=adapter.newLocal(Types.PAGE_EXCEPTION);
	        adapter.loadLocal(e);
	        adapter.invokeStatic(Types.CASTER, TO_PAGE_EXCEPTION);
			adapter.storeLocal(pe);
			
			Iterator<Tag> it = catches.iterator();
			Attribute attrType;
			Expression type;
			Label endAllIfs=new Label();
			Tag tagElse=null;
			while(it.hasNext()) {
				tag=it.next();
				Label endIf=new Label();
				attrType = tag.getAttribute("type");
				type=ANY;
				if(attrType!=null)type=attrType.getValue();

				if(type instanceof LitString && ((LitString)type).getString().equalsIgnoreCase("any")){
					tagElse=tag;
					continue;
				}
				
				ExpressionUtil.visitLine(bc, tag.getStart());
				
				// if(pe.typeEqual(@type)
				adapter.loadLocal(pe);
				type.writeOut(bc, Expression.MODE_REF);
				adapter.invokeVirtual(Types.PAGE_EXCEPTION, TYPE_EQUAL);
				
				adapter.ifZCmp(Opcodes.IFEQ, endIf);
					catchBody(bc,adapter,tag,pe,true,true);
					
	            adapter.visitJumpInsn(Opcodes.GOTO, endAllIfs);
	            
	            adapter.visitLabel(endIf);
				
				
			}
			// else 
			if(tagElse!=null){
				catchBody(bc, adapter, tagElse, pe, true,true);
			}
			else{
				// pc.setCatch(pe,true);
				adapter.loadArg(0);
		        adapter.loadLocal(pe);
		        adapter.push(false);
		        adapter.push(true);
		        adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_CATCH3);
				
				//throw pe;
				adapter.loadLocal(pe);
				adapter.throwException();
			}
			adapter.visitLabel(endAllIfs);
			
		
		// PageExceptionImpl old=pc.getCatch();
        adapter.loadArg(0);
        adapter.loadLocal(old);
        adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_CATCH_PE);
			
		tcfv.visitCatchEnd(bc);
	}
	

	private static void catchBody(BytecodeContext bc, GeneratorAdapter adapter,Tag tag, int pe,boolean caugth, boolean store) throws BytecodeException {
		// pc.setCatch(pe,true);
		adapter.loadArg(0);
        adapter.loadLocal(pe);
        adapter.push(caugth);
        adapter.push(store);
        adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_CATCH3);
		tag.getBody().writeOut(bc);
    	
	}
	
	private boolean hasFinally(){
		List<Statement> statements = getBody().getStatements();
		Statement stat;
		Tag tag;
		Iterator<Statement> it = statements.iterator();
		while(it.hasNext()) {
			stat= it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Finally"))	{
					return true;
				}
			}
		}
		return false;
	}
	

	@Override
	public FlowControlFinal getFlowControlFinal() {
		if(!checked) {
			checked=true;
			if(!hasFinally()) return null;
			fcf=new FlowControlFinalImpl();
		}
			
		return fcf;
	}

	@Override
	public Label getRetryLabel() {
		return begin;
	}

	@Override
	public String getLabel() {
		return null;
	}


}
