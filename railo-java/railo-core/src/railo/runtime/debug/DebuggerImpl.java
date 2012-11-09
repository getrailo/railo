package railo.runtime.debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import railo.commons.io.SystemUtil;
import railo.commons.io.log.LogUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.SQL;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.DebugQueryColumn;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;


/**
 * Class to debug the application
 */
public final class DebuggerImpl implements Debugger {
	private static final long serialVersionUID = 3957043879267494311L;
	
	
	private static final Collection.Key QUERIES = KeyImpl.intern("queries");
	private static final Collection.Key IMPLICIT_ACCESS= KeyImpl.intern("implicitAccess");
	private static final Collection.Key PAGE_PARTS= KeyImpl.intern("pageParts");
	//private static final Collection.Key OUTPUT_LOG= KeyImpl.intern("outputLog");
	
	


	private static final int MAX_PARTS = 100;

	private Map<String,DebugEntryTemplateImpl> entries=new HashMap<String,DebugEntryTemplateImpl>();
	private Map<String,DebugEntryTemplatePartImpl> partEntries;
	private List<QueryEntry> queries=new ArrayList<QueryEntry>();
	private List<DebugTimerImpl> timers=new ArrayList<DebugTimerImpl>();
	private List<DebugTraceImpl> traces=new ArrayList<DebugTraceImpl>();
	private List<CatchBlock> exceptions=new ArrayList<CatchBlock>();
	private Map<String,ImplicitAccessImpl> implicitAccesses=new HashMap<String,ImplicitAccessImpl>();
	
	private boolean output=true;
	private long lastEntry;
	private long lastTrace;
	private Array historyId=new ArrayImpl();
	private Array historyLevel=new ArrayImpl();

	private DateTimeImpl starttime;


	private DebugOutputLog outputLog;

	@Override
	public void reset() {
		entries.clear();
		if(partEntries!=null)partEntries.clear();
		queries.clear();
		implicitAccesses.clear();
		timers.clear();
		traces.clear();
		exceptions.clear();
		historyId.clear();
		historyLevel.clear();
		output=true;
		outputLog=null;
	}

	public DebuggerImpl() {	
	}

	@Override
	public DebugEntryTemplate getEntry(PageContext pc,PageSource source) {
        return getEntry(pc,source,null);
    }

	@Override
	public DebugEntryTemplate getEntry(PageContext pc,PageSource source, String key) {
    	lastEntry = System.currentTimeMillis();
        String src=DebugEntryTemplateImpl.getSrc(source==null?"":source.getDisplayPath(),key);
        
        DebugEntryTemplateImpl de= entries.get(src);
        if(de!=null ){
            de.countPP();
			historyId.appendEL(de.getId());
			historyLevel.appendEL(Caster.toInteger(pc.getCurrentLevel()));
            return de;
        }
        de=new DebugEntryTemplateImpl(source,key);
        entries.put(src,de);
		historyId.appendEL(de.getId());
		historyLevel.appendEL(Caster.toInteger(pc.getCurrentLevel()));
        return de;
    }
	


	@Override
	public DebugEntryTemplatePart getEntry(PageContext pc,PageSource source, int startPos, int endPos) {
    	String src=DebugEntryTemplatePartImpl.getSrc(source==null?"":source.getDisplayPath(),startPos,endPos);
    	DebugEntryTemplatePartImpl de=null;
    	if(partEntries!=null){
    		de=partEntries.get(src);
    		if(de!=null ){
	            de.countPP();
	            return de;
	        }
    	}
    	else {
    		partEntries=new HashMap<String, DebugEntryTemplatePartImpl>();
    	}
        de=new DebugEntryTemplatePartImpl(source,startPos,endPos);
        partEntries.put(src,de);
        return de;
    }

	private ArrayList<DebugEntryTemplate> toArray() {
        ArrayList<DebugEntryTemplate> arrPages=new ArrayList<DebugEntryTemplate>(entries.size());
        Iterator<String> it = entries.keySet().iterator();
        while(it.hasNext()) {
            DebugEntryTemplate page =entries.get(it.next());
            page.resetQueryTime();
            arrPages.add(page);
            
        }
        Collections.sort(arrPages,new DebugEntryTemplateComparator());
        

        // Queries
        int len=queries.size();
        for(int i=0;i<len;i++) {
            QueryEntry entry=queries.get(i);
            String path=entry.getSrc();
            Object o=entries.get(path);
            
            if(o!=null) {
                DebugEntryTemplate oe=(DebugEntryTemplate) o;
                oe.updateQueryTime(entry.getExe());
            }
        }
        
        return arrPages;
    }

