package railo.runtime.chart;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;

public class TickUnitWrap extends NumberTickUnit {

	private TickUnit tickUnit;
	private int labelFormat;

	public TickUnitWrap(TickUnit tickUnit, int labelFormat) {
		super(tickUnit.getSize());
		this.tickUnit=tickUnit;
		this.labelFormat=labelFormat;
	}

	/**
	 *
	 * @see org.jfree.chart.axis.TickUnit#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		return tickUnit.compareTo(object);
	}

	/**
	 *
	 * @see org.jfree.chart.axis.TickUnit#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return tickUnit.equals(obj);
	}

	/**
	 *
	 * @see org.jfree.chart.axis.TickUnit#getSize()
	 */
	public double getSize() {
		return tickUnit.getSize();
	}

	/**
	 * @see org.jfree.chart.axis.TickUnit#hashCode()
	 */
	public int hashCode() {
		return tickUnit.hashCode();
	}

	/**
	 * @see org.jfree.chart.axis.TickUnit#valueToString(double)
	 */
	public String valueToString(double value) {
		return LabelFormatUtil.format(labelFormat, value);
	}
}
