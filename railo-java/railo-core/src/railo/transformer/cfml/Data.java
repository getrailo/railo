package railo.transformer.cfml;

import railo.runtime.config.Config;
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
		
		
	    public Data(Page page,CFMLString cfml,EvaluatorPool ep,TransfomerSettings settings,FunctionLib[] flibs,TagLibTag[] scriptTags) {
	    	this.config = page.getConfig();
	    	this.page = page;
	    	this.cfml = cfml;
	    	this.settings = settings;
			this.flibs = flibs;
			this.scriptTags = scriptTags;
			this.ep = ep;
		}
	}