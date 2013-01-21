package railo.transformer.bytecode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.io.res.Resource;
import railo.commons.lang.NumberUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.component.ImportDefintion;
import railo.runtime.component.ImportDefintionImpl;
import railo.runtime.exp.TemplateException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.KeyConstants;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.extern.StringExternalizerWriter;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Argument;
import railo.transformer.bytecode.statement.HasBodies;
import railo.transformer.bytecode.statement.HasBody;
import railo.transformer.bytecode.statement.IFunction;
import railo.transformer.bytecode.statement.NativeSwitch;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagImport;
import railo.transformer.bytecode.statement.tag.TagThread;
import railo.transformer.bytecode.statement.udf.Function;
import railo.transformer.bytecode.statement.udf.FunctionImpl;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;
import railo.transformer.bytecode.visitor.ConditionVisitor;
import railo.transformer.bytecode.visitor.DecisionIntVisitor;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.bytecode.visitor.TryCatchFinallyVisitor;

/**
 * represent a single Page like "index.cfm"
 */
public final class Page extends BodyBase {


	public void doFinalize(BytecodeContext bc) {
		ExpressionUtil.visitLine(bc, getEnd());
	}

	private static final Type KEY_IMPL = Type.getType(KeyImpl.class);
	private static final Type KEY_CONSTANTS = Type.getType(KeyConstants.class);
	private static final Method KEY_INIT = new Method(
			"init",
			Types.COLLECTION_KEY,
			new Type[]{Types.STRING}
    		);
	private static final Method KEY_INTERN = new Method(
			"intern",
			Types.COLLECTION_KEY,
			new Type[]{Types.STRING}
    		);
	
	// public static ImportDefintion getInstance(String fullname,ImportDefintion defaultValue)
	private static final Method ID_GET_INSTANCE = new Method(
			"getInstance",
			Types.IMPORT_DEFINITIONS,
			new Type[]{Types.STRING,Types.IMPORT_DEFINITIONS}
    		);

	public final static Method STATIC_CONSTRUCTOR = Method.getMethod("void <clinit> ()V");
	//public final static Method CONSTRUCTOR = Method.getMethod("void <init> ()V");

	private static final Method CONSTRUCTOR = new Method(
			"<init>",
			Types.VOID,
			new Type[]{}//
    		);
	private static final Method CONSTRUCTOR_PS = new Method(
			"<init>",
			Types.VOID,
			new Type[]{Types.PAGE_SOURCE}//
    		);

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
    
    private final static Method SET_PAGE_SOURCE = new Method(
			"setPageSource",
			Types.VOID,
			new Type[]{Types.PAGE_SOURCE}
    		);
    
    // public ImportDefintion[] getImportDefintions()
    private final static Method GET_IMPORT_DEFINITIONS = new Method(
			"getImportDefintions",
			Types.IMPORT_DEFINITIONS_ARRAY,
			new Type[]{}
    		);
    
