package railo.runtime.tag;

import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.ListUtil;

public class MissingAttribute {
	

	private final Key name;
	private final String type;
	private final String[] alias;

	public MissingAttribute(Key name, String type, String[] alias) {
		this.name=name;
		this.type=type;
		this.alias=alias;
	}

	public static MissingAttribute newInstance(Key name,String type){
		return new MissingAttribute(name,type,null);
	}
	public static MissingAttribute newInstance(String name,String type){
		return newInstance(KeyImpl.init(name),type,null);
	}

	public static MissingAttribute newInstance(Key name,String type, String[] alias){
		return new MissingAttribute(name,type,alias);
	}
	public static MissingAttribute newInstance(String name,String type, String[] alias){
		return newInstance(KeyImpl.init(name),type);
	}

	public String[] getAlias() {
		return alias;
	}

	/**
	 * @return the name
	 */
	public Key getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	

	@Override
	public String toString() {
		return "name:"+name+";type:"+type+";alias:"+(alias==null?"null":ListUtil.arrayToList(alias, ","))+";";
	}
}
