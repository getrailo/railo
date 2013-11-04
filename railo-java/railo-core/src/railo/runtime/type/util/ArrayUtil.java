package railo.runtime.type.util;

import java.sql.Types;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.ArrayUtilException;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.type.Array;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.comparator.NumberComparator;
import railo.runtime.type.comparator.SortRegister;
import railo.runtime.type.comparator.TextComparator;

/**
 * Util for diffrent methods to manipulate arrays
 */
public final class ArrayUtil {
    
    public static final Object[] OBJECT_EMPTY = new Object[]{};
	
    /**
     * trims all value of a String Array
     * @param arr
     * @return trimmed array
     */
    public static String[] trim(String[] arr) {
        for(int i=0;i<arr.length;i++) {
            arr[i]=arr[i].trim();
        }
        return arr;
    }
    
    
	/**
	 * @param list
	 * @return array
	 */
	public static SortRegister[] toSortRegisterArray(ArrayList list) {
		SortRegister[] arr=new SortRegister[list.size()];
		for(int i=0;i<arr.length;i++) {
			arr[i]=new SortRegister(i,list.get(i));
		}
		return arr;
	}

	/**
	 * @param column
	 * @return array
	 */
	public static SortRegister[] toSortRegisterArray(QueryColumn column) {
		SortRegister[] arr=new SortRegister[column.size()];
		int type = column.getType();
		for(int i=0;i<arr.length;i++) {
			arr[i]=new SortRegister(i,toSortRegisterArray(column.get(i+1,null),type));
		}
		return arr;
	}
	
	private static Object toSortRegisterArray(Object value, int type) {
		
		Object mod=null;
	    // Date
	    if(Types.TIMESTAMP==type) {
	        mod= Caster.toDate(value, true, null,null);
	    }
	    // Double
	    else if(Types.DOUBLE==type) {
	    	mod= Caster.toDouble(value,null);
	    }
	    // Boolean
	    else if(Types.BOOLEAN==type) {
	    	mod= Caster.toBoolean(value,null);
	    }
	    // Varchar
	    else if(Types.VARCHAR==type) {
	    	mod= Caster.toString(value,null);
	    }
	    else return value;
	    
	    if(mod!=null) return mod;
	    return value;
	}
	
	/**
	 * swap to values of the array
	 * @param array
	 * @param left left value to swap
	 * @param right right value to swap
	 * @throws ExpressionException
	 */
	public static void swap(Array array, int left, int right) throws ExpressionException {
		int len=array.size();
		
		if(len==0)
			throw new ExpressionException("array is empty");
		if(left<1 || left>len)
			throw new ExpressionException("invalid index ["+left+"]","valid indexes are from 1 to "+len);
		if(right<1 || right>len)
			throw new ExpressionException("invalid index ["+right+"]","valid indexes are from 1 to "+len);
		
		
		try {
			Object leftValue=array.get(left,null);
			Object rightValue=array.get(right,null);
			
			array.setE(left,rightValue);
			array.setE(right,leftValue);
		} catch (PageException e) {
			throw new ExpressionException("can't swap values of array",e.getMessage());
		}
		
	}
	
	/**
	 * find a object in array
	 * @param array
	 * @param object object to find
	 * @return position in array or 0
	 */
	public static int find(Array array, Object object) {
		int len=array.size();
		for(int i=1;i<=len;i++) {
			Object tmp=array.get(i,null);
			try {
				if(tmp !=null && Operator.compare(object,tmp)==0)
					return i;
			} catch (PageException e) {}
		}
		return 0;
	}
	
	/**
	 * average of all values of the array, only work when all values are numeric
	 * @param array
	 * @return average of all values
	 * @throws ExpressionException
	*/
	public static double avg(Array array) throws ExpressionException {
		if(array.size()==0)return 0;
		return sum(array)/array.size();
	}
	
