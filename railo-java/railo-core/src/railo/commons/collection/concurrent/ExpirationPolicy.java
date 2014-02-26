package railo.commons.collection.concurrent;

import railo.commons.collection.concurrent.ConcurrentLinkedHashMapPro.Entry;

public class ExpirationPolicy implements EvictionPolicy {
	
	long ageThresholdMillis;
	long idleTimeThresholdMillis;
	
	public ExpirationPolicy(long ageThresholdMillis, long idleTimeThresholdMillis) {
		this.ageThresholdMillis = ageThresholdMillis;
		this.idleTimeThresholdMillis = idleTimeThresholdMillis;
	}

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
		long now = System.currentTimeMillis();
		long accessedEntryAge = (now - accessedEntry.getCreationTime());
		long accessedEntryIdleTime = (now - accessedEntry.getLastAccessTime());
		if(accessedEntryIdleTime < idleTimeThresholdMillis && accessedEntryAge < ageThresholdMillis)
			return head;
		return accessedEntry.getAfter();
	}

}
