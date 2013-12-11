package railo.runtime.functions.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerImpl;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.smart.SmartCacheHandler;
import railo.runtime.cache.tag.smart.SmartEntry;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.other.AnalyzeSmartEntries.Result;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public class AnalyzeSmartEntries extends BIF {
	

    public synchronized static Object call(PageContext pc ) {
		// TODO input boundries
    	RefInteger counter=new RefIntegerImpl(0);
    	Map<String, SmartEntry> entries = SmartCacheHandler.entries;
    	Map<String,Entry> data=new HashMap<String, AnalyzeSmartEntries.Entry>();
    	{
	    	Iterator<SmartEntry> it = entries.values().iterator();
	    	SmartEntry se;
	    	
	    	// create entries
	    	Entry entry;
	    	while(it.hasNext()){
	    		se = it.next();
	    		entry=data.get(se.getEntryHash());
	    		if(entry!=null) {
	    			entry.add(se);
	    		}
	    		else data.put(se.getEntryHash(), entry=new Entry(se,counter));
	    	}
    	}
    	Query qry=new QueryImpl(
    			new String[]{
    					"entryhash","resulthash","template",
    					"line","typeid","meta","dependency",
    					"totalExecutionTime","timeUnchanged","count"}, counter.toInt(), "query");
    	int row=0;
    	Iterator<Map.Entry<String, railo.runtime.functions.other.AnalyzeSmartEntries.Entry>> it = data.entrySet().iterator();
    	Map.Entry<String, railo.runtime.functions.other.AnalyzeSmartEntries.Entry> e;
    	String eh,rh;
    	railo.runtime.functions.other.AnalyzeSmartEntries.Entry entry;
    	Iterator<Map.Entry<String, Result>> iit;
    	Map.Entry<String, Result> ee;
    	Result res;
    	while(it.hasNext()){
    		e = it.next();
    		eh=e.getKey();
    		entry = e.getValue();
    		iit = entry.data.entrySet().iterator();
    		while(iit.hasNext()){
    			ee = iit.next();
    			rh = ee.getKey();
    			res = ee.getValue();
    			
    			row++;
    			qry.setAtEL("entryhash", row, eh);
    			qry.setAtEL("resulthash", row, rh);
    			qry.setAtEL("template", row, entry.template);
    			qry.setAtEL("line", row, entry.line);
    			qry.setAtEL("typeid", row, entry.typeId);
    			qry.setAtEL("meta", row, entry.meta);
    			qry.setAtEL("dependency", row, dependency(res));
    			qry.setAtEL("totalExecutionTime", row, res.totalExecution);
    			qry.setAtEL("timeUnchanged", row, res.lastExecution-res.firstExecution);
    			qry.setAtEL("count", row, res.executionTimes.size());
    			
    			
    		}
    	}
    	
    	
    	return qry;
	}
	
	

	private static String dependency(Result res) {
		if(res.cfids.size()==1) return "session";
		if(res.applications.size()==1) return "application";
		return "independent";
	}
	
	
	static class Entry {
		
		public final Map<String,Result> data=new HashMap<String, AnalyzeSmartEntries.Result>();
    	
		public final String name; 
		public final String meta; 
		public final String typeId; 
		public final String template; 
		public final int line;

		public Entry(SmartEntry se, RefInteger counter) {
			counter.plus(1);
			add(se);
			
			
			// Meta
			name=se.getName();
			meta=se.getMeta();
			typeId=se.getTypeId();
			template=se.getTemplate();
			line=se.getLine();
		}

		public void add(SmartEntry se) {
			
			Result res = data.get(se.getResultHash());
    		if(res!=null) {
    			res.add(se);
    		}
    		else data.put(se.getResultHash(), res=new Result(se));
    		
    		
		}
		
	}
	
	static class Result {
		
		public final Set<String> applications=new HashSet<String>();
		public final List<DateTime> createTimes=new ArrayList<DateTime>();
		public final List<Long> executionTimes=new ArrayList<Long>();
		public final Set<String> cfids=new HashSet<String>();
		public final int payLoad;
		
		private long totalExecution=0;
		private long firstExecution=Long.MAX_VALUE;
		private long lastExecution=Long.MIN_VALUE;
		
		
		public Result(SmartEntry se) {
			add(se);
			payLoad=se.getPayLoad();
		}

		public void add(SmartEntry se) {
			applications.add(se.getApplicationName());
			createTimes.add(new DateTimeImpl(se.getCreateTime(),false));
			if(firstExecution>se.getCreateTime())	firstExecution=se.getCreateTime();
			if(lastExecution<se.getCreateTime())	lastExecution=se.getCreateTime();
			executionTimes.add(new Long(se.getExecutionTime()));
			totalExecution+=se.getExecutionTime();
			cfids.add(se.getCfid());
			
		}
		
	}
	
	

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc);
	}

}
