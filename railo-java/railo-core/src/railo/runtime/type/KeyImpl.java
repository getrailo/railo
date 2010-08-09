package railo.runtime.type;

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

public class KeyImpl implements Collection.Key,Castable,Comparable,Sizeable {
	
	private String key;
	private String lcKey;
	private String ucKey;
	private int hashcode;
	
	
	
	private static Map<String,KeyImpl> keys=new HashMap<String,KeyImpl>();
	
	//public int index;
	//private static long count=0;
	
	protected KeyImpl(String key) {
		//this.index=count++;
		
		this.key=key;
		this.lcKey=StringUtil.toLowerCase(key);
		hashcode = lcKey.hashCode();//&0x7FFFFFFF;
		keys.put(key,this);
		//print.out(key+":"+(count++));
	}

	
	/**
	 * for dynamic loading of key objects
	 * @param string
	 * @return
	 */
	
	public static Collection.Key init(String key) {
		Collection.Key k= (Key) keys.get(key);
		if(k!=null) return k;
		return new KeyImpl(key);
	}
	

	/**
	 * used for static iniatisation of a key object (used by compiler)
	 * @param string
	 * @return
	 */
	public static Collection.Key getInstance(String key) {
		Collection.Key k= (Key) keys.get(key);
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
		if(other!=null)return hashcode==other.hashCode();
		if(other instanceof String)	{
			return key.equalsIgnoreCase((String)other);
		}
		/*if(other instanceof KeyImpl)	{
			//print.out("slow("+(slow++)+"):"+other);
			return hashcode==(((KeyImpl)other).hashcode);
		}
		if(other instanceof String)	{
			print.out("veryslow("+(slow++)+"):"+other);
			return key.equalsIgnoreCase((String)other);
		}*/
		return false;
	}

	public boolean equals(Key key) {
		if(this==key) return true;
		if(key!=null)return hashcode==key.hashCode();
		return false;
	}
	
	/**
	 * @see railo.runtime.type.Collection.Key#equalsIgnoreCase(railo.runtime.type.Collection.Key)
	 */
	public boolean equalsIgnoreCase(Key key) {
		if(this==key) return true;
		if(key!=null)return hashcode==key.hashCode();
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
	
		//print.err("count("+(test++)+"):"+key);
		//print.dumpStack();
		return hashcode;
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
		return SizeOf.size(this.hashcode)+
		SizeOf.size(this.key)+
		SizeOf.size(this.lcKey)+
		SizeOf.size(this.ucKey);
	}
}
