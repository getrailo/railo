package railo.runtime.chart;

import java.io.Serializable;

import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

public class TickUnitsImpl implements TickUnitSource, Cloneable, Serializable {

	private TickUnitSource tus;
	private int labelFormat;

	/**
	 * Constructor of the class
	 * @param tus
	 */
	public TickUnitsImpl(TickUnitSource tus, int labelFormat) {
		this.tus=tus;
		this.labelFormat=labelFormat;
	}
	
	@Override
	public TickUnit getCeilingTickUnit(TickUnit unit) {
		return new TickUnitWrap(tus.getCeilingTickUnit(unit),labelFormat);
	}

	@Override
	public TickUnit getCeilingTickUnit(double size) {
		return new TickUnitWrap(tus.getCeilingTickUnit(size),labelFormat);
	}

	@Override
	public TickUnit getLargerTickUnit(TickUnit unit) {
		return new TickUnitWrap(tus.getLargerTickUnit(unit),labelFormat);
	}

}
