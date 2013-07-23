package railo.runtime.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import railo.commons.collection.MapFactory;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructUtil;
 
/**
 * CFML data type struct
 */
public final class StructImplString extends StructImpl implements Struct {

	public static final int TYPE_WEAKED=0;
	public static final int TYPE_LINKED=1;
	public static final int TYPE_SYNC=2;
	public static final int TYPE_REGULAR=3;
	
	private Map<Collection.Key,Object> map;
	//private static  int scount=0;
	//private static int kcount=0;
	
	/**
	 * default constructor
	 */
	public StructImplString() {
		map=new HashMap<Collection.Key, Object>();
	}
	
    /**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param doubleLinked
     */
    public StructImplString(int type) {
    	if(type==TYPE_LINKED)		map=new LinkedHashMap<Collection.Key, Object>();
    	else if(type==TYPE_WEAKED)	map=new java.util.WeakHashMap<Collection.Key, Object>();
        else if(type==TYPE_SYNC)	map=MapFactory.<Collection.Key,Object>getConcurrentMap();
        else 						map=new HashMap<Collection.Key, Object>();
    }
    
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		Object rtn=map.get(key.getLowerString());
		if(rtn!=null) return rtn;
		return defaultValue;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		Object rtn=map.get(key.getLowerString());
		if(rtn!=null) return rtn;
		throw invalidKey(key.getString());
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		map.put(key,value);
		return value;
	}
	
	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
        map.put(key,value);
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return map.size();
	}

	public Collection.Key[] keys() {
		Iterator<Key> it = map.keySet().iterator();
		Collection.Key[] keys = new Collection.Key[size()];
		int count=0;
		while(it.hasNext()) {
			keys[count++]=it.next();
		}
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		Object obj= map.remove(key.getLowerString());
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exist");
		return obj;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return map.remove(key.getLowerString());
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 *
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return StructUtil.toDumpTable(this, "struct", pageContext, maxlevel, dp);
		/*Iterator it=map.keySet().iterator();
		
		DumpTable table = new DumpTable("struct","#9999ff","#ccccff","#000000");
		table.setTitle("Struct");
		maxlevel--;
		while(it.hasNext()) {
			Object key=it.next();
			if(DumpUtil.keyValid(dp, maxlevel,key.toString()))
				table.appendRow(1,new SimpleDumpData(key.toString()),DumpUtil.toDumpData(map.get(key), pageContext,maxlevel,dp));
		}
		return table;*/
	}

	/**
	 * throw exception for invalid key
	 * @param key Invalid key
	 * @return returns an invalid key Exception
	 */
	protected ExpressionException invalidKey(String key) {
		return new ExpressionException("key ["+key+"] doesn't exist in struct");
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImplString();
		copy(this,sct,deepCopy);
		return sct;
	}
	
	public static void copy(Struct src,Struct trg,boolean deepCopy) {
		Iterator<Entry<Key, Object>> it = src.entryIterator();
		Entry<Key, Object> e;
		ThreadLocalDuplication.set(src, trg);
		try {
			while(it.hasNext()) {
				e = it.next();
				if(!deepCopy) trg.setEL(e.getKey(),e.getValue());
				else trg.setEL(e.getKey(),Duplicator.duplicate(e.getValue(),deepCopy));
			}
		}
		finally {
			// ThreadLocalDuplication.remove(src);  removed "remove" to catch sisters and brothers
		}
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return map.keySet().iterator();
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator valueIterator() {
		return map.values().iterator();
	}

    /**
     * @see railo.runtime.type.Collection#_contains(java.lang.String)
     */
    public boolean containsKey(Collection.Key key) {
        return map.containsKey(key.getLowerString());
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Built-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }

	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("can't cast Complex Object Type Struct to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	
	public java.util.Collection values() {
		return map.values();
	}

}