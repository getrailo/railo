package railo.runtime.exp;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;

import railo.commons.io.CharsetUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.err.ErrorPage;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.writer.CFMLWriter;

/**
 * Railo Runtime Page Exception, all runtime Exception are sub classes of this class
 */
public abstract class PageExceptionImpl extends PageException {

	private static final long serialVersionUID = -5816929795661373219L;
	
	
	
	
	
	private Array tagContext=new ArrayImpl();
	private Struct additional=new StructImpl(Struct.TYPE_LINKED);
	/**
	 * Field <code>detail</code>
	 */
	protected String detail="";
	//private Throwable rootCause;
	private int tracePointer;
	private String errorCode="0";
	private String extendedInfo=null;

	private String type;
	private String customType;
	private boolean isInitTagContext=false;
	private LinkedList<PageSource> sources=new LinkedList<PageSource>();
	private String varName;


	/**
	 * Class Constructor
	 * @param message Exception Message
	 * @param type Type as String
	 */
	public PageExceptionImpl(String message,String type) {
		this(message,type,null);
	}
	/**
	 * Class Constructor
	 * @param message Exception Message
	 * @param type Type as String
	 * @param customType CUstom Type as String
	 */
	public PageExceptionImpl(String message,String type, String customType) {
		super(message==null?"":message);
		//rootCause=this;
		this.type=type.toLowerCase().trim();
		this.customType=customType;
        //setAdditional("customType",getCustomTypeAsString());
	}
	
	/**
	 * Class Constructor
	 * @param e exception
	 * @param type Type as String
	 */
	public PageExceptionImpl(Throwable e,String type) {
		this(e,type,null);
	}
	
	/**
	 * Class Constructor
	 * @param e exception
	 * @param type Type as String
	 * @param customType CUstom Type as String
	 */
	public PageExceptionImpl(Throwable e,String type, String customType) {
		super(StringUtil.isEmpty(e.getMessage(),true)?e.getClass().getName():e.getMessage());
		if(e instanceof InvocationTargetException)e=((InvocationTargetException)e).getTargetException();
        
        //this.i
        initCause(e);
        //this.setStackTrace(e.getStackTrace());
        
		if(e instanceof IPageException) {
            IPageException pe=(IPageException)e;
			this.additional=pe.getAdditional();
			this.setDetail(pe.getDetail());
			this.setErrorCode(pe.getErrorCode());
			this.setExtendedInfo(pe.getExtendedInfo());
		}
		
		//else if(e.getCause()!=null)rootCause=e.getCause();
		//else rootCause=e;

		this.type=type.trim();
		this.customType=(customType==null)?this.type:customType;
	}
    
    @Override
	public String getDetail() { 
		if(detail==null || detail.equals(getMessage()))return "";
		return detail; 
	}
	
	@Override
	public String getErrorCode() { return errorCode==null?"":errorCode; }
	
	@Override
	public String getExtendedInfo() { return extendedInfo==null?"":extendedInfo; }
	
	@Override
	public void setDetail(String detail) {
		this.detail=detail;
	}
	@Override
	public void setErrorCode(String errorCode) {
		this.errorCode=errorCode;
	}
	@Override
	public void setExtendedInfo(String extendedInfo) {
		this.extendedInfo=extendedInfo;
	}
	
	public final Struct getCatchBlock() {
		return getCatchBlock(ThreadLocalPageContext.getConfig());
	}
	
	@Override
	public final Struct getCatchBlock(PageContext pc) {
		return getCatchBlock(ThreadLocalPageContext.getConfig(pc));
	}
	
	@Override
	public CatchBlock getCatchBlock(Config config) {
		return new CatchBlockImpl(this);
	}
	
	public Array getTagContext(Config config) {
		if(isInitTagContext) return tagContext;
		_getTagContext( config,tagContext,getStackTraceElements(this),sources);
		isInitTagContext=true;
		return tagContext;
	}
	

	public static Array getTagContext(Config config,StackTraceElement[] traces) {
		Array tagContext=new ArrayImpl();
		_getTagContext( config,tagContext,traces,new LinkedList<PageSource>());
		return tagContext;
	}

	private static void _getTagContext(Config config, Array tagContext, StackTraceElement[] traces, 
			LinkedList<PageSource> sources) {
		//StackTraceElement[] traces = getStackTraceElements(t);
		
		int line=0;
		String template="",tlast;
		Struct item;
		StackTraceElement trace=null;
		int index=-1;
		PageSource ps;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			tlast=template;
			template=trace.getFileName();
			
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			// content
			if(!StringUtil.emptyIfNull(tlast).equals(template))index++;
			
			String[] content=null;
			try {
				
				// FUTURE only do the 3th try below 
				Resource res = config.getResource(template);
				if(!res.exists()) res = ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), template,true,true);
				
