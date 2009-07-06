package railo.commons.lang;

import java.io.UnsupportedEncodingException;

import railo.commons.io.SystemUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;





/**
 * Util to do some addional String Operations
 */
public final class StringUtil {
    
    /**
     * translate a String from local encoding to a specified encoding
     * @param str String to translate
     * @param charset charset to translate
     * @return translated string
     * @throws UnsupportedEncodingException
     */
    public static String translateString(String str, String charset) throws UnsupportedEncodingException {
        return new String(str.getBytes(SystemUtil.getCharset()),charset);
    }
    
    public static String translateString(String str, String charset, String defaultValue) {
        try {
            return translateString(str,charset);
        } catch (UnsupportedEncodingException e) {
            return defaultValue;
        }
    }
    
	/**
	 * do first Letter Upper case
	 * @param str String to operate
	 * @return uppercase string
	 */
	public static String ucFirst(String str) {
		if(str==null) return null;
		else if(str.length()<=1) return str.toUpperCase();
		else {
			return str.substring(0,1).toUpperCase()+str.substring(1);
		}
	}
	
	/**
	 * do first Letter Upper case
	 * @param str String to operate
	 * @return lower case String
	 */
	public static String lcFirst(String str) {
		if(str==null) return null;
		else if(str.length()<=1) return str.toLowerCase();
		else {
			return str.substring(0,1).toLowerCase()+str.substring(1);
		}
	}
	
	/**
	 * Unescapes HTML Tags
	 * @param html html code  to escape
	 * @return escaped html code
	 */
	public static String unescapeHTML(String html) {
		return HTMLEntities.unescapeHTML(html);
	}
	
	/**
	 * Escapes XML Tags
	 * @param html html code to unescape
	 * @return unescaped html code
	 */
	public static String escapeHTML(String html) {
		return HTMLEntities.escapeHTML(html);
	}
	
	/**
	 * escapes JS sensitive characters
	 * @param str String to escape
	 * @return escapes String
	 */
	public static String escapeJS(String str) {
		char[] arr=str.toCharArray();
		StringBuffer rtn=new StringBuffer(arr.length);
		for(int i=0;i<arr.length;i++) {
			switch(arr[i]) {
				case '\\': rtn.append("\\\\"); break;
				case '\n': rtn.append("\\n"); break;
				case '\r': rtn.append("\\r"); break;
				case '\f': rtn.append("\\f"); break;
				case '\b': rtn.append("\\b"); break;
				case '\t': rtn.append("\\t"); break;
				case '"' : rtn.append("\\\""); break;
				case '\'': rtn.append("\\\'"); break;
				default : rtn.append(arr[i]); break;
			}
		}
		return rtn.toString();
	}

	/**
	 * reapeats a string
	 * @param str string to repeat
	 * @param count how many time string will be reapeted
	 * @return reapted string
	 */
    public static String repeatString(String str,int count) {
        //StringBuffer rtn=new StringBuffer();
        if(count<=0) return "";
        char[] chars = str.toCharArray();
        char[] rtn=new char[chars.length*count];
        int pos=0;
        for(int i=0;i<count;i++) {
            for(int y=0;y<chars.length;y++)rtn[pos++]=chars[y];
            //rtn.append(str);
        }
        return new String(rtn);
    }
    /*
    public static String repeatString(String str,int count) {
        if(count<=0) return "";
        StringBuffer rtn=new StringBuffer(str.length()*count);
        for(int i=0;i<count;i++) {
            rtn.append(str);
        }
        return new String(rtn);
    }
	*/
	/**
	 * translate, like method toString, a object to a string, but when value is null value will be translated to a empty String (""). 
	 * @param o Object to convert
	 * @return converted String
	 */
	public static String toStringEmptyIfNull(Object o) {
		if(o==null)return "";
		return o.toString();
	}
	
