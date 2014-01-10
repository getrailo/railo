package railo.transformer.bytecode.statement.tag;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.IterationTag;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.ClassException;
import railo.runtime.exp.Abort;
import railo.runtime.tag.MissingAttribute;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;
import railo.transformer.library.tag.TagLibTag;

public final class TagHelper {
	private static final Type MISSING_ATTRIBUTE = Type.getType(MissingAttribute.class);
	private static final Type MISSING_ATTRIBUTE_ARRAY = Type.getType(MissingAttribute[].class);
	private static final Type TAG=Type.getType(javax.servlet.jsp.tagext.Tag.class);
	private static final Type TAG_UTIL=Type.getType(railo.runtime.tag.TagUtil.class);
	
	// TagUtil.setAttributeCollection(Tag, Struct)
	private static final Method SET_ATTRIBUTE_COLLECTION = new Method(
			"setAttributeCollection",Types.VOID,new Type[]{Types.PAGE_CONTEXT,TAG,MISSING_ATTRIBUTE_ARRAY,Types.STRUCT,Types.INT_VALUE});
	
	// Tag use(String)
	private static final Method USE= new Method("use",TAG,new Type[]{Types.STRING});
	
	// void setAppendix(String appendix)
	private static final Method SET_APPENDIX = new Method("setAppendix",Type.VOID_TYPE,new Type[]{Types.STRING});
	
