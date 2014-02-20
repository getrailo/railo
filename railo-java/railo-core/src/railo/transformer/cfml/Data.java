package railo.transformer.cfml;

import railo.runtime.config.Config;
import railo.transformer.Factory;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public abstract class Data {
	
		public final CFMLString cfml;
		public final TransfomerSettings settings; 
		public final FunctionLib[] flibs;
		public final Config config;
		public final Page page;
		public final TagLibTag[] scriptTags;
		public final EvaluatorPool ep;
		public final Factory factory;
		
		
	    public Data(Factory factory,Page page,CFMLString cfml,EvaluatorPool ep,TransfomerSettings settings,FunctionLib[] flibs,TagLibTag[] scriptTags) {
	    	this.config = page.getPageSource().getMapping().getConfig();
	    	this.page = page;
	    	this.cfml = cfml;
	    	this.settings = settings;
			this.flibs = flibs;
			this.scriptTags = scriptTags;
			this.ep = ep;
			this.factory = factory;
		}
	    
	    
	}