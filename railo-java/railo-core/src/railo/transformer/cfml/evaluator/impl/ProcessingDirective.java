package railo.transformer.cfml.evaluator.impl;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Pr¸ft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */ 
public final class ProcessingDirective extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, CFMLString cfml) throws TemplateException {
    	String encoding=null;
    	Boolean exeLog=null;
    	if(tag.containsAttribute("pageencoding")) {
            encoding=ASMUtil.getAttributeString(tag, "pageencoding",null);
            if(encoding==null)
            	throw new TemplateException(cfml,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
            
            if(encoding.equalsIgnoreCase(cfml.getCharset()) || "UTF-8".equalsIgnoreCase(cfml.getCharset())) {
	        	encoding=null;
	        }
        }
    	if(tag.containsAttribute("executionlog")) {
    		String strExeLog=ASMUtil.getAttributeString(tag, "executionlog",null);
            exeLog=Caster.toBoolean(strExeLog,null);
            if(exeLog==null)
            	throw new TemplateException(cfml,"attribute [executionlog] of the tag [processingdirective] must be a constant boolean value");
            if(exeLog.booleanValue()==cfml.getWriteLog())
            	exeLog=null;
        }
    	
    	if(encoding!=null || exeLog!=null){
    		if(encoding==null)	encoding=cfml.getCharset();
    		if(exeLog==null)exeLog=cfml.getWriteLog()?Boolean.TRUE:Boolean.FALSE;
	    	throw new ProcessingDirectiveException(cfml,encoding,exeLog);
    	}
    	
    	
    	return null;	//ç
	}
}