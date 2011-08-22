package railo.runtime.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.collections.HashTable;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.StructSupport;

/**
 * cold fusion data type struct
 */
public class StructImpl extends StructSupport {
	private static final long serialVersionUID = 1421746759512286393L;

	
	
	private Map<Collection.Key,Object> map;
	
	/**
	 * default constructor
	 */
	public StructImpl() {
		this(TYPE_REGULAR);//asx
	}
	
    /**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param doubleLinked
     */
    public StructImpl(int type) {
    	if(type==TYPE_LINKED)		map=new LinkedHashMap<Collection.Key,Object>();
    	else if(type==TYPE_WEAKED)	map=new java.util.WeakHashMap<Collection.Key,Object>(); 
    	else if(type==TYPE_SOFT)	map=new ReferenceMap();
        else if(type==TYPE_SYNC)	map=Collections.synchronizedMap(new HashMap<Collection.Key,Object>());
        else 						map=new HashMap<Collection.Key,Object>();
    }
    
    private int getType(){
    	if(map instanceof LinkedHashMap) return TYPE_LINKED;
    	if(map instanceof java.util.WeakHashMap) return TYPE_WEAKED;
    	if(map instanceof ReferenceMap) return TYPE_SOFT;
    	if(map instanceof HashTable) return TYPE_SYNC;
    	return TYPE_REGULAR;
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
	
	public Collection.Key[] keys() {
		try	{
			//Collection.Key[] keys = new Collection.Key[size()];
			return map.keySet().toArray(new Key[map.size()]);
			/*Iterator<Key> it = map.keySet().iterator();
			int count=0;
			while(it.hasNext() && keys.length>count) {
				keys[count++]=KeyImpl.toKey(it.next(), null);
			}
			return keys;*/
		}
		catch(Throwable t) {
			Map<Key, Object> old = map;
			try{	
				map=Collections.synchronizedMap(map);
				Set<Key> set = map.keySet();
				Collection.Key[] keys = new Collection.Key[size()];
				synchronized(map){
					Iterator<Key> it = set.iterator();
					int count=0;
					while(it.hasNext() && keys.length>count) {
						keys[count++]=KeyImpl.toKey(it.next(), null);
					}
					return keys;
				}
			}
			finally {
				map=old;
			}
		}
	}

	
	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		try	{
			//if(true)throw new RuntimeException("");
			String[] keys = new String[size()];
			Iterator<Key> it = map.keySet().iterator();
			int count=0;
			while(it.hasNext() && keys.length>count) {
				keys[count++]=Caster.toString(it.next(), "");
			}
			return keys;
		}
		catch(Throwable t) {
			Map<Key, Object> old = map;
			try{	
				map=Collections.synchronizedMap(map);
				Object[] arr = map.keySet().toArray();
				String[] keys = new String[arr.length];
				for(int i=0;i<arr.length;i++){
					keys[i]=Caster.toString(arr[i], "");
				}	
				return keys;
			}
			finally {
				map=old;
			}
		}
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
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImpl(getType());
		copy(this,sct,deepCopy);
		return sct;
	}
	
	public static void copy(Struct src,Struct trg,boolean deepCopy) {
		ThreadLocalDuplication.set(src,trg);
		try{
			Key[] keys = src.keys();
			Key key;
			for(int i=0;i<keys.length;i++) {
				key=keys[i];
				if(!deepCopy) trg.setEL(key,src.get(key,null));
				else trg.setEL(key,Duplicator.duplicate(src.get(key,null),deepCopy));
			}
		}
		finally {
			ThreadLocalDuplication.remove(src);
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
	protected Map<Collection.Key,Object> getMap() {
		return map;
	}
}