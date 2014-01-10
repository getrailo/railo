package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;

public class GetFileInfo {

	public static Struct call(PageContext pc, Object oSrc) throws PageException {
		Resource src = Caster.toResource(pc,oSrc,true);
		pc.getConfig().getSecurityManager().checkFileLocation(src);
		
		Struct sct=new StructImpl();

		sct.set("canRead", Caster.toBoolean(src.isReadable()));
		sct.set("canWrite", Caster.toBoolean(src.isWriteable()));
		sct.set("isHidden", Caster.toBoolean(src.getAttribute(Resource.ATTRIBUTE_HIDDEN)));
		sct.set("lastmodified",new DateTimeImpl(pc,src.lastModified(),false) );
		sct.set(KeyConstants._name,src.getName() );
		sct.set(KeyConstants._parent,src.getParent() );
		sct.set(KeyConstants._path,src.getAbsolutePath() );
		sct.set(KeyConstants._size, Caster.toDouble(src.length()));
		if(src.isDirectory())sct.set(KeyConstants._type, "directory");
		else if(src.isFile())sct.set(KeyConstants._type, "file");
		else sct.set(KeyConstants._type, "");
		
		// supported only by railo
		sct.set("isArchive", Caster.toBoolean(src.getAttribute(Resource.ATTRIBUTE_ARCHIVE)));
		sct.set("isSystem", Caster.toBoolean(src.getAttribute(Resource.ATTRIBUTE_SYSTEM)));
		sct.set("scheme", src.getResourceProvider().getScheme());
		sct.set("isCaseSensitive", Caster.toBoolean(src.getResourceProvider().isCaseSensitive()));
		sct.set("isAttributesSupported", Caster.toBoolean(src.getResourceProvider().isAttributesSupported()));
		sct.set("isModeSupported", Caster.toBoolean(src.getResourceProvider().isModeSupported()));
		
		return sct;
	}
}
