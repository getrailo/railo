package railo.runtime.type.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Component;
import railo.runtime.ComponentSpecificAccess;
import railo.runtime.Mapping;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.component.Property;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.net.rpc.Pojo;
import railo.runtime.net.rpc.server.ComponentController;
import railo.runtime.net.rpc.server.RPCServer;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPropertiesImpl;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMPropertyImpl;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

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
	public static Class getComponentJavaAccess(PageContext pc,Component component, RefBoolean isNew,boolean create,boolean writeLog, boolean suppressWSbeforeArg) throws PageException {
    	isNew.setValue(false);
    	String classNameOriginal=component.getPageSource().getFullClassName();
    	String className=getClassname(component).concat("_wrap");
    	String real=className.replace('.','/');
    	String realOriginal=classNameOriginal.replace('.','/');
    	Mapping mapping = component.getPageSource().getMapping();
		PhysicalClassLoader cl=null;
		try {
			cl = (PhysicalClassLoader) ((PageContextImpl)pc).getRPCClassLoader(false);
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
    	cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, real, null, "java/lang/Object", null);

    	//GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,Page.STATIC_CONSTRUCTOR,null,null,cw);
		BytecodeContext statConstr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.STATIC_CONSTRUCTOR);

    	///ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC,Page.CONSTRUCTOR,null,null,cw);
		BytecodeContext constr = null;//new BytecodeContext(null,null,null,cw,real,ga,Page.CONSTRUCTOR);
		
    	
   	// field component
    	//FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE, "c", "Lrailo/runtime/ComponentImpl;", null, null);
    	//fv.visitEnd();
    	
    	java.util.List<LitString> _keys=new ArrayList<LitString>();
    
        // remote methods
        Collection.Key[] keys = ComponentProUtil.keys(component,Component.ACCESS_REMOTE);
        int max;
        for(int i=0;i<keys.length;i++){
        	max=-1;
        	while((max=createMethod(statConstr,constr,_keys,cw,real,component.get(keys[i]),max, writeLog,suppressWSbeforeArg))!=-1){
        		break;// for overload remove this
        	}
        }
        
        // Constructor
        GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR_OBJECT,null,null,cw);
		adapter.loadThis();
        adapter.invokeConstructor(Types.OBJECT, CONSTRUCTOR_OBJECT);
        railo.transformer.bytecode.Page.registerFields(new BytecodeContext(null,statConstr,constr,getPage(statConstr,constr),_keys,cw,real,adapter,CONSTRUCTOR_OBJECT,writeLog,suppressWSbeforeArg), _keys);
        adapter.returnValue();
        adapter.endMethod();
        
        
        cw.visitEnd();
        byte[] barr = cw.toByteArray();
    	
        try {
        	ResourceUtil.touch(classFile);
	        IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        
	        cl = (PhysicalClassLoader) ((PageContextImpl)pc).getRPCClassLoader(true);
	        
	        return registerTypeMapping(cl.loadClass(className, barr));
        }
        catch(Throwable t) {
        	throw Caster.toPageException(t);
        }
    }

    private static railo.transformer.bytecode.Page getPage(BytecodeContext bc1, BytecodeContext bc2) {
    	railo.transformer.bytecode.Page page=null;
    	if(bc1!=null)page=bc1.getPage();
    	if(page==null && bc2!=null)page=bc2.getPage();
    	return page;
	}

	/**
	 * check if one of the children is changed
	 * @param component
	 * @param clazz
	 * @return return true if children has changed
	 */
	private static boolean hasChangesOfChildren(long last, Class clazz) {
		return hasChangesOfChildren(last,ThreadLocalPageContext.get(),clazz);
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
		clazz=ClassUtil.toComponentType(clazz);
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
		if(clazz==null) return;
		
		if(!isComplexType(clazz)) {
			if(clazz.isArray()) {
				_registerTypeMapping(server, clazz.getComponentType());
			}
			return;
		}
		server.registerTypeMapping(clazz);
		registerTypeMapping(server,clazz);
	}

	public static String getClassname(Component component) {
    	PageSource ps = component.getPageSource();
    	return ps.getComponentName();
    	
    	
    	/*String path=ps.getDisplayPath();// Must remove webroot
    	Config config = ps.getMapping().getConfig();
    	String root = config.getRootDirectory().getAbsolutePath();
    	if(path.startsWith(root))
    		path=path.substring(root.length());

    	path=path.replace('\\', '/').toLowerCase();
    	path=ListUtil.trim(path, "/");
    	String[] arr = ListUtil.listToStringArray(path, '/');
    	
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
    	return rtn.toString();*/
	}

	/*
	 * includes the application context javasettings 
	 * @param pc
	 * @param className
	 * @param properties
	 * @return
	 * @throws PageException
	 */
	public static Object getClientComponentPropertiesObject(PageContext pc, String className, ASMProperty[] properties) throws PageException {
		try {
			return _getClientComponentPropertiesObject(pc,pc.getConfig(), className, properties);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	/*
	 * does not include the application context javasettings 
	 * @param pc
	 * @param className
	 * @param properties
	 * @return
	 * @throws PageException
	 */
	public static Object getClientComponentPropertiesObject(Config config, String className, ASMProperty[] properties) throws PageException {
		try {
			return _getClientComponentPropertiesObject(null,config, className, properties);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	

    
    private static Object _getClientComponentPropertiesObject(PageContext pc, Config secondChanceConfig, String className, ASMProperty[] properties) throws PageException, IOException, ClassNotFoundException {
    	String real=className.replace('.','/');
    	
		PhysicalClassLoader cl;
    	if(pc==null)cl = (PhysicalClassLoader)secondChanceConfig.getRPCClassLoader(false);
    	else cl = (PhysicalClassLoader)((PageContextImpl)pc).getRPCClassLoader(false);
		
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
    	
    	if(pc==null)cl = (PhysicalClassLoader)secondChanceConfig.getRPCClassLoader(exist);
    	else cl = (PhysicalClassLoader)((PageContextImpl)pc).getRPCClassLoader(exist);
    	
		return ClassUtil.loadInstance(cl.loadClass(className));

	}

	public static Class getServerComponentPropertiesClass(PageContext pc,Component component) throws PageException {
		try {
	    	return _getServerComponentPropertiesClass(pc,component);
		}
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
    }

    private static Class _getServerComponentPropertiesClass(PageContext pc,Component component) throws PageException, IOException, ClassNotFoundException {
    	String className=getClassname(component);//StringUtil.replaceLast(classNameOriginal,"$cfc","");
    	String real=className.replace('.','/');

    	Mapping mapping = component.getPageSource().getMapping();
		PhysicalClassLoader cl = (PhysicalClassLoader)((PageContextImpl)pc).getRPCClassLoader(false);
		
		Resource classFile = cl.getDirectory().getRealResource(real.concat(".class"));
		
		// get component class information
    	String classNameOriginal=component.getPageSource().getFullClassName();
    	String realOriginal=classNameOriginal.replace('.','/');
		Resource classFileOriginal = mapping.getClassRootDirectory().getRealResource(realOriginal.concat(".class"));

		// load existing class when pojo is still newer than component class file
		if(classFile.lastModified()>=classFileOriginal.lastModified()) {
			try {
				Class clazz=cl.loadClass(className);
				if(clazz!=null && !hasChangesOfChildren(classFile.lastModified(), clazz))return clazz;//ClassUtil.loadInstance(clazz);
			}
			catch(Throwable t){}
		}
		
		// extends
		String strExt = component.getExtends();
		Class<?> ext=Object.class;
		if(!StringUtil.isEmpty(strExt,true)) {
			ext = Caster.cfTypeToClass(strExt);
		}
		//

		// create file
		byte[] barr = ASMUtil.createPojo(real, ASMUtil.toASMProperties(
				ComponentProUtil.getProperties(component, false, true, false, false)),ext,new Class[]{Pojo.class},component.getPageSource().getDisplayPath());
		ResourceUtil.touch(classFile);
		IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
		cl = (PhysicalClassLoader)((PageContextImpl)pc).getRPCClassLoader(true);
		return cl.loadClass(className); //ClassUtil.loadInstance(cl.loadClass(className));
	}

	public static Class getServerStructPropertiesClass(PageContext pc,Struct sct, PhysicalClassLoader cl) throws PageException {
		try {
			return _getServerStructPropertiesClass(pc,sct,cl);
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	private static Class _getServerStructPropertiesClass(PageContext pc,Struct sct, PhysicalClassLoader cl) throws PageException, IOException, ClassNotFoundException {
		// create hash based on the keys of the struct
		String hash = StructUtil.keyHash(sct);
		char c=hash.charAt(0);
		if(c>='0' && c<='9') hash="a"+hash;

		// create class name (struct class name + hash)
		String className=sct.getClass().getName()+"."+hash;

		// create physcal location for the file
		String real=className.replace('.','/');
		Resource classFile = cl.getDirectory().getRealResource(real.concat(".class"));

		// load existing class
		if(classFile.exists()) {
			try {
				Class clazz=cl.loadClass(className);
				if(clazz!=null )return clazz;
			}
			catch(Throwable t){}
		}

		// Properties
		List<ASMProperty> props=new ArrayList<ASMProperty>();
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			props.add(new ASMPropertyImpl(
					ASMUtil.toType(e.getValue()==null?Object.class:Object.class/*e.getValue().getClass()*/, true)
					,e.getKey().getString()
					));
		}

		// create file
		byte[] barr = ASMUtil.createPojo(real, props.toArray(new ASMProperty[props.size()])
				,Object.class,new Class[]{Pojo.class},null);

		// create class file from bytecode 
		ResourceUtil.touch(classFile);
		IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
		cl = (PhysicalClassLoader)((PageContextImpl)pc).getRPCClassLoader(true);
		return cl.loadClass(className);
	}

	private static int createMethod(BytecodeContext statConstr,BytecodeContext constr, java.util.List<LitString> keys,ClassWriter cw,String className, Object member,int max,boolean writeLog, boolean suppressWSbeforeArg) throws PageException {
		
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
            BytecodeContext bc = new BytecodeContext(null,statConstr,constr,getPage(statConstr,constr),keys,cw,className,adapter,method,writeLog,suppressWSbeforeArg);
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
    			av.visitEndItem(bc.getAdapter());
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



	public static String md5(Component c) throws IOException {
		return md5(ComponentSpecificAccess.toComponentSpecificAccess(Component.ACCESS_PRIVATE,c));
	}
	public static String md5(ComponentSpecificAccess cw) throws IOException {
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
			
			Collection.Key[] other=ComponentProUtil.keys(c,access);
			
			if(other.length==0)
				return new ExpressionException(
						"component ["+c.getCallName()+"] has no "+strAccess+" function with name ["+key+"]");
			
			return new ExpressionException(
					"component ["+c.getCallName()+"] has no "+strAccess+" function with name ["+key+"]",
					"accessible functions are ["+ListUtil.arrayToList(other,",")+"]");
		}
		return new ExpressionException("member ["+key+"] of component ["+c.getCallName()+"] is not a function", "Member is of type ["+Caster.toTypeName(member)+"]");
	}

	

	/*public static ComponentAccess toComponentAccess(Component comp) throws ExpressionException {
		ComponentAccess ca = toComponentAccess(comp, null);
		if(ca!=null) return ca;
		throw new ExpressionException("can't cast class ["+Caster.toClassName(comp)+"] to a class of type ComponentAccess");
	}*/

	/*public static Component toComponentAccess(Component comp, Component defaultValue) {
		if(comp instanceof ComponentAccess) return (ComponentAccess) comp;
		if(comp instanceof ComponentSpecificAccess) return ((ComponentSpecificAccess) comp).getComponentAccess();
		return defaultValue;
	}*/
	
	
	
	public static Component toComponent(Object obj) throws ExpressionException {
		if(obj instanceof Component) return (Component) obj;
		throw new ExpressionException("can't cast class ["+Caster.toClassName(obj)+"] to a class of type Component");
	}
	


	public static PageSource getPageSource(Component cfc) {
		// TODO Auto-generated method stub
		try {
			return toComponent(cfc).getPageSource();
		} catch (ExpressionException e) {
			return null;
		}
	}

	public static Component getActiveComponent(PageContext pc, Component current) {
		if(pc.getActiveComponent()==null) return current; 
		if(pc.getActiveUDF()!=null && (pc.getActiveComponent()).getPageSource()==(pc.getActiveUDF().getOwnerComponent()).getPageSource()){
			
			return pc.getActiveUDF().getOwnerComponent();
		}
		return pc.getActiveComponent();
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
		PageSourceImpl psi = (PageSourceImpl)ps;
		
		Page p = psi.getPage();
		if(p!=null){
			//print.o("getPage(existing):"+ps.getDisplayPath()+":"+psi.hashCode()+":"+p.hashCode());
			return p;
		}
		pc=ThreadLocalPageContext.get(pc);
		return psi.loadPage(pc);
	}

	public static Struct getPropertiesAsStruct(Component c, boolean onlyPersistent) {
		Property[] props = c.getProperties(onlyPersistent);
		Struct sct=new StructImpl();
		if(props!=null)for(int i=0;i<props.length;i++){
			sct.setEL(KeyImpl.getInstance(props[i].getName()), props[i]);
		}
		return sct;
	}
	public static Struct getMetaData(PageContext pc,UDFPropertiesImpl udf) throws PageException {
		StructImpl func=new StructImpl();
        pc=ThreadLocalPageContext.get(pc);
		// TODO func.set("roles", value);
        // TODO func.set("userMetadata", value); neo unterstﾟtzt irgendwelche a
        // meta data
        Struct meta = udf.meta;
        if(meta!=null) StructUtil.copy(meta, func, true);
        
        func.setEL(KeyConstants._closure, Boolean.FALSE);
		
		func.set(KeyConstants._access,ComponentUtil.toStringAccess(udf.getAccess()));
        String hint=udf.hint;
        if(!StringUtil.isEmpty(hint))func.set(KeyConstants._hint,hint);
        String displayname=udf.displayName;
        if(!StringUtil.isEmpty(displayname))func.set(KeyConstants._displayname,displayname);
        func.set(KeyConstants._name,udf.functionName);
        func.set(KeyConstants._output,Caster.toBoolean(udf.output));
        func.set(KeyConstants._returntype, udf.strReturnType);
        func.set(KeyConstants._description, udf.description);
        if(udf.localMode!=null)func.set("localMode", AppListenerUtil.toLocalMode(udf.localMode.intValue(), ""));
        
        func.set(KeyConstants._owner, udf.pageSource.getDisplayPath());
        
	    	   
	    int format = udf.returnFormat;
        if(format<0 || format==UDF.RETURN_FORMAT_WDDX)			func.set(KeyConstants._returnFormat, "wddx");
        else if(format==UDF.RETURN_FORMAT_PLAIN)	func.set(KeyConstants._returnFormat, "plain");
        else if(format==UDF.RETURN_FORMAT_JSON)	func.set(KeyConstants._returnFormat, "json");
        else if(format==UDF.RETURN_FORMAT_SERIALIZE)func.set(KeyConstants._returnFormat, "cfml");
        
        
        FunctionArgument[] args =  udf.arguments;
        Array params=new ArrayImpl();
        //Object defaultValue;
        Struct m;
        //Object defaultValue;
        for(int y=0;y<args.length;y++) {
            StructImpl param=new StructImpl();
            param.set(KeyConstants._name,args[y].getName().getString());
            param.set(KeyConstants._required,Caster.toBoolean(args[y].isRequired()));
            param.set(KeyConstants._type,args[y].getTypeAsString());
            displayname=args[y].getDisplayName();
            if(!StringUtil.isEmpty(displayname)) param.set(KeyConstants._displayname,displayname);
            
            int defType = args[y].getDefaultType();
            if(defType==FunctionArgument.DEFAULT_TYPE_RUNTIME_EXPRESSION){
            	param.set(KeyConstants._default, "[runtime expression]");
            }
            else if(defType==FunctionArgument.DEFAULT_TYPE_LITERAL){
            	param.set(KeyConstants._default, 
            			UDFUtil.getDefaultValue(pc, udf.pageSource, udf.index, y, null));
            }
            
            hint=args[y].getHint();
            if(!StringUtil.isEmpty(hint))param.set(KeyConstants._hint,hint);
            // TODO func.set("userMetadata", value); neo unterstﾟtzt irgendwelche attr, die dann hier ausgebenen werden blﾚdsinn
            
            // meta data
            m=args[y].getMetaData();
            if(m!=null) StructUtil.copy(m, param, true);
                
            params.append(param);
        }
        func.set(KeyConstants._parameters,params);
		return func;
	}


}
