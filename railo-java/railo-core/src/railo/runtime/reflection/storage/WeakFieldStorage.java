package railo.runtime.reflection.storage;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import org.apache.commons.collections.map.ReferenceMap;

import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * Method Storage Class
 */
public final class WeakFieldStorage {
	private WeakHashMap map=new WeakHashMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	/**
	 * returns all fields matching given criteria or null if field does exist
	 * @param clazz clazz to get field from
	 * @param fieldname Name of the Field to get
	 * @return matching Fields as Array
	 */
	public synchronized Field[] getFields(Class clazz,String fieldname) {
		Object o=map.get(clazz);
		Struct fieldMap;
		if(o==null) {
			fieldMap=store(clazz);
		}
		else fieldMap=(Struct) o;
		
		o=fieldMap.get(fieldname,null);
		if(o==null) return null;
		return (Field[]) o;
		
	}


	/**
	 * store a class with his methods
	 * @param clazz
	 * @return returns stored Struct
	 */
	private StructImpl store(Class clazz) {
		Field[] fieldsArr=clazz.getFields();
		StructImpl fieldsMap=new StructImpl();
		for(int i=0;i<fieldsArr.length;i++) {
			storeField(fieldsArr[i],fieldsMap);
		}
		map.put(clazz,fieldsMap);
		return fieldsMap;
	}

	/**
	 * stores a single method
	 * @param field
	 * @param fieldsMap
	 */
	private void storeField(Field field, StructImpl fieldsMap) {
		String fieldName=field.getName();
		Object o=fieldsMap.get(fieldName,null);		
		Field[] args;
		if(o==null) {
			args=new Field[1];
			fieldsMap.setEL(fieldName,args);
		}
		else {
			Field[] fs = (Field[]) o;
			args = new Field[fs.length+1];
			for(int i=0;i<fs.length;i++) {
			    fs[i].setAccessible(true);
				args[i]=fs[i];
			}
			fieldsMap.setEL(fieldName,args);
		}
		args[args.length-1]=field;
	}

}