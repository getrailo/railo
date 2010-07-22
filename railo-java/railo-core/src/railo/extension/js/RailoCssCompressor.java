package railo.extension.js;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.yahoo.platform.yui.compressor.CssCompressor;

import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.util.ResourceUtil;

public class RailoCssCompressor {
	
	private int linebreak;
	
	public RailoCssCompressor(int linebreak){
		 this.linebreak = linebreak;
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
			
			CssCompressor cssComp = new CssCompressor(isr);
			cssComp.compress(osw, linebreak);	
		}
		finally {
			Util.closeEL(isr);
			Util.closeEL(osw);
		}
		
	}

}
