package railo.commons.collection;

import java.util.LinkedHashMap;

public class LinkedHashMapMaxSize<K, V> extends LinkedHashMap<K, V> {
	
	private int maxSize;

	public LinkedHashMapMaxSize(int maxSize){
		this.maxSize=maxSize;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}
