package railo.runtime.functions.s3;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.res.type.s3.AccessControl;
import railo.commons.io.res.type.s3.AccessControlPolicy;
import railo.commons.io.res.type.s3.S3Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class StoreAddACL extends S3Function {
	
	public static String call(PageContext pc , String url, Object objACL) throws PageException {
		try {
			return _call(pc, url, objACL);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	public static String _call(PageContext pc , String url, Object objACL) throws PageException, IOException {
		S3Resource res=toS3Resource(pc,url,"StoreAddACL");
		AccessControlPolicy acp = res.getAccessControlPolicy();
		
		List<AccessControl> acl = acp.getAccessControlList();
		List<AccessControl> newACL = AccessControl.toAccessControlList(objACL);
		
		Iterator<AccessControl> it = newACL.iterator();
		while(it.hasNext()){
			acl.add(it.next());
		}
		AccessControlPolicy.removeDuplicates(acl);
		res.setAccessControlPolicy(acp);
		
		return null;
	}
	

	
	
}
