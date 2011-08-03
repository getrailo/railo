package railo.commons.lang;

import java.io.Serializable;

/**
 * a Simple name value Pair
 */
public final class Pair implements Serializable {
	String name;
	Object value;


	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 */
	public Pair(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name+":"+value;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}