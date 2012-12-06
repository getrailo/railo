package railo.runtime.tag;

import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

public class MissingAttribute {
	

	private Key name;
	private String type;

	public MissingAttribute(Key name, String type) {
		this.name=name;
		this.type=type;
	}

	public static MissingAttribute newInstance(Key name,String type){
		return new MissingAttribute(name,type);
	}
	public static MissingAttribute newInstance(String name,String type){
		return newInstance(KeyImpl.init(name),type);
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
		return "name:"+name+";type:"+type+";";
	}
}
