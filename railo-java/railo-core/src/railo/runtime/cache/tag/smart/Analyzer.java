package railo.runtime.cache.tag.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerImpl;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public class Analyzer {
	
	

    public synchronized static Query analyze(int type) throws ApplicationException {
    	Query qry=new QueryImpl(
    			new String[]{
    					"name","entryhash","resulthash","template",
    					"line","type","meta","dependency",
    					"totalExecutionTime","executionTimes","createTimes","timeUnchanged","calls","payload"}, 0/*TODO counter.toInt()*/, "query");
    	if(type==ConfigImpl.CACHE_DEFAULT_INCLUDE)
    		analyze(qry, type);
    	else if(type==ConfigImpl.CACHE_DEFAULT_QUERY)
    		analyze(qry, type);
    	else if(type==ConfigImpl.CACHE_DEFAULT_FUNCTION)
    		analyze(qry, type);
    	else {
    		analyze(qry, ConfigImpl.CACHE_DEFAULT_INCLUDE);
    		analyze(qry, ConfigImpl.CACHE_DEFAULT_QUERY);
    		analyze(qry, ConfigImpl.CACHE_DEFAULT_FUNCTION);
    	}
    	
    	return qry;
    }

    private static void analyze(Query qry,int type) throws ApplicationException {
    	
    	
    	
		// TODO input boundries
    	RefInteger counter=new RefIntegerImpl(0);
    	Map<String, SmartEntry> entries;
    	if(type==ConfigImpl.CACHE_DEFAULT_INCLUDE)
    		entries=CacheHandlerFactory.include.getSmartCacheHandler().getEntries();
    	else if(type==ConfigImpl.CACHE_DEFAULT_QUERY)
    		entries=CacheHandlerFactory.query.getSmartCacheHandler().getEntries();
    	else if(type==ConfigImpl.CACHE_DEFAULT_FUNCTION)
    		entries=CacheHandlerFactory.function.getSmartCacheHandler().getEntries();
    	else
    		throw new ApplicationException("invalid type defintion");
    	
    	//Map<String, SmartEntry> entries = SmartCacheHandler.entries;
    	Map<String,Entry> data=new HashMap<String, Analyzer.Entry>();
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
    	int row;//=0;
    	Iterator<Map.Entry<String, Analyzer.Entry>> it = data.entrySet().iterator();
    	Map.Entry<String, Analyzer.Entry> e;
    	String eh,rh;
    	Analyzer.Entry entry;
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
    			
    			row=qry.addRow();//row++;
    			qry.setAtEL("entryhash", row, eh);
    			qry.setAtEL("resulthash", row, rh);
    			qry.setAtEL("name", row, entry.name);
    			qry.setAtEL("template", row, entry.template);
    			qry.setAtEL("line", row, entry.line);
    			qry.setAtEL("type", row, entry.type);
    			qry.setAtEL("meta", row, entry.meta);
    			qry.setAtEL("dependency", row, dependency(res));
    			qry.setAtEL("totalExecutionTime", row, res.totalExecution);
    			qry.setAtEL("executionTimes", row, toArray(res.executionTimes));
    			qry.setAtEL("createTimes", row, toArray(res.createTimes));
    			qry.setAtEL("timeUnchanged", row, res.lastExecution-res.firstExecution);
    			qry.setAtEL("calls", row, res.executionTimes.size());
    			qry.setAtEL("payload", row, Caster.toDouble(res.payLoad));
    			

    		}
    	}
	}
	
	

	private static Array toArray(List list) {
		ArrayImpl arr = new ArrayImpl();
		Iterator it = list.iterator();
		while(it.hasNext()){
			arr.add(it.next());
		}
		return arr;
	}



	private static String dependency(Result res) {
		if(res.cfids.size()==1) return "session";
		if(res.applications.size()==1) return "application";
		return "independent";
	}
	
	
	static class Entry {
		
		public final Map<String,Result> data=new HashMap<String, Analyzer.Result>();
    	
		public final String name; 
		public final String meta; 
		public final String type; 
		public final String template; 
		public final int line;

		public Entry(SmartEntry se, RefInteger counter) {
			counter.plus(1);
			add(se);
			
			
			// Meta
			name=se.getName();
			meta=se.getMeta();
			type=se.getType();
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
		public final long payLoad;
		
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
}
