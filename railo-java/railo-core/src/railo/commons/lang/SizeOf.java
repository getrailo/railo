package railo.commons.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import railo.commons.io.SystemUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;

/**
 * Calculation of object size.
 */
public class SizeOf {
	public static final int OBJECT_GRANULARITY_IN_BYTES = 8;
	public static final int WORD_SIZE = Architecture.getVMArchitecture().getWordSize();
    public static final int HEADER_SIZE = 2 * WORD_SIZE;

    public static final int DOUBLE_SIZE = 8;
    public static final int FLOAT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int INT_SIZE = 4;
    public static final int SHORT_SIZE = 2;
    public static final int BYTE_SIZE = 1;
    public static final int BOOLEAN_SIZE = 1;
    public static final int CHAR_SIZE = 2;
    public static final int REF_SIZE = WORD_SIZE;

	private static ThreadLocal _inside=new ThreadLocal();
	private static ThreadLocal map=new ThreadLocal();


	private static ThreadLocal<Set<Integer>> done=new ThreadLocal<Set<Integer>>();
	

	private static boolean inside(boolean inside) {
		Boolean was=(Boolean) _inside.get();
		_inside.set(inside?Boolean.TRUE:Boolean.FALSE);
		return was!=null && was.booleanValue();
	}
	
    private static Map get(boolean clear) {
		Map m=(Map) map.get();
		if(m==null){
			m=new IdentityHashMap();
			map.set(m);
		}
		else if(clear) m.clear();
		
    	return m;
	}
    
    
    /**
     * Calculates the size of an object.
     * @param object the object that we want to have the size calculated.
     * @return the size of the object or 0 if null.
     */

    public static long size2(Object o) {
    	Set<Integer> d = done.get();
    	boolean inside=true;
    	if(d==null){
    		inside=false;
    		d=new HashSet<Integer>();
    		done.set(d);
    	}
    	try{
    		return size(o,d);
    	}
    	finally{
    		if(!inside) done.set(null);
    	}
    }
    
    private static long size(Object o,Set<Integer> done) {
    	if(o == null) return 0;
    	if(done.contains(o.hashCode())) return 0;
    	done.add(o.hashCode());
    	
    	if(o instanceof Sizeable){
    		return ((Sizeable)o).sizeOf();
    	}
    	Class clazz = o.getClass();
    	long size=0;
    	
    	// Native ARRAY
    	// TODO how big is the array itself
    	if (clazz.isArray()) {
    		Class ct = clazz.getComponentType();
    		
    		if(ct.isPrimitive())return primSize(ct)*Array.getLength(o);
    		
    		size=REF_SIZE*Array.getLength(o);
    		for (int i=Array.getLength(o)-1; i>=0;i--) {
                size += size(Array.get(o, i),done);
            }
        	return size;
        }
    	
    	if(o instanceof Boolean) return REF_SIZE+BOOLEAN_SIZE;
    	if(o instanceof Character) return REF_SIZE+CHAR_SIZE;
    	
    	if(o instanceof Number){
    		if(o instanceof Double || o instanceof Long) return REF_SIZE+LONG_SIZE;
    		if(o instanceof Byte) return REF_SIZE+BYTE_SIZE;
    		if(o instanceof Short) return REF_SIZE+SHORT_SIZE;
    		return REF_SIZE+INT_SIZE;// float,int
    	}
    	if(o instanceof String){
    		int len=((String)o).length();
    		return (REF_SIZE*len)+(REF_SIZE*CHAR_SIZE);
    	}
    	
    	
    	throw new PageRuntimeException(new ExpressionException("can not terminate the size of a object of type ["+Caster.toTypeName(o)+":"+o.getClass().getName()+"]"));
    }
    
    
    private static int primSize(Class ct) {
    	if(ct==double.class) return LONG_SIZE;
    	if(ct==long.class) return LONG_SIZE;
    	if(ct==float.class) return INT_SIZE;
    	if(ct==short.class) return SHORT_SIZE;
    	if(ct==int.class) return INT_SIZE;
    	if(ct==byte.class) return BYTE_SIZE;
    	if(ct==boolean.class) return BOOLEAN_SIZE;
    	return CHAR_SIZE;
	}

