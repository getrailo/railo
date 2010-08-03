package railo.runtime.chart;

import java.text.AttributedString;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import railo.runtime.op.Caster;

public class PieSectionLabelGeneratorImpl implements PieSectionLabelGenerator {

	private int labelFormat; 

	public PieSectionLabelGeneratorImpl(int labelFormat) {
		this.labelFormat=labelFormat;
	}

	public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
		return null;
	}

	public String generateSectionLabel(PieDataset pd, Comparable c) {
		double value = Caster.toDoubleValue(pd.getValue(c),0.0);
		return LabelFormatUtil.format(labelFormat, value);
		}
		
		

}
