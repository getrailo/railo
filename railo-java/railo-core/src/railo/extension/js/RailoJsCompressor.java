package railo.extension.js;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.util.ResourceUtil;

public class RailoJsCompressor {
	
	private int linebreak;
	private boolean munge;
	private boolean verbose;
	private boolean preserveAllSemiColons;
	private boolean disableOptimizations;

	public RailoJsCompressor(int linebreak, boolean munge, boolean verbose, boolean preserveAllSemiColons, boolean disableOptimizations){
		this.linebreak=linebreak;
		this.munge=munge;
		this.verbose=verbose;
		this.preserveAllSemiColons=preserveAllSemiColons;
		this.disableOptimizations=disableOptimizations;
	}
	
	public void compress(String source, String destination) throws PageException, IOException{
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		PageContext pc = engine.getThreadPageContext();
		ResourceUtil resUtil = engine.getResourceUtil();
		
		Resource src = resUtil.toResourceExisting(pc, source);
		Resource dest = resUtil.toResourceNotExisting(pc, destination);

		InputStreamReader isr=null;
		OutputStreamWriter osw=null;
		try	{
			isr=new InputStreamReader(src.getInputStream());
			osw=new OutputStreamWriter(dest.getOutputStream());
			
			JavaScriptCompressor compressor=new JavaScriptCompressor(isr, new ErrorReporterImpl());
			compressor.compress(osw, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
		}
		finally {
			Util.closeEL(isr);
			Util.closeEL(osw);
		}
	}

}
