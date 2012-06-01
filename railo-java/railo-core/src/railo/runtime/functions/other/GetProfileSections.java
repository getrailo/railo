/**
 * Implements the CFML Function getprofilesections
 */
package railo.runtime.functions.other;

import java.io.IOException;

import railo.commons.io.ini.IniFile;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class GetProfileSections implements Function {
	public static railo.runtime.type.Struct call(PageContext pc , String fileName) throws PageException {
		try {
            return IniFile.getProfileSections(ResourceUtil.toResourceExisting(pc,fileName));
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
	}
	
	 
}