package railo.transformer.cfml.evaluator.impl;

import railo.print;
import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Prueft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */ 
public final class ProcessingDirective extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, Data data) throws TemplateException {

    	// dot notation
    	Boolean dotNotationUpperCase = null;
    	if(tag.containsAttribute("dotnotationuppercase")) {
            dotNotationUpperCase = ASMUtil.getAttributeBoolean(tag, "dotnotationuppercase",null);
            if(dotNotationUpperCase==null)
            	throw new TemplateException(data.cfml,"attribute [dotnotationuppercase] of the tag [processingdirective] must be a constant boolean value");
            if(dotNotationUpperCase==data.settings.dotNotationUpper)
            	dotNotationUpperCase=null;
            
    	}

    	// page encoding
    	String encoding=null;
    	if(tag.containsAttribute("pageencoding")) {
            encoding=ASMUtil.getAttributeString(tag, "pageencoding",null);
            if(encoding==null)
            	throw new TemplateException(data.cfml,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
            
            if(encoding.equalsIgnoreCase(data.cfml.getCharset()) || "UTF-8".equalsIgnoreCase(data.cfml.getCharset())) {
	        	encoding=null;
	        }
        }

    	// execution log
    	Boolean exeLog=null;
    	if(tag.containsAttribute("executionlog")) {
    		String strExeLog=ASMUtil.getAttributeString(tag, "executionlog",null);
            exeLog=Caster.toBoolean(strExeLog,null);
            if(exeLog==null)
            	throw new TemplateException(data.cfml,"attribute [executionlog] of the tag [processingdirective] must be a constant boolean value");
            if(exeLog.booleanValue()==data.cfml.getWriteLog())
            	exeLog=null;
        }
    	
    	
    	if(encoding!=null || exeLog!=null || dotNotationUpperCase!=null){
    		if(encoding==null)	encoding=data.cfml.getCharset();
    		if(exeLog==null)exeLog=data.cfml.getWriteLog()?Boolean.TRUE:Boolean.FALSE;
    		if(dotNotationUpperCase==null)dotNotationUpperCase=data.settings.dotNotationUpper;
	    	throw new ProcessingDirectiveException(data.cfml,encoding,dotNotationUpperCase,exeLog);
    	}
    	
    	
    	return null;
	}
}