package railo.runtime.functions.s3;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.type.s3.AccessControl;
import railo.commons.io.res.type.s3.S3Exception;
import railo.commons.io.res.type.s3.S3Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class StoreGetACL {

	public static final Collection.Key GROUP = KeyImpl.intern("group");
	public static final Collection.Key DISPLAY_NAME = KeyImpl.intern("displayName");
	public static final Collection.Key EMAIL = KeyImpl.intern("email");
	public static final Collection.Key PERMISSION = KeyImpl.intern("permission");
	
	
	public static Object call(PageContext pc , String url) throws PageException {
        
		S3Resource res=toS3Resource(pc,url);
		try {
			return toArrayStruct(res.getAccessControlPolicy().getAccessControlList());
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
    }

	public static S3Resource toS3Resource(PageContext pc, String url) throws ExpressionException {
		Resource res=ResourceUtil.toResourceNotExisting(pc, url);
		ResourceProvider provider = res.getResourceProvider();
		if(!provider.getScheme().equalsIgnoreCase("s3") || !res.exists()) 
			throw new FunctionException(pc,"StoreGetACL",1,"url","defined url must be a valid existing S3 Resource");
		
		return (S3Resource) res;
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
			sct.setEL(KeyImpl.ID, ac.getId());
			sct.setEL(PERMISSION, ac.getPermission());
			
			type = AccessControl.toType(ac.getType());
			if("Group".equalsIgnoreCase(type)) 				
				setGroup(sct,ac);
			else if("CanonicalUser".equalsIgnoreCase(type)) 
				sct.setEL(DISPLAY_NAME, ac.getDisplayName());
			else 
				sct.setEL(EMAIL, ac.getId());
		}
		return arr;
	}
	
	private static void setGroup(Struct sct, AccessControl ac) {
		String uri = ac.getUri();
		sct.setEL(KeyImpl.ID, uri);
		if("http://acs.amazonaws.com/groups/global/AllUsers".equalsIgnoreCase(uri))
			sct.setEL(GROUP, "all");
		else if("http://acs.amazonaws.com/groups/global/AuthenticatedUsers".equalsIgnoreCase(uri))
			sct.setEL(GROUP, "authenticated");
		else if("http://acs.amazonaws.com/groups/s3/LogDelivery".equalsIgnoreCase(uri))
			sct.setEL(GROUP, "log_delivery");
	}

}
