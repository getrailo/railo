package railo.commons.lang;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;

import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public final class ExceptionUtil {
	
	public static String getStacktrace(Throwable t, boolean addMessage) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		String st = sw.toString();
		String msg=t.getMessage();
		if(addMessage && !StringUtil.isEmpty(msg) && !st.startsWith(msg.trim()))
			st=msg+"\n"+st;
		return st;
		
	}
	

	public static String getMessage(Throwable t) {
		String msg=t.getMessage();
		if(StringUtil.isEmpty(msg,true)) msg=t.getClass().getName();
		
		StringBuilder sb=new StringBuilder(msg);
		
		if(t instanceof PageException){
			PageException pe=(PageException)t;
			String detail = pe.getDetail();
			if(!StringUtil.isEmpty(detail,true)) {
				sb.append('\n');
				sb.append(detail);
			}
		}
		return sb.toString();
	}

	public static PageException addHint(PageExceptionImpl pe,String hint) {
		pe.setAdditional(KeyConstants._Hint, hint);
		return pe;
	}

	/**
	 * creates a message for key not found with soundex check for similar key
	 * @param keys
	 * @param keyLabel
	 * @return
	 */
	public static String similarKeyMessage(Collection.Key[] _keys,String keySearched, String keyLabel, String keyLabels, boolean listAll) {
		
		Arrays.sort(_keys);
		String list=ListUtil.arrayToList(_keys, ",");
		String keySearchedSoundex=StringUtil.soundex(keySearched);
		
		for(int i=0;i<_keys.length;i++){
			if(StringUtil.soundex(_keys[i].getString()).equals(keySearchedSoundex))
				return "The "+keyLabel+" ["+keySearched+"] does not exist, but there is a similar "+keyLabel+" with name ["+_keys[i].getString()+"] available"+(listAll?". Here is a complete list of all available "+keyLabels+": ["+list+"].":".");
		}
		return "The "+keyLabel+" ["+keySearched+"] does not exist"+(listAll?", only the following "+keyLabels+" are available: ["+list+"].":".");
	}
	

	public static String similarKeyMessage(Collection coll,String keySearched, String keyLabel, String keyLabels, boolean listAll) {
		return similarKeyMessage(CollectionUtil.keys(coll), keySearched, keyLabel, keyLabels,listAll);
	}

	public static IOException toIOException(Throwable t) {
		if(t instanceof IOException) return (IOException) t;
		if(t instanceof InvocationTargetException) return toIOException(((InvocationTargetException) t).getCause());
		if(t instanceof NativeException) return toIOException(((NativeException)t).getCause());
		
		IOException ioe = new IOException(t.getClass().getName()+":"+t.getMessage());
		ioe.setStackTrace(t.getStackTrace());
		return ioe;
	}


	public static String createSoundexDetail(String name, Iterator<String> it, String keyName) {
		StringBuilder sb=new StringBuilder();
		String k ,sname=StringUtil.soundex(name);
		while(it.hasNext()){
			k = it.next();
			if(StringUtil.soundex(k).equals(sname))
				return "did you mean ["+k+"]";
			if(sb.length()!=0)sb.append(',');
			sb.append(k);
		}
		return "available "+keyName+" are ["+sb+"]";
	}
}
