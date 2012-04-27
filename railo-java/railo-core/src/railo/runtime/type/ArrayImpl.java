package railo.runtime.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.comparator.NumberComparator;
import railo.runtime.type.comparator.TextComparator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.ArraySupport;
import railo.runtime.type.util.ListIteratorImpl;



/**
 * cold fusion array object
 */
public class ArrayImpl extends ArraySupport implements Sizeable {
	

	private Object[] arr;
	private int dimension=1;
	private final int cap=32;
	private int size=0;
	private int offset=0;
	private int offCount=0;
	
	/**
	 * constructor with definiton of the dimension
	 * @param dimension dimension goes from one to 3
	 * @throws ExpressionException
	 */
	public ArrayImpl(int dimension) throws ExpressionException {
		if(dimension>3 || dimension<1)
			throw new ExpressionException("Array Dimension must be between 1 and 3");
		this.dimension=dimension;
		arr=new Object[offset+cap];
	}
	
	/**
	 * constructor with default dimesnion (1)
	 */
	public ArrayImpl() {
		arr=new Object[offset+cap];
	}
	
	/**
	 * constructor with to data to fill
	 * @param objects Objects array data to fill
	 */
	public ArrayImpl(Object[] objects) {
		arr=new Object[objects.length];
		for(int i=0;i<arr.length;i++){
			arr[i]=objects[i];
		}
		size=arr.length;
		offset=0;
	}
	
	/**
	 * return dimension of the array
	 * @return dimension of the array
	 */
	public int getDimension() {
		return dimension;
	}
	
	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws ExpressionException {
        return getE(Caster.toIntValue(key));
	}	

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws ExpressionException {
        return getE(Caster.toIntValue(key.getString()));
	}	
	
