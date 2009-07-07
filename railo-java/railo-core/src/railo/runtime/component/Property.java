package railo.runtime.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.asm.Type;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.bytecode.util.ASMProperty;
import railo.transformer.bytecode.util.ASMUtil;

/**
 * 
 */
public final class Property extends MemberSupport implements ASMProperty {
    
	
	private String type="any";
	private String name;
	private boolean required;
	private String _default;
	private String displayname="";
	private String hint="";
	private Map meta=new HashMap(); 
	
	public Property() {
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

	/**
	 *
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		return _default;
	}

	/**
	 *
	 * @throws PageException 
	 * @see railo.transformer.bytecode.util.ASMProperty#getASMType()
	 */
	public Type getASMType() throws PageException {
		return ASMUtil.toType(getType(), true);
	}
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "default:"+this._default+";displayname:"+this.displayname+";hint:"+this.hint+
		";name:"+this.name+";type:"+this.type+";";
	}

	public Object getMetaData() {
		Struct sct=new StructImpl();
		sct.setEL("name",name);
		if(!StringUtil.isEmpty(hint))sct.setEL("hint",hint);
		if(!StringUtil.isEmpty(displayname))sct.setEL("displayname",displayname);
		if(!StringUtil.isEmpty(type))sct.setEL("type",type);
		Iterator it = meta.keySet().iterator();
		Object key;
		while(it.hasNext()) {
			key=it.next();
			sct.setEL(key.toString(),meta.get(key));
		}
		return sct;
	}
	
	public Map getMeta() {
		return meta;
	}

	
}