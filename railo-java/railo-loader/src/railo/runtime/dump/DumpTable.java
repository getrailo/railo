package railo.runtime.dump;

import java.util.ArrayList;
import java.util.List;


/**
 * class to generate Railo HTML Boxes for dumps
 */
public class DumpTable implements DumpData {

	private List rows=new ArrayList();
	private String title;
	private String comment;
	private String highLightColor;
	private String normalColor;
	private String borderColor;
	private String fontColor;
	private String width;
	private String height;
	
	
	public DumpTable(String highLightColor, String normalColor, String borderColor, String fontColor) {
		this.highLightColor=highLightColor;
		this.normalColor=normalColor;
		this.borderColor=borderColor;
		this.fontColor=fontColor;
	}
	public DumpTable(String highLightColor, String normalColor, String borderColor) {
		this(highLightColor, normalColor, borderColor, borderColor);
	}
	
	/**
	 * @return returns if the box has content or not
	 */
	public boolean isEmpty() {
		return rows.isEmpty();
	}

	/**
	 * clear all data set in the HTMLBox
	 */
	public void clear() {
		rows.clear();
	}

    /**
     * @param title sets the title of the HTML Box
     */
    public void setTitle(String title) {
    	this.title=title;
    }
    
    /**
     * returns the title of the DumpTable, if not defined returns null
     * @return title of the DumpTable
     */
    public String getTitle() {
    	return title;
    }

    /**
     * @param comment sets the comment of the HTML Box
     */
    public void setComment(String comment) {
    	this.comment=comment;
    }
    
    /**
     * returns the comment of the DumpTable, if not defined returns null
     * @return title of the DumpTable
     */
    public String getComment() {
    	return comment;
    }
	
	/**
	 * @param width sets the With of the HTML Box, can be a number or a procentual value
	 */
	public void setWidth(String width) {
		this.width=width;
	}

	/**
	 * @param height sets the Height of the HTML Box, can be a number or a procentual value
	 */
	public void setHeight(String height) {
		this.height=height;
	}


	/**
	 * @return the borderColor
	 */
	public String getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the fontColor
	 */
	public String getFontColor() {
		return fontColor;
	}

	/**
	 * @param fontColor the fontColor to set
	 */
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * @return the highLightColor
	 */
	public String getHighLightColor() {
		return highLightColor;
	}

	/**
	 * @param highLightColor the highLightColor to set
	 */
	public void setHighLightColor(String highLightColor) {
		this.highLightColor = highLightColor;
	}

	/**
	 * @return the normalColor
	 */
	public String getNormalColor() {
		return normalColor;
	}

	/**
	 * @param normalColor the normalColor to set
	 */
	public void setNormalColor(String normalColor) {
		this.normalColor = normalColor;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @return the rows
	 */
	public DumpRow[] getRows() {
		return (DumpRow[])rows.toArray(new DumpRow[rows.size()]);
	}

	public void appendRow(DumpRow row) {
		rows.add(row);
	}
	
	public void appendRow(int highlightType, DumpData item1) {
		appendRow(new DumpRow(highlightType,new DumpData[]{item1}));
	}

    public void appendRow(int highlightType, DumpData item1, DumpData item2) {
    	appendRow(new DumpRow(highlightType,new DumpData[]{item1,item2}));
	}

	public void appendRow(int highlightType, DumpData item1, DumpData item2, DumpData item3) {
		appendRow(new DumpRow(highlightType,new DumpData[]{item1,item2,item3}));
	}
	
	public void appendRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4) {
		appendRow(new DumpRow(highlightType,new DumpData[]{item1,item2,item3,item4}));
	}
	
	public void appendRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4, DumpData item5) {
		appendRow(new DumpRow(highlightType,new DumpData[]{item1,item2,item3,item4,item5}));
	}

	public void appendRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4, DumpData item5, DumpData item6) {
		appendRow(new DumpRow(highlightType,new DumpData[]{item1,item2,item3,item4,item5,item6}));
	}
	

	public void prependRow(DumpRow row) {
		rows.add(0, row);
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}
}