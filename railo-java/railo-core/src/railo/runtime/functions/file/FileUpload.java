package railo.runtime.functions.file;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.security.SecurityManager;
import railo.runtime.tag.FileTag;
import railo.runtime.tag.util.FileUtil;
import railo.runtime.type.Struct;

public class FileUpload {

	public static Struct call(PageContext pc, String destination) throws PageException {
		return call(pc, destination, null, null, null, null, null,null);
	}
	public static Struct call(PageContext pc, String destination,String fileField) throws PageException {
		return call(pc, destination, fileField, null, null, null, null,null);
	}
	public static Struct call(PageContext pc, String destination,String fileField,String accept) throws PageException {
		return call(pc, destination, fileField, accept, null, null, null,null);
	}
	public static Struct call(PageContext pc, String destination,String fileField,String accept, String nameConflict) throws PageException {
		return call(pc, destination, fileField, accept, nameConflict, null, null,null);
	}
	public static Struct call(PageContext pc, String destination,String fileField,String accept, String nameConflict,String mode) throws PageException {
		return call(pc, destination, fileField, accept, nameConflict, mode, null,null);
	}
	public static Struct call(PageContext pc, String destination,String fileField,String accept, String nameConflict,String mode,String attributes) throws PageException {
		return call(pc, destination, fileField, accept, nameConflict, mode, attributes,null);
	}
	
	public static Struct call(PageContext pc, String destination,String fileField,String accept, String nameConflict,String mode,String attributes,Object acl) throws PageException {
	    SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    
	    int nc = FileUtil.toNameConflict(nameConflict);
	    int m=FileTag.toMode(mode);
	    
	    return FileTag.actionUpload(pc, securityManager, fileField,  destination, nc, accept,true, m, attributes, acl, null);
	}
}
	