package railo.transformer.library.tag;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import railo.commons.lang.ClassUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;


/**
 * Die Klasse TagLibTagAttr repraesentiert ein einzelnes Attribute eines Tag 
 * und haelt saemtliche Informationen zu diesem Attribut.
 */
public final class TagLibTagAttr {

	public static final short SCRIPT_SUPPORT_NONE = 0;
	public static final short SCRIPT_SUPPORT_OPTIONAL = 1;
	public static final short SCRIPT_SUPPORT_REQUIRED = 2;
	
	private String name="noname";
	private String type;
	private String description="";
	private boolean required;
	private boolean rtexpr=true;
	private Object defaultValue;
    private TagLibTag tag;
	private boolean hidden;
	private boolean _default;
	private short status=TagLib.STATUS_IMPLEMENTED;
	private short scriptSupport=SCRIPT_SUPPORT_NONE;

	
	public TagLibTagAttr duplicate(TagLibTag tag) {
		TagLibTagAttr tlta=new TagLibTagAttr(tag);
		tlta.name=name;
		tlta.type=type;
		tlta.description=description;
		tlta.required=required;
		tlta.rtexpr=rtexpr;
		tlta.defaultValue=defaultValue;
		tlta.hidden=hidden;
		tlta._default=_default;
		tlta.status=status;
		
		
		return tlta;
	}
	
	
	/**
	 * @return the status (TagLib.,TagLib.STATUS_IMPLEMENTED,TagLib.STATUS_DEPRECATED,TagLib.STATUS_UNIMPLEMENTED)
	 */
	public short getStatus() {
		return status;
	}


	/**
	 * @param status the status to set (TagLib.,TagLib.STATUS_IMPLEMENTED,TagLib.STATUS_DEPRECATED,TagLib.STATUS_UNIMPLEMENTED)
	 */
	public void setStatus(short status) {
		this.status = status;
	}

	/**
	 * Geschuetzer Konstruktor ohne Argumente.
	 */
	public TagLibTagAttr(TagLibTag tag) {
	    this.tag=tag;
	}

	/**
	 * Gibt den Namen des Attribut zurueck.
	 * @return Name des Attribut.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gibt zurueck, ob das Attribut Pflicht ist oder nicht.
	 * @return Ist das Attribut Pflicht.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Gibt den Typ des Attribut zurŸck (query, struct, string usw.)
	 * @return Typ des Attribut
	 */
	public String getType() {
	    if(this.type==null) {
	    	try {
	        String methodName=  "set"+
			(name.length()>0?""+Character.toUpperCase(name.charAt(0)):"")+
			(name.length()>1?name.substring(1):"");
	        
	        Class clazz= ClassUtil.loadClass(tag.getTagClassName(),(Class)null);//Class.orName(tag.getTagClassName());
            if(clazz!=null) {
            	Method[] methods = clazz.getMethods();
                for(int i=0;i<methods.length;i++) {
                    Method method = methods[i];
                    if(method.getName().equalsIgnoreCase(methodName)) {
                        Class[] types = method.getParameterTypes();
                        if(types.length==1) {
                        	Class type=types[0];
                            if(type==String.class)this.type="string";
                            else if(type==double.class)this.type="number";
                            else if(type==Date.class)this.type="datetime";
                            else this.type=type.getName();
                        }
                    }
                }
            }
	    	}
	    	catch(Throwable t) {
	    		
	    		return "string";
	    	}
	    }
		return this.type;
	}

	/**
	 * Gibt zurueck ob das Attribute eines Tag, mithilfe des ExprTransformer, uebersetzt werden soll oder nicht.
	 * @return Soll das Attribut uebbersetzt werden
	 */
	public boolean getRtexpr() {
		return rtexpr;
	}

	/**
	 * Setzt den Namen des Attribut.
	 * @param name Name des Attribut.
	 */
	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	/**
	 * Setzt, ob das Argument Pflicht ist oder nicht.
	 * @param required Ist das Attribut Pflicht.
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Setzt, ob das Attribute eines Tag, mithilfe des ExprTransformer, uebersetzt werden soll oder nicht.
	 * @param rtexpr Soll das Attribut uebbersetzt werden
	 */
	public void setRtexpr(boolean rtexpr) {
		this.rtexpr = rtexpr;
	}

	/**
	 * Setzt, den Typ des Attribut (query, struct, string usw.)
	 * @param type Typ des Attribut.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * @param defaultValue
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue=defaultValue;
        tag.setHasDefaultValue(true);
    }

    /**
     * @return Returns the defaultValue.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return
     */
    public boolean hasDefaultValue() {
        return defaultValue!=null;
    }

	public void setHidden(boolean hidden) {
		this.hidden=hidden;
	}
	public boolean getHidden() {
		return hidden;
	}

	public String getHash() {
		StringBuffer sb=new StringBuffer();
		sb.append(this.getDefaultValue());
		sb.append(this.getName());
		sb.append(this.getRtexpr());
		sb.append(this.getType());
		
		try {
			return Md5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
	}

	public void isDefault(boolean _default) {
		if(_default)
			tag.setDefaultAttribute(this);
		this._default=_default;
	}

	public boolean isDefault() {
		return _default;
	}


	public void setScriptSupport(String str) {
		if(!StringUtil.isEmpty(str))  {
			str=str.trim().toLowerCase();
			if("optional".equals(str)) this.scriptSupport=SCRIPT_SUPPORT_OPTIONAL;
			else if("opt".equals(str)) this.scriptSupport=SCRIPT_SUPPORT_OPTIONAL;
			else if("required".equals(str)) this.scriptSupport=SCRIPT_SUPPORT_REQUIRED;
			else if("req".equals(str)) this.scriptSupport=SCRIPT_SUPPORT_REQUIRED;
		}
	}


	/**
	 * @return the scriptSupport
	 */
	public short getScriptSupport() {
		return scriptSupport;
	}


	public Object getScriptSupportAsString() {
		if(scriptSupport==SCRIPT_SUPPORT_OPTIONAL) return "optional";
		if(scriptSupport==SCRIPT_SUPPORT_REQUIRED) return "required";
		return "none";
	}

}