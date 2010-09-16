package railo.runtime.debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import railo.print;
import railo.commons.io.log.LogUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.db.SQL;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.CatchBlock;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;


/**
 * Class to debug the application
 */
public final class DebuggerImpl implements Dumpable, Debugger {

	private static final Collection.Key PAGES = KeyImpl.getInstance("pages");
	private static final Collection.Key QUERIES = KeyImpl.getInstance("queries");
	private static final Collection.Key TIMERS = KeyImpl.getInstance("timers");
	private static final Collection.Key TRACES = KeyImpl.getInstance("traces");
	private static final Collection.Key EXCEPTIONS = KeyImpl.getInstance("exceptions");
	private static final Collection.Key HISTORY = KeyImpl.getInstance("history");
	
	private Map<String,DebugEntryImpl> pages=new HashMap<String,DebugEntryImpl>();
	private List<QueryEntryImpl> queries=new ArrayList<QueryEntryImpl>();
	private List<DebugTimerImpl> timers=new ArrayList<DebugTimerImpl>();
	private List<DebugTraceImpl> traces=new ArrayList<DebugTraceImpl>();
	private List<CatchBlock> exceptions=new ArrayList<CatchBlock>();
	
	private boolean output=true;
	private long lastEntry;
	private long lastTrace;
	private Array historyId=new ArrayImpl();
	private Array historyLevel=new ArrayImpl();
	
	/**
     * @see railo.runtime.debug.Debugger#reset()
     */
	public void reset() {
		pages.clear();
		queries.clear();
		timers.clear();
		traces.clear();
		exceptions.clear();
		historyId.clear();
		historyLevel.clear();
		output=true;
	}

	/**
	 * standart Constructor of the class
	 */
	public DebuggerImpl() {
		
	}

    /**
     *
     * @see railo.runtime.debug.Debugger#getEntry(railo.runtime.PageContext, railo.runtime.PageSource)
     */
    public DebugEntry getEntry(PageContext pc,PageSource source) {
        return getEntry(pc,source,null);
    }

    /**
     *
     * @see railo.runtime.debug.Debugger#getEntry(railo.runtime.PageContext, railo.runtime.PageSource, java.lang.String)
     */
    public DebugEntry getEntry(PageContext pc,PageSource source, String key) {
    	lastEntry = System.currentTimeMillis();
        String src=DebugEntryImpl.getSrc(source,key);
        
        DebugEntryImpl de=(DebugEntryImpl) pages.get(src);
        if(de!=null ){
            de.countPP();
			historyId.appendEL(de.getId());
			historyLevel.appendEL(Caster.toInteger(pc.getCurrentLevel()));
            return de;
        }
        de=new DebugEntryImpl(source,key);
        pages.put(src,de);
		historyId.appendEL(de.getId());
		historyLevel.appendEL(Caster.toInteger(pc.getCurrentLevel()));
        return de;
    }

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		if(!output) return null;
		
        // fill pages to aray
        ArrayList arrPages=toArray();
        QueryEntry[] arrQueries=(QueryEntry[]) queries.toArray(new QueryEntry[queries.size()]);

		
		DumpTable boxPage = new DumpTable("#eeeeee","white","#666666");
		boxPage.setWidth("100%");
		
        int len=arrPages.size();
        int tLoad=0;
        int tQuery=0;
        int tApp=0;
        int tCount=0;
        for(int i=0;i<len;i++) {
            DebugEntry de=(DebugEntry) arrPages.get(i);
            tLoad+=de.getFileLoadTime();
            tQuery+=de.getQueryTime();
            tApp+=de.getExeTime();
            tCount+=de.getCount();
            boxPage.appendRow(new DumpRow(0,new DumpData[]{
                    new SimpleDumpData(de.getSrc()),
                    new SimpleDumpData(String.valueOf(de.getCount())),
                    //plus(de.getMin()),//Min
                    //plus(de.getExeTime()/de.getCount()),
                    //plus(de.getMax()),//Max
                    _toDumpData(de.getFileLoadTime()),
                    _toDumpData(de.getQueryTime()),
                    new SimpleDumpData(_toDumpData(de.getExeTime()-de.getQueryTime())+""),
                    new SimpleDumpData(""),
                    _toDumpData(de.getFileLoadTime()+de.getExeTime())}));
			
		}
        // Total
        DumpRow row = new DumpRow(1023,new DumpData[]{new SimpleDumpData("Total"),_toDumpData(tCount),_toDumpData(tLoad),_toDumpData(tQuery),_toDumpData(tApp-tQuery),new SimpleDumpData(""),_toDumpData(tLoad+tApp)});
        boxPage.appendRow(row);
        boxPage.prependRow(row);
        
