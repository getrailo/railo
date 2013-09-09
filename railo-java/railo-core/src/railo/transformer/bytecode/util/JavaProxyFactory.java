package railo.transformer.bytecode.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.KeyGenerator;
import railo.commons.lang.PhysicalClassLoader;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.java.JavaProxy;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.transformer.bytecode.visitor.ArrayVisitor;

/**
 * creates a Java Proxy for components, so you can use componets as java classes following a certain interface or class
 */
public class JavaProxyFactory {
	

	private static final String COMPONENT_NAME="L"+Types.COMPONENT.getInternalName()+";";
	private static final String CONFIG_WEB_NAME="L"+Types.CONFIG_WEB.getInternalName()+";";

	private static final Type JAVA_PROXY = Type.getType(JavaProxy.class);

	
	private static final org.objectweb.asm.commons.Method CALL = new org.objectweb.asm.commons.Method(
			"call",
			Types.OBJECT,
			new Type[]{Types.CONFIG_WEB,Types.COMPONENT,Types.STRING,Types.OBJECT_ARRAY});
	
	private static final org.objectweb.asm.commons.Method CONSTRUCTOR = new org.objectweb.asm.commons.Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.CONFIG_WEB,
					Types.COMPONENT
				}
    		);
	private static final org.objectweb.asm.commons.Method SUPER_CONSTRUCTOR = new org.objectweb.asm.commons.Method(
			"<init>",
			Types.VOID,
			new Type[]{}
    		);

	private static final org.objectweb.asm.commons.Method TO_BOOLEAN = new org.objectweb.asm.commons.Method(
			"toBoolean",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_FLOAT = new org.objectweb.asm.commons.Method(
			"toFloat",
			Types.FLOAT_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_INT = new org.objectweb.asm.commons.Method(
			"toInt",
			Types.INT_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_DOUBLE = new org.objectweb.asm.commons.Method(
			"toDouble",
			Types.DOUBLE_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_LONG = new org.objectweb.asm.commons.Method(
			"toLong",
			Types.LONG_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_CHAR = new org.objectweb.asm.commons.Method(
			"toChar",
			Types.CHAR,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_BYTE = new org.objectweb.asm.commons.Method(
			"toByte",
			Types.BYTE_VALUE,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_SHORT = new org.objectweb.asm.commons.Method(
			"toShort",
			Types.SHORT,
			new Type[]{Types.OBJECT});
	private static final org.objectweb.asm.commons.Method TO_ = new org.objectweb.asm.commons.Method(
			"to",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.CLASS});
	
	

	private static final org.objectweb.asm.commons.Method _BOOLEAN = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.BOOLEAN_VALUE});
	private static final org.objectweb.asm.commons.Method _FLOAT = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.FLOAT_VALUE});
	private static final org.objectweb.asm.commons.Method _INT = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.INT_VALUE});
	private static final org.objectweb.asm.commons.Method _DOUBLE = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.DOUBLE_VALUE});
	private static final org.objectweb.asm.commons.Method _LONG = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.LONG_VALUE});
	private static final org.objectweb.asm.commons.Method _CHAR = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.CHAR});
	private static final org.objectweb.asm.commons.Method _BYTE = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.BYTE_VALUE});
	private static final org.objectweb.asm.commons.Method _SHORT = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.SHORT});
	private static final org.objectweb.asm.commons.Method _OBJECT = new org.objectweb.asm.commons.Method(
			"toCFML",
			Types.OBJECT,
			new Type[]{Types.OBJECT});

	
	
	
	
	
