package railo.runtime.listener;

import railo.commons.lang.StringUtil;

public abstract class ApplicationContextSupport implements ApplicationContext {

	private static final long serialVersionUID = 1384678713928757744L;
	
	protected int idletimeout=1800;
	protected String cookiedomain;
	protected String applicationtoken;

	/**
	 * @see railo.runtime.listener.ApplicationContext#setSecuritySettings(java.lang.String, java.lang.String, int)
	 */
	public void setSecuritySettings(String applicationtoken, String cookiedomain, int idletimeout) {
		this.applicationtoken=applicationtoken;
		this.cookiedomain=cookiedomain;
		this.idletimeout=idletimeout;
		
	}
	
	/**
	 * @see railo.runtime.listener.ApplicationContext#getSecurityApplicationToken()
	 */
	public String getSecurityApplicationToken() {
		if(StringUtil.isEmpty(applicationtoken,true)) return getName();
		return applicationtoken;
	}
	
	/**
	 * @see railo.runtime.listener.ApplicationContext#getSecurityCookieDomain()
	 */
	public String getSecurityCookieDomain() {
		if(StringUtil.isEmpty(applicationtoken,true)) return null;
		return cookiedomain;
	}
	
	/**
	 * @see railo.runtime.listener.ApplicationContext#getSecurityIdleTimeout()
	 */
	public int getSecurityIdleTimeout() {
		if(idletimeout<1) return 1800;
		return idletimeout;
	}

	public abstract boolean getBufferOutput();
	public abstract void setBufferOutput(boolean bufferOutput);

}
