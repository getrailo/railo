package railo.transformer.cfml.evaluator.impl;

import java.nio.charset.Charset;

import railo.runtime.exp.TemplateException;
import railo.transformer.util.SourceCode;

public final class ProcessingDirectiveException extends TemplateException {

	private Charset charset;
	private Boolean writeLog;
	private Boolean dotNotationUpperCase;

	public ProcessingDirectiveException(SourceCode cfml, Charset charset,Boolean dotNotationUpperCase, Boolean writeLog) {
		super(cfml, createMessage(cfml,charset,writeLog));
		this.charset=charset;
		this.writeLog=writeLog;
		this.dotNotationUpperCase=dotNotationUpperCase;
	}

	private static String createMessage(SourceCode cfml, Charset charset,boolean writeLog) {
		StringBuffer msg=new StringBuffer();
		if(!cfml.getCharset().equals(charset))
			msg.append("change charset from ["+cfml.getCharset()+"] to ["+charset+"].");
		
		if(cfml.getWriteLog()!=writeLog)
			msg.append("change writelog from ["+cfml.getWriteLog()+"] to ["+writeLog+"].");
		
		return msg.toString();
	}

	public Charset getCharset() {
		return charset;
	}

	public Boolean getDotNotationUpperCase() {
		return dotNotationUpperCase;
	}
	public Boolean getWriteLog() {
		return writeLog;
	}

}
