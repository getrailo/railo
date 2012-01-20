package railo.runtime.type;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.tagext.BodyContent;

import railo.commons.lang.CFTypes;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.MemberSupport;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.UDFCasterException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.LocalImpl;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.writer.BodyContentUtil;

/**
 * defines a abstract class for a User defined Functions
 */
public class UDFImpl extends MemberSupport implements UDF,Sizeable,Externalizable {
	
	private static final FunctionArgument[] EMPTY = new FunctionArgument[0];
	
	
	
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

	/**
	 * @see railo.runtime.engine.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(properties);
	}
    
	
	public UDF duplicate(ComponentImpl c) {
		UDFImpl udf = new UDFImpl(properties);
		udf.ownerComponent=c;
		udf.setAccess(getAccess());
		return udf;
	}
	
	public UDF duplicate(boolean deepCopy) {
		return duplicate(ownerComponent);
	}
	
	public UDF duplicate() {
		return duplicate(ownerComponent);
	}

	/**
     * @throws Throwable 
	 * @see railo.runtime.type.UDF#implementation(railo.runtime.PageContext)
     */
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
				Object d=getDefaultValue(pc,i);
				if(d==null) { 
					if(funcArgs[i].isRequired()) {
						throw new ExpressionException("The parameter "+funcArgs[i].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
					}
					newArgs.setEL(funcArgs[i].getName(),Argument.NULL);
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
    	argumentCollection(values,funcArgs);
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
			else {
				value=values.removeEL(ArgumentIntKey.init(i+1)); 
				if(value!=null) {
					newArgs.set(name,castToAndClone(pageContext,funcArgs[i], value,i+1));
					continue;
				}
			}
			
			// default argument or exception
			Object defaultValue=getDefaultValue(pageContext,i);//funcArgs[i].getDefaultValue();
			if(defaultValue==null) { 
				if(funcArgs[i].isRequired()) {
					throw new ExpressionException("The parameter "+funcArgs[i].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
				}
				newArgs.set(name,Argument.NULL);
			}
			else newArgs.set(name,castTo(funcArgs[i],defaultValue,i+1));	
		}
		
		
		Collection.Key[] arr=values.keys();
		for(int i=0;i<arr.length;i++) {
			newArgs.set(arr[i],values.get(arr[i],null));
		}
	}
    

	public static void argumentCollection(Struct values) {
		argumentCollection(values,EMPTY);
	}

	public static void argumentCollection(Struct values, FunctionArgument[] funcArgs) {
		Object value=values.removeEL(KeyImpl.ARGUMENT_COLLECTION);
		if(value !=null) {
			value=Caster.unwrap(value,value);
			
			if(value instanceof Argument) {
				Argument argColl=(Argument) value;
			    Collection.Key[] keys = argColl.keys();
			    for(int i=0;i<keys.length;i++) {
			    	if(funcArgs.length>i && keys[i] instanceof ArgumentIntKey) {
	            		if(!values.containsKey(funcArgs[i].getName()))
	            			values.setEL(funcArgs[i].getName(),argColl.get(keys[i],Argument.NULL));
	            		else 
	            			values.setEL(keys[i],argColl.get(keys[i],Argument.NULL));
			    	}
	            	else if(!values.containsKey(keys[i])){
	            		values.setEL(keys[i],argColl.get(keys[i],Argument.NULL));
	            	}
	            }
		    }
			else if(value instanceof Collection) {
		        Collection argColl=(Collection) value;
			    Collection.Key[] keys = argColl.keys();
			    for(int i=0;i<keys.length;i++) {
			    	if(!values.containsKey(keys[i])){
	            		values.setEL(keys[i],argColl.get(keys[i],Argument.NULL));
	            	}
	            }
		    }
			else if(value instanceof Map) {
				Map map=(Map) value;
			    Iterator it = map.entrySet().iterator();
			    Map.Entry entry;
			    Key key;
			    while(it.hasNext()) {
			    	entry=(Entry) it.next();
			    	key = toKey(entry.getKey());
			    	if(!values.containsKey(key)){
	            		values.setEL(key,entry.getValue());
	            	}
	            }
		    }
			else if(value instanceof java.util.List) {
				java.util.List list=(java.util.List) value;
			    Iterator it = list.iterator();
			    Object v;
			    int index=0;
			    Key k;
			    while(it.hasNext()) {
			    	v= it.next();
			    	k=ArgumentIntKey.init(++index);
			    	if(!values.containsKey(k)){
	            		values.setEL(k,v);
	            	}
	            }
		    }
		    else {
		        values.setEL(KeyImpl.ARGUMENT_COLLECTION,value);
		    }
		} 
	}
	
	public static Collection.Key toKey(Object obj) {
		if(obj==null) return null;
		if(obj instanceof Collection.Key) return (Collection.Key) obj;
		String str = Caster.toString(obj,null);
		if(str==null) return KeyImpl.init(obj.toString());
		return KeyImpl.init(str);
	}

	/**
     * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
     */
    public Object callWithNamedValues(PageContext pc, Struct values,boolean doIncludePath) throws PageException {
    	return _call(pc, null, values, doIncludePath);
    }

	/**
     * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
     */
    public Object call(PageContext pc, Object[] args, boolean doIncludePath) throws PageException {
    	return _call(pc, args,null, doIncludePath);
    }
   // private static int count=0;
    private Object _call(PageContext pc, Object[] args, Struct values,boolean doIncludePath) throws PageException {
    	//print.out(count++);
    	PageContextImpl pci=(PageContextImpl) pc;
    	Argument newArgs= pci.getScopeFactory().getArgumentInstance();
        newArgs.setFunctionArgumentNames(properties.argumentsSet);
        LocalImpl newLocal=pci.getScopeFactory().getLocalInstance();
        
		Undefined 	undefined=pc.undefinedScope();
		Argument	oldArgs=pc.argumentsScope();
        Local		oldLocal=pc.localScope();
        
		pci.setFunctionScopes(newLocal,newArgs);
		int oldCheckArgs=undefined.setMode(pc.getApplicationContext().getLocalMode());
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
//////////////////////////////////////////
			BodyContent bc =  (getOutput()?null:pci.pushBody());
		    //boolean isC=ownerComponent!=null;
		    
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
	        	BodyContentUtil.flushAndPop(pc,bc);
	        	throw Caster.toPageException(t);
	        }
	        BodyContentUtil.clearAndPop(pc,bc);
	        //pc.popBody();
			
	        
	        
	        
	        if(properties.returnType==CFTypes.TYPE_ANY) return returnValue;
	        else if(Decision.isCastableTo(properties.strReturnType,returnValue,false)) return returnValue;
	        else throw new UDFCasterException(this,properties.strReturnType,returnValue);
			//REALCAST return Caster.castTo(pageContext,returnType,returnValue,false);
//////////////////////////////////////////
			
		}
		finally {
			pc.removeLastPageSource(psInc!=null);
            pci.setFunctionScopes(oldLocal,oldArgs);
		    undefined.setMode(oldCheckArgs);
            pci.getScopeFactory().recycle(newArgs);
            pci.getScopeFactory().recycle(newLocal);
		}
	}

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return toDumpData(pageContext, maxlevel, dp,this,false);
	}
	public static DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp,UDF udf, boolean closure) {
	
		if(!dp.getShowUDFs())
			return new SimpleDumpData(closure?"<Closure>":"<UDF>");
		
		// arguments
		FunctionArgument[] args = udf.getFunctionArguments();
        
        DumpTable atts = closure?new DumpTable("udf","#ff00ff","#ffccff","#000000"):new DumpTable("udf","#cc66ff","#ffccff","#000000");
        
		atts.appendRow(new DumpRow(63,new DumpData[]{new SimpleDumpData("label"),new SimpleDumpData("name"),new SimpleDumpData("required"),new SimpleDumpData("type"),new SimpleDumpData("default"),new SimpleDumpData("hint")}));
		for(int i=0;i<args.length;i++) {
			FunctionArgument arg=args[i];
			DumpData def;
			try {
				Object oa=null;
                try {
                    oa = udf.getDefaultValue(pageContext,i);
                } catch (PageException e1) {
                }
                if(oa==null)oa="null";
				def=new SimpleDumpData(Caster.toString(oa));
			} catch (PageException e) {
				def=new SimpleDumpData("");
			}
			atts.appendRow(new DumpRow(0,new DumpData[]{
					new SimpleDumpData(arg.getDisplayName()),
					new SimpleDumpData(arg.getName().getString()),
					new SimpleDumpData(arg.isRequired()),
					new SimpleDumpData(arg.getTypeAsString()),
					def,
					new SimpleDumpData(arg.getHint())}));
			//atts.setRow(0,arg.getHint());
			
		}
		
		DumpTable func = closure?new DumpTable("#ff00ff","#ffccff","#000000"):new DumpTable("#cc66ff","#ffccff","#000000");
		if(closure) func.setTitle("Closure");
		else {
			String f="Function ";
			try {
				f=StringUtil.ucFirst(ComponentUtil.toStringAccess(udf.getAccess()).toLowerCase())+" "+f;
			} 
			catch (ExpressionException e) {}
			func.setTitle(f+udf.getFunctionName());
		}
		
		
		
		
		if(udf instanceof UDFImpl)func.setComment("source:"+((UDFImpl)udf).getPageSource().getDisplayPath());
		
		
		if(!StringUtil.isEmpty(udf.getDescription()))func.setComment(udf.getDescription());
		
		
		
		func.appendRow(1,new SimpleDumpData("arguments"),atts);
		func.appendRow(1,new SimpleDumpData("return type"),new SimpleDumpData(udf.getReturnTypeAsString()));
		
		boolean hasLabel=!StringUtil.isEmpty(udf.getDisplayName());//displayName!=null && !displayName.equals("");
		boolean hasHint=!StringUtil.isEmpty(udf.getHint());//hint!=null && !hint.equals("");
		
		if(hasLabel || hasHint) {
			DumpTable box = new DumpTable("#ffffff","#cccccc","#000000");
			box.setTitle(hasLabel?udf.getDisplayName():udf.getFunctionName());
			if(hasHint)box.appendRow(0,new SimpleDumpData(udf.getHint()));
			box.appendRow(0,func);
			return box;
		}
		
		/* / meta data
		try {
			Struct meta = udf.getMetaData(pageContext);
			if(meta.size()>0)
				func.appendRow(1,new SimpleDumpData("metadata"),meta.toDumpData(pageContext, maxlevel, dp));
				
		}
		catch (PageException e) {}
		*/
		return func;
	}
	/**
     * @see railo.runtime.type.UDF#getDisplayName()
     */
	public String getDisplayName() {
		return properties.displayName;
	}
	/**
     * @see railo.runtime.type.UDF#getHint()
     */
	public String getHint() {
		return properties.hint;
	}
    
    /**
     * @see railo.runtime.type.UDF#getPageSource()
     */
    public PageSource getPageSource() {
        return properties.pageSource;
    }

	public Struct getMeta() {
		return properties.meta;
	}
	
	public Struct getMetaData(PageContext pc) throws PageException {
		return ComponentUtil.getMetaData(pc, properties);
		//return getMetaData(pc, this);
	}
	
	/*public static Struct getMetaData(PageContext pc,UDFImpl udf) throws PageException {
		StructImpl func=new StructImpl();
        
		// TODO func.set("roles", value);
        // TODO func.set("userMetadata", value); neo unterstﾟtzt irgendwelche a
        // meta data
        Struct meta = udf.getMeta();
        if(meta!=null) StructUtil.copy(meta, func, true);
        
		
		func.set(KeyImpl.ACCESS,ComponentUtil.toStringAccess(udf.getAccess()));
        String hint=udf.getHint();
        if(!StringUtil.isEmpty(hint))func.set(KeyImpl.HINT,hint);
        String displayname=udf.getDisplayName();
        if(!StringUtil.isEmpty(displayname))func.set(KeyImpl.DISPLAY_NAME,displayname);
        func.set(KeyImpl.NAME,udf.getFunctionName());
        func.set(KeyImpl.OUTPUT,Caster.toBoolean(udf.getOutput()));
        func.set(KeyImpl.RETURN_TYPE, udf.getReturnTypeAsString());
        func.set(KeyImpl.DESCRIPTION, udf.getDescription());
        
        func.set(KeyImpl.OWNER, udf.getPageSource().getDisplayPath());
        
	    	   
	    int format = udf.getReturnFormat();
        if(format==UDF.RETURN_FORMAT_WDDX)			func.set(KeyImpl.RETURN_FORMAT, "wddx");
        else if(format==UDF.RETURN_FORMAT_PLAIN)	func.set(KeyImpl.RETURN_FORMAT, "plain");
        else if(format==UDF.RETURN_FORMAT_JSON)	func.set(KeyImpl.RETURN_FORMAT, "json");
        else if(format==UDF.RETURN_FORMAT_SERIALIZE)func.set(KeyImpl.RETURN_FORMAT, "serialize");
        
        
        FunctionArgument[] args =  udf.getFunctionArguments();
        Array params=new ArrayImpl();
        //Object defaultValue;
        Struct m;
        //Object defaultValue;
        for(int y=0;y<args.length;y++) {
            StructImpl param=new StructImpl();
            param.set(KeyImpl.NAME,args[y].getName().getString());
            param.set(KeyImpl.REQUIRED,Caster.toBoolean(args[y].isRequired()));
            param.set(KeyImpl.TYPE,args[y].getTypeAsString());
            displayname=args[y].getDisplayName();
            if(!StringUtil.isEmpty(displayname)) param.set(KeyImpl.DISPLAY_NAME,displayname);
            
            int defType = args[y].getDefaultType();
            if(defType==FunctionArgument.DEFAULT_TYPE_RUNTIME_EXPRESSION){
            	param.set(KeyImpl.DEFAULT, "[runtime expression]");
            }
            else if(defType==FunctionArgument.DEFAULT_TYPE_LITERAL){
            	param.set(KeyImpl.DEFAULT, udf.getDefaultValue(pc,y));
            }
            
            hint=args[y].getHint();
            if(!StringUtil.isEmpty(hint))param.set(KeyImpl.HINT,hint);
            // TODO func.set("userMetadata", value); neo unterstﾟtzt irgendwelche attr, die dann hier ausgebenen werden blﾚdsinn
            
            // meta data
            m=args[y].getMetaData();
            if(m!=null) StructUtil.copy(m, param, true);
                
            params.append(param);
        }
        func.set(KeyImpl.PARAMETERS,params);
		return func;
	}*/

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
	
	/**
	 * @see railo.runtime.type.UDF#getOwnerComponent()
	 */
	public Component getOwnerComponent() {
		return ownerComponent;//+++
	}
	

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

	/**
	 * @see railo.runtime.type.UDF#getSecureJson()
	 */
	public Boolean getSecureJson() {
		return properties.secureJson;
	}

	/**
	 * @see railo.runtime.type.UDF#getVerifyClient()
	 */
	public Boolean getVerifyClient() {
		return properties.verifyClient;
	}
	
	/*public boolean isInjected() {
		return injected;
	}*/

	public Object clone() {
		return duplicate();
	}


	
	/**
     * @see railo.runtime.type.UDF#getFunctionArguments()
     */
    public FunctionArgument[] getFunctionArguments() {
        return properties.arguments;
    }
	
	/**
     * @see railo.runtime.type.UDF#getDefaultValue(railo.runtime.PageContext, int)
     */
    public Object getDefaultValue(PageContext pc,int index) throws PageException {
    	return ComponentUtil.getPage(pc,properties.pageSource).udfDefaultValue(pc,properties.index,index);
    }
    // public abstract Object getDefaultValue(PageContext pc,int index) throws PageException;

	/**
     * @see railo.runtime.type.UDF#getFunctionName()
     */
	public String getFunctionName() {
		return properties.functionName;
	}

	/**
     * @see railo.runtime.type.UDF#getOutput()
     */
	public boolean getOutput() {
		return properties.output;
	}

	/**
     * @see railo.runtime.type.UDF#getReturnType()
     */
	public int getReturnType() {
		return properties.returnType;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getReturnTypeAsString()
	 */
	public String getReturnTypeAsString() {
		return properties.strReturnType;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getDescription()
	 */
	public String getDescription() {
		return properties.description;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getReturnFormat()
	 */
	public int getReturnFormat() {
		return properties.returnFormat;
	}
	
	public final String getReturnFormatAsString() {
		return properties.strReturnFormat;
	}
	
	
	public static int toReturnFormat(String returnFormat) throws ExpressionException {
		if(StringUtil.isEmpty(returnFormat,true))
			return UDF.RETURN_FORMAT_WDDX;
			
			
		returnFormat=returnFormat.trim().toLowerCase();
		if("wddx".equals(returnFormat))				return UDF.RETURN_FORMAT_WDDX;
		else if("json".equals(returnFormat))		return UDF.RETURN_FORMAT_JSON;
		else if("plain".equals(returnFormat))		return UDF.RETURN_FORMAT_PLAIN;
		else if("text".equals(returnFormat))		return UDF.RETURN_FORMAT_PLAIN;
		else if("serialize".equals(returnFormat))	return UDF.RETURN_FORMAT_SERIALIZE;
		else throw new ExpressionException("invalid returnFormat definition ["+returnFormat+"], valid values are [wddx,plain,json,serialize]");
	}

	public static String toReturnFormat(int returnFormat) throws ExpressionException {
		if(RETURN_FORMAT_WDDX==returnFormat)		return "wddx";
		else if(RETURN_FORMAT_JSON==returnFormat)	return "json";
		else if(RETURN_FORMAT_PLAIN==returnFormat)	return "plain";
		else if(RETURN_FORMAT_SERIALIZE==returnFormat)	return "serialize";
		else throw new ExpressionException("invalid returnFormat definition, valid values are [wddx,plain,json,serialize]");
	}
	
	public static String toReturnFormat(int returnFormat,String defaultValue) {
		if(RETURN_FORMAT_WDDX==returnFormat)		return "wddx";
		else if(RETURN_FORMAT_JSON==returnFormat)	return "json";
		else if(RETURN_FORMAT_PLAIN==returnFormat)	return "plain";
		else if(RETURN_FORMAT_SERIALIZE==returnFormat)	return "serialize";
		else return defaultValue;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// access
		setAccess(in.readInt());
		
		// properties
		properties=(UDFPropertiesImpl) in.readObject();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		// access
		out.writeInt(getAccess());
		
		// properties
		out.writeObject(properties);
		
		
	}

	
}

