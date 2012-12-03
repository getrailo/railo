package railo.runtime.security;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

/**
 * Script-protect to remove cross-attacks from strings
 */
public final class ScriptProtect {
	
	public static final String[] invalids=new String[]{
		"object", "embed", "script", "applet", "meta", "iframe"
	};
	
	/**
	 * translate all strig values of the struct i script-protected form
	 * @param sct Struct to translate its values
	 */
	public static void translate(Struct sct) {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		Object value;
		while(it.hasNext()) {
			e = it.next();
			value=e.getValue(); 
			if(value instanceof String) {
				sct.setEL(e.getKey(),translate((String)value));
			}
		}
	}
	
	/**
	 * translate string to script-protected form
	 * @param str
	 * @return translated String
	 */
	public static String translate(String str) {
		// TODO do-while machen und StringBuffer 
		int index,last=0,endIndex;
		StringBuffer sb=null;
		String tagName;
		while((index=str.indexOf('<',last))!=-1) {
			// read tagname
			int len=str.length();
			char c;
			for(endIndex=index+1;endIndex<len;endIndex++) {
				c=str.charAt(endIndex);
				if((c<'a' || c>'z') && (c<'A' || c>'Z'))break;
			}
			tagName=str.substring(index+1,endIndex);

			if(compareTagName(tagName)) {
				if(sb==null) {
					sb=new StringBuffer();
					last=0;
				}
				sb.append(str.substring(last,index+1));
				sb.append("invalidTag");
				last=endIndex;
			}
			else if(sb!=null) {
				sb.append(str.substring(last,index+1));
				last=index+1;
			}
			else last=index+1;
			
		}
		if(sb!=null) {
			if(last!=str.length())sb.append(str.substring(last));
			return sb.toString(); 
		}
		return str;
	}
	
	
	private static boolean compareTagName(String tagName) {
		for(int i=0;i<invalids.length;i++) {
			if(invalids[i].equalsIgnoreCase(tagName)) return true;
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(translate("<hell <script><script susi=1><scriptsrc><> how are you <br />object <object ddd"));

	}
}
