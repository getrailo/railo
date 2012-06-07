package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.Admin;
import railo.runtime.tag.util.DeprecatedUtil;

public final class SSLCertificateInstall implements Function {

	private static final long serialVersionUID = -831759073098524176L;

	public static String call(PageContext pc, String host) throws PageException {
    	return call(pc, host, 443);
    }
    
    public static String call(PageContext pc, String host, double port) throws PageException {
    	DeprecatedUtil.function(pc, "SSLCertificateInstall");
    	Admin.updateSSLCertificate(pc.getConfig(), host, (int)port);
		return "";
    }

}