        row=new DumpRow(1023,new DumpData[]{new SimpleDumpData("file"),new SimpleDumpData("count"),new SimpleDumpData("load"),new SimpleDumpData("query"),new SimpleDumpData("app"),new SimpleDumpData(""),new SimpleDumpData("total")});
        boxPage.appendRow(row);
        boxPage.prependRow(row);


//      Exceptions
        DumpTable tableExceptions=null;
		int tl=exceptions==null?0:exceptions.size();
		if(tl>0) {
			tableExceptions = new DumpTable("#eeeeee","white","#666666");
			
			tableExceptions.appendRow(15, new SimpleDumpData("type"),new SimpleDumpData("message"),new SimpleDumpData("detail"), new SimpleDumpData("template"));
			
			
	        	Iterator<CatchBlock> it = exceptions.iterator();
	        	CatchBlock block;
	        	PageException pe;
	        	String type,msg,detail,templ;
	        	while(it.hasNext()) {
	        		block=it.next();
	        		pe=block.getPageException();
	        		type=StringUtil.toStringEmptyIfNull(pe.getTypeAsString());
	        		msg=StringUtil.toStringEmptyIfNull(pe.getMessage());
	        		detail=StringUtil.toStringEmptyIfNull(pe.getDetail());
	        		templ=getTemplate(pageContext.getConfig(),pe,true);
	        		tableExceptions.appendRow(0, 
	        				new SimpleDumpData(type),
	        				new SimpleDumpData(msg),
	        				new SimpleDumpData(detail),
	        				new SimpleDumpData(templ)) ;
	        	}
		}
		
		
//      Timers
        DumpTable tableTimer=null;
		tl=timers==null?0:timers.size();
		if(tl>0) {
			tableTimer = new DumpTable("#eeeeee","white","#666666");
			//boxTimer.setWidth("100%");
			tableTimer.appendRow(7, new SimpleDumpData("label"), new SimpleDumpData("time (ms)"),new SimpleDumpData("template"));
			
			
	        	Iterator it = timers.iterator();
	        	DebugTimer timer;
	        	while(it.hasNext()) {
	        		timer=(DebugTimer) it.next();
	        		tableTimer.appendRow(0, new SimpleDumpData(timer.getLabel()),new SimpleDumpData(timer.getTime()),new SimpleDumpData(timer.getTemplate())) ;
	        	}
		}

//      Traces
		DumpTable tableTraces=null;
		tl=traces==null?0:traces.size();
		if(tl>0) {
			tableTraces = new DumpTable("#eeeeee","white","#666666");
			tableTraces.setWidth("100%");
			tableTraces.appendRow(new DumpRow(2047, new DumpData[]{new SimpleDumpData("type"), new SimpleDumpData("category"),new SimpleDumpData("text"),new SimpleDumpData("template"),new SimpleDumpData("line")
					,new SimpleDumpData("var name"),new SimpleDumpData("var value"),new SimpleDumpData("total time (ms)"),new SimpleDumpData("trace slot time (ms)")}));
		
            	Iterator it = traces.iterator();
	        	DebugTrace trace;
	        	int total=0;
	        	while(it.hasNext()) {
	        		trace=(DebugTrace) it.next();
	        		total+=trace.getTime();
	        		DumpTable tableVar=new DumpTable("#eeeeee","white","#666666");
	        		SimpleDumpData varValue = new SimpleDumpData(toString(trace.getVarValue()));
	        		DumpData var;
	        		try {
	        			Object value = new CFMLExpressionInterpreter().interpret(pageContext,toString(trace.getVarValue()));
	        			tableVar.appendRow(0, varValue);
	        			tableVar.appendRow(0,DumpUtil.toDumpData(value, pageContext, maxlevel,dp) );
	        			var=tableVar;
	        		}
	        		catch(Throwable t) {
	        			var=varValue;
	        		}
	        		
	        		tableTraces.appendRow(new DumpRow(0, new DumpData[]{
	        				new SimpleDumpData(LogUtil.toStringType(trace.getType(), "INFO")),
	        				new SimpleDumpData(toString(trace.getCategory())),
	        				new SimpleDumpData(toString(trace.getText())),
	        				new SimpleDumpData(toString(trace.getTemplate())),
	        				new SimpleDumpData(Caster.toString(trace.getLine())),
	        				new SimpleDumpData(toString(trace.getVarName())),
	        				var,
	        				new SimpleDumpData(Caster.toString(total)),
	        				new SimpleDumpData(Caster.toString(trace.getTime()))}));
	        	}
		}
		
//		 Query
		DumpTable tableQuery=null;
		DumpTable tableQueryItem = null;
		if(arrQueries.length>0) {
			
			tableQuery = new DumpTable("#eeeeee","white","#666666");
			tableQuery.setWidth("100%");
			
			for(int i=0;i<arrQueries.length;i++) {
				tableQueryItem = new DumpTable("#eeeeee","white","#666666");
				tableQueryItem.appendRow(1, new SimpleDumpData("Source"), new SimpleDumpData(arrQueries[i].getSrc()));
				tableQueryItem.appendRow(1, new SimpleDumpData("Execution Time"), new SimpleDumpData(arrQueries[i].getExe()));
				tableQueryItem.appendRow(1, new SimpleDumpData("Recordcount"), new SimpleDumpData(arrQueries[i].getRecordcount()));
				tableQueryItem.appendRow(1, new SimpleDumpData("Query"), new SimpleDumpData((arrQueries[i].getSQL().toString().trim())));
				
			    tableQuery.appendRow(0,tableQueryItem);
			}
		}
		
