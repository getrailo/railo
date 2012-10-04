package railo.transformer.library.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.TemplateException;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.tag.TagLib;



/**
 * Eine FunctionLibFunction repr�sentiert eine einzelne Funktion innerhalb einer FLD.
 */
public final class FunctionLibFunction {
	
	/**
	 * Dynamischer Argument Typ
	 */
	public static final int ARG_DYNAMIC = 0;
	/**
	 * statischer Argument Typ
	 */
	public static final int ARG_FIX = 1;

	 
	private FunctionLib functionLib;
	private String name;
	private ArrayList<FunctionLibFunctionArg> argument=new ArrayList<FunctionLibFunctionArg>();
	
	private int argMin=0;
	private int argMax=-1;
	private int argType=ARG_FIX;
	

	private String strReturnType;

	private String cls="";
	private Class clazz;
	private String description;
	private boolean hasDefaultValues;
	private FunctionEvaluator eval;
	private String tteClass;	
	private short status=TagLib.STATUS_IMPLEMENTED;
	private String memberName;
	private short memberType=CFTypes.TYPE_UNKNOW;
	private boolean memberChaining;

	
	/**
	 * Gesch�tzer Konstruktor ohne Argumente.
	 */
	public FunctionLibFunction() {
	}
	public FunctionLibFunction(FunctionLib functionLib) {
			this.functionLib=functionLib;
	}
	
	/**
	 * Gibt den Namen der Funktion zur�ck.
	 * @return name Name der Funktion.
	 */
	public String getName() {
		return name;
	}
	
	/**
	* Gibt alle Argumente einer Funktion als ArrayList zur�ck.
	* @return Argumente der Funktion.
	*/
   public ArrayList<FunctionLibFunctionArg> getArg() {
	   return argument;
   }

	/**
	 * Gibt zur�ck wieviele Argumente eine Funktion minimal haben muss.
	 * @return Minimale Anzahl Argumente der Funktion.
	 */
	public int getArgMin() {
		return argMin;
	}
	
