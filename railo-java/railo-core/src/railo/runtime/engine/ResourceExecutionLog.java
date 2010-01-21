package railo.runtime.engine;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
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

	public void init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
		
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
	}

	public void release() {
		try {
			IOUtil.write(file, content.toString(), null, false);
		} catch (IOException ioe) {
			err(pc,ioe);
		}
	}

	public void line(int line) {
		long time=System.nanoTime()-last;
		if(minTime<=time){
			content.append(pc.getCurrentPageSource().getDisplayPath());
			content.append("\t");
			content.append(line);
			content.append("\t");
			content.append(time);
			content.append("\n");
		}
		last=System.nanoTime();
	}

	private void err(PageContext pc, String msg) {
		SystemOut.print(pc.getConfig().getErrWriter(), msg);
	}

	private void err(PageContext pc, Throwable t) {
		String msg = t.getMessage()+"\n"+ExceptionUtil.getStacktrace(t);
		SystemOut.print(pc.getConfig().getErrWriter(), msg);
	}
}