	/*private DumpData _toDumpData(int value) {
        return new SimpleDumpData(_toString(value));
    }
	private DumpData _toDumpData(long value) {
        return new SimpleDumpData(_toString(value));
    }*/
	
	private String _toString(long value) {
        if(value<=0) return "0";
        return String.valueOf(value);
    }
	private String _toString(int value) {
        if(value<=0) return "0";
        return String.valueOf(value);
    }
	
	@Override
	public void addQuery(Query query,String datasource,String name,SQL sql, int recordcount, PageSource src,int time) {
		queries.add(new QueryEntryImpl(query,datasource,name,sql,recordcount,src.getDisplayPath(),time));
	}
	
	@Override
	public void setOutput(boolean output) {
		this.output = output;
	}
    
	@Override
	public List<QueryEntry> getQueries() {
        return queries;
    }

	@Override
	public void writeOut(PageContext pc) throws IOException {
        //stop();
        if(!output)return;
        String addr = pc.getHttpServletRequest().getRemoteAddr();
        railo.runtime.config.DebugEntry debugEntry = ((ConfigImpl)pc.getConfig()).getDebugEntry(addr, null);
		
        // no debug File 

		if(debugEntry==null) {
		    //pc.forceWrite(pc.getConfig().getDefaultDumpWriter().toString(pc,toDumpData(pc, 9999,DumpUtil.toDumpProperties()),true)); 
		    return;
		} 
		
		Struct args=new StructImpl();
		args.setEL(KeyConstants._custom, debugEntry.getCustom());
		try {
			args.setEL(KeyConstants._debugging, pc.getDebugger().getDebuggingData(pc));
		} catch (PageException e1) {}
		
		try {
			PageSource[] arr = ((PageContextImpl)pc).getPageSources(debugEntry.getPath());
			Page p = PageSourceImpl.loadPage(pc, arr);
			pc.addPageSource(p.getPageSource(), true);
			try{
				Component cfc = pc.loadComponent(debugEntry.getFullname());
				cfc.callWithNamedValues(pc, "output", args);
			}
			finally {
				pc.removeLastPageSource(true);
			}
        } 
		catch (PageException e) {
            pc.handlePageException(e);
        }
    }

	@Override
	public Struct getDebuggingData(PageContext pc) throws DatabaseException {
    	return getDebuggingData(pc, false);
    }
    
