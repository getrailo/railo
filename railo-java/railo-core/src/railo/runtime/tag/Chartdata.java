package railo.runtime.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import railo.runtime.ext.tag.TagImpl;

public final class Chartdata extends TagImpl {

	private ChartDataBean data=new ChartDataBean();
	
	@Override
	public void release() {
		super.release();
		data=new ChartDataBean();
	}
	
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		data.setItem(item);
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		data.setValue(value);
	}

	@Override
	public int doStartTag() throws JspException {

		//print.out("do start tag");
		Tag parent=this;
		do{
			parent = parent.getParent();
			if(parent instanceof Chartseries) {
				((Chartseries)parent).addChartData(data);
				break;
			}
		}
		while(parent!=null);
		return SKIP_BODY;
	}
}
