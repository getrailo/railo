package railo.runtime.chart;

import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.data.general.PieDataset;

public class PieToolTipGeneratorImpl implements PieToolTipGenerator {

	
	
	private int labelFormat;

	/**
	 * Constructor of the class
	 * @param labelFormat
	 */
	public PieToolTipGeneratorImpl(int labelFormat) {
		this.labelFormat=labelFormat;
	}

	@Override
	public String generateToolTip(PieDataset dataset, Comparable key) {
		
		String result = null;    
        if (dataset != null) {
            result=LabelFormatUtil.format(labelFormat,dataset.getValue(key).doubleValue());
        }
        return result;
		
		// TODO Auto-generated method stub
		//return toolTipGenerator.generateToolTip(dataset, key);
	}

}
