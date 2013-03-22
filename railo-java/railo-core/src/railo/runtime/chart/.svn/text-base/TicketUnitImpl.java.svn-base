package railo.runtime.chart;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;

public class TicketUnitImpl extends NumberTickUnit {

	
	private int labelFormat;

	/**
	 * Constructor of the class
	 * @param size
	 */
	public TicketUnitImpl(int labelFormat,double size) {
		super(size);
		this.labelFormat=labelFormat;
		
	}

	/**
	 * Constructor of the class
	 * @param unit
	 */
	public TicketUnitImpl(int labelFormat,TickUnit unit) {
		this(labelFormat,unit.getSize());
	}

	/**
	 *
	 * @see org.jfree.chart.axis.TickUnit#valueToString(double)
	 */
	public String valueToString(double value) {
		return LabelFormatUtil.format(labelFormat,value);
	}

}
