package railo.commons.collection.concurrent;

import railo.commons.collection.concurrent.ConcurrentLinkedHashMapPro.Entry;


public class MRUPolicy implements EvictionPolicy {
	
	@Override
	public boolean accessOrder() {
		return true;
	}
	
	@Override
	public boolean insertionOrder() {
		return false;
	}
	
	@Override
	public Entry<?, ?> evictElement(Entry<?, ?> head) {
		return head.getBefore();
	}

	@Override
	public Entry<?, ?> recordInsertion(Entry<?, ?> head, Entry<?, ?> insertedEntry) {
		return null;
	}

	@Override
	public Entry<?, ?> recordAccess(Entry<?, ?> head, Entry<?, ?> accessedEntry) {
		return head;
	}

}
