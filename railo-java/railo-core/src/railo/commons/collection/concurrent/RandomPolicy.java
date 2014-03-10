package railo.commons.collection.concurrent;

import java.util.Random;

import railo.commons.collection.concurrent.ConcurrentLinkedHashMapPro.Entry;

public class RandomPolicy implements EvictionPolicy {

	Random random = new Random();
	
	@Override
	public boolean accessOrder() {
		return false;
	}
	
	@Override
	public boolean insertionOrder() {
		return false;
	}
	
	@Override
	public Entry<?, ?> evictElement(Entry<?, ?> head) {
		int hops = random.nextInt();
		Entry<?,?> entryToEvict = head.getAfter();
		for(int i = 0; i < hops; i++)
			entryToEvict = entryToEvict.getAfter();
		return entryToEvict;
	}

	@Override
	public Entry<?, ?> recordInsertion(Entry<?, ?> head, Entry<?, ?> insertedEntry) {
		return null;
	}

	@Override
	public Entry<?, ?> recordAccess(Entry<?, ?> head, Entry<?, ?> accessedEntry) {
		return null;
	}

}
