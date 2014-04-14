package railo.runtime.type;

import java.io.Externalizable;
import java.io.Serializable;

public interface UDFProperties extends Serializable,Externalizable {

/*
	public  String functionName;
	public  int returnType;
	public  String strReturnType;
	public  boolean output;
	public String hint;
	public String displayName;
	//public Page page;
	public PageSource pageSource;
	public int index;
	public FunctionArgument[] arguments;
	public Struct meta;
	public String description;
	public Boolean secureJson;
	public Boolean verifyClient;
	public boolean async;
	public String strReturnFormat;
	public int returnFormat;
	public Set<Collection.Key> argumentsSet;
	public int access; 
*/

	
	
	public int getAccess();






}