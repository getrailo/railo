package railo.transformer.cfml.evaluator.impl;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Prüft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */
public final class ProcessingDirective extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, CFMLString cfml) throws TemplateException {
    	//print.ln("im here");
    	if(tag.containsAttribute("pageencoding")) {
            String encoding=ASMUtil.getAttributeString(tag, "pageencoding",null);
            if(encoding==null)
            	throw new TemplateException(cfml,"attribute [pageencoding] must be a constant value");
            
            if(!encoding.equalsIgnoreCase(cfml.getCharset()) && !"UTF-8".equalsIgnoreCase(cfml.getCharset())) {
            	throw new ProcessingDirectiveException(cfml,encoding);
            }
            // TODO remove supresswhitespace wenn 
        }
    	return null;	
	}
}