	/**
	 * escape all special characters of the regular expresson language
	 * @param str String to escape
	 * @return escaped String
	 */
	public static String reqExpEscape(String str) {
		char[] arr = str.toCharArray();
		StringBuffer sb=new StringBuffer(str.length()*2);
		
		for(int i=0;i<arr.length;i++) {
			sb.append('\\');
			sb.append(arr[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * translate a string to a valid identity variable name
	 * @param varName variable name template to translate
	 * @return translated variable name
	 */
	public static String toIdentityVariableName(String varName) {
		char[] chars=varName.toCharArray();
		long changes=0;

		StringBuffer rtn=new StringBuffer(chars.length+2);
		rtn.append("CF");		
		
		for(int i=0;i<chars.length;i++) {
			char c=chars[i];
			if((c>='a' && c<='z') ||(c>='A' && c<='Z') ||(c>='0' && c<='9'))
				rtn.append(c);
			else {	
				rtn.append('_');
				changes+=(c*(i+1));
			}
		}
		
		return rtn.append(changes).toString();
	}
	/**
	 * translate a string to a valid classname string
	 * @param str string to translate
	 * @return translated String
	 */
	public static String toClassName(String str) {
		StringBuffer rtn=new StringBuffer();
		String[] arr=str.split("[\\\\|//]");
		for(int i=0;i<arr.length;i++) {
			if(arr[i].length()==0)continue;
			if(rtn.length()!=0)rtn.append('.');
			char[] chars=arr[i].toCharArray();
			long changes=0;
			for(int y=0;y<chars.length;y++) {
				char c=chars[y];
				if(y==0 && (c>='0' && c<='9'))rtn.append("_"+c);
				else if((c>='a' && c<='z') ||(c>='A' && c<='Z') ||(c>='0' && c<='9'))
					rtn.append(c);
				else {	
					rtn.append('_');
					changes+=(c*(i+1));
				}
			}
			if(changes>0)rtn.append(changes);
		}
		return rtn.toString();
	}

	/**
	 * translate a string to a valid variable string
	 * @param str string to translate
	 * @return translated String
	 */

	public static String toVariableName(String str) {
		return toVariableName(str, true);
	}
	public static String toVariableName(String str, boolean addIdentityNumber) {
		
		StringBuffer rtn=new StringBuffer();
		char[] chars=str.toCharArray();
		long changes=0;
		boolean doCorrect=true;
		for(int i=0;i<chars.length;i++) {
			char c=chars[i];
			if(i==0 && (c>='0' && c<='9'))rtn.append("_"+c);
			else if((c>='a' && c<='z') ||(c>='A' && c<='Z') ||(c>='0' && c<='9') || c=='_' || c=='$')
				rtn.append(c);
			else {	
			    doCorrect=false;
				rtn.append('_');
				//if(!(c=='.' && str.substring(i).equals(".cfm")))
				changes+=(c*(i+1));
			}
		}
		
		if(addIdentityNumber && changes>0)rtn.append(changes);
		//print.ln(" - "+rtn);
		
		if(doCorrect)return correctReservedWord(rtn.toString());
		return rtn.toString();
	}
	
	/*public static String toJavaIdentifier(String str) {
		StringBuffer rtn=new StringBuffer();
		char[] chars=str.toCharArray();
		boolean doCorrect=true;
		for(int i=0;i<chars.length;i++) {
			char c=chars[i];
			if(i==0 && (c>='0' && c<='9'))rtn.append("_"+c);
			else if((c>='a' && c<='z') ||(c>='A' && c<='Z') ||(c>='0' && c<='9') || c=='$')
				rtn.append(c);
			else {	
			    doCorrect=false;
				rtn.append("_"+toHex(c));
				//if(!(c=='.' && str.substring(i).equals(".cfm")))
				//changes+=(c*(i+1));
			}
		}
		
		if(doCorrect)return correctReservedWord(rtn.toString());
		return rtn.toString();
	}
	private static String toHex(char c) {
		return Integer.toHexString(c);
	}

	public static void main(String[] args) throws Exception {
		print(""+((char)1));
		print("Û");
		print("Š");
		print("_2sali");
	}
	
	
	
	private static void print(String str) throws Exception {
		print.out("++++");
		print.out((str));
		print.out(toJavaIdentifier(str));
		//print.out(URLEncoder.encode(str));
		//print.out(URLDecoder.decode(URLEncoder.encode(str)));
	}*/

	/**
	 * if given string is a keyword it will be replaced with none keyword
	 * @param str
	 * @return corrected word
	 */
	private static String correctReservedWord(String str) {
		char first=str.charAt(0);
		
		switch(first) {
		case 'a':
			if(str.equals("abstract")) return "_"+str;
		break;
		case 'b':
			if(str.equals("boolean")) return "_"+str;
			else if(str.equals("break")) return "_"+str;
			else if(str.equals("byte")) return "_"+str;
		break;
		case 'c':
			if(str.equals("case")) return "_"+str;
			else if(str.equals("catch")) return "_"+str;
			else if(str.equals("char")) return "_"+str;
			else if(str.equals("const")) return "_"+str;
			else if(str.equals("class")) return "_"+str;
			else if(str.equals("continue")) return "_"+str;
		break;
		case 'd':
			if(str.equals("default")) return "_"+str;
			else if(str.equals("do")) return "_"+str;
			else if(str.equals("double")) return "_"+str;
		break;
		case 'e':
			if(str.equals("else")) return "_"+str;
			else if(str.equals("extends")) return "_"+str;
			else if(str.equals("enum")) return "_"+str;
		break;
		case 'f':
			if(str.equals("false")) return "_"+str;
			else if(str.equals("final")) return "_"+str;
			else if(str.equals("finally")) return "_"+str;
			else if(str.equals("float")) return "_"+str;
			else if(str.equals("for")) return "_"+str;
		break;
		case 'g':
			if(str.equals("goto")) return "_"+str;
		break;
		case 'i':
			if(str.equals("if")) return "_"+str;
			else if(str.equals("implements")) return "_"+str;
			else if(str.equals("import")) return "_"+str;
			else if(str.equals("instanceof")) return "_"+str;
			else if(str.equals("int")) return "_"+str;
			else if(str.equals("interface")) return "_"+str;
		break;
		case 'n':
			if(str.equals("native")) return "_"+str;
			else if(str.equals("new")) return "_"+str;
			else if(str.equals("null")) return "_"+str;
		break;
		case 'p':
			if(str.equals("package")) return "_"+str;
			else if(str.equals("private")) return "_"+str;
			else if(str.equals("protected")) return "_"+str;
			else if(str.equals("public")) return "_"+str;
		break;
		case 'r':
			if(str.equals("return")) return "_"+str;
		break;
		case 's':
			if(str.equals("short")) return "_"+str;
			else if(str.equals("static")) return "_"+str;
			else if(str.equals("strictfp")) return "_"+str;
			else if(str.equals("super")) return "_"+str;
			else if(str.equals("switch")) return "_"+str;
			else if(str.equals("synchronized")) return "_"+str;
		break;
		case 't':
			if(str.equals("this")) return "_"+str;
			else if(str.equals("throw")) return "_"+str;
			else if(str.equals("throws")) return "_"+str;
			else if(str.equals("transient")) return "_"+str;
			else if(str.equals("true")) return "_"+str;
			else if(str.equals("try")) return "_"+str;
		break;
		case 'v':
			if(str.equals("void")) return "_"+str;
			else if(str.equals("volatile")) return "_"+str;
		break;
		case 'w':
			if(str.equals("while")) return "_"+str;
		break;
		}
		return str;
		
	}
	
	/** 
     * This function returns a string with whitespace stripped from the beginning of str 
     * @param str String to clean 
     * @return cleaned String 
     */ 
    public static String ltrim(String str,String defaultValue) { 
    		if(str==null) return defaultValue;
            int len = str.length(); 
            int st = 0; 

            while ((st < len) && (str.charAt(st) <= ' ')) { 
                st++; 
            } 
            return ((st > 0)) ? str.substring(st) : str; 
    } 
    
    /** 
     * This function returns a string with whitespace stripped from the end of str 
     * @param str String to clean 
     * @return cleaned String 
     */ 
    public static String rtrim(String str,String defaultValue) { 
    	if(str==null) return defaultValue;
            int len = str.length(); 

            while ((0 < len) && (str.charAt(len-1) <= ' ')) { 
                len--; 
            } 
            return (len < str.length()) ? str.substring(0, len) : str; 
    }	
    


    /**
     * return if in a string are line feeds or not
     * @param str string to check
     * @return translated string
     */
    public static boolean hasLineFeed(String str) {
        int len=str.length();
        char c;
        for(int i=0;i<len;i++) {
            c=str.charAt(i);
            if(c=='\n' || c=='\r') return true;
        }
        return false;
    }

    /**
     * remove all white spaces followd by whitespaces
     * @param str strring to translate
     * @return translated string
     */
    public static String suppressWhiteSpace(String str) {
        int len=str.length();
        StringBuffer sb=new StringBuffer(len);
        //boolean wasWS=false;
        
        char c;
        char buffer=0;
        for(int i=0;i<len;i++) {
            c=str.charAt(i);
            if(c=='\n' || c=='\r')		buffer='\n';
            else if(c==' ' || c=='\t')	{
            	if(buffer==0)buffer=c;
            }
            else {
            	if(buffer!=0){
            		sb.append(buffer);
            		buffer=0;
            	}
            	sb.append(c);
            }
            //sb.append(c);
        }
        if(buffer!=0)sb.append(buffer);
        
        return sb.toString();
    }
	


    /**
     * returns string, if given string is null or lengt 0 return default value
     * @param value
     * @param defaultValue
     * @return value or default value
     */
    public static String toString(String value, String defaultValue) {
        return value==null || value.length()==0?defaultValue:value;
    }

    /**
     * returns string, if given string is null or lengt 0 return default value
     * @param value
     * @param defaultValue
     * @return value or default value
     */
    public static String toString(Object value, String defaultValue) {
    	if(value==null) return defaultValue;
    	return toString(value.toString(), defaultValue);
    }

    /**
     * cut string to max size if the string is greater, otherweise to nothing
     * @param content
     * @param max 
     * @return cutted string
     */
    public static String max(String content,int max) {
        if(content==null) return null;
        if(content.length()<=max) return content;
        return content.substring(0,max);
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
     * adds zeros add the begin of a int example: addZeros(2,3) return "002"
     * @param i number to add nulls
     * @param size 
     * @return min len of return value;
     */
    public static String addZeros(int i, int size) {
        String rtn=Caster.toString(i);
        if(rtn.length()<size) return repeatString("0",size-rtn.length())+rtn;
        return rtn;
    }

    
    /**
     * adds zeros add the begin of a int example: addZeros(2,3) return "002"
     * @param i number to add nulls
     * @param size 
     * @return min len of return value;
     */
    public static String addZeros(long i, int size) {
        String rtn=Caster.toString(i);
        if(rtn.length()<size) return repeatString("0",size-rtn.length())+rtn;
        return rtn;
    }
    
	public static int indexOfIgnoreCase(String haystack, String needle) {
		if(StringUtil.isEmpty(haystack) || StringUtil.isEmpty(needle)) return -1;
		needle=needle.toLowerCase();
		
		int lenHaystack=haystack.length();
		int lenNeedle=needle.length();
		
		char lastNeedle=needle.charAt(lenNeedle-1);
		char c;
		outer:for(int i=lenNeedle-1;i<lenHaystack;i++) {
			c=Character.toLowerCase(haystack.charAt(i));
			if(c==lastNeedle) {
				for(int y=0;y<lenNeedle-1;y++) {
					if(needle.charAt(y)!=Character.toLowerCase(haystack.charAt(i-(lenNeedle-1)+y)))
							continue outer;
				}
				return i-(lenNeedle-1);
			}
		}
		
		
		return -1;
	}
    
    /**
     * Tests if this string starts with the specified prefix.
     * @param str string to check first char
     * @param prefix the prefix.
     * @return is first of given type
     */
    public static boolean startsWith(String str, char prefix) {
        return str!=null && str.length()>0 && str.charAt(0)==prefix;
    }
    
    /**
     * Tests if this string ends with the specified suffix.
     * @param str string to check first char
     * @param suffix the suffix.
     * @return is last of given type
     */
    public static boolean endsWith(String str, char suffix) {
        return str!=null && str.length()>0 && str.charAt(str.length()-1)==suffix;
    }

    /**
     * Tests if this string ends with the specified suffix.
     * @param str string to check first char
     * @param suffix the suffix.
     * @return is last of given type
     */
    /**
     * Helper functions to query a strings start portion. The comparison is case insensitive.
     *
     * @param base  the base string.
     * @param start  the starting text.
     *
     * @return true, if the string starts with the given starting text.
     */
    public static boolean startsWithIgnoreCase(final String base, final String start) {
        if (base.length() < start.length()) {
            return false;
        }
        return base.regionMatches(true, 0, start, 0, start.length());
    }

    /**
     * Helper functions to query a strings end portion. The comparison is case insensitive.
     *
     * @param base  the base string.
     * @param end  the ending text.
     *
     * @return true, if the string ends with the given ending text.
     */
    public static boolean endsWithIgnoreCase(final String base, final String end) {
        if (base.length() < end.length()) {
            return false;
        }
        return base.regionMatches(true, base.length() - end.length(), end, 0, end.length());
    }

    

    /**
     * returns if byte arr is a BOM character Stream (UTF-8,UTF-16)
    * @param barr
    * @return is BOM or not
    */
   public static  boolean isBOM(byte[] barr) {
        return barr.length>=3 && barr[0]==0xEF && barr[1]==0xBB && barr[2]==0xBF;
    }

    /**
     * return "" if value is null otherwise return same string
     * @param str
     * @return string (not null)
     */
    public static String valueOf(String str) {
        if(str==null)return "";
        return str;
    }

    
    /**
     * cast a string a lower case String, is faster than the String.toLowerCase, if all Character are already Low Case
     * @param str
     * @return lower case value
     */
    public static String toLowerCase(String str) {
        int len=str.length();
        char c;
        for(int i=0;i<len;i++) {
            c=str.charAt(i);
            if(!((c>='a' && c<='z') || (c>='0' && c<='9'))) {
                return str.toLowerCase();
            }
        }
        
        return str;
    }
    public static String toUpperCase(String str) {
        int len=str.length();
        char c;
        for(int i=0;i<len;i++) {
            c=str.charAt(i);
            if(!((c>='A' && c<='Z') || (c>='0' && c<='9'))) {
                return str.toUpperCase();
            }
        }
        
        return str;
    }

    /**
     * soundex function
     * @param str
     * @return soundex from given string
     */
    public static String soundex(String str) {
        return new org.apache.commons.codec.language.Soundex().soundex(str);
    }

    /**
     * return the last character of a string, if string ist empty return 0;
     * @param str string to get last character
     * @return last character
     */
    public static char lastChar(String str) {
        if(str==null || str.length()==0) return 0;
        return str.charAt(str.length()-1);
    }
    

    /**
     * 
     * @param str
     * @return return if a String is "Empty", that means NULL or String with length 0 (whitespaces will not counted) 
     */
    public static boolean isEmpty(String str) {
        return str==null || str.length()==0;
    }
    /**
     * 
     * @param str
     * @return return if a String is "Empty", that means NULL or String with length 0 (whitespaces will not counted) 
     */
    public static boolean isEmpty(String str, boolean trim) {
        if(!trim) return isEmpty(str);
        return str==null || str.trim().length()==0;
    }
    
    /**
     * return the first character of a string, if string ist empty return 0;
     * @param str string to get first character
     * @return first character
     */
    public static char firstChar(String str) {
        if(isEmpty(str)) return 0;
        return str.charAt(0);
    }

	/**
	 * change charset of string from system default to givenstr
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String changeCharset(String str, String charset) throws UnsupportedEncodingException {
		if(str==null) return str;
		return new String(str.getBytes(charset),charset);
	}

	/**
	 * change charset of string from system default to givenstr
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String changeCharset(String str, String charset, String defaultValue) {
		if(str==null) return str;
		try {
			return new String(str.getBytes(SystemUtil.getCharset()),charset);
		} catch (UnsupportedEncodingException e) {
			return defaultValue;
		}
	}

	public static boolean isWhiteSpace(char c) {
		return c<=' ';
	}

	public static String removeWhiteSpace(String str) {
		if(isEmpty(str)) return str;
		StringBuffer sb=new StringBuffer();
		char[] carr = str.trim().toCharArray();
		for(int i=0;i<carr.length;i++) {
			if(!isWhiteSpace(carr[i]))sb.append(carr[i]);
		}
		return sb.toString();
	}

	public static String replaceLast(String str, char from, char to) {
		int index = str.lastIndexOf(from);
		if(index==-1)return str;
		return str.substring(0,index)+to+str.substring(index+1);
	}
	public static String replaceLast(String str, String from, String to) {
		int index = str.lastIndexOf(from);
		if(index==-1)return str;
		return str.substring(0,index)+to+str.substring(index+from.length());
	}

	/**
	 * removes quotes(",') that wraps the string
	 * @param string
	 * @return
	 */
	public static String removeQuotes(String string,boolean trim) {
		if(trim)string=string.trim();
		if((StringUtil.startsWith(string, '"') && StringUtil.endsWith(string, '"')) || (StringUtil.startsWith(string, '\'') && StringUtil.endsWith(string, '\''))){
			string= string.substring(1,string.length()-1);
			if(trim)string=string.trim();
		}
		return string;
	}

	public static boolean isEmpty(Object obj, boolean trim) {
		if(obj==null) return true;
		if(obj instanceof String)return isEmpty((String)obj,trim);
		if(obj instanceof StringBuffer)return isEmpty((StringBuffer)obj,trim);
		if(obj instanceof Collection.Key)return isEmpty(((Collection.Key)obj).getString(),trim);
		return false;
	}
	
	public static boolean isEmpty(Object obj) {
		if(obj==null) return true;
		if(obj instanceof String)return isEmpty((String)obj);
		if(obj instanceof StringBuffer)return isEmpty((StringBuffer)obj);
		if(obj instanceof Collection.Key)return isEmpty(((Collection.Key)obj).getString());
		return false;
	}

	public static boolean isEmpty(StringBuffer sb,boolean trim) {
		if(trim) return sb==null || sb.toString().trim().length()==0;
		return sb==null || sb.length()==0;
	}

	public static boolean isEmpty(StringBuffer sb) {
		return sb==null || sb.length()==0;
	}

	public static String removeStarting(String str, String sub) {
		if(isEmpty(str) || isEmpty(sub) || !str.startsWith(sub)) return str;
		return str.substring(sub.length());
	}

	public static String removeStartingIgnoreCase(String str, String sub) {
		if(isEmpty(sub) || !startsWithIgnoreCase(str, sub)) return str;
		return str.substring(sub.length());
	}

	
	public static String[] merge(String str, String[] arr) {
		String[] narr=new String[arr.length+1];
    	narr[0]=str;
    	for(int i=0;i<arr.length;i++) {
    		narr[i+1]=arr[i];
    	}
    	return narr;
    	
	}

	public static int length(String str) {
		if(str==null) return 0;
		return str.length();
	}

	public static boolean hasUpperCase(String str) {
		if(isEmpty(str)) return false;
		return !str.equals(str.toLowerCase());
	}

	/**
	 * trim given value, return defaultvalue when input is null
	 * @param string
	 * @param defaultValue
	 * @return trimmed string or defaultValue
	 */
	public static String trim(String str,String defaultValue) {
		if(str==null) return defaultValue;
		return str.trim();
	}

	public static boolean contains(String str, String substr) {
		if(str==null) return false;
		return str.indexOf(substr)!=-1;
	}

	public static boolean containsIgnoreCase(String str, String substr) {
		return indexOfIgnoreCase(str,substr)!=-1;
	}
}