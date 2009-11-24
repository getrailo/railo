package railo.transformer.bytecode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.ComponentPage;
import railo.runtime.InterfacePage;
import railo.runtime.exp.TemplateException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.Undefined;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Argument;
import railo.transformer.bytecode.statement.Function;
import railo.transformer.bytecode.statement.HasBodies;
import railo.transformer.bytecode.statement.HasBody;
import railo.transformer.bytecode.statement.IFunction;
import railo.transformer.bytecode.statement.NativeSwitch;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagThread;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ConditionVisitor;
import railo.transformer.bytecode.visitor.DecisionIntVisitor;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;

/**
 * represent a single Page like "index.cfm"
 */
public final class Page extends BodyBase {

	private static final Type KEY_IMPL = Type.getType(KeyImpl.class);
	private static final Method GET_INSTANCE = new Method(
			"getInstance",
			Types.COLLECTION_KEY,
			new Type[]{Types.STRING}
    		);

	public final static Method STATIC_CONSTRUCTOR = Method.getMethod("void <clinit> ()V");
	public final static Method CONSTRUCTOR = Method.getMethod("void <init> ()V");
	

    public static final Type STRUCT_IMPL = Type.getType(StructImpl.class);
	private static final Method INIT_STRUCT_IMPL = new Method(
			"<init>",
			Types.VOID,
			new Type[]{}
    		);

	
    // void call (railo.runtime.PageContext)
    private final static Method CALL = new Method(
			"call",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT}
    		);
    
    /*/ void _try ()
    private final static Method TRY = new Method(
			"_try",
			Types.VOID,
			new Type[]{}
    		);*/
    	
    // int getVersion()
    private final static Method VERSION = new Method(
			"getVersion",
			Types.INT_VALUE,
			new Type[]{}
    		);
    
    // long getSourceLastModified()
    private final static Method LAST_MODX = new Method(
			"getSourceLastModified",
			Types.LONG_VALUE,
			new Type[]{}
    		);
    
    private final static Method COMPILE_TIME = new Method(
			"getCompileTime",
			Types.LONG_VALUE,
			new Type[]{}
    		);

    private static final Type USER_DEFINED_FUNCTION = Type.getType(UDF.class);
    private static final Method UDF_CALL = new Method(
			"udfCall",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT, USER_DEFINED_FUNCTION, Types.INT_VALUE}
			);
    

	private static final Method THREAD_CALL = new Method(
			"threadCall",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT, Types.INT_VALUE}
			);

	private static final Method UDF_DEFAULT_VALUE = new Method(
			"udfDefaultValue",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT, Types.INT_VALUE, Types.INT_VALUE}
			);

	private static final Method NEW_COMPONENT_IMPL_INSTANCE = new Method(
			"newInstance",
			Types.COMPONENT_IMPL,
			new Type[]{Types.PAGE_CONTEXT,Types.STRING,Types.BOOLEAN_VALUE}
    		);
	
	private static final Method NEW_INTERFACE_IMPL_INSTANCE = new Method(
			"newInstance",
			Types.INTERFACE_IMPL,
			new Type[]{Types.STRING,Types.BOOLEAN_VALUE,Types.MAP}
    		);
	
	

	

	// void init(PageContext pc,ComponentImpl c) throws PageException
	private static final Method INIT_COMPONENT = new Method(
			"initComponent",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.COMPONENT_IMPL}
    		);
	private static final Method INIT_INTERFACE = new Method(
			"initInterface",
			Types.VOID,
			new Type[]{Types.INTERFACE_IMPL}
    		);
	

	// public boolean setMode(int mode) {
	private static final Method SET_MODE = new Method(
			"setMode",
			Types.INT_VALUE,
			new Type[]{Types.INT_VALUE}
    		);
	
	

	private static final Type COMPONENT_PAGE = Type.getType(ComponentPage.class);

	private static final Type INTERFACE_PAGE = Type.getType(InterfacePage.class);
	
	


	private static final Method CONSTR_INTERFACE_IMPL = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
						INTERFACE_PAGE,
						Types.STRING, // extends
						Types.STRING, // hind
						Types.STRING, // display
						Types.STRING, // callpath
						Types.BOOLEAN_VALUE, // realpath
						Types.MAP 
					}
    		);
	
	
	//void init(PageContext pageContext,ComponentPage componentPage)
	private static final Method INIT = new Method(
			"init",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,COMPONENT_PAGE}
    		);
	
	private static final Method CHECK_INTERFACE = new Method(
			"checkInterface",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,COMPONENT_PAGE}
    		);
	
	

	// boolean getOutput()
	private static final Method GET_OUTPUT = new Method(
			"getOutput",
			Types.BOOLEAN_VALUE,
			new Type[]{}
    		);


	private static final Method PUSH_BODY = new Method(
			"pushBody",
			Types.BODY_CONTENT,
			new Type[]{}
    		);
	
	/*/ boolean setSilent()
	private static final Method SET_SILENT = new Method(
			"setSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
    		);
*/
	// Scope beforeCall(PageContext pc)
	private static final Method BEFORE_CALL = new Method(
			"beforeCall",
			Types.VARIABLES,
			new Type[]{Types.PAGE_CONTEXT}
    		);

	private static final Method TO_PAGE_EXCEPTION = new Method(
			"toPageException",
			Types.PAGE_EXCEPTION,
			new Type[]{Types.THROWABLE});
	
	
	// boolean unsetSilent()
	/*private static final Method UNSET_SILENT = new Method(
			"unsetSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
    		);*/

	// void afterCall(PageContext pc, Scope parent)
	private static final Method AFTER_CALL = new Method(
			"afterConstructor",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.VARIABLES}
    		);

	// ComponentImpl(ComponentPage,boolean, String, String, String) NS==No Style
	/*private static final Method CONSTR_COMPONENT_IMPL_NS = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
						COMPONENT_PAGE,
						Types.BOOLEAN,
						Types.BOOLEAN_VALUE,
						Types.STRING,
						Types.STRING,
						Types.STRING,
						Types.STRING,
						Types.BOOLEAN_VALUE,
						STRUCT_IMPL
					}
    		);*/
	
	// ComponentImpl(ComponentPage,boolean, String, String, String, String) WS==With Style
	private static final Method CONSTR_COMPONENT_IMPLX = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					COMPONENT_PAGE,
					Types.BOOLEAN,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					STRUCT_IMPL
				}
    		);
	private static final Method SET_EL = new Method(
			"setEL",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT}
    		);
	private static final Method UNDEFINED_SCOPE = new Method(
			"us",
			Types.UNDEFINED,
			new Type[]{}
    		);
	private static final Method FLUSH_AND_POP = new Method(
			"flushAndPop",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.BODY_CONTENT}
    		);
	private static final Method CLEAR_AND_POP = new Method(
			"clearAndPop",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.BODY_CONTENT}
    		);

	
    private int version;
    private long lastModifed;
    private String name;
    
    //private Body body=new Body();
	private String source;
	private boolean isComponent;
	private boolean isInterface;

	private List functions=new ArrayList();
	private List threads=new ArrayList();
    
	
	
    public Page(String source,String name,int version, long lastModifed) {
    	name=name.replace('.', '/');
    	//body.setParent(this);
        this.name=name;
        this.version=version;
        this.lastModifed=lastModifed;
        this.source=source;
    }
    
    /**
     * result byte code as binary array
     * @param classFile 
     * @return byte code
     * @throws IOException 
     * @throws TemplateException 
     */
    public byte[] execute() throws BytecodeException {
    	List keys=new ArrayList();
    	ClassWriter cw = ASMUtil.getClassWriter(); 
    	//ClassWriter cw = new ClassWriter(true);
    	
    	// parent
    	String parent="railo/runtime/Page";
    	if(isComponent()) parent="railo/runtime/ComponentPage";
    	else if(isInterface()) parent="railo/runtime/InterfacePage";
    	
    	cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL, name, null, parent, null);
        cw.visitSource(this.source, null);

        // static constructor
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,STATIC_CONSTRUCTOR,null,null,cw);
		BytecodeContext statConstr = new BytecodeContext(null,null,keys,cw,name,ga,STATIC_CONSTRUCTOR);
		
		
        // constructor
        ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR,null,null,cw);
		BytecodeContext constr = new BytecodeContext(null,null,keys,cw,name,ga,CONSTRUCTOR);
		ga.loadThis();
        Type t=Types.PAGE;
        if(isComponent())t=COMPONENT_PAGE;
        else if(isInterface())t=INTERFACE_PAGE;
        ga.invokeConstructor(t, CONSTRUCTOR);
        
        
        
        
        
     // getVersion
         GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , VERSION, null, null, cw);
         adapter.push(version);
         adapter.returnValue();
         adapter.endMethod();

