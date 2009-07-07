package railo.runtime.reflection.storage;

import java.lang.reflect.Method;
import java.util.WeakHashMap;

import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;

/**
 * Method Storage Class
 */
public final class WeakMethodStorage {
	private WeakHashMap map=new WeakHashMap();
	
	/**
	 * returns a methods matching given criteria or null if method doesn't exist
	 * @param clazz clazz to get methods from
	 * @param methodName Name of the Method to get
	 * @param count wished count of arguments
	 * @return matching Methods as Array
	 */
	public Method[] getMethods(Class clazz,String methodName, int count) {
		Object o=map.get(clazz);
		StructImpl methodsMap;
		if(o==null) {
			methodsMap=store(clazz);
		}
		else methodsMap=(StructImpl) o;
		
		o=methodsMap.get(methodName,null);
		if(o==null) return null;
		ArrayImpl methods=(ArrayImpl) o;
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
		ArrayImpl methodArgs;
		if(o==null) {
			methodArgs=new ArrayImpl();
			methodsMap.setEL(methodName,methodArgs);
		}
		else methodArgs=(ArrayImpl) o;
		storeArgs(method,methodArgs);
		//Modifier.isStatic(method.getModifiers());
	}

	/**
	 * stores arguments of a method
	 * @param method
	 * @param methodArgs
	 */
	private void storeArgs(Method method, ArrayImpl methodArgs) {
		
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