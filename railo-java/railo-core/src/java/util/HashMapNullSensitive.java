package java.util;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;

public class HashMapNullSensitive extends HashMap<Key, Object> {


	public Object gib(Key key) throws PageException {
		int hash = hash(key.hashCode());
		Key k;
		for (Entry<Key,Object> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && ((k = e.key) == key || key.equalsIgnoreCase(k)))
				return e.value;
			}
			throw invalidKey(this,key);
	}
	

	public Object gib(Key key, Object defaultValue) {
		int hash = hash(key.hashCode());
		Key k;
		for (Entry<Key,Object> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			if (e.hash == hash && ((k = e.key) == key || key.equalsIgnoreCase(k)))
				return e.value;
			}
			return defaultValue;
	}
	
	public Object haeb(Key key, Object value) {
		int hash = hash(key.hashCode());
		int i = indexFor(hash, table.length);
		for (Entry<Key,Object> e = table[i]; e != null; e = e.next) {
			Key k;
			if (e.hash == hash && ((k = e.key) == key || key.equalsIgnoreCase(k))) {
				Object oldValue = e.value;
				e.value = value;
				e.recordAccess(this);
				return oldValue;
		  	}
		}

		modCount++;
		addEntry(hash, key, value, i);
		return null;
	}
	
	
	public static ExpressionException invalidKey(Map map,Key key) {

		StringBuilder sb=new StringBuilder();
		Iterator<Key> it = map.keySet().iterator();
		Key k;

		while(it.hasNext()){
			k = it.next();
			if( k.equals( key ) )
				return new ExpressionException( "the value from key [" + key.getString() + "] is NULL, which is the same as not existing in CFML" );
			if(sb.length()>0)sb.append(',');
			sb.append(k.getString());
		}

		return new ExpressionException( "key [" + key.getString() + "] doesn't exist (existing keys:" + sb.toString() + ")" );
	}
}
