package railo.runtime.type.util;

public class StringListData {

	public final String list;
	public final String delimiter;
	public final boolean includeEmptyFieldsx;
	public final boolean multiCharacterDelimiter;

	public StringListData(String list, String delimiter, boolean includeEmptyFields, boolean multiCharacterDelimiter) {
		this.list=list;
		this.delimiter=delimiter;
		this.includeEmptyFieldsx=includeEmptyFields;
		this.multiCharacterDelimiter=multiCharacterDelimiter;
	}

}
