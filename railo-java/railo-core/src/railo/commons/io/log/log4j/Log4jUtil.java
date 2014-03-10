package railo.commons.io.log.log4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Appender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SyslogAppender;
import org.apache.log4j.xml.XMLLayout;

import railo.commons.io.CharsetUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.log4j.appender.ConsoleAppender;
import railo.commons.io.log.log4j.appender.RollingResourceAppender;
import railo.commons.io.log.log4j.appender.TaskAppender;
import railo.commons.io.log.log4j.layout.ClassicLayout;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.retirement.RetireListener;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;

public class Log4jUtil {
	
	public static final long MAX_FILE_SIZE=1024*1024*10;
    public static final int MAX_FILES=10;
	private static final String DEFAULT_PATTERN = "%d{dd.MM.yyyy HH:mm:ss,SSS} %-5p [%c] %m%n"; 



	public static Logger getResourceLog(Config config, Resource res, Charset charset, String name, Level level, int timeout,RetireListener listener, boolean async) throws IOException {
		Appender a = new RollingResourceAppender(
				new ClassicLayout()
				,res
				,charset
				,true
				,RollingResourceAppender.DEFAULT_MAX_FILE_SIZE
				,RollingResourceAppender.DEFAULT_MAX_BACKUP_INDEX
				,timeout,listener); // no open stream at all
		
		if(async) {
			a=new TaskAppender(config, a);
		}
		
		
		return getLogger(config, a, name, level);
	}

	public static Logger getConsoleLog(Config config, boolean errorStream, String name, Level level) {
		// Printwriter
		PrintWriter pw=errorStream?config.getErrWriter():config.getOutWriter();
		if(pw==null)pw=new PrintWriter(errorStream?System.err:System.out);
		
		return getLogger(config, new ConsoleAppender(pw,new PatternLayout(DEFAULT_PATTERN)), name, level);
	}
	
