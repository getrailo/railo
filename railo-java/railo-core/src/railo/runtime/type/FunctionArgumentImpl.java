package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExternalizableUtil;

/**
 * a single argument of a function
 */
public final class FunctionArgumentImpl implements FunctionArgument,Externalizable {
	
	private String dspName;
	private String hint;
	private Collection.Key name;
	private short type;
	private String strType;
	private boolean required;
	private Struct meta;
	private int defaultType;
	private boolean passByReference;
	

	/**
	 * constructor of the class
	 * @param name name of the function argument
	 * @param type type f the argument
	 * @param required if argument is required or not
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required) {
		this(name,type,required,"","");
	}

	
	/**
	 * constructor of the class
	 * @param name name of the function argument
	 * @param type type f the argument
	 * @param required if argument is required or not
	 * @param dspName Display Name 
	 * @param hint Hint
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,String dspName,String hint) {
		this(name,type,required,DEFAULT_TYPE_RUNTIME_EXPRESSION,true,dspName,hint,null);
	}

	
	/**
	 * constructor of the class
	 * @param name name of the function argument
	 * @param type type f the argument
	 * @param required if argument is required or not
	 * @param dspName Display Name 
	 * @param hint Hint
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,String dspName,String hint,StructImpl meta) {
		this(name,type,required,DEFAULT_TYPE_RUNTIME_EXPRESSION,true,dspName,hint,meta);
	}
	

	/**
	 * Constructor of the class
	 * @param name
	 * @param type
	 * @param required
	 * @param defaultType
	 * @param dspName
	 * @param hint
	 * @param meta
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,int defaultType,String dspName,String hint,StructImpl meta) {
		this(name, type, required, defaultType,true, dspName, hint, meta);
	}
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param type
	 * @param required
	 * @param defaultType
	 * @param dspName
	 * @param hint
	 * @param meta
	 * 
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,double defaultType,String dspName,String hint,StructImpl meta) {
		this(name, type, required, (int)defaultType,true, dspName, hint, meta);
	}
	/**
	 * Constructor of the class
	 * @param name
	 * @param type
	 * @param required
	 * @param defaultType
	 * @param passByReference
	 * @param dspName
	 * @param hint
	 * @param meta
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,double defaultType,boolean passByReference,String dspName,String hint,StructImpl meta) {
		this(name, type, required, (int)defaultType,passByReference, dspName, hint, meta);
	}
	
	/**
	 * @param name name of the function argument
	 * @param type type f the argument
	 * @param required if argument is required or not
	 * @param hasDefault
	 * @param dspName Display Name 
	 * @param hint Hint
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(String name,String type,boolean required,int defaultType,boolean passByReference,String dspName,String hint,StructImpl meta) {
		this(KeyImpl.init(name),type,required,defaultType,passByReference,dspName,hint,meta);
	}
	

	/**
	 * NEVER USE THIS CONSTRUCTOR, this constructor is only for deserialize this object from stream
	 */
	public FunctionArgumentImpl() {

	}

	public FunctionArgumentImpl(String name,String strType,short type,boolean required,int defaultType,boolean passByReference,String dspName,String hint,StructImpl meta) {
		this(KeyImpl.init(name), strType, type, required, defaultType, passByReference, dspName, hint, meta);
	}
	
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param type
	 * @param required
	 * @param defaultType
	 * @param passByReference
	 * @param dspName
	 * @param hint
	 * @param meta
	 * @deprecated use other constructor
	 */
	public FunctionArgumentImpl(Collection.Key name,String type,boolean required,int defaultType,boolean passByReference,String dspName,String hint,StructImpl meta) {
		this.name=name;
		this.strType=(type);
		this.type=CFTypes.toShortStrict(type,CFTypes.TYPE_UNKNOW);
		this.required=required;
		this.defaultType=defaultType;
		this.dspName=dspName;
		this.hint=hint;
		this.meta=meta;
		this.passByReference=passByReference;
		//print.out(name);
		
	}
	

	public FunctionArgumentImpl(Collection.Key name,String strType,short type,boolean required,int defaultType,boolean passByReference,String dspName,String hint,StructImpl meta) {
		this.name=name;
		this.strType=strType;
		this.type=type;
		this.required=required;
		this.defaultType=defaultType;
		this.dspName=dspName;
		this.hint=hint;
		this.meta=meta;
		this.passByReference=passByReference;
	}
	
	
	
	
	
	
	
	//private static StructImpl sct=new StructImpl();


	/**
	 * @return the defaultType
	 */
	public int getDefaultType() {
		return defaultType;
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
		return required;
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
		return hint;
	}


	/**
	 *
	 * @see railo.runtime.type.FunctionArgument#getDisplayName()
	 */
	public String getDisplayName() {
		return dspName;
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
		return meta;
	}
	
	public boolean isPassByReference() {
		return passByReference;
	}


	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		dspName=ExternalizableUtil.readString(in);
		hint=ExternalizableUtil.readString(in);
		name=KeyImpl.init(ExternalizableUtil.readString(in));
		type=in.readShort();
		strType=ExternalizableUtil.readString(in);
		required=in.readBoolean();
		meta=(Struct) in.readObject();
		defaultType=in.readInt();
		passByReference=in.readBoolean();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		ExternalizableUtil.writeString(out, dspName);
		ExternalizableUtil.writeString(out, hint);
		ExternalizableUtil.writeString(out, name.getString());
		out.writeShort(type);
		ExternalizableUtil.writeString(out, strType);
		out.writeBoolean(required);
		out.writeObject(meta);
		out.writeInt(defaultType);
		out.writeBoolean(passByReference);
	}
}