package railo.runtime.engine;

import java.io.IOException;
import java.nio.charset.Charset;
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

public class ResourceExecutionLog extends ExecutionLogSupport {
	
	private static int count=1;
	private Resource file;
	private StringBuffer content;
	private PageContext pc;
	private StringBuffer header;
	private ArrayList<String> pathes=new ArrayList<String>();
	private long start;
	private Resource dir;
	
	
	protected void _init(PageContext pc, Map<String, String> arguments) {
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
		createHeader(header,"unit",unitShortToString(unit));
		createHeader(header,"min-time-nano",min+"");
		
		content=new StringBuffer();
		
		
		// directory
		String strDirectory=arguments.get("directory");
		if(dir==null) {
			if(StringUtil.isEmpty(strDirectory)) {
				dir=getTemp(pc);
			}
			else {
				try {
					dir = ResourceUtil.toResourceNotExisting(pc, strDirectory,false);
					if(!dir.exists()){
						dir.createDirectory(true);
					}
					else if(dir.isFile()){
						err(pc,"can not create directory ["+dir+"], there is already a file with same name.");
					}
				} 
				catch (Throwable t) {
					err(pc,t);
					dir=getTemp(pc);
				}
			}
		}
		file=dir.getRealResource((pc.getId())+"-"+CreateUUID.call(pc)+".exl");
		file.createNewFile();
		start=System.currentTimeMillis();
	}

	private static Resource getTemp(PageContext pc) {
		Resource tmp = pc.getConfig().getConfigDir();
		Resource dir = tmp.getRealResource("execution-log");
		if(!dir.exists())dir.mkdirs();
		return dir;
	}

	protected void _release() {
		
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
			IOUtil.write(file, header+sb.toString()+content.toString(), (Charset)null, false);
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

	
	
	@Override
	protected void _log(int startPos, int endPos, long startTime, long endTime) {
		long diff=endTime-startTime;
		if(unit==UNIT_MICRO)diff/=1000;
		else if(unit==UNIT_MILLI)diff/=1000000;
		
		content.append(path(pc.getCurrentPageSource().getDisplayPath()));
		content.append("\t");
		content.append(startPos);
		content.append("\t");
		content.append(endPos);
		content.append("\t");
		content.append(diff);
		content.append("\n");
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
