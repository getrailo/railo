package railo.runtime.tag;

import javax.servlet.jsp.JspException;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.List;
import railo.runtime.type.dt.DateTime;

public final class Calendar extends TagImpl {

	private static final String[] DAY_NAMES_DEFAULT = new String[]{"S", "M", "T", "W", "Th", "F", "S"};

	private static final String[] MONTH_NAMES_DEFAULT = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	
	private String name;
	private int height=-1;
	private int width=-1;
	private DateTime selectedDate;
	private DateTime startRange;
	private DateTime endRange;
	private boolean disabled;
	private String mask="MM/DD/YYYY";
	private int firstDayOfWeek=0;
	private String[] dayNames=DAY_NAMES_DEFAULT;
	private String[] monthNames=MONTH_NAMES_DEFAULT;
	private String style;
	private boolean enabled=true;
	private boolean visible=true;
	private String tooltip;
	private String onChange;
	private String onBlur;
	private String onFocus;
	
	
	public Calendar() throws ApplicationException {
		// TODO impl. tag Calendar
		throw new TagNotSupported("Calendar");
	}

	/**
	 * @see railo.runtime.ext.tag.TagImpl#release()
	 */
	public void release() {
		super.release();
		name=null;
		height=-1;
		width=-1;
		selectedDate=null;
		startRange=null;
		endRange=null;
		disabled=false;
		mask="MM/DD/YYYY";
		firstDayOfWeek=0;
		dayNames=DAY_NAMES_DEFAULT;
		monthNames=MONTH_NAMES_DEFAULT;
		style=null;
		enabled=true;
		visible=true;
		tooltip=null;
		onChange=null;
		onBlur=null;
		onFocus=null;
	}
	
	/**
	 *
	 * @see railo.runtime.ext.tag.TagImpl#doStartTag()
	 */
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	/**
	 * @param dayNames the dayNames to set
	 */
	public void setDaynames(String listDayNames) {
		this.dayNames = List.listToStringArray(listDayNames,',');
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param endRange the endRange to set
	 */
	public void setEndrange(DateTime endRange) {
		this.endRange = endRange;
	}

	/**
	 * @param firstDayOfWeek the firstDayOfWeek to set
	 */
	public void setFirstdayofweek(double firstDayOfWeek) {
		this.firstDayOfWeek = (int)firstDayOfWeek;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = (int)height;
	}

	/**
	 * @param mask the mask to set
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/**
	 * @param monthNames the monthNames to set
	 */
	public void setMonthnames(String listMonthNames) {
		this.monthNames = monthNames;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param onBlur the onBlur to set
	 */
	public void setOnblur(String onBlur) {
		this.onBlur = onBlur;
	}

	/**
	 * @param onChange the onChange to set
	 */
	public void setOnchange(String onChange) {
		this.onChange = onChange;
	}

	/**
	 * @param onFocus the onFocus to set
	 */
	public void setOnfocus(String onFocus) {
		this.onFocus = onFocus;
	}

	/**
	 * @param selectedDate the selectedDate to set
	 */
	public void setSelecteddate(DateTime selectedDate) {
		this.selectedDate = selectedDate;
	}

	/**
	 * @param startRange the startRange to set
	 */
	public void setStartrange(DateTime startRange) {
		this.startRange = startRange;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = (int)width;
	}

}