/*

	public static Object to(Object obj, Class clazz) {
		return obj;
	}*/
	
	
	

	/*public static Object createProxy(Config config,Component cfc, String className) throws PageException, IOException {
		return createProxy(cfc, null, ClassUtil.loadClass(config.getClassLoader(), className));
	}*/

	public static Object createProxy(PageContext pc, Component cfc, Class extendz,Class... interfaces) throws PageException, IOException {
		PageContextImpl pci=(PageContextImpl) pc;
		if(extendz==null) extendz=Object.class;
		if(interfaces==null) interfaces=new Class[0];
		else {
			for(int i=0;i<interfaces.length;i++){
				if(!interfaces[i].isInterface()) 
					throw new IOException("definition ["+interfaces[i].getName()+"] is a class and not a interface");
			}
		}
		
		
		
		Type typeExtends = Type.getType(extendz); 
		Type[] typeInterfaces = ASMUtil.toTypes(interfaces); 
		String[] strInterfaces=new String[typeInterfaces.length];
		for(int i=0;i<strInterfaces.length;i++){
			strInterfaces[i]=typeInterfaces[i].getInternalName();
		}
		
		
		String className=createClassName(extendz,interfaces);
    	//Mapping mapping = cfc.getPageSource().getMapping();
		
    	// get ClassLoader
    	PhysicalClassLoader cl=null;
		try {
			cl = (PhysicalClassLoader) pci.getRPCClassLoader(false);// mapping.getConfig().getRPCClassLoader(false)
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		Resource classFile = cl.getDirectory().getRealResource(className.concat(".class"));
		
		// check if already exists, if yes return
		if(classFile.exists()) {
			//Object obj=newInstance(cl,className,cfc);
			// if(obj!=null) return obj;
		}
		
		/*
		String classNameOriginal=component.getPageSource().getFullClassName();
    	String realOriginal=classNameOriginal.replace('.','/');
		Resource classFileOriginal = mapping.getClassRootDirectory().getRealResource(realOriginal.concat(".class"));
		*/	
		ClassWriter cw = ASMUtil.getClassWriter();
	    
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, className, null, typeExtends.getInternalName(), strInterfaces);
		//BytecodeContext statConstr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.STATIC_CONSTRUCTOR);
		//BytecodeContext constr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.CONSTRUCTOR);
		
		
		// field Component
		FieldVisitor _fv = cw.visitField(Opcodes.ACC_PRIVATE, "cfc", COMPONENT_NAME, null, null);
		_fv.visitEnd();
		_fv = cw.visitField(Opcodes.ACC_PRIVATE, "config", CONFIG_WEB_NAME, null, null);
		_fv.visitEnd();
		
		 // Constructor
        GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR,null,null,cw);
        Label begin = new Label();
        adapter.visitLabel(begin);
		adapter.loadThis();
        adapter.invokeConstructor(Types.OBJECT, SUPER_CONSTRUCTOR);
        
        //adapter.putField(JAVA_PROXY, arg1, arg2)

        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitVarInsn(Opcodes.ALOAD, 1);
        adapter.visitFieldInsn(Opcodes.PUTFIELD, className, "config", CONFIG_WEB_NAME);

        adapter.visitVarInsn(Opcodes.ALOAD, 0);
        adapter.visitVarInsn(Opcodes.ALOAD, 2);
        adapter.visitFieldInsn(Opcodes.PUTFIELD, className, "cfc", COMPONENT_NAME);
		
        adapter.visitInsn(Opcodes.RETURN);
		Label end = new Label();
		adapter.visitLabel(end);
		adapter.visitLocalVariable("config",CONFIG_WEB_NAME, null, begin, end, 1);
		adapter.visitLocalVariable("cfc",COMPONENT_NAME, null, begin, end, 2);
		
        //adapter.returnValue();
        adapter.endMethod();
        
		
		// create methods
		Set<Class> cDone=new HashSet<Class>();
		Map<String,Class> mDone=new HashMap<String,Class>();
		for(int i=0;i<interfaces.length;i++){
			_createProxy(cw,cDone,mDone, cfc, interfaces[i],className);
		}
        cw.visitEnd();
        
        
        // create class file
        byte[] barr = cw.toByteArray();
    	
        try {
        	ResourceUtil.touch(classFile);
	        IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        
	        cl = (PhysicalClassLoader) pci.getRPCClassLoader(true);
	        Class<?> clazz = cl.loadClass(className, barr);
	        return newInstance(clazz, pc.getConfig(),cfc);
        }
        catch(Throwable t) {
        	throw Caster.toPageException(t);
        }
	}

	private static void _createProxy(ClassWriter cw, Set<Class> cDone,Map<String,Class> mDone, Component cfc, Class clazz, String className) throws IOException {
    	if(cDone.contains(clazz)) return;
		
		cDone.add(clazz);
		
		// super class
		Class superClass = clazz.getSuperclass();
		if(superClass!=null)_createProxy(cw, cDone,mDone, cfc, superClass,className);
		
		// interfaces
		Class[] interfaces = clazz.getInterfaces();
		if(interfaces!=null)for(int i=0;i<interfaces.length;i++){
			_createProxy(cw,cDone,mDone, cfc, interfaces[i],className);
		}
		
		Method[] methods = clazz.getMethods();
		if(methods!=null)for(int i=0;i<methods.length;i++){
			_createMethod(cw,mDone,methods[i],className);
		}  
	}

	private static void _createMethod(ClassWriter cw, Map<String,Class> mDone, Method src, String className) throws IOException {
		Class<?>[] classArgs = src.getParameterTypes();
		Class<?> classRtn = src.getReturnType();
		
		String str=src.getName()+"("+Reflector.getDspMethods(classArgs)+")";
		Class rtnClass = mDone.get(str);
		if(rtnClass!=null) {
			if(rtnClass!=classRtn) throw new IOException("there is a conflict with method ["+str+"], this method is declared more than once with different return types.");
			return;
		}
		mDone.put(str,classRtn);
		
		Type[] typeArgs = ASMUtil.toTypes(classArgs); 
		Type typeRtn = Type.getType(classRtn); 
		
		org.objectweb.asm.commons.Method method = new org.objectweb.asm.commons.Method(
         		src.getName(),
         		typeRtn,
     			typeArgs
         		);
         GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , method, null, null, cw);
         //BytecodeContext bc = new BytecodeContext(statConstr,constr,null,null,keys,cw,className,adapter,method,writeLog);
         Label start=adapter.newLabel();
         adapter.visitLabel(start);
         
         
         //JavaProxy.call(cfc,"add",new Object[]{arg0})
         // config
         adapter.visitVarInsn(Opcodes.ALOAD, 0);
         adapter.visitFieldInsn(Opcodes.GETFIELD, className, "config", CONFIG_WEB_NAME);
         
         // cfc
         adapter.visitVarInsn(Opcodes.ALOAD, 0);
         adapter.visitFieldInsn(Opcodes.GETFIELD, className, "cfc", COMPONENT_NAME);
         
         // name
         adapter.push(src.getName());
         
         // arguments
         ArrayVisitor av=new ArrayVisitor();
         av.visitBegin(adapter,Types.OBJECT,typeArgs.length);
         for(int y=0;y<typeArgs.length;y++){
 			av.visitBeginItem(adapter, y);
 				adapter.loadArg(y);
 				if(classArgs[y]==boolean.class) adapter.invokeStatic(JAVA_PROXY, _BOOLEAN);
 				else if(classArgs[y]==byte.class) adapter.invokeStatic(JAVA_PROXY, _BYTE);
 				else if(classArgs[y]==char.class) adapter.invokeStatic(JAVA_PROXY, _CHAR);
 				else if(classArgs[y]==double.class) adapter.invokeStatic(JAVA_PROXY, _DOUBLE);
 				else if(classArgs[y]==float.class) adapter.invokeStatic(JAVA_PROXY, _FLOAT);
 				else if(classArgs[y]==int.class) adapter.invokeStatic(JAVA_PROXY, _INT);
 				else if(classArgs[y]==long.class) adapter.invokeStatic(JAVA_PROXY, _LONG);
 				else if(classArgs[y]==short.class) adapter.invokeStatic(JAVA_PROXY, _SHORT);
 				else {
 					adapter.invokeStatic(JAVA_PROXY, _OBJECT);
 				}
 				
 				
 			av.visitEndItem(adapter);
         }
         av.visitEnd();
         adapter.invokeStatic(JAVA_PROXY, CALL);
         
       //JavaProxy.to...(...);
         int rtn=Opcodes.IRETURN;
         if(classRtn==boolean.class) 	adapter.invokeStatic(JAVA_PROXY, TO_BOOLEAN);
         else if(classRtn==byte.class) 	adapter.invokeStatic(JAVA_PROXY, TO_BYTE);
         else if(classRtn==char.class) 	adapter.invokeStatic(JAVA_PROXY, TO_CHAR);
         else if(classRtn==double.class){
        	 							rtn=Opcodes.DRETURN;
        	 							adapter.invokeStatic(JAVA_PROXY, TO_DOUBLE);
         }
         else if(classRtn==float.class) {
										rtn=Opcodes.FRETURN;
										adapter.invokeStatic(JAVA_PROXY, TO_FLOAT);
         }
         else if(classRtn==int.class) 	adapter.invokeStatic(JAVA_PROXY, TO_INT);
         else if(classRtn==long.class) 	{
        	 							rtn=Opcodes.LRETURN;
        	 							adapter.invokeStatic(JAVA_PROXY, TO_LONG);
         }
         else if(classRtn==short.class) adapter.invokeStatic(JAVA_PROXY, TO_SHORT);
         else if(classRtn==void.class){
        	 							rtn=Opcodes.RETURN;
        	 							adapter.pop();
         }
         else {
										rtn=Opcodes.ARETURN;
										adapter.checkCast(typeRtn);
         }
         
         
         
         /*mv = cw.visitMethod(ACC_PUBLIC, "add", "(Ljava/lang/Object;)Z", null, null);
         mv.visitCode();
         Label l0 = new Label();
         mv.visitLabel(l0);
         mv.visitLineNumber(20, l0);
         mv.visitVarInsn(ALOAD, 0);
         mv.visitFieldInsn(GETFIELD, "Test", "cfc", "Ljava/lang/Object;");
         mv.visitLdcInsn("add");
         mv.visitInsn(ICONST_1);
         mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
         mv.visitInsn(DUP);
         mv.visitInsn(ICONST_0);
         mv.visitVarInsn(ALOAD, 1);
         mv.visitInsn(AASTORE);
         mv.visitMethodInsn(INVOKESTATIC, "JavaProxy", "call", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
         mv.visitMethodInsn(INVOKESTATIC, "JavaProxy", "toBoolean", "(Ljava/lang/Object;)Z");
         mv.visitInsn(IRETURN);
         Label l1 = new Label();
         mv.visitLabel(l1);
         mv.visitLocalVariable("this", "LTest;", null, l0, l1, 0);
         mv.visitLocalVariable("arg0", "Ljava/lang/Object;", null, l0, l1, 1);
         mv.visitMaxs(6, 2);
         mv.visitEnd();*/
         
         
         
         adapter.visitInsn(rtn);
         adapter.endMethod();
		
		
	}

	private static Object newInstance(PhysicalClassLoader cl, String className, ConfigWeb config,Component cfc) 
		throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, ClassNotFoundException {
			return newInstance(cl.loadClass(className),config,cfc);
	}
	private static Object newInstance(Class<?> _clazz,ConfigWeb config, Component cfc) 
	throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Constructor<?> constr = _clazz.getConstructor(new Class[]{ConfigWeb.class,Component.class});
		return constr.newInstance(new Object[]{config,cfc});
	}
	
	private static String createClassName(Class extendz, Class[] interfaces) throws IOException {
		if(extendz==null) extendz=Object.class;
		
		
		
		StringBuilder sb=new StringBuilder(extendz.getName());
		if(interfaces!=null && interfaces.length>0){
			sb.append(';');
			
			String[] arr=new String[interfaces.length];
			for(int i=0;i<interfaces.length;i++){
				arr[i]=interfaces[i].getName();
			}
			Arrays.sort(arr);
			
			sb.append(railo.runtime.type.util.ListUtil.arrayToList(arr, ";"));
		}
		
		String key = KeyGenerator.createVariable(sb.toString());
		
		
		return key;
	}



}
