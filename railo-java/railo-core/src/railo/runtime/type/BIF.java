package railo.runtime.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.MemberSupport;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.cast.Casting;
import railo.runtime.interpreter.ref.func.BIFCall;
import railo.runtime.interpreter.ref.literal.LFunctionValue;
import railo.runtime.interpreter.ref.literal.LString;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.UDFUtil;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public class BIF extends MemberSupport implements UDFPlus {
	
	private final FunctionLibFunction flf;
	private short rtnType=CFTypes.TYPE_UNKNOW;
	private ComponentImpl owner;
	private final ConfigImpl ci;
	private FunctionArgument[] args;

	public BIF(Config config,String name) throws ApplicationException{
		super(Component.ACCESS_PUBLIC);
		ci=(ConfigImpl) config;
		FunctionLib fl = ci.getCombinedFLDs();
		flf = fl.getFunction(name);
		
		// BIF not fuound
		if(flf==null) {
			Key[] keys = CollectionUtil.toKeys(fl.getFunctions().keySet());
			throw new ApplicationException(ExceptionUtil.similarKeyMessage(keys, name, "build in function", "build in functions", false));
		}
	}
	
	public BIF(Config config, FunctionLibFunction flf) {
		super(Component.ACCESS_PUBLIC);
		ci=(ConfigImpl) config;
		this.flf=flf;
	}

	@Override
	public FunctionArgument[] getFunctionArguments() {
		if(args==null) {
			ArrayList<FunctionLibFunctionArg> src = flf.getArg();
			args = new FunctionArgument[src.size()];
			
			String def;
			int index=-1;
			FunctionLibFunctionArg arg;
			Iterator<FunctionLibFunctionArg> it = src.iterator();
			while(it.hasNext()){
				arg = it.next();
				def = arg.getDefaultValue();
				args[++index]=new FunctionArgumentImpl(
						KeyImpl.init(arg.getName())
						, arg.getTypeAsString()
						, arg.getType()
						, arg.getRequired()
						, def==null?FunctionArgument.DEFAULT_TYPE_NULL:FunctionArgument.DEFAULT_TYPE_LITERAL
						, true
						, arg.getName()
						, arg.getDescription()
						, null);
			}
		}
		
		return args;
	}

	@Override
	public Object callWithNamedValues(PageContext pageContext, Struct values, boolean doIncludePath) throws PageException {
		ArrayList<FunctionLibFunctionArg> flfas = flf.getArg();
		Iterator<FunctionLibFunctionArg> it = flfas.iterator();
		FunctionLibFunctionArg arg;
		Object val;

		List<Ref> refs=new ArrayList<Ref>();
		while(it.hasNext()){
			arg=it.next();
			
			// match by name
			val = values.get(arg.getName(),null);
			
			//match by alias
			if(val==null) {
				String alias=arg.getAlias();
				if(!StringUtil.isEmpty(alias,true)) {
					String[] aliases = railo.runtime.type.util.ListUtil.trimItems(railo.runtime.type.util.ListUtil.listToStringArray(alias,','));
					for(int x=0;x<aliases.length;x++){
						val = values.get(aliases[x],null);
						if(val!=null) break;
					}
				}
			}
			
			if(val==null) {
				if(arg.getRequired()) {
					String[] names = flf.getMemberNames();
					String n=ArrayUtil.isEmpty(names)?"":names[0];
					throw new ExpressionException("missing required argument ["+arg.getName()+"] for build in function call ["+n+"]");
				}
			}
			else{
				refs.add(new Casting(arg.getTypeAsString(),arg.getType(),new LFunctionValue(new LString(arg.getName()),val)));
			}
		}
		
		BIFCall call=new BIFCall(flf, refs.toArray(new Ref[refs.size()]));
		return call.getValue(pageContext);
	}


	@Override
	public Object call(PageContext pageContext, Object[] args, boolean doIncludePath) throws PageException {
		ArrayList<FunctionLibFunctionArg> flfas = flf.getArg();
		FunctionLibFunctionArg flfa;
		List<Ref> refs=new ArrayList<Ref>();
		for(int i=0;i<args.length;i++){
			if(i>=flfas.size()) throw new ApplicationException("too many Attributes in function call ["+flf.getName()+"]");
			flfa=flfas.get(i);
			refs.add(new Casting(flfa.getTypeAsString(),flfa.getType(),args[i]));
		}
		BIFCall call=new BIFCall(flf, refs.toArray(new Ref[refs.size()]));
		return call.getValue(pageContext);
	}

	@Override
	public Object callWithNamedValues(PageContext pageContext, Key calledName, Struct values, boolean doIncludePath) throws PageException {
		return callWithNamedValues(pageContext, values, doIncludePath);
	}

	@Override
	public Object call(PageContext pageContext, Key calledName, Object[] args, boolean doIncludePath) throws PageException {
		return call(pageContext, args, doIncludePath);
	}
	






	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable dt= (DumpTable) UDFUtil.toDumpData(pageContext, maxlevel, dp,this,UDFUtil.TYPE_BIF);
		//dt.setTitle(title);
		return dt;
	}

	@Override
	public UDF duplicate() {
		return new BIF(ci, flf);
	}

	@Override
	public Object duplicate(boolean deepCopy) {
		return duplicate();
	}

	@Override
	public Component getOwnerComponent() {
		return owner;
	}

	@Override
	public String getDisplayName() {
		return flf.getName();
	}

	@Override
	public String getHint() {
		return flf.getDescription();
	}

	@Override
	public String getFunctionName() {
		return flf.getName();
	}

	@Override
	public int getReturnType() {
		if(rtnType==CFTypes.TYPE_UNKNOW)
			rtnType=CFTypes.toShort(flf.getReturnTypeAsString(), false, CFTypes.TYPE_UNKNOW);
		return rtnType;
	}

	@Override
	public String getDescription() {
		return flf.getDescription();
	}

	@Override
	public void setOwnerComponent(ComponentImpl owner) {
		this.owner=owner;
	}

	@Override
	public int getReturnFormat(int defaultFormat) {
		return getReturnFormat();
	}

	@Override
	public int getReturnFormat() {
		return UDF.RETURN_FORMAT_WDDX;
	}

	@Override
	public String getReturnTypeAsString() {
		return flf.getReturnTypeAsString();
	}
	


	@Override
	public Object getValue() {
		return this;
	}
	
	@Override
	public boolean getOutput() {
		return false;
	}

	@Override
	public Object getDefaultValue(PageContext pc, int index) throws PageException {
		return null;
	}

	@Override
	public Boolean getSecureJson() {
		return null;
	}

	@Override
	public Boolean getVerifyClient() {
		return null;
	}

	@Override
	public PageSource getPageSource() {
		return null;
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException {
		return null;
	}

	
	
	// MUST
	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return new StructImpl();
	}

	@Override
	public Object implementation(PageContext pageContext) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}


}