	public static long size(Object object) {
    	return size(object, Integer.MAX_VALUE);
    }
	
	public static long size(Object object,int maxDepth) {
    	if (object==null)return 0;
    	boolean wasInside=inside(true);
    	Map instances=get(!wasInside);
    	//IdentityHashMap instances = new IdentityHashMap();
        Map dictionary = new HashMap();
        long size = _size(object, instances, dictionary, maxDepth, Long.MAX_VALUE);
        inside(wasInside);
        return size;
    }


	private static long _size(Object object, Map instances,Map dictionary, int maxDepth,long maxSize) {
		try	{
		return __size(object, instances, dictionary, maxDepth, maxSize);
		}
		catch(Throwable t){
			t.printStackTrace();
			return 0;
		}
	}

	private static long __size(Object object, Map instances,Map dictionary, int maxDepth,long maxSize) {
        if (object==null || instances.containsKey(object) || maxDepth==0 || maxSize < 0) return 0;
        
        instances.put(object, object);
        
        if(object instanceof Sizeable)return ((Sizeable)object).sizeOf();
        
        if(object instanceof String){
        	return (SizeOf.CHAR_SIZE*((String)object).length())+SizeOf.REF_SIZE;
        }
        
        if(object instanceof Number){
        	if(object instanceof Double) return SizeOf.DOUBLE_SIZE+SizeOf.REF_SIZE;
        	if(object instanceof Float) return SizeOf.FLOAT_SIZE+SizeOf.REF_SIZE;
        	if(object instanceof Long) return SizeOf.LONG_SIZE+SizeOf.REF_SIZE;
        	if(object instanceof Integer) return SizeOf.INT_SIZE+SizeOf.REF_SIZE;
        	if(object instanceof Short) return SizeOf.SHORT_SIZE+SizeOf.REF_SIZE;
        	if(object instanceof Byte) return SizeOf.BYTE_SIZE+SizeOf.REF_SIZE;
        }
        
        
        if(object instanceof Object[]) {
        	 Object[] arr=(Object[]) object;
        	 long size=SizeOf.REF_SIZE;
        	 for(int i=0;i<arr.length;i++){
        		 size+=_size(arr[i], instances, dictionary, maxDepth - 1, maxSize);
        	 }
        	 return size;
        }
        
        if(object instanceof Map) {
        	long size=SizeOf.REF_SIZE;
        	Map.Entry entry;
        	Map map=(Map) object;
        	Iterator it = map.entrySet().iterator();
        	while(it.hasNext()){
        		entry=(Entry) it.next();
        		size+=SizeOf.REF_SIZE;
        		size+=_size(entry.getKey(), instances, dictionary, maxDepth - 1, maxSize);
        		size+=_size(entry.getValue(), instances, dictionary, maxDepth - 1, maxSize);
        	}
        	return size;
	    }
        if(object instanceof List) {
        	long size=SizeOf.REF_SIZE;
        	List list=(List) object;
        	Iterator it = list.iterator();
        	while(it.hasNext()){
        		size+=_size(it.next(), instances, dictionary, maxDepth - 1, maxSize);
        	}
        	return size;
	    }
        	
        
        Class clazz = object.getClass();
        
        Meta cmd = Meta.getMetaData(clazz, dictionary);
        long shallowSize = cmd.calcInstanceSize(object);
        long size = shallowSize;
        if (clazz.isArray() && !cmd.getComponentClass().isPrimitive()) {
        	for (int i=Array.getLength(object)-1; i>=0;i--) {
                size += _size(Array.get(object, i), instances, dictionary, maxDepth - 1, maxSize - size);
            }
        } 
        else {
            List values = cmd.getReferenceFieldValues(object);
            Iterator it = values.iterator();
            while(it.hasNext()) {
            	size += _size(it.next(), instances, dictionary, maxDepth - 1, maxSize - size);
            }
        }
        return size;
    }

	public static long size(long value) {
		return SizeOf.LONG_SIZE;
	}


	public static long size(boolean value) {
		return SizeOf.BOOLEAN_SIZE;
	}
}


class Meta {
    

    /** Class for which this metadata applies */
    private Class aClass;

    /** Parent class's metadata */
    private Meta superMetaData;

