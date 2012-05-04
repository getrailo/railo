package railo.runtime.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;
import railo.runtime.functions.other.CreateUUID;
import railo.runtime.op.Caster;

public class DebugExecutionLog extends ExecutionLogSupport {
	
	private static int count=1;
	private Resource file;
	private StringBuffer content;
	private PageContext pc;
	private StringBuffer header;
	private ArrayList<String> pathes=new ArrayList<String>();
	private long start;
	
	private Map<String,Entry> entries=Collections.synchronizedMap(new HashMap<String,Entry>());
	
	
	protected void _init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
		header=new StringBuffer();
		content=new StringBuffer();
		start=System.currentTimeMillis();
	}
	
	@Override
	protected void _log(int startPos, int endPos, long startTime, long endTime) {
		long diff=endTime-startTime;
		if(unit==UNIT_MICRO)diff/=1000;
		else if(unit==UNIT_MILLI)diff/=1000000;
		
		int index = path(pc.getCurrentPageSource().getDisplayPath());
		String key=index+":"+startPos+":"+endPos;
		Entry entry=entries.get(key);
		if(entry==null) entries.put(key, new Entry(startPos,endPos,diff));
		else entry.add(diff);
	}
	

	private int path(String path) {
		int index= pathes.indexOf(path);
		if(index==-1){
			pathes.add(path);
			return pathes.size()-1;
		}
		return index;
	}

	@Override
	protected void _release() {
		print.e(entries);
	}

	
	
	
	class Entry{

		private final int startPos;
		private final int endPos;
		private long time;
		private int count=1;

		public Entry(int startPos, int endPos, long time) { 
			this.startPos=startPos;
			this.endPos=endPos;
			this.time=time;
		}

		public void add(long time) { 
			this.time+=time;
			this.count++;
		}
		
		public String toString(){
			return "time:"+time+";count:"+count+";start-pos:"+startPos+";end-pos:"+endPos;
		}
		
	}
}
