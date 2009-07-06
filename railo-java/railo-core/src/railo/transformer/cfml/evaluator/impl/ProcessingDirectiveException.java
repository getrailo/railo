package railo.transformer.cfml.evaluator.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.util.CFMLString;

public final class ProcessingDirectiveException extends TemplateException {

	private String trgCharset;
	private String srcCharset;

	public ProcessingDirectiveException(CFMLString cfml, String charset) {
		super(cfml, "change charset from ["+cfml.getCharset()+"] to ["+charset+"]");
		this.trgCharset=charset;
		this.srcCharset=cfml.getCharset();
	}

	public String getTargetCharset() {
		return trgCharset;
	}

	public String getSourceCharset() {
		return srcCharset;
	}

}
