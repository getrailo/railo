/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.type;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.collection.HashMapPro;
import railo.commons.collection.LinkedHashMapPro;
import railo.commons.collection.MapFactory;
import railo.commons.collection.MapPro;
import railo.commons.collection.MapProWrapper;
import railo.commons.collection.SyncMap;
import railo.commons.collection.WeakHashMapPro;
import railo.commons.lang.SerializableObject;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

/**
 * CFML data type struct
 */
public class StructImpl extends StructSupport {
	private static final long serialVersionUID = 1421746759512286393L;

	public static final int TYPE_UNDEFINED = -1;// FUTURE add to interface Struct 

	private MapPro<Collection.Key,Object> map;
	
	/**
	 * default constructor
	 */
	public StructImpl() {
		this(StructImpl.TYPE_UNDEFINED,HashMapPro.DEFAULT_INITIAL_CAPACITY);//asx
	}
	
	/**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param type
     */
    public StructImpl(int type) {
    	this(type,HashMapPro.DEFAULT_INITIAL_CAPACITY);
    }
	
    /**
     * This implementation spares its clients from the unspecified, 
     * generally chaotic ordering provided by normally Struct , 
     * without incurring the increased cost associated with TreeMap. 
     * It can be used to produce a copy of a map that has the same order as the original
     * @param type
     * @param initialCapacity initial capacity - MUST be a power of two.
     */
    public StructImpl(int type, int initialCapacity) {
    	if(type==TYPE_WEAKED)	map=new SyncMap<Collection.Key, Object>(new WeakHashMapPro<Collection.Key,Object>(initialCapacity));
    	else if(type==TYPE_SOFT)	map=new SyncMap<Collection.Key, Object>(new MapProWrapper<Collection.Key, Object>(new ReferenceMap(ReferenceMap.HARD,ReferenceMap.SOFT,initialCapacity,0.75f),new SerializableObject()));
    	else if(type==TYPE_LINKED)		map=new SyncMap<Collection.Key, Object>(new LinkedHashMapPro<Collection.Key,Object>(initialCapacity));
    	else 						map=MapFactory.getConcurrentMap(initialCapacity);
    }
    
    
    
    
    public int getType(){
    	return StructUtil.getType(map);
    }
    
    
    
    
	
	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		if(NullSupportHelper.full())return map.g(key, defaultValue);
		
		Object rtn=map.get(key);
		if(rtn!=null) return rtn;
		return defaultValue;
	}
	

	public Object g(Collection.Key key, Object defaultValue) {
		return map.g(key, defaultValue);
	}
	public Object g(Collection.Key key) throws PageException {
		return map.g(key);
	}

	@Override
	public Object get(Collection.Key key) throws PageException {
		if(NullSupportHelper.full()) return map.g(key);
		
		Object rtn=map.get(key);
		if(rtn!=null) return rtn;
		throw StructSupport.invalidKey(null,this,key);
	}
	
	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		map.put(key,value);
		return value;
	}
	
	@Override
	public Object setEL(Collection.Key key, Object value) {
		map.put(key,value);
		return value;
	}

	@Override
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
				map = new railo.commons.collection.SyncMap(map);
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

	@Override
	public Object remove(Collection.Key key) throws PageException {
		if(NullSupportHelper.full())return map.r(key);
		Object obj= map.remove(key);
		if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exist");
		return obj;
	}
	
	@Override
	public Object removeEL(Collection.Key key) {
		return map.remove(key);
	}
	
	@Override
	public void clear() {
		map.clear();
	}

	
	@Override
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
	
	
	
	@Override
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
	
	@Override
	public Iterator<Object> valueIterator() {
		return map.values().iterator();
	}

    @Override
    public boolean containsKey(Collection.Key key) {
        return map.containsKey(key);
    }
    
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	@Override
	public java.util.Collection<Object> values() {
		return map.values();
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return map.equals(obj);
	}


	@Override
	public Set keySet() {
		return StructUtil.keySet(this,getType()==TYPE_LINKED);
	}
}