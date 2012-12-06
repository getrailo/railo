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

	@Override
	public int compareTo(Object object) {
		return tickUnit.compareTo(object);
	}

	@Override
	public boolean equals(Object obj) {
		return tickUnit.equals(obj);
	}

	@Override
	public double getSize() {
		return tickUnit.getSize();
	}

	@Override
	public int hashCode() {
		return tickUnit.hashCode();
	}

	@Override
	public String valueToString(double value) {
		return LabelFormatUtil.format(labelFormat, value);
	}
}
