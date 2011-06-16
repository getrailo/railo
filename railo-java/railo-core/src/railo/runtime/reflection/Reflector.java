package railo.runtime.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.java.JavaObject;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.reflection.pairs.ConstructorInstance;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.reflection.storage.WeakConstructorStorage;
import railo.runtime.reflection.storage.WeakFieldStorage;
import railo.runtime.reflection.storage.WeakMethodStorage;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.Type;

/**
 * Class to reflect on Objects and classes 
 */
public final class Reflector {

	
    private static final Object NULL = new Object();
    
    
	private static WeakConstructorStorage cStorage=new WeakConstructorStorage();
    private static WeakFieldStorage fStorage=new WeakFieldStorage();
    private static WeakMethodStorage mStorage=new WeakMethodStorage();

    /**
     * check if Class is instanceof a a other Class
     * @param srcClassName Class name to check
     * @param trg is Class of?
     * @return is Class Class of...
     */
    public static boolean isInstaneOf(String srcClassName,Class trg) {
        try {
            return isInstaneOf(Class.forName(srcClassName),trg);
        } 
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * check if Class is instanceof a a other Class
     * @param srcClassName Class name to check
     * @param trgClassName is Class of?
     * @return is Class Class of...
     */
    public static boolean isInstaneOf(String srcClassName,String trgClassName) {
        try {
            return isInstaneOf(Class.forName(srcClassName),trgClassName);
        } 
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * check if Class is instanceof a a other Class
     * @param src is Class of?
     * @param trgClassName Class name to check
     * @return is Class Class of...
     */
    public static boolean isInstaneOf(Class src, String trgClassName) {
        try {
            return isInstaneOf(src,Class.forName(trgClassName));
        } 
        catch (ClassNotFoundException e) {
            return false;
        }
    }
	
    
    public static boolean isInstaneOfIgnoreCase(Class src ,String trg) {
        if(src.isArray()) {
            return isInstaneOfIgnoreCase(src.getComponentType() ,trg);
        }
        
        if(src.getName().equalsIgnoreCase(trg))return true;
        
        // Interface
        if(_checkInterfaces(src,trg)) {
            return true;
        }
        // Extends 
        src=src.getSuperclass();
        if(src!=null) return isInstaneOfIgnoreCase(src, trg);
        return false;
    }
    
    
	/**
	 * check if Class is instanceof a a other Class
	 * @param src Class to check
	 * @param trg is Class of?
	 * @return is Class Class of...
	 */
    public static boolean isInstaneOf(Class src ,Class trg) {
        if(src.isArray() && trg.isArray()) {
            return isInstaneOf(src.getComponentType() ,trg.getComponentType());
        }
        
        if(src==trg)return true;
        
        // Interface
        if(trg.isInterface()) {
            return _checkInterfaces(src,trg);
        }
        // Extends 
        
        while(src!=null) {
            if(src==trg) return true;
            src=src.getSuperclass();
        }
        return trg==Object.class;
    }
    
    private static boolean _checkInterfaces(Class src, String trg) {
        Class[] interfaces = src.getInterfaces();
        if(interfaces==null) return false;
        for(int i=0;i<interfaces.length;i++) {
            if(interfaces[i].getName().equalsIgnoreCase(trg))return true;
            if(_checkInterfaces(interfaces[i],trg)) return true;
        }
        return false;
    }
    
    private static boolean _checkInterfaces(Class src, Class trg) {
        Class[] interfaces = src.getInterfaces();
        if(interfaces==null) return false;
        for(int i=0;i<interfaces.length;i++) {
            if(interfaces[i]==trg)return true;
            if(_checkInterfaces(interfaces[i],trg)) return true;
        }
        src=src.getSuperclass();
        if(src!=null) return _checkInterfaces(src,trg);
        return false;
    }
	
	/**
	 * get all Classes from a Object Array
	 * @param objs Objects to get
	 * @return classes from Objects
	 */
	public static Class[] getClasses(Object[] objs) {
		Class[] cls=new Class[objs.length];
		for(int i=0;i<objs.length;i++) {
			if(objs[i]==null)cls[i]=Object.class;
			else cls[i]=objs[i].getClass();
		}
		return cls;
	}
	
	/**
	 * convert a primitive class Type to a Reference Type (Example: int -> java.lang.Integer)
	 * @param c Class to convert
	 * @return converted Class (if primitive)
	 */
	public static Class toReferenceClass(Class c) {
		if(c.isPrimitive()) {
			if(c==boolean.class)  return Boolean.class;
			if(c==byte.class)  return Byte.class;
			if(c==short.class)  return Short.class;
			if(c==char.class)  return Character.class;
			if(c==int.class)  return Integer.class;
			if(c==long.class)  return Long.class;
			if(c==float.class)  return Float.class;
			if(c==double.class)  return Double.class;
		}
		return c;		
	}

	/**
	 * creates a string list with class arguments in a displable form
	 * @param clazzArgs arguments to display
	 * @return list
	 */
	public static String getDspMethods(Class[] clazzArgs) {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<clazzArgs.length;i++) {
			if(i>0)sb.append(", ");
			sb.append(Caster.toTypeName(clazzArgs[i]));
		}
		return sb.toString();
	}
	
	/**
	 * checks if src Class is "like" trg class
	 * @param src Source Class
	 * @param trg Target Class
	 * @return is similar
	 */
	private static boolean like(Class src, Class trg) {
		if(src==trg) return true;
		return isInstaneOf(src,trg);
	}
	
	/**
	 * convert Object from src to trg Type, if possible
	 * @param src Object to convert
	 * @param srcClass Source Class
	 * @param trgClass Target Class
	 * @return converted Object
	 * @throws PageException 
	 */
	private static Object convert(Object src, Class trgClass) throws PageException {
		if(src==null) {
			if(trgClass.isPrimitive())
				throw new ApplicationException("can't convert [null] to ["+trgClass.getName()+"]");
			return null;
		}
		if(like(src.getClass(), trgClass)) return src;
		String className=trgClass.getName();
		

		if(src instanceof ObjectWrap) {
			src = ((ObjectWrap) src).getEmbededObject();
			return convert(src, trgClass);
		}
		if(className.startsWith("java.lang.")){
			if(trgClass==Boolean.class)		return Caster.toBoolean(src);
			if(trgClass==Integer.class)		return Caster.toInteger(src);
			if(trgClass==String.class)			return Caster.toString(src);
			if(trgClass==Byte.class)			return Caster.toByte(src);
			if(trgClass==Short.class)			return Caster.toShort(src);
			if(trgClass==Long.class) 			return Caster.toLong(src);
			if(trgClass==Float.class)			return Caster.toFloat(src);
			if(trgClass==Double.class)			return Caster.toDouble(src);
			if(trgClass==Character.class) {
				String str=Caster.toString(src,null);
				if(str!=null && str.length()==1) return new Character(str.charAt(0));
			}
		}
		
		if(Decision.isArray(src)) {
			if(trgClass.isArray()) {
				return toNativeArray(trgClass,src);
			}
			else if(isInstaneOf(trgClass, List.class)) {
				return Caster.toList(src);
			}
			else if(isInstaneOf(trgClass, Array.class)) {
				return Caster.toArray(src);
			}
		}
		
		if(trgClass==Date.class) return Caster.toDate(src,true,null);
		else if(trgClass==Query.class) return Caster.toQuery(src);
		else if(trgClass==Map.class) return Caster.toMap(src);
		else if(trgClass==Struct.class) return Caster.toStruct(src);
		else if(trgClass==Resource.class) return Caster.toResource(src,false);
		// this 2 method are used to support conversion that match neo src types
		else if(trgClass==Hashtable.class) return Caster.toHashtable(src);
		else if(trgClass==Vector.class) return Caster.toVetor(src);
		else if(trgClass==java.util.Collection.class) return Caster.toJavaCollection(src);
		else if(trgClass==TimeZone.class && Decision.isString(src)) return Caster.toTimeZone(Caster.toString(src));
		else if(trgClass==Collection.Key.class) return KeyImpl.toKey(src);
		else if(trgClass==Locale.class && Decision.isString(src)) return Caster.toLocale(Caster.toString(src));
		if(trgClass.isPrimitive()) {
			//return convert(src,srcClass,toReferenceClass(trgClass));
			return convert(src,toReferenceClass(trgClass));
		}
		throw new ApplicationException("can't convert ["+Caster.toClassName(src)+"] to ["+Caster.toClassName(trgClass)+"]");
	}


	/**
	 * gets Constructor Instance matching given parameter
	 * @param clazz Clazz to Invoke
	 * @param args Matching args
	 * @return Matching ConstructorInstance
	 * @throws NoSuchMethodException
	 * @throws PageException
	 */

	public static ConstructorInstance getConstructorInstance(Class clazz, Object[] args) throws NoSuchMethodException {
		ConstructorInstance ci=getConstructorInstance(clazz, args,null);
	    if(ci!=null) return ci;
	    throw new NoSuchMethodException("No matching Constructor for "+clazz.getName()+"("+getDspMethods(getClasses(args))+") found");
	}
	
	public static ConstructorInstance getConstructorInstance(Class clazz, Object[] args, ConstructorInstance defaultValue) {
		args=cleanArgs(args);
		Constructor[] constructors=cStorage.getConstructors(clazz,args.length);//getConstructors(clazz);
		if(constructors!=null) {
		    Class[] clazzArgs = getClasses(args);
			// exact comparsion
			outer:for(int i=0;i<constructors.length;i++) {
				if(constructors[i]!=null) {
					
					Class[] parameterTypes = constructors[i].getParameterTypes();
					for(int y=0;y<parameterTypes.length;y++) {
						if(toReferenceClass(parameterTypes[y])!=clazzArgs[y]) continue outer;
					}
					return new ConstructorInstance(constructors[i],args);
				}
			}
			// like comparsion
			outer:for(int i=0;i<constructors.length;i++) {
				if(constructors[i]!=null) {
					Class[] parameterTypes = constructors[i].getParameterTypes();
					for(int y=0;y<parameterTypes.length;y++) {
						if(!like(clazzArgs[y],toReferenceClass(parameterTypes[y]))) continue outer;
					}
					return new ConstructorInstance(constructors[i],args);
				}
			}	
			// convert comparsion
			outer:for(int i=0;i<constructors.length;i++) {
				if(constructors[i]!=null) {
					Class[] parameterTypes = constructors[i].getParameterTypes();
					Object[] newArgs = new Object[args.length];
					for(int y=0;y<parameterTypes.length;y++) {
						//newArgs[y]=convert(args[y],toReferenceClass(parameterTypes[y]));
						//if(newArgs[y]==null) continue outer;
						try {
							//newArgs[y]=convert(args[y],clazzArgs[y],toReferenceClass(parameterTypes[y]));
							newArgs[y]=convert(args[y],toReferenceClass(parameterTypes[y]));
						} catch (PageException e) {
							continue outer;
						}
					}
					return new ConstructorInstance(constructors[i],newArgs);
				}
			}
		}
		return defaultValue;
		//throw new NoSuchMethodException("No matching Constructor for "+clazz.getName()+"("+getDspMethods(getClasses(args))+") found");
	}

    /**
	 * gets the MethodInstance matching given Parameter
	 * @param clazz Class Of the Method to get
	 * @param methodName Name of the Method to get
	 * @param args Arguments of the Method to get
	 * @return return Matching Method
     * @throws  
	 */
	public static MethodInstance getMethodInstanceEL(Class clazz, Collection.Key methodName, Object[] args) {
	    args=cleanArgs(args);
		
		Method[] methods = mStorage.getMethods(clazz,methodName,args.length);//getDeclaredMethods(clazz);
		
		if(methods!=null) {
		    Class[] clazzArgs = getClasses(args);
			// exact comparsion
		    //print.o("exact:"+methodName);
		    outer:for(int i=0;i<methods.length;i++) {
				if(methods[i]!=null) {
					Class[] parameterTypes = methods[i].getParameterTypes();
					for(int y=0;y<parameterTypes.length;y++) {
						if(toReferenceClass(parameterTypes[y])!=clazzArgs[y]) continue outer;
					}
					return new MethodInstance(methods[i],args);
				}
			}
			// like comparsion
		    MethodInstance mi=null;
		    //print.o("like:"+methodName);
		    outer:for(int i=0;i<methods.length;i++) {
				if(methods[i]!=null) {
					Class[] parameterTypes = methods[i].getParameterTypes();
					for(int y=0;y<parameterTypes.length;y++) {
						if(!like(clazzArgs[y],toReferenceClass(parameterTypes[y]))) continue outer;
					}
					mi=create(mi,methods[i],args);
				}
			}
		    if(mi!=null) return mi;
		    
		    
			// convert comparsion
		    //print.o("convert:"+methodName);
		    mi=null;
			outer:for(int i=0;i<methods.length;i++) {
				if(methods[i]!=null) {
					Class[] parameterTypes = methods[i].getParameterTypes();
					Object[] newArgs = new Object[args.length];
					for(int y=0;y<parameterTypes.length;y++) {
						try {
							//newArgs[y]=convert(args[y],clazzArgs[y],toReferenceClass(parameterTypes[y]));
							newArgs[y]=convert(args[y],toReferenceClass(parameterTypes[y]));
						} catch (PageException e) {e.printStackTrace();
							continue outer;
						}
						//if(newArgs[y]==null) continue outer;
					}
					mi=create(mi,methods[i],newArgs);
					//return new MethodInstance(methods[i],newArgs);
				}
			}
		    if(mi!=null) return mi;
		}
		return null;
	}

    private static MethodInstance create(MethodInstance existing, Method method, Object[] args) {
		if(existing==null) return new MethodInstance(method,args);
    	Class[] exTypes = existing.getMethod().getParameterTypes();
    	Class[] nwTypes = method.getParameterTypes();
		for(int i=0;i<exTypes.length;i++){
			if(exTypes[i]==nwTypes[i]) continue;
			if(isInstaneOf(exTypes[i], nwTypes[i])){
				return existing;
			}
			if(isInstaneOf(nwTypes[i], exTypes[i])){
				return  new MethodInstance(method,args);
			}
		}
    	return existing;
	}
    

	/**
     * gets the MethodInstance matching given Parameter
     * @param clazz Class Of the Method to get
     * @param methodName Name of the Method to get
     * @param args Arguments of the Method to get
     * @return return Matching Method
     * @throws NoSuchMethodException
     * @throws PageException
     */
    public static MethodInstance getMethodInstance(Class clazz, String methodName, Object[] args) 
        throws NoSuchMethodException {
        MethodInstance mi=getMethodInstanceEL(clazz, KeyImpl.init(methodName), args);
        if(mi!=null) return mi;
        
        Class[] classes = getClasses(args);
        //StringBuilder sb=null;
        JavaObject jo;
        Class c;
        ConstructorInstance ci;
        for(int i=0;i<classes.length;i++){
        	if(args[i] instanceof JavaObject) {
        		jo=(JavaObject) args[i];
        		c=jo.getClazz();
        		ci = Reflector.getConstructorInstance(c, new Object[0], null);
        		if(ci==null) {
        			
        		throw new NoSuchMethodException("The "+pos(i+1)+" parameter of "+methodName+"("+getDspMethods(classes)+") ia a object created " +
        				"by the createObject function (JavaObject/JavaProxy). This object has not been instantiated because it does not have a constructor " +
        				"that takes zero arguments. Railo cannot instantiate it for you, please use the .init(...) method to instantiate it with the correct parameters first");
        			
        			
        		}
        	}
        }
        /*
        the argument list contains objects created by createObject, 
        that are no instantiated (first,third,10th) and because this object have no constructor taking no arguments, Railo cannot instantiate them.
        you need first to instantiate this objects. 
        */
        throw new NoSuchMethodException("No matching Method for "+methodName+"("+getDspMethods(classes)+") found for "+
        		Caster.toTypeName(clazz));
    }
	
	private static String pos(int index) {
		if(index==1) return "first";
		if(index==2) return "second";
		if(index==3) return "third";
		
		return index+"th";
	}

	/**
	 * same like method getField from Class but ignore case from field name
	 * @param clazz class to search the field
	 * @param name name to search
	 * @return Matching Field
	 * @throws NoSuchFieldException
	 
	 */
    public static Field[] getFieldsIgnoreCase(Class clazz, String name) throws NoSuchFieldException  {
        Field[] fields=fStorage.getFields(clazz,name);
        if(fields!=null) return fields;
        throw new NoSuchFieldException("there is no field with name "+name+" in object ["+Type.getName(clazz)+"]");
    }
    
    public static Field[] getFieldsIgnoreCase(Class clazz, String name, Field[] defaultValue)  {
        Field[] fields=fStorage.getFields(clazz,name);
        if(fields!=null) return fields;
        return defaultValue;
        
        
    }

    public static String[] getPropertyKeys(Class clazz)  {
    	Set keys=new HashSet();
    	Field[] fields = clazz.getFields();
    	Field field;
    	Method[] methods = clazz.getMethods();
    	Method method;
    	String name;
    	
    	for(int i=0;i<fields.length;i++) {
    		field=fields[i];
    		if(Modifier.isPublic(field.getModifiers()) )keys.add(field.getName());
    	}
    	
    	for(int i=0;i<methods.length;i++) {
    		method=methods[i];
    		if(Modifier.isPublic(method.getModifiers()) ) {
    			if(isGetter(method)) {
    				name=method.getName();
    				if(name.startsWith("get"))keys.add(method.getName().substring(3));
    				else keys.add(method.getName().substring(2));
    			}
    			else if(isSetter(method)) keys.add(method.getName().substring(3));
    		}
    	}
    	
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public static boolean hasPropertyIgnoreCase(Class clazz, String name)  {
    	if(hasFieldIgnoreCase(clazz, name)) return true;
    	
    	Method[] methods = clazz.getMethods();
    	Method method;
    	String n;
    	for(int i=0;i<methods.length;i++) {
    		method=methods[i];
    		if(Modifier.isPublic(method.getModifiers()) && StringUtil.endsWithIgnoreCase(method.getName(),name)) {
    			n=null;
    			if(isGetter(method)) {
    				n=method.getName();
    				if(n.startsWith("get"))n=method.getName().substring(3);
    				else n=method.getName().substring(2);
    			}
    			else if(isSetter(method)) n=method.getName().substring(3);
    			if(n!=null && n.equalsIgnoreCase(name)) return true;
    		}
    	}
        return false;
    }
    
    
    public static boolean hasFieldIgnoreCase(Class clazz, String name)  {
        return !ArrayUtil.isEmpty(getFieldsIgnoreCase(clazz, name, null));
        //getFieldIgnoreCaseEL(clazz, name)!=null;
    }
    

	/**
	 * call constructor of a class with matching arguments
	 * @param clazz Class to get Instance
	 * @param args Arguments for the Class
	 * @return invoked Instance
	 * @throws PageException
	 */
	public static Object callConstructor(Class clazz, Object[] args) throws PageException {
	    args=cleanArgs(args);
	    try {
            return getConstructorInstance(clazz,args).invoke();
        } 
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		} 
		catch (Exception e) {
		    throw Caster.toPageException(e);
		}
	}
	
	public static Object callConstructor(Class clazz, Object[] args, Object defaultValue) {
	    args=cleanArgs(args);
	    try {
            ConstructorInstance ci = getConstructorInstance(clazz,args,null);
            if(ci==null) return defaultValue;
            return ci.invoke();
        }
		catch (Throwable t) {
		    return defaultValue;
		}
	}

	/**
	 * calls a Method of a Objct
	 * @param obj Object to call Method on it
	 * @param methodName Name of the Method to get
	 * @param args Arguments of the Method to get
	 * @return return return value of the called Method
	 * @throws PageException
	 */
	public static Object callMethod(Object obj, String methodName, Object[] args) throws PageException {
		return callMethod(obj, KeyImpl.init(methodName), args);
	}
	
	public static Object callMethod(Object obj, Collection.Key methodName, Object[] args) throws PageException {
		if(obj==null) {
			throw new ExpressionException("can't call method ["+methodName+"] on object, object is null");
		}
		MethodInstance mi=getMethodInstanceEL(obj.getClass(), methodName, args);
		if(mi==null)
		    throw throwCall(obj,methodName,args);
	    try {
	    	
            return mi.invoke(obj);
        }
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw new NativeException(e.getTargetException());
		} 
		catch (Exception e) {
			throw new NativeException(e);
		}
	}

