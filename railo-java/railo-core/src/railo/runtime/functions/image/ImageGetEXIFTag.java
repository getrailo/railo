package railo.runtime.functions.image;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public class ImageGetEXIFTag {

	public static Object call(PageContext pc, Object name, String tagName) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		Image img = Image.toImage(name);
		
		Struct data = ImageGetEXIFMetadata.getData(img);
		Object value = data.get(tagName, null);
		if(value==null){
			throw new FunctionException(pc, "ImageGetEXIFTag", 2, "tagName", ExceptionUtil.similarKeyMessage(data,tagName,"tag","tags",true));
		}
		return value;
	}
	
}
