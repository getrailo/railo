package railo.runtime;

import java.io.InputStream;
import java.util.Properties;

import org.osgi.framework.Bundle;

import railo.Info;
import railo.print;
import railo.commons.date.TimeZoneConstants;
import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ListUtil;

/**
 * Info to this Version
 */
public final class InfoImpl implements Info  {


	public static final int STATE_ALPHA = 2*100000000;
	public static final int STATE_BETA = 1*100000000;
	public static final int STATE_RC = 3*100000000;
	public static final int STATE_FINAL = 0;
	
	// Mod this
    private DateTime releaseDate;
    private String versionName;
	private String versionNameExplanation;
    private int state;
    private int major;
    private int minor;
    private int releases;
    private int patches;
    private final long releaseTime;
    private String version;
	private String level;
    private final String strState;
    private final int intVersion;
    private final int fullVersion;
    

    public InfoImpl() {
    	this(null);
    }
    
    public InfoImpl(Bundle bundle) {
    	
    	InputStream is=null;
    	try{
    		Properties prop = new Properties();
    	    
    		// first check the bundle for the default.properties
    		if(bundle!=null) {
	    		try {
	    			is = bundle.getEntry("default.properties").openStream();
	    			prop.load(is);
	    		}
	    		catch (Throwable t) {}
	    		finally {IOUtil.closeEL(is);}
    		}

    		if(prop.getProperty("railo.core.name")==null) {
    			try{
        	    	is = getClass().getClassLoader().getResourceAsStream("default.properties");
    	            prop.load(is);
        		}
	    		catch (Throwable t) {}
	    		finally {IOUtil.closeEL(is);}
	    		
	    		if(prop.getProperty("railo.core.name")==null) {
	    			try{
	        	    	is = getClass().getClassLoader().getResourceAsStream("/default.properties");
	    	            prop.load(is);
	        		}
		    		catch (Throwable t) {}
		    		finally {IOUtil.closeEL(is);}
		    		
		    		if(prop.getProperty("railo.core.name")==null) {
		    			try{
		        	    	is = getClass().getResourceAsStream("/default.properties");
		    	            prop.load(is);
		        		}
			    		catch (Throwable t) {}
			    		finally {IOUtil.closeEL(is);}
			    		
			    		if(prop.getProperty("railo.core.name")==null) {
			    			try{
			        	    	is = getClass().getResourceAsStream("../../default.properties");
			    	            prop.load(is);
			        		}
				    		catch (Throwable t) {}
				    		finally {IOUtil.closeEL(is);}
			    		}
		    		}
	    		}
    		}

    		print.o("-->"+prop.getProperty("railo.core.name"));
    		
    		//IniFile ini=new IniFile(is);
    		//Map verIni=ini.getSection("version");
    		versionName=StringUtil.removeQuotes(prop.getProperty("railo.core.name"),true);
    		if(versionName==null)throw new RuntimeException("missing railo.core.name");
    		versionNameExplanation=StringUtil.removeQuotes(prop.getProperty("railo.core.name.explanation"),true);
    		releaseDate=DateCaster.toDateAdvanced(StringUtil.removeQuotes(prop.getProperty("railo.core.release.date"),true), TimeZoneConstants.EUROPE_ZURICH);
    		state=toIntState(StringUtil.removeQuotes(prop.getProperty("railo.core.state"),true));
    		level="os";
    		version=StringUtil.removeQuotes(prop.getProperty("railo.core.version"),true);
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
    

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
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
    public DateTime getRealeaseDate() {
        return releaseDate;
    }
    
    /**
     * @return Returns the releaseTime.
     */
    public long getRealeaseTime() {
        return releaseTime;
    }
    
    /**
     * @return Returns the version.
     */
    public String getVersionAsString() {
        return version;
    }

    /**
     * @return Returns the intVersion.
     */
    public int getVersionAsInt() {
        return intVersion;
    }

    /**
     * @return returns the state
     */
    public int getStateAsInt() {
        return state;
    }

    /**
     * @return returns the state
     */
    public String getStateAsString() {
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
    public int toIntState(String state) {
    	state=state.trim().toLowerCase();
    	if("final".equals(state)) return STATE_FINAL;
    	else if("beta".equals(state)) return STATE_BETA;
    	else if("rc".equals(state)) return STATE_RC;
        else return STATE_ALPHA;
    }
    

	public int getFullVersionInfo() {
		return fullVersion;
	}

	public String getVersionName() {
		return versionName;
	}
	public int getMajorVersion() {
		return major;
	}
	public int getMinorVersion() {
		return minor;
	}
	
	public String getVersionNameExplanation() {
		return versionNameExplanation;
	}
	
	
	/**
     * cast a railo string version to a int version
     * @param version
     * @return int version
     */
    public static int toInVersion(String version) {
        
        int	rIndex = version.lastIndexOf(".rcs");
        if(rIndex==-1)	rIndex = version.lastIndexOf(".rc");
        
        if(rIndex!=-1) {
            version=version.substring(0,rIndex);
        }
        
        //1.0.0.090
        int beginIndex=0;
        
        //Major
        int endIndex=version.indexOf('.',beginIndex);
        int intVersion=0;
        intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*1000000; // FUTURE 10000000

        // Minor
        beginIndex=endIndex+1;
        endIndex=version.indexOf('.',beginIndex);
        intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*10000; // FUTURE 100000

        // releases
        beginIndex=endIndex+1;
        endIndex=version.indexOf('.',beginIndex);
        intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*100; // FUTURE 1000
        
        // patches
        beginIndex=endIndex+1;
        intVersion+=Integer.parseInt(version.substring(beginIndex));
        
        return intVersion;
    }
}