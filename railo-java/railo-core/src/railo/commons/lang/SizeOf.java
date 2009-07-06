package railo.commons.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.runtime.type.Sizeable;

/**
 * Calculation of object size.
 */
public class SizeOf {
  
	private static ThreadLocal _inside=new ThreadLocal();
	private static ThreadLocal map=new ThreadLocal();



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
    public static long size(Object object) {
    	return size(object, Integer.MAX_VALUE);
    }
	
	public static long size(Object object,int maxDepth) {
    	if (object==null)return 0;
    	boolean wasInside=inside(true);
    	//print.out(wasInside?" - x":"x");
    	Map instances=get(!wasInside);
    	//IdentityHashMap instances = new IdentityHashMap();
        Map dictionary = new HashMap();
        long size = _size(object, instances, dictionary, maxDepth, Long.MAX_VALUE);
        inside(wasInside);
        //print.out(wasInside?" - x":"x");
        return size;
    }


	private static long _size(Object object, Map instances,Map dictionary, int maxDepth,long maxSize) {
        if (object==null || instances.containsKey(object) || maxDepth==0 || maxSize < 0) return 0;
        
        instances.put(object, object);
        
        if(object instanceof Sizeable)return ((Sizeable)object).sizeOf();
        
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
		return Meta.LONG_SIZE;
	}

	public static long size(boolean value) {
		return Meta.BOOLEAN_SIZE;
	}
}


class Meta {
    private static final int OBJECT_GRANULARITY_IN_BYTES = 8;
    private static final int WORD_SIZE = Architecture.getVMArchitecture().getWordSize();
    private static final int HEADER_SIZE = 2 * WORD_SIZE;

    public static final int LONG_SIZE = 8;
    public static final int INT_SIZE = 4;
    public static final int SHORT_SIZE = 2;
    public static final int BYTE_SIZE = 1;
    public static final int BOOLEAN_SIZE = 1;
    public static final int REF_SIZE = WORD_SIZE;

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
            return LONG_SIZE;
        }

        if (componentClass.equals(Integer.TYPE) ||
            componentClass.equals(Float.TYPE)) {
            return INT_SIZE;
        }

        if (componentClass.equals(Character.TYPE) ||
            componentClass.equals(Short.TYPE)) {
            return SHORT_SIZE;
        }

        if (componentClass.equals(Byte.TYPE) ||
            componentClass.equals(Boolean.TYPE))  {
            return BYTE_SIZE;
        }

        return REF_SIZE;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    private void processArrayClass(Class aClass) {
        isArray = true;
        componentClass = aClass.getComponentType();
        componentSize = getComponentSize(componentClass);
        instanceSize = HEADER_SIZE + WORD_SIZE;
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

        int alignedParentSize = align(parentTotalFieldSize, OBJECT_GRANULARITY_IN_BYTES);
        int paddingSpace = alignedParentSize - parentTotalFieldSize;
        // we try to pad with ints, and then shorts, and then bytes, and then refs.
        while (localIntCount > 0 && paddingSpace >= INT_SIZE) {
            paddingSpace -= INT_SIZE;
            localIntCount--;
        }

        while(localShortCount > 0 && paddingSpace >= SHORT_SIZE) {
            paddingSpace -= SHORT_SIZE;
            localShortCount--;
        }

        while (localByteCount > 0 && paddingSpace >= BYTE_SIZE) {
            paddingSpace -= BYTE_SIZE;
            localByteCount--;
        }

        while (localRefCount > 0 && paddingSpace >= REF_SIZE) {
            paddingSpace -= REF_SIZE;
            localRefCount--;
        }

        int preFieldSize = paddingSpace +
                longCount * LONG_SIZE +
                intCount * INT_SIZE +
                shortCount * SHORT_SIZE +
                byteCount * BYTE_SIZE +
                refCount * REF_SIZE;

        fieldSize = align(preFieldSize, REF_SIZE);
        
        totalFieldSize = parentTotalFieldSize + fieldSize;

        instanceSize = align(HEADER_SIZE + totalFieldSize, OBJECT_GRANULARITY_IN_BYTES);
    }

    public int calcArraySize(int length) {
        return align(instanceSize + componentSize * length, OBJECT_GRANULARITY_IN_BYTES);
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
            String archString = System.getProperty("sun.arch.data.model");
            if (archString != null) {
                if (archString.equals("32")) {
                    return ARCH_32_BITS;
                } else if (archString.equals("64")) {
                    return ARCH_64_BITS;
                }
            }
            return ARCH_UNKNOWN;
        }
    }
