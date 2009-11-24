package railo.runtime.video;

import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.io.res.type.http.HTTPResource;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.string.Hash;
import railo.runtime.op.Caster;

public class VideoUtilImpl implements VideoUtil {


	private static Map sizes=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	private static VideoUtilImpl instance=new VideoUtilImpl();

	private VideoUtilImpl(){		}

	public static VideoUtilImpl getInstance() {
		return instance;
	}
	
	/**
	 * @see railo.runtime.video.VideoUtil#createVideoInput(railo.commons.io.res.Resource)
	 */
	public VideoInput createVideoInput(Resource input) {
		return new VideoInputImpl(input);
	}

	/**
	 * @see railo.runtime.video.VideoUtil#createVideoOutput(railo.commons.io.res.Resource)
	 */
	public VideoOutput createVideoOutput(Resource output) {
		return new VideoOutputImpl(output);
	}

	/**
	 * @see railo.runtime.video.VideoUtil#createVideoProfile()
	 */
	public VideoProfile createVideoProfile() {
		return new VideoProfileImpl();
	}

	
	public long toBytes(String byt) throws PageException {
		byt=byt.trim().toLowerCase();
		if(byt.endsWith("kb/s") || byt.endsWith("kbps")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-4).trim())*1024);
		}
		if(byt.endsWith("mb/s") || byt.endsWith("mbps")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-4).trim())*1024*1024);
		}
		if(byt.endsWith("gb/s") || byt.endsWith("gbps")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-4).trim())*1024*1024*1024);
		}
		if(byt.endsWith("b/s") || byt.endsWith("bps")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-3).trim()));
		}
		
		if(byt.endsWith("kbit/s")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-6).trim())*1024);
		}
		if(byt.endsWith("mbit/s")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-6).trim())*1024*1024);
		}
		if(byt.endsWith("gbit/s")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-6).trim())*1024*1024*1024);
		}
		if(byt.endsWith("bit/s")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-5).trim()));
		}
		

		if(byt.endsWith("kb")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-2).trim())*1024);
		}
		if(byt.endsWith("mb")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-2).trim())*1024*1024);
		}
		if(byt.endsWith("gb")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-2).trim())*1024*1024*1024);
		}

		if(byt.endsWith("g")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-1).trim())*1024*1024*1024);
		}
		if(byt.endsWith("m")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-1).trim())*1024*1024);
		}
		if(byt.endsWith("k")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-1).trim())*1024);
		}
		if(byt.endsWith("b")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-1).trim()));
		}
		return Caster.toLongValue(byt);
	}
	
	public long toHerz(String byt) throws PageException {
		byt=byt.trim().toLowerCase();
		if(byt.endsWith("mhz")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-3).trim())*1000*1000);
		}
		if(byt.endsWith("khz")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-3).trim())*1000);
		}
		if(byt.endsWith("hz")) {
			return (long)(Caster.toDoubleValue(byt.substring(0,byt.length()-2).trim()));
		}
		return Caster.toLongValue(byt);
	}
	
	public long toMillis(String time) throws PageException {
		int last=0,index=time.indexOf(':');
		long hour=Caster.toIntValue(time.substring(last,index).trim());
		last=index+1;
		
		index=time.indexOf(':',last);
		long minute=Caster.toIntValue(time.substring(last,index).trim());
		
		double seconds=Caster.toDoubleValue(time.substring(index+1).trim());
		return (hour*60L*60L*1000L)+(minute*60L*1000L)+((int)(seconds*1000F));
	}
	
	public static VideoExecuter createVideoExecuter(Config config) throws ClassException {
		Class clazz = config.getVideoExecuterClass();
		return (VideoExecuter) ClassUtil.loadInstance(clazz);
	}

	public int[] calculateDimension(PageContext pc,VideoInput[] sources,int width, String strWidth,int height, String strHeight) throws PageException {
		int[] rtn;
		
		if(width!=-1 && height!=-1) {
			return new int[]{width,height};
		}
		
		// video component not installed
		try {
			if(VideoUtilImpl.createVideoExecuter(pc.getConfig()) instanceof VideoExecuterNotSupported){
				throw new ApplicationException("attributes width/height are required when no video analyser is installed");
			}
		} catch (ClassException e) {
			
		}
		
		
		VideoInput source;
		
		// hash
		StringBuffer sb=new StringBuffer(strHeight+"-"+strWidth);
		for(int i=0;i<sources.length;i++) {
			sb.append(sources[i].getResource().toString());
		}
		
		// get from casche
		String key = Hash.call(pc, sb.toString());
		
		int[] ci=(int[]) sizes.get(key);
		if(ci!=null) {
			return ci;
		}
		// getSize
		int w=0,h=0;
		try {
			for(int i=0;i<sources.length;i++) {
				source = sources[i];
				checkResource(source.getResource());
				
				
				VideoInfo info = VideoUtilImpl.createVideoExecuter(pc.getConfig()).info(pc.getConfig(),source);
				
				if(w<info.getWidth()) {
					h=info.getHeight();
					w=info.getWidth();
				}
				
				
			}
		}
		catch(Exception ve) {
			throw Caster.toPageException(ve);
		}

		// calculate only height
		if(width!=-1) {
			height=calculateSingle(w,width,strHeight,h);
		}
		// calculate only height
		else if(height!=-1) {
			width=calculateSingle(h,height,strWidth,w);
		}
		else {
			width=procent2pixel(strWidth,w);
			height=procent2pixel(strHeight,h);
			if(width!=-1 && height!=-1) {}
			else if(width==-1 && height==-1) {
				width=w;
				height=h;
			}
			else if(width!=-1) 
				height=calucalteFromOther(h,w,width);
			else 
				width=calucalteFromOther(w,h,height);
			
			
		}
		sizes.put(key, rtn=new int[]{width,height});
		return rtn;
	}
	
	private static int procent2pixel(String str, int source) throws ExpressionException {
		if(!StringUtil.isEmpty(str)) {
			if(StringUtil.endsWith(str, '%')) {
				str=str.substring(0,str.length()-1).trim();
				double procent = Caster.toDoubleValue(str);
				if(procent<0 )
					throw new ExpressionException("procent has to be positive number (now "+str+")");
				return (int)(source*(procent/100D));
			}
			return Caster.toIntValue(str);
		}
		return -1;
	}

	private static int calculateSingle(int srcOther,int destOther, String strDim, int srcDim) throws ExpressionException {
		int res = procent2pixel(strDim, srcDim);
		if(res!=-1) return res;
		return calucalteFromOther(srcDim,srcOther,destOther);//(int)(Caster.toDoubleValue(srcDim)*Caster.toDoubleValue(destOther)/Caster.toDoubleValue(srcOther));
	}

	private static int calucalteFromOther(int srcDim,int srcOther,int destOther) {
		return (int)(Caster.toDoubleValue(srcDim)*Caster.toDoubleValue(destOther)/Caster.toDoubleValue(srcOther));
	}
	

	private static void checkResource(Resource resource) throws ApplicationException {
		if(resource instanceof FileResource)return;
		if(resource instanceof HTTPResource)
			throw new ApplicationException("attribute width and height are required when external sources are invoked");
		
		throw new ApplicationException("the resource type ["+resource.getResourceProvider().getScheme()+"] is not supported");
	}
}
