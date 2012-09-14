package railo.runtime.op;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.converter.JavaConverter;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Duplicable;
import railo.runtime.type.UDF;


/**
 *
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class Duplicator {

	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _boolean boolean value to duplicate
	 * @return duplicated value
	 */
	public static boolean duplicate(boolean _boolean) {
		return _boolean;
	}
	
	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _byte byte value to duplicate
	 * @return duplicated value
	 */
	public static byte duplicate(byte _byte) {
		return _byte;
	}
	
	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _short byte value to duplicate
	 * @return duplicated value
	 */
	public static short duplicate(short _short) {
		return _short;
	}
	
	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _int byte value to duplicate
	 * @return duplicated value
	 */
	public static int duplicate(int _int) {
		return _int;
	}
	
	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _long byte value to duplicate
	 * @return duplicated value
	 */
	public static long duplicate(long _long) {
		return _long;
	}
	
	/**
	 * primitive value duplication (do nothing, value type must not be duplicated)
	 * @param _double byte value to duplicate
	 * @return duplicated value
	 */
	public static double duplicate(double _double) {
		return _double;
	}
	
	/**
	 * reference type value duplication
	 * @param object object to duplicate
	 * @return duplicated value
	 */
	
	public static Object duplicate(Object object, boolean deepCopy) {
		if(object == null) 				return null;
        if(object instanceof Number)	return object;
        if(object instanceof String)	return object;
        if(object instanceof Date)		return ((Date)object).clone();
        if(object instanceof Boolean)	return object;
		
        RefBoolean before=new RefBooleanImpl();
		try{
			Object copy = ThreadLocalDuplication.get(object,before);
			if(copy!=null){
	    		return copy;
	    	}
	    	
	
	    	//if(object instanceof CollectionPlus)return ((CollectionPlus)object).duplicate(deepCopy,ThreadLocalDuplication.getMap());
	    	if(object instanceof Collection)return ((Collection)object).duplicate(deepCopy);
	    	if(object instanceof Duplicable)return ((Duplicable)object).duplicate(deepCopy);
	    	if(object instanceof UDF)		return ((UDF)object).duplicate();
	        if(object instanceof List)		return duplicateList((List)object,deepCopy);
	        if(object instanceof Map) 		return duplicateMap((Map)object,deepCopy);
			if(object instanceof Serializable) {
				try {
					String ser = JavaConverter.serialize((Serializable)object);
					return JavaConverter.deserialize(ser);
					
				} catch (Throwable t) {}
			}
        }  
        finally {
        	if(!before.toBooleanValue())ThreadLocalDuplication.reset();
        }
	    
		return object;
    }

    public static List duplicateList(List list, boolean deepCopy) {
    	List newList;
    	try {
    		newList=(List) ClassUtil.loadInstance(list.getClass());
		} catch (ClassException e) {
			newList=new ArrayList();
		}
    	return duplicateList(list, newList, deepCopy);
	}
    
    public static List duplicateList(List list,List newList, boolean deepCopy) {
    	ListIterator it = list.listIterator();	
    	while(it.hasNext()) {
    		if(deepCopy)
        		newList.add(Duplicator.duplicate(it.next(),deepCopy));
    		else
    			newList.add(it.next());
    	}
		return newList;
	}

	/**
     * duplicate a map
     * @param map
     * @param doKeysLower
     * @return duplicated Map
     * @throws PageException 
     */
    public static Map duplicateMap(Map map, boolean doKeysLower,boolean deepCopy) throws PageException{
        if(doKeysLower) {
        	Map newMap;
        	try {
        		newMap=(Map) ClassUtil.loadInstance(map.getClass());
    		} catch (ClassException e) {
    			newMap=new HashMap();
    		}
    		ThreadLocalDuplication.set(map,newMap);
            Iterator it=map.keySet().iterator();
            while(it.hasNext()) {
                Object key=it.next();
                if(deepCopy)newMap.put(StringUtil.toLowerCase(Caster.toString(key)),duplicate(map.get(key), deepCopy));
                else newMap.put(StringUtil.toLowerCase(Caster.toString(key)),map.get(key));
            }
            //ThreadLocalDuplication.remove(map); removed "remove" to catch sisters and brothers
            return newMap;
        }
        return duplicateMap(map,deepCopy);
    }

    public static Map duplicateMap(Map map,boolean deepCopy){
    	Map other;
    	try {
			other=(Map) ClassUtil.loadInstance(map.getClass());
		} catch (ClassException e) {
			other=new HashMap();
    	}
		ThreadLocalDuplication.set(map,other);
        duplicateMap(map,other, deepCopy);
        //ThreadLocalDuplication.remove(map); removed "remove" to catch sisters and brothers
        return other;
    }
    
    public static Map duplicateMap(Map map,Map newMap,boolean deepCopy){
    	
    	
    	Iterator it=map.keySet().iterator();
        while(it.hasNext()) {
            Object key=it.next();
            if(deepCopy)newMap.put(key,duplicate(map.get(key),deepCopy));
            else newMap.put(key,map.get(key));
        }
        return newMap;
    }
}


