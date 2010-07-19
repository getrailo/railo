package railo.runtime.functions.file;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileSkipBytes {


	
	public static String call(PageContext pc, Object fileObj, double len) throws PageException {
		
		if(!(fileObj instanceof FileStreamWrapper)) 
			throw new FunctionException(pc,"FileSkipBytes",1,"fileObj",
					"invalid type ["+Caster.toTypeName(fileObj)+"], only File Object produced by FileOpen supported");
		FileStreamWrapper fs=(FileStreamWrapper) fileObj;
		fs.skip((int)len);
		return null;
		
	}
	
}
