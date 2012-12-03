package railo.runtime.text.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class SimpleErrorListener implements ErrorListener {

	public static final ErrorListener THROW_FATAL = new SimpleErrorListener(false,true,true);
	public static final ErrorListener THROW_ERROR = new SimpleErrorListener(false,false,true);
	public static final ErrorListener THROW_WARNING = new SimpleErrorListener(false,false,false);
	private boolean ignoreFatal;
	private boolean ignoreError;
	private boolean ignoreWarning;

	public SimpleErrorListener(boolean ignoreFatal, boolean ignoreError, boolean ignoreWarning){
		this.ignoreFatal=ignoreFatal;
		this.ignoreError=ignoreError;
		this.ignoreWarning=ignoreWarning;
	}
	
	
	@Override
	public void error(TransformerException te) throws TransformerException {
		if(!ignoreError) throw te;
	}

	@Override
	public void fatalError(TransformerException te) throws TransformerException {
		if(!ignoreFatal) throw te;
	}

	@Override
	public void warning(TransformerException te) throws TransformerException {
		if(!ignoreWarning) throw te;
	}

}
