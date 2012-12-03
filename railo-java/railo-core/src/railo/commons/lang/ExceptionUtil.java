package railo.commons.lang;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.List;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;

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
	public static String similarKeyMessage(Collection.Key[] _keys,String keySearched, String keyLabel, String keyLabels) {
		
		Arrays.sort(_keys);
		String list=List.arrayToList(_keys, ",");
		String keySearchedSoundex=StringUtil.soundex(keySearched);
		
		for(int i=0;i<_keys.length;i++){
			if(StringUtil.soundex(_keys[i].getString()).equals(keySearchedSoundex))
				return keyLabel+" ["+keySearched+"] does not exist, but there is a similar "+keyLabel+" ["+_keys[i].getString()+"] available, complete list of all available "+keyLabels+" ["+list+"]";
		}
		return keyLabel+" ["+keySearched+"] does not exist, only the followings are available "+keyLabels+" ["+list+"]";
	}
	

	public static String similarKeyMessage(Collection coll,String keySearched, String keyLabel, String keyLabels) {
		return similarKeyMessage(CollectionUtil.keys(coll), keySearched, keyLabel, keyLabels);
	}

	public static IOException toIOException(Throwable t) {
		if(t instanceof IOException) return (IOException) t;
		if(t instanceof InvocationTargetException) return toIOException(((InvocationTargetException) t).getCause());
		if(t instanceof NativeException) return toIOException(((NativeException)t).getCause());
		
		IOException ioe = new IOException(t.getClass().getName()+":"+t.getMessage());
		ioe.setStackTrace(t.getStackTrace());
		return ioe;
	}
}