	@Override
	public Struct getDebuggingData(PageContext pc, boolean addAddionalInfo) throws DatabaseException {
		List<QueryEntry> queries = getQueries();
	    Struct qryExe=new StructImpl();
	    ListIterator<QueryEntry> qryIt = queries.listIterator();
        Collection.Key[] cols = new Collection.Key[]{
        		KeyConstants._name,
        		KeyConstants._time,
        		KeyConstants._sql,
        		KeyConstants._src,
        		KeyConstants._count,
        		KeyConstants._datasource,
        		KeyConstants._usage};
        String[] types = new String[]{"VARCHAR","DOUBLE","VARCHAR","VARCHAR","DOUBLE","VARCHAR","ANY"};
        
        //queries
        Query qryQueries=null;
        try {
            qryQueries = new QueryImpl(cols,types,queries.size(),"query");
        } catch (DatabaseException e) {
            qryQueries = new QueryImpl(cols,queries.size(),"query");
        }
		int row=0;
		try {
		    while(qryIt.hasNext()) {
		        row++;
		        QueryEntry qe=qryIt.next();
				qryQueries.setAt(KeyConstants._name,row,qe.getName()==null?"":qe.getName());
		        qryQueries.setAt(KeyConstants._time,row,Integer.valueOf(qe.getExe()));
		        qryQueries.setAt(KeyConstants._sql,row,qe.getSQL().toString());
				qryQueries.setAt(KeyConstants._src,row,qe.getSrc());
                qryQueries.setAt(KeyConstants._count,row,Integer.valueOf(qe.getRecordcount()));
                qryQueries.setAt(KeyConstants._datasource,row,qe.getDatasource());
                
                Struct usage = getUsage(qe);
                if(usage!=null) qryQueries.setAt(KeyConstants._usage,row,usage);
                
                
                
		        Object o=qryExe.get(KeyImpl.init(qe.getSrc()),null);
		        if(o==null) qryExe.setEL(KeyImpl.init(qe.getSrc()),Integer.valueOf(qe.getExe()));
		        else qryExe.setEL(KeyImpl.init(qe.getSrc()),Integer.valueOf(((Integer)o).intValue()+qe.getExe()));
		    }
		}
		catch(PageException dbe) {}
		
	    // Pages
	    // src,load,app,query,total
	    Struct debugging=new StructImpl();
	    row=0;
        ArrayList<DebugEntryTemplate> arrPages = toArray();
		int len=arrPages.size();
        Query qryPage=new QueryImpl(
                new Collection.Key[]{
                		KeyConstants._id,
                		KeyConstants._count,
                		KeyConstants._min,
                		KeyConstants._max,
                		KeyConstants._avg
                		,KeyConstants._app,
                		KeyConstants._load,
                		KeyConstants._query,
                		KeyConstants._total,
                		KeyConstants._src},
                len,"query");

		try {
            DebugEntryTemplate de;
            //PageSource ps;
		    for(int i=0;i<len;i++) {
		        row++;
		        de=arrPages.get(i);
                //ps = de.getPageSource();
                
		        qryPage.setAt(KeyConstants._id,row,de.getId());
		        qryPage.setAt(KeyConstants._count,row,_toString(de.getCount()));
                qryPage.setAt(KeyConstants._min,row,_toString(de.getMin()));
                qryPage.setAt(KeyConstants._max,row,_toString(de.getMax()));
                qryPage.setAt(KeyConstants._avg,row,_toString(de.getExeTime()/de.getCount()));
                qryPage.setAt(KeyConstants._app,row,_toString(de.getExeTime()-de.getQueryTime()));
                qryPage.setAt(KeyConstants._load,row,_toString(de.getFileLoadTime()));
		        qryPage.setAt(KeyConstants._query,row,_toString(de.getQueryTime()));
                qryPage.setAt(KeyConstants._total,row,_toString(de.getFileLoadTime()+de.getExeTime()));
		        qryPage.setAt(KeyConstants._src,row,de.getSrc());    
			}
		}
		catch(PageException dbe) {}
		

		
	    // Pages Parts
		boolean hasParts=partEntries!=null && !partEntries.isEmpty();
		int qrySize=0;
		if(hasParts) {
			qrySize=partEntries.size()<MAX_PARTS?partEntries.size():MAX_PARTS;
		}
		
		Query qryPart=new QueryImpl(
                new Collection.Key[]{
                		KeyConstants._id
                		,KeyConstants._count,
                		KeyConstants._min,
                		KeyConstants._max,
                		KeyConstants._avg,
                		KeyConstants._total,
                		KeyConstants._path,
                		KeyConstants._start,
                		KeyConstants._end},
                qrySize,"query");
		if(hasParts) {
			row=0;
			DebugEntryTemplatePart[] tmp = partEntries.values().toArray(new DebugEntryTemplatePart[partEntries.size()]);
	        Arrays.sort(tmp,new DebugEntryTemplatePartComparator());
	       
	        len=tmp.length<MAX_PARTS?tmp.length:MAX_PARTS;
	        DebugEntryTemplatePart[] parts=new DebugEntryTemplatePart[len];
	        for(int i=0;i<len;i++) {
	        	parts[i]=tmp[i];
	        }
	        
	
			try {
	            DebugEntryTemplatePart de;
	            //PageSource ps;
			    for(int i=0;i<parts.length;i++) {
			        row++;
			        de=parts[i];
	                
			        qryPart.setAt(KeyConstants._id,row,de.getId());
			        qryPart.setAt(KeyConstants._count,row,_toString(de.getCount()));
			        qryPart.setAt(KeyConstants._min,row,_toString(de.getMin()));
			        qryPart.setAt(KeyConstants._max,row,_toString(de.getMax()));
			        qryPart.setAt(KeyConstants._avg,row,_toString(de.getExeTime()/de.getCount()));
			        qryPart.setAt(KeyConstants._start,row,_toString(de.getStartPosition()));
			        qryPart.setAt(KeyConstants._end,row,_toString(de.getEndPosition()));
			        qryPart.setAt(KeyConstants._total,row,_toString(de.getExeTime()));
			        qryPart.setAt(KeyConstants._path,row,de.getPath());    
				}
			}
			catch(PageException dbe) {}
		}
		
		
		

		// exceptions
		len = exceptions==null?0:exceptions.size();
		
        Array arrExceptions=new ArrayImpl();
        if(len>0) {
	        	Iterator<CatchBlock> it = exceptions.iterator();
	        	row=0;
	        	while(it.hasNext()) {
	        		arrExceptions.appendEL(it.next());  
	        	}
			
        }

		// output log
        //Query qryOutputLog=getOutputText();
        
        

		// timers
		len=timers==null?0:timers.size();
        Query qryTimers=new QueryImpl(
                new Collection.Key[]{KeyConstants._label,KeyConstants._time,KeyConstants._template},
                len,"timers");
        if(len>0) {
        	try {
	        	Iterator<DebugTimerImpl> it = timers.iterator();
	        	DebugTimer timer;
	        	row=0;
	        	while(it.hasNext()) {
	        		timer=it.next();
	        		row++;
	        		qryTimers.setAt(KeyConstants._label,row,timer.getLabel()); 
	        		qryTimers.setAt(KeyConstants._template,row,timer.getTemplate()); 
	        		qryTimers.setAt(KeyConstants._time,row,Caster.toDouble(timer.getTime()));    
	        	}
			}
			catch(PageException dbe) {}
        }

		// traces
		len=traces==null?0:traces.size();
        Query qryTraces=new QueryImpl(
                new Collection.Key[]{
                		KeyConstants._type,
                		KeyConstants._category,
                		KeyConstants._text,
                		KeyConstants._template,
                		KeyConstants._line,
                		KeyConstants._action,
                		KeyConstants._varname,
                		KeyConstants._varvalue,
                		KeyConstants._time},
                len,"traces");
        if(len>0) {
        	try {
	        	Iterator<DebugTraceImpl> it = traces.iterator();
	        	DebugTraceImpl trace;
	        	row=0;
	        	while(it.hasNext()) {
	        		trace= it.next();
	        		row++;
	        		qryTraces.setAt(KeyConstants._type,row,LogUtil.toStringType(trace.getType(), "INFO"));  
	        		if(!StringUtil.isEmpty(trace.getCategory()))qryTraces.setAt(KeyConstants._category,row,trace.getCategory()); 
	        		if(!StringUtil.isEmpty(trace.getText()))qryTraces.setAt(KeyConstants._text,row,trace.getText()); 
	        		if(!StringUtil.isEmpty(trace.getTemplate()))qryTraces.setAt(KeyConstants._template,row,trace.getTemplate()); 
	        		if(trace.getLine()>0)qryTraces.setAt(KeyConstants._line,row,new Double(trace.getLine())); 
	        		if(!StringUtil.isEmpty(trace.getAction()))qryTraces.setAt(KeyConstants._action,row,trace.getAction()); 
	        		if(!StringUtil.isEmpty(trace.getVarName()))qryTraces.setAt(KeyImpl.init("varname"),row,trace.getVarName()); 
	        		if(!StringUtil.isEmpty(trace.getVarValue()))qryTraces.setAt(KeyImpl.init("varvalue"),row,trace.getVarValue()); 
	        		qryTraces.setAt(KeyConstants._time,row,new Double(trace.getTime())); 
	        	}
			}
			catch(PageException dbe) {}
        }
        


		// scope access
		len=implicitAccesses==null?0:implicitAccesses.size();
        Query qryImplicitAccesseses=new QueryImpl(
                new Collection.Key[]{
                		KeyConstants._template,
                		KeyConstants._line,
                		KeyConstants._scope,
                		KeyConstants._count,
                		KeyConstants._name},
                len,"implicitAccess");
        if(len>0) {
        	try {
	        	Iterator<ImplicitAccessImpl> it = implicitAccesses.values().iterator();
	        	ImplicitAccessImpl das;
	        	row=0;
	        	while(it.hasNext()) {
	        		das= it.next();
	        		row++;
	        		qryImplicitAccesseses.setAt(KeyConstants._template,row,das.getTemplate()); 
	        		qryImplicitAccesseses.setAt(KeyConstants._line,row,new Double(das.getLine()));
	        		qryImplicitAccesseses.setAt(KeyConstants._scope,row,das.getScope()); 
	        		qryImplicitAccesseses.setAt(KeyConstants._count,row,new Double(das.getCount())); 
	        		qryImplicitAccesseses.setAt(KeyConstants._name,row,das.getName());  
	        		
	        	}
			}
			catch(PageException dbe) {}
        }
        
        Query history=new QueryImpl(new Collection.Key[]{},0,"history");
        try {
			history.addColumn(KeyConstants._id, historyId);
	        history.addColumn(KeyConstants._level, historyLevel);
		} catch (PageException e) {
		}
		
		if(addAddionalInfo) {
			debugging.setEL(KeyConstants._cgi,pc.cgiScope());
			debugging.setEL(KeyImpl.init("starttime"),starttime);
			debugging.setEL(KeyConstants._id,pc.getId());
		}

		debugging.setEL(KeyConstants._pages,qryPage);
		debugging.setEL(PAGE_PARTS,qryPart);
		debugging.setEL(QUERIES,qryQueries);
		debugging.setEL(KeyConstants._timers,qryTimers);
		debugging.setEL(KeyConstants._traces,qryTraces);
		debugging.setEL(IMPLICIT_ACCESS,qryImplicitAccesseses);
		//debugging.setEL(OUTPUT_LOG,qryOutputLog);
		
		
		
		
		debugging.setEL(KeyImpl.intern("history"),history);
		debugging.setEL(KeyConstants._exceptions,arrExceptions);
		
		return debugging;
    }
    
