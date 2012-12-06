package railo.runtime.type.scope.session;

import railo.commons.io.log.Log;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.storage.StorageScopeCookie;

public final class SessionCookie extends StorageScopeCookie implements Session {

	private static final long serialVersionUID = -3166541654190337670L;
	
	private static final String TYPE = "SESSION";

	private SessionCookie(PageContext pc,String cookieName,Struct sct) {
		super(pc,cookieName,"session",SCOPE_SESSION,sct);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private SessionCookie(SessionCookie other,boolean deepCopy) {
		super(other,deepCopy);
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new SessionCookie(this,deepCopy);
	}
	
	
	
	/**
	 * load new instance of the class
	 * @param name
	 * @param pc
	 * @return
	 */
	public static Session getInstance(String name, PageContext pc,Log log) {
		if(!StringUtil.isEmpty(name))
			name=StringUtil.toUpperCase(StringUtil.toVariableName(name));
		String cookieName="CF_"+TYPE+"_"+name;
		return new SessionCookie(pc,cookieName, _loadData(pc,cookieName,SCOPE_SESSION,"session",log));
	}
	
	public static boolean hasInstance(String name, PageContext pc) {
		if(!StringUtil.isEmpty(name))
			name=StringUtil.toUpperCase(StringUtil.toVariableName(name));
		String cookieName="CF_"+TYPE+"_"+name;
		return has(pc,cookieName,SCOPE_SESSION,"session");
	}
}
