package railo.transformer.cfml.evaluator.impl;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.TemplateException;
import railo.transformer.util.CFMLString;

public final class ProcessingDirectiveException extends TemplateException {

	private String charset;
	private boolean writeLog;

	public ProcessingDirectiveException(CFMLString cfml, String charset, boolean writeLog) {
		super(cfml, createMessage(cfml,charset,writeLog));
		this.charset=charset;
		this.writeLog=writeLog;
		
	}

	private static String createMessage(CFMLString cfml, String charset,boolean writeLog) {
		StringBuffer msg=new StringBuffer();
		if(!(cfml.getCharset()+"").equalsIgnoreCase(charset))
			msg.append("change charset from ["+cfml.getCharset()+"] to ["+charset+"].");
		
		if(cfml.getWriteLog()!=writeLog)
			msg.append("change writelog from ["+cfml.getWriteLog()+"] to ["+writeLog+"].");
		
		return msg.toString();
	}

	public String getCharset() {
		return charset;
	}

	public boolean getWriteLog() {
		return writeLog;
	}

}