// getSourceLastModified
        adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , LAST_MODX, null, null, cw);
        adapter.push(lastModifed);
        adapter.returnValue();
        adapter.endMethod();
        
        adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , COMPILE_TIME, null, null, cw);
        adapter.push(System.currentTimeMillis());
        adapter.returnValue();
        adapter.endMethod();
   
// newInstance/initComponent/call
        if(isComponent()) {
        	Tag component=getComponent();
   	        writeOutNewComponent(statConstr,constr,keys,cw,component);
	        writeOutInitComponent(statConstr,constr,keys,cw,component);
        }
        else if(isInterface()) {
        	Tag interf=getInterface();
   	        writeOutNewInterface(statConstr,constr,keys,cw,interf);
	        writeOutInitInterface(statConstr,constr,keys,cw,interf);
        }
        else {
	        writeOutCall(statConstr,constr,keys,cw);
        }
        
// udfCall     
        Function[] functions=getFunctions();
        
        ConditionVisitor cv;
        DecisionIntVisitor div;
    // less/equal than 10 functions
        if(functions.length==0 || isInterface()){}
        else if(functions.length<=10) {
        	
            adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_CALL, null, new Type[]{Types.THROWABLE}, cw);
            BytecodeContext bc = new BytecodeContext(statConstr,constr,keys,cw,name,adapter,UDF_CALL);
        	if(functions.length==1){
        		ExpressionUtil.visitLine(bc,functions[0].getStartLine());
        		functions[0].getBody().writeOut(bc);
        		ExpressionUtil.visitLine(bc,functions[0].getEndLine());
        	}
        	else writeOutUdfCallInner(bc,functions,0,functions.length);
            adapter.visitInsn(Opcodes.ACONST_NULL);
            adapter.returnValue();
            adapter.endMethod(); 
        }
   // more than 10 functions
        else {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_CALL, null, new Type[]{Types.THROWABLE}, cw);
        	BytecodeContext bc = new BytecodeContext(statConstr,constr,keys,cw,name,adapter,UDF_CALL);
		        cv = new ConditionVisitor();
		        cv.visitBefore();
		        int count=0;
		        for(int i=0;i<functions.length;i+=10) {
		        	cv.visitWhenBeforeExpr();
			        	div=new DecisionIntVisitor();
						div.visitBegin();
							adapter.loadArg(2);
						div.visitLT();
							adapter.push(i+10);
						div.visitEnd(bc);
		        	cv.visitWhenAfterExprBeforeBody(bc);
		        		
			        	adapter.visitVarInsn(Opcodes.ALOAD, 0);
			        	adapter.visitVarInsn(Opcodes.ALOAD, 1);
			        	adapter.visitVarInsn(Opcodes.ALOAD, 2);
			        	adapter.visitVarInsn(Opcodes.ILOAD, 3);
			        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, "udfCall"+(++count), "(Lrailo/runtime/PageContext;Lrailo/runtime/type/UDF;I)Ljava/lang/Object;");
			        	adapter.visitInsn(Opcodes.ARETURN);//adapter.returnValue();
		        	cv.visitWhenAfterBody(bc);
		        }
		        cv.visitAfter(bc);
	        
	        adapter.visitInsn(Opcodes.ACONST_NULL);
	        adapter.returnValue();
	        adapter.endMethod();
	        
	        count=0;
	        Method innerCall;
	        for(int i=0;i<functions.length;i+=10) {
	        	innerCall = new Method("udfCall"+(++count),Types.OBJECT,new Type[]{Types.PAGE_CONTEXT, USER_DEFINED_FUNCTION, Types.INT_VALUE});
	        	
	        	adapter = new GeneratorAdapter(Opcodes.ACC_PRIVATE+Opcodes.ACC_FINAL , innerCall, null, new Type[]{Types.THROWABLE}, cw);
	        	writeOutUdfCallInner(new BytecodeContext(statConstr,constr,keys,cw,name,adapter,innerCall), functions, i, i+10>functions.length?functions.length:i+10);
	        	
	        	adapter.visitInsn(Opcodes.ACONST_NULL);
		        adapter.returnValue();
		        adapter.endMethod();
	        }
        }
        

     // threadCall
             TagThread[] threads=getThreads();
             if(threads.length>0) {
             	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , THREAD_CALL, null, new Type[]{Types.THROWABLE}, cw);
         			writeOutThreadCallInner(new BytecodeContext(statConstr,constr,keys,cw,name,adapter,THREAD_CALL),threads,0,threads.length);
         		//adapter.visitInsn(Opcodes.ACONST_NULL);
         		adapter.returnValue();
         		adapter.endMethod();
             }
        

        
                
