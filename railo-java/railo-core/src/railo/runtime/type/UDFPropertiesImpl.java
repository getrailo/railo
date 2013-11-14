package railo.runtime.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.ExternalizableUtil;
import railo.commons.lang.SizeOf;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.engine.ThreadLocalPageSource;
import railo.runtime.type.util.UDFUtil;

public final class UDFPropertiesImpl implements UDFProperties {
	private static final long serialVersionUID = 8679484452640746605L; // do not change

	
	public  String functionName;
	public  int returnType;
	public  String strReturnType;
	public  boolean output;
	public  Boolean bufferOutput;
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
	public String strReturnFormat;
	public int returnFormat;
	public Set<Collection.Key> argumentsSet;
	public int access;
	public long cachedWithin; 
	public Integer localMode;

	/**
	 * NEVER USE THIS CONSTRUCTOR, this constructor is only for deserialize this object from stream
	 */
	public UDFPropertiesImpl(){
		
	}
	
	
	public UDFPropertiesImpl(
	        PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        String strReturnType, 
	        String strReturnFormat, 
	        boolean output,
	        int access, 
	        Boolean bufferOutput,
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        long cachedWithin,
	        Integer localMode,
	        StructImpl meta) {
		
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
		this.description = description;
		this.displayName = displayName;
		this.functionName = functionName;
		this.hint = hint;
		this.index = index;
		this.meta = meta;
		this.output = output;
		this.bufferOutput = bufferOutput;
		//this.page = PageProxy.toProxy(page);
		this.pageSource=pageSource;
		
		
		this.strReturnType=strReturnType;
		this.returnType=CFTypes.toShortStrict(strReturnType,CFTypes.TYPE_UNKNOW);
		this.strReturnFormat=strReturnFormat;
		this.returnFormat=UDFUtil.toReturnFormat(strReturnFormat,-1);
		
		this.secureJson = secureJson;
		this.verifyClient = verifyClient;
		this.access = access;
		this.cachedWithin=cachedWithin;
		this.localMode=localMode;
	}
	
	public UDFPropertiesImpl(
	        PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        int access, 
	        Boolean bufferOutput,
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        long cachedWithin,
	        Integer localMode,
	        StructImpl meta) {
		
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
		this.description = description;
		this.displayName = displayName;
		this.functionName = functionName;
		this.hint = hint;
		this.index = index;
		this.meta = meta;
		this.output = output;
		this.bufferOutput = bufferOutput;
		this.pageSource = pageSource;
		
		this.strReturnType=CFTypes.toString(returnType,"any");
		this.returnType=returnType;
		this.strReturnFormat=strReturnFormat;
		this.returnFormat=UDFUtil.toReturnFormat(strReturnFormat,-1);
		
		this.secureJson = secureJson;
		this.verifyClient = verifyClient;
		this.access = access;
		this.cachedWithin=cachedWithin;
		this.localMode=localMode;
	}
	
	/**
	 * @deprecated only supported for old compile templates in .ra archives
	 * */
	public UDFPropertiesImpl(
	        PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        String strReturnType, 
	        String strReturnFormat, 
	        boolean output,
	        int access, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        long cachedWithin,
	        StructImpl meta) {
		this(pageSource, arguments, index, functionName, strReturnType, strReturnFormat, 
				output,  access, null,displayName, description, hint, secureJson, verifyClient, cachedWithin,null, meta);
	}
	
	/**
	 * @deprecated only supported for old compile templates in .ra archives
	 * */
	public UDFPropertiesImpl(
	        PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        int access, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        long cachedWithin,
	        StructImpl meta) {
		this(pageSource, arguments, index, functionName, returnType, strReturnFormat, 
				output,  access,null, displayName, description, hint, secureJson, verifyClient, cachedWithin, null, meta);
	}
		
	/**
	 * @deprecated only supported for very old compile templates in .ra archives
	 * */
	public UDFPropertiesImpl(
	        PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        int access) {
		this(pageSource, arguments, index, functionName, returnType,strReturnFormat, output, access, null,
				"","", "", null, null, 0L, null, null);
	}

	@Override
	public long sizeOf() {
		return 
		SizeOf.size(functionName)+
		SizeOf.size(returnType)+
		SizeOf.size(strReturnType)+
		SizeOf.size(output)+
		SizeOf.size(bufferOutput)+
		SizeOf.size(hint)+
		SizeOf.size(index)+
		SizeOf.size(displayName)+
		SizeOf.size(arguments)+
		SizeOf.size(meta)+
		SizeOf.size(description)+
		SizeOf.size(secureJson)+
		SizeOf.size(verifyClient)+
		SizeOf.size(strReturnFormat)+
		SizeOf.size(returnFormat)+
		SizeOf.size(cachedWithin);
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
			String path=ExternalizableUtil.readString(in);
			pageSource=PageSourceImpl.best(cw.getPageSources(pc,null, path, false,true,true));
			
		} 
		catch (Throwable e) {
			e.printStackTrace();
			throw ExceptionUtil.toIOException(e);
		}
		
		arguments=(FunctionArgument[]) in.readObject();
		access = in.readInt();
		index = in.readInt();
		returnFormat = in.readInt();
		returnType = in.readInt();
		description = ExternalizableUtil.readString(in);
		displayName = ExternalizableUtil.readString(in);
		functionName = ExternalizableUtil.readString(in);
		hint = ExternalizableUtil.readString(in);
		meta = (Struct) in.readObject();
		output = in.readBoolean();
		bufferOutput = ExternalizableUtil.readBoolean(in);
		secureJson = ExternalizableUtil.readBoolean(in);
		strReturnFormat = ExternalizableUtil.readString(in);
		strReturnType = ExternalizableUtil.readString(in);
		verifyClient = ExternalizableUtil.readBoolean(in);
		cachedWithin = in.readLong();
		
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
		ExternalizableUtil.writeString(out,description);
		ExternalizableUtil.writeString(out,displayName);
		ExternalizableUtil.writeString(out,functionName);
		ExternalizableUtil.writeString(out,hint);
		out.writeObject(meta);
		out.writeBoolean(output);
		ExternalizableUtil.writeBoolean(out,bufferOutput);
		ExternalizableUtil.writeBoolean(out,secureJson);
		ExternalizableUtil.writeString(out,strReturnFormat);
		ExternalizableUtil.writeString(out,strReturnType);
		ExternalizableUtil.writeBoolean(out,verifyClient);
		out.writeLong(cachedWithin);
		
		
	}




}