package railo.runtime.type.util;

public class StringListData {

	public final String list;
	public final String delimiter;
	public final boolean includeEmptyFields;

	public StringListData(String list, String delimiter, boolean includeEmptyFields) {
		this.list=list;
		this.delimiter=delimiter;
		this.includeEmptyFields=includeEmptyFields;
	}

}
