package railo.runtime.chart;

import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;

import railo.commons.lang.HTMLEntities;

public class ToolTipTagFragmentGeneratorImpl implements
		ToolTipTagFragmentGenerator {

	private String url;
	
	public ToolTipTagFragmentGeneratorImpl(String url) {
		this.url=url;
	}	

	/**
     * Generates a tooltip string to go in an HTML image map.
     * @param toolTipText  the tooltip.
     * @return The formatted HTML area tag attribute(s).
     */
    public String generateToolTipFragment(String toolTipText) {
    	toolTipText=HTMLEntities.escapeHTML(toolTipText,HTMLEntities.HTMLV20);
    	
    	String href="";
    	
        return href+" title=\"" + toolTipText 
            + "\" alt=\"" + toolTipText 
            + "\"";
    }

}
