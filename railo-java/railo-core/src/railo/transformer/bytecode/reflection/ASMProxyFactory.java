package railo.transformer.bytecode.reflection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public class ASMProxyFactory {

	public static final Type ASM_METHOD=Type.getType(ASMMethod.class);
	public static final Type CLASS404=Type.getType(ClassNotFoundException.class);
	
	
	
	private static final org.objectweb.asm.commons.Method CONSTRUCTOR = 
    	new org.objectweb.asm.commons.Method("<init>",Types.VOID,new Type[]{Types.CLASS_LOADER,Types.CLASS});

	private static final org.objectweb.asm.commons.Method LOAD_CLASS = new org.objectweb.asm.commons.Method(
			"loadClass",
			Types.CLASS,
			new Type[]{Types.STRING});
	
	private static final org.objectweb.asm.commons.Method INVOKE = new org.objectweb.asm.commons.Method(
			"invoke",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.OBJECT_ARRAY});
	
	
	// primitive to reference type
	private static final org.objectweb.asm.commons.Method BOOL_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.BOOLEAN,new Type[]{Types.BOOLEAN_VALUE});
	private static final org.objectweb.asm.commons.Method SHORT_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.SHORT,new Type[]{Types.SHORT_VALUE});
	private static final org.objectweb.asm.commons.Method INT_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.INTEGER,new Type[]{Types.INT_VALUE});
	private static final org.objectweb.asm.commons.Method LONG_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.LONG,new Type[]{Types.LONG_VALUE});
	private static final org.objectweb.asm.commons.Method FLT_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.FLOAT,new Type[]{Types.FLOAT_VALUE});
	private static final org.objectweb.asm.commons.Method DBL_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.DOUBLE,new Type[]{Types.DOUBLE_VALUE});
	private static final org.objectweb.asm.commons.Method CHR_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.CHARACTER,new Type[]{Types.CHARACTER});
	private static final org.objectweb.asm.commons.Method BYT_VALUE_OF = new org.objectweb.asm.commons.Method("valueOf",Types.BYTE,new Type[]{Types.BYTE_VALUE});
	
	// reference type to primitive
	private static final org.objectweb.asm.commons.Method BOOL_VALUE = new org.objectweb.asm.commons.Method("booleanValue",Types.BOOLEAN_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method SHORT_VALUE = new org.objectweb.asm.commons.Method("shortValue",Types.SHORT_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method INT_VALUE = new org.objectweb.asm.commons.Method("intValue",Types.INT_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method LONG_VALUE = new org.objectweb.asm.commons.Method("longValue",Types.LONG_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method FLT_VALUE = new org.objectweb.asm.commons.Method("floatValue",Types.FLOAT_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method DBL_VALUE = new org.objectweb.asm.commons.Method("doubleValue",Types.DOUBLE_VALUE,new Type[]{});
	private static final org.objectweb.asm.commons.Method CHR_VALUE = new org.objectweb.asm.commons.Method("charValue",Types.CHAR,new Type[]{});
	private static final org.objectweb.asm.commons.Method BYT_VALUE = new org.objectweb.asm.commons.Method("byteValue",Types.BYTE_VALUE,new Type[]{});
	
	private static final org.objectweb.asm.commons.Method ASM_METHOD_CONSTRUCTOR = new org.objectweb.asm.commons.Method(
			"<init>",
			Types.VOID,
			new Type[]{Types.CLASS,Types.STRING,Types.CLASS_ARRAY,Types.CLASS,Types.CLASS_ARRAY,Types.INT_VALUE}
    		);
	
	private static final org.objectweb.asm.commons.Method ASM_METHOD_CONSTRUCTOR_TEST = new org.objectweb.asm.commons.Method(
			"<init>",
			Types.VOID,
			new Type[]{}
    		);
	
	
	private static final Map<String,ASMMethod>methods=new HashMap<String, ASMMethod>();
	
	public ASMProxyFactory(Object obj,Resource classRoot) throws IOException{
		
		load((ConfigWeb)ThreadLocalPageContext.getConfig(),obj.getClass());
		
	}
	
	public static void main(String[] args) throws IOException {
		ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		Resource root = frp.getResource("/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/wrappers/");

		new ASMProxyFactory("x", root);
		//new ASMProxyFactory(new Test(), root);
		
		
	}
	
	public ASMClass load(ConfigWeb config,Class clazz) throws IOException{
		Type type = Type.getType(clazz); 
		/*String className = clazz.getName()+"Proxy";
		
		Type type = Type.getType(clazz); 
		String name = "L"+type.getInternalName()+";";
		
		Type typeExtends = Type.getType(Object.class); 
		String[] strInterfaces=null;
		
		ClassWriter cw = ASMUtil.getClassWriter();
	    cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, typeExtends.getInternalName(), strInterfaces);
		
	    // Constructor
	    org.objectweb.asm.commons.Method constr = 
	    	new org.objectweb.asm.commons.Method("<init>",Types.VOID,new Type[]{type});

	    
	    GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,constr,null,null,cw);
        Label begin = new Label();
        adapter.visitLabel(begin);
		adapter.loadThis();
        adapter.invokeConstructor(Types.OBJECT, SUPER_CONSTRUCTOR);
        
        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitVarInsn(Opcodes.ALOAD, 1);
        adapter.visitFieldInsn(Opcodes.PUTFIELD, className, "obj", name);

        adapter.visitInsn(Opcodes.RETURN);
		Label end = new Label();
		adapter.visitLabel(end);
		adapter.visitLocalVariable("config",name, null, begin, end, 1);
		
        adapter.endMethod();
        
        */
        
        
        

	    // Fields
	    Field[] fields = clazz.getFields();
	    for(int i=0;i<fields.length;i++){
	    	if(Modifier.isPrivate(fields[i].getModifiers())) continue;
	    	createField(type,fields[i]);
	    }
	    
	    // Methods
	    Method[] methods = clazz.getMethods();
	    Map<String,ASMMethod> amethods=new HashMap<String, ASMMethod>();
	    for(int i=0;i<methods.length;i++){
	    	if(Modifier.isPrivate(methods[i].getModifiers())) continue;
	    	amethods.put(methods[i].getName(), getMethod(config,type,clazz,methods[i]));
	    }
	    
	    return new ASMClass(clazz.getName(),amethods);
	    
	}
	
	private void createField(Type type, Field field) {
		// TODO Auto-generated method stub
		
	}


	private ASMMethod getMethod(ConfigWeb config,Type type,Class clazz, Method method) throws IOException {
		String className = "method."+clazz.getName()+"."+method.getName()+paramNames(method.getParameterTypes());
		
		// check if already in memory cache
		ASMMethod asmm = methods.get(className);
		if(asmm!=null)return asmm;
		
		// check if class already exists
		// TODO
		
		// create and load
		PhysicalClassLoader cl = (PhysicalClassLoader)config.getRPCClassLoader(false);
		Resource classRoot = cl.getDirectory();
		
		
		
		try {
			byte[] barr = _createMethod(config, type, clazz, method, classRoot, className);
			return (ASMMethod) cl.loadClass(className, barr).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	private byte[] _createMethod(ConfigWeb config,Type type,Class clazz, Method method,Resource classRoot, String className) throws IOException {
		
		className=className.replace('.',File.separatorChar);
		Resource classFile=classRoot.getRealResource(className+".class");
		print.e(classFile);
		ClassWriter cw = ASMUtil.getClassWriter();
	    cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, ASM_METHOD.getInternalName(), null);
		

// CONSTRUCTOR

	    GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR,null,null,cw);
	    
	    Label begin = new Label();
        adapter.visitLabel(begin);
		adapter.loadThis();
        
	    
	    
	    // clazz
        adapter.visitVarInsn(Opcodes.ALOAD, 2);
        
        // name
        adapter.push(method.getName());
        
        // parameterTypes 
        Class<?>[] params = method.getParameterTypes();
        Type[] paramTypes = new Type[params.length];
	    ArrayVisitor av=new ArrayVisitor();
	    av.visitBegin(adapter, Types.CLASS, params.length);
	    for(int i=0;i<params.length;i++){
	    	paramTypes[i]=Type.getType(params[i]);
	    	av.visitBeginItem(adapter, i);
	    		loadClass(adapter,params[i]);
	    	av.visitEndItem(adapter);
	    }
	    av.visitEnd();
	    
	    // returnType
	    Class<?> rtn = method.getReturnType();
	    Type rtnType = Type.getType(rtn);
	    loadClass(adapter,method.getReturnType());
	    
		
		// exceptionTypes 
        Class<?>[] exceptions = method.getExceptionTypes();
	    av=new ArrayVisitor();
	    av.visitBegin(adapter, Types.CLASS, exceptions.length);
	    for(int i=0;i<exceptions.length;i++){
	    	av.visitBeginItem(adapter, i);
	    		loadClass(adapter,exceptions[i]);
	    	av.visitEndItem(adapter);
	    }
	    av.visitEnd();
	    
		// modifier
		adapter.push(method.getModifiers());

		adapter.invokeConstructor(ASM_METHOD, ASM_METHOD_CONSTRUCTOR);
		adapter.visitInsn(Opcodes.RETURN);
		
		Label end = new Label();
		adapter.visitLabel(end);
		
		adapter.endMethod();
       	
		
	// METHOD INVOKE
		adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC , INVOKE, null, null, cw);
        Label start=adapter.newLabel();
        adapter.visitLabel(start);
        
        // load Object
        adapter.visitVarInsn(Opcodes.ALOAD, 1);
        adapter.checkCast(type);
        
        // load params
        for(int i=0;i<params.length;i++){
        	adapter.visitVarInsn(Opcodes.ALOAD, 2);
        	adapter.push(i);
        	//adapter.visitInsn(Opcodes.ICONST_0);
        	adapter.visitInsn(Opcodes.AALOAD);
        	
        	adapter.checkCast(toReferenceType(params[i],paramTypes[i]));
        	
        	// cast
        	if(params[i]==boolean.class) adapter.invokeVirtual(Types.BOOLEAN, BOOL_VALUE);
        	else if(params[i]==short.class) adapter.invokeVirtual(Types.SHORT, SHORT_VALUE);
        	else if(params[i]==int.class) adapter.invokeVirtual(Types.INTEGER, INT_VALUE);
        	else if(params[i]==float.class) adapter.invokeVirtual(Types.FLOAT, FLT_VALUE);
        	else if(params[i]==long.class) adapter.invokeVirtual(Types.LONG, LONG_VALUE);
        	else if(params[i]==double.class) adapter.invokeVirtual(Types.DOUBLE, DBL_VALUE);
        	else if(params[i]==char.class) adapter.invokeVirtual(Types.CHARACTER, CHR_VALUE);
        	else if(params[i]==byte.class) adapter.invokeVirtual(Types.BYTE, BYT_VALUE);
        	//else adapter.checkCast(paramTypes[i]);
        	
        }
        
        
        // call method
    	final org.objectweb.asm.commons.Method m = new org.objectweb.asm.commons.Method(method.getName(),rtnType,paramTypes);
    	adapter.invokeVirtual(type, m);
         
    	
    	// return
    	if(rtn==void.class) ASMConstants.NULL(adapter);
    	
    	
    	// cast result to object
    	if(rtn==boolean.class) adapter.invokeStatic(Types.BOOLEAN, BOOL_VALUE_OF);
    	else if(rtn==short.class) adapter.invokeStatic(Types.SHORT, SHORT_VALUE_OF);
    	else if(rtn==int.class) adapter.invokeStatic(Types.INTEGER, INT_VALUE_OF);
    	else if(rtn==long.class) adapter.invokeStatic(Types.LONG, LONG_VALUE_OF);
    	else if(rtn==float.class) adapter.invokeStatic(Types.FLOAT, FLT_VALUE_OF);
    	else if(rtn==double.class) adapter.invokeStatic(Types.DOUBLE, DBL_VALUE_OF);
    	else if(rtn==char.class) adapter.invokeStatic(Types.CHARACTER, CHR_VALUE_OF);
    	else if(rtn==byte.class) adapter.invokeStatic(Types.BYTE, BYT_VALUE_OF);
    	
    	adapter.visitInsn(Opcodes.ARETURN);
        
        adapter.endMethod();
		
        /*mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(AALOAD);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lrailo/transformer/bytecode/reflection/substring_int;", null, l0, l1, 0);
        mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, l0, l1, 1);
        mv.visitLocalVariable("args", "[Ljava/lang/Object;", null, l0, l1, 2);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
		*/
        return store(cw.toByteArray(),classFile);
	}
	

	private Type toReferenceType(Class<?> clazz, Type defaultValue) {
		if(int.class==clazz) return Types.INTEGER;
		else if(long.class==clazz) return Types.LONG;
		else if(char.class==clazz) return Types.CHARACTER;
		else if(byte.class==clazz) return Types.BYTE;
		else if(float.class==clazz) return Types.FLOAT;
		else if(double.class==clazz) return Types.DOUBLE;
		else if(boolean.class==clazz) return Types.BOOLEAN;
		else if(short.class==clazz) return Types.SHORT;
		return defaultValue;
	}

	private void loadClass(GeneratorAdapter adapter, Class<?> clazz) {
		if(void.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
		
		// primitive types
		else if(int.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
		else if(long.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
		else if(char.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
		else if(byte.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
		else if(float.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
		else if(double.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
		else if(boolean.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
		else if(short.class==clazz) adapter.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
		
		// TODO ref types
		
		else {
		    adapter.visitVarInsn(Opcodes.ALOAD, 1);
	        adapter.push(clazz.getName());
			adapter.invokeVirtual(Types.CLASS_LOADER,LOAD_CLASS );
		}
	}

	private String paramNames(Class<?>[] params) {
		if(ArrayUtil.isEmpty(params)) return "";
		
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<params.length;i++){
			sb.append('$');
			sb.append(StringUtil.replace(Caster.toClassName(params[i]).replace('.', '_'),"[]","_arr",false));
		}
		return sb.toString();
	}

	private byte[] store(byte[] barr,Resource classFile) throws IOException {
		// create class file
        ResourceUtil.touch(classFile);
        IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
       return barr;
	}
	/*private void store(ClassWriter cw) {
		// create class file
        byte[] barr = cw.toByteArray();
    	
        try {
        	ResourceUtil.touch(classFile);
	        IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        
	        cl = (PhysicalClassLoader) mapping.getConfig().getRPCClassLoader(true);
	        Class<?> clazz = cl.loadClass(className, barr);
	        return newInstance(clazz, config,cfc);
        }
        catch(Throwable t) {
        	throw Caster.toPageException(t);
        }
	}*/
	
}