// udfDefaultValue
    // less/equal than 10 functions
        if(functions.length==0 || isInterface()) {}
        else if(functions.length<=10) {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_DEFAULT_VALUE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
            writeUdfDefaultValueInner(new BytecodeContext(statConstr,constr,keys,cw,name,adapter,UDF_DEFAULT_VALUE),functions,0,functions.length);
            
            ASMConstants.NULL(adapter);
        	adapter.returnValue();
            adapter.endMethod();
        }
        else {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_DEFAULT_VALUE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
            BytecodeContext bc = new BytecodeContext(statConstr,constr,keys,cw,name,adapter,UDF_DEFAULT_VALUE);
        	cv = new ConditionVisitor();
	        cv.visitBefore();
	        int count=0;
	        for(int i=0;i<functions.length;i+=10) {
	        	cv.visitWhenBeforeExpr();
		        	div=new DecisionIntVisitor();
					div.visitBegin();
						adapter.loadArg(1);
					div.visitLT();
						adapter.push(i+10);
					div.visitEnd(bc);
	        	cv.visitWhenAfterExprBeforeBody(bc);
	        		
		        	adapter.visitVarInsn(Opcodes.ALOAD, 0);
		        	adapter.visitVarInsn(Opcodes.ALOAD, 1);
		        	adapter.visitVarInsn(Opcodes.ILOAD, 2);
		        	adapter.visitVarInsn(Opcodes.ILOAD, 3);
		        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, "udfDefaultValue"+(++count), "(Lrailo/runtime/PageContext;II)Ljava/lang/Object;");
		        	adapter.visitInsn(Opcodes.ARETURN);//adapter.returnValue();
		        	
	        	cv.visitWhenAfterBody(bc);
	        }
	        cv.visitAfter(bc);
        
        adapter.visitInsn(Opcodes.ACONST_NULL);
        adapter.returnValue();
        adapter.endMethod();
        
        count=0;
        Method innerDefaultValue;
        for(int i=0;i<functions.length;i+=10) {
        	innerDefaultValue = new Method("udfDefaultValue"+(++count),Types.OBJECT,new Type[]{Types.PAGE_CONTEXT, Types.INT_VALUE, Types.INT_VALUE});
        	adapter = new GeneratorAdapter(Opcodes.ACC_PRIVATE+Opcodes.ACC_FINAL , innerDefaultValue, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        	writeUdfDefaultValueInner(new BytecodeContext(statConstr,constr,keys,cw,name,adapter,innerDefaultValue), functions, i, i+10>functions.length?functions.length:i+10);
        	
        	adapter.visitInsn(Opcodes.ACONST_NULL);
	        adapter.returnValue();
	        adapter.endMethod();
        }
        	
        }
        
        
        registerFields(statConstr,keys);
        
        

        statConstr.getAdapter().returnValue();
        statConstr.getAdapter().endMethod();
        
        
        adapter = constr.getAdapter();//new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR,null,null,cw);
        
        adapter.returnValue();
        adapter.endMethod();
    	
        return cw.toByteArray();
 	
    }
    




	public static void registerFields(BytecodeContext staticBC, List keys) throws BytecodeException {
		Iterator it = keys.iterator();
		//GeneratorAdapter ad = bc.getAdapter();
		int count=0;
		String name;
		LitString value;
		
		//GeneratorAdapter statcConst = new GeneratorAdapter(Opcodes.ACC_PUBLIC,STATIC_CONSTRUCTOR,null,null,bc.getClassWriter());
		//BytecodeContext cbw = new BytecodeContext(null,bc.getClassWriter(),bc.getClassName(),statcConst,STATIC_CONSTRUCTOR);
		//BytecodeContext cbw = bc.getStaticConstructor();
		GeneratorAdapter statcConst = staticBC.getAdapter();
		
		
		
		while(it.hasNext()) {
			name="key"+(++count);
			value=(LitString) it.next();
			

			FieldVisitor fv = staticBC.getClassWriter().visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, 
					name, "Lrailo/runtime/type/Collection$Key;", null, null);
			fv.visitEnd();
			
			ExpressionUtil.writeOutSilent(value,staticBC, Expression.MODE_REF);
			statcConst.invokeStatic(KEY_IMPL, GET_INSTANCE);
			statcConst.visitFieldInsn(Opcodes.PUTSTATIC, staticBC.getClassName(), name, "Lrailo/runtime/type/Collection$Key;");
			
			
			
		}

		//statcConst.returnValue();
		//statcConst.endMethod();
		
	}

	private void writeUdfDefaultValueInner(BytecodeContext bc, Function[] functions, int offset, int length) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ConditionVisitor cv = new ConditionVisitor();
		DecisionIntVisitor div;
        cv.visitBefore();
        for(int i=offset;i<length;i++) {
        	cv.visitWhenBeforeExpr();
	        	div = new DecisionIntVisitor();
				div.visitBegin();
					adapter.loadArg(1);
				div.visitEQ();
					adapter.push(i);
				div.visitEnd(bc);
        	cv.visitWhenAfterExprBeforeBody(bc);
        		writeOutFunctionDefaultValueInnerInner(bc, functions[i]);
        	cv.visitWhenAfterBody(bc);
        }
        cv.visitAfter(bc);
	}


	private void writeOutUdfCallInnerIf(BytecodeContext bc,Function[] functions, int offset, int length) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ConditionVisitor cv=new ConditionVisitor();
        DecisionIntVisitor div;
        cv.visitBefore();
        for(int i=offset;i<length;i++) {
        	cv.visitWhenBeforeExpr();
	        	div=new DecisionIntVisitor();
				div.visitBegin();
					adapter.loadArg(2);
				div.visitEQ();
					adapter.push(i);
				div.visitEnd(bc);
        	cv.visitWhenAfterExprBeforeBody(bc);
        		ExpressionUtil.visitLine(bc, functions[i].getStartLine());
        		functions[i].getBody().writeOut(bc);
        		ExpressionUtil.visitLine(bc, functions[i].getEndLine());
        	cv.visitWhenAfterBody(bc);
        }
        cv.visitAfter(bc);
	}

	private void writeOutUdfCallInner(BytecodeContext bc,Function[] functions, int offset, int length) throws BytecodeException {
		NativeSwitch ns=new NativeSwitch(2,NativeSwitch.ARG_REF,-1,-1);
		
		for(int i=offset;i<length;i++) {
        	ns.addCase(i, functions[i].getBody(),functions[i].getStartLine(),functions[i].getEndLine(),true);
        }
        ns._writeOut(bc);
	}
	
	

	private void writeOutThreadCallInner(BytecodeContext bc,TagThread[] threads, int offset, int length) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ConditionVisitor cv=new ConditionVisitor();
        DecisionIntVisitor div;
        cv.visitBefore();
        //print.ln("functions:"+functions.length);
	        for(int i=offset;i<length;i++) {
	        	cv.visitWhenBeforeExpr();
		        	div=new DecisionIntVisitor();
					div.visitBegin();
						adapter.loadArg(1);
					div.visitEQ();
						adapter.push(i);
					div.visitEnd(bc);
	        	cv.visitWhenAfterExprBeforeBody(bc);
	        		threads[i].getRealBody().writeOut(bc);
	        	cv.visitWhenAfterBody(bc);
	        }
        cv.visitAfter(bc);
	}

	private void writeOutInitComponent(BytecodeContext statConstr,BytecodeContext constr,List keys, ClassWriter cw, Tag component) throws BytecodeException {
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , INIT_COMPONENT, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,keys,cw,name,adapter,INIT_COMPONENT);
		Label methodBegin=new Label();
    	Label methodEnd=new Label();

		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);
        
		// Scope oldData=null;
		int oldData=adapter.newLocal(Types.VARIABLES);
		ASMConstants.NULL(adapter);
		adapter.storeLocal(oldData);
		
		int localBC=adapter.newLocal(Types.BODY_CONTENT);
		ConditionVisitor cv=new ConditionVisitor();
		cv.visitBefore();
			cv.visitWhenBeforeExpr();
				adapter.loadArg(1);
				adapter.invokeVirtual(Types.COMPONENT_IMPL, GET_OUTPUT);
			cv.visitWhenAfterExprBeforeBody(bc);
				ASMConstants.NULL(adapter);
			cv.visitWhenAfterBody(bc);

			cv.visitOtherviseBeforeBody();
				adapter.loadArg(0);
				adapter.invokeVirtual(Types.PAGE_CONTEXT, PUSH_BODY);
			cv.visitOtherviseAfterBody();
		cv.visitAfter(bc);
		adapter.storeLocal(localBC);
		
		// c.init(pc,this);
		adapter.loadArg(1);
		adapter.loadArg(0);
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		adapter.invokeVirtual(Types.COMPONENT_IMPL, INIT);
			
			
		//int oldCheckArgs=	pc.undefinedScope().setMode(Undefined.MODE_NO_LOCAL_AND_ARGUMENTS);
			int oldCheckArgs = adapter.newLocal(Types.INT_VALUE);
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, UNDEFINED_SCOPE);
			adapter.push(Undefined.MODE_NO_LOCAL_AND_ARGUMENTS);
			adapter.invokeInterface(Types.UNDEFINED, SET_MODE);
			adapter.storeLocal(oldCheckArgs);
		
			
		TryCatchFinallyVisitor tcf=new TryCatchFinallyVisitor();
		tcf.visitTryBegin(bc);
			// oldData=c.beforeCall(pc);
			adapter.loadArg(1);
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, BEFORE_CALL);
			adapter.storeLocal(oldData);
			ExpressionUtil.visitLine(bc, component.getStartLine());
			writeOutCallBody(bc,component.getBody(),IFunction.PAGE_TYPE_COMPONENT);
			ExpressionUtil.visitLine(bc, component.getEndLine());
		tcf.visitTryEnd(bc);
		
		int t=tcf.visitCatchBegin(bc, Types.THROWABLE);
			// BodyContentUtil.flushAndPop(pc,bc);
			adapter.loadArg(0);
			adapter.loadLocal(localBC);
			adapter.invokeStatic(Types.BODY_CONTENT_UTIL, FLUSH_AND_POP);
		
			// throw Caster.toPageException(t);
			adapter.loadLocal(t);
			adapter.invokeStatic(Types.CASTER, TO_PAGE_EXCEPTION);
			adapter.throwException();
		tcf.visitCatchEnd(bc);
		
		tcf.visitFinallyBegin(bc);
		
		// undefined.setMode(oldMode);
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, UNDEFINED_SCOPE);
		adapter.loadLocal(oldCheckArgs,Types.INT_VALUE);
		adapter.invokeInterface(Types.UNDEFINED, SET_MODE);
		adapter.pop();
		
			// c.afterCall(pc,_oldData);
			adapter.loadArg(1);
			adapter.loadArg(0);
			adapter.loadLocal(oldData);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, AFTER_CALL);
		
			
			
		tcf.visitFinallyEnd(bc);
		adapter.loadArg(0);
		adapter.loadLocal(localBC);
		adapter.invokeStatic(Types.BODY_CONTENT_UTIL, CLEAR_AND_POP);
	
    	adapter.returnValue();
	    adapter.visitLabel(methodEnd);
	    
	    adapter.endMethod();
    	
	}
	
	private void writeOutInitInterface(BytecodeContext statConstr,BytecodeContext constr,List keys, ClassWriter cw, Tag interf) throws BytecodeException {
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , INIT_INTERFACE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,keys,cw,name,adapter,INIT_INTERFACE);
		Label methodBegin=new Label();
    	Label methodEnd=new Label();

		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);
        
    	ExpressionUtil.visitLine(bc, interf.getStartLine());
		writeOutCallBody(bc,interf.getBody(),IFunction.PAGE_TYPE_INTERFACE);
		ExpressionUtil.visitLine(bc, interf.getEndLine());
		
    	adapter.returnValue();
	    adapter.visitLabel(methodEnd);
	    
	    adapter.endMethod();
    	
	}

	private Tag getComponent() throws BytecodeException {
		Iterator it = getStatements().iterator();
		Statement s;
		Tag t;
        while(it.hasNext()) {
        	s=(Statement)it.next();
        	if(s instanceof Tag) {
        		t=(Tag)s;
        		if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Component"))return t;
        	}
        }
		throw new BytecodeException("missing component",getLine());
	}
	private Tag getInterface() throws BytecodeException {
		Iterator it = getStatements().iterator();
		Statement s;
		Tag t;
        while(it.hasNext()) {
        	s=(Statement)it.next();
        	if(s instanceof Tag) {
        		t=(Tag)s;
        		if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Interface"))return t;
        	}
        }
		throw new BytecodeException("missing interface",getLine());
	}

	private void writeOutFunctionDefaultValueInnerInner(BytecodeContext bc, Function function) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		
		List args = function.getArguments();
		
		if(args.size()==0) {
			adapter.visitInsn(Opcodes.ACONST_NULL);
			adapter.returnValue();
			return;
		}
			
		Iterator it = args.iterator();
		Argument arg;
		ConditionVisitor cv=new ConditionVisitor();
		DecisionIntVisitor div;
		cv.visitBefore();
		int count=0;
		while(it.hasNext()) {
			arg=(Argument) it.next();
			cv.visitWhenBeforeExpr();
				div=new DecisionIntVisitor();
				div.visitBegin();
					adapter.loadArg(2);
				div.visitEQ();
					adapter.push(count++);
				div.visitEnd(bc);
			cv.visitWhenAfterExprBeforeBody(bc);
				Expression defaultValue = arg.getDefaultValue();
				if(defaultValue!=null) arg.getDefaultValue().writeOut(bc, Expression.MODE_REF);
				else 
					adapter.visitInsn(Opcodes.ACONST_NULL);
				adapter.returnValue();
			cv.visitWhenAfterBody(bc);
		}
		cv.visitOtherviseBeforeBody();
			//adapter.visitInsn(ACONST_NULL);
			//adapter.returnValue();
		cv.visitOtherviseAfterBody();
		cv.visitAfter(bc);
	}

	private Function[] getFunctions() {
		Function[] funcs=new Function[functions.size()];
		Iterator it = functions.iterator();
		int count=0;
		while(it.hasNext()) {
			funcs[count++]=(Function) it.next();
		}
		return funcs;
	}
	
	private TagThread[] getThreads() {
		TagThread[] threads=new TagThread[this.threads.size()];
		Iterator it = this.threads.iterator();
		int count=0;
		while(it.hasNext()) {
			threads[count++]=(TagThread) it.next();
		}
		return threads;
	}
	
	
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		
	}

	private void writeOutNewComponent(BytecodeContext statConstr,BytecodeContext constr,List keys,ClassWriter cw, Tag component) throws BytecodeException {
		
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , NEW_COMPONENT_IMPL_INSTANCE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,keys,cw,name,adapter,NEW_COMPONENT_IMPL_INSTANCE);
    	Label methodBegin=new Label();
    	Label methodEnd=new Label();
    	
		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);

    	//ExpressionUtil.visitLine(adapter, component.getStartLine());
    	
		int comp=adapter.newLocal(Types.COMPONENT_IMPL);
		adapter.newInstance(Types.COMPONENT_IMPL);
		adapter.dup();
		
		Attribute attr;
		// ComponentPage
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		adapter.checkCast(COMPONENT_PAGE);

		// Output
		attr = component.removeAttribute("output");
		if(attr!=null) {
			ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		}
		else ASMConstants.NULL(adapter);

		// synchronized 
		attr = component.removeAttribute("synchronized");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_VALUE);
		else adapter.push(false);

		// extends
		attr = component.removeAttribute("extends");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");

		// implements
		attr = component.removeAttribute("implements");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
		
		// hint
		attr = component.removeAttribute("hint");
		if(attr!=null) {
			Expression value = attr.getValue();
			if(!(value instanceof Literal)){
				value=LitString.toExprString("[runtime expression]");
			}
			ExpressionUtil.writeOutSilent(value,bc, Expression.MODE_REF);
		}
		else adapter.push("");
		
		// dspName
		attr = component.removeAttribute("displayname");
		if(attr==null) attr=component.getAttribute("display");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
				
		// callpath
		adapter.visitVarInsn(Opcodes.ALOAD, 2);
		// realpath
		adapter.visitVarInsn(Opcodes.ILOAD, 3);
		
		
		// style
		attr = component.removeAttribute("style");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
		
		//adapter.visitVarInsn(Opcodes.ALOAD, 4);
		
		createMetaDataStruct(bc,component.getAttributes());
		
		adapter.invokeConstructor(Types.COMPONENT_IMPL, CONSTR_COMPONENT_IMPLX);
		
		adapter.storeLocal(comp);
		
		//ComponentImpl(ComponentPage componentPage,boolean output, String extend, String hint, String dspName)
		
		
		// initComponent(pc,c);
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		adapter.loadArg(0);
		adapter.loadLocal(comp);
		adapter.invokeVirtual(COMPONENT_PAGE, INIT_COMPONENT);
		
        adapter.visitLabel(methodEnd);
        
        // return component;
        adapter.loadLocal(comp);

        adapter.returnValue();
    	//ExpressionUtil.visitLine(adapter, component.getEndLine());
        adapter.endMethod();
		
        
	}
	
	private void writeOutNewInterface(BytecodeContext statConstr,BytecodeContext constr,List keys,ClassWriter cw, Tag interf) throws BytecodeException {
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , NEW_INTERFACE_IMPL_INSTANCE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,keys,cw,name,adapter,NEW_INTERFACE_IMPL_INSTANCE);
    	Label methodBegin=new Label();
    	Label methodEnd=new Label();

    	
		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);

    	//ExpressionUtil.visitLine(adapter, interf.getStartLine());
    	
		int comp=adapter.newLocal(Types.INTERFACE_IMPL);
		
		
		adapter.newInstance(Types.INTERFACE_IMPL);
		adapter.dup();
		
		
		Attribute attr;
		// Interface Page
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		adapter.checkCast(INTERFACE_PAGE);

		// extened
		attr = interf.removeAttribute("extends");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
		
		// hint
		attr = interf.removeAttribute("hint");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
		
		// dspName
		attr = interf.removeAttribute("displayname");
		if(attr==null) attr=interf.getAttribute("display");
		if(attr!=null) ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_REF);
		else adapter.push("");
		
		
		// callpath
		adapter.visitVarInsn(Opcodes.ALOAD, 1);
		// realpath
		adapter.visitVarInsn(Opcodes.ILOAD, 2);
		
		// interface udfs
		adapter.visitVarInsn(Opcodes.ALOAD, 3);
		
		
		adapter.invokeConstructor(Types.INTERFACE_IMPL, CONSTR_INTERFACE_IMPL);
		
		adapter.storeLocal(comp);
		
		
		
		// initInterface(pc,c);
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		//adapter.loadArg(0);
		adapter.loadLocal(comp);
		adapter.invokeVirtual(INTERFACE_PAGE, INIT_INTERFACE);
		
		adapter.visitLabel(methodEnd);
        
        
        // return interface;
        adapter.loadLocal(comp);

        adapter.returnValue();
    	//ExpressionUtil.visitLine(adapter, interf.getEndLine());
        adapter.endMethod();
		
        
	}
	
	
	
	public static void createMetaDataStruct(BytecodeContext bc, Map attrs) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		if(attrs==null || attrs.size()==0){
			ASMConstants.NULL(bc.getAdapter());
			bc.getAdapter().cast(Types.OBJECT,STRUCT_IMPL);
			return;
		}
		
		int sct=adapter.newLocal(STRUCT_IMPL);
		adapter.newInstance(STRUCT_IMPL);
		adapter.dup();
		adapter.invokeConstructor(STRUCT_IMPL, INIT_STRUCT_IMPL);
		adapter.storeLocal(sct);
		if(attrs!=null) {
			Attribute attr;
			Iterator it = attrs.entrySet().iterator();
			while(it.hasNext()){
				attr=(Attribute) ((Map.Entry)it.next()).getValue();
				adapter.loadLocal(sct);
				adapter.push(attr.getName());
				ExpressionUtil.writeOutSilent(attr.getValue(),bc,Expression.MODE_REF);
				adapter.invokeVirtual(STRUCT_IMPL, SET_EL);
				adapter.pop();
			}
		}
		adapter.loadLocal(sct);
	}

	private void writeOutCall(BytecodeContext statConstr,BytecodeContext constr,List keys,ClassWriter cw) throws BytecodeException {
		//GeneratorAdapter adapter = bc.getAdapter();
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , CALL, null, new Type[]{Types.THROWABLE}, cw);
	    	Label methodBegin=new Label();
	    	Label methodEnd=new Label();
	
			adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
	    	adapter.visitLabel(methodBegin);
	        
	        writeOutCallBody(new BytecodeContext(statConstr, constr,keys,cw,name,adapter,CALL),this,IFunction.PAGE_TYPE_REGULAR);
	        
	        adapter.visitLabel(methodEnd);
	        adapter.returnValue();
        adapter.endMethod();
    }


	private void writeOutCallBody(BytecodeContext bc,Body body, int pageType) throws BytecodeException {
		// Other
		writeOutFunctions(bc,body,pageType);
		
		
		if(pageType==IFunction.PAGE_TYPE_COMPONENT) {
			GeneratorAdapter adapter = bc.getAdapter();
			adapter.loadArg(1);
			adapter.loadArg(0);
			adapter.visitVarInsn(Opcodes.ALOAD, 0);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, CHECK_INTERFACE);

		}
		if(pageType!=IFunction.PAGE_TYPE_INTERFACE){
			BodyBase.writeOut(bc.getStaticConstructor(),bc.getConstructor(),bc.getKeys(),body.getStatements(), bc);
		}
	}

	private static void writeOutFunctions(BytecodeContext bc, Body body, int pageType) throws BytecodeException {
		if(ASMUtil.isEmpty(body)) return;
		Statement stat;
		List stats = body.getStatements();
    	int len=stats.size();
        for(int i=0;i<len;i++) {
        	stat = (Statement)stats.get(i);
        	
        	// IFunction
        	if(stat instanceof IFunction) {
        		((IFunction)stat).writeOut(bc,pageType);
        		stats.remove(i);
        		len--;
        		i--;
        	}
        	else if(stat instanceof HasBody) writeOutFunctions(bc, ((HasBody)stat).getBody(), pageType);
        	else if(stat instanceof HasBodies) {
        		Body[] bodies=((HasBodies)stat).getBodies();
        		for(int y=0;y<bodies.length;y++) {
        			writeOutFunctions(bc,bodies[y] , pageType);
        		}
        		
        	}
        }
	}


	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return if it is a component
	 */
	public boolean isComponent() {
		return isComponent;
	}
	
	/**
	 * set if the page is a component or not
	 * @param cfc 
	 */
	public void setIsComponent(boolean isComponent) {
		this.isComponent = isComponent;
	}

	/**
	 * @return if it is a component
	 */
	public boolean isInterface() {
		return isInterface;
	}
	
	/**
	 * set if the page is a component or not
	 * @param cfc 
	 */
	public void setIsInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public int addFunction(IFunction function) {
		functions.add(function);
		return functions.size()-1;
	}
	
	public int addThread(TagThread thread) {
		threads.add(thread);
		return threads.size()-1;
	}

}
