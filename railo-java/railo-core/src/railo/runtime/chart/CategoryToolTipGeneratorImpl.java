/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
