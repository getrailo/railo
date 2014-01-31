package railo.runtime.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.Util;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.smart.Analyzer;
import railo.runtime.cache.tag.smart.SmartCacheHandler;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagSupport;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeSpan;

// MUST change behavior of multiple headers now is a array, it das so?

/**
* Lets you execute HTTP POST and GET operations on files. Using cfhttp, you can execute standard 
*   GET operations and create a query object from a text file. POST operations lets you upload MIME file 
*   types to a server, or post cookie, formfield, URL, file, or CGI variables directly to a specified server.
*
*
*
* 
**/
public final class SmartCache extends TagSupport {
	
	private static final short ACTION_NONE=0;
	private static final short ACTION_ANALYZE=1;
	private static final short ACTION_SET_RULE=2;
	private static final short ACTION_REMOVE_RULE=4;
	private static final short ACTION_CLEAR_RULES=8;
	private static final short ACTION_GET_RULES=16;
	private static final short ACTION_INFO=32;
	private static final short ACTION_START=64;
	private static final short ACTION_STOP=128;
	
	
	private String returnVariable="smart";
	private short action=ACTION_ANALYZE;


	private Object entryHash;
	private Object timespan;
	private int type=ConfigImpl.CACHE_DEFAULT_NONE;
	
	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		returnVariable="smart";
		action=ACTION_ANALYZE;
		entryHash=null;
		timespan=null;
		type=ConfigImpl.CACHE_DEFAULT_NONE;
	}

	public void setAction(String strAction) throws ApplicationException {
		if(Util.isEmpty(strAction,true)) return;
		action=toAction(strAction, ACTION_NONE);
		if(action==ACTION_NONE)
			throw new ApplicationException("invalid action ["+strAction+"], valid actions are [analyze, setRule, removeRule, clearRules, getRules, info, start, stop]"); 
		
	}
	
	public void setType(String strType) throws ApplicationException {
		if(Util.isEmpty(strType,true)) return;
		strType=strType.trim().toLowerCase();
		if(strType.equals("function"))		type=ConfigImpl.CACHE_DEFAULT_FUNCTION;
		else if(strType.equals("include"))	type=ConfigImpl.CACHE_DEFAULT_INCLUDE;
		else if(strType.equals("query"))	type=ConfigImpl.CACHE_DEFAULT_QUERY;
		else
			throw new ApplicationException("invalid type ["+strType+"], valid types are [function, include, template]"); 
		
	}

	public void setReturnvariable(String var) {
		if(Util.isEmpty(var,true)) return;
		returnVariable=var.trim();
		
	}
	
	public void setEntryhash(Object entryHash) {
		this.entryHash=entryHash;
		
	}
	public void setTimespan(Object timespan) {
		this.timespan=timespan;
	}


	@Override
	public int doStartTag() throws PageException {
		if(action==ACTION_ANALYZE) doAnalyze();
		else if(action==ACTION_SET_RULE) doSetRule();
		else if(action==ACTION_REMOVE_RULE) doRemoveRule();
		else if(action==ACTION_CLEAR_RULES) doClearRules();
		else if(action==ACTION_GET_RULES) doGetRules();
		else if(action==ACTION_INFO) doInfo();
		else if(action==ACTION_START) doStart();
		else if(action==ACTION_STOP) doStop();
		
		// START
		return SKIP_BODY;
	}

	private void doInfo() throws PageException {
		pageContext.setVariable(returnVariable, SmartCacheHandler.info(type));
	}

	private void doGetRules() throws PageException {
		pageContext.setVariable(returnVariable, SmartCacheHandler.getRules(type));
	}

	private void doStart() {
		SmartCacheHandler.start();
	}
	private void doStop() {
		SmartCacheHandler.stop();
	}

	private void doClearRules() throws PageException {
		typeRequired();
		SmartCacheHandler.clearRules(type);
	}

	private void doRemoveRule() throws PageException {
		typeRequired();
		String[] hashes=getEntryHashes();
		
		for(int i=0;i<hashes.length;i++){
			SmartCacheHandler.removeRule(type,hashes[i]);
		}
	}


	private void doSetRule() throws PageException {
		typeRequired();
		Pair[] pairs=getPairs();
		for(int i=0;i<pairs.length;i++){
			SmartCacheHandler.setRule(pageContext,type,pairs[i].entryHash,pairs[i].timespan);
		}
	}

	private void doAnalyze() throws PageException {
		//typeRequired();
		pageContext.setVariable(returnVariable, Analyzer.analyze(type));
	}
	

	private void typeRequired() throws PageException {
		if(type==ConfigImpl.CACHE_DEFAULT_NONE)
			required("smartcache", toAction(action,null), "type", null);
			//throw new ApplicationException("attribute type is required for tag smartcache with this action");
	}
	
	private String toAction(short action, String defaultValue) {
		switch(action){
			case ACTION_ANALYZE: return "analyze";
			case ACTION_SET_RULE: return "setrule";
			case ACTION_CLEAR_RULES: return "clearrules";
			case ACTION_REMOVE_RULE: return "removerule";
			case ACTION_GET_RULES: return "getrules";
			case ACTION_INFO: return "info";
			case ACTION_START: return "start";
			case ACTION_STOP: return "stop";
		}
		return defaultValue;
	}
	
	private short toAction(String strAction, short defaultValue) {
		strAction=strAction.trim().toLowerCase();
		if(strAction.equals("analyze")) return ACTION_ANALYZE;
		else if(strAction.equals("setrule")) return ACTION_SET_RULE;
		else if(strAction.equals("clearrules")) return ACTION_CLEAR_RULES;
		else if(strAction.equals("removerule")) return ACTION_REMOVE_RULE;
		else if(strAction.equals("getrules")) return ACTION_GET_RULES;
		else if(strAction.equals("info")) return ACTION_INFO;
		else if(strAction.equals("start")) return ACTION_START;
		else if(strAction.equals("stop")) return ACTION_STOP;
		else return defaultValue;
	}

	private static class Pair{

		private TimeSpan timespan;
		private String entryHash;

		public Pair(String entryHash, TimeSpan timespan) {
			this.entryHash=entryHash;
			this.timespan=timespan;
		}

		
	}
	
	
	private Pair[] getPairs() throws PageException {
		Pair[] pairs;
		if(Decision.isArray(entryHash)) {
			Object[] naEntryHash = Caster.toNativeArray(entryHash);
			Object[] naTimspan = Caster.toNativeArray(timespan);
			if(naEntryHash.length!=naTimspan.length)
				throw new ApplicationException("entry hash array and timespan array has not the same length.");
			
			pairs=new Pair[naEntryHash.length];
			for(int i=0;i<naEntryHash.length;i++){
				
				pairs[i]=new Pair(Caster.toString(naEntryHash[i]),Caster.toTimespan(naTimspan[i]));
			}
		}
		else {
			pairs=new Pair[]{new Pair(Caster.toString(entryHash),Caster.toTimespan(timespan))};
			
		}
		return pairs;
	}
	
	private String[] getEntryHashes() throws PageException {
		String[] hashes;
		if(Decision.isArray(entryHash)) {
			Object[] naEntryHash = Caster.toNativeArray(entryHash);
			
			hashes=new String[naEntryHash.length];
			for(int i=0;i<naEntryHash.length;i++){
				hashes[i]=Caster.toString(naEntryHash[i]);
			}
		}
		else {
			hashes=new String[]{Caster.toString(entryHash)};
			
		}
		return hashes;
	}

}