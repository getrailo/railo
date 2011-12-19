package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExternalizableUtil;
import railo.commons.lang.SizeOf;
import railo.runtime.Page;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.engine.ThreadLocalPageSource;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.util.ComponentUtil;

public final class UDFProperties implements Sizeable,Serializable,Externalizable {


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
			this(page.getPageSource(),
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
		
		this(
		        page.getPageSource(),
		        arguments,
				index,
		        functionName, 
		        strReturnType, 
		        strReturnFormat, 
		        output, 
		        async, 
		        access, 
		        displayName, 
		        description, 
		        hint, 
		        secureJson,
		        verifyClient,
		        meta);
		
	}
	
	public UDFProperties(
	        PageSource pageSource,
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
		
		// this happens when a arcive is based on older source code
		if(pageSource==null){
			pageSource = ThreadLocalPageSource.get();
		}
		
		
		if(arguments.length>0){
			this.argumentsSet=new HashSet<Collection.Key>();
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
		//this.page = PageProxy.toProxy(page);
		this.pageSource=pageSource;
		
		
		this.strReturnType=strReturnType;
		this.returnType=CFTypes.toShortStrict(strReturnType,CFTypes.TYPE_UNKNOW);
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
		this(page.getPageSource(),
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
		
	/**
	 * NEVER USE THIS CONSTRUCTOR, this constructor is only for deserialize this object from stream
	 */
	public UDFProperties(){
		
	}
	
	//@deprecated use instead UDFProperties(PageSource pageSource,...
	public UDFProperties(Page page, FunctionArgument[] arguments, int index, String functionName, short returnType,
			String strReturnFormat, boolean output, boolean async, int access, String displayName, String description,String hint, 
			Boolean secureJson,Boolean verifyClient,StructImpl meta) throws ExpressionException {
		this(page.getPageSource(),arguments,index,functionName, returnType, strReturnFormat, output, async,access, displayName, 
				description, hint, secureJson,verifyClient,meta);
	}
	
	public UDFProperties(
	        PageSource pageSource,
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
		
		// this happens when a arcive is based on older source code
		if(pageSource==null){
			pageSource = ThreadLocalPageSource.get();
		}
		
		
		if(arguments.length>0){
			this.argumentsSet=new HashSet<Collection.Key>();
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
		this.pageSource = pageSource;
		
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

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
			ConfigWebImpl cw = (ConfigWebImpl) ThreadLocalPageContext.getConfig(pc);
			pageSource=cw.getPageSource(pc,null, ExternalizableUtil.readString(in), false,pc.useSpecialMappings(),true);
			
		} 
		catch (Throwable e) {
			e.printStackTrace();
			IOException ioe = new IOException(e.getMessage());
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}
		
		arguments=(FunctionArgument[]) in.readObject();
		access = in.readInt();
		index = in.readInt();
		returnFormat = in.readInt();
		returnType = in.readInt();
		async = in.readBoolean();
		description = ExternalizableUtil.readString(in);
		displayName = ExternalizableUtil.readString(in);
		functionName = ExternalizableUtil.readString(in);
		hint = ExternalizableUtil.readString(in);
		meta = (Struct) in.readObject();
		output = in.readBoolean();
		secureJson = ExternalizableUtil.readBoolean(in);
		strReturnFormat = ExternalizableUtil.readString(in);
		strReturnType = ExternalizableUtil.readString(in);
		verifyClient = ExternalizableUtil.readBoolean(in);
		
		if(arguments!=null && arguments.length>0){
			this.argumentsSet=new HashSet<Collection.Key>();
			for(int i=0;i<arguments.length;i++){
				argumentsSet.add(arguments[i].getName());
			}
		}
		
	}


	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(pageSource.getFullRealpath());
		out.writeObject(arguments);
		out.writeInt(access);
		out.writeInt(index);
		out.writeInt(returnFormat);
		out.writeInt(returnType);
		out.writeBoolean(async);
		ExternalizableUtil.writeString(out,description);
		ExternalizableUtil.writeString(out,displayName);
		ExternalizableUtil.writeString(out,functionName);
		ExternalizableUtil.writeString(out,hint);
		out.writeObject(meta);
		out.writeBoolean(output);
		ExternalizableUtil.writeBoolean(out,secureJson);
		ExternalizableUtil.writeString(out,strReturnFormat);
		ExternalizableUtil.writeString(out,strReturnType);
		ExternalizableUtil.writeBoolean(out,verifyClient);
		
		
	}




}