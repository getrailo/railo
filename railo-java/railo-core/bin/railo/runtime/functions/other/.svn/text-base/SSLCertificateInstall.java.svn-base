package railo.runtime.functions.other;


import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.CertificateInstaller;
import railo.runtime.op.Caster;

public final class SSLCertificateInstall implements Function {

    public static String call(PageContext pc, String host) throws PageException {
    	return call(pc, host, 443);
    }
    
    public static String call(PageContext pc, String host, double port) throws PageException {
    	Resource cacerts = pc.getConfig().getConfigDir().getRealResource("security/cacerts");
    	try {
			CertificateInstaller installer = new CertificateInstaller(cacerts,host,(int)port);
			installer.installAll();
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		return "";
    }

}