	/**
	 * sum of all values of a array, only work when all values are numeric
	 * @param array Array 
	 * @return sum of all values
	 * @throws ExpressionException
	*/
	public static double sum(Array array) throws ExpressionException {
		if(array.getDimension()>1)
			throw new ExpressionException("can only get sum/avg from 1 dimensional arrays");
		
		double rtn=0;
		int len=array.size();
		//try {			
			for(int i=1;i<=len;i++) {
				rtn+=_toDoubleValue(array,i);
			}
		/*} 
		catch (PageException e) {
			throw new ExpressionException("exception while execute array operation: "+e.getMessage());
		}*/
		return rtn;
	}
	
	private static double _toDoubleValue(Array array, int i) throws ExpressionException {
		Object obj = array.get(i,null);
		if(obj==null)throw new ExpressionException("there is no element at position ["+i+"] or the element is null");
		double tmp = Caster.toDoubleValue(obj,Double.NaN);
		if(Double.isNaN(tmp))
			throw new CasterException(obj,Double.class);
		return tmp;
	}


	/**
	 * the smallest value, of all values inside the array, only work when all values are numeric
	 * @param array
	 * @return the smallest value
	 * @throws PageException 
	*/
	public static double min(Array array) throws PageException {
		if(array.getDimension()>1)
			throw new ExpressionException("can only get max value from 1 dimensional arrays");
		if(array.size()==0) return 0;
		
		double rtn=_toDoubleValue(array,1);
		int len=array.size();
		try {
			for(int i=2;i<=len;i++) {
				double v=_toDoubleValue(array,i);
				if(rtn>v)rtn=v;
				
			}
		} catch (PageException e) {
			throw new ExpressionException("exception while execute array operation: "+e.getMessage());
		}
		return rtn;
	}
	
	/**
	 * the greatest value, of all values inside the array, only work when all values are numeric
	 * @param array
	 * @return the greatest value
	 * @throws PageException 
	*/
	public static double max(Array array) throws PageException {
		if(array.getDimension()>1)
			throw new ExpressionException("can only get max value from 1 dimensional arrays");
		if(array.size()==0) return 0;
		
		double rtn=_toDoubleValue(array,1);
		int len=array.size();
		try {
			for(int i=2;i<=len;i++) {
				double v=_toDoubleValue(array,i);
				if(rtn<v)rtn=v;
				
			}
		} catch (PageException e) {
			throw new ExpressionException("exception while execute array operation: "+e.getMessage());
		}
		return rtn;
	}
	
	/**
	 * return index of given value in Array or -1
	 * @param arr
	 * @param value
	 * @return index of position in array
	 */
	public static int indexOf(String[] arr, String value) {
	    for(int i=0;i<arr.length;i++) {
	        if(arr[i].equals(value)) return i;
	    }
	    return -1;
	}
	
