package railo.runtime.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import railo.commons.collections.HashTable;
import railo.commons.collections.HashTableNotSync;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.StructSupport;

/**
 * cold fusion data type struct
 */
public class StructImpl extends StructSupport {

	
	private Map map;
	
	/**
	 * default constructor
	 */
	public StructImpl() {
		map=new HashTableNotSync();//asx
	}
	
    /**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param doubleLinked
     */
    public StructImpl(int type) {
    	if(type==TYPE_LINKED)		map=new LinkedHashMap();
    	else if(type==TYPE_WEAKED)	map=new java.util.WeakHashMap();
        else if(type==TYPE_SYNC)	map=new HashTable();
        else 						map=new HashMap();
    }
	
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		Object rtn=map.get(key);
		if(rtn!=null) return rtn;
		return defaultValue;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {//print.out("k:"+(kcount++));
		Object rtn=map.get(key);
		if(rtn!=null) return rtn;
		throw invalidKey(key);
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

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		
		Iterator it = map.keySet().iterator();
		Collection.Key[] keys = new Collection.Key[size()];
		int count=0;
		while(it.hasNext()) {
			keys[count++]=KeyImpl.toKey(it.next(), null);
		}
		
		return keys;
	}
	
	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		Iterator it = map.keySet().iterator();
		String[] keys = new String[size()];
		int count=0;
		while(it.hasNext()) {
			keys[count++]=Caster.toString(it.next(), "");
		}
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 */
	public Object remove(String key) throws PageException {
		Object obj= map.remove(KeyImpl.init(key));
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exists");
		return obj;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		Object obj= map.remove(key);
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exists");
		return obj;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return map.remove(key);
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * throw exception for invalid key
	 * @param key Invalid key
	 * @return returns a invalid key Exception
	 */
	protected ExpressionException invalidKey(Key key) {
		return new ExpressionException("key ["+key.getString()+"] doesn't exist in struct (keys:"+List.arrayToList(keysAsString(), ",")+")");
	}
	
	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImpl();
		copy(this,sct,deepCopy);
		return sct;
	}
	
	public static void copy(Struct src,Struct trg,boolean deepCopy) {
		Key[] keys = src.keys();
		Key key;
		for(int i=0;i<keys.length;i++) {
			key=keys[i];
			if(!deepCopy) trg.setEL(key,src.get(key,null));
			else trg.setEL(key,Duplicator.duplicate(src.get(key,null),deepCopy));
		}
		
	}
	
	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator valueIterator() {
		return values().iterator();
	}

    /**
     * @see railo.runtime.type.Collection#_contains(java.lang.String)
     */
    public boolean containsKey(Collection.Key key) {
        return map.containsKey(key);
    }
    
	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return map.values();
	}

	/**
	 * @return the map
	 */
	protected Map getMap() {
		return map;
	}
}