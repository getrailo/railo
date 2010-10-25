package railo.transformer.library.function;

import java.io.IOException;

import railo.commons.lang.Md5;



/**
 * Eine FunctionLibFunctionArg repr�sentiert ein einzelnes Argument einer Funktion.
 */
public final class FunctionLibFunctionArg {
	

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	private String strType;
	private boolean required;
	private FunctionLibFunction function;
	private String name;
	private String description="";
	private String defaultValue=null;
	private boolean hidden;

	
	/**
	 * Gesch�tzer Konstruktor ohne Argumente.
	 */
	public FunctionLibFunctionArg() {}
	public FunctionLibFunctionArg(FunctionLibFunction function) {
		this.function=function;
	}

	/**
	 * Gibt den Typ des Argument als String zur�ck (query, struct, string usw.)
	 * @return Typ des Argument
	 */
	public String getTypeAsString() {
		return this.strType;
	}

	/**
	 * Gibt den Typ des Argument zur�ck (query, struct, string usw.)
	 * @return Typ des Argument
	 */
	public String getType() {
		return strType;
	}

	/**
	 * Gibt zur�ck, ob das Argument Pflicht ist oder nicht, alias f�r isRequired.
	 * @return Ist das Argument Pflicht.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Gibt zur�ck, ob das Argument Pflicht ist oder nicht.
	 * @return Ist das Argument Pflicht.
	 */
	public boolean getRequired() {
		return required;
	}

	/**
	 * Gibt die Funktion zur�ck zu der das Argument geh�rt.
	 * @return Zugeh�rige Funktion.
	 */
	public FunctionLibFunction getFunction() {
		return function;
	}

	/**
	 * Setzt die Funktion zu der das Argument geh�rt.
	 * @param function Zugeh�rige Funktion.
	 */
	protected void setFunction(FunctionLibFunction function) {
		this.function = function;
	}

	/**
	 * Setzt, den Typ des Argument (query, struct, string usw.)
	 * @param type Typ des Argument.
	 */
	public void setType(String type) {
		this.strType = type;
	}

	/**
	 * Setzt, ob das Argument Pflicht ist oder nicht.
	 * @param value Ist das Argument Pflicht.
	 */
	public void setRequired(String value) {
		value=value.toLowerCase().trim();
		required=(value.equals("yes") || value.equals("true"));
	}
	public void setRequired(boolean value) {
		required=value;
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

	public Object getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getHash() {
		StringBuffer sb=new StringBuffer();
		sb.append(this.getDefaultValue());
		sb.append(this.getName());
		sb.append(this.getRequired());
		sb.append(this.getType());
		sb.append(this.getTypeAsString());
		
		try {
			return Md5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
	}
	public void setHidden(boolean hidden) {
		this.hidden=hidden;
	}
}