package railo.runtime.orm.hibernate;


public class ColumnInfo {

	private String name;
	private int type;
	private String typeName;
	private int size;
	private boolean nullable;
	
	public ColumnInfo(String name, int type, String typeName, int size,boolean nullable) {
		
		
		this.name = name;
		this.type = type;
		this.typeName = typeName;
		this.size = size;
		this.nullable = nullable;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}
	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}
	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public String toString() {
		return "name:"+name+";type:"+type+";typeName:"+typeName+";size:"+size+";nullable:"+nullable+";";
	}	
}