	public static ExpressionException throwCall(Object obj,String methodName, Object[] args) {
		return new ExpressionException("No matching Method/Function for "+Type.getName(obj)+"."+methodName+"("+getDspMethods(getClasses(args))+") found");
	}
	public static ExpressionException throwCall(Object obj,Collection.Key methodName, Object[] args) {
		return new ExpressionException("No matching Method/Function for "+Type.getName(obj)+"."+methodName+"("+getDspMethods(getClasses(args))+") found");
	}

	/**
	 * calls a Static Method on the given CLass
	 * @param clazz Class to call Method on it
	 * @param methodName Name of the Method to get
	 * @param args Arguments of the Method to get
	 * @return return return value of the called Method
	 * @throws PageException
	 */
	public static Object callStaticMethod(Class clazz, String methodName, Object[] args) throws PageException {
		try {
            return getMethodInstance(clazz,methodName,args).invoke(null);
        }
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
    
    /**
     * to get a Getter Method of a Object
     * @param clazz Class to invoke method from
     * @param prop Name of the Method without get
     * @return return Value of the getter Method
     * @throws NoSuchMethodException
     * @throws PageException
     */
    public static MethodInstance getGetter(Class clazz, String prop) throws PageException, NoSuchMethodException {
        String getterName = "get"+StringUtil.ucFirst(prop);
        MethodInstance mi = getMethodInstanceEL(clazz,KeyImpl.init(getterName),ArrayUtil.OBJECT_EMPTY);
        if(mi==null)
        	throw new ExpressionException("No matching property ["+prop+"] found in ["+Caster.toTypeName(clazz)+"]");
        Method m=mi.getMethod();
        
        if(m.getReturnType()==void.class) 
            throw new NoSuchMethodException("invalid return Type, method ["+m.getName()+"] for Property ["+getterName+"] must have return type not void");
        
        return mi;
    }
    
    /**
     * to get a Getter Method of a Object
     * @param clazz Class to invoke method from
     * @param prop Name of the Method without get
     * @return return Value of the getter Method
     */
    public static MethodInstance getGetterEL(Class clazz, String prop) {
        prop="get"+StringUtil.ucFirst(prop);
        MethodInstance mi = getMethodInstanceEL(clazz,KeyImpl.init(prop),ArrayUtil.OBJECT_EMPTY);
        if(mi==null) return null;
        if(mi.getMethod().getReturnType()==void.class) return null;
        return mi;
    }
	
	/**
	 * to invoke a getter Method of a Object
	 * @param obj Object to invoke method from
	 * @param prop Name of the Method without get
	 * @return return Value of the getter Method
	 * @throws PageException
	 */
	public static Object callGetter(Object obj, String prop) throws PageException {
	    try {
		    return getGetter(obj.getClass(), prop).invoke(obj);
		}
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
    
    /**
     * to invoke a setter Method of a Object
     * @param obj Object to invoke method from
     * @param prop Name of the Method without get
     * @param value Value to set to the Method
     * @return MethodInstance
     * @throws NoSuchMethodException
     * @throws PageException
     */
    public static MethodInstance getSetter(Object obj, String prop,Object value) throws NoSuchMethodException {
            prop="set"+StringUtil.ucFirst(prop);
            MethodInstance mi = getMethodInstance(obj.getClass(),prop,new Object[]{value});
            Method m=mi.getMethod();
            
            if(m.getReturnType()!=void.class)
                throw new NoSuchMethodException("invalid return Type, method ["+m.getName()+"] must have return type void, now ["+m.getReturnType().getName()+"]");
            return mi;
    }
    
    
    
    
    /**
     * to invoke a setter Method of a Object
     * @param obj Object to invoke method from
     * @param prop Name of the Method without get
     * @param value Value to set to the Method
     * @return MethodInstance
     */
    public static MethodInstance getSetterEL(Object obj, String prop,Object value)  {
            prop="set"+StringUtil.ucFirst(prop);
            MethodInstance mi = getMethodInstanceEL(obj.getClass(),KeyImpl.init(prop),new Object[]{value});
            if(mi==null) return null;
            Method m=mi.getMethod();
            
            if(m.getReturnType()!=void.class) return null;
            return mi;
    }
	
	/**
	 * to invoke a setter Method of a Object
	 * @param obj Object to invoke method from
	 * @param prop Name of the Method without get
	 * @param value Value to set to the Method
	 * @throws PageException
	 */
	public static void callSetter(Object obj, String prop,Object value) throws PageException {
	    try {
		    getSetter(obj, prop, value).invoke(obj);
		}
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	
	/**
	 * do nothing when not exist
	 * @param obj
	 * @param prop
	 * @param value
	 * @throws PageException
	 */
	public static void callSetterEL(Object obj, String prop,Object value) throws PageException {
	    try {
		    MethodInstance setter = getSetterEL(obj, prop, value);
		    if(setter!=null)setter.invoke(obj);
		}
		catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 * to get a visible Field of a object
	 * @param obj Object to invoke
	 * @param prop property to call
	 * @return property value
	 * @throws PageException
	 */
	public static Object getField(Object obj, String prop) throws PageException  {
	    try {
	    	return getFieldsIgnoreCase(obj.getClass(),prop)[0].get(obj);
        }
		catch (Throwable e) {
            throw Caster.toPageException(e);
		}
	}
	
	public static Object getField(Object obj, String prop, Object defaultValue) {
	    if(obj==null) return defaultValue;
		Field[] fields = getFieldsIgnoreCase(obj.getClass(),prop,null);
		if(ArrayUtil.isEmpty(fields)) return defaultValue;
		
		try {
			return fields[0].get(obj);
		} catch (Throwable t) {
			return defaultValue;
		}
	}
	
	/**
	 * assign a value to a visible Field of a object
	 * @param obj Object to assign value to his property
	 * @param prop name of property
	 * @param value Value to assign
	 * @throws PageException
	 */
	public static boolean setField(Object obj, String prop,Object value) throws PageException {
	    Class clazz=value.getClass();
		try {
	    	Field[] fields = getFieldsIgnoreCase(obj.getClass(),prop);
	    	// exact comparsion
			for(int i=0;i<fields.length;i++) {
				if(toReferenceClass(fields[i].getType())==clazz){
					fields[i].set(obj,value);
					return true;
				}
			}
			// like comparsion
			for(int i=0;i<fields.length;i++) {
				if(like(fields[i].getType(),clazz)) {
					fields[i].set(obj,value);
					return true;
				}
			}	
			// convert comparsion
			for(int i=0;i<fields.length;i++) {	
				try {
					fields[i].set(obj,convert(value,toReferenceClass(fields[i].getType())));
					return true;
				} catch (PageException e) {}
			}
        }
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		return false;
	}

	/**
	 * to get a visible Propety (Field or Getter) of a object
	 * @param obj Object to invoke
	 * @param prop property to call
	 * @return property value
	 * @throws PageException
	 */
	public static Object getProperty(Object obj, String prop) throws PageException  {
	    Object rtn=getField(obj,prop,NULL);// NULL is used because the field can contain null as well
		if(rtn!=NULL) return rtn;
		
		char first=prop.charAt(0);
        if(first>='0' && first<='9') throw new ApplicationException("there is no property with name ["+prop+"]");
        return callGetter(obj,prop);
        
	}

	/**
	 * to get a visible Propety (Field or Getter) of a object
	 * @param obj Object to invoke
	 * @param prop property to call
	 * @return property value
	 */
	public static Object getProperty(Object obj, String prop, Object defaultValue)  {
		
		// first try field
		Field[] fields = getFieldsIgnoreCase(obj.getClass(),prop,null);
		if(!ArrayUtil.isEmpty(fields)) {
			try {
				return fields[0].get(obj);
			} catch (Throwable t) {}
		}
		
		// then getter
        try {
            char first=prop.charAt(0);
            if(first>='0' && first<='9') return defaultValue;
            return getGetter(obj.getClass(), prop).invoke(obj);
        } catch (Throwable e1) {
            return defaultValue;
        } 
	}
	
	/**
	 * assign a value to a visible Property (Field or Setter) of a object
	 * @param obj Object to assign value to his property
	 * @param prop name of property
	 * @param value Value to assign
	 * @throws PageException
	 */
	public static void setProperty(Object obj, String prop,Object value) throws PageException{
	    boolean done=false;
		try {
	    	if(setField(obj,prop,value))done=true;
	    } 
		catch (Throwable t) {
			
        }
	    if(!done)callSetter(obj,prop,value);
	}
	
	/**
	 * assign a value to a visible Property (Field or Setter) of a object
	 * @param obj Object to assign value to his property
	 * @param prop name of property
	 * @param value Value to assign
	 */
	public static void setPropertyEL(Object obj, String prop,Object value) {
		
		// first check for field
		Field[] fields = getFieldsIgnoreCase(obj.getClass(),prop,null);
		if(!ArrayUtil.isEmpty(fields)){
			try {
				fields[0].set(obj,value);
				return;
			} catch (Throwable t) {}
		}
		
		// then check for setter
        try {
            getSetter(obj, prop, value).invoke(obj);
        } 
        catch (Throwable t) {} 
        
	}
	
	/**
	 * clean all Arguments
     * @param args arguments to clean
     * @return cleaned Arguments
	 * @throws PageException
     */
    private static Object[] cleanArgs(Object[] args) {
        if(args==null)return new Object[0];
        for(int i=0;i<args.length;i++) {
            if(args[i] instanceof ObjectWrap) {
				try {
					args[i]=((ObjectWrap)args[i]).getEmbededObject();
				}
				catch (PageException e) {}
            }
        }
        
        return args;
    }
    

    private static Object toNativeArray(Class clazz, Object obj) throws PageException {
		if(obj.getClass()==clazz) return obj;
		Object[] objs=null;
		if(obj instanceof Array)						objs=toRefArray((Array)obj);
		else if(obj instanceof List)					objs=toRefArray((List)obj);
		else if(Decision.isNativeArray(obj)) {
			if(obj.getClass()==boolean[].class)			objs=toRefArray((boolean[])obj);
			else if(obj.getClass()==byte[].class)		objs=toRefArray((byte[])obj);
			else if(obj.getClass()==char[].class)		objs=toRefArray((char[])obj);
			else if(obj.getClass()==short[].class)		objs=toRefArray((short[])obj);
			else if(obj.getClass()==int[].class)		objs=toRefArray((int[])obj);
			else if(obj.getClass()==long[].class)		objs=toRefArray((long[])obj);
			else if(obj.getClass()==float[].class)		objs=toRefArray((float[])obj);
			else if(obj.getClass()==double[].class)		objs=toRefArray((double[])obj);
			else										objs=(Object[]) obj;
			
		}
		//if(objs==null) return defaultValue;
		//Class srcClass = objs.getClass().getComponentType();
		Class trgClass = clazz.getComponentType();
		Object rtn = java.lang.reflect.Array.newInstance(trgClass, objs.length);
		for(int i=0;i<objs.length;i++) {
			//java.lang.reflect.Array.set(rtn, i, convert(objs[i], srcClass, trgClass));
			java.lang.reflect.Array.set(rtn, i, convert(objs[i], trgClass));
		}
		return rtn;
	}
	

	private static Object[] toRefArray(boolean[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=src[i]?Boolean.TRUE:Boolean.FALSE;
		}
		return trg;
	}

	private static Object[] toRefArray(byte[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=new Byte(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(char[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=new Character(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(short[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=Short.valueOf(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(int[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=Integer.valueOf(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(long[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=Long.valueOf(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(float[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=new Float(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(double[] src) {
		Object[] trg=new Object[src.length];
		for(int i=0;i<trg.length;i++) {
			trg[i]=new Double(src[i]);
		}
		return trg;
	}

	private static Object[] toRefArray(Array array) throws PageException {
		Object[] objs=new Object[array.size()];
		for(int i=0;i<objs.length;i++) {
			objs[i]=array.getE(i+1);
		}
		return objs;
	}
	
	private static Object[] toRefArray(List list) {
		Object[] objs=new Object[list.size()];
		for(int i=0;i<objs.length;i++) {
			objs[i]=list.get(i);
		}
		return objs;
	}

	public static boolean isGetter(Method method) {
		if(method.getParameterTypes().length>0) return false;
		if(method.getReturnType()==void.class) return false;
		if(!method.getName().startsWith("get") && !method.getName().startsWith("is")) return false;
		if(method.getDeclaringClass()==Object.class) return false;
		return true;
	}

	public static boolean isSetter(Method method) {
		if(method.getParameterTypes().length!=1) return false;
		if(method.getReturnType()!=void.class) return false;
		if(!method.getName().startsWith("set")) return false;
		if(method.getDeclaringClass()==Object.class) return false;
		return true;
	}

	/**
	 * return all methods that are defined by the class itself (not extended)
	 * @param clazz
	 * @return
	 */
	public static Method[] getDeclaredMethods(Class clazz) {
		Method[] methods = clazz.getMethods();
		ArrayList list=new ArrayList();
		for(int i=0;i<methods.length;i++) {
			if(methods[i].getDeclaringClass()==clazz) list.add(methods[i]);
		}
		if(list.size()==0) return new Method[0];
		return (Method[]) list.toArray(new Method[list.size()]);
	}

	public static Method[] getSetters(Class clazz) {
		Method[] methods = clazz.getMethods();
		ArrayList list=new ArrayList();
		for(int i=0;i<methods.length;i++) {
			if(isSetter(methods[i])) list.add(methods[i]);
		}
		if(list.size()==0) return new Method[0];
		return (Method[]) list.toArray(new Method[list.size()]);
	}
	
	public static Method[] getGetters(Class clazz) {
		Method[] methods = clazz.getMethods();
		ArrayList list=new ArrayList();
		for(int i=0;i<methods.length;i++) {
			if(isGetter(methods[i])) list.add(methods[i]);
		}
		if(list.size()==0) return new Method[0];
		return (Method[]) list.toArray(new Method[list.size()]);
	}

	/**
	 * check if given class "from" can be converted to class "to" without explicit casting
	 * @param from source class
	 * @param to target class
	 * @return is it possible to convert from "from" to "to"
	 */
	public static boolean canConvert(Class from, Class to) {
		// Identity Conversions
		if(from==to) return true;
		
		// Widening Primitive Conversion
		if(from==byte.class) {
			return to==short.class || to==int.class || to==long.class || to==float.class || to==double.class ;
		}
		if(from==short.class) {
			return to==int.class || to==long.class || to==float.class || to==double.class ;
		}
		if(from==char.class) {
			return to==int.class || to==long.class || to==float.class || to==double.class ;
		}
		if(from==int.class) {
			return to==long.class || to==float.class || to==double.class ;
		}
		if(from==long.class) {
			return to==float.class || to==double.class ;
		}
		if(from==float.class) {
			return to==double.class ;
		}
		return false;
	}

	

	 
}