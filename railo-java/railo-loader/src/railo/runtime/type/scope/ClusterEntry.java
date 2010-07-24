package railo.runtime.type.scope;

import java.io.Serializable;

import railo.runtime.type.Collection;

public interface ClusterEntry extends Serializable {

	/**
	 * @param key the key to set
	 */
	public void setKey(Collection.Key key);
	
	/**
	 * @param time the time to set
	 */
	public void setTime(long time);
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Serializable value);
	
	/**
	 * @return the key
	 */
	public Collection.Key getKey();

	/**
	 * @return the time as Long reference
	 */
	public Long getTimeRef();
	
	/**
	 * @return the time 
	 */
	public long getTime();

	/**
	 * @return the value
	 */
	public Serializable getValue();
	
}