    private boolean isArray;

    /** Only filled if this is an array */
    private int componentSize;

    /** Only filled if this class is an array */
    private Class componentClass;

    /** Number of bytes reserved for the fields of this class and its parents,
     *  aligned to a word boundary. */
    private int fieldSize;

    /** this.fieldSize + super.totalFieldSize */
    private int totalFieldSize;

    /** Number of bytes reserved for instances of this class, aligned to a
     * 8 bytes boundary */
    private int instanceSize;

    private List referenceFields = new ArrayList();

    public static Meta getMetaData(Class aClass) {
        return getMetaData(aClass, new HashMap());
    }

    public static Meta getMetaData(Class aClass, Map dictionary) {
        if (aClass == null) {
            throw new IllegalArgumentException("aClass argument cannot be null");
        }

        if (aClass.isPrimitive()) {
            throw new IllegalArgumentException("ClassMetaData not supported for primitive types: " + aClass.getName());
        }

        if (dictionary.containsKey(aClass)) {
            return (Meta) dictionary.get(aClass);
        } 
        Meta result = new Meta(aClass, dictionary);
        dictionary.put(aClass, result);
        return result;
    }

    private Meta(Class aClass, Map dictionary) {
        
        Class parentClass = aClass.getSuperclass();
        if (parentClass != null) {
            superMetaData = getMetaData(parentClass, dictionary);
        }

        this.aClass = aClass;

        if (aClass.isArray()) {
            processArrayClass(aClass);
        } else {
            processRegularClass(aClass);
            processFields(aClass);
        }
    }

    private static int getComponentSize(Class componentClass) {
        if (componentClass.equals(Double.TYPE) ||
            componentClass.equals(Long.TYPE)) {
            return SizeOf.LONG_SIZE;
        }

        if (componentClass.equals(Integer.TYPE) ||
            componentClass.equals(Float.TYPE)) {
            return SizeOf.INT_SIZE;
        }

        if (componentClass.equals(Character.TYPE) ||
            componentClass.equals(Short.TYPE)) {
            return SizeOf.SHORT_SIZE;
        }

        if (componentClass.equals(Byte.TYPE) ||
            componentClass.equals(Boolean.TYPE))  {
            return SizeOf.BYTE_SIZE;
        }

        return SizeOf.REF_SIZE;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    private void processArrayClass(Class aClass) {
        isArray = true;
        componentClass = aClass.getComponentType();
        componentSize = getComponentSize(componentClass);
        instanceSize = SizeOf.HEADER_SIZE + SizeOf.WORD_SIZE;
    }

    private void processFields(Class aClass) {
        Field[] fields = aClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            int fieldModifier = field.getModifiers();
            if (Modifier.isStatic(fieldModifier)) {
                continue;
            }
            Class fieldType = field.getType();
            if (fieldType.isPrimitive()) {
                continue;
            }
            field.setAccessible(true);
            referenceFields.add(field);
        }
    }

    private void processRegularClass(Class aClass) {
        Field[] fields = aClass.getDeclaredFields();

        int longCount  = 0;
        int intCount   = 0;
        int shortCount = 0;
        int byteCount  = 0;
        int refCount   = 0;

        // Calculate how many fields of each type we have.
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            int fieldModifiers = field.getModifiers();
            if (Modifier.isStatic(fieldModifiers)) {
                continue;
            }

            Class fieldClass = field.getType();

            if (fieldClass.equals(Double.TYPE) ||
                fieldClass.equals(Long.TYPE)) {
                longCount++;
            } else if (fieldClass.equals(Integer.TYPE) ||
                fieldClass.equals(Float.TYPE)) {
                intCount++;
            } else if (fieldClass.equals(Character.TYPE) ||
                fieldClass.equals(Short.TYPE)) {
                shortCount++;
            }else if (fieldClass.equals(Byte.TYPE) ||
                fieldClass.equals(Boolean.TYPE))  {
                byteCount++;
            } else {
                refCount++;
            }
        }

        int localIntCount = intCount;
        int localShortCount = shortCount;
        int localByteCount = byteCount;
        int localRefCount = refCount;
        
        int parentTotalFieldSize = superMetaData == null ? 0 : superMetaData.getFieldSize();

