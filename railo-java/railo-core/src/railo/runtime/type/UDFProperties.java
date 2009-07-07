package railo.runtime.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.commons.lang.SizeOf;
import railo.runtime.Page;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.util.ComponentUtil;

public class UDFProperties implements Sizeable,Serializable {


	public final String functionName;
	public final int returnType;
	public final String strReturnType;
	public final boolean output;
	public final String hint;
	public final String displayName;
	public final Page page;
	public final int index;
	public final FunctionArgument[] arguments;
	public final StructImpl meta;
	public final String description;
	public final Boolean secureJson;
	public final Boolean verifyClient;
	public final boolean async;
	public final String strReturnFormat;
	public final int returnFormat;
	public final Set argumentsSet;
	public final int access; 


		public UDFProperties(
		        Page page,
		        FunctionArgument[] arguments,
				int index,
		        String functionName, 
		        String strReturnType, 
		        String strReturnFormat, 
		        boolean output, 
		        boolean async, 
		        String strAccess, 
		        String displayName, 
		        String description, 
		        String hint, 
		        Boolean secureJson,
		        Boolean verifyClient,
		        StructImpl meta) throws ExpressionException {
			this(page,
			    arguments,
				index,
			    functionName, 
			    strReturnType, 
			    strReturnFormat, 
			    output, 
			    async, 
			    ComponentUtil.toIntAccess(strAccess), 
			    displayName, 
			    description, 
			    hint, 
			    secureJson,
			    verifyClient,
			    meta);
			
		}

	 
	public UDFProperties(
	        Page page,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        String strReturnType, 
	        String strReturnFormat, 
	        boolean output, 
	        boolean async, 
	        int access, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        StructImpl meta) throws ExpressionException {
		
		if(arguments.length>0){
			this.argumentsSet=new HashSet();
			for(int i=0;i<arguments.length;i++){
				argumentsSet.add(arguments[i].getName());
			}
		}
		else this.argumentsSet=null;
		this.arguments = arguments;
		this.async = async;
		this.description = description;
		this.displayName = displayName;
		this.functionName = functionName;
		this.hint = hint;
		this.index = index;
		this.meta = meta;
		this.output = output;
		this.page = page;
		
		this.strReturnType=strReturnType;
		this.returnType=CFTypes.toShort(strReturnType);
		this.strReturnFormat=strReturnFormat;
		this.returnFormat=UDFImpl.toReturnFormat(strReturnFormat);
		
		this.secureJson = secureJson;
		this.verifyClient = verifyClient;
		this.access = access;
		
	}
	
	public UDFProperties(
	        Page page,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        boolean async, 
	        String strAccess, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        StructImpl meta) throws ExpressionException {
		this(page,
		    arguments,
			index,
		    functionName, 
		    returnType, 
		    strReturnFormat, 
		    output, 
		    async, 
		    ComponentUtil.toIntAccess(strAccess), 
		    displayName, 
		    description, 
		    hint, 
		    secureJson,
		    verifyClient,
		    meta);
	}
		
	
	
	public UDFProperties(
	        Page page,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        boolean async, 
	        int access, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        StructImpl meta) throws ExpressionException {
		
		
		if(arguments.length>0){
			this.argumentsSet=new HashSet();
			for(int i=0;i<arguments.length;i++){
				argumentsSet.add(arguments[i].getName());
			}
		}
		else this.argumentsSet=null;
		
		this.arguments = arguments;
		this.async = async;
		this.description = description;
		this.displayName = displayName;
		this.functionName = functionName;
		this.hint = hint;
		this.index = index;
		this.meta = meta;
		this.output = output;
		this.page = page;
		
		this.strReturnType=CFTypes.toString(returnType);
		this.returnType=returnType;
		this.strReturnFormat=strReturnFormat;
		this.returnFormat=UDFImpl.toReturnFormat(strReturnFormat);
		
		this.secureJson = secureJson;
		this.verifyClient = verifyClient;
		this.access = access;
		
	}
	
	
	

	/**
	 * @see railo.runtime.engine.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return 
		SizeOf.size(functionName)+
		SizeOf.size(returnType)+
		SizeOf.size(strReturnType)+
		SizeOf.size(output)+
		SizeOf.size(hint)+
		SizeOf.size(index)+
		SizeOf.size(displayName)+
		SizeOf.size(arguments)+
		SizeOf.size(meta)+
		SizeOf.size(description)+
		SizeOf.size(secureJson)+
		SizeOf.size(verifyClient)+
		SizeOf.size(async)+
		SizeOf.size(strReturnFormat)+
		SizeOf.size(returnFormat);
	}
	

	 /**
	 * @return the access
	 */
	public int getAccess() {
		return access;
	}


}