    // long getSourceLastModified()
    private final static Method LAST_MOD = new Method(
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
	
	

	

	// void init(PageContext pc,Component Impl c) throws PageException
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
	
	


	
	


	private static final Method CONSTR_INTERFACE_IMPL = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.INTERFACE_PAGE,
						Types.STRING, // extends
						Types.STRING, // hind
						Types.STRING, // display
						Types.STRING, // callpath
						Types.BOOLEAN_VALUE, // realpath
						Types.MAP, //interfaceudfs
						Types.MAP // meta
					}
    		);
	
	
	//void init(PageContext pageContext,ComponentPage componentPage)
	private static final Method INIT = new Method(
			"init",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.COMPONENT_PAGE}
    		);
	
	private static final Method CHECK_INTERFACE = new Method(
			"checkInterface",
			Types.VOID,
			new Type[]{Types.PAGE_CONTEXT,Types.COMPONENT_PAGE}
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
	
	
	// Component Impl(ComponentPage,boolean, String, String, String, String) WS==With Style
	private static final Method CONSTR_COMPONENT_IMPL = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COMPONENT_PAGE,
					Types.BOOLEAN,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					Types.BOOLEAN_VALUE,
					Types.BOOLEAN_VALUE,
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
	public static final byte CF = (byte)207;
	public static final byte _33 = (byte)51;
	private static final boolean ADD_C33 = false;
	//private static final String SUB_CALL_UDF = "udfCall";
	private static final String SUB_CALL_UDF = "_";
		
    private int version;
    private long lastModifed;
    private String name;
    
    //private Body body=new Body();
	private Resource source;
	private final String path;
	private boolean isComponent;
	private boolean isInterface;

	private List<IFunction> functions=new ArrayList<IFunction>();
	private List threads=new ArrayList();
	private boolean _writeLog;
	private StringExternalizerWriter externalizer;
	private boolean supressWSbeforeArg;
    
	
	
    public Page(Resource source,String name,int version, long lastModifed, boolean writeLog, boolean supressWSbeforeArg) {
    	name=name.replace('.', '/');
    	//body.setParent(this);
        this.name=name;
        this.version=version;
        this.lastModifed=lastModifed;
        this.source=source;
        this.path=source.getAbsolutePath();
        
        this._writeLog=writeLog;
        this.supressWSbeforeArg=supressWSbeforeArg;
    }
    
    /**
     * result byte code as binary array
     * @param classFile 
     * @return byte code
     * @throws IOException 
     * @throws TemplateException 
     */
    public byte[] execute(Resource classFile) throws BytecodeException {
    	
    	Resource p = classFile.getParentResource().getRealResource(classFile.getName()+".txt");
        this.externalizer=new StringExternalizerWriter(p);
		
    	List keys=new ArrayList();
    	ClassWriter cw = ASMUtil.getClassWriter(); 
    	//ClassWriter cw = new ClassWriter(true);
    	
    	ArrayList<String> list = new ArrayList<String>();
        getImports(list, this); 
    	
    	// parent
    	String parent="railo/runtime/Page";
    	if(isComponent()) parent="railo/runtime/ComponentPage";
    	else if(isInterface()) parent="railo/runtime/InterfacePage";
    	
    	cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL, name, null, parent, null);
        cw.visitSource(this.path, null);

        // static constructor
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,STATIC_CONSTRUCTOR,null,null,cw);
		BytecodeContext statConstr = new BytecodeContext(null,null,this,externalizer,keys,cw,name,ga,STATIC_CONSTRUCTOR,writeLog(),supressWSbeforeArg);
		
		// private static  ImportDefintion[] test=new ImportDefintion[]{...};
	    if(list.size()>0){
			FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, 
					"imports", "[Lrailo/runtime/component/ImportDefintion;", null, null);
			fv.visitEnd();
		
			GeneratorAdapter adapter = statConstr.getAdapter();
			ArrayVisitor av=new ArrayVisitor();
			av.visitBegin(adapter,Types.IMPORT_DEFINITIONS,list.size());
			int index=0;
			Iterator<String> it = list.iterator();
			while(it.hasNext()){
				av.visitBeginItem(adapter,index++);
				adapter.push(it.next());
				ASMConstants.NULL(adapter);
				adapter.invokeStatic(Types.IMPORT_DEFINITIONS_IMPL, ID_GET_INSTANCE);
				av.visitEndItem(adapter);
			}
			av.visitEnd();
			adapter.visitFieldInsn(Opcodes.PUTSTATIC, name, "imports", "[Lrailo/runtime/component/ImportDefintion;");
				
		}
		
		
        // constructor
        ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR_PS,null,null,cw);
		BytecodeContext constr = new BytecodeContext(null,null,this,externalizer,keys,cw,name,ga,CONSTRUCTOR_PS,writeLog(),supressWSbeforeArg);
		ga.loadThis();
        Type t=Types.PAGE;
        if(isComponent())t=Types.COMPONENT_PAGE;
        else if(isInterface())t=Types.INTERFACE_PAGE;
        
        ga.invokeConstructor(t, CONSTRUCTOR);

        
        //setPageSource(pageSource);
        ga.visitVarInsn(Opcodes.ALOAD, 0);
        ga.visitVarInsn(Opcodes.ALOAD, 1);
        ga.invokeVirtual(t, SET_PAGE_SOURCE);
        
        
        
 
        
     // getVersion
         GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , VERSION, null, null, cw);
         adapter.push(version);
         adapter.returnValue();
         adapter.endMethod();
         
         
    // public ImportDefintion[] getImportDefintions()
         if(list.size()>0){
        	 adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , GET_IMPORT_DEFINITIONS, null, null, cw);
             adapter.visitFieldInsn(Opcodes.GETSTATIC, name, "imports", "[Lrailo/runtime/component/ImportDefintion;");
        	 adapter.returnValue();
             adapter.endMethod();
         }
         

         
