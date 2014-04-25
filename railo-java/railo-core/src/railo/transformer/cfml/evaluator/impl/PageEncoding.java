package railo.transformer.cfml.evaluator.impl;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
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
    	String str=ASMUtil.getAttributeString(tag, "charset",null);
        if(str==null)
        	throw new TemplateException(data.srcCode,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
        
        Charset cs=CharsetUtil.toCharset(str);
        if(cs.equals(data.srcCode.getCharset()) || CharsetUtil.UTF8.equals(data.srcCode.getCharset())) {
        	cs=null;
        }
        
        // 
    	
    	if(cs!=null){
    		throw new ProcessingDirectiveException(data.srcCode,cs,null,data.srcCode.getWriteLog());
    	}
    	
    	
    	return null;
	}
}