package railo.runtime.functions.file;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.security.SecurityManager;
import railo.runtime.tag.FileTag;
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
	
	public static Struct call(PageContext pc, String destination,String fileField,String accept, String nameConflict,String mode,String attributes,String strACL) throws PageException {
	    SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    
	    int nc = FileTag.toNameconflict(nameConflict);
	    int m=FileTag.toMode(mode);
	    int acl=FileTag.toAcl(strACL);
	    
	    return FileTag.actionUpload(pc, securityManager, fileField,  destination, nc, accept, m, attributes, acl, null);
	}
}
	