/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.loader.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import railo.commons.io.res.Resource;

/**
 * Util class for different little jobs
 */
public class Util {
    
    private static File tempFile;
    private static File homeFile;
    
    private final static SimpleDateFormat HTTP_TIME_STRING_FORMAT;
	static {
		HTTP_TIME_STRING_FORMAT = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH);
		HTTP_TIME_STRING_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
    

    /**
     * copy a inputstream to a outputstream
     * @param in 
     * @param out
     * @throws IOException
     */
    public final static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[0xffff];
        int len;
        while((len = in.read(buffer)) !=-1)
          out.write(buffer, 0, len);
        
        closeEL(in);
        closeEL(out);
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
    

	public static boolean toBooleanValue(String str) throws IOException {
		str=str.trim().toLowerCase();

		if("true".equals(str)) return true;
		if("false".equals(str)) return false;
		if("yes".equals(str)) return true;
		if("no".equals(str)) return false;
		throw new IOException("can't cast string to a boolean value");
	}
    

    /**
     * close inputstream without a Exception
     * @param is 
     * @param os 
     */
     public static void closeEL(InputStream is,OutputStream os) {
         closeEL(is);
         closeEL(os);
      }

     /**
      * close inputstream without a Exception
      * @param is 
      */
      public static void closeEL(InputStream is) {
           try {
             if(is!=null)is.close();
         } 
         catch (Throwable e) {}
       }

      /**
       * close reader without a Exception
       * @param is 
       */
       public static void closeEL(Reader r) {
            try {
              if(r!=null)r.close();
          } 
          catch (Throwable e) {}
        }

       /**
        * close reader without a Exception
        * @param is 
        */
        public static void closeEL(Writer w) {
           try {
               if(w!=null)w.close();
           } 
           catch (Throwable e) {}
         }

     
     /**
      * close outputstream without a Exception
      * @param os 
      */
     public static void closeEL(OutputStream os) {
           try {
               if(os!=null)os.close();
         } 
         catch (Throwable e) {}
       }
     
    /**
     * @param is inputStream to get content From
     * @param charset
     * @return returns content from a file inputed by input stream
     * @throws IOException
     */
    public static String getContentAsString(InputStream is, String charset) throws IOException {
    
        BufferedReader br = (charset==null)?
                new BufferedReader(new InputStreamReader(is)):
                new BufferedReader(new InputStreamReader(is,charset)); 
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
     * check if string is empty (null or "")
     * @param str
     * @return is empty or not
     */
    public static boolean isEmpty(String str) {
        return str==null || str.length()==0;
    }

    /**
     * check if string is empty (null or "")
     * @param str
     * @return is empty or not
     */
    public static boolean isEmpty(String str, boolean trim) {
        if(!trim) return isEmpty(str);
        return str==null || str.trim().length()==0;
    }


	public static int length(String str) {
		if(str==null) return 0;
		return str.length();
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
        
        
        //intVersion=(major*1000000)+(minor*10000)+(releases*100)+patches;
        
    }
    
    public static String toStringVersion(int version) {
        
    	StringBuffer sb=new StringBuffer();

    	// Major
    	int tmp=(version/1000000); // FUTURE 10000000
    	version-=tmp*1000000; // FUTURE 10000000
    	sb.append(String.valueOf(tmp));
    	sb.append(".");

    	// Minor
    	tmp=(version/10000); // FUTURE 100000
    	version-=tmp*10000; // FUTURE 100000
    	sb.append(len(String.valueOf(tmp),2));
    	sb.append(".");

    	// releases
    	tmp=(version/100); // FUTURE 1000
    	version-=tmp*100; // FUTURE 1000
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

	/**
     * @param str String to work with
     * @param sub1 value to replace
     * @param sub2 replacement
     * @param onlyFirst replace only first or all 
     * @return new String
     */
    public static String replace(String str, String sub1, String sub2, boolean onlyFirst) {
        if(sub1.equals(sub2)) return str;
        
        if(!onlyFirst && sub1.length()==1 && sub2.length()==1)return str.replace(sub1.charAt(0),sub2.charAt(0));
        
        
        StringBuffer sb=new StringBuffer();
        int start=0;
        int pos;
        int sub1Length=sub1.length();
        
        while((pos=str.indexOf(sub1,start))!=-1){
            sb.append(str.substring(start,pos));
            sb.append(sub2);
            start=pos+sub1Length;
            if(onlyFirst)break;
        }
        sb.append(str.substring(start));
        
        return sb.toString();
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
    
    /**
     * returns the Temp Directory of the System
     * @return temp directory
     */
    public static File getTempDirectory() {
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
     * returns the Hoome Directory of the System
     * @return home directory
     */
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
     * @return return System directory
     */
    public static File getSystemDirectory() {
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
    
    /**
     * Returns the canonical form of this abstract pathname.
     * @param file file to get canoncial form from it
     *
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed.
     */
    public static File getCanonicalFileEL(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }
    	
	public static String toHTTPTimeString(Date date) {
		return replace(HTTP_TIME_STRING_FORMAT.format(date),"+00:00","",true);
	}
    	
	public static String toHTTPTimeString() {
		return replace(HTTP_TIME_STRING_FORMAT.format(new Date()),"+00:00","",true);
	}
	
	public static boolean hasUpperCase(String str) {
		if(isEmpty(str)) return false;
		return !str.equals(str.toLowerCase());
	}

	public static BufferedInputStream toBufferedInputStream(InputStream is) {
		if(is instanceof BufferedInputStream) return (BufferedInputStream) is;
		return new BufferedInputStream(is);
	}
    
    public static BufferedOutputStream toBufferedOutputStream(OutputStream os) {
		if(os instanceof BufferedOutputStream) return (BufferedOutputStream) os;
		return new BufferedOutputStream(os);
	}

    public static void copy(Resource in, Resource out) throws IOException {
		InputStream is=null;
		OutputStream os=null;
		try {
			is=toBufferedInputStream(in.getInputStream());
			os=toBufferedOutputStream(out.getOutputStream());
		}
		catch(IOException ioe) {
			closeEL(os);
			closeEL(is);
			throw ioe;
		}
		copy(is,os);
	}
    
    public static String toVariableName(String str, boolean addIdentityNumber) {
		StringBuffer rtn=new StringBuffer();
		char[] chars=str.toCharArray();
		long changes=0;
		for(int i=0;i<chars.length;i++) {
			char c=chars[i];
			if(i==0 && (c>='0' && c<='9'))rtn.append("_"+c);
			else if((c>='a' && c<='z') ||(c>='A' && c<='Z') ||(c>='0' && c<='9') || c=='_' || c=='$')
				rtn.append(c);
			else {	
			    rtn.append('_');
				changes+=(c*(i+1));
			}
		}
		if(addIdentityNumber && changes>0)rtn.append(changes);
		return rtn.toString();
	}
    
    public static String first(String str,String delimiter){
		StringTokenizer st=new StringTokenizer(str,delimiter);
		return st.nextToken();
	}
	
	public static String last(String str,String delimiter){
		StringTokenizer st=new StringTokenizer(str,delimiter);
		String rtn=null;
		while(st.hasMoreTokens())
			rtn= st.nextToken();
		return rtn;
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
}
