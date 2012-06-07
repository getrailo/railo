package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.Admin;
import railo.runtime.tag.util.DeprecatedUtil;
import railo.runtime.type.Query;

public final class SSLCertificateList implements Function {

	private static final long serialVersionUID = 1114950592159155566L;

	public static Query call(PageContext pc, String host) throws PageException {
    	return call(pc, host, 443);
    }
    
    public static Query call(PageContext pc, String host, double port) throws PageException {
    	DeprecatedUtil.function(pc, "SSLCertificateList");
    	return Admin.getSSLCertificate(pc.getConfig(), host, (int)port);
    }

}