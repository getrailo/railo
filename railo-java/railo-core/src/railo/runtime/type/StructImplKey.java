package railo.runtime.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import railo.commons.collection.MapFactory;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.StructSupport;

/**
 * CFML data type struct
 */
public final class StructImplKey extends StructSupport implements Struct {

	public static final int TYPE_WEAKED=0;
	public static final int TYPE_LINKED=1;
	public static final int TYPE_SYNC=2;
	public static final int TYPE_REGULAR=3;
	
	private Map<Collection.Key,Object> _map;
	//private static  int scount=0;
	//private static int kcount=0;
	
	/**
	 * default constructor
	 */
	public StructImplKey() {
		_map=new HashMap<Collection.Key,Object>();
	}
	
    /**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param doubleLinked
     */
    public StructImplKey(int type) {
    	if(type==TYPE_LINKED)		_map=new LinkedHashMap<Collection.Key,Object>();
    	else if(type==TYPE_WEAKED)	_map=new java.util.WeakHashMap<Collection.Key,Object>();
        else if(type==TYPE_SYNC)	_map=MapFactory.<Collection.Key,Object>getConcurrentMap();
        else 						_map=new HashMap<Collection.Key,Object>();
    }
    
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		Object rtn=_map.get(key);
		if(rtn!=null) return rtn;
		return defaultValue;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {//print.out("k:"+(kcount++));
		Object rtn=_map.get(key);
		if(rtn!=null) return rtn;
		throw invalidKey(key.getString());
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		_map.put(key,value);
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
        _map.put(key,value);
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return _map.size();
	}

	public Collection.Key[] keys() {//print.out("keys");
		Iterator<Key> it = keyIterator();
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
		Object obj= _map.remove(key);
		if(obj==null) throw new ExpressionException("can't remove key ["+key.getString()+"] from struct, key doesn't exist");
		return obj;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return _map.remove(key);
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		_map.clear();
	}
	 
	/**
	 *
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    Iterator it=_map.keySet().iterator();
		
		DumpTable table = new DumpTable("struct","#9999ff","#ccccff","#000000");
		table.setTitle("Struct");
		maxlevel--;
		int maxkeys=dp.getMaxKeys();
		int index=0;
		while(it.hasNext()) {
			Object key=it.next();
			if(DumpUtil.keyValid(dp, maxlevel,key.toString())){
				if(maxkeys<=index++)break;
				table.appendRow(1,new SimpleDumpData(key.toString()),DumpUtil.toDumpData(_map.get(key), pageContext,maxlevel,dp));
			}
		}
		return table;
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
		Struct sct=new StructImplKey();
		copy(this,sct,deepCopy);
		return sct;
	}
	
	
	public static void copy(Struct src,Struct trg,boolean deepCopy) {
		ThreadLocalDuplication.set(src, trg);
		try {
			Iterator<Entry<Key, Object>> it = src.entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()) {
				e = it.next();
				if(!deepCopy) trg.setEL(e.getKey(),e.getValue());
				else trg.setEL(e.getKey(),Duplicator.duplicate(e.getValue(),deepCopy));
			}
		}
		finally {
			//ThreadLocalDuplication.remove(src); removed "remove" to catch sisters and brothers
		}
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return _map.keySet().iterator();
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator valueIterator() {
		return _map.values().iterator();
	}

    /**
     * @see railo.runtime.type.Collection#_contains(java.lang.String)
     */
    public boolean containsKey(Collection.Key key) {
        return _map.containsKey(key);
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
		return _map.containsValue(value);
	}

	public java.util.Collection values() {
		return _map.values();
	}

}