	public static final Logger getLogger(Config config,Appender appender, String name, Level level){
		// fullname
		String fullname=name;
		if(config instanceof ConfigWeb) {
	    	ConfigWeb cw=(ConfigWeb) config;
	    	fullname="web."+cw.getLabel()+"."+name;
	    }
		else fullname="server."+name;
		
		Logger l = LogManager.exists(fullname);
		if(l!=null) l.removeAllAppenders();
		else l = LogManager.getLogger(fullname);
		l.setAdditivity(false);
    	l.addAppender(appender);
    	l.setLevel(level);
    	return l;
	}
	
    
    public static final Appender getAppender(Config config,Layout layout,String name,String strAppender, Map<String, String> appenderArgs){
    	if(appenderArgs==null)appenderArgs=new HashMap<String, String>();
    	
    	// Appender
		Appender appender=null;
		if(!StringUtil.isEmpty(strAppender)) {
			// Console Appender
			if("console".equalsIgnoreCase(strAppender) || ConsoleAppender.class.getName().equalsIgnoreCase(strAppender)) {
				// stream-type
				boolean doError=false;
				String st = Caster.toString(appenderArgs.get("streamtype"),null);
				if(!StringUtil.isEmpty(st,true)) {
					st=st.trim().toLowerCase();
					if(st.equals("err") || st.equals("error"))
						doError=true;
				}
				appenderArgs.put("streamtype",doError?"error":"output");
				
				
				// get print writer
				PrintWriter pw;
				if(doError) {
					if(config.getErrWriter()==null)pw=new PrintWriter(System.err);
					else pw=config.getErrWriter();
				} 
				else {
					if(config.getOutWriter()==null)pw=new PrintWriter(System.out);
					else pw=config.getOutWriter();
				}
				appender = new ConsoleAppender(pw,layout);
			}
			else if("resource".equalsIgnoreCase(strAppender) || RollingResourceAppender.class.getName().equalsIgnoreCase(strAppender)) {
				
				// path
				Resource res=null;
				String path = Caster.toString(appenderArgs.get("path"),null);
				if(!StringUtil.isEmpty(path,true)) {
					path=path.trim();
					path=ConfigWebUtil.translateOldPath(path);
					res=ConfigWebUtil.getFile(config, config.getConfigDir(),path, ResourceUtil.TYPE_FILE);
					if(res.isDirectory()) {
						res=res.getRealResource(name+".log");
					}
				}
				if(res==null) {
					res=ConfigWebUtil.getFile(config, config.getConfigDir(),"logs/"+name+".log", ResourceUtil.TYPE_FILE);
				}
				
				
				// charset
				Charset charset = CharsetUtil.toCharset(Caster.toString(appenderArgs.get("charset"),null),null);
				if(charset==null){
					charset=config.getResourceCharset();
					appenderArgs.put("charset",charset.name());
				}
				
				// maxfiles
				int maxfiles = Caster.toIntValue(appenderArgs.get("maxfiles"),10);
				appenderArgs.put("maxfiles",Caster.toString(maxfiles));
				
				// maxfileSize
				long maxfilesize = Caster.toLongValue(appenderArgs.get("maxfilesize"),1024*1024*10);
				appenderArgs.put("maxfilesize",Caster.toString(maxfilesize));
				
				// timeout
				int timeout = Caster.toIntValue(appenderArgs.get("timeout"),60); // timeout in seconds
				appenderArgs.put("timeout",Caster.toString(timeout));
				
				try {
					appender=new RollingResourceAppender(layout,res,charset,true,maxfilesize,maxfiles,timeout,null);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			// class defintion
			else {
				Object obj = ClassUtil.loadInstance(strAppender,null,null);
				if(obj instanceof Appender) {
					appender=(Appender) obj;
					Iterator<Entry<String, String>> it = appenderArgs.entrySet().iterator();
					Entry<String, String> e;
					while(it.hasNext()){
						e = it.next();
						try {
							Reflector.callSetter(obj, e.getKey(), e.getValue());
						}
						catch (PageException e1) {
							e1.printStackTrace(); // TODO log
						}
					}
					org.apache.log4j.net.SyslogAppender s=(SyslogAppender) appender;
				}
			}
		}
		
		if(appender==null) {
			PrintWriter pw;
			if(config.getOutWriter()==null)pw=new PrintWriter(System.out);
			else pw=config.getOutWriter();
			appender=new ConsoleAppender(pw,layout);
		}
		
		return appender;
    }
    
    public static final Layout getLayout(String strLayout, Map<String, String> layoutArgs) {
    	if(layoutArgs==null)layoutArgs=new HashMap<String, String>();
    	
    	// Layout
		Layout layout=null;
		if(!StringUtil.isEmpty(strLayout)) {
			// Classic Layout
			if("classic".equalsIgnoreCase(strLayout) || ClassicLayout.class.getName().equalsIgnoreCase(strLayout))
				layout=new ClassicLayout();
			
			// HTML Layout
			else if("html".equalsIgnoreCase(strLayout) || HTMLLayout.class.getName().equalsIgnoreCase(strLayout)) {
				HTMLLayout html = new HTMLLayout();
				layout=html;
				
				// Location Info
				Boolean locInfo = Caster.toBoolean(layoutArgs.get("locationinfo"),null);
				if(locInfo!=null) html.setLocationInfo(locInfo.booleanValue());
				else locInfo=Boolean.FALSE;
				layoutArgs.put("locationinfo", locInfo.toString());
				
				// Title
				String title = Caster.toString(layoutArgs.get("title"),"");
				if(!StringUtil.isEmpty(title,true)) html.setTitle(title);
				layoutArgs.put("title", title);
				
			}
			// XML Layout
			else if("xml".equalsIgnoreCase(strLayout) || XMLLayout.class.getName().equalsIgnoreCase(strLayout)) {
				XMLLayout xml = new XMLLayout();
				layout=xml;
	
				// Location Info
				Boolean locInfo = Caster.toBoolean(layoutArgs.get("locationinfo"),null);
				if(locInfo!=null) xml.setLocationInfo(locInfo.booleanValue());
				else locInfo=Boolean.FALSE;
				layoutArgs.put("locationinfo", locInfo.toString());
				
				// Properties
				Boolean props = Caster.toBoolean(layoutArgs.get("properties"),null);
				if(props!=null) xml.setProperties(props.booleanValue());
				else props=Boolean.FALSE;
				layoutArgs.put("properties", props.toString());
				
			}
			// Pattern Layout
			else if("pattern".equalsIgnoreCase(strLayout) || PatternLayout.class.getName().equalsIgnoreCase(strLayout)) {
				PatternLayout patt = new PatternLayout();
				layout=patt;
				
				// pattern
				String pattern = Caster.toString(layoutArgs.get("pattern"),null);
				if(!StringUtil.isEmpty(pattern,true)) patt.setConversionPattern(pattern);
				else {
					patt.setConversionPattern(DEFAULT_PATTERN);
					layoutArgs.put("pattern", DEFAULT_PATTERN);
				}
			}
			// class defintion
			else {
				Object obj = ClassUtil.loadInstance(strLayout,null,null);
				if(obj instanceof Layout) {
					layout=(Layout) obj;
					Iterator<Entry<String, String>> it = layoutArgs.entrySet().iterator();
					Entry<String, String> e;
					while(it.hasNext()){
						e = it.next();
						try {
							Reflector.callSetter(obj, e.getKey(), e.getValue());
						}
						catch (PageException e1) {
							e1.printStackTrace(); // TODO log
						}
					}
					
				}
			}
		}
		if(layout!=null) return layout;
		return new ClassicLayout();
    }
    
    //private static LoggerRepository repository=new Hierarchy(null); 

	public static Level toLevel(int level) {
		switch(level){
		case Log.LEVEL_FATAL: return Level.FATAL;
		case Log.LEVEL_ERROR: return Level.ERROR;
		case Log.LEVEL_WARN: return Level.WARN;
		case Log.LEVEL_DEBUG: return Level.DEBUG;
		case Log.LEVEL_INFO: return Level.INFO;
		case Log.LEVEL_TRACE: return Level.TRACE;
		}
		return Level.INFO;
	}

	public static int toLevel(Level level) {
		if(Level.FATAL.equals(level)) return Log.LEVEL_FATAL;
		if(Level.ERROR.equals(level)) return Log.LEVEL_ERROR;
		if(Level.WARN.equals(level)) return Log.LEVEL_WARN;
		if(Level.DEBUG.equals(level)) return Log.LEVEL_DEBUG;
		if(Level.INFO.equals(level)) return Log.LEVEL_INFO;
		if(Level.TRACE.equals(level)) return Log.LEVEL_TRACE;
		return Log.LEVEL_INFO;
	}
	
	public static Level toLevel(String strLevel, Level defaultValue) {
        if(strLevel==null) return defaultValue;
        strLevel=strLevel.toLowerCase().trim();
        if(strLevel.startsWith("info")) return Level.INFO;
        if(strLevel.startsWith("debug")) return Level.DEBUG;
        if(strLevel.startsWith("warn")) return Level.WARN;
        if(strLevel.startsWith("error")) return Level.ERROR;
        if(strLevel.startsWith("fatal")) return Level.FATAL;
        if(strLevel.startsWith("trace")) return Level.TRACE;
        return defaultValue;
    }
}
