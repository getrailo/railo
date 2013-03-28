package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileGetMimeType {
	public static String call(PageContext pc, Object oSrc) throws PageException {
		return call(pc, oSrc, true);
	}
	
	
	public static String call(PageContext pc, Object oSrc, boolean checkHeader) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,false);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		
		// check type
        int checkingType=checkHeader?ResourceUtil.MIMETYPE_CHECK_HEADER:ResourceUtil.MIMETYPE_CHECK_EXTENSION;
        
        String mimeType = ResourceUtil.getMimeType(src, checkingType, null);
        if(StringUtil.isEmpty(mimeType,true)) return "application/octet-stream";
        return mimeType;
	}
}
