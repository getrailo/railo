package railo.runtime.op;

import railo.commons.digest.HashUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ListUtil;
import railo.runtime.util.Strings;

public class StringsImpl implements Strings {

	private static Strings singelton;

	public static Strings getInstance() {
		if(singelton==null)singelton=new StringsImpl();
		return singelton;
	}

	@Override
	public String replace(String input, String find, String repl, boolean firstOnly, boolean ignoreCase) {
		return StringUtil.replace(input, find, repl, firstOnly, ignoreCase);
	}

	@Override
	public String toVariableName(String str, boolean addIdentityNumber,boolean allowDot) {
		return StringUtil.toVariableName(str, addIdentityNumber,allowDot);
	}

	@Override
	public String first(String list, String delimiter, boolean ignoreEmpty) {
		return ListUtil.first(list, delimiter,ignoreEmpty);
	}
	
	@Override
	public String last(String list, String delimiter, boolean ignoreEmpty) {
		return ListUtil.last(list, delimiter,ignoreEmpty);
	}

	@Override
	public String removeQuotes(String str, boolean trim) {
		return StringUtil.removeQuotes(str, trim);
	}
	
	@Override
	public long create64BitHash(CharSequence cs) {
		return HashUtil.create64BitHash(cs);
	}
}