// getSourceLastModified
        adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , LAST_MOD, null, null, cw);
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
            BytecodeContext bc = new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,UDF_CALL,writeLog(),supressWSbeforeArg);
            if(functions.length==0){}
            else if(functions.length==1){
        		ExpressionUtil.visitLine(bc,functions[0].getStart());
        		functions[0].getBody().writeOut(bc);
        		ExpressionUtil.visitLine(bc,functions[0].getEnd());
        	}
        	else writeOutUdfCallInner(bc,functions,0,functions.length);
            adapter.visitInsn(Opcodes.ACONST_NULL);
            adapter.returnValue();
            adapter.endMethod(); 
        }
   // more than 10 functions
        else {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_CALL, null, new Type[]{Types.THROWABLE}, cw);
        	BytecodeContext bc = new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,UDF_CALL,writeLog(),supressWSbeforeArg);
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
			        	adapter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, createFunctionName(++count), "(Lrailo/runtime/PageContext;Lrailo/runtime/type/UDF;I)Ljava/lang/Object;");
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
	        	innerCall = new Method(createFunctionName(++count),Types.OBJECT,new Type[]{Types.PAGE_CONTEXT, USER_DEFINED_FUNCTION, Types.INT_VALUE});
	        	
	        	adapter = new GeneratorAdapter(Opcodes.ACC_PRIVATE+Opcodes.ACC_FINAL , innerCall, null, new Type[]{Types.THROWABLE}, cw);
	        	writeOutUdfCallInner(new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,innerCall,writeLog(),supressWSbeforeArg), functions, i, i+10>functions.length?functions.length:i+10);
	        	
	        	adapter.visitInsn(Opcodes.ACONST_NULL);
		        adapter.returnValue();
		        adapter.endMethod();
	        }
        }
        

     // threadCall
             TagThread[] threads=getThreads();
             if(threads.length>0) {
             	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , THREAD_CALL, null, new Type[]{Types.THROWABLE}, cw);
         			writeOutThreadCallInner(new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,THREAD_CALL,writeLog(),supressWSbeforeArg),threads,0,threads.length);
         		//adapter.visitInsn(Opcodes.ACONST_NULL);
         		adapter.returnValue();
         		adapter.endMethod();
             }
        

        
                
