package railo.runtime.type.comparator;

/**
 * a value of a array with information of old position in array
 */
public final class SortRegister {
	
	private Object value;
	private int oldPosition;
	
	/**
	 * constructor of the class
	 * @param pos
	 * @param value
	 */
	public SortRegister(int pos,Object value) {
		this.value=value;
		oldPosition=pos;
	}
	/**
	 * @return Returns the oldPosition.
	 */
	public int getOldPosition() {
		return oldPosition;
	}
	/**
	 * @param oldPosition The oldPosition to set.
	 */
	public void setOldPosition(int oldPosition) {
		this.oldPosition = oldPosition;
	}
	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}