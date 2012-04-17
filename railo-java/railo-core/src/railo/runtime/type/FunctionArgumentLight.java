package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExternalizableUtil;

/**
 * a single argument of a function, this is lightway function, just contain name and type (return default value for the rest)
 */
public final class FunctionArgumentLight implements FunctionArgument,Externalizable {
	
	private Collection.Key name;
	private short type;
	private String strType;
	

	
	/**
	 * NEVER USE THIS CONSTRUCTOR, this constructor is only for deserialize this object from stream
	 */
	public FunctionArgumentLight() {}

	
	public FunctionArgumentLight(Collection.Key name) {
		this(name, "any", CFTypes.TYPE_ANY);
	}

	public FunctionArgumentLight(Collection.Key name,short type) {
		this(name, CFTypes.toString(type,"any"), type);
	}

	public FunctionArgumentLight(Collection.Key name,String strType,short type) {
		this.name=name;
		this.strType=strType;
		this.type=type;
	}

	/**
	 * @return the defaultType
	 */
	public int getDefaultType() {
		return DEFAULT_TYPE_NULL;
	}


	/**
     * @see railo.runtime.type.FunctionArgument#getName()
     */
	public Collection.Key getName() {
		return name;
	}

	/**
     * @see railo.runtime.type.FunctionArgument#isRequired()
     */
	public boolean isRequired() {
		return false;
	}

	/**
     * @see railo.runtime.type.FunctionArgument#getType()
     */
	public short getType() {
		return type;
	}

	/**
     * @see railo.runtime.type.FunctionArgument#getTypeAsString()
     */
	public String getTypeAsString() {
		return strType;
	}

	/**
     * @see railo.runtime.type.FunctionArgument#getHint()
     */
	public String getHint() {
		return "";
	}


	/**
	 *
	 * @see railo.runtime.type.FunctionArgument#getDisplayName()
	 */
	public String getDisplayName() {
		return "";
	}


	/**
     * @see railo.runtime.type.FunctionArgument#getDspName()
     * @deprecated replaced with <code>getDisplayName();</code>
     */
	public String getDspName() {
		return getDisplayName();
	}
	
	/**
	 * @see railo.runtime.type.FunctionArgument#getMetaData()
	 */
	public Struct getMetaData() {
		return null;
	}
	
	public boolean isPassByReference() {
		return true;
	}


	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		name=KeyImpl.init(ExternalizableUtil.readString(in));
		type=in.readShort();
		strType=ExternalizableUtil.readString(in);
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.writeString(out, name.getString());
		out.writeShort(type);
		ExternalizableUtil.writeString(out, strType);
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof FunctionArgument)) return false;
		return FunctionArgumentImpl.equals(this,(FunctionArgument)obj);
	}
}