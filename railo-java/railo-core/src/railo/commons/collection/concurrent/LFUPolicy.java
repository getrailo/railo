package railo.commons.collection.concurrent;

import railo.commons.collection.concurrent.ConcurrentLinkedHashMapPro.Entry;


public class LFUPolicy implements EvictionPolicy {

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
		return head.getAfter();
	}

	@Override
	public Entry<?, ?> recordInsertion(Entry<?, ?> head, Entry<?, ?> insertedEntry) {
		return null;
	}

	@Override
	public Entry<?, ?> recordAccess(Entry<?, ?> head, Entry<?, ?> accessedEntry) {
		Entry<?, ?> lfuEntry = accessedEntry.getAfter();
		while(lfuEntry != head && lfuEntry.getAccessCount() <= accessedEntry.getAccessCount())
			lfuEntry = lfuEntry.getAfter();
		return lfuEntry;
	}


}
