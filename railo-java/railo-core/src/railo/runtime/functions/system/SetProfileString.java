/**
 * Implements the CFML Function setprofilestring
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.ini.IniFile;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class SetProfileString implements Function {
	public static String call(PageContext pc , String fileName, String section, String key, String value) throws PageException {
	    try {
	    	Resource res = ResourceUtil.toResourceNotExisting(pc,fileName);
            IniFile ini = new IniFile(res);
            ini.setKeyValue(section, key,value);
            ini.save();
        } 
        catch (IOException e) {
            throw Caster.toPageException(e);
        }
        return "";
	}
}