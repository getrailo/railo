package railo.runtime.type.scope;

import java.io.Serializable;

import railo.runtime.type.Collection;

public final class ClusterEntryImpl  implements ClusterEntry {

	private Collection.Key key;
	private long time;
	private Serializable value;

	public ClusterEntryImpl(Collection.Key key,Serializable value, int offset) {
		this.key=key;
		this.time=System.currentTimeMillis()+offset;
		this.value=value;
	}
	public ClusterEntryImpl(Collection.Key key,Serializable value, long time) {
		this.key=key;
		this.time=time;
		this.value=value;
	}
	
	/**
	 * Constructor of the class for Webservice Bean Deserializer
	 */
	public ClusterEntryImpl() {}

	/**
	 * @param key the key to set
	 */
	@Override
	public void setKey(Collection.Key key) {
		this.key = key;
	}
	/**
	 * @param time the time to set
	 */
	@Override
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @param value the value to set
	 */
	@Override
	public void setValue(Serializable value) {
		this.value = value;
	}
	/**
	 * @return the key
	 */
	@Override
	public Collection.Key getKey() {
		return key;
	}

	/**
	 * @return the time
	 */
	@Override
	public Long getTimeRef() {
		return Long.valueOf(time);
	}
	
	@Override
	public long getTime() {
		return time;
	}

	/**
	 * @return the value
	 */
	@Override
	public Serializable getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ClusterEntry) {
			ClusterEntry other = (ClusterEntry)obj;
			return key.equalsIgnoreCase(other.getKey());
		}
		return false;
	}
}