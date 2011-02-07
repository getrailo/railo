package railo.commons.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.type.List;

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

	public static PageException addHint(PageExceptionImpl pe,String hint) {
		pe.setAdditional("Hint", hint);
		return pe;
	}

	/**
	 * creates a message for key not found with soundex check for similar key
	 * @param keys
	 * @param keyLabel
	 * @return
	 */
	public static String similarKeyMessage(String[] keys,String keySearched, String keyLabel, String keyLabels) {
		
		Arrays.sort(keys);
		String list=List.arrayToList(keys, ",");
		String keySearchedSoundex=StringUtil.soundex(keySearched);
		
		for(int i=0;i<keys.length;i++){
			if(StringUtil.soundex(keys[i]).equals(keySearchedSoundex))
				return keyLabel+" ["+keySearched+"] does not exist, but there is a similar "+keyLabel+" ["+keys[i]+"] available, complete list of all available "+keyLabels+" ["+list+"]";
		}
		return keyLabel+" ["+keySearched+"] does not exist, only the followings are available "+keyLabels+" ["+list+"]";
	}
}
