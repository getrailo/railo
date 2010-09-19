package railo.runtime.type.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.axis.AxisFault;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPro;
import railo.runtime.ComponentWrap;
import railo.runtime.Mapping;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.component.Property;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.net.rpc.Pojo;
import railo.runtime.net.rpc.server.ComponentController;
import railo.runtime.net.rpc.server.RPCServer;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.List;
import railo.runtime.type.UDF;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;
// TODO doc
public final class ComponentUtil {
	

	private final static Method CONSTRUCTOR_OBJECT = Method.getMethod("void <init> ()");
	private static final Type COMPONENT_CONTROLLER = Type.getType(ComponentController.class); 
	private static final Method INVOKE = new Method("invoke",Types.OBJECT,new Type[]{Types.STRING,Types.OBJECT_ARRAY});
	
	//private static final Method INVOKE_PROPERTY = new Method("invoke",Types.OBJECT,new Type[]{Types.STRING,Types.OBJECT_ARRAY});
	
    /**
     * generate a ComponentJavaAccess (CJA) class from a component
     * a CJA is a dynamic genarted java class that has all method defined inside a component as java methods.
	 * 
	 * This is used to generated server side Webservices.
	 * @param component
     * @param isNew 
     * @return
     * @throws PageException
     */
	public static Class getComponentJavaAccess(ComponentImpl component, RefBoolean isNew,boolean create,boolean writeLog) throws PageException {
		return _getComponentJavaAccess(component, isNew,create,writeLog);
	}
	    
    private static Class _getComponentJavaAccess(ComponentImpl component, RefBoolean isNew,boolean create,boolean writeLog) throws PageException {
    	isNew.setValue(false);
    	String classNameOriginal=component.getPageSource().getFullClassName();
    	String className=getClassname(component).concat("_wrap");
    	String real=className.replace('.','/');
    	String realOriginal=classNameOriginal.replace('.','/');
    	Mapping mapping = component.getPageSource().getMapping();
		PhysicalClassLoader cl=null;
		try {
			cl = (PhysicalClassLoader) (mapping.getConfig()).getRPCClassLoader(false);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		Resource classFile = cl.getDirectory().getRealResource(real.concat(".class"));
		Resource classFileOriginal = mapping.getClassRootDirectory().getRealResource(realOriginal.concat(".class"));
		
	// LOAD CLASS
    	//print.out(className);
		// check last Mod
		if(classFile.lastModified()>=classFileOriginal.lastModified()) {
			try {
				Class clazz=cl.loadClass(className);
				if(clazz!=null && !hasChangesOfChildren(classFile.lastModified(),clazz))return registerTypeMapping(clazz);
			}
			catch(Throwable t){}
		}
		if(!create) return null;
		isNew.setValue(true);
    	//print.out("new");
    // CREATE CLASS	
		ClassWriter cw = ASMUtil.getClassWriter();
        //ClassWriter cw = new ClassWriter(true);
    	cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC, real, null, "java/lang/Object", null);

    	//GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,Page.STATIC_CONSTRUCTOR,null,null,cw);
		BytecodeContext statConstr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.STATIC_CONSTRUCTOR);

