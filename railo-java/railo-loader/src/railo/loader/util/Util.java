package railo.loader.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipFile;

import railo.commons.io.res.Resource;
import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.exp.PageException;

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
     * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().copy(...)
     * copy a inputstream to a outputstream
     * @param in 
     * @param out
     * @throws IOException
     */
    public final static void copy(InputStream in, OutputStream out) throws IOException {
    	CFMLEngineFactory.getInstance().getIOUtil().copy(in, out, true, true);
    }
    
    /**
     * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().toString(InputStream is, Charset cs)
      * read String data from a InputStream and returns it as String Object 
     * @param is InputStream to read data from.
     * @return readed data from InputStream
     * @throws IOException
     */
	public static String toString(InputStream is) throws IOException {
		return CFMLEngineFactory.getInstance().getIOUtil().toString(is, null);
	}

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance.getCastUtil().toBooleanValue(...)
     * @param str
     * @return
     * @throws IOException
     */
	public static boolean toBooleanValue(String str) throws PageException {
		return CFMLEngineFactory.getInstance().getCastUtil().toBooleanValue(str);
	}
    

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().closeSilent(InputStream is,OutputStream os)
      * close inputstream without a Exception
     * @param is 
     * @param os 
     */
	public static void closeEL(InputStream is,OutputStream os) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(is, os);
	}

     /**
      * @deprecated no replacement
      * @param zf
      */
	public static void closeEL(ZipFile zf) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(zf);
	}

     /**
      * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().closeSilent(InputStream is)
      * close inputstream without a Exception
      * @param is 
      */
	public static void closeEL(InputStream is) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(is);
	}

      /**
       * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().closeSilent(Reader r)
       * close reader without a Exception
       * @param is 
       */
	public static void closeEL(Reader r) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(r);
	}

       /**
        * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().closeSilent(Writer w)
        * close reader without a Exception
        * @param is 
        */
	public static void closeEL(Writer w) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(w);
	}

     
     /**
      * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().closeSilent(InputStream is,OutputStream os)
      * close outputstream without a Exception
      * @param os 
      */
	public static void closeEL(OutputStream os) {
		CFMLEngineFactory.getInstance().getIOUtil().closeSilent(os);
	}

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().toString(...)
     * @param is inputStream to get content From
     * @param charset
     * @return returns content from a file inputed by input stream
     * @throws IOException
     * @throws PageException 
     */
	public static String getContentAsString(InputStream is, String charset) throws IOException, PageException {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		return engine.getIOUtil().toString(is, engine.getCastUtil().toCharset(charset));
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

	/**
	 * @deprecated no replacement
	 * @param str
	 * @return
	 */
	public static int length(String str) {
		if(str==null) return 0;
		return str.length();
	}
	
	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getStringUtil().replace(...)
     * @param str String to work with
     * @param sub1 value to replace
     * @param sub2 replacement
     * @param onlyFirst replace only first or all 
     * @return new String
     */
	public static String replace(String str, String sub1, String sub2, boolean onlyFirst) {
		return CFMLEngineFactory.getInstance().getStringUtil().replace(str, sub1, sub2, onlyFirst, false);
	}

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance().getResourceUtil().parsePlaceHolder(...)
     * replace path placeholder with the real path, placeholders are [{temp-directory},{system-directory},{home-directory}]
     * @param path
     * @return updated path
     */
    public static String parsePlaceHolder(String path) {
        return CFMLEngineFactory.getInstance().getResourceUtil().parsePlaceHolder(path);
    }
    
    /**
     * @deprecated use instead CFMLEngineFactory.getInstance().getResourceUtil().getTempDirectory()
     * returns the Temp Directory of the System
     * @return temp directory
     */
    public static File getTempDirectory() {
    	return (File)CFMLEngineFactory.getInstance().getResourceUtil().getTempDirectory();
    }
    
    /**
     * @deprecated use instead CFMLEngineFactory.getInstance().getResourceUtil().getHomeDirectory()
     * returns the Home Directory of the System
     * @return home directory
     */
    public static File getHomeDirectory() {
    	return (File)CFMLEngineFactory.getInstance().getResourceUtil().getHomeDirectory();
    }
    

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance().getResourceUtil().getSystemDirectory()
     * @return return System directory
     */
    public static File getSystemDirectory() {
    	return (File)CFMLEngineFactory.getInstance().getResourceUtil().getSystemDirectory();	
    }
    
    /**
     * @deprecated no replacement
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
    
    /**
     * @deprecated deprecated with no replacement
     * @param date
     * @return
     */
	public static String toHTTPTimeString(Date date) {
		return replace(HTTP_TIME_STRING_FORMAT.format(date),"+00:00","",true);
	}

	/**
	 * @deprecated deprecated with no replacement
	 * @return
	 */
	public static String toHTTPTimeString() {
		return replace(HTTP_TIME_STRING_FORMAT.format(new Date()),"+00:00","",true);
	}
	
	/**
	 * @deprecated deprecated with no replacement
	 */
	public static boolean hasUpperCase(String str) {
		if(isEmpty(str)) return false;
		return !str.equals(str.toLowerCase());
	}

	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getIOUtil().toBufferedInputStream(...)
	 * @param os
	 * @return
	 */
	public static BufferedInputStream toBufferedInputStream(InputStream is) {
		return CFMLEngineFactory.getInstance().getIOUtil().toBufferedInputStream(is);
	}

	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getIOUtil().toBufferedOutputStream(...)
	 * @param os
	 * @return
	 */
	public static BufferedOutputStream toBufferedOutputStream(OutputStream os) {
		return CFMLEngineFactory.getInstance().getIOUtil().toBufferedOutputStream(os);
	}

    /**
     * @deprecated use instead CFMLEngineFactory.getInstance.getIOUtil().copy(...)
     * @param in
     * @param out
     * @throws IOException
     */
	public static void copy(Resource in, Resource out) throws IOException {
		CFMLEngineFactory.getInstance().getIOUtil().copy(in, out);
	}

	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getStringUtil().toVariableName(...)
     * @param str
	 * @param addIdentityNumber
	 * @return
	 */
	public static String toVariableName(String str, boolean addIdentityNumber) {
		return CFMLEngineFactory.getInstance().getStringUtil().toVariableName(str, addIdentityNumber);
	}

	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getStringUtil().first(...);
     * @param str
	 * @param delimiter
	 * @return
	 */
	public static String first(String str,String delimiter){
		return CFMLEngineFactory.getInstance().getStringUtil().first(str, delimiter, true);
	}
	
	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getStringUtil().last(...);
	 * @param str
	 * @param delimiter
	 * @return
	 */
	public static String last(String str,String delimiter){
		return CFMLEngineFactory.getInstance().getStringUtil().last(str, delimiter, true);
	}

	/**
	 * @deprecated use instead CFMLEngineFactory.getInstance().getStringUtil().removeQuotes(...);
	 * @param str
	 * @param trim
	 * @return
	 */
	public static String removeQuotes(String str, boolean trim) {
		return CFMLEngineFactory.getInstance().getStringUtil().removeQuotes(str, trim);
	}
}
