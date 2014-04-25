package railo.transformer.cfml;

import railo.runtime.config.Config;
import railo.transformer.Factory;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.SourceCode;

public abstract class Data {
	
		public final SourceCode srcCode;
		public final TransfomerSettings settings; 
		public final TagLib[][] tlibs;
		public final FunctionLib[] flibs;
		public final Config config;
		public final Page page;
		public final TagLibTag[] scriptTags;
		public final EvaluatorPool ep;
		public final Factory factory;
		
	    public Data(Factory factory,Page page,SourceCode cfml,EvaluatorPool ep,TransfomerSettings settings,TagLib[][] tlibs,FunctionLib[] flibs,TagLibTag[] scriptTags) {

	    	this.config = page.getPageSource().getMapping().getConfig();
	    	this.page = page;
	    	this.srcCode = cfml;
	    	this.settings = settings;
	    	this.tlibs = tlibs;
	    	this.flibs = flibs;
			this.scriptTags = scriptTags;
			this.ep = ep;
			this.factory = factory;
		}
	    
	    
	}