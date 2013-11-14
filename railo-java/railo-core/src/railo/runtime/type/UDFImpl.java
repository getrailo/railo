package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.jsp.tagext.BodyContent;

import railo.commons.io.cache.Cache;
import railo.commons.lang.CFTypes;
import railo.commons.lang.SizeOf;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.cache.ram.RamCache;
import railo.runtime.component.MemberSupport;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.UDFCasterException;
import railo.runtime.functions.cache.Util;
import railo.runtime.listener.ApplicationContextSupport;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.LocalImpl;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.udf.UDFCacheEntry;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.UDFUtil;
import railo.runtime.writer.BodyContentUtil;

/**
 * defines a abstract class for a User defined Functions
 */
public class UDFImpl extends MemberSupport implements UDFPlus,Sizeable,Externalizable {
	
	private static final RamCache DEFAULT_CACHE=new RamCache();
	private static final long serialVersionUID = -7288148349256615519L; // do not change

	
	
	protected ComponentImpl ownerComponent;
	protected UDFPropertiesImpl properties;
    
	/**
	 * DO NOT USE THIS CONSTRUCTOR!
	 * this constructor is only for deserialize process
	 */
	public UDFImpl(){
		super(0);
	}
	
	public UDFImpl(UDFProperties properties) {
		super(properties.getAccess());
		this.properties= (UDFPropertiesImpl) properties;
	}

	@Override
	public long sizeOf() {
		return SizeOf.size(properties);
	}
	
	public UDF duplicate(ComponentImpl cfc) {
		UDFImpl udf = new UDFImpl(properties);
		udf.ownerComponent=cfc;
		udf.setAccess(getAccess());
		return udf;
	}
	
	@Override
	public UDF duplicate(boolean deepCopy) {
		return duplicate(ownerComponent);
	}
	
	@Override
	public UDF duplicate() {
		return duplicate(ownerComponent);
	}

	@Override
	public Object implementation(PageContext pageContext) throws Throwable {
		return ComponentUtil.getPage(pageContext, properties.pageSource).udfCall(pageContext,this,properties.index);
	}

	private final Object castToAndClone(PageContext pc,FunctionArgument arg,Object value, int index) throws PageException {
		//if(value instanceof Array)print.out(count++);
		if(Decision.isCastableTo(arg.getType(),arg.getTypeAsString(),value)) 
			return arg.isPassByReference()?value:Duplicator.duplicate(value,false);
		throw new UDFCasterException(this,arg,value,index);
		//REALCAST return Caster.castTo(pc,arg.getType(),arg.getTypeAsString(),value);
	}
	private final Object castTo(FunctionArgument arg,Object value, int index) throws PageException {
		if(Decision.isCastableTo(arg.getType(),arg.getTypeAsString(),value)) return value;
		throw new UDFCasterException(this,arg,value,index);
	}
	
