package railo.commons.pdf;

public class PDFPageMark {

	private int areaHeight;
	private String htmlTemplate;

	 
	
	/**
	 * Constructor of the class
	 * @param areaHeight
	 * @param htmlTemplate
	 */
	public PDFPageMark(int areaHeight, String htmlTemplate) {
		this.areaHeight = areaHeight;
		this.htmlTemplate = htmlTemplate;
	}
	/**
	 * @return the areaHeight
	 */
	public int getAreaHeight() {
		return areaHeight;
	}
	/**
	 * @param areaHeight the areaHeight to set
	 */
	public void setAreaHeight(int areaHeight) {
		this.areaHeight = areaHeight;
	}
	/**
	 * @return the htmlTemplate
	 */
	public String getHtmlTemplate() {
		return htmlTemplate;
	}
	/**
	 * @param htmlTemplate the htmlTemplate to set
	 */
	public void setHtmlTemplate(String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}


}