	private static Struct getUsage(QueryEntry qe) throws PageException {
		Query qry = ((QueryEntryImpl)qe).getQry();
        
        QueryColumn c;
        DebugQueryColumn dqc;
        outer:if(qry!=null) {
        	Struct usage=null;
        	Collection.Key[] columnNames = qry.getColumnNames();
        	Collection.Key columnName; 
        	for(int i=0;i<columnNames.length;i++){
        		columnName=columnNames[i];
        		c = qry.getColumn(columnName);
        		if(!(c instanceof DebugQueryColumn)) break outer;
        		dqc=(DebugQueryColumn) c;
        		if(usage==null) usage=new StructImpl();
        		usage.setEL(columnName, Caster.toBoolean(dqc.isUsed()));
        	}
        	return usage;
        }
        return null;
	}

	/*private static String getUsageList(QueryEntry qe) throws PageException  {
		Query qry = ((QueryEntryImpl)qe).getQry();
        StringBuilder sb=new StringBuilder();
        QueryColumn c;
        DebugQueryColumn dqc;
        outer:if(qry!=null) {
        	String[] columnNames = qry.getColumns();
        	Collection.Key colName;
        	for(int i=0;i<columnNames.length;i++){
        		colName=KeyImpl.init(columnNames[i]);
        		c = qry.getColumn(colName);
        		if(!(c instanceof DebugQueryColumn)) break outer;
        		dqc=(DebugQueryColumn) c;
        		if(!dqc.isUsed()){
        			if(sb.length()>0) sb.append(", ");
        			sb.append(colName.getString());
        		}
        	}
        }
        return sb.toString();
	}*/

