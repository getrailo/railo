package railo.runtime.reflection.storage;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.map.ReferenceMap;

import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

/**
 * Method Storage Class
 */
public final class SoftMethodStorage {
	private Map<Class,Map<Key,Array>> map=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	/**
	 * returns a methods matching given criteria or null if method doesn't exist
	 * @param clazz clazz to get methods from
	 * @param methodName Name of the Method to get
	 * @param count wished count of arguments
	 * @return matching Methods as Array
	 */
	public Method[] getMethods(Class clazz,Collection.Key methodName, int count) {
		Map<Key,Array> methodsMap = map.get(clazz); 
		if(methodsMap==null) 
			methodsMap=store(clazz);
		
		Array methods = methodsMap.get(methodName);
		if(methods==null) return null;
		
		Object o = methods.get(count+1,null);
		if(o==null) return null;
		return (Method[]) o;
	}


	/**
	 * store a class with his methods
	 * @param clazz
	 * @return returns stored struct
	 */
	private Map<Key,Array> store(Class clazz) {
		Method[] methods=clazz.getMethods();
		Map<Key,Array> methodsMap=new ConcurrentHashMap<Key, Array>();
		for(int i=0;i<methods.length;i++) {
			storeMethod(methods[i],methodsMap);
			
		}
		map.put(clazz,methodsMap);
		return methodsMap;
	}

	/**
	 * stores a single method
	 * @param method
	 * @param methodsMap
	 */
	private synchronized void storeMethod(Method method, Map<Key,Array> methodsMap) {
		Key methodName = KeyImpl.init(method.getName());

		Array methodArgs=methodsMap.get(methodName);
		if(methodArgs==null) {
			methodArgs=new ArrayImpl();
			methodsMap.put(methodName,methodArgs);
		}
		
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