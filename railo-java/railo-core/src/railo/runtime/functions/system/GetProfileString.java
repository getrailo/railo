/**
 * Implements the CFML Function getprofilestring
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

public final class GetProfileString implements Function {
	public static String call(PageContext pc , String fileName, String section, String key) throws PageException {
        try {
        	Resource res = ResourceUtil.toResourceNotExisting(pc,fileName);
            if(!res.isFile()) return "";
            	
        	IniFile ini = new IniFile(res);
            String str=ini.getKeyValueEL(section, key);
            if(str==null) return "";
            return str;
        } 
        catch (IOException e) {
            throw Caster.toPageException(e);
        }
	}
}