	@Override
	public DebugTimer addTimer(String label, long time, String template) {
		DebugTimerImpl t;
		timers.add(t=new DebugTimerImpl(label,time,template));
		return t;
	}

	@Override
	public DebugTrace addTrace(int type, String category, String text, PageSource page,String varName,String varValue) {
		
		long _lastTrace =(traces.isEmpty())?lastEntry: lastTrace;
		lastTrace = System.currentTimeMillis();
        StackTraceElement[] _traces = new Exception("Stack trace").getStackTrace();
		String clazz=page.getFullClassName();
		int line=0;
		
		// line
		for(int i=0;i<_traces.length;i++) {
			StackTraceElement trace=_traces[i];
    		if(trace.getClassName().startsWith(clazz)) {
    			line=trace.getLineNumber();
    			break;
			}
		}
		
		DebugTraceImpl t;
		traces.add(t=new DebugTraceImpl(type,category,text,page.getDisplayPath(),line,"",varName,varValue,lastTrace-_lastTrace));
		return t;
	}
	
	@Override
	public DebugTrace addTrace(int type, String category, String text, String template,int line,String action,String varName,String varValue) {
		
		long _lastTrace =(traces.isEmpty())?lastEntry: lastTrace;
		lastTrace = System.currentTimeMillis();
        
		DebugTraceImpl t;
		traces.add(t=new DebugTraceImpl(type,category,text,template,line,action,varName,varValue,lastTrace-_lastTrace));
		return t;
	}
	
