package railo.commons.collection;

import java.util.Map;

import railo.runtime.exp.PageException;

public interface MapPro<K,V> extends Map<K, V> {

	public V g(K key) throws PageException;
	public V g(K key, V defaultValue);
	
	public V r(K key) throws PageException;
	public V r(K key, V defaultValue);
	//public V p(K key, V value);

}