	/**
	 *
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		double index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return defaultValue;
	    return get((int)index,defaultValue);
	}	
	
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		double index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return defaultValue;
	    return get((int)index,defaultValue);
	}		

	/**
	 *
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public synchronized Object get(int key, Object defaultValue) {
		if(key>size || key<1) {
			if(dimension>1) {
				ArrayImpl ai = new ArrayImpl();
				ai.dimension=dimension-1;
				return setEL(key,ai);
			}
			return defaultValue;
		}
		Object o=arr[(offset+key)-1];
		
		if(o==null) {
			if(dimension>1) {
				ArrayImpl ai = new ArrayImpl();
				ai.dimension=dimension-1;
				return setEL(key,ai);
			}
			return defaultValue;
		}
		return o;
	}
	
	/**
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public synchronized Object getE(int key) throws ExpressionException {
		if(key<1) {
			throw invalidPosition(key);
		}
		else if(key>size) {
			if(dimension>1)return setE(key,new ArrayImpl(dimension-1));
			throw invalidPosition(key);
		}
		
		Object o=arr[(offset+key)-1];
		
		if(o==null) {
			if(dimension>1) return setE(key,new ArrayImpl(dimension-1));
			throw invalidPosition(key);
		}
		return o;
	}
	
	/**
	 * Exception method if key doesn't exist at given position
	 * @param pos
	 * @return exception
	 */
	private ExpressionException invalidPosition(int pos) {
		return new ExpressionException("Element at position ["+pos+"] doesn't exist in array");
	}
	
	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		try {
			return setEL(Caster.toIntValue(key), value);
		} catch (ExpressionException e) {
			return null;
		}
	}
	
	/**
	 * @see railo.runtime.type.Collection#_setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		try {
			return setEL(Caster.toIntValue(key.getString()), value);
		} catch (ExpressionException e) {
			return null;
		}
	}
	
	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws ExpressionException {
		return setE(Caster.toIntValue(key),value);
	}
	
	/**
	 * @see railo.runtime.type.Collection#_set(java.lang.String, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws ExpressionException {
		return setE(Caster.toIntValue(key.getString()),value);
	}

	/**
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public synchronized Object setEL(int key, Object value) {
		if(offset+key>arr.length)enlargeCapacity(key);
		if(key>size)size=key;
		arr[(offset+key)-1]=checkValueEL(value);
		return value;
	}
	
	/**
	 * set value at defined position
	 * @param key 
	 * @param value
	 * @return defined value
	 * @throws ExpressionException
	 */
	public synchronized Object setE(int key, Object value) throws ExpressionException {
		if(key<1)throw new ExpressionException("Invalid index ["+key+"] for array. Index must be a positive integer (1, 2, 3, ...)");
		if(offset+key>arr.length)enlargeCapacity(key);
		if(key>size)size=key;
		arr[(offset+key)-1]=checkValue(value);
		return value;		
	}	
	
	/**
     * !!! all methods that use this method must be sync
	 * enlarge the inner array to given size
	 * @param key min size of the array
	 */
	private void enlargeCapacity(int key) {
		int diff=offCount-offset;
		int newSize=arr.length;
		if(newSize<1) newSize=1;
		while(newSize<key+offset+diff) {
			newSize*=2;
		}
		if(newSize>arr.length) {
			Object[] na=new Object[newSize];
			for(int i=offset;i<offset+size;i++) {
				na[i+diff]=arr[i];
			}
			arr=na;
			offset+=diff;
		}
	}
	
	/**
	 * !!! all methods that use this method must be sync
     * enlarge the offset if 0
	 */
	private void enlargeOffset() {
		if(offset==0) {
			offCount=offCount==0?1:offCount*2;
			offset=offCount;
			Object[] narr=new Object[arr.length+offset];
			for(int i=0;i<size;i++) {
				narr[offset+i]=arr[i];
			}
			arr=narr;
		}
	}

	/**
	 * !!! all methods that use this method must be sync
     * check if value is valid to insert to array (to a multidimesnional array only array with one smaller dimension can be inserted)
	 * @param value value to check
	 * @return checked value
	 * @throws ExpressionException
	*/
	private Object checkValue(Object value) throws ExpressionException {
		// is a 1 > Array
		if(dimension>1)	{
			if(value instanceof Array)	{
				if(((Array)value).getDimension()!=dimension-1)
					throw new ExpressionException("You can only Append an Array with "+(dimension-1)+" Dimension","aray has wron dimension, now is "+(((Array)value).getDimension())+ " but it must be "+(dimension-1));
			}
			else 
				throw new ExpressionException("You can only Append an Array with "+(dimension-1)+" Dimension","now is a object of type "+Caster.toClassName(value));
		}
		return value;
	}
	
	/**
	 * !!! all methods that use this method must be sync
     * check if value is valid to insert to array (to a multidimesnional array only array with one smaller dimension can be inserted), if value is invalid return null;
	 * @param value value to check
	 * @return checked value
	*/
	private Object checkValueEL(Object value) {
		// is a 1 > Array
		if(dimension>1)	{
			if(value instanceof Array)	{
				if(((Array)value).getDimension()!=dimension-1)
					return null;
			}
			else 
				return null;
		}
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return size;
	}
	
	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public synchronized Collection.Key[] keys() {
		
		ArrayList lst=new ArrayList();
		int count=0;
		for(int i=offset;i<offset+size;i++) {
			Object o=arr[i];
			count++;
			if(o!=null) lst.add(KeyImpl.getInstance(count+""));
		}
		return (Collection.Key[]) lst.toArray(new Collection.Key[lst.size()]);
	}
	
	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public synchronized String[] keysAsString() {
		
		ArrayList lst=new ArrayList();
		int count=0;
		for(int i=offset;i<offset+size;i++) {
			Object o=arr[i];
			count++;
			if(o!=null) lst.add(count+"");
		}
		return (String[]) lst.toArray(new String[lst.size()]);
	}
	
	/**
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public synchronized int[] intKeys() {
		ArrayList lst=new ArrayList();		
		int count=0;
		for(int i=offset;i<offset+size;i++) {
			Object o=arr[i];
			count++;
			if(o!=null) lst.add(Integer.valueOf(count));
		}

		int[] ints=new int[lst.size()];
		
		for(int i=0;i<ints.length;i++){
			ints[i]=((Integer)lst.get(i)).intValue();
		}
		return ints;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws ExpressionException {
		return removeE(Caster.toIntValue(key.getString()));
	}
	
	public Object removeEL(Collection.Key key) {
		return removeEL(Caster.toIntValue(key.getString(),-1));
	}
	
	/**
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public synchronized Object removeE(int key) throws ExpressionException {
		if(key>size || key<1) throw invalidPosition(key);
		Object obj=get(key,null);
		for(int i=(offset+key)-1;i<(offset+size)-1;i++) {
			arr[i]=arr[i+1];
		}
		size--;
		return obj;
	}
	
	/**
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public synchronized Object removeEL(int key) {
	    if(key>size || key<1) return null;
		Object obj=get(key,null);
	    
		for(int i=(offset+key)-1;i<(offset+size)-1;i++) {
			arr[i]=arr[i+1];
		}
		size--;
		return obj;
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public synchronized void clear() {
	    if(size()>0) {
			arr=new Object[cap];
			size=0;
			offCount=1;
			offset=0;
	    }
	}
	
	/**
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
	public synchronized boolean insert(int key, Object value) throws ExpressionException {
		if(key<1 || key>size+1) {
			throw new ExpressionException("can't insert value to array at position "+key+", array goes from 1 to "+size());
		}
		// Left
		if((size/2)>=key) {
			enlargeOffset();
			for(int i=offset;i<(offset+key)-1;i++) {
				arr[i-1]=arr[i];
			}
			offset--;
			arr[(offset+key)-1]=checkValue(value);
			size++;
		}
		// Right
		else {
			if((offset+key)>arr.length || size+offset>=arr.length)enlargeCapacity(arr.length+2);
			for(int i=size+offset;i>=key+offset;i--) {
				arr[i]=arr[i-1];
			}
			arr[(offset+key)-1]=checkValue(value);
			size++;
			
		}
		return true;
	}

	/**
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
    public synchronized Object append(Object o) throws ExpressionException {
        if(offset+size+1>arr.length)enlargeCapacity(size+1);
        arr[offset+size]=checkValue(o);
        size++;
        return o;
    }
    
	/**
	 * append a new value to the end of the array
	 * @param o value to insert
	 * @return inserted value
	 */
    public synchronized Object appendEL(Object o) {
        
        if(offset+size+1>arr.length)enlargeCapacity(size+1);
        arr[offset+size]=o;
        size++;
        return o;
    }

	/**
	 * append a new value to the end of the array
	 * @param str value to insert
	 * @return inserted value
	 */
	public synchronized String _append(String str) {
		if(offset+size+1>arr.length)enlargeCapacity(size+1);
		arr[offset+size]=str;
		size++;
		return str;
	}
	
	/**
	 * add a new value to the begin of the array
	 * @param o value to insert
	 * @return inserted value
	 * @throws ExpressionException
	 */
	public Object prepend(Object o) throws ExpressionException {
		insert(1,o);
		return o;
	}
	
	/**
	 * resize array to defined size
	 * @param to new minimum size of the array
	 */
	public synchronized void resize(int to) {
		if(to>size) {
			enlargeCapacity(to);
			size=to;
		}
	}

	/**
	 * sort values of a array
	 * @param sortType search type (text,textnocase,numeric)
	 * @param sortOrder (asc,desc)
	 * @throws PageException
	 */
	public synchronized void sort(String sortType, String sortOrder) throws PageException {
		if(getDimension()>1)
			throw new ExpressionException("only 1 dimensional arrays can be sorted");
		
		// check sortorder
		boolean isAsc=true;
		PageException ee=null;
		if(sortOrder.equalsIgnoreCase("asc"))isAsc=true;
		else if(sortOrder.equalsIgnoreCase("desc"))isAsc=false;
		else throw new ExpressionException("invalid sort order type ["+sortOrder+"], sort order types are [asc and desc]");
		
		// text
		if(sortType.equalsIgnoreCase("text")) {
			TextComparator comp=new TextComparator(isAsc,false);
			//Collections.sort(list,comp);
			Arrays.sort(arr,offset,offset+size,comp);
			ee=comp.getPageException();
		}
		// text no case
		else if(sortType.equalsIgnoreCase("textnocase")) {
			TextComparator comp=new TextComparator(isAsc,true);
			//Collections.sort(list,comp);
			Arrays.sort(arr,offset,offset+size,comp);
			ee=comp.getPageException();
			
		}
		// numeric
		else if(sortType.equalsIgnoreCase("numeric")) {
			NumberComparator comp=new NumberComparator(isAsc);
			//Collections.sort(list,comp);
			Arrays.sort(arr,offset,offset+size,comp);
			ee=comp.getPageException();
			
		}
		else {
			throw new ExpressionException("invalid sort type ["+sortType+"], sort types are [text, textNoCase, numeric]");
		}
		if(ee!=null) {
			throw new ExpressionException("can only sort arrays with simple values",ee.getMessage());
		}
			
	}
	
	
	public synchronized void sort(Comparator comp) throws PageException {
		if(getDimension()>1)
			throw new ExpressionException("only 1 dimensional arrays can be sorted");
		Arrays.sort(arr,offset,offset+size,comp);	
	}
	
	/**
	 * @return return arra as native (Java) Object Array
	 */
	public synchronized Object[] toArray() {
		Object[] rtn=new Object[size];
		int count=0;
		for(int i=offset;i<offset+size;i++) {
			rtn[count++]=arr[i];
		}
		
		return rtn;
	}

	/**
	 * @return return array as ArrayList
	 */
	/*public synchronized ArrayList toArrayList() {
		ArrayList al=new ArrayList();
		for(int i=offset;i<offset+size;i++) {
			al.add(arr[i]);
		}
		return al;
	}*/
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = new DumpTablePro("array","#99cc33","#ccff33","#000000");
		table.setTitle("Array");
		//if(size()>10)table.setComment("Size:"+size());
		int length=size();
		maxlevel--;
		for(int i=1;i<=length;i++) {
			Object o=null;
			try {
				o = getE(i);
			} 
			catch (Exception e) {}
			table.appendRow(1,new SimpleDumpData(i),DumpUtil.toDumpData(o, pageContext,maxlevel,dp));
		}
		return table;
	}
	
	/**
	 * return code print of the array as plain text
	 * @return content as string
	*/
	public synchronized String toPlain() {
		
		StringBuffer sb=new StringBuffer();
		int length=size();
		for(int i=1;i<=length;i++) {
			sb.append(i);
			sb.append(": ");
			sb.append(get(i-1,null));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public synchronized Collection duplicate(boolean deepCopy) {
		return duplicate(new ArrayImpl(),deepCopy);
	}
	
	
	
	protected Collection duplicate(ArrayImpl arr,boolean deepCopy) {
		arr.dimension=dimension;
		String[] keys=this.keysAsString();
		ThreadLocalDuplication.set(this, arr);
		try {
			for(int i=0;i<keys.length;i++) {
				String key=keys[i];
				if(deepCopy)arr.set(key,Duplicator.duplicate(this.get(key,null),deepCopy));
				else arr.set(key,this.get(key,null));
			}
		}
		catch (ExpressionException e) {}
		finally{
			ThreadLocalDuplication.remove(this);
		}
		
		return arr;
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}

	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return new ListIteratorImpl(this,0);
	}

	/**
	 * @see railo.runtime.engine.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(arr)
		+SizeOf.size(dimension)
		+SizeOf.size(cap)
		+SizeOf.size(size)
		+SizeOf.size(offset)
		+SizeOf.size(offCount)
		+SizeOf.REF_SIZE;
	}
	
	
	
	
}