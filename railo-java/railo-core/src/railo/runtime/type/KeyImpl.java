package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Castable;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;

public class KeyImpl implements Collection.Key,Castable,Comparable,Sizeable,Externalizable {
	
	private String key;
	private String lcKey;
	private String ucKey;
	//private int hashcode;
	
	public KeyImpl() {
		// DO NOT USE, JUST FOR UNSERIALIZE
	}
	
	
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(key);
		out.writeObject(lcKey);
		out.writeObject(ucKey);
	}
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		key=(String) in.readObject();
		lcKey=((String) in.readObject()).intern();
		ucKey=(String) in.readObject();
		
	}
	
	
	
	private static Map<String,KeyImpl> keys = new HashMap<String, KeyImpl>();//HashTableNotSync();
	
	//public int index;
	//private static long count=0;
	
	protected KeyImpl(String key) {
		//this.index=count++;
		
		this.key=key;
		this.lcKey=StringUtil.toLowerCase(key).intern();
		keys.put(key,this);
		
	}
	/*public static void main(String[] args) {
		KeyImpl k1 = (KeyImpl) KeyImpl.init("an");
		KeyImpl k2 = (KeyImpl) KeyImpl.init("c0");
		KeyImpl k3 = (KeyImpl) KeyImpl.init("AN");

		print.o(k1.equals(k2));
		print.o(k1.equals(k3));
		
		long start=System.currentTimeMillis();
		for(int i=0;i<10000000;i++){
			k1.equals(k3);
		}
		print.o(System.currentTimeMillis()-start);


		start=System.currentTimeMillis();
		for(int i=0;i<10000000;i++){
			k1.equals(k3);
		}
		print.o(System.currentTimeMillis()-start);

		
		
		
		start=System.currentTimeMillis();
		for(int i=0;i<10000000;i++){
			k1.equals(k2);
		}
		print.o(System.currentTimeMillis()-start);


		start=System.currentTimeMillis();
		for(int i=0;i<10000000;i++){
			k1.equals(k2);
		}
		print.o(System.currentTimeMillis()-start);
		
	}*/

	
	/*private static int sunCRC32( byte[] ba )
    {
    // create a new CRC-calculating object
    final CRC32 crc = new CRC32();
    crc.update( ba );
    // crc.update( int ) processes only the low order 8-bits. It actually expects an unsigned byte.
    return ( int ) crc.getValue();
    }*/
	
	/*private static int digest( byte[] theTextToDigestAsBytes )
    {
    final Adler32 digester = new Adler32();
    digester.update( theTextToDigestAsBytes );
    // digester.update( int ) processes only the low order 8-bits. It actually expects an unsigned byte.
    // getValue produces a long to conform to the Checksum interface.
    // Actual result is 32 bits long not 64.
    return ( int ) digester.getValue();
    }*/
	
	
	/**
	 * for dynamic loading of key objects
	 * @param string
	 * @return
	 */
	
	public static Collection.Key init(String key) {
		return getInstance(key);
	}
	

	/**
	 * used for static iniatisation of a key object (used by compiler)
	 * @param string
	 * @return
	 */
	public synchronized static Collection.Key getInstance(String key) {
		Collection.Key k=  keys.get(key);
		if(k!=null) return k;
		return new KeyImpl(key);
		
		
		
	}
	
	/**
	 * @see railo.runtime.type.Collection.Key#charAt(int)
	 */
	public char charAt(int index) {
		return key.charAt(index);
	}

	/**
	 * @see railo.runtime.type.Collection.Key#lowerCharAt(int)
	 */
	public char lowerCharAt(int index) {
		return lcKey.charAt(index);
	}
	
	public char upperCharAt(int index) {
		return getUpperString().charAt(index);
	}

	/**
	 * @see railo.runtime.type.Collection.Key#getLowerString()
	 */
	public String getLowerString() {
		return lcKey;
	}
	
	public String getUpperString() {
		if(ucKey==null)ucKey=StringUtil.toUpperCase(key); // TODO
		return ucKey;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return key;
	}

	/**
	 * @see railo.runtime.type.Collection.Key#getString()
	 */
	public String getString() {
		return key;
	}

	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if(this==other) {
			return true;
		}
		//if(other==null) return false;
		/*if(hashcode!=other.hashCode()) {
			return false;
		}*/
		//return lcKey==(((KeyImpl)other).lcKey);
		
		if(other instanceof KeyImpl)	{
			//print.out("slow("+veryslow+":"+(slow++)+":"+fast+"):"+other);
			return lcKey==(((KeyImpl)other).lcKey);
		}
		else if(other instanceof String)	{
			//print.out("veryslow("+(veryslow++)+":"+fast+"):"+other);
			return key.equalsIgnoreCase((String)other);
		}
		else if(other instanceof Key)	{
			//print.out("veryslow("+(veryslow++)+":"+fast+"):"+other);
			return lcKey.equalsIgnoreCase(((Key)other).getLowerString());
		}
		
		return false;
	}

	/*public boolean equals(Key other) {
		if(this==other) return true;
		if(other==null) return false;
		if(hashcode!=other.hashCode()) {
			return false;
		}
		//return lcKey==(((KeyImpl)other).lcKey);
		
		if(other instanceof KeyImpl)	{
			//print.out("slow("+veryslow+":"+(slow++)+":"+fast+"):"+other);
			return lcKey==(((KeyImpl)other).lcKey);
		}
		return lcKey.equalsIgnoreCase(((Key)other).getLowerString());
	}*/
	
	/**
	 * @see railo.runtime.type.Collection.Key#equalsIgnoreCase(railo.runtime.type.Collection.Key)
	 */
	public boolean equalsIgnoreCase(Key key) {
		return equals(key);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
	
		return lcKey.hashCode();
	}

	/**
	 * @see railo.runtime.type.Collection.Key#getId()
	 */
	public int getId() {
		return hashCode();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBooleanValue(key);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return Caster.toBoolean(key,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return Caster.toDatetime(key,null);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return DateCaster.toDateAdvanced(key,true,null,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(key);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	return Caster.toDoubleValue(key,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return key;
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return key;
	}

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(key, b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(key, (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(key, d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(key, str);
	}
	

	public int compareTo(Object o) {
		try {
			return Operator.compare(key, o);
		} catch (PageException e) {
			ClassCastException cce = new ClassCastException(e.getMessage());
			cce.setStackTrace(e.getStackTrace());
			throw cce;
			
		}
	}
	

	public static Array toUpperCaseArray(Key[] keys) {
		ArrayImpl arr=new ArrayImpl();
		for(int i=0;i<keys.length;i++) {
			arr._append(((KeyImpl)keys[i]).getUpperString());
		}
		return arr;
	}
	public static Array toLowerCaseArray(Key[] keys) {
		ArrayImpl arr=new ArrayImpl();
		for(int i=0;i<keys.length;i++) {
			arr._append(((KeyImpl)keys[i]).getLowerString());
		}
		return arr;
	}
	
	public static Array toArray(Key[] keys) {
		ArrayImpl arr=new ArrayImpl();
		for(int i=0;i<keys.length;i++) {
			arr._append(((KeyImpl)keys[i]).getString());
		}
		return arr;
	}

	public static String toUpperCaseList(Key[] array, String delimeter) {
		if(array.length==0) return "";
		StringBuffer sb=new StringBuffer(((KeyImpl)array[0]).getUpperString());
		
		if(delimeter.length()==1) {
			char c=delimeter.charAt(0);
			for(int i=1;i<array.length;i++) {
				sb.append(c);
				sb.append(((KeyImpl)array[i]).getUpperString());
			}
		}
		else {
			for(int i=1;i<array.length;i++) {
				sb.append(delimeter);
				sb.append(((KeyImpl)array[i]).getUpperString());
			}
		}
		return sb.toString();
	}

	public static String toList(Key[] array, String delimeter) {
		if(array.length==0) return "";
		StringBuffer sb=new StringBuffer(((KeyImpl)array[0]).getString());
		
		if(delimeter.length()==1) {
			char c=delimeter.charAt(0);
			for(int i=1;i<array.length;i++) {
				sb.append(c);
				sb.append(((KeyImpl)array[i]).getString());
			}
		}
		else {
			for(int i=1;i<array.length;i++) {
				sb.append(delimeter);
				sb.append(((KeyImpl)array[i]).getString());
			}
		}
		return sb.toString();
	}

	public static String toLowerCaseList(Key[] array, String delimeter) {
		if(array.length==0) return "";
		StringBuffer sb=new StringBuffer(((KeyImpl)array[0]).getLowerString());
		
		if(delimeter.length()==1) {
			char c=delimeter.charAt(0);
			for(int i=1;i<array.length;i++) {
				sb.append(c);
				sb.append(((KeyImpl)array[i]).getLowerString());
			}
		}
		else {
			for(int i=1;i<array.length;i++) {
				sb.append(delimeter);
				sb.append(((KeyImpl)array[i]).getLowerString());
			}
		}
		return sb.toString();
	}

	public static Collection.Key toKey(Object obj, Collection.Key defaultValue) {
		if(obj instanceof Collection.Key) return (Collection.Key) obj;
		String str = Caster.toString(obj,null);
		if(str==null) return defaultValue;
		return init(str);
	}

	public static Collection.Key toKey(Object obj) throws CasterException {
		if(obj instanceof Collection.Key) return (Collection.Key) obj;
		String str = Caster.toString(obj,null);
		if(str==null) throw new CasterException(obj,Collection.Key.class);
		return init(str);
	}


	public long sizeOf() {
		return 
		SizeOf.size(this.key)+
		SizeOf.size(this.lcKey)+
		SizeOf.size(this.ucKey)+
		SizeOf.REF_SIZE;
	}
	
}
