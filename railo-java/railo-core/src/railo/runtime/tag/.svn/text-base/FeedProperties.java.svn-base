package railo.runtime.tag;

import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public class FeedProperties {
	private static final Collection.Key ITEM = KeyImpl.getInstance("ITEM");
	private static final Collection.Key ENTRY = KeyImpl.getInstance("ENTRY");
	
	
	public static Struct toProperties(Struct data) {
		data=(Struct) data.duplicate(false);
		data.removeEL(ITEM);
		data.removeEL(ENTRY);
		
		return data;
	}
}
