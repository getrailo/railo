package railo.runtime.functions.s3;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.res.type.s3.AccessControl;
import railo.commons.io.res.type.s3.S3Exception;
import railo.commons.io.res.type.s3.S3Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public class StoreGetACL extends S3Function {

	public static final Collection.Key DISPLAY_NAME = KeyImpl.intern("displayName");
	public static final Collection.Key PERMISSION = KeyImpl.intern("permission");
	
	
	public static Object call(PageContext pc , String url) throws PageException {
        
		S3Resource res=toS3Resource(pc,url,"StoreGetACL");
		try {
			return toArrayStruct(res.getAccessControlPolicy().getAccessControlList());
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
    }

	private static Object toArrayStruct(List<AccessControl> accessControlList) throws S3Exception {
		Array arr=new ArrayImpl();
		String type;
		Struct sct;
		AccessControl ac;
		Iterator<AccessControl> it = accessControlList.iterator();
		while(it.hasNext()){
			ac=it.next();
			arr.appendEL(sct=new StructImpl());
			sct.setEL(KeyConstants._id, ac.getId());
			sct.setEL(PERMISSION, ac.getPermission());
			
			type = AccessControl.toType(ac.getType());
			if("Group".equalsIgnoreCase(type)) 				
				setGroup(sct,ac);
			else if("CanonicalUser".equalsIgnoreCase(type)) 
				sct.setEL(DISPLAY_NAME, ac.getDisplayName());
			else 
				sct.setEL(KeyConstants._email, ac.getId());
		}
		return arr;
	}
	
	private static void setGroup(Struct sct, AccessControl ac) {
		String uri = ac.getUri();
		sct.setEL(KeyConstants._id, uri);
		if("http://acs.amazonaws.com/groups/global/AllUsers".equalsIgnoreCase(uri))
			sct.setEL(KeyConstants._group, "all");
		else if("http://acs.amazonaws.com/groups/global/AuthenticatedUsers".equalsIgnoreCase(uri))
			sct.setEL(KeyConstants._group, "authenticated");
		else if("http://acs.amazonaws.com/groups/s3/LogDelivery".equalsIgnoreCase(uri))
			sct.setEL(KeyConstants._group, "log_delivery");
	}

}
