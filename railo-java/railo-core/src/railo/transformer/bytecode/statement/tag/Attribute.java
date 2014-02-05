package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.expression.Expression;

public final class Attribute {
	
	final String name;
	final Expression value;
	private final String type;
	private final boolean dynamicType;
	private String setterName;
	
	public Attribute(boolean dynamicType,String name, Expression value, String type) {
		this.dynamicType = dynamicType;
		this.name = name;
		this.value = value;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Expression getValue() {
		return value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the dynamicType
	 */
	public boolean isDynamicType() {
		return dynamicType;
	}
	
	public String toString(){
		return "name:"+this.name
		+";value:"+this.value
		+";type:"+this.type
		+";dynamicType:"+this.dynamicType
		+";setterName:"+this.setterName;
	}
}