	// void setDynamicAttribute(String uri, String name, Object value)
	private static final Method SET_DYNAMIC_ATTRIBUTE = new Method(
			"setDynamicAttribute",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.COLLECTION_KEY,Types.OBJECT});
	
	private static final Method SET_META_DATA = new Method(
			"setMetaData",
			Type.VOID_TYPE,
			new Type[]{Types.STRING,Types.OBJECT});

	// void hasBody(boolean hasBody)
	private static final Method HAS_BODY = new Method(
			"hasBody",
			Type.VOID_TYPE,
			new Type[]{Types.BOOLEAN_VALUE});

	// int doStartTag()
	private static final Method DO_START_TAG = new Method(
			"doStartTag",
			Types.INT_VALUE,
			new Type[]{});

	// int doEndTag()
	private static final Method DO_END_TAG =  new Method(
			"doEndTag",
			Types.INT_VALUE,
			new Type[]{});

	private static final Type ABORT = Type.getType(Abort.class);
	//private static final Type EXPRESSION_EXCEPTION = Type.getType(ExpressionException.class);
	private static final Type BODY_TAG = Type.getType(BodyTag.class);

	// ExpressionException newInstance(int)
	private static final Method NEW_INSTANCE =  new Method(
			"newInstance",
			ABORT,
			new Type[]{Types.INT_VALUE});
	private static final Method NEW_INSTANCE_MAX =  new Method(
			"newInstance",
			MISSING_ATTRIBUTE,
			new Type[]{Types.COLLECTION_KEY,Types.STRING});
	
	
	

	// void initBody(BodyTag bodyTag, int state)
	private static final Method INIT_BODY = new Method(
			"initBody",
			Types.VOID,
			new Type[]{BODY_TAG,Types.INT_VALUE});

	// int doAfterBody()
	private static final Method DO_AFTER_BODY = new Method(
			"doAfterBody",
			Types.INT_VALUE,
			new Type[]{});

	// void doCatch(Throwable t)
	private static final Method DO_CATCH = new Method(
			"doCatch",
			Types.VOID,
			new Type[]{Types.THROWABLE});

	// void doFinally()
	private static final Method DO_FINALLY = new Method(
			"doFinally",
			Types.VOID,
			new Type[]{});

	// JspWriter popBody()
	private static final Method POP_BODY = new Method(
			"popBody",
			Types.JSP_WRITER,
			new Type[]{});

	// void reuse(Tag tag)
	private static final Method RE_USE = new Method(
			"reuse",
			Types.VOID,
			new Type[]{Types.TAG});
	
	/**
	 * writes out the tag
	 * @param tag
	 * @param bc
	 * @param doReuse
	 * @throws BytecodeException
	 */
	public static void writeOut(Tag tag, BytecodeContext bc, boolean doReuse, final FlowControlFinal fcf) throws BytecodeException {
		final GeneratorAdapter adapter = bc.getAdapter();
		final TagLibTag tlt = tag.getTagLibTag();
		final Type currType=getTagType(tag);
		
		final int currLocal=adapter.newLocal(currType);
		Label tagBegin=new Label();
		Label tagEnd=new Label();
		ExpressionUtil.visitLine(bc, tag.getStart());
		// TODO adapter.visitLocalVariable("tag", "L"+currType.getInternalName()+";", null, tagBegin, tagEnd, currLocal);

		adapter.visitLabel(tagBegin);
		
	// tag=pc.use(str);
		adapter.loadArg(0);
		adapter.push(tlt.getTagClassName());
		adapter.invokeVirtual(Types.PAGE_CONTEXT, USE);
		adapter.checkCast(currType);
		adapter.storeLocal(currLocal);
	
	TryFinallyVisitor outerTcfv=new TryFinallyVisitor(new OnFinally() {
		public void writeOut(BytecodeContext bc) {

			adapter.loadArg(0);
			adapter.loadLocal(currLocal);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, RE_USE);
		}
	},null);
	if(doReuse)outerTcfv.visitTryBegin(bc);
		
	// appendix
		if(tlt.hasAppendix()) {
			adapter.loadLocal(currLocal);
			adapter.push(tag.getAppendix());
			adapter.invokeVirtual(currType, SET_APPENDIX);
		}
	
	// hasBody
		boolean hasBody=tag.getBody()!=null;
		if(tlt.isBodyFree() && tlt.hasBodyMethodExists()) {
			adapter.loadLocal(currLocal);
			adapter.push(hasBody);
			adapter.invokeVirtual(currType, HAS_BODY);
		}
		
	// attributes
		Attribute attr;
		
		// attributeCollection
		attr=tag.getAttribute("attributecollection");
		if(attr!=null){
			int attrType = tag.getTagLibTag().getAttributeType();
			if(TagLibTag.ATTRIBUTE_TYPE_NONAME!=attrType) {
				tag.removeAttribute("attributecollection");
				// TagUtil.setAttributeCollection(Tag, Struct)
				adapter.loadArg(0);
				adapter.loadLocal(currLocal);
				adapter.cast(currType, TAG);
				
				///
				Map missings = tag.getMissingAttributes();
				if(missings.size()>0) {
					ArrayVisitor av=new ArrayVisitor();
		            av.visitBegin(adapter,MISSING_ATTRIBUTE,missings.size());
		            Map.Entry entry;
		            int count=0;
		            Iterator it = missings.entrySet().iterator();
		            while(it.hasNext()){
		            	entry=(Entry) it.next();
		    			av.visitBeginItem(adapter, count++);
		    				Variable.registerKey(bc, LitString.toExprString((String)entry.getKey()));
			    			adapter.push((String)entry.getValue());
		    				adapter.invokeStatic(MISSING_ATTRIBUTE, NEW_INSTANCE_MAX);
		    			av.visitEndItem(bc.getAdapter());
		            }
		            av.visitEnd();
				}
				else {
					ASMConstants.NULL(adapter);
				}
				///
				attr.getValue().writeOut(bc, Expression.MODE_REF);
				
				adapter.push(attrType);
				adapter.invokeStatic(TAG_UTIL, SET_ATTRIBUTE_COLLECTION);
			}
		}


		// metadata
		Map<String, Attribute> metadata = tag.getMetaData();
		if(metadata!=null){
			Iterator<Attribute> it = metadata.values().iterator();
			while(it.hasNext()) {
				attr=it.next();
					adapter.loadLocal(currLocal);
					adapter.push(attr.getName());
					attr.getValue().writeOut(bc, Expression.MODE_REF);
					adapter.invokeVirtual(currType, SET_META_DATA);
			}
		}
		
		
		
		String methodName;
		Map attributes = tag.getAttributes();

		// static attributes
		Iterator it = attributes.values().iterator();
		while(it.hasNext()) {
			attr=(Attribute) it.next();
			if(!attr.isDynamicType()){
				Type type = CastOther.getType(attr.getType());
				methodName=tag.getTagLibTag().getSetter(attr,type);
				adapter.loadLocal(currLocal);
				attr.getValue().writeOut(bc, Types.isPrimitiveType(type)?Expression.MODE_VALUE:Expression.MODE_REF);
				adapter.invokeVirtual(currType, new Method(methodName,Type.VOID_TYPE,new Type[]{type}));
			}
		}
		
		// dynamic attributes
		it = attributes.values().iterator();
		while(it.hasNext()) {
			attr=(Attribute) it.next();
			if(attr.isDynamicType()){
				adapter.loadLocal(currLocal);
				adapter.visitInsn(Opcodes.ACONST_NULL);
				//adapter.push(attr.getName());
				Variable.registerKey(bc, LitString.toExprString(attr.getName()));
				attr.getValue().writeOut(bc, Expression.MODE_REF);
				adapter.invokeVirtual(currType, SET_DYNAMIC_ATTRIBUTE);
			}
		}
		
		
	// Body
		if(hasBody){
			final int state=adapter.newLocal(Types.INT_VALUE);
			
			// int state=tag.doStartTag();
			adapter.loadLocal(currLocal);
			adapter.invokeVirtual(currType, DO_START_TAG);
			adapter.storeLocal(state);
			
			// if (state!=Tag.SKIP_BODY)
			Label endBody=new Label();
			adapter.loadLocal(state);
			adapter.push(javax.servlet.jsp.tagext.Tag.SKIP_BODY);
			adapter.visitJumpInsn(Opcodes.IF_ICMPEQ, endBody);
				// pc.initBody(tag, state);
				adapter.loadArg(0);
				adapter.loadLocal(currLocal);
				adapter.loadLocal(state);
				adapter.invokeVirtual(Types.PAGE_CONTEXT, INIT_BODY);
				
				
				OnFinally onFinally = new OnFinally() {
					
					public void writeOut(BytecodeContext bc) {
						Label endIf = new Label();
						/*if(tlt.handleException() && fcf!=null && fcf.getAfterFinalGOTOLabel()!=null){
							ASMUtil.visitLabel(adapter, fcf.getFinalEntryLabel());
						}*/
						adapter.loadLocal(state);
						adapter.push(javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE);
						adapter.visitJumpInsn(Opcodes.IF_ICMPEQ, endIf);
							// ... pc.popBody();
							adapter.loadArg(0);
							adapter.invokeVirtual(Types.PAGE_CONTEXT, POP_BODY);
							adapter.pop();
						adapter.visitLabel(endIf);
					
						// tag.doFinally();
						if(tlt.handleException()) {
							adapter.loadLocal(currLocal);
							adapter.invokeVirtual(currType, DO_FINALLY);
						}
						// GOTO after execution body, used when a continue/break was called before
						/*if(fcf!=null) {
							Label l = fcf.getAfterFinalGOTOLabel();
							if(l!=null)adapter.visitJumpInsn(Opcodes.GOTO, l);
						}*/
						
					}
				};
				
				
				if(tlt.handleException()) {
					TryCatchFinallyVisitor tcfv=new TryCatchFinallyVisitor(onFinally,fcf);
					tcfv.visitTryBegin(bc);
						doTry(bc,adapter,tag,currLocal,currType);
					int t=tcfv.visitTryEndCatchBeging(bc);
						// tag.doCatch(t);
						adapter.loadLocal(currLocal);
						adapter.loadLocal(t);
						//adapter.visitVarInsn(Opcodes.ALOAD,t);
						adapter.invokeVirtual(currType, DO_CATCH);
					tcfv.visitCatchEnd(bc);
				}
				else {
					TryFinallyVisitor tfv=new TryFinallyVisitor(onFinally,fcf);
					tfv.visitTryBegin(bc);
						doTry(bc,adapter,tag,currLocal,currType);
					tfv.visitTryEnd(bc);
				}
				

			adapter.visitLabel(endBody);
				
		}
		else {
			//tag.doStartTag();
			adapter.loadLocal(currLocal);
			adapter.invokeVirtual(currType, DO_START_TAG);
			adapter.pop();
		}
		
		// if (tag.doEndTag()==Tag.SKIP_PAGE) throw new Abort(0<!-- SCOPE_PAGE -->);
		Label endDoEndTag=new Label();
		adapter.loadLocal(currLocal);
		adapter.invokeVirtual(currType, DO_END_TAG);
		adapter.push(javax.servlet.jsp.tagext.Tag.SKIP_PAGE);
		adapter.visitJumpInsn(Opcodes.IF_ICMPNE, endDoEndTag);
			adapter.push(Abort.SCOPE_PAGE);
			adapter.invokeStatic(ABORT, NEW_INSTANCE);
			adapter.throwException();
		adapter.visitLabel(endDoEndTag);
		
		
		if(doReuse) {
			// } finally{pc.reuse(tag);}
			outerTcfv.visitTryEnd(bc);
		}
		

		adapter.visitLabel(tagEnd);
		ExpressionUtil.visitLine(bc, tag.getEnd());
	}

	private static void doTry(BytecodeContext bc, GeneratorAdapter adapter, Tag tag, int currLocal, Type currType) throws BytecodeException {
		Label beginDoWhile=new Label();
		adapter.visitLabel(beginDoWhile);
			bc.setCurrentTag(currLocal);
			tag.getBody().writeOut(bc);
			
		// while (tag.doAfterBody()==BodyTag.EVAL_BODY_AGAIN);
			adapter.loadLocal(currLocal);
			adapter.invokeVirtual(currType, DO_AFTER_BODY);
			adapter.push(IterationTag.EVAL_BODY_AGAIN);
		adapter.visitJumpInsn(Opcodes.IF_ICMPEQ, beginDoWhile);
	}

	private static Type getTagType(Tag tag) throws BytecodeException {
		TagLibTag tlt = tag.getTagLibTag();
		try {
			return tlt.getTagType();
		} catch (ClassException e) {
			throw new BytecodeException(e,tag.getStart());
		}
	}
}