	/**
	 * Gibt zur�ck wieviele Argumente eine Funktion minimal haben muss.
	 * @return Maximale Anzahl Argumente der Funktion.
	 */
	public int getArgMax() {
		return argMax;
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
	 * Gibt die argument art zur�ck.
	 * @return argument art
	 */
	public int getArgType() {
		return argType;
	}
	
	/**
	 * Gibt die argument art als String zur�ck.
	 * @return argument art
	 */
	public String getArgTypeAsString() {
		if(argType==ARG_DYNAMIC) return "dynamic";
		return "fixed";
	}

	/**
	 * Gibt zur�ck von welchem Typ der R�ckgabewert dieser Funktion sein muss (query, string, struct, number usw.).
	 * @return Typ des R�ckgabewert.
	 */
	public String getReturnTypeAsString() {
		return strReturnType;
	}

	/**
	 * Gibt die Klassendefinition als Zeichenkette zur�ck, welche diese Funktion implementiert.
	 * @return Klassendefinition als Zeichenkette.
	 */
	public String getCls() {
		return cls;
	}

	/**
	 * Gibt die Klasse zur�ck, welche diese Funktion implementiert.
	 * @return Klasse der Function.
	 */
	public Class getCazz() {
		if(clazz==null) {
			clazz=ClassUtil.loadClass(cls,(Class)null);
			/*try {
				clazz=Class.orName(cls);
			} catch (ClassNotFoundException e) {
				
			}*/
		}
		return clazz;
	}

	/**
	 * Gibt die Beschreibung der Funktion zur�ck.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gibt die FunctionLib zur�ck, zu der die Funktion geh�rt.
	 * @return Zugeh�rige FunctionLib.
	 */
	public FunctionLib getFunctionLib() {
		return functionLib;
	}

	/**
	 * Setzt den Namen der Funktion.
	 * @param name Name der Funktion.
	 */
	public void setName(String name) {
		this.name = name.toLowerCase();
	}	

	/**
	 * F�gt der Funktion ein Argument hinzu.
	 * @param arg Argument zur Funktion.
	 */
	public void addArg(FunctionLibFunctionArg arg) {
		arg.setFunction(this);
		argument.add(arg); 
		if(arg.getDefaultValue()!=null)
			hasDefaultValues=true;
	}

	/**
	 * F�gt der Funktion ein Argument hinzu, alias f�r addArg.
	 * @param arg Argument zur Funktion.
	 */
	public void setArg(FunctionLibFunctionArg arg) {
		addArg(arg);
	}


	/**
	 * Setzt wieviele Argumente eine Funktion minimal haben muss.
	 * @param argMin Minimale Anzahl Argumente der Funktion.
	 */
	public void setArgMin(int argMin) {
		this.argMin = argMin;
	}
	
	/**
	 * Setzt wieviele Argumente eine Funktion minimal haben muss.
	 * @param argMax Maximale Anzahl Argumente der Funktion.
	 */
	public void setArgMax(int argMax) {
		this.argMax = argMax;
	}

	/**
	 * Setzt den R�ckgabewert der Funktion (query,array,string usw.)
	 * @param value
	 */
	public void setReturn(String value) {
		strReturnType=value;
	}

	/**
	 * Setzt die Klassendefinition als Zeichenkette, welche diese Funktion implementiert.
	 * @param value Klassendefinition als Zeichenkette.
	 */
	public void setCls(String value) {
		cls+=value;
		
	}

	/**
	 * Setzt die Beschreibung der Funktion.
	 * @param description Beschreibung der Funktion.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Setzt die zugeh�rige FunctionLib.
	 * @param functionLib Zugeh�rige FunctionLib.
	 */
	public void setFunctionLib(FunctionLib functionLib) {
		this.functionLib=functionLib;
	}

	/**
	 * sets the argument type of the function
	 * @param argType
	 */
	public void setArgType(int argType) {
		this.argType=argType;
	}
	

	public String getHash() {
		StringBuffer sb=new StringBuffer();
		sb.append(this.getArgMax());
		sb.append(this.getArgMin());
		sb.append(this.getArgType());
		sb.append(this.getArgTypeAsString());
		sb.append(this.getCls());
		sb.append(this.getName());
		sb.append(this.getReturnTypeAsString());
		
		Iterator it = this.getArg().iterator();
		FunctionLibFunctionArg arg;
		while(it.hasNext()){
			arg=(FunctionLibFunctionArg) it.next();
			sb.append(arg.getHash());
		}
		
		try {
			return Md5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
	}
	public boolean hasDefaultValues() {
		return hasDefaultValues;
	}

	public boolean hasTteClass() {
		return tteClass !=null && tteClass.length()>0;
	}
	
	public FunctionEvaluator getEvaluator() throws TemplateException {
		if(!hasTteClass()) return null;
		if(eval!=null) return eval;
		try {
			eval = (FunctionEvaluator) ClassUtil.loadInstance(tteClass);
		} 
		catch (ClassException e) {
			throw new TemplateException(e.getMessage());
		} 
		return eval;
	}
	public void setTteClass(String tteClass) {
		this.tteClass=tteClass;
	}
	public void setMemberName(String memberName) {
		if(StringUtil.isEmpty(memberName,true)) return;
		this.memberName=memberName.trim();	
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberChaining(boolean memberChaining) {
		this.memberChaining=memberChaining;	
	}
	public boolean getMemberChaining() {
		return memberChaining;
	}
	
	public short getMemberType() {
		if(memberName!=null && memberType==CFTypes.TYPE_UNKNOW){
			ArrayList<FunctionLibFunctionArg> args = getArg();
			if(args.size()>=1){
				memberType=CFTypes.toShortStrict(args.get(0).getTypeAsString(),CFTypes.TYPE_UNKNOW);
			}
		}
		return memberType;
	}
	public String getMemberTypeAsString() {
		return CFTypes.toString(getMemberType(),"any");
	}
}