        int alignedParentSize = align(parentTotalFieldSize, SizeOf.OBJECT_GRANULARITY_IN_BYTES);
        int paddingSpace = alignedParentSize - parentTotalFieldSize;
        // we try to pad with ints, and then shorts, and then bytes, and then refs.
        while (localIntCount > 0 && paddingSpace >= SizeOf.INT_SIZE) {
            paddingSpace -= SizeOf.INT_SIZE;
            localIntCount--;
        }

        while(localShortCount > 0 && paddingSpace >= SizeOf.SHORT_SIZE) {
            paddingSpace -= SizeOf.SHORT_SIZE;
            localShortCount--;
        }

        while (localByteCount > 0 && paddingSpace >= SizeOf.BYTE_SIZE) {
            paddingSpace -= SizeOf.BYTE_SIZE;
            localByteCount--;
        }

        while (localRefCount > 0 && paddingSpace >= SizeOf.REF_SIZE) {
            paddingSpace -= SizeOf.REF_SIZE;
            localRefCount--;
        }

        int preFieldSize = paddingSpace +
                longCount * SizeOf.LONG_SIZE +
                intCount * SizeOf.INT_SIZE +
                shortCount * SizeOf.SHORT_SIZE +
                byteCount * SizeOf.BYTE_SIZE +
                refCount * SizeOf.REF_SIZE;

        fieldSize = align(preFieldSize, SizeOf.REF_SIZE);
        
        totalFieldSize = parentTotalFieldSize + fieldSize;

        instanceSize = align(SizeOf.HEADER_SIZE + totalFieldSize, SizeOf.OBJECT_GRANULARITY_IN_BYTES);
    }

    public int calcArraySize(int length) {
        return align(instanceSize + componentSize * length, SizeOf.OBJECT_GRANULARITY_IN_BYTES);
    }

    public int calcInstanceSize(Object instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Parameter cannot be null");
        }
        if (!instance.getClass().equals(aClass)) {
            throw new IllegalArgumentException("Parameter not of proper class.  Was: " + instance.getClass() + " expected: " + aClass);
        }

        if (isArray) {
            int length = Array.getLength(instance);
            return calcArraySize(length);
        } 
        return instanceSize;
    }

    public List getReferenceFieldValues(Object instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }

        List results;
        if (superMetaData == null) {
            results = new ArrayList();
        } else {
            results = superMetaData.getReferenceFieldValues(instance);
        }
        Iterator it = referenceFields.iterator();
        Field field;
        while(it.hasNext()) {
        	field=(Field) it.next();
            Object value;
            try {
                value = field.get(instance);
            } catch (IllegalAccessException ex) {
                // Should never happen in practice.
                throw new IllegalStateException("Unexpected exeption: "+ ex.getMessage());
            }
            if (value != null) {
                results.add(value);
            }
        }
        return results;
    }

    public Meta getSuperMetaData() {
        return superMetaData;
    }

    public int getInstanceSize() {
        return instanceSize;
    }

    public int getTotalFieldSize() {
        return totalFieldSize;
    }

    public Class getTheClass() {
        return aClass;
    }

    public Class getComponentClass() {
        return componentClass;
    }
    
    public static int align(int size, int granularity) {
        return size + (granularity - size % granularity) % granularity;
    }
}

    
    class Architecture {

    	private static final Architecture ARCH_32_BITS=new Architecture(32, 4);
    	private static final Architecture ARCH_64_BITS=new Architecture(64, 8);
    	private static final Architecture ARCH_UNKNOWN=new Architecture(32, 4);
        
        private int bits;
        private int wordSize;

        private Architecture(int bits, int wordSize) {
            this.bits = bits;
            this.wordSize = wordSize;
        }

        public int getBits() {
            return bits;
        }

        public int getWordSize() {
            return wordSize;
        }

        public static Architecture getVMArchitecture() {
            if (SystemUtil.getJREArch()==SystemUtil.ARCH_32) 
            	return ARCH_32_BITS;
            else if (SystemUtil.getJREArch()==SystemUtil.ARCH_64) 
            	return ARCH_64_BITS;
            
            return ARCH_UNKNOWN;
        }
    }