	@Override
	public DebugTrace[] getTraces() {
		return traces.toArray(new DebugTrace[traces.size()]);
	}
	
	@Override
	public void addException(Config config,PageException pe) {
		if(exceptions.size()>1000) return;
		try {
			exceptions.add(((PageExceptionImpl)pe).getCatchBlock(config));
		}
		catch(Throwable t){}
	}
	
	@Override
	public CatchBlock[] getExceptions() {
		return exceptions.toArray(new CatchBlock[exceptions.size()]);
	}

	public static boolean debugQueryUsage(PageContext pageContext, Query query) {
		if(pageContext.getConfig().debug() && query instanceof QueryImpl) {
			if(((ConfigWebImpl)pageContext.getConfig()).getDebugShowQueryUsage()){
				((QueryImpl)query).enableShowQueryUsage();
				return true;
			}
		}
		return false;
	}

	public void init(Config config) {
		this.starttime=new DateTimeImpl(config);
	}

	@Override
	public void addImplicitAccess(String scope, String name) {
		if(implicitAccesses.size()>1000) return;
		try {
			SystemUtil.TemplateLine tl = SystemUtil.getCurrentContext(); 
			String key=tl+":"+scope+":"+name;
			ImplicitAccessImpl dsc = implicitAccesses.get(key);
			if(dsc!=null)
				dsc.inc();
			else 
				implicitAccesses.put(key,new ImplicitAccessImpl(scope,name,tl.template,tl.line));
		}
		catch(Throwable t){}
	}

	@Override
	public ImplicitAccess[] getImplicitAccesses(int scope, String name) {
		return implicitAccesses.values().toArray(new ImplicitAccessImpl[implicitAccesses.size()]);
	}

	public void setOutputLog(DebugOutputLog outputLog) { 
		this.outputLog=outputLog;
	}
	
	public DebugTextFragment[] getOutputTextFragments() { 
		return this.outputLog.getFragments();
	}
	
	public Query getOutputText() throws DatabaseException { 
		DebugTextFragment[] fragments = outputLog.getFragments();
		int len = fragments==null?0:fragments.length;
		Query qryOutputLog=new QueryImpl(
                new Collection.Key[]{
                		KeyConstants._line
                		,KeyConstants._template,
                		KeyConstants._text},
                len,"query");
		
		
        if(len>0) {
	        	for(int i=0;i<fragments.length;i++) {
	        		qryOutputLog.setAtEL(KeyConstants._line,i+1,fragments[i].line);
	        		qryOutputLog.setAtEL(KeyConstants._template,i+1,fragments[i].template);
	        		qryOutputLog.setAtEL(KeyConstants._text,i+1,fragments[i].text);  
	        	}
        }
        return qryOutputLog;
        
	}
}

final class DebugEntryTemplateComparator implements Comparator<DebugEntryTemplate> {
    
    public int compare(DebugEntryTemplate de1,DebugEntryTemplate de2) {
        return (int)((de2.getExeTime()+de2.getFileLoadTime())-(de1.getExeTime()+de1.getFileLoadTime()));
    }
}

final class DebugEntryTemplatePartComparator implements Comparator<DebugEntryTemplatePart> {
	
	@Override
	public int compare(DebugEntryTemplatePart de1,DebugEntryTemplatePart de2) {
        return (int) (de2.getExeTime()-de1.getExeTime());
		
		
		/*int diff = de1.getPath().compareTo(de2.getPath());		
        if(diff!=0) return diff;
		
		diff= de1.getStartPosition()-de2.getStartPosition();
		if(diff!=0) return diff;
		
		return de1.getEndPosition()-de2.getEndPosition();
		*/
		
    }
}