				if(!res.exists()) {
					PageSource _ps = PageSourceImpl.best(config.getPageSources(ThreadLocalPageContext.get(), null, template, false, false, true));
					if(_ps!=null && _ps.exists()) {
						res=_ps.getResource();
					}
				}
				
				if(res.exists())	
					content=IOUtil.toStringArray(IOUtil.getReader(res,CharsetUtil.toCharset(config.getTemplateCharset())));
				else {
					if(sources.size()>index)ps = sources.get(index);
					else ps=null;

					if(ps!=null && trace.getClassName().equals(ps.getFullClassName())) {
						if(ps.physcalExists())
							content=IOUtil.toStringArray(IOUtil.getReader(ps.getPhyscalFile(), CharsetUtil.toCharset(config.getTemplateCharset())));
						template=ps.getDisplayPath();
					}
				}	
			} 
			catch (Throwable th) {
				//th.printStackTrace();
			}
			
			// check last
			if(tagContext.size()>0){
				try {
					Struct last=(Struct) tagContext.getE(tagContext.size());
					if(last.get(KeyConstants._Raw_Trace).equals(trace.toString()))continue;
				} 
				catch (Exception e) {
					//e.printStackTrace();
				}
			}
			
			item=new StructImpl();
			line=trace.getLineNumber();
			item.setEL(KeyConstants._template,template);
			item.setEL(KeyConstants._line,new Double(line));
			item.setEL(KeyConstants._id,"??");
			item.setEL(KeyConstants._Raw_Trace,trace.toString());
			item.setEL(KeyConstants._type,"cfml");
			item.setEL(KeyConstants._column,new Double(0));
			if(content!=null) {
				item.setEL(KeyConstants._codePrintHTML,getCodePrint(content,line,true));
				item.setEL(KeyConstants._codePrintPlain,getCodePrint(content,line,false));
			}
			else {
				item.setEL(KeyConstants._codePrintHTML,"");
				item.setEL(KeyConstants._codePrintPlain,"");
			}
			// FUTURE id 
			tagContext.appendEL(item);
		}
	}
	
	
	
	public int getPageDeep() {
		StackTraceElement[] traces = getStackTraceElements(this);
		
		String template="",tlast;
		StackTraceElement trace=null;
		int index=0;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			tlast=template;
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			if(!StringUtil.emptyIfNull(tlast).equals(template))index++;
			
		}
		return index;
	}
	
	
	@Override
	public Struct getErrorBlock(PageContext pc,ErrorPage ep) {
		Struct struct=new StructImpl();

		struct.setEL("browser",pc.cgiScope().get("HTTP_USER_AGENT",""));
		struct.setEL("datetime",new DateTimeImpl(pc));
		struct.setEL("diagnostics",getMessage()+' '+getDetail()+"<br>The error occurred on line "+getLine(pc.getConfig())+" in file "+getFile(pc.getConfig())+".");
		struct.setEL("GeneratedContent",getGeneratedContent(pc));
		struct.setEL("HTTPReferer",pc.cgiScope().get("HTTP_REFERER",""));
		struct.setEL("mailto",ep.getMailto());
		struct.setEL(KeyConstants._message,getMessage());
		struct.setEL("QueryString",StringUtil.emptyIfNull(pc. getHttpServletRequest().getQueryString()));
		struct.setEL("RemoteAddress",pc.cgiScope().get("REMOTE_ADDR",""));
		struct.setEL("RootCause",getCatchBlock(pc));
		struct.setEL("StackTrace",getStackTraceAsString());
		struct.setEL(KeyConstants._template,pc. getHttpServletRequest().getServletPath());
		
		struct.setEL(KeyConstants._Detail,getDetail());
		struct.setEL("ErrorCode",getErrorCode());
		struct.setEL("ExtendedInfo",getExtendedInfo());
		struct.setEL(KeyConstants._type,getTypeAsString());
		struct.setEL("TagContext",getTagContext(pc.getConfig()));
		struct.setEL("additional",additional);
			// TODO RootCause,StackTrace
		
		return struct;
	}
	
	private String getGeneratedContent(PageContext pc){
		PageContextImpl pci=(PageContextImpl)pc;
		CFMLWriter ro=pci.getRootOut();
		String gc=ro.toString();
		try{
			ro.clearBuffer();
		}
		catch(IOException ioe){}
		if(gc==null) return "";
		return gc;
	}
	
	
	/**
	 * @return return the file where the failure occurred
	 */
	private String getFile(Config config) {
        if(getTagContext(config).size()==0) return "";
        
        Struct sct=(Struct) getTagContext(config).get(1,null);
        return Caster.toString(sct.get(KeyConstants._template,""),"");
    }
    
	public String getLine(Config config) {
        if(getTagContext(config).size()==0) return "";
        
        Struct sct=(Struct) getTagContext(config).get(1,null);
        return Caster.toString(sct.get(KeyConstants._line,""),"");
    }
    
	@Override
	public void addContext(PageSource pr, int line, int column, StackTraceElement element) {
		if(line==-187) {
			sources.add(pr);
			return;
		}
        
		Struct struct=new StructImpl();
        //print.out(pr.getDisplayPath());
		try {
			String[] content=pr.getSource();
			struct.set(KeyConstants._template,pr.getDisplayPath());
			struct.set(KeyConstants._line,new Double(line));
			struct.set(KeyConstants._id,"??");
			struct.set(KeyConstants._Raw_Trace,(element!=null)?element.toString():"");
			struct.set(KeyConstants._Type,"cfml");
			struct.set(KeyConstants._column,new Double(column));
			if(content!=null){
				struct.set(KeyConstants._codePrintHTML,getCodePrint(content,line,true));
				struct.set(KeyConstants._codePrintPlain,getCodePrint(content,line,false));
			}
			tagContext.append(struct);
		} 
		catch (Exception e) {}
	}
	
	private static String getCodePrint(String[] content,int line, boolean asHTML ) {
		StringBuilder sb=new StringBuilder();
		// bad Line
		for(int i=line-2;i<line+3;i++) {
			if(i>0 && i<=content.length) {
				if(asHTML && i==line)sb.append("<b>");
				if(asHTML)sb.append(i+": "+StringUtil.escapeHTML(content[i-1]));
				else sb.append(i+": "+(content[i-1]));
				if(asHTML && i==line)sb.append("</b>");
				if(asHTML)sb.append("<br>");
				sb.append('\n');
			}
		}
		return sb.toString();
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		
		//FFFFCF
    	DumpTable htmlBox = new DumpTable("exception","#ff9900","#FFCC00","#000000");
		htmlBox.setTitle("Railo ["+Info.getVersionAsString()+"] - Error ("+StringUtil.ucFirst(getTypeAsString())+")");
		
		
		// Message
		htmlBox.appendRow(1,new SimpleDumpData("Message"),new SimpleDumpData(getMessage()));
		
		// Detail
		String detail=getDetail();
		if(!StringUtil.isEmpty(detail,true))
			htmlBox.appendRow(1,new SimpleDumpData("Detail"),new SimpleDumpData(detail));
		
		// additional
		Iterator<Key> it = additional.keyIterator();
		Collection.Key k;
		while(it.hasNext()) {
			k=it.next();
			htmlBox.appendRow(1,new SimpleDumpData(k.getString()),new SimpleDumpData(additional.get(k,"").toString()));
		}
		
		Array tagContext = getTagContext(pageContext.getConfig());
		// Context MUSTMUST
		if(tagContext.size()>0) {
			//Collection.Key[] keys=tagContext.keys();
			Iterator<Object> vit = tagContext.valueIterator();
			//Entry<Key, Object> te;
			DumpTable context=new DumpTable("#ff9900","#FFCC00","#000000");
			//context.setTitle("The Error Occurred in");
			//context.appendRow(0,new SimpleDumpData("The Error Occurred in"));
			context.appendRow(7,
					new SimpleDumpData(""),
					new SimpleDumpData("template"),
					new SimpleDumpData("line"));
			try {
				boolean first=true;
				while(vit.hasNext()) {
					Struct struct=(Struct)vit.next();
					context.appendRow(1,
							new SimpleDumpData(first?"called from ":"occurred in"),
							new SimpleDumpData(struct.get(KeyConstants._template,"")+""),
							new SimpleDumpData(Caster.toString(struct.get(KeyConstants._line,null))));
					first=false;
				}
				htmlBox.appendRow(1,new SimpleDumpData("Context"),context);
				
				
				// Code
				String strCode=((Struct)tagContext.get(1,null)).get(KeyConstants._codePrintPlain,"").toString();
				String[] arrCode = ListUtil.listToStringArray(strCode, '\n');
				arrCode=ListUtil.trim(arrCode);
				DumpTable code=new DumpTable("#ff9900","#FFCC00","#000000");
				
				for(int i=0;i<arrCode.length;i++) {
					code.appendRow(i==2?1:0,new SimpleDumpData(arrCode[i]));
				}
				htmlBox.appendRow(1,new SimpleDumpData("Code"),code);

			} 
            catch (PageException e) {}
		}
		
		
		// Java Stacktrace
		String strST=getStackTraceAsString();
		String[] arrST = ListUtil.listToStringArray(strST, '\n');
		arrST=ListUtil.trim(arrST);
		DumpTable st=new DumpTable("#ff9900","#FFCC00","#000000");
		
		for(int i=0;i<arrST.length;i++) {
			st.appendRow(i==0?1:0,new SimpleDumpData(arrST[i]));
		}
		htmlBox.appendRow(1,new SimpleDumpData("Java Stacktrace"),st);

		return htmlBox; 
	}	
	
	@Override
	public String getStackTraceAsString() {
		
        StringWriter sw=new StringWriter();
	    PrintWriter pw=new PrintWriter(sw);
        printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
    
    
    
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        StackTraceElement[] traces = getStackTraceElements(this);
        StackTraceElement trace;
        
        s.println(getMessage());
        for(int i=0;i<traces.length;i++){
            trace=traces[i];
            s.println("\tat "+trace);
        }
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
    	StackTraceElement[] traces = getStackTraceElements(this);
        StackTraceElement trace;
        
        s.println(getMessage());
        for(int i=0;i<traces.length;i++){
            trace=traces[i];
            s.println("\tat "+trace+":"+trace.getLineNumber());
        }
    }
    

    private static StackTraceElement[] getStackTraceElements(Throwable t) {
    	StackTraceElement[] st=getStackTraceElements(t,true);
    	if(st==null) st= getStackTraceElements(t,false);
    	return st;
    }
    
    private static StackTraceElement[] getStackTraceElements(Throwable t, boolean onlyWithCML) {
    	StackTraceElement[] st;
    	Throwable cause=t.getCause();
    	if(cause!=null){
    		st = getStackTraceElements(cause,onlyWithCML);
        	if(st!=null) return st;
    	}
    	
    	st=t.getStackTrace();
    	if(!onlyWithCML || hasCFMLinStacktrace(st)){
    		return st;
    	}
    	return null;
	}
    

    private static boolean hasCFMLinStacktrace(StackTraceElement[] traces) {
		for(int i=0;i<traces.length;i++) {
			if(traces[i].getFileName()!=null && !traces[i].getFileName().endsWith(".java")) return true;
		}
		return false;
	}
    /*ths code has produced duplettes
     * private static void fillStackTraceElements(ArrayList<StackTraceElement> causes, Throwable t) {
		if(t==null) return;
		fillStackTraceElements(causes, t.getCause());
		StackTraceElement[] traces = t.getStackTrace();
		for(int i=0;i<traces.length;i++) {
			//if(causes.contains(traces[i]))
			causes.add(traces[i]);
		}
	}*/
    
	/**
	 * set a additional key value
	 * @param key
	 * @param value
	 */
	public void setAdditional(Collection.Key key, Object value) {
		additional.setEL(key,StringUtil.toStringEmptyIfNull(value));
	}
	
	
	@Override
	public Throwable getRootCause() {
        Throwable cause=this; 
        Throwable temp; 
        
        while((temp=cause.getCause())!=null)cause=temp;
        return cause;
	}

	@Override
	public int getTracePointer() {
		return tracePointer;
	}
	@Override
	public void setTracePointer(int tracePointer) {
		this.tracePointer = tracePointer;
	}
	
	@Override
    public boolean typeEqual(String type) {
    	if(type==null) return true;
        type=StringUtil.toUpperCase(type);
    	// ANY
        if(type.equals("ANY")) return true;// MUST check
        // Type Compare
        if(getTypeAsString().equalsIgnoreCase(type)) return true;
        return getClass().getName().equalsIgnoreCase(type);
    }
    
    @Override
	public String getTypeAsString() {
		return type;
	}
    

	public String getType() { // for compatibility to ACF
		return type;
	}
	
	@Override
	public String getCustomTypeAsString() {
		return customType==null?type:customType;
	}
    
    @Override
	public Struct getAdditional() {
        return additional;
    }public Struct getAddional() {
        return additional;
    }
    
    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
    
    /*public static void printStackTrace(PrintWriter s,Throwable t) {
        
        //while((temp=cause.getCause())!=null)cause=temp;
        
        StackTraceElement[] traces = t.getStackTrace();
        StackTraceElement trace;
        
        s.println(t.getMessage());
        for(int i=0;i<traces.length;i++){
            trace=traces[i];
            s.println("\tat "+trace+":"+trace.getLineNumber());
        }
        t=t.getCause();
        if(t!=null)printStackTrace(s,t);
    }*/
    
    
    
}