package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

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
import railo.runtime.type.util.KeyConstants;

public class KeyImpl implements Collection.Key,Castable,Comparable,Sizeable,Externalizable {
	//public static HashMap<String,RefIntegerImpl> log=new HashMap<String,RefIntegerImpl>();
	

	public static final Collection.Key ACTION=KeyConstants._action;
	public static final Key CFID=KeyConstants._cfid;
	public static final Key CFTOKEN=KeyConstants._cftoken;
	public static final Key DETAIL=KeyConstants._detail;
	public static final Key ID=KeyConstants._id;
	public static final Key RETURN_FORMAT =KeyConstants._returnFormat;
	public static final Key NAME=KeyConstants._name;
	public static final Key NAME_UC=KeyConstants._NAME;
	public static final Key DATA=KeyConstants._data;
	public static final Key S3=KeyConstants._s3;
	public static final Key SIZE=KeyConstants._size;
	public static final Key SUPER=KeyConstants._super;
	public static final Key SUPER_UC=KeyConstants._SUPER;
	public static final Key TEMPLATE=KeyConstants._template;
	public static final Key THIS=KeyConstants._this;
	public static final Key THIS_UC=KeyConstants._THIS;
	public static final Key TIME=KeyConstants._time;
	public static final Key TYPE=KeyConstants._type;
	public static final Key HINT = 		KeyConstants._hint;
	public static final Key REQUIRED = 	KeyConstants._required;
	public static final Key DEFAULT = 		KeyConstants._default;
	public static final Key DATA_SOURCE = 		KeyConstants._datasource;
	

	public static final Key ARGUMENT_COLLECTION = KeyConstants._argumentCollection;
	public static final Key ACCESS = 		KeyConstants._access;
	public static final Key OUTPUT = 		KeyConstants._output;
	public static final Key RETURN_TYPE = 	KeyConstants._returntype;
	public static final Key DESCRIPTION = 	KeyConstants._description;
	public static final Key OWNER = 	KeyConstants._owner;
	public static final Key DISPLAY_NAME = KeyConstants._displayname;
	public static final Key PARAMETERS = 	KeyConstants._parameters;

	public static final Key VALUE = KeyConstants._value;
	public static final Key PATH = KeyConstants._path;
	public static final Key ENTRY = KeyConstants._entry;
	public static final Key KEY = KeyConstants._key;
	public static final Key LINE = KeyConstants._line;
	public static final Key COLUMN = KeyConstants._column;
	public static final Key ARGUMENTS = KeyConstants._arguments;
	public static final Key STATUS = KeyConstants._status;
	public static final Key THREAD = KeyConstants._thread;
	public static final Key VARIABLES = KeyConstants._variables;
	public static final Key FIELD_NAMES = KeyConstants._fieldnames;
	public static final Key LOCAL = KeyConstants._local;
	public static final Key SERVER = KeyConstants._server;
	public static final Key EXCEPTIONS = KeyConstants._exceptions;
	public static final Key BODY = KeyConstants._body;
	public static final Key TITLE = KeyConstants._title;
	public static final Key URL = KeyConstants._url;
	public static final Key LABEL = KeyConstants._label;
	public static final Key TOTAL = KeyConstants._total;

	public static final Key CLOSURE = KeyConstants._closure;
	public static final Key FUNCTION = KeyConstants._function;

	//private boolean intern;
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
		//out.writeBoolean(intern);
	}
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		key=(String) in.readObject();
		lcKey=((String) in.readObject());
		ucKey=(String) in.readObject();
		//intern= in.readBoolean();
		//if(intern)lcKey=lcKey.intern();
	}
	
	
	protected KeyImpl(String key) {
		this.key=key;
		this.lcKey=key.toLowerCase();
		//RefIntegerImpl count=log.get(key);
		//if(count!=null) count.plus(1);
		//else log.put(key, new RefIntegerImpl(1));
		
	}
	
	/*public static void print(){
		//Iterator<Entry<String, RefIntegerImpl>> it = log.entrySet().iterator();
		String[] keys = log.keySet().toArray(new String[log.size()]);
		Arrays.sort(keys);
		int total=0,big=0;
		for(int i=0;i<keys.length;i++){
			RefIntegerImpl value = log.get(keys[i]);
			if(value.toInt()>10 && keys[i].length()<=10 && keys[i].indexOf('.')==-1 && keys[i].indexOf('-')==-1) {
				print.e("public static final Key "+keys[i]+"=KeyImpl.intern(\""+keys[i]+"\");");
				big++;
			}
			total++;
		}
		print.e("total:"+total);
		print.e("big:"+big);
	}*/
	
	
	
	/*private KeyImpl(String key, boolean intern) {
		this.key=key;
		this.lcKey=intern?key.toLowerCase():key.toLowerCase();
		this.intern=intern;
	}*/	
	
	/**
	 * for dynamic loading of key objects
	 * @param string
	 * @return
	 */
	public static Collection.Key init(String key) {
		return new KeyImpl(key);
	}
	

	/**
	 * used for static iniatisation of a key object (used by compiler)
	 * @param string
	 * @return
	 */
	public synchronized static Collection.Key getInstance(String key) {
		return new KeyImpl(key);
	}
	

	public synchronized static Collection.Key intern(String key) {
		/*Long l= keys.get(key);
		String st=ExceptionUtil.getStacktrace(new Exception("Stack trace"), false);
		String[] arr = railo.runtime.type.List.listToStringArray(st,'\n');
		if(l!=null) {
			if(arr[2].indexOf("/Users/mic/")==-1)
				keys.put(key, l.longValue()+1);
		}
		else {

			if(arr[2].indexOf("/Users/mic/")==-1)
				keys.put(key, 1L);
		}*/
		return new KeyImpl(key);
	}
	
	/*public static void dump(){
		Iterator<Entry<String, Long>> it = keys.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Long> e = it.next();
			if(e.getValue()>1)print.o(e.getKey()+":"+e.getValue());
		}
	}*/
	
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
		if(ucKey==null)ucKey=StringUtil.toUpperCase(key);
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
	public boolean equals(Object other) {//call++;
		
		
		if(other instanceof KeyImpl)	{
			/*if(intern && ((KeyImpl)other).intern) {//eq++;
				return lcKey==(((KeyImpl)other).lcKey);
			}*/
			return lcKey.equals((((KeyImpl)other).lcKey));
			
		}
		if(other instanceof String)	{
			return key.equalsIgnoreCase((String)other);
		}
		if(other instanceof Key)	{
			return lcKey.equalsIgnoreCase(((Key)other).getLowerString());
		}
		return false;
	}

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
