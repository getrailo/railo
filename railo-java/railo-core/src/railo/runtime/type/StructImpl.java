package railo.runtime.type;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.util.mod.HashMapPro;
import railo.commons.util.mod.LinkedHashMapPro;
import railo.commons.util.mod.MapPro;
import railo.commons.util.mod.MapProWrapper;
import railo.commons.util.mod.SyncMap;
import railo.commons.util.mod.WeakHashMapPro;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.StructSupport;

/**
 * CFML data type struct
 */
public class StructImpl extends StructSupport {
	private static final long serialVersionUID = 1421746759512286393L;

	// NULL Support private static final Object NULL=new Object();
	
	private MapPro<Collection.Key,Object> map;
	
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
    	if(type==TYPE_LINKED)		map=new LinkedHashMapPro<Collection.Key,Object>();
    	else if(type==TYPE_WEAKED)	map=new WeakHashMapPro<Collection.Key,Object>();
    	else if(type==TYPE_SOFT)	map=new MapProWrapper<Collection.Key, Object>(new ReferenceMap(),new Object());
        else if(type==TYPE_SYNC)	map=new SyncMap<Collection.Key, Object>(new HashMapPro<Collection.Key,Object>());
        else 						map=new HashMapPro<Collection.Key,Object>();
    }
    
    private int getType(){
    	if(map instanceof LinkedHashMapPro) return TYPE_LINKED;
    	if(map instanceof WeakHashMapPro) return TYPE_WEAKED;
    	if(map instanceof SyncMap) return TYPE_SYNC;
    	if(map instanceof MapProWrapper) return TYPE_SOFT;
    	return TYPE_REGULAR;
    }
    
	
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return map.g(key, defaultValue); // NULL Support
		/* NULL old behavior
		 Object rtn=map.get(key);
		if(rtn!=null) {
			// NULL Support if(rtn==NULL) return null;
			return rtn;
		}
		return defaultValue;*/
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {//print.out("k:"+(kcount++));
		return map.g(key); // NULL Support
		/* NULL Old behavior
		 Object rtn=map.get(key);
		if(rtn!=null) {
			// NULL Support if(rtn==NULL) return null;
			return rtn;
		}
		throw invalidKey(this,key);*/
	}
	
	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		// NULL Support map.put(key,value==null?NULL:value);
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
			return map.keySet().toArray(new Key[map.size()]);
		}
		catch(Throwable t) {
			MapPro<Key, Object> old = map;
			try{	
				map = new railo.commons.util.mod.SyncMap(map);
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

	/* *
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 * /
	public Object remove (String key) throws PageException {
		Object obj= map.remove (KeyImpl.init(key));
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exist");
		return obj;
	}*/

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return map.r(key); // NULL Support
		/* NULL old behavior
		Object obj= map.remove(key);
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exist");
		return obj;*/
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
			Iterator<Entry<Key, Object>> it = src.entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()) {
				e = it.next();
				if(!deepCopy) trg.setEL(e.getKey(),e.getValue());
				else trg.setEL(e.getKey(),Duplicator.duplicate(e.getValue(),deepCopy));
			}
		}
		finally {
			//ThreadLocalDuplication.remove(src);
		}	
	}
	
	
	
	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return map.keySet().iterator();
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
		return new StringIterator(keys());
	}
	

	public Iterator<Entry<Key, Object>> entryIterator() {
		return this.map.entrySet().iterator();
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator<Object> valueIterator() {
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
	public java.util.Collection<Object> values() {
		return map.values();
	}
}