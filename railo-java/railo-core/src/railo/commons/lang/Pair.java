package railo.commons.lang;

import java.io.Serializable;

/**
 * a Simple name value Pair
 */
public final class Pair<K,V> implements Serializable {
	K name;
	V value;


	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 */
	public Pair(K name, V value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @return the name
	 */
	public K getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(K name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name+":"+value;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}

}