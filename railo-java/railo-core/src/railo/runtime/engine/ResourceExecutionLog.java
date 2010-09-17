package railo.runtime.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;
import railo.runtime.functions.other.CreateUUID;
import railo.runtime.op.Caster;

public class ResourceExecutionLog implements ExecutionLog {
	
	private static int count=1;
	private Resource file;
	private StringBuffer content;
	private long last;
	private PageContext pc;
	private int minTime;
	private StringBuffer header;
	private ArrayList<String> pathes=new ArrayList<String>();
	private long start;
	
	
	public void init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
		
		//header
		HttpServletRequest req = pc.getHttpServletRequest();
		
		header=new StringBuffer();
		createHeader(header,"context-path",req.getContextPath());
		createHeader(header,"remote-user",req.getRemoteUser());
		createHeader(header,"remote-addr",req.getRemoteAddr());
		createHeader(header,"remote-host",req.getRemoteHost());
		createHeader(header,"script-name",StringUtil.emptyIfNull(req.getContextPath())+StringUtil.emptyIfNull(req.getServletPath()));
		createHeader(header,"server-name",req.getServerName());
		createHeader(header,"protocol",req.getProtocol());
		createHeader(header,"server-port",Caster.toString(req.getServerPort()));
		createHeader(header,"path-info",StringUtil.replace(
				StringUtil.emptyIfNull(req.getRequestURI()), 
				StringUtil.emptyIfNull(req.getServletPath()),"", true));
		//createHeader(header,"path-translated",pc.getBasePageSource().getDisplayPath());
		createHeader(header,"query-string",req.getQueryString());
		
		
		
		// min-time
		String strMinTime=arguments.get("min-time");
		minTime=Caster.toIntValue(strMinTime,0);
		
		// directory
		String strDirectory=arguments.get("directory");
		Resource dir=null;
		content=new StringBuffer();
		try {
			dir = ResourceUtil.toResourceExisting(pc, strDirectory);
			if(!dir.exists()){
				dir.createDirectory(true);
			}
			else if(dir.isFile()){
				err(pc,"can not create directory ["+dir+"], there is already a file with same name.");
			}
		} 
		catch (Throwable t) {
			err(pc,t);
		}
		file=dir.getRealResource((pc.getId())+"-"+CreateUUID.call(pc)+".exl");
		file.createNewFile();
		last=System.nanoTime();
		start=System.currentTimeMillis();
	}

	public void release() {
		
		// execution time
		createHeader(header, "execution-time", Caster.toString(System.currentTimeMillis()-start));
		header.append("\n");
		
		
		//path
		StringBuffer sb=new StringBuffer();
		Iterator<String> it = pathes.iterator();
		int count=0;
		while(it.hasNext()){
			sb.append(count++);
			sb.append(":");
			sb.append(it.next());
			sb.append("\n");
		}
		sb.append("\n");
		
		try {
			IOUtil.write(file, header+sb.toString()+content.toString(), null, false);
		} catch (IOException ioe) {
			err(pc,ioe);
		}
	}

	private void createHeader(StringBuffer sb,String name, String value) {
		sb.append(name);
		sb.append(":");
		sb.append(StringUtil.emptyIfNull(value));
		sb.append("\n");
	}

	public void line(int line) {
		long time=System.nanoTime()-last;
		if(minTime<=time){
			content.append(path(pc.getCurrentPageSource().getDisplayPath()));
			content.append("\t");
			content.append(line);
			content.append("\t");
			content.append(time);
			content.append("\n");
		}
		last=System.nanoTime();
	}

	private int path(String path) {
		int index= pathes.indexOf(path);
		if(index==-1){
			pathes.add(path);
			return pathes.size()-1;
		}
		return index;
	}

	private void err(PageContext pc, String msg) {
		SystemOut.print(pc.getConfig().getErrWriter(), msg);
	}

	private void err(PageContext pc, Throwable t) {
		String msg =ExceptionUtil.getStacktrace(t,true);
		SystemOut.print(pc.getConfig().getErrWriter(), msg);
	}
}
