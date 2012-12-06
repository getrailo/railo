package railo.runtime.tag;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class ChartDataBean implements Serializable,Comparable {

	private Object item;
	private String strItem;
	private double value;
	/**
	 * @return the item
	 */
	public Object getItem() {
		return item;
	}
	public String getItemAsString() {
		return strItem;
	}
	/**
	 * @param item the item to set
	 * @throws PageException 
	 */
	public void setItem(PageContext pc,Object obj) throws PageException {
		this.strItem = itemToString(pc, obj);
		this.item=obj;
	}
	public void setItem(String str)  {
		this.strItem = str;
		this.item=str;
	}
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "item:"+item+";"+"value;"+value+";";
	}
	public int compareTo(Object o) {
		if(!(o instanceof ChartDataBean)) return 0;
		ChartDataBean other=(ChartDataBean) o;
		return getItemAsString().compareTo(other.getItemAsString());
	}
	

	private String itemToString(PageContext pc,Object obj) throws PageException {
		if(obj instanceof Date) {
			TimeZone tz = pc.getTimeZone();
			return new railo.runtime.format.DateFormat(pc.getLocale()).format(Caster.toDate(obj, tz),"short",tz)+" "+
			new railo.runtime.format.TimeFormat(pc.getLocale()).format(Caster.toDate(obj, tz),"short",tz);
		}
		return Caster.toString(obj);
	}
}
