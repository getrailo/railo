package railo.transformer.bytecode.statement.tag;

import railo.transformer.expression.Expression;

public final class Attribute {
	
	final String name;
	final Expression value;
	private final String type;
	private final boolean dynamicType;
	private boolean defaultAttribute;
	private String setterName;
	private final boolean isDefaultValue;
	
	public Attribute(boolean dynamicType,String name, Expression value, String type) {
		this(dynamicType,name, value, type, false);
	}
	
	public Attribute(boolean dynamicType,String name, Expression value, String type, boolean isDefaultValue) {
		this.dynamicType = dynamicType;
		this.name = name;
		this.value = value;
		this.type = type;
		this.isDefaultValue = isDefaultValue;
	}
	

	public boolean isDefaultValue() {
		return isDefaultValue;
	}
	public boolean isDefaultAttribute() {
		return defaultAttribute;
	}

	public void setDefaultAttribute(boolean defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
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
	
	@Override
	public String toString(){
		return "name:"+this.name
		+";value:"+this.value
		+";type:"+this.type
		+";dynamicType:"+this.dynamicType
		+";setterName:"+this.setterName;
	}
}