	private void defineArguments(PageContext pc,FunctionArgument[] funcArgs, Object[] args,Argument newArgs) throws PageException {
		// define argument scope
		for(int i=0;i<funcArgs.length;i++) {
			// argument defined
			if(args.length>i) {
				newArgs.setEL(funcArgs[i].getName(),castToAndClone(pc,funcArgs[i], args[i],i+1));
			}
			// argument not defined
			else {
				Object d=getDefaultValue(pc,i,NullSupportHelper.NULL());
				if(d==NullSupportHelper.NULL()) { 
					if(funcArgs[i].isRequired()) {
						throw new ExpressionException("The parameter "+funcArgs[i].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
					}
					if(!NullSupportHelper.full()) newArgs.setEL(funcArgs[i].getName(),Argument.NULL);
				}
				else {
					newArgs.setEL(funcArgs[i].getName(),castTo(funcArgs[i],d,i+1));
				}
			}
		}
		for(int i=funcArgs.length;i<args.length;i++) {
			newArgs.setEL(ArgumentIntKey.init(i+1),args[i]);
		}
	}

	
    private void defineArguments(PageContext pageContext, FunctionArgument[] funcArgs, Struct values, Argument newArgs) throws PageException {
    	// argumentCollection
    	UDFUtil.argumentCollection(values,funcArgs);
    	//print.out(values.size());
    	Object value;
    	Collection.Key name;
		
    	for(int i=0;i<funcArgs.length;i++) {
			// argument defined
			name=funcArgs[i].getName();
			value=values.removeEL(name); 
			if(value!=null) {
				newArgs.set(name,castToAndClone(pageContext,funcArgs[i], value,i+1));
				continue;
			}
			value=values.removeEL(ArgumentIntKey.init(i+1)); 
			if(value!=null) {
				newArgs.set(name,castToAndClone(pageContext,funcArgs[i], value,i+1));
				continue;
			}
			
			
			// default argument or exception
			Object defaultValue=getDefaultValue(pageContext,i,NullSupportHelper.NULL());//funcArgs[i].getDefaultValue();
			if(defaultValue==NullSupportHelper.NULL()) { 
				if(funcArgs[i].isRequired()) {
					throw new ExpressionException("The parameter "+funcArgs[i].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
				}
				newArgs.set(name,Argument.NULL);
			}
			else newArgs.set(name,castTo(funcArgs[i],defaultValue,i+1));	
		}
		
		
		Iterator<Entry<Key, Object>> it = values.entryIterator();
    	Entry<Key, Object> e;
		while(it.hasNext()) {
			e = it.next();
			newArgs.set(e.getKey(),e.getValue());
		}
	}
    

	
	
	public static Collection.Key toKey(Object obj) {
		if(obj==null) return null;
		if(obj instanceof Collection.Key) return (Collection.Key) obj;
		String str = Caster.toString(obj,null);
		if(str==null) return KeyImpl.init(obj.toString());
		return KeyImpl.init(str);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Struct values,boolean doIncludePath) throws PageException {
    	return this.properties.cachedWithin>0?
    			_callCachedWithin(pc,null, null, values, doIncludePath):
    			_call(pc,null, null, values, doIncludePath);
    }
	public Object callWithNamedValues(PageContext pc,Collection.Key calledName, Struct values,boolean doIncludePath) throws PageException {
    	return this.properties.cachedWithin>0?
    			_callCachedWithin(pc,calledName, null, values, doIncludePath):
    			_call(pc,calledName, null, values, doIncludePath);
    }

    @Override
	public Object call(PageContext pc, Object[] args, boolean doIncludePath) throws PageException {
    	return  this.properties.cachedWithin>0?
    			_callCachedWithin(pc,null, args,null, doIncludePath):
    			_call(pc,null, args,null, doIncludePath);
    }

	public Object call(PageContext pc,Collection.Key calledName, Object[] args, boolean doIncludePath) throws PageException {
    	return  this.properties.cachedWithin>0?
    			_callCachedWithin(pc,calledName, args,null, doIncludePath):
    			_call(pc,calledName, args,null, doIncludePath);
    }
   // private static int count=0;
    
    

    private Object _callCachedWithin(PageContext pc,Collection.Key calledName, Object[] args, Struct values,boolean doIncludePath) throws PageException {
    	PageContextImpl pci=(PageContextImpl) pc;
    	String id = UDFUtil.callerHash(this,args,values);
    	
		Cache cache = Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_FUNCTION,DEFAULT_CACHE);	
		Object o =  cache.getValue(id,null);
		
		// get from cache
		if(o instanceof UDFCacheEntry ) {
			UDFCacheEntry entry = (UDFCacheEntry)o;
			//if(entry.creationdate+properties.cachedWithin>=System.currentTimeMillis()) {
				try {
					pc.write(entry.output);
				} catch (IOException e) {
					throw Caster.toPageException(e);
				}
				return entry.returnValue;
			//}
			
			//cache.remove(id);
		}
    	
		// execute the function
		BodyContent bc =  pci.pushBody();
	    
	    try {
	    	Object rtn = _call(pci,calledName, args, values, doIncludePath);
	    	String out = bc.getString();
	    	cache.put(id, new UDFCacheEntry(out, rtn),properties.cachedWithin,properties.cachedWithin);
	    	return rtn;
		}
        finally {
        	BodyContentUtil.flushAndPop(pc,bc);
        }
    }
    
    private Object _call(PageContext pc,Collection.Key calledName, Object[] args, Struct values,boolean doIncludePath) throws PageException {
    	
    	//print.out(count++);
    	PageContextImpl pci=(PageContextImpl) pc;
    	Argument newArgs= pci.getScopeFactory().getArgumentInstance();
        newArgs.setFunctionArgumentNames(properties.argumentsSet);
        LocalImpl newLocal=pci.getScopeFactory().getLocalInstance();
        
		Undefined 	undefined=pc.undefinedScope();
		Argument	oldArgs=pc.argumentsScope();
        Local		oldLocal=pc.localScope();
        Collection.Key oldCalledName=pci.getActiveUDFCalledName();
        
		pc.setFunctionScopes(newLocal,newArgs);
		pci.setActiveUDFCalledName(calledName);
		
		int oldCheckArgs=undefined.setMode(properties.localMode==null?pc.getApplicationContext().getLocalMode():properties.localMode.intValue());
		PageSource psInc=null;
		try {
			PageSource ps = getPageSource();
			if(doIncludePath)psInc = ps;
			//if(!ps.getDisplayPath().endsWith("Dump.cfc"))print.e(getPageSource().getDisplayPath());
			if(doIncludePath && getOwnerComponent()!=null) {
				//if(!ps.getDisplayPath().endsWith("Dump.cfc"))print.ds(ps.getDisplayPath());
				psInc=ComponentUtil.getPageSource(getOwnerComponent());
				if(psInc==pci.getCurrentTemplatePageSource()) {
					psInc=null;
				}
				
			}
			pci.addPageSource(ps,psInc);
			pci.addUDF(this);
			
//////////////////////////////////////////
			BodyContent bc=null;
			Boolean wasSilent=null;
			boolean bufferOutput=getBufferOutput(pci);
			if(!getOutput()) {
				if(bufferOutput) bc =  pci.pushBody();
				else wasSilent=pc.setSilent()?Boolean.TRUE:Boolean.FALSE;
			}
			
		    UDF parent=null;
		    if(ownerComponent!=null) {
			    parent=pci.getActiveUDF();
			    pci.setActiveUDF(this);
		    }
		    Object returnValue = null;
		    
		    try {
		    	
		    	if(args!=null)	defineArguments(pc,getFunctionArguments(),args,newArgs);
				else 			defineArguments(pc,getFunctionArguments(),values,newArgs);
		    	
				returnValue=implementation(pci);
				if(ownerComponent!=null)pci.setActiveUDF(parent);
			}
	        catch(Throwable t) {
	        	if(ownerComponent!=null)pci.setActiveUDF(parent);
	        	if(!getOutput()) {
	        		if(bufferOutput)BodyContentUtil.flushAndPop(pc,bc);
	        		else if(!wasSilent)pc.unsetSilent();
	        	}
	        	//BodyContentUtil.flushAndPop(pc,bc);
	        	throw Caster.toPageException(t);
	        }
	        if(!getOutput()) {
        		if(bufferOutput)BodyContentUtil.clearAndPop(pc,bc);
        		else if(!wasSilent)pc.unsetSilent();
        	}
	        //BodyContentUtil.clearAndPop(pc,bc);
        	
	        
	        
	        
	        if(properties.returnType==CFTypes.TYPE_ANY) return returnValue;
	        else if(Decision.isCastableTo(properties.strReturnType,returnValue,false,false,-1)) return returnValue;
	        else throw new UDFCasterException(this,properties.strReturnType,returnValue);
			//REALCAST return Caster.castTo(pageContext,returnType,returnValue,false);
//////////////////////////////////////////
			
		}
		finally {
			pc.removeLastPageSource(psInc!=null);
			pci.removeUDF();
            pci.setFunctionScopes(oldLocal,oldArgs);
            pci.setActiveUDFCalledName(oldCalledName);
		    undefined.setMode(oldCheckArgs);
            pci.getScopeFactory().recycle(newArgs);
            pci.getScopeFactory().recycle(newLocal);
		}
	}

    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return UDFUtil.toDumpData(pageContext, maxlevel, dp,this,false);
	}
	
