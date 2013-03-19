package railo.runtime.chart;

import java.awt.Font;
import java.awt.FontMetrics;
import java.text.AttributedString;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import railo.commons.lang.StringList;
import railo.commons.lang.font.FontUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class PieSectionLegendLabelGeneratorImpl implements
		PieSectionLabelGenerator {


	private FontMetrics metrics;
	private int with;

	public PieSectionLegendLabelGeneratorImpl(Font font,int with) {
		this.metrics=FontUtil.getFontMetrics(font);
		this.with=with-20;
	}

	public AttributedString generateAttributedSectionLabel(PieDataset dataset,
			Comparable key) {
		return null;
	}

	public String generateSectionLabel(PieDataset pd, Comparable c) {
		String value=Caster.toString(pd.getKey(pd.getIndex(c)),"");
		
		StringList list = ListUtil.toList(value, '\n');
		StringBuffer sb=new StringBuffer();
		String line;
		int lineLen;
		while(list.hasNext()) {
			line=list.next();
			lineLen=metrics.stringWidth(line);
			if(lineLen>with) {
				reorganize(sb,list,new StringBuffer(line));
				break;
			}
			if(sb.length()>0)sb.append('\n');
			sb.append(line);
		}
		
		
		
		//int strLen = metrics.stringWidth(value);
		return sb.toString();//metrics.stringWidth(value)+"-"+with+":"+value;
		//return "StringUtil.reverse()";
	}

	private void reorganize(StringBuffer sb, StringList list, StringBuffer rest) {
		// fill rest
		String item;
		while(list.hasNext()) {
			item=list.next();
			rest.append(list.delimiter());
			rest.append(item);
		}
		
		StringList words = ListUtil.toWordList(rest.toString());
		StringBuffer line=new StringBuffer();
		
		while(words.hasNext()) {
			item=words.next();
			
			if(line.length()>0 && metrics.stringWidth(item.concat(" ").concat(line.toString()))>with) {
				if(sb.length()>0) sb.append('\n');
				sb.append(line);
				//print.out("line:"+line);
				line=new StringBuffer(item);
			}
			else {
				//item=words.next();
				if(line.length()>0)line.append(words.delimiter()==0?' ':words.delimiter());
				line.append(item);
			}
		}
		if(line.length()>0){
			if(sb.length()>0) sb.append('\n');
			sb.append(line);
		}
	}
}
