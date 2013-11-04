package railo.transformer.cfml.evaluator.impl;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;

public final class PageEncoding extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, Data data) throws TemplateException {
    	
    	// encoding
    	String encoding=ASMUtil.getAttributeString(tag, "charset",null);
        if(encoding==null)
        	throw new TemplateException(data.cfml,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
        
        if(encoding.equalsIgnoreCase(data.cfml.getCharset()) || "UTF-8".equalsIgnoreCase(data.cfml.getCharset())) {
        	encoding=null;
        }
        
        // 
    	
    	if(encoding!=null){
    		throw new ProcessingDirectiveException(data.cfml,encoding,null,data.cfml.getWriteLog());
    	}
    	
    	
    	return null;
	}
}