    	///ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,Page.CONSTRUCTOR,null,null,cw);
		BytecodeContext constr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.CONSTRUCTOR);
		
    	
   	// field component
    	//FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE, "c", "Lrailo/runtime/ComponentImpl;", null, null);
    	//fv.visitEnd();
    	
    	java.util.List _keys=new ArrayList();
    
        // remote methods
        String[] keys = component.keysAsString(Component.ACCESS_REMOTE);
        int max;
        for(int i=0;i<keys.length;i++){
        	max=-1;
        	while((max=createMethod(statConstr,constr,_keys,cw,real,component.get(keys[i]),max, writeLog))!=-1){
        		break;// for overload remove this
        	}
        }
        
        // Constructor
        GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR_OBJECT,null,null,cw);
        adapter.loadThis();
        adapter.invokeConstructor(Types.OBJECT, CONSTRUCTOR_OBJECT);
        railo.transformer.bytecode.Page.registerFields(new BytecodeContext(statConstr,constr,_keys,cw,real,adapter,CONSTRUCTOR_OBJECT,writeLog), _keys);
        adapter.returnValue();
        adapter.endMethod();
        
        
        cw.visitEnd();
        byte[] barr = cw.toByteArray();
    	
        try {
        	ResourceUtil.touch(classFile);
	        IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        //PhysicalClassLoader cl=(PhysicalClassLoader) mapping.getClassLoaderForPhysical(true); 
	        
	        cl = (PhysicalClassLoader) mapping.getConfig().getRPCClassLoader(true);
	        
	        return registerTypeMapping(cl.loadClass(className, barr));
        }
        catch(Throwable t) {
        	throw Caster.toPageException(t);
        }
        
    }

    /**
	 * check if one of the children is changed
	 * @param component
	 * @param clazz
	 * @return return true if children has changed
	 */
	private static boolean hasChangesOfChildren(long last, Class clazz) {

		boolean b= hasChangesOfChildren(last,ThreadLocalPageContext.get(),clazz);
		return b;
	}

	/**
	 * check if one of the children is changed
	 * @param component
	 * @param pc
	 * @param clazz
	 * @return return true if children has changed
	 */
	private static boolean hasChangesOfChildren(long last,PageContext pc, Class clazz) {

    	java.lang.reflect.Method[] methods = clazz.getMethods();
    	java.lang.reflect.Method method;
    	Class[] params;
    	for(int i=0;i<methods.length;i++){
    		method=methods[i];
    		if(method.getDeclaringClass()==clazz){
    			if(_hasChangesOfChildren(pc,   last,method.getReturnType())) return true;
    			params = method.getParameterTypes();
    			for(int y=0;y<params.length;y++){
    				if(_hasChangesOfChildren(pc,  last, params[y])) return true;
    			}
    		}
    	}
		return false;
	}

	private static boolean _hasChangesOfChildren(PageContext pc, long last, Class clazz) {
		java.lang.reflect.Method m = getComplexTypeMethod(clazz);
		if(m==null) return false;
		try {
			String path=Caster.toString(m.invoke(null, new Object[0]));
			Resource res = ResourceUtil.toResourceExisting(pc, path);
			if(last<res.lastModified()) {
				return true;
			}
		} 
		catch (Exception e) {
			return true;
		}
		// possible that a child of the Cmplex Object is also a complex object
		return hasChangesOfChildren(last, pc, clazz);
	}

	private static boolean isComplexType(Class clazz) {
		return getComplexTypeMethod(clazz)!=null;
		
	}
	private static java.lang.reflect.Method getComplexTypeMethod(Class clazz) {
		try {
			return clazz.getMethod("_srcName", new Class[0]);
		} 
		catch (Exception e) {
			return null;
		}
	}

	/**
     * search in methods of a class for complex types
     * @param clazz
     * @return
     */
    private static Class registerTypeMapping(Class clazz) throws AxisFault {
    	PageContext pc = ThreadLocalPageContext.get();
    	RPCServer server=RPCServer.getInstance(pc.getId(),pc.getServletContext());
		return registerTypeMapping(server, clazz);
    }
    /**
     * search in methods of a class for complex types
     * @param server
     * @param clazz
     * @return
     */
    private static Class registerTypeMapping(RPCServer server, Class clazz) {
    	java.lang.reflect.Method[] methods = clazz.getMethods();
    	java.lang.reflect.Method method;
    	Class[] params;
    	for(int i=0;i<methods.length;i++){
    		method=methods[i];
    		if(method.getDeclaringClass()==clazz){
    			_registerTypeMapping(server, method.getReturnType());
    			params = method.getParameterTypes();
    			for(int y=0;y<params.length;y++){
    				_registerTypeMapping(server, params[y]);
    			}
    		}
    	}
    	return clazz;
	}

	/**
	 * register ComplexType
	 * @param server
	 * @param clazz
	 */
	private static void _registerTypeMapping(RPCServer server, Class clazz) {
		if(!isComplexType(clazz)) return;
		server.registerTypeMapping(clazz);
		registerTypeMapping(server,clazz);
	}

	private static String getClassname(Component component) throws ExpressionException {
    	PageSource ps = ComponentUtil.toComponentPro(component).getPageSource();
    	//ps.getRealpath()
    	//String path=ps.getMapping().getVirtual()+ps.getRealpath();
    	String path=ps.getDisplayPath();// Must remove webroot
    	Config config = ps.getMapping().getConfig();
    	String root = config.getRootDirectory().getAbsolutePath();
    	if(path.startsWith(root))
    		path=path.substring(root.length());
    	
    	
    	
    	
    	path=path.replace('\\', '/').toLowerCase();
    	path=List.trim(path, "/");
    	String[] arr = List.listToStringArray(path, '/');
    	
    	StringBuffer rtn=new StringBuffer();
    	for(int i=0;i<arr.length;i++) {
    		if(i+1==arr.length) {
    			rtn.append(StringUtil.toVariableName(StringUtil.replaceLast(arr[i],".cfc","")));
    		}
    		else {
    			rtn.append(StringUtil.toVariableName(arr[i]));
    			rtn.append('.');
    		}
    	}
    	return rtn.toString();
	}


	public static Object getClientComponentPropertiesObject(Config config, String className, ASMProperty[] properties) throws PageException {
		try {
			return _getClientComponentPropertiesObject(config, className, properties);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	

    
    private static Object _getClientComponentPropertiesObject(Config config, String className, ASMProperty[] properties) throws PageException, IOException, ClassNotFoundException {

    	String real=className.replace('.','/');
    	
		//Config config = pc.getConfig();
		PhysicalClassLoader cl = (PhysicalClassLoader)config.getRPCClassLoader(false);
		
		Resource rootDir = cl.getDirectory();
		Resource classFile = rootDir.getRealResource(real.concat(".class"));
		
		if(classFile.exists()) {
			try {
				Class clazz = cl.loadClass(className);
				Field field = clazz.getField("_md5_");
				if(ASMUtil.createMD5(properties).equals(field.get(null))){
				//if(equalInterface(properties,clazz)) {
					return ClassUtil.loadInstance(clazz);
				}
			}
			catch(Exception e) {
				
			}
		}
		
		// create file
		byte[] barr = ASMUtil.createPojo(real, properties,Object.class,new Class[]{Pojo.class},null);
    	boolean exist=classFile.exists();
		ResourceUtil.touch(classFile);
    	IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
    	
    	cl = (PhysicalClassLoader)config.getRPCClassLoader(exist);
    	
		return ClassUtil.loadInstance(cl.loadClass(className));

	}

	
    
	public static Class getServerComponentPropertiesClass(Component component) throws PageException {
		try {
	    	return _getServerComponentPropertiesClass(component);
		}
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
    }
    
    public static Class _getServerComponentPropertiesClass(Component component) throws PageException, IOException, ClassNotFoundException {
    	String className=getClassname(component);//StringUtil.replaceLast(classNameOriginal,"$cfc","");
    	String real=className.replace('.','/');
    	
    	ComponentPro cp = ComponentUtil.toComponentPro(component);
    	
    	Mapping mapping = cp.getPageSource().getMapping();
		Config config = mapping.getConfig();
		PhysicalClassLoader cl = (PhysicalClassLoader)config.getRPCClassLoader(false);
		
		Resource classFile = cl.getDirectory().getRealResource(real.concat(".class"));
		
    	String classNameOriginal=cp.getPageSource().getFullClassName();
    	String realOriginal=classNameOriginal.replace('.','/');
		Resource classFileOriginal = mapping.getClassRootDirectory().getRealResource(realOriginal.concat(".class"));
		
		
		// load existing class
		if(classFile.lastModified()>=classFileOriginal.lastModified()) {
			try {
				Class clazz=cl.loadClass(className);
				if(clazz!=null && !hasChangesOfChildren(classFile.lastModified(), clazz))return clazz;//ClassUtil.loadInstance(clazz);
			}
			catch(Throwable t){}
		}
		//print.out("new");
    	
		
		// create file
		byte[] barr = ASMUtil.createPojo(real, ComponentUtil.getProperties(component,false),Object.class,new Class[]{Pojo.class},cp.getPageSource().getDisplayPath());
    	ResourceUtil.touch(classFile);
    	IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
    	cl = (PhysicalClassLoader)config.getRPCClassLoader(true);
		return cl.loadClass(className); //ClassUtil.loadInstance(cl.loadClass(className));
    }

	private static int createMethod(BytecodeContext statConstr,BytecodeContext constr, java.util.List keys,ClassWriter cw,String className, Object member,int max,boolean writeLog) throws PageException {
		
		boolean hasOptionalArgs=false;
		
    	if(member instanceof UDF) {
    		UDF udf = (UDF) member;
    		FunctionArgument[] args = udf.getFunctionArguments();
    		Type[] types=new Type[max<0?args.length:max];
    		for(int y=0;y<types.length;y++){
    			types[y]=toType(args[y].getTypeAsString(),true);//Type.getType(Caster.cfTypeToClass(args[y].getTypeAsString()));
    			if(!args[y].isRequired())hasOptionalArgs=true;
    		}
    		Type rtnType=toType(udf.getReturnTypeAsString(),true);
            Method method = new Method(
            		udf.getFunctionName(),
            		rtnType,
        			types
            		);
            GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC+Opcodes.ACC_FINAL , method, null, null, cw);
            BytecodeContext bc = new BytecodeContext(statConstr,constr,keys,cw,className,adapter,method,writeLog);
            Label start=adapter.newLabel();
            adapter.visitLabel(start);
            
       //ComponentController.invoke(name, args);
            // name
            adapter.push(udf.getFunctionName());
            
            // args
            ArrayVisitor av=new ArrayVisitor();
            av.visitBegin(adapter,Types.OBJECT,types.length);
            for(int y=0;y<types.length;y++){
    			av.visitBeginItem(adapter, y);
    				adapter.loadArg(y);
    			av.visitEndItem(bc);
            }
            av.visitEnd();
            adapter.invokeStatic(COMPONENT_CONTROLLER, INVOKE);
            adapter.checkCast(rtnType);
            
            //ASMConstants.NULL(adapter);
            adapter.returnValue();
            Label end=adapter.newLabel();
            adapter.visitLabel(end);
            
            for(int y=0;y<types.length;y++){
    			adapter.visitLocalVariable(args[y].getName().getString(), types[y].getDescriptor(), null, start, end, y+1);
            }
            adapter.endMethod();
            
            if(hasOptionalArgs) {
            	if(max==-1)max=args.length-1;
            	else max--;
            	return max;
            }
    	}
    	return -1;
	}



	private static Type toType(String cfType, boolean axistype) throws PageException {
		Class clazz=Caster.cfTypeToClass(cfType);
		if(axistype)clazz=AxisCaster.toAxisTypeClass(clazz);
		return Type.getType(clazz);
		
	}



	public static String md5(Component c) throws IOException, ExpressionException {
		//Iterator it=component.keyIterator();
		ComponentImpl ci = toComponentImpl(c);
		ComponentWrap cw = new ComponentWrap(Component.ACCESS_PRIVATE,ci);
		Key[] keys = cw.keys();
		Arrays.sort(keys);
		
		StringBuffer _interface=new StringBuffer();
		
		Object member;
        UDF udf;
        FunctionArgument[] args;
        FunctionArgument arg;
        for(int y=0;y<keys.length;y++) {
        	member = cw.get(keys[y],null);
        	if(member instanceof UDF) {
        		udf=(UDF) member;
        		//print.out(udf.);
        		_interface.append(udf.getAccess());
        		_interface.append(udf.getOutput());
        		_interface.append(udf.getFunctionName());
        		_interface.append(udf.getReturnTypeAsString());
        		args = udf.getFunctionArguments();
        		for(int i=0;i<args.length;i++){
        			arg=args[i];
            		_interface.append(arg.isRequired());
            		_interface.append(arg.getName());
            		_interface.append(arg.getTypeAsString());
        		}
        	}
        }
        // MUST remove this
        Config config = ThreadLocalPageContext.getConfig();
        if(config!=null)
            SystemOut.print(config.getOutWriter(),_interface.toString().toLowerCase());
        else
        	SystemOut.print("\n"+_interface.toString().toLowerCase()+"\n");
        //////////////////
        
		return  MD5.getDigestAsString(_interface.toString().toLowerCase());
	}
	

    /**
     * cast a strong access definition to the int type
     * @param access access type
     * @return int access type
     * @throws ExpressionException
     */
	public static int toIntAccess(String access) throws ExpressionException {
        access=StringUtil.toLowerCase(access.trim());
        if(access.equals("package"))return Component.ACCESS_PACKAGE;
        else if(access.equals("private"))return Component.ACCESS_PRIVATE;
        else if(access.equals("public"))return Component.ACCESS_PUBLIC;
        else if(access.equals("remote"))return Component.ACCESS_REMOTE;
        throw new ExpressionException("invalid access type ["+access+"], access types are remote, public, package, private");
        
    }
	
	public static int toIntAccess(String access, int defaultValue) {
        access=StringUtil.toLowerCase(access.trim());
        if(access.equals("package"))return Component.ACCESS_PACKAGE;
        else if(access.equals("private"))return Component.ACCESS_PRIVATE;
        else if(access.equals("public"))return Component.ACCESS_PUBLIC;
        else if(access.equals("remote"))return Component.ACCESS_REMOTE;
        return defaultValue;
    }
    
    /**
     * cast int type to string type
     * @param access
     * @return String access type
     * @throws ExpressionException
     */
    public static String toStringAccess(int access) throws ExpressionException {
        String res = toStringAccess(access,null);
        if(res!=null) return res; 
        throw new ExpressionException("invalid access type ["+access+"], access types are Component.ACCESS_PACKAGE, Component.ACCESS_PRIVATE, Component.ACCESS_PUBLIC, Component.ACCESS_REMOTE");
    }
    
    public static String toStringAccess(int access,String defaultValue)  {
        switch(access) {
            case Component.ACCESS_PACKAGE:      return "package";
            case Component.ACCESS_PRIVATE:      return "private";
            case Component.ACCESS_PUBLIC:       return "public";
            case Component.ACCESS_REMOTE:       return "remote";
        }
        return defaultValue;
    }

	public static ExpressionException notFunction(Component c,Collection.Key key, Object member,int access) {
		if(member==null) {
			String strAccess = toStringAccess(access,"");
			
			String[] other;
			if(c instanceof ComponentImpl)
				other=((ComponentImpl)c).keysAsString(access);
			else 
				other=c.keysAsString();
			
			if(other.length==0)
				return new ExpressionException(
						"component ["+c.getCallName()+"] has no "+strAccess+" function with name ["+key+"]");
			
			return new ExpressionException(
					"component ["+c.getCallName()+"] has no "+strAccess+" function with name ["+key+"]",
					"accessible functions are ["+List.arrayToList(other,",")+"]");
		}
		return new ExpressionException("member ["+key+"] of component ["+c.getCallName()+"] is not a function", "Member is of type ["+Caster.toTypeName(member)+"]");
	}

	public static Property[] getProperties(Component c,boolean onlyPeristent) {
		if(c instanceof ComponentPro)
			return ((ComponentPro)c).getProperties(onlyPeristent);
		
		throw new RuntimeException("class ["+Caster.toClassName(c)+"] does not support method [getProperties(boolean)]");
	}

	public static ComponentImpl toComponentImpl(Component comp) throws ExpressionException {
		if(comp instanceof ComponentImpl) return (ComponentImpl) comp;
		if(comp instanceof ComponentWrap) return ((ComponentWrap) comp).getComponentImpl();
		throw new ExpressionException("can't cast class ["+Caster.toClassName(comp)+"] to a class of type ComponentImpl");
		
	}
	
	public static ComponentPro toComponentPro(Component comp) throws ExpressionException {
		if(comp instanceof ComponentPro) return (ComponentPro) comp;
		throw new ExpressionException("can't cast class ["+Caster.toClassName(comp)+"] to a class of type ComponentPro");
	}
	public static ComponentPro toComponentPro(Object obj) throws ExpressionException {
		if(obj instanceof ComponentPro) return (ComponentPro) obj;
		throw new ExpressionException("can't cast class ["+Caster.toClassName(obj)+"] to a class of type ComponentPro");
	}
	
	public static ComponentPro toComponentPro(Component comp, ComponentPro defaultValue) {
		if(comp instanceof ComponentPro) return (ComponentPro) comp;
		return defaultValue;
	}

	public static ComponentImpl getActiveComponent(PageContext pc, ComponentImpl current) {
		if(pc.getActiveComponent()==null) return current; 
		if(pc.getActiveUDF()!=null && ((ComponentPro)pc.getActiveComponent()).getPageSource()==((ComponentPro)pc.getActiveUDF().getOwnerComponent()).getPageSource()){
			
			return (ComponentImpl) pc.getActiveUDF().getOwnerComponent();
		}
		return (ComponentImpl) pc.getActiveComponent();//+++
		
		
	}

	public static long getCompileTime(PageContext pc, PageSource ps,long defaultValue) {
		try {
			return getCompileTime(pc, ps);
		} catch (Throwable t) {
			return defaultValue;
		}
	}

	public static long getCompileTime(PageContext pc, PageSource ps) throws PageException {
		return getPage(pc,ps).getCompileTime();
	}

	public static Page getPage(PageContext pc, PageSource ps) throws PageException {
		pc=ThreadLocalPageContext.get(pc);
		PageSourceImpl psi = (PageSourceImpl)ps;
		
		Page p = psi.getPage();
		if(p!=null){
			//print.o("getPage(existing):"+ps.getDisplayPath()+":"+psi.hashCode()+":"+p.hashCode());
			return p;
		}
		//print.o("getPage(load):"+ps.getDisplayPath()+":"+psi.hashCode());
		return psi.loadPage(pc,pc.getConfig());
	}
}
