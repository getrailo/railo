package railo.runtime.functions.other;


import java.security.cert.X509Certificate;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.net.http.CertificateInstaller;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

public final class SSLCertificateList implements Function {

    public static Query call(PageContext pc, String host) throws PageException {
    	return call(pc, host, 443);
    }
    
    public static Query call(PageContext pc, String host, double port) throws PageException {
    	Resource cacerts = pc.getConfig().getConfigDir().getRealResource("security/cacerts");
    	CertificateInstaller installer;
		try {
			installer = new CertificateInstaller(cacerts,host,(int)port);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
    	X509Certificate[] certs = installer.getCertificates();
    	X509Certificate cert;
    	
    	QueryImpl qry=new QueryImpl(new String[]{"subject","issuer"},certs.length,"certificates");
    	for(int i=0;i<certs.length;i++){
    		cert=certs[i];
    		qry.setAtEL("subject",i+1, cert.getSubjectDN().getName());
    		qry.setAtEL("issuer",i+1, cert.getIssuerDN().getName());
    	}
    	return qry;
    	
    }

}