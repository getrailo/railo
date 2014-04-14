package railo.loader.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class CFMLEngineFactorySupport {
	private static File tempFile;
	private static File homeFile; 


	/**
     * copy a inputstream to a outputstream
     * @param in 
     * @param out
     * @throws IOException
     */
	public final static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[0xffff];
        int len;
        try{
	        while((len = in.read(buffer)) !=-1)
	          out.write(buffer, 0, len);
        }
        finally{
        	closeEL(in);
        	closeEL(out);
        }
    }
    
    /**
     * close inputstream without a Exception
     * @param is 
     */
    public final static void closeEL(InputStream is) {
          try {
            if(is!=null)is.close();
        } 
        catch (Throwable e) {}
      }
    
    /**
     * close outputstream without a Exception
     * @param os 
     */
    public final static void closeEL(OutputStream os) {
          try {
              if(os!=null)os.close();
        } 
        catch (Throwable e) {}
      }
    
    /**
     * read String data from a InputStream and returns it as String Object 
     * @param is InputStream to read data from.
     * @return readed data from InputStream
     * @throws IOException
     */
    public static String toString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
        StringBuffer content=new StringBuffer();
        
        String line=br.readLine();
        if(line!=null) {
            content.append(line);
            while((line=br.readLine())!=null)   {
                content.append("\n"+line);
            }
        }
        br.close();
        return content.toString();
    }
    
    /**
     * cast a railo string version to a int version
     * @param version
     * @return int version
     */
    public static int toInVersion(String version, int defaultValue) {
        
        int	rIndex = version.lastIndexOf(".rcs");
        if(rIndex==-1)	rIndex = version.lastIndexOf(".rc");
        
        if(rIndex!=-1) {
            version=version.substring(0,rIndex);
        }
        
        //1.0.0.090
        int beginIndex=0;

        //Major
        int endIndex=version.indexOf('.',beginIndex);
        if(endIndex==-1) return defaultValue;
        int intVersion=0;
        try{
        	intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*10000000;
        }
        catch(Throwable t){
        	return defaultValue;
        }

        // Minor
        beginIndex=endIndex+1;
        endIndex=version.indexOf('.',beginIndex);
        if(endIndex==-1) return defaultValue;
        try{
        	intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*100000;
        }
        catch(Throwable t){
        	return defaultValue;
        }

        // releases
        beginIndex=endIndex+1;
        endIndex=version.indexOf('.',beginIndex);
        if(endIndex==-1) return defaultValue;
        try{
        	intVersion+=Integer.parseInt(version.substring(beginIndex,endIndex))*1000;
        }
        catch(Throwable t){
        	return defaultValue;
        }

        // patches
        beginIndex=endIndex+1;
        try{
        	intVersion+=Integer.parseInt(version.substring(beginIndex));
        }
        catch(Throwable t){
        	return defaultValue;
        }
        
        return intVersion;
    }
    
    /**
	 * @param version
	 * @return
	 */
    public static String toStringVersion(int version) {
        
    	StringBuffer sb=new StringBuffer();

    	// Major
    	int tmp=(version/10000000); // FUTURE 10000000
    	version-=tmp*10000000; // FUTURE 10000000
    	sb.append(String.valueOf(tmp));
    	sb.append(".");

    	// Minor
    	tmp=(version/100000); // FUTURE 100000
    	version-=tmp*100000; // FUTURE 100000
    	sb.append(len(String.valueOf(tmp),2));
    	sb.append(".");

    	// releases
    	tmp=(version/1000); // FUTURE 1000
    	version-=tmp*1000; // FUTURE 1000
    	sb.append(len(String.valueOf(tmp),2));
    	sb.append(".");
    	
        // patches
    	sb.append(len(String.valueOf(version),3));
    	
    	return sb.toString();
        
    }
    

    private static Object len(String str, int i) {
		while(str.length()<i)
			str="0"+str;
		return str;
	}

	public static String removeQuotes(String str, boolean trim) {
		if(str==null) return str;
		if(trim)str=str.trim();
		if(str.length()<2) return str;
		
		char first=str.charAt(0);
		char last=str.charAt(str.length()-1);
		
		if((first=='"' || first=='\'') && first==last)
			return str.substring(1,str.length()-1);
		
		return str;
	}
	
	/**
     * replace path placeholder with the real path, placeholders are [{temp-directory},{system-directory},{home-directory}]
     * @param path
     * @return updated path
     */
    public static String parsePlaceHolder(String path) {
        if(path==null) return path;
        // Temp
        if(path.startsWith("{temp")) {
            if(path.startsWith("}",5)) path=new File(getTempDirectory(),path.substring(6)).toString();
            else if(path.startsWith("-dir}",5)) path=new File(getTempDirectory(),path.substring(10)).toString();
            else if(path.startsWith("-directory}",5)) path=new File(getTempDirectory(),path.substring(16)).toString();
        }
        // System
        else if(path.startsWith("{system")) {
            if(path.startsWith("}",7)) path=new File(getSystemDirectory(),path.substring(8)).toString();
            else if(path.startsWith("-dir}",7)) path=new File(getSystemDirectory(),path.substring(12)).toString();
            else if(path.startsWith("-directory}",7)) path=new File(getSystemDirectory(),path.substring(18)).toString();
        }
        // Home
        else if(path.startsWith("{home")) {
            if(path.startsWith("}",5)) path=new File(getHomeDirectory(),path.substring(6)).toString();
            else if(path.startsWith("-dir}",5)) path=new File(getHomeDirectory(),path.substring(10)).toString();
            else if(path.startsWith("-directory}",5)) path=new File(getHomeDirectory(),path.substring(16)).toString();
        }
        return path;
    }
    
    public static File getHomeDirectory() {
    	if(homeFile!=null) return homeFile;
        
        String homeStr = System.getProperty("user.home");
        if(homeStr!=null) {
            homeFile=new File(homeStr);
            homeFile=getCanonicalFileEL(homeFile);
        }
        return homeFile;
    }
    

    /**
     * returns the Temp Directory of the System
     * @return temp directory
     */
    protected static File getTempDirectory() {
    	if(tempFile!=null) return tempFile;
        
        String tmpStr = System.getProperty("java.io.tmpdir");
        if(tmpStr!=null) {
            tempFile=new File(tmpStr);
            if(tempFile.exists()) {
                tempFile=getCanonicalFileEL(tempFile);
                return tempFile;
            }
        }
        try {
            File tmp = File.createTempFile("a","a");
            tempFile=tmp.getParentFile();
            tempFile=getCanonicalFileEL(tempFile);
            tmp.delete();
        }
        catch(IOException ioe) {}
        
        return tempFile;
    }
    
    /**
     * @return return System directory
     */
    private static File getSystemDirectory() {
    	String pathes=System.getProperty("java.library.path");
        if(pathes!=null) {
            String[] arr=pathes.split(File.pathSeparator);
            //String[] arr=List.toStringArrayEL(List.listToArray(pathes,File.pathSeparatorChar));
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("windows\\system")!=-1) {
                    File file = new File(arr[i]);
                    if(file.exists() && file.isDirectory() && file.canWrite()) return getCanonicalFileEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("windows")!=-1) {
                    File file = new File(arr[i]);
                    if(file.exists() && file.isDirectory() && file.canWrite()) return getCanonicalFileEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("winnt")!=-1) {
                    File file = new File(arr[i]);
                    if(file.exists() && file.isDirectory() && file.canWrite()) return getCanonicalFileEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {    
                if(arr[i].toLowerCase().indexOf("win")!=-1) {
                    File file = new File(arr[i]);
                    if(file.exists() && file.isDirectory() && file.canWrite()) return getCanonicalFileEL(file);
                    
                }
            }
            for(int i=0;i<arr.length;i++) {
                File file = new File(arr[i]);
                if(file.exists() && file.isDirectory() && file.canWrite()) return getCanonicalFileEL(file);
            }
        }
        return null;
    }
    
    private static File getCanonicalFileEL(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }
}
