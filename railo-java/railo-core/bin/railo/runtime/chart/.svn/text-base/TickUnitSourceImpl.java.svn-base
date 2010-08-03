package railo.runtime.chart;

import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

public class TickUnitSourceImpl implements TickUnitSource {

	private int labelFormat;

	public TickUnitSourceImpl(int labelFormat) {
		this.labelFormat=labelFormat;
	}
	
	public TickUnit getCeilingTickUnit(TickUnit unit) {
		return new TicketUnitImpl(labelFormat,unit);
	}

	public TickUnit getCeilingTickUnit(double size) {
		return new TicketUnitImpl(labelFormat,size);
	}

	public TickUnit getLargerTickUnit(TickUnit unit) {
		return new TicketUnitImpl(labelFormat,unit);
	}

	public static TickUnitSource getInstance(int labelFormat) {
		return new TickUnitSourceImpl(labelFormat);
	}


}
