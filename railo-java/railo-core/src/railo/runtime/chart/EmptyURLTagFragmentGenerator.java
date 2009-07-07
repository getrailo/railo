package railo.runtime.chart;

import org.jfree.chart.imagemap.URLTagFragmentGenerator;

public class EmptyURLTagFragmentGenerator implements URLTagFragmentGenerator {

	/**
     * Generates a URL string to go in an HTML image map.
     * @param urlText  the URL text (fully escaped).
     * @return The formatted text
     */
    public String generateURLFragment(String urlText) {
    	return "";
		//return " href=\"" + urlText + "\"";
	}

}
