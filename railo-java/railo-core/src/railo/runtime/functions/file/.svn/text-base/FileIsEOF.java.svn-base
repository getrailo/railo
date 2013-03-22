package railo.runtime.functions.file;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileIsEOF {

	public static boolean call(PageContext pc, Object fileObj) throws PageException {
		if(!(fileObj instanceof FileStreamWrapper)) 
			throw new FunctionException(pc,"FileIsEOF",1,"fileObj",
					"invalid type ["+Caster.toTypeName(fileObj)+"], only File Object produced by FileOpen supported");
		return ((FileStreamWrapper)fileObj).isEndOfFile();
	}
}