	/**
	 * return index of given value in Array or -1
	 * @param arr
	 * @param value
	 * @return index of position in array
	 */
	public static int indexOfIgnoreCase(String[] arr, String value) {
	    for(int i=0;i<arr.length;i++) {
	        if(arr[i].equalsIgnoreCase(value)) return i;
	    }
	    return -1;
	}
	
	

	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Boolean[] toReferenceType(boolean[] primArr) {
		Boolean[] refArr=new Boolean[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=Caster.toBoolean(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Byte[] toReferenceType(byte[] primArr) {
		Byte[] refArr=new Byte[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=new Byte(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Character[] toReferenceType(char[] primArr) {
		Character[] refArr=new Character[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=new Character(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Short[] toReferenceType(short[] primArr) {
		Short[] refArr=new Short[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=Short.valueOf(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Integer[] toReferenceType(int[] primArr) {
		Integer[] refArr=new Integer[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=Integer.valueOf(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Long[] toReferenceType(long[] primArr) {
		Long[] refArr=new Long[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=Long.valueOf(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Float[] toReferenceType(float[] primArr) {
		Float[] refArr=new Float[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=new Float(primArr[i]);
		return refArr;
	}
	
	/**
	 * convert a primitive array (value type) to Object Array (reference type).
	 * @param primArr value type Array 
	 * @return reference type Array
	 */
	public static Double[] toReferenceType(double[] primArr) {
		Double[] refArr=new Double[primArr.length];
		for(int i=0;i<primArr.length;i++)refArr[i]=new Double(primArr[i]);
		return refArr;
	}

	/**
	 * gets a value of a array at defined index
	 * @param o
	 * @param index
	 * @return value at index position
	 * @throws ArrayUtilException
	 */
	public static Object get(Object o,int index) throws ArrayUtilException {
	    o=get(o,index,null);
		if(o!=null) return o;
		throw new ArrayUtilException("Object is not a array, or index is invalid");
	}
	
	/**
	 * gets a value of a array at defined index
	 * @param o
	 * @param index
	 * @return value of the variable
	 */
	public static Object get(Object o,int index, Object defaultValue) {
	    if(index<0) return null;
	    if(o instanceof Object[])	{
		    Object[] arr=((Object[])o);
		    if(arr.length>index)return arr[index];
		}
	    else if(o instanceof boolean[])	{
	        boolean[] arr=((boolean[])o);
		    if(arr.length>index)return arr[index]?Boolean.TRUE:Boolean.FALSE;
		}
	    else if(o instanceof byte[])	{
	        byte[] arr=((byte[])o);
		    if(arr.length>index)return new Byte(arr[index]);
		}
	    else if(o instanceof char[])	{
	        char[] arr=((char[])o);
		    if(arr.length>index)return ""+(arr[index]);
		}
	    else if(o instanceof short[])	{
	        short[] arr=((short[])o);
		    if(arr.length>index)return Short.valueOf(arr[index]);
		}
	    else if(o instanceof int[])	{
	        int[] arr=((int[])o);
		    if(arr.length>index)return Integer.valueOf(arr[index]);
		}
	    else if(o instanceof long[])	{
	        long[] arr=((long[])o);
		    if(arr.length>index)return Long.valueOf(arr[index]);
		}
	    else if(o instanceof float[])	{
	        float[] arr=((float[])o);
		    if(arr.length>index)return new Float(arr[index]);
		}
	    else if(o instanceof double[])	{
	        double[] arr=((double[])o);
		    if(arr.length>index)return new Double(arr[index]);
		}
		return defaultValue;
	}

	/**
	 * sets a value to a array at defined index
	 * @param o
	 * @param index
	 * @param value
	 * @return value setted
	 * @throws ArrayUtilException
	 */
	public static Object set(Object o,int index, Object value) throws ArrayUtilException {
	    if(index<0) 
		    throw invalidIndex(index,0);
	    if(o instanceof Object[])	{
		    Object[] arr=((Object[])o);
		    if(arr.length>index)return arr[index]=value;
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof boolean[])	{
	        boolean[] arr=((boolean[])o);
	        if(arr.length>index) {
	            arr[index]=Caster.toBooleanValue(value,false);
	            return arr[index]?Boolean.TRUE:Boolean.FALSE;
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof byte[])	{
	        byte[] arr=((byte[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return new Byte(arr[index]=(byte)v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof short[])	{
	        short[] arr=((short[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return Short.valueOf(arr[index]=(short)v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof int[])	{
	        int[] arr=((int[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return Integer.valueOf(arr[index]=(int)v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof long[])	{
	        long[] arr=((long[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return Long.valueOf(arr[index]=(long)v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof float[])	{
	        float[] arr=((float[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return new Float(arr[index]=(float)v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof double[])	{
	        double[] arr=((double[])o);
	        if(arr.length>index) {
	            double v=Caster.toDoubleValue(value,Double.NaN);
	            if(Decision.isValid(v)) {
	                return new Double(arr[index]=v);
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
	    else if(o instanceof char[])	{
	        char[] arr=((char[])o);
	        if(arr.length>index) {
	            String str=Caster.toString(value,null);
	            if(str!=null && str.length()>0) {
	                char c=str.charAt(0);
	                arr[index]=c;
	                return str;
	            }
	        }
		    throw invalidIndex(index,arr.length);
		}
		throw new ArrayUtilException("Object ["+Caster.toClassName(o)+"] is not a Array");
	}

	
    private static ArrayUtilException invalidIndex(int index, int length) {
        return new ArrayUtilException("Invalid index ["+index+"] for native Array call, Array has a Size of "+length);
    }

    /**
	 * sets a value to a array at defined index
	 * @param o
	 * @param index
	 * @param value
	 * @return value setted
	 */
	public static Object setEL(Object o,int index, Object value) {
	    try {
            return set(o,index,value);
        } catch (ArrayUtilException e) {
            return null;
        }
	}

	public static boolean isEmpty(List list) {
		return list==null || list.isEmpty();
	}
	
	public static boolean isEmpty(Object[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(boolean[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(char[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(double[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(long[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(int[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(float[] array) {
		return array==null || array.length==0;
	}
	public static boolean isEmpty(byte[] array) {
		return array==null || array.length==0;
	}

	
	
	
	
	
	



	public static int size(Object[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(boolean[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(char[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(double[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(long[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(int[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(float[] array) {
		if(array==null) return 0; 
		return  array.length;
	}
	public static int size(byte[] array) {
		if(array==null) return 0; 
		return  array.length;
	}


	public static boolean[] toBooleanArray(Object obj) throws PageException {
		if(obj instanceof boolean[]) return (boolean[]) obj;
		
		Array arr = Caster.toArray(obj);
		boolean[] tarr=new boolean[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toBooleanValue(arr.getE(i+1));
		}
		return tarr;
	}

	public static byte[] toByteArray(Object obj) throws PageException {
		if(obj instanceof byte[]) return (byte[]) obj;
		
		Array arr = Caster.toArray(obj);
		byte[] tarr=new byte[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toByteValue(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static short[] toShortArray(Object obj) throws PageException {
		if(obj instanceof short[]) return (short[]) obj;
		
		Array arr = Caster.toArray(obj);
		short[] tarr=new short[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toShortValue(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static int[] toIntArray(Object obj) throws PageException {
		if(obj instanceof int[]) return (int[]) obj;
		
		Array arr = Caster.toArray(obj);
		int[] tarr=new int[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toIntValue(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static Object[] toNullArray(Object obj) throws PageException {
		Array arr = Caster.toArray(obj);
		Object[] tarr=new Object[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toNull(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static long[] toLongArray(Object obj) throws PageException {
		if(obj instanceof long[]) return (long[]) obj;
		
		Array arr = Caster.toArray(obj);
		long[] tarr=new long[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toLongValue(arr.getE(i+1));
		}
		return tarr;
	}

	public static float[] toFloatArray(Object obj) throws PageException {
		if(obj instanceof float[]) return (float[]) obj;
		
		Array arr = Caster.toArray(obj);
		float[] tarr=new float[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toFloatValue(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static double[] toDoubleArray(Object obj) throws PageException {
		if(obj instanceof double[]) return (double[]) obj;
		
		Array arr = Caster.toArray(obj);
		double[] tarr=new double[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toDoubleValue(arr.getE(i+1));
		}
		return tarr;
	}
	
	public static char[] toCharArray(Object obj) throws PageException {
		if(obj instanceof char[]) return (char[]) obj;
		
		Array arr = Caster.toArray(obj);
		char[] tarr=new char[arr.size()];
		for(int i=0;i<tarr.length;i++) {
			tarr[i]=Caster.toCharValue(arr.getE(i+1));
		}
		return tarr;
	}


	public static int arrayContainsIgnoreEmpty(Array arr, String value, boolean ignoreCase) {
		int count=0;
		int len=arr.size();
		
		for(int i=1;i<=len;i++) {
			String item=arr.get(i,"").toString();
			if(ignoreCase) {
				if(StringUtil.indexOfIgnoreCase(item,value)!=-1) return count;
			}
			else {
				if(item.indexOf(value)!=-1) return count;
			}
			count++;
		}
		return -1;
	}


	public static Object[] toReferenceType(Object obj) throws CasterException {
		Object[] ref = toReferenceType(obj,null);
		if(ref!=null) return ref;
		throw new CasterException(obj,Object[].class);
		
	}
	public static Object[] toReferenceType(Object obj,Object[] defaultValue) {
		if(obj instanceof Object[]) 			return (Object[])obj;
		else if(obj instanceof boolean[])		return toReferenceType((boolean[])obj);
		else if(obj instanceof byte[])		return toReferenceType((byte[])obj);
		else if(obj instanceof char[])		return toReferenceType((char[])obj);
		else if(obj instanceof short[])		return toReferenceType((short[])obj);
		else if(obj instanceof int[])			return toReferenceType((int[])obj);
		else if(obj instanceof long[])		return toReferenceType((long[])obj);
		else if(obj instanceof float[])		return toReferenceType((float[])obj);
		else if(obj instanceof double[])		return toReferenceType((double[])obj);
		return defaultValue;
	}


	public static Object[] clone(Object[] src, Object[] trg) {
		for(int i=0;i<src.length;i++){
			trg[i]=src[i];
		}
		return trg;
	}


	public static Object[] keys(Map map) {
		if(map==null) return new Object[0];
		Set set = map.keySet();
		if(set==null) return new Object[0];
		Object[] arr = set.toArray();
		if(arr==null) return new Object[0];
		return arr;
	}
	
	public static Object[] values(Map map) {
		if(map==null) return new Object[0];
		return map.values().toArray();
	}


	public static long sizeOf(List list) {
		ListIterator it = list.listIterator();
		long size=0;
		while(it.hasNext()){
			size+=SizeOf.size(it.next());
		}
		return size;
	}
	
	public static long sizeOf(Array array) {
		Iterator it = array.valueIterator();
		long size=0;
		while(it.hasNext()){
			size+=SizeOf.size(it.next());
		}
		return size;
	}


	/**
	 * creates a native array out of the input list, if all values are from the same type, this type is used for the array, otherwise object
	 * @param list
	 */
	public static Object[] toArray(List<?> list) {
		Iterator<?> it = list.iterator();
		Class clazz=null;
		while(it.hasNext()){
			Object v = it.next();
			if(v==null) continue;
			if(clazz==null) clazz=v.getClass();
			else if(clazz!=v.getClass()) return list.toArray();	
		}
		if(clazz==Object.class || clazz==null) 
			return list.toArray();
		
		Object arr = java.lang.reflect.Array.newInstance(clazz, list.size());
		return list.toArray((Object[]) arr);	
	}
	

	public static Comparator toComparator(PageContext pc,String sortType, String sortOrder, boolean localeSensitive) throws PageException {
		// check sortorder
		boolean isAsc=true;
		if(sortOrder.equalsIgnoreCase("asc"))isAsc=true;
		else if(sortOrder.equalsIgnoreCase("desc"))isAsc=false;
		else throw new ExpressionException("invalid sort order type ["+sortOrder+"], sort order types are [asc and desc]");
		
		// text
		if(sortType.equalsIgnoreCase("text")) {
			if(localeSensitive)return toCollator(pc,Collator.IDENTICAL);
			return new TextComparator(isAsc,false);
		}
		// text no case
		else if(sortType.equalsIgnoreCase("textnocase")) {
			if(localeSensitive)return toCollator(pc,Collator.TERTIARY);
			return new TextComparator(isAsc,true);
		}
		// numeric
		else if(sortType.equalsIgnoreCase("numeric")) {
			return new NumberComparator(isAsc);
		}
		else {
			throw new ExpressionException("invalid sort type ["+sortType+"], sort types are [text, textNoCase, numeric]");
		}	
	}
	private static Comparator toCollator(PageContext pc, int strength) {
		Collator c=Collator.getInstance(ThreadLocalPageContext.getLocale(pc));
		c.setStrength(strength);
		c.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
		return c;
	}


	public static <E> List<E> merge(E[] a1, E[] a2) {
		List<E> list=new ArrayList<E>();
		for(int i=0;i<a1.length;i++){
			list.add(a1[i]);
		}
		for(int i=0;i<a2.length;i++){
			list.add(a2[i]);
		}
		return list;
	}
}