package railo.runtime.component;

import org.objectweb.asm.Type;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructUtil;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMUtil;

/**
 */
public final class PropertyImpl extends MemberSupport implements Property,ASMProperty {
    

	private static final long serialVersionUID = 3206074213415946902L;

	
	private String type="any";
	private String name;
	private boolean required;
	private boolean setter=true;
	private boolean getter=true;
	

	private String _default;
	private String displayname="";
	private String hint="";
	private Struct dynAttrs=new StructImpl();
	private Struct metadata;

	private String ownerName;
	
	public PropertyImpl() {
		super(Component.ACCESS_REMOTE);
	}

	/**
	 * @return the _default
	 */
	public String getDefault() {
		return _default;
	}

	/**
	 * @param _default the _default to set
	 */
	public void setDefault(String _default) {
		this._default = _default;
	}

	/**
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * @param displayname the displayname to set
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	/**
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * @param hint the hint to set
	 */
	public void setHint(String hint) {
		this.hint = hint;
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
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Object getValue() {
		return _default;
	}

	@Override
	public Type getASMType() throws PageException {
		return ASMUtil.toType(getType(), true);
	}
	
	/**
	 * @return the setter
	 */
	public boolean getSetter() {
		return setter;
	}

	/**
	 * @param setter the setter to set
	 */
	public void setSetter(boolean setter) {
		this.setter = setter;
	}

	/**
	 * @return the getter
	 */
	public boolean getGetter() {
		return getter;
	}

	/**
	 * @param getter the getter to set
	 */
	public void setGetter(boolean getter) {
		this.getter = getter;
	}
	
	

	public Object getMetaData() {
		Struct sct=new StructImpl();
		
		// meta 
		if(metadata!=null)
			StructUtil.copy(metadata, sct, true);
		
		sct.setEL(KeyConstants._name,name);
		if(!StringUtil.isEmpty(hint,true))sct.setEL(KeyConstants._hint,hint);
		if(!StringUtil.isEmpty(displayname,true))sct.setEL(KeyConstants._displayname,displayname);
		if(!StringUtil.isEmpty(type,true))sct.setEL(KeyConstants._type,type);
		
		// dyn attributes

		StructUtil.copy(dynAttrs, sct, true);
		
		return sct;
	}

	public Struct getDynamicAttributes() {
		return dynAttrs;
	}
	public Struct getMeta() {
		if(metadata==null) metadata=new StructImpl();
		return metadata;
	}

	@Override
	public Class getClazz() {
		return null;
	}

	public boolean isPeristent() {
		return Caster.toBooleanValue(dynAttrs.get(KeyConstants._persistent,Boolean.TRUE),true);
	}

	public void setOwnerName(String ownerName) {
		this.ownerName=ownerName;
	}
	public String getOwnerName() {
		return ownerName;
	}

	
	
	@Override
	public String toString() {
		String strDynAttrs="";
		try{
		strDynAttrs=new ScriptConverter().serialize(dynAttrs);
		}
		catch(ConverterException ce){}
		
		return "default:"+this._default+";displayname:"+this.displayname+";hint:"+this.hint+
		";name:"+this.name+";type:"+this.type+";ownerName:"+ownerName+";attrs:"+strDynAttrs+";";
	}
	
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Property)) return false;
		Property other=(Property)obj;
		
		return toString().equals(other.toString());
	}

	public Object duplicate(boolean deepCopy) {
		PropertyImpl other = new PropertyImpl();
		other._default=_default;
		other.displayname=displayname;
		other.getter=getter;
		other.hint=hint;
		other.dynAttrs=deepCopy?(Struct) Duplicator.duplicate(dynAttrs,deepCopy):dynAttrs;
		other.name=name;
		other.ownerName=ownerName;
		other.required=required;
		other.setter=setter;
		other.type=type;
		
		return other;
	}
}