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

public final class PageEncoding extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, CFMLString cfml) throws TemplateException {
    	String encoding=ASMUtil.getAttributeString(tag, "charset",null);
        if(encoding==null)
        	throw new TemplateException(cfml,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
        
        if(encoding.equalsIgnoreCase(cfml.getCharset()) || "UTF-8".equalsIgnoreCase(cfml.getCharset())) {
        	encoding=null;
        }
    	
    	if(encoding!=null){
    		throw new ProcessingDirectiveException(cfml,encoding,cfml.getWriteLog()?Boolean.TRUE:Boolean.FALSE);
    	}
    	
    	
    	return null;
	}
}