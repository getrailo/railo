package railo.runtime;

import javax.servlet.jsp.JspEngineInfo;



/**
 * implementation of the javax.servlet.jsp.JspEngineInfo interface, 
 * return information to JSP Engine, 
 * railo is no JSP Engine but compatible to the most j2ee specification for wen applications, also the most jsp specification
 */
public final class JspEngineInfoImpl extends JspEngineInfo {

	private String version;

	/**
	 * constructor of the JSPEngineInfo
	 * @param version railo version Information
	 */
	public JspEngineInfoImpl(String version) {
		this.version=version;
	}

	@Override
	public String getSpecificationVersion() {
		// Railo Version
		return version;
	}

}