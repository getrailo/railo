/**
 * Implements the Cold Fusion Function getbasetemplatepath
 */
package railo.runtime.functions.system;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class GetBaseTemplatePath implements Function {
	public static String call(PageContext pc ) throws ExpressionException {
	    return ResourceUtil.getResource(pc, pc.getBasePageSource()).getAbsolutePath();
		/*
	    PageSource ps = pc.getBasePageSource();
        Resource file = ps.getPhyscalFile();
        if(ResourceUtil.exists(file)) return file.getAbsolutePath();
        
        return ps.getDisplayPath();
        */
	}
}