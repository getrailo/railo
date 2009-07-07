package railo.runtime.chart;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;

import railo.commons.lang.StringUtil;

public class CategoryToolTipGeneratorImpl implements CategoryToolTipGenerator {

	private int labelFormat;

	public CategoryToolTipGeneratorImpl(int labelFormat) {
		this.labelFormat=labelFormat;
	}

	public String generateToolTip(CategoryDataset dataset, int row, int column) {
		String r = dataset.getRowKey(row).toString();
		String c = dataset.getColumnKey(column).toString();
		String both=r+","+c;
		if(StringUtil.isEmpty(r)) both=c;
		if(StringUtil.isEmpty(c)) both=r;
		
		return LabelFormatUtil.format(labelFormat, dataset.getValue(row, column).doubleValue())+" ("+both+")";
	}

}
