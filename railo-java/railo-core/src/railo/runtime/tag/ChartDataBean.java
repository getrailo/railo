package railo.runtime.tag;

import java.io.Serializable;

import railo.print;

public class ChartDataBean implements Serializable,Comparable {

	private String item;
	private double value;
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {print.ds();
		this.item = item;
	}
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "item:"+item+";"+"value;"+value+";";
	}
	public int compareTo(Object o) {
		if(!(o instanceof ChartDataBean)) return 0;
		ChartDataBean other=(ChartDataBean) o;
		return getItem().compareTo(other.getItem());
	}
}