	@Override
	public String getDisplayName() {
		return properties.displayName;
	}
	
	@Override
	public String getHint() {
		return properties.hint;
	}
    
	@Override
	public PageSource getPageSource() {
        return properties.pageSource;
    }

	public Struct getMeta() {
		return properties.meta;
	}
	
	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		return ComponentUtil.getMetaData(pc, properties);
		//return getMetaData(pc, this);
	}

	@Override
	public Object getValue() {
		return this;
	}


	/**
	 * @param componentImpl the componentImpl to set
	 * @param injected 
	 */
	public void setOwnerComponent(ComponentImpl component) {
		this.ownerComponent = component;
	}
	
	@Override
	public Component getOwnerComponent() {
		return ownerComponent;//+++
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer(properties.functionName);
		sb.append("(");
		int optCount=0;
		for(int i=0;i<properties.arguments.length;i++) {
			if(i>0)sb.append(", ");
			if(!properties.arguments[i].isRequired()){
				sb.append("[");
				optCount++;
			}
			sb.append(properties.arguments[i].getTypeAsString());
			sb.append(" ");
			sb.append(properties.arguments[i].getName());
		}
		for(int i=0;i<optCount;i++){
			sb.append("]");
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Boolean getSecureJson() {
		return properties.secureJson;
	}

	@Override
	public Boolean getVerifyClient() {
		return properties.verifyClient;
	}
	
	@Override
	public Object clone() {
		return duplicate();
	}

	@Override
	public FunctionArgument[] getFunctionArguments() {
        return properties.arguments;
    }
	
	@Override
	public Object getDefaultValue(PageContext pc,int index) throws PageException {
    	return UDFUtil.getDefaultValue(pc,properties.pageSource,properties.index,index,null);
    }
    
    @Override
	public Object getDefaultValue(PageContext pc,int index, Object defaultValue) throws PageException {
    	return UDFUtil.getDefaultValue(pc,properties.pageSource,properties.index,index,defaultValue);
    }
    // public abstract Object getDefaultValue(PageContext pc,int index) throws PageException;

    @Override
	public String getFunctionName() {
		return properties.functionName;
	}

	@Override
	public boolean getOutput() {
		return properties.output;
	}
	
	public Boolean getBufferOutput() {
		return properties.bufferOutput;
	}
	
	private boolean getBufferOutput(PageContextImpl pc) {// FUTURE move to interface
		if(properties.bufferOutput!=null)
			return properties.bufferOutput.booleanValue();
		return ((ApplicationContextSupport)pc.getApplicationContext()).getBufferOutput();
	}

	@Override
	public int getReturnType() {
		return properties.returnType;
	}
	
	@Override
	public String getReturnTypeAsString() {
		return properties.strReturnType;
	}
	
	@Override
	public String getDescription() {
		return properties.description;
	}
	
	@Override
	public int getReturnFormat() {
		if(properties.returnFormat<0) return UDF.RETURN_FORMAT_WDDX;
		return properties.returnFormat;
	}
	
	@Override
	public int getReturnFormat(int defaultValue) {
		if(properties.returnFormat<0) return defaultValue;
		return properties.returnFormat;
	}
	
	public final String getReturnFormatAsString() {
		return properties.strReturnFormat;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// access
		setAccess(in.readInt());
		
		// properties
		properties=(UDFPropertiesImpl) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// access
		out.writeInt(getAccess());
		
		// properties
		out.writeObject(properties);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof UDF)) return false;
		return equals(this,(UDF)obj);
	}
	public static boolean equals(UDF left, UDF right){
		if(
			!left.getPageSource().equals(right.getPageSource())
			|| !_eq(left.getFunctionName(),right.getFunctionName())
			|| left.getAccess()!=right.getAccess()
			|| !_eq(left.getFunctionName(),right.getFunctionName())
			|| left.getOutput()!=right.getOutput()
			|| left.getReturnFormat()!=right.getReturnFormat()
			|| left.getReturnType()!=right.getReturnType()
			|| !_eq(left.getReturnTypeAsString(),right.getReturnTypeAsString())
			|| !_eq(left.getSecureJson(),right.getSecureJson())
			|| !_eq(left.getVerifyClient(),right.getVerifyClient())
		) return false;

		// Arguments
		FunctionArgument[] largs = left.getFunctionArguments();
		FunctionArgument[] rargs = right.getFunctionArguments();
		if(largs.length!=rargs.length) return false;
		for(int i=0;i<largs.length;i++){
			if(!largs[i].equals(rargs[i]))return false;
		}
		
		
		
		
		return true;
	}

	private static boolean _eq(Object left, Object right) {
		if(left==null) return right==null;
		return left.equals(right);
	}
	
	public int getIndex(){
		return properties.index;
	}
	
}

