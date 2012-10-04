package railo.runtime.reflection.storage;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * Method Storage Class
 */
public final class WeakMethodStorage {
	private Map<Class,Struct> map=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	/**
	 * returns a methods matching given criteria or null if method doesn't exist
	 * @param clazz clazz to get methods from
	 * @param methodName Name of the Method to get
	 * @param count wished count of arguments
	 * @return matching Methods as Array
	 */
	public synchronized Method[] getMethods(Class clazz,Collection.Key methodName, int count) {
		Struct methodsMap = map.get(clazz); 
		if(methodsMap==null) 
			methodsMap=store(clazz);
		
		Object o = methodsMap.get(methodName,null);
		if(o==null) return null;
		Array methods=(Array) o;
		o=methods.get(count+1,null);
		if(o==null) return null;
		return (Method[]) o;
	}


	/**
	 * store a class with his methods
	 * @param clazz
	 * @return returns stored struct
	 */
	private StructImpl store(Class clazz) {
		Method[] methodsArr=clazz.getMethods();
		StructImpl methodsMap=new StructImpl();
		for(int i=0;i<methodsArr.length;i++) {
			storeMethod(methodsArr[i],methodsMap);
			
		}
		map.put(clazz,methodsMap);
		return methodsMap;
	}

	/**
	 * stores a single method
	 * @param method
	 * @param methodsMap
	 */
	private void storeMethod(Method method, StructImpl methodsMap) {
		Key methodName = KeyImpl.init(method.getName());
		
		
		Object o=methodsMap.get(methodName,null);
		Array methodArgs;
		if(o==null) {
			methodArgs=new ArrayImpl();
			methodsMap.setEL(methodName,methodArgs);
		}
		else methodArgs=(Array) o;
		storeArgs(method,methodArgs);
		//Modifier.isStatic(method.getModifiers());
	}

	/**
	 * stores arguments of a method
	 * @param method
	 * @param methodArgs
	 */
	private void storeArgs(Method method, Array methodArgs) {
		
		Class[] pmt = method.getParameterTypes();
		Object o=methodArgs.get(pmt.length+1,null);
		Method[] args;
		if(o==null) {
			args=new Method[1];
			methodArgs.setEL(pmt.length+1,args);
		}
		else {
			Method[] ms = (Method[]) o;
			args = new Method[ms.length+1];
			for(int i=0;i<ms.length;i++) {
				args[i]=ms[i];
			}
			methodArgs.setEL(pmt.length+1,args);
		}
		args[args.length-1]=method;
	}
}