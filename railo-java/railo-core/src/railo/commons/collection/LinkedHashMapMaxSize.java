package railo.commons.collection;

import java.util.LinkedHashMap;

public class LinkedHashMapMaxSize<K, V> extends LinkedHashMap<K, V> {
	
	private int maxSize;

	public LinkedHashMapMaxSize(int maxSize, int initCapacity, boolean accessOrder) {
		super( initCapacity, 0.75f, accessOrder );
		this.maxSize=maxSize;
	}

	public LinkedHashMapMaxSize(int maxSize) {
		this( maxSize, (int)Math.ceil( maxSize / 0.75f ), false );
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}
