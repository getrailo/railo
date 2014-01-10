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
	
	private static final long serialVersionUID = 817360221819952381L; // do not change
	
	
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


	@Override
	public Collection.Key getName() {
		return name;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public short getType() {
		return type;
	}

	@Override
	public String getTypeAsString() {
		return strType;
	}

	@Override
	public String getHint() {
		return "";
	}


	@Override
	public String getDisplayName() {
		return "";
	}
	
	@Override
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