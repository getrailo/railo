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
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;

public final class TagTry extends TagBase {

	private static final ExprString ANY=LitString.toExprString("any", -1);

	private static final Method GET_VARIABLE = new Method(
			"getVariable",
			Types.OBJECT,
			new Type[]{Types.STRING});
	
	private static final Method TO_PAGE_EXCEPTION = new Method(
			"toPageException",
			Types.PAGE_EXCEPTION,
			new Type[]{Types.THROWABLE});
	
	// PageException setCatch(Throwable t)
	private static final Method SET_CATCH_T = new Method(
			"setCatch",
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

	

	public TagTry(int line) {
		super(line);
	}

	public TagTry(int sl,int el) {
		super(sl,el);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		Body tryBody=new BodyBase();
		List catches=new ArrayList();
		Tag _finally=null;

		tryBody.setParent(getBody().getParent());
		
		List statements = getBody().getStatements();
		Statement stat;
		Tag tag;
		Iterator it = statements.iterator();
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Catch"))	{
					catches.add(tag);
					continue;
				}
				else if(tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Finally"))	{
					_finally=tag;
					continue;
				}
			}
			tryBody.addStatement(stat);
		};
		
		
		
		
		TryCatchFinallyVisitor tcfv=new TryCatchFinallyVisitor();
		// Try
		tcfv.visitTryBegin(bc);
			tryBody.writeOut(bc);
		tcfv.visitTryEnd(bc);
		
		// Catch
		int e=tcfv.visitCatchBegin(bc, Types.THROWABLE);
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
	        adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
	        adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL, GET_CATCH);
			adapter.storeLocal(old);
			
			/*int obj=adapter.newLocal(Types.OBJECT);
	        adapter.loadArg(0);
	        adapter.push("cfcatch");
	        adapter.invokeVirtual(Types.PAGE_CONTEXT, GET_VARIABLE);
			adapter.storeLocal(obj);*/
			
	        
	        // PageException pe=Caster.toPageEception(e);
	        int pe=adapter.newLocal(Types.PAGE_EXCEPTION);
	        adapter.loadLocal(e);
	        adapter.invokeStatic(Types.CASTER, TO_PAGE_EXCEPTION);
			adapter.storeLocal(pe);
			/*
			adapter.loadArg(0);
	        adapter.loadLocal(e);
	        adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_CATCH_T);
			adapter.storeLocal(pe);
			*/
			
			
			it=catches.iterator();
			Attribute attrType;
			Expression type;
			Label endAllIfs=new Label();
			Tag tagElse=null;
			while(it.hasNext()) {
				tag=(Tag) it.next();
				Label endIf=new Label();
				attrType = tag.getAttribute("type");
				type=ANY;
				if(attrType!=null)type=attrType.getValue();

				if(type instanceof LitString && ((LitString)type).getString().equalsIgnoreCase("any")){
					tagElse=tag;
					continue;
				}
				
				ExpressionUtil.visitLine(bc, tag.getLine());
				
				// if(pe.typeEqual(@type)
				adapter.loadLocal(pe);
				type.writeOut(bc, Expression.MODE_REF);
				adapter.invokeVirtual(Types.PAGE_EXCEPTION, TYPE_EQUAL);
				
				adapter.ifZCmp(Opcodes.IFEQ, endIf);
					catchBody(bc,adapter,tag,pe,true);
					
	            adapter.visitJumpInsn(Opcodes.GOTO, endAllIfs);
	            
	            adapter.visitLabel(endIf);
				
				
			}
			// else 
			if(tagElse!=null){
				catchBody(bc, adapter, tagElse, pe, true);
			}
			else{
				// pc.setCatch(pe,true);
				adapter.loadArg(0);
		        adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
		        adapter.loadLocal(pe);
		        adapter.push(false);
		        adapter.push(true);
		        adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL, SET_CATCH3);
				
				//throw pe;
				adapter.loadLocal(pe);
				adapter.throwException();
			}
			adapter.visitLabel(endAllIfs);
			
		
		// PageExceptionImpl old=pc.getCatch();
        adapter.loadArg(0);
        adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
        adapter.loadLocal(old);
        adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL, SET_CATCH_PE);
			
		tcfv.visitCatchEnd(bc);
		
		
		
		// Finally
		tcfv.visitFinallyBegin(bc);
			// pc.clearCatch();
				//adapter.loadArg(0);
				//adapter.invokeVirtual(Types.PAGE_CONTEXT, CLEAR_CATCH);
		
			if(_finally!=null) {
				ExpressionUtil.visitLine(bc, _finally.getLine());
				_finally.getBody().writeOut(bc);
			}
		tcfv.visitFinallyEnd(bc);
	}

	private static void catchBody(BytecodeContext bc, GeneratorAdapter adapter,Tag tag, int pe,boolean caugth) throws BytecodeException {
		// pc.setCatch(pe,true);
		adapter.loadArg(0);
        adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
        adapter.loadLocal(pe);
        adapter.push(caugth);
        adapter.push(true);
        adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL, SET_CATCH3);
		tag.getBody().writeOut(bc);
    	
	}


}
