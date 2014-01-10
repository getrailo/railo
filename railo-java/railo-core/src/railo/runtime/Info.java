package railo.runtime;

import java.io.InputStream;
import java.util.Map;

import railo.commons.date.TimeZoneConstants;
import railo.commons.io.IOUtil;
import railo.commons.io.ini.IniFile;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ListUtil;

/**
 * Info to this Version
 */
public final class Info {

	/**
	 * @return the level
	 */
	public static String getLevel() {
		return level;
	}

	public static final int STATE_ALPHA = 2*100000000;
	public static final int STATE_BETA = 1*100000000;
	public static final int STATE_RC = 3*100000000;
	public static final int STATE_FINAL = 0;
	
	// Mod this
    private static DateTime releaseDate;//=DateUtil.toDateTime(TimeZone.getDefault(),2009,6,29,0,0,0,null);
    
    private static String versionName;//="Barry";
	private static String versionNameExplanation;//="http://en.wikipedia.org/wiki/Barry_(dog)";

    // 3.1
    private static int state;//=STATE_BETA;
    private static int major;//=3;
    private static int minor;//=1;
    private static int releases;//=0;
    private static int patches;//=18;
   
    
    private static final long releaseTime;//=releaseDate.getTime();
    private static String version;
	private static String level;
    private static final String strState;//=toStringState(state);
    private static final int intVersion;
    private static final int fullVersion;
    
    static {
    	InputStream is = Info.class.getClassLoader().getResourceAsStream("railo/runtime/Info.ini");
    	try{
    		IniFile ini=new IniFile(is);
    		Map verIni=ini.getSection("version");
    		versionName=(String) verIni.get("name");
    		versionNameExplanation=(String) verIni.get("name-explanation");
    		releaseDate=DateCaster.toDateAdvanced((String) verIni.get("release-date"), TimeZoneConstants.EUROPE_ZURICH);
    		state=toIntState((String) verIni.get("state"));
    		level=(String) verIni.get("level");
    		version=(String) verIni.get("number");
    		String[] aVersion = ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(version,'.'));

    	    major=Caster.toIntValue(aVersion[0]);
    	    minor=Caster.toIntValue(aVersion[1]);
    	    releases=Caster.toIntValue(aVersion[2]);
    	    patches=Caster.toIntValue(aVersion[3]);
    	} 
    	catch (Throwable t) {
    		t.printStackTrace();
    		throw new PageRuntimeException(Caster.toPageException(t));
		}
    	finally{
    		IOUtil.closeEL(is);
    	}
    	
    	releaseTime=releaseDate.getTime();
    	strState=toStringState(state);
        version=StringUtil.addZeros(major,1)+'.'+ 
            StringUtil.addZeros(minor,1)+'.'+ 
            StringUtil.addZeros(releases,1)+'.'+ 
            StringUtil.addZeros(patches,3);    
        intVersion=(major*1000000)+(minor*10000)+(releases*100)+patches;
        fullVersion=intVersion+state;       
    }
    
    public static int toIntVersion(String version, int defaultValue) {
    	try{
	    	String[] aVersion = ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(version,'.'));
	    	int ma = Caster.toIntValue(aVersion[0]);
		    int mi = Caster.toIntValue(aVersion[1]);
		    int re = Caster.toIntValue(aVersion[2]);
		    int pa = Caster.toIntValue(aVersion[3]);
	    	return (ma*1000000)+(mi*10000)+(re*100)+pa;
    	}
    	catch(Throwable t){
    		return defaultValue;
    	}
    }
    
    // Version <version>.<major>.<minor>.<patches>
    
    /**
     * @return Returns the releaseDate.
     */
    public static DateTime getRealeaseDate() {
        return releaseDate;
    }
    
    /**
     * @return Returns the releaseTime.
     */
    public static long getRealeaseTime() {
        return releaseTime;
    }
    
    /**
     * @return Returns the version.
     */
    public static String getVersionAsString() {
        return version;
    }

    /**
     * @return Returns the intVersion.
     */
    public static int getVersionAsInt() {
        return intVersion;
    }

    /**
     * @return returns the state
     */
    public static int getStateAsInt() {
        return state;
    }

    /**
     * @return returns the state
     */
    public static String getStateAsString() {
        return strState;
    }
    

    /**
     * @return returns the state
     */
    public static String toStringState(int state) {
        if(state==STATE_FINAL) return "final";
        else if(state==STATE_BETA) return "beta";
        else if(state==STATE_RC) return "rc";
        else return "alpha";
    }
    
    /**
     * @return returns the state
     */
    public static int toIntState(String state) {
    	state=state.trim().toLowerCase();
    	if("final".equals(state)) return STATE_FINAL;
    	else if("beta".equals(state)) return STATE_BETA;
    	else if("rc".equals(state)) return STATE_RC;
        else return STATE_ALPHA;
    }
    

	public static int getFullVersionInfo() {
		return fullVersion;
	}

	public static String getVersionName() {
		return versionName;
	}
	public static int getMajorVersion() {
		return major;
	}
	public static int getMinorVersion() {
		return minor;
	}
	
	public static String getVersionNameExplanation() {
		return versionNameExplanation;
	}
	
	/*public static void main(String[] args) {
		print.out("getFullVersionInfo(103010018):"+getFullVersionInfo());
		print.out("getStateAsString(beta):"+getStateAsString());
		print.out("getStateAsInt(100000000):"+getStateAsInt());
		print.out("getVersionAsInt(3010018):"+getVersionAsInt());
		print.out("getVersionAsString(3.1.0.018):"+getVersionAsString());
		print.out("getVersionName(Barry):"+getVersionName());
		print.out("getVersionNameExplanation(http://en.wikipedia.org/wiki/Barry_(dog)):"+getVersionNameExplanation());
		print.out("getRealeaseDate({ts '2009-06-29 00:00:00'}):"+getRealeaseDate());
		print.out("getRealeaseTime(1246226400000):"+getRealeaseTime());
		print.out("getLevel():"+getLevel());
		
		
		
		
	}*/
	
	
}