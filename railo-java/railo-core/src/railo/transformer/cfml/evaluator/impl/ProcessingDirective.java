package railo.transformer.cfml.evaluator.impl;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
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

/**
 * Prueft den Kontext des Tag <code>catch</code>.
 * Das Tag darf sich nur direkt innerhalb des Tag <code>try</code> befinden.
 */ 
public final class ProcessingDirective extends EvaluatorSupport {
    
    public TagLib execute(Config config, Tag tag, TagLibTag libTag, FunctionLib[] flibs, Data data) throws TemplateException {

    	// dot notation
    	Boolean dotNotationUpperCase = null;
    	if(tag.containsAttribute("preservecase")) {
    		Boolean preservecase = ASMUtil.getAttributeBoolean(tag, "preservecase",null);
            if(preservecase==null)
            	throw new TemplateException(data.cfml,"attribute [preserveCase] of the tag [processingdirective] must be a constant boolean value");
            dotNotationUpperCase=preservecase.booleanValue()?Boolean.FALSE:Boolean.TRUE;
            
            if(dotNotationUpperCase==data.settings.dotNotationUpper)
            	dotNotationUpperCase=null;
            
    	}

    	// page encoding
    	Charset cs=null;
    	if(tag.containsAttribute("pageencoding")) {
    		String str=ASMUtil.getAttributeString(tag, "pageencoding",null);
            if(str==null)
            	throw new TemplateException(data.cfml,"attribute [pageencoding] of the tag [processingdirective] must be a constant value");
            
            cs = CharsetUtil.toCharset(str);
            
            if(cs.equals(data.cfml.getCharset()) || CharsetUtil.UTF8.equals(data.cfml.getCharset())) {
	        	cs=null;
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
    	
    	
    	if(cs!=null || exeLog!=null || dotNotationUpperCase!=null){
    		if(cs==null)	cs=data.cfml.getCharset();
    		if(exeLog==null)exeLog=data.cfml.getWriteLog()?Boolean.TRUE:Boolean.FALSE;
    		if(dotNotationUpperCase==null)dotNotationUpperCase=data.settings.dotNotationUpper;
	    	throw new ProcessingDirectiveException(data.cfml,cs,dotNotationUpperCase,exeLog);
    	}
    	
    	
    	return null;
	}
}