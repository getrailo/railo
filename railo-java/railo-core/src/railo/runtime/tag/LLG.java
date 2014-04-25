package railo.runtime.tag;

import java.text.AttributedString;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

public class LLG implements PieSectionLabelGenerator {

	@Override
	public AttributedString generateAttributedSectionLabel(PieDataset arg0,
			Comparable arg1) {
		//print.out("11111111");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateSectionLabel(PieDataset arg0, Comparable arg1) {
		//print.out("222222222");
		return "33333";
	}

}