// udfDefaultValue
    // less/equal than 10 functions
        if(functions.length==0 || isInterface()) {}
        else if(functions.length<=10) {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_DEFAULT_VALUE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
            writeUdfDefaultValueInner(new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,UDF_DEFAULT_VALUE,writeLog(),supressWSbeforeArg),functions,0,functions.length);
            
            ASMConstants.NULL(adapter);
        	adapter.returnValue();
            adapter.endMethod();
        }
        else {
        	adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , UDF_DEFAULT_VALUE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
            BytecodeContext bc = new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,UDF_DEFAULT_VALUE,writeLog(),supressWSbeforeArg);
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
        	writeUdfDefaultValueInner(new BytecodeContext(statConstr,constr,this,externalizer,keys,cw,name,adapter,innerDefaultValue,writeLog(),supressWSbeforeArg), functions, i, i+10>functions.length?functions.length:i+10);
        	
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
    	
        try {
			if(externalizer!=null)externalizer.writeOut();
		} catch (IOException e) {
			throw new BytecodeException(e.getMessage(), null);
		}
        
        
        if(ADD_C33) {
        	byte[] tmp = cw.toByteArray();
	        byte[] bLastMod=NumberUtil.longToByteArray(lastModifed);
	        byte[] barr = new byte[tmp.length+10];
	        // Magic Number
	        barr[0]=CF; // CF
	        barr[1]=_33; // 33
	        
	        // Last Modified
	        for(int i=0;i<8;i++){
	        	barr[i+2]=bLastMod[i];
	        }
	        for(int i=0;i<tmp.length;i++){
	        	barr[i+10]=tmp[i];
	        }
	        return barr;
        }
        return cw.toByteArray();
 	
    }
    



	private String createFunctionName(int i) {
		return "_"+Integer.toString(i, Character.MAX_RADIX);
	}

	private boolean writeLog() {
		return _writeLog && !isInterface();
	}

	public static void registerFields(BytecodeContext bc, List keys) throws BytecodeException {
		//if(keys.size()==0) return;
		
		GeneratorAdapter ga = bc.getAdapter();
		
		FieldVisitor fv = bc.getClassWriter().visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
				"keys", Types.COLLECTION_KEY_ARRAY.toString(), null, null);
		fv.visitEnd();
		
		
		int index=0;
		LitString value;
		Iterator it = keys.iterator();
		//ga.visitVarInsn(Opcodes.ALOAD, 0);
		ga.push(keys.size());
		ga.newArray(Types.COLLECTION_KEY);
		while(it.hasNext()) {
			value=(LitString) it.next();
			ga.dup();
			ga.push(index++);
			
			/*String str = value.getString();
			if(KeyConstants.hasConstant(str)) {
				ga.getStatic(KEY_CONSTANTS, "_"+str, Types.COLLECTION_KEY);
			}
			else {*/
				ExpressionUtil.writeOutSilent(value,bc, Expression.MODE_REF);
				ga.invokeStatic(KEY_IMPL, KEY_INTERN);
			//}
			ga.visitInsn(Opcodes.AASTORE);
		}
		ga.visitFieldInsn(Opcodes.PUTSTATIC, 
				bc.getClassName(), "keys", Types.COLLECTION_KEY_ARRAY.toString());

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
        		ExpressionUtil.visitLine(bc, functions[i].getStart());
        		functions[i].getBody().writeOut(bc);
        		ExpressionUtil.visitLine(bc, functions[i].getEnd());
        	cv.visitWhenAfterBody(bc);
        }
        cv.visitAfter(bc);
	}

	private void writeOutUdfCallInner(BytecodeContext bc,Function[] functions, int offset, int length) throws BytecodeException {
		NativeSwitch ns=new NativeSwitch(2,NativeSwitch.ARG_REF,null,null);
		
		for(int i=offset;i<length;i++) {
        	ns.addCase(i, functions[i].getBody(),functions[i].getStart(),functions[i].getEnd(),true);
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
	        		Body body = threads[i].getRealBody();
	        		if(body!=null)body.writeOut(bc);
	        	cv.visitWhenAfterBody(bc);
	        }
        cv.visitAfter(bc);
	}

	private void writeOutInitComponent(BytecodeContext statConstr,BytecodeContext constr,List keys, ClassWriter cw, Tag component) throws BytecodeException {
		final GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , INIT_COMPONENT, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,this,externalizer,keys,cw,name,adapter,INIT_COMPONENT,writeLog(),supressWSbeforeArg);
		Label methodBegin=new Label();
    	Label methodEnd=new Label();

		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);
        
		// Scope oldData=null;
		final int oldData=adapter.newLocal(Types.VARIABLES);
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
			final int oldCheckArgs = adapter.newLocal(Types.INT_VALUE);
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, UNDEFINED_SCOPE);
			adapter.push(Undefined.MODE_NO_LOCAL_AND_ARGUMENTS);
			adapter.invokeInterface(Types.UNDEFINED, SET_MODE);
			adapter.storeLocal(oldCheckArgs);
		
			
		TryCatchFinallyVisitor tcf=new TryCatchFinallyVisitor(new OnFinally() {
			
			public void writeOut(BytecodeContext bc) {

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
				
				
			}
		},null);
		tcf.visitTryBegin(bc);
			// oldData=c.beforeCall(pc);
			adapter.loadArg(1);
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, BEFORE_CALL);
			adapter.storeLocal(oldData);
			ExpressionUtil.visitLine(bc, component.getStart());
			writeOutCallBody(bc,component.getBody(),IFunction.PAGE_TYPE_COMPONENT);
			ExpressionUtil.visitLine(bc, component.getEnd());
		int t = tcf.visitTryEndCatchBeging(bc);
			// BodyContentUtil.flushAndPop(pc,bc);
			adapter.loadArg(0);
			adapter.loadLocal(localBC);
			adapter.invokeStatic(Types.BODY_CONTENT_UTIL, FLUSH_AND_POP);
		
			// throw Caster.toPageException(t);
			adapter.loadLocal(t);
			adapter.invokeStatic(Types.CASTER, TO_PAGE_EXCEPTION);
			adapter.throwException();
		tcf.visitCatchEnd(bc);
		
		adapter.loadArg(0);
		adapter.loadLocal(localBC);
		adapter.invokeStatic(Types.BODY_CONTENT_UTIL, CLEAR_AND_POP);
	
    	adapter.returnValue();
	    adapter.visitLabel(methodEnd);
	    
	    adapter.endMethod();
    	
	}
	
	private void writeOutInitInterface(BytecodeContext statConstr,BytecodeContext constr,List keys, ClassWriter cw, Tag interf) throws BytecodeException {
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , INIT_INTERFACE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,this,externalizer,keys,cw,name,adapter,INIT_INTERFACE,writeLog(),supressWSbeforeArg);
		Label methodBegin=new Label();
    	Label methodEnd=new Label();

		adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
    	adapter.visitLabel(methodBegin);
        
    	ExpressionUtil.visitLine(bc, interf.getStart());
		writeOutCallBody(bc,interf.getBody(),IFunction.PAGE_TYPE_INTERFACE);
		ExpressionUtil.visitLine(bc, interf.getEnd());
		
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
		throw new BytecodeException("missing component",getStart());
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
		throw new BytecodeException("missing interface",getStart());
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
        BytecodeContext bc=new BytecodeContext(statConstr, constr,this,externalizer,keys,cw,name,adapter,NEW_COMPONENT_IMPL_INSTANCE,writeLog(),supressWSbeforeArg);
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
		adapter.checkCast(Types.COMPONENT_PAGE);

		// !!! also check CFMLScriptTransformer.addMetaData if you do any change here !!!
		
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

		// persistent
		attr = component.removeAttribute("persistent");
		boolean persistent=false;
		if(attr!=null) {
			persistent=ASMUtil.toBoolean(attr,component.getStart()).booleanValue();
		}
		
		// persistent
		attr = component.removeAttribute("accessors");
		boolean accessors=false;
		if(attr!=null) {
			accessors=ASMUtil.toBoolean(attr,component.getStart()).booleanValue();
		}

		adapter.push(persistent);
		adapter.push(accessors);
		//ExpressionUtil.writeOutSilent(attr.getValue(),bc, Expression.MODE_VALUE);
		
		//adapter.visitVarInsn(Opcodes.ALOAD, 4);
		createMetaDataStruct(bc,component.getAttributes(),component.getMetaData());
		
		adapter.invokeConstructor(Types.COMPONENT_IMPL, CONSTR_COMPONENT_IMPL);
		
		adapter.storeLocal(comp);
		
		//Component Impl(ComponentPage componentPage,boolean output, String extend, String hint, String dspName)
		
		
		// initComponent(pc,c);
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		adapter.loadArg(0);
		adapter.loadLocal(comp);
		adapter.invokeVirtual(Types.COMPONENT_PAGE, INIT_COMPONENT);
		
        adapter.visitLabel(methodEnd);
        
        // return component;
        adapter.loadLocal(comp);

        adapter.returnValue();
    	//ExpressionUtil.visitLine(adapter, component.getEndLine());
        adapter.endMethod();
		
        
	}
	
	private void writeOutNewInterface(BytecodeContext statConstr,BytecodeContext constr,List keys,ClassWriter cw, Tag interf) throws BytecodeException {
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , NEW_INTERFACE_IMPL_INSTANCE, null, new Type[]{Types.PAGE_EXCEPTION}, cw);
        BytecodeContext bc=new BytecodeContext(statConstr, constr,this,externalizer,keys,cw,name,adapter,NEW_INTERFACE_IMPL_INSTANCE,writeLog(),supressWSbeforeArg);
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
		adapter.checkCast(Types.INTERFACE_PAGE);

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
		
		createMetaDataStruct(bc,interf.getAttributes(),interf.getMetaData());
		
		
		adapter.invokeConstructor(Types.INTERFACE_IMPL, CONSTR_INTERFACE_IMPL);
		
		adapter.storeLocal(comp);
		
		
		
		// initInterface(pc,c);
		adapter.visitVarInsn(Opcodes.ALOAD, 0);
		//adapter.loadArg(0);
		adapter.loadLocal(comp);
		adapter.invokeVirtual(Types.INTERFACE_PAGE, INIT_INTERFACE);
		
		adapter.visitLabel(methodEnd);
        
        
        // return interface;
        adapter.loadLocal(comp);

        adapter.returnValue();
    	//ExpressionUtil.visitLine(adapter, interf.getEndLine());
        adapter.endMethod();
		
        
	}
	
	public static boolean hasMetaDataStruct(Map attrs, Map meta) {
		if((attrs==null || attrs.size()==0) && (meta==null || meta.size()==0)){
			return false;
		}
		return true;
	}
	
	public static void createMetaDataStruct(BytecodeContext bc, Map attrs, Map meta) throws BytecodeException {
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		if((attrs==null || attrs.size()==0) && (meta==null || meta.size()==0)){
			ASMConstants.NULL(bc.getAdapter());
			bc.getAdapter().cast(Types.OBJECT,STRUCT_IMPL);
			return;
		}
		
		int sct=adapter.newLocal(STRUCT_IMPL);
		adapter.newInstance(STRUCT_IMPL);
		adapter.dup();
		adapter.invokeConstructor(STRUCT_IMPL, INIT_STRUCT_IMPL);
		adapter.storeLocal(sct);
		if(meta!=null) {
			_createMetaDataStruct(bc,adapter,sct,meta);
		}
		if(attrs!=null) {
			_createMetaDataStruct(bc,adapter,sct,attrs);
		}
		
		
		adapter.loadLocal(sct);
	}

	private static void _createMetaDataStruct(BytecodeContext bc, GeneratorAdapter adapter, int sct, Map attrs) throws BytecodeException {
		Attribute attr;
		Iterator it = attrs.entrySet().iterator();
		Entry entry;
		while(it.hasNext()){
			entry = (Map.Entry)it.next();
			attr=(Attribute) entry.getValue();
			adapter.loadLocal(sct);
			adapter.push(attr.getName());
			if(attr.getValue() instanceof Literal)
				ExpressionUtil.writeOutSilent(attr.getValue(),bc,Expression.MODE_REF);
			else
				adapter.push("[runtime expression]");
			
			adapter.invokeVirtual(STRUCT_IMPL, SET_EL);
			adapter.pop();
		}
	}

	private void writeOutCall(BytecodeContext statConstr,BytecodeContext constr,List keys,ClassWriter cw) throws BytecodeException {
		//GeneratorAdapter adapter = bc.getAdapter();
		GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , CALL, null, new Type[]{Types.THROWABLE}, cw);
	    	Label methodBegin=new Label();
	    	Label methodEnd=new Label();
	
			adapter.visitLocalVariable("this", "L"+name+";", null, methodBegin, methodEnd, 0);
	    	adapter.visitLabel(methodBegin);

	    		        writeOutCallBody(new BytecodeContext(statConstr, constr,this,externalizer,keys,cw,name,adapter,CALL,writeLog(),supressWSbeforeArg),this,IFunction.PAGE_TYPE_REGULAR);
	        
	        adapter.visitLabel(methodEnd);
	        adapter.returnValue();
        adapter.endMethod();
    }


	private void writeOutCallBody(BytecodeContext bc,Body body, int pageType) throws BytecodeException {
		// Other
		List<IFunction> functions=new ArrayList<IFunction>();
		getFunctions(functions,bc,body,pageType);
		
		String className = Types.UDF_PROPERTIES_ARRAY.toString();
		//FieldVisitor fv = bc.getClassWriter().visitField(Opcodes.ACC_PRIVATE , "udfs",className , null, null);
		//fv.visitEnd();
		
		BytecodeContext constr = bc.getConstructor();
		GeneratorAdapter cga = constr.getAdapter();
		
		cga.visitVarInsn(Opcodes.ALOAD, 0);
		cga.push(functions.size());
		//cga.visitTypeInsn(Opcodes.ANEWARRAY, Types.UDF_PROPERTIES.toString());
		cga.newArray(Types.UDF_PROPERTIES);
		cga.visitFieldInsn(Opcodes.PUTFIELD, bc.getClassName(), "udfs", className);
		
		
		Iterator<IFunction> it = functions.iterator();
		while(it.hasNext()){
			it.next().writeOut(bc, pageType);
		}
		
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

	private static void getImports(List<String> list,Body body) throws BytecodeException {
		if(ASMUtil.isEmpty(body)) return;
		Statement stat;
		List stats = body.getStatements();
    	int len=stats.size();
        for(int i=0;i<len;i++) {
        	stat = (Statement)stats.get(i);
        	
        	// IFunction
        	if(stat instanceof TagImport && !StringUtil.isEmpty(((TagImport)stat).getPath(),true)) {
        		ImportDefintion id = ImportDefintionImpl.getInstance(((TagImport) stat).getPath(), null);
        		if(id!=null && (!list.contains(id.toString()) && !list.contains(id.getPackage()+".*"))){
        			list.add(id.toString());
        		}
        		stats.remove(i);
        		len--;
        		i--;
        		
        	}
        	else if(stat instanceof HasBody) getImports(list, ((HasBody)stat).getBody());
        	else if(stat instanceof HasBodies) {
        		Body[] bodies=((HasBodies)stat).getBodies();
        		for(int y=0;y<bodies.length;y++) {
        			getImports(list,bodies[y]);
        		}
        	}
        }
	}
	
	
	private static void getFunctions(List<IFunction> functions,BytecodeContext bc, Body body, int pageType) throws BytecodeException {
		//writeOutImports(bc, body, pageType);
		if(ASMUtil.isEmpty(body)) return;
		Statement stat;
		List stats = body.getStatements();
    	int len=stats.size();
        for(int i=0;i<len;i++) {
        	stat = (Statement)stats.get(i);
        	
        	// IFunction
        	if(stat instanceof IFunction) {
        		functions.add((IFunction)stat);
        		//((IFunction)stat).writeOut(bc,pageType);
        		stats.remove(i);
        		len--;
        		i--;
        	}
        	else if(stat instanceof HasBody) getFunctions(functions,bc, ((HasBody)stat).getBody(), pageType);
        	else if(stat instanceof HasBodies) {
        		Body[] bodies=((HasBodies)stat).getBodies();
        		for(int y=0;y<bodies.length;y++) {
        			getFunctions(functions,bc,bodies[y] , pageType);
        		}
        		
        	}
        }
	}


	/**
	 * @return the source
	 */
	public String getSource() {
		return path;
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
	

	public boolean isPage() {
		return !isInterface && !isComponent;
	}
	
	/**
	 * set if the page is a component or not
	 * @param cfc 
	 */
	public void setIsInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	/**
	 * @return the lastModifed
	 */
	public long getLastModifed() {
		return lastModifed;
	}


	public int[] addFunction(IFunction function) {
		int[] indexes=new int[2];
		Iterator<IFunction> it = functions.iterator();
		while(it.hasNext()){
			if(it.next() instanceof FunctionImpl)indexes[IFunction.ARRAY_INDEX]++;
		}
		indexes[IFunction.VALUE_INDEX]=functions.size();
		
		functions.add(function);
		return indexes;
	}
	
	public int addThread(TagThread thread) {
		threads.add(thread);
		return threads.size()-1;
	}

	public static byte[] setSourceLastModified(byte[] barr,  long lastModified) {
		ClassReader cr = new ClassReader(barr);
		ClassWriter cw = ASMUtil.getClassWriter();
		ClassVisitor ca = new SourceLastModifiedClassAdapter(cw,lastModified);
		cr.accept(ca, ClassReader.SKIP_DEBUG);
		return cw.toByteArray();
	}
	


}
	class SourceLastModifiedClassAdapter extends ClassVisitor {

		private long lastModified;
		public SourceLastModifiedClassAdapter(ClassWriter cw, long lastModified) {
			super(Opcodes.ASM4,cw);
			this.lastModified=lastModified;
		}
		public MethodVisitor visitMethod(int access,String name, String desc,  String signature, String[] exceptions) {
			
			if(!name.equals("getSourceLastModified"))return super.visitMethod(access,name, desc, signature, exceptions);
			
			MethodVisitor mv = cv.visitMethod(access,name, desc, signature, exceptions);
			mv.visitCode();
			mv.visitLdcInsn(Long.valueOf(lastModified));
			mv.visitInsn(Opcodes.LRETURN);
			mv.visitEnd();
			return mv;
		}

	}
