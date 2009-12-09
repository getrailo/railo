package railo.runtime;

import java.io.Serializable;
import java.util.Map;

import railo.runtime.exp.ExpressionException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;


public class ComponentProperties implements Serializable {
	
	private static final Collection.Key WSDL_FILE = KeyImpl.getInstance("wsdlfile");
	final String dspName;
	final String extend;
	final String hint;
	Boolean output;
	final String callPath;
	final boolean realPath;
	final boolean _synchronized;
	Class javaAccessClass;
	Map properties;
	Struct meta;
	String implement;

	public ComponentProperties(String dspName,String extend,String implement,String hint, Boolean output, String callPath, boolean realPath,boolean _synchronized,Class javaAccessClass,Struct meta) {
		this.dspName=dspName;
		this.extend=extend;
		this.implement=implement;
		this.hint=hint;
		this.output=output;
		this.callPath=callPath;
		this.realPath=realPath;
		this._synchronized=_synchronized;
		this.javaAccessClass=javaAccessClass;
		this.meta=meta;
	}

	public ComponentProperties duplicate() {
		ComponentProperties cp= new ComponentProperties(dspName,extend,implement,hint,output,callPath,realPath,_synchronized,javaAccessClass,meta);
		cp.properties=properties;
		return cp;
	}

    
	/**
	 * returns null if there is no wsdlFile defined
	 * @return the wsdlFile
	 * @throws ExpressionException 
	 */
	public String getWsdlFile() {
		if(meta==null) return null;
		return (String) meta.get(WSDL_FILE,null);
	}


}
