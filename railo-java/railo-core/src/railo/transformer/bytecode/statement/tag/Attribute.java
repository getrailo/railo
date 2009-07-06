package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.expression.Expression;

public final class Attribute {
	
	String name;
	Expression value;
	private String type;
	private boolean dynamicType;
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
}