		DumpTable table = new DumpTable("#cccccc","eeeeee","#666666");
		table.setTitle("Debugging Output");
		table.setWidth("100%");
		table.appendRow(1,new SimpleDumpData("Pages"),boxPage);
		
		
		if(tableExceptions!=null && !tableExceptions.isEmpty())table.appendRow(1,new SimpleDumpData("Caught Exceptions"),tableExceptions);
		if(tableTimer!=null && !tableTimer.isEmpty())table.appendRow(1,new SimpleDumpData("Timers"),tableTimer);
		if(tableTraces!=null && !tableTraces.isEmpty())table.appendRow(1,new SimpleDumpData("Traces"),tableTraces);
		if(tableQuery!=null && !tableQuery.isEmpty())table.appendRow(1,new SimpleDumpData("Queries"),tableQuery);
		
		return table;
	}

	private String getTemplate(Config config,PageException pe,boolean withLine) {
		try {
			Array arr = ((PageExceptionImpl)pe).getTagContext(config);
			Struct sct=Caster.toStruct(arr.getE(1));
			
			String templ= Caster.toString(sct.get("template"));
			if(withLine)templ +=":"+ Caster.toString(sct.get("line"));
			return templ;
		} 
		catch (Throwable t) {print.e(t);}
		
		return "";
	}

	private String toString(Object o) {
		if(o==null) return "";
		return Caster.toString(o,"");
	}

	private ArrayList toArray() {
        ArrayList arrPages=new ArrayList(pages.size());
        Iterator it = pages.keySet().iterator();
        while(it.hasNext()) {
            DebugEntry page = (DebugEntry) pages.get(it.next());
            page.resetQueryTime();
            arrPages.add(page);
            
        }
        
        Collections.sort(arrPages,new DebugEntryComparator());
        

        // Queries
        int len=queries.size();
        for(int i=0;i<len;i++) {
            QueryEntry entry=(QueryEntry) queries.get(i);
            String path=entry.getSrc();
            Object o=pages.get(path);
            
            if(o!=null) {
                DebugEntry oe=(DebugEntry) o;
                oe.updateQueryTime(entry.getExe());
            }
        }
        
        return arrPages;
    }

	private DumpData _toDumpData(int value) {
        return new SimpleDumpData(_toString(value));
    }
	private String _toString(int value) {
        if(value<=0) return "0";
        return String.valueOf(value);
    }
 
    /**
     * @see railo.runtime.debug.Debugger#addQueryExecutionTime(java.lang.String, java.lang.String, railo.runtime.db.SQL, int, railo.runtime.PageSource, int)
     */
	public void addQueryExecutionTime(String datasource,String name,SQL sql, int recordcount, PageSource src,int time) {
		queries.add(new QueryEntryImpl(datasource,name,sql,recordcount,src.getDisplayPath(),time));
	}
	
	/**
     * @see railo.runtime.debug.Debugger#setOutput(boolean)
     */
	public void setOutput(boolean output) {
		this.output = output;
	}
    
    /**
     * @see railo.runtime.debug.Debugger#getQueries()
     */
    public List getQueries() {
        return queries;
    }

    /**
     * @see railo.runtime.debug.Debugger#writeOut(railo.runtime.PageContext)
     */
    public void writeOut(PageContext pc) throws IOException {
        //stop();
        if(!output)return;
        
        String template=pc.getConfig().getDebugTemplate();
		
        // no debug File 
		if(StringUtil.isEmpty(template)) {
		    pc.forceWrite(pc.getConfig().getDefaultDumpWriter().toString(pc,toDumpData(pc, 9999,DumpUtil.toDumpProperties()),true)); 
		    return;
		} 
		try {
		    pc.doInclude(pc.getRelativePageSource(template));
        } 
		catch (PageException e) {
            pc.handlePageException(e);
        }
    }
    
    /**
     * @see railo.runtime.debug.Debugger#getDebuggingData()
     */
    public Struct getDebuggingData() {
		List queries = getQueries();
	    Struct qryExe=new StructImpl();
	    ListIterator qryIt = queries.listIterator();
        String[] cols = new String[]{"name","time","sql","src","count","datasource"};
        String[] types = new String[]{"VARCHAR","DOUBLE","VARCHAR","VARCHAR","DOUBLE","VARCHAR"};
        
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
		        QueryEntry qe=(QueryEntry) qryIt.next();
				qryQueries.setAt("name",row,qe.getName()==null?"":qe.getName());
		        qryQueries.setAt("time",row,Integer.valueOf(qe.getExe()));
		        qryQueries.setAt("sql",row,qe.getSQL().toString());
				qryQueries.setAt("src",row,qe.getSrc());
                qryQueries.setAt("count",row,Integer.valueOf(qe.getRecordcount()));
                qryQueries.setAt("datasource",row,qe.getDatasource());
                
		        Object o=qryExe.get(qe.getSrc(),null);
		        if(o==null) qryExe.setEL(qe.getSrc(),Integer.valueOf(qe.getExe()));
		        else qryExe.setEL(qe.getSrc(),Integer.valueOf(((Integer)o).intValue()+qe.getExe()));
		    }
		}
		catch(PageException dbe) {}
		
	    // Pages
	    // src,load,app,query,total
	    Struct debugging=new StructImpl();
		    
		row=0;
        ArrayList arrPages = toArray();
		int len=arrPages.size();
        Query qryPage=new QueryImpl(
                new String[]{"id","count","min","max","avg","app","load","query","total","src"},
                len,"query");

		try {
            DebugEntry de;
            //PageSource ps;
		    for(int i=0;i<len;i++) {
		        row++;
		        de=(DebugEntry) arrPages.get(i);
                //ps = de.getPageSource();
                
		        qryPage.setAt("id",row,de.getId());
		        qryPage.setAt("count",row,_toString(de.getCount()));
                qryPage.setAt("min",row,_toString(de.getMin()));
                qryPage.setAt("max",row,_toString(de.getMax()));
                qryPage.setAt("avg",row,_toString(de.getExeTime()/de.getCount()));
                qryPage.setAt("app",row,_toString(de.getExeTime()-de.getQueryTime()));
                qryPage.setAt("load",row,_toString(de.getFileLoadTime()));
		        qryPage.setAt("query",row,_toString(de.getQueryTime()));
                qryPage.setAt("total",row,_toString(de.getFileLoadTime()+de.getExeTime()));
		        qryPage.setAt("src",row,de.getSrc());    
			}
		}
		catch(PageException dbe) {}

		// exceptions
		len=exceptions==null?0:exceptions.size();
		
        Array arrExceptions=new ArrayImpl();
        if(len>0) {
	        	Iterator<CatchBlock> it = exceptions.iterator();
	        	row=0;
	        	while(it.hasNext()) {
	        		arrExceptions.appendEL(it.next());  
	        	}
			
        }

		// timers
		len=timers==null?0:timers.size();
        Query qryTimers=new QueryImpl(
                new String[]{"label","time","template"},
                len,"timers");
        if(len>0) {
        	try {
	        	Iterator it = timers.iterator();
	        	DebugTimer timer;
	        	row=0;
	        	while(it.hasNext()) {
	        		timer=(DebugTimer) it.next();
	        		row++;
	        		qryTimers.setAt("label",row,timer.getLabel()); 
	        		qryTimers.setAt("template",row,timer.getTemplate()); 
	        		qryTimers.setAt("time",row,Caster.toDouble(timer.getTime()));    
	        	}
			}
			catch(PageException dbe) {}
        }

		// traces
		len=traces==null?0:traces.size();
        Query qryTraces=new QueryImpl(
                new String[]{"type","category","text","template","line","varname","varvalue","time"},
                len,"traces");
        if(len>0) {
        	try {
	        	Iterator it = traces.iterator();
	        	DebugTrace trace;
	        	row=0;
	        	while(it.hasNext()) {
	        		trace=(DebugTrace) it.next();
	        		row++;
	        		qryTraces.setAt("type",row,LogUtil.toStringType(trace.getType(), "INFO"));  
	        		if(!StringUtil.isEmpty(trace.getCategory()))qryTraces.setAt("category",row,trace.getCategory()); 
	        		if(!StringUtil.isEmpty(trace.getText()))qryTraces.setAt("text",row,trace.getText()); 
	        		if(!StringUtil.isEmpty(trace.getTemplate()))qryTraces.setAt("template",row,trace.getTemplate()); 
	        		if(trace.getLine()>0)qryTraces.setAt("line",row,new Double(trace.getLine())); 
	        		if(!StringUtil.isEmpty(trace.getVarName()))qryTraces.setAt("varname",row,trace.getVarName()); 
	        		if(!StringUtil.isEmpty(trace.getVarValue()))qryTraces.setAt("varvalue",row,trace.getVarValue()); 
	        		qryTraces.setAt("time",row,new Double(trace.getTime())); 
	        	}
			}
			catch(PageException dbe) {}
        }
		
        Query history=new QueryImpl(new String[]{},0,"history");
        try {
			history.addColumn("id", historyId);
	        history.addColumn("level", historyLevel);
		} catch (PageException e) {
		}
        
		debugging.setEL(PAGES,qryPage);
		debugging.setEL(QUERIES,qryQueries);
		debugging.setEL(TIMERS,qryTimers);
		debugging.setEL(TRACES,qryTraces);
		debugging.setEL(HISTORY,history);
		debugging.setEL(EXCEPTIONS,arrExceptions);
		return debugging;
    }

    
    
    
	/**
	 * @see railo.runtime.debug.Debugger#addTimer(java.lang.String, long, java.lang.String)
	 */
	public DebugTimer addTimer(String label, long time, String template) {
		DebugTimerImpl t;
		timers.add(t=new DebugTimerImpl(label,time,template));
		return t;
	}

	/**
	 * @see railo.runtime.debug.Debugger#addTrace(int, java.lang.String, java.lang.String, railo.runtime.PageSource, java.lang.String)
	 */
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
			}
		}
		DebugTraceImpl t;
		traces.add(t=new DebugTraceImpl(type,category,text,page.getDisplayPath(),line,varName,varValue,lastTrace-_lastTrace));
		return t;
	}

	/**
	 *
	 * @see railo.runtime.debug.Debugger#getTraces()
	 */
	public DebugTrace[] getTraces() {
		return traces.toArray(new DebugTrace[traces.size()]);
	}

	// FUTURE add to interface
	public void addException(Config config,PageException pe) {
		print.e("set catchpe2");
		if(exceptions.size()>1000) return;
		try {
			exceptions.add(((PageExceptionImpl)pe).getCatchBlock(config));
		}
		catch(Throwable t){print.e(t);}
	}
	
	
	// FUTURE add to interface
	public CatchBlock[] getExceptions() {
		return exceptions.toArray(new CatchBlock[exceptions.size()]);
	}

}