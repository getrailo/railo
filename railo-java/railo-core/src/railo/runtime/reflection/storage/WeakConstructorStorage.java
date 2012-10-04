package railo.runtime.reflection.storage;

import java.lang.reflect.Constructor;
import java.util.WeakHashMap;

import org.apache.commons.collections.map.ReferenceMap;

import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

/**
 * Constructor Storage Class
 */
public final class WeakConstructorStorage {
	private WeakHashMap map=new WeakHashMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	/**
	 * returns a constructor matching given criteria or null if Constructor doesn't exist
	 * @param clazz Class to get Constructor for
	 * @param count count of arguments for the constructor
	 * @return returns the constructors
	 */
	public synchronized Constructor[] getConstructors(Class clazz,int count) {
		Object o=map.get(clazz);
		Array con;
		if(o==null) {
			con=store(clazz);
		}
		else con=(Array) o;

		o=con.get(count+1,null);
		if(o==null) return null;
		return (Constructor[]) o;
	}

	/**
	 * stores the constructors for a Class
	 * @param clazz 
	 * @return stored structure
	 */
	private Array store(Class clazz) {
			Constructor[] conArr=clazz.getConstructors();
			Array args=new ArrayImpl();
			for(int i=0;i<conArr.length;i++) {
				storeArgs(conArr[i],args);
			}
			map.put(clazz,args);
			return args;
		
	}

	/**
	 * seperate and store the different arguments of one constructor
	 * @param constructor
	 * @param conArgs
	 */
	private void storeArgs(Constructor constructor, Array conArgs) {
		Class[] pmt = constructor.getParameterTypes();
		Object o=conArgs.get(pmt.length+1,null);
		Constructor[] args;
		if(o==null) {
			args=new Constructor[1];
			conArgs.setEL(pmt.length+1,args);
		}
		else {
			Constructor[] cs=(Constructor[]) o;
			args = new Constructor[cs.length+1];
			for(int i=0;i<cs.length;i++) {
				args[i]=cs[i];
			}
			conArgs.setEL(pmt.length+1,args);
		}
		args[args.length-1]=constructor;
		
	}
}