package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.cast.Casting;
import railo.runtime.interpreter.ref.func.BIFCall;
import railo.runtime.interpreter.ref.literal.LFunctionValue;
import railo.runtime.interpreter.ref.literal.LString;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public class MemberUtil {
	
	private static final Object DEFAULT_VALUE = new Object();
	private static Map<Short,Map<Collection.Key,FunctionLibFunction>> matches=new HashMap<Short, Map<Collection.Key,FunctionLibFunction>>();
	
	public static Map<Collection.Key,FunctionLibFunction> getMembers(PageContext pc, short type) {
		
		Map<Key, FunctionLibFunction> match = matches.get(type);
		if(match!=null) return match;
		
		FunctionLib[] flds = ((ConfigWebImpl)pc.getConfig()).getFLDs();
		Iterator<FunctionLibFunction> it;
		FunctionLibFunction f;
		match=new HashMap<Collection.Key,FunctionLibFunction>();
		for(int i=0;i<flds.length;i++){
			 it = flds[i].getFunctions().values().iterator();
			 while(it.hasNext()){
				 f = it.next();
				 if(f.getMemberName()!=null && f.getMemberType()==type && f.getArgType()==FunctionLibFunction.ARG_FIX) {
					 match.put(KeyImpl.getInstance(f.getMemberName()),f);
				 }
			 }
		}
		matches.put(type, match);
		return match;
	}
	
	public static Object call(PageContext pc, Object coll,Collection.Key methodName, Object[] args, short type, String strType) throws PageException {
		Map<Key, FunctionLibFunction> members = getMembers(pc, type);
		FunctionLibFunction member=members.get(methodName); 
		
		if(member!=null){
			List<FunctionLibFunctionArg> _args = member.getArg();
			FunctionLibFunctionArg arg;
			if(args.length<_args.size()){
				ArrayList<Ref> refs=new ArrayList<Ref>();
				refs.add(new Casting(strType,type,coll));
				for(int y=0;y<args.length;y++){
					arg = _args.get(y+1);
					refs.add(new Casting(arg.getTypeAsString(),arg.getType(),args[y]));
				}
				return new BIFCall(coll, member, refs.toArray(new Ref[refs.size()])).getValue(pc);
			}
			
		}
		if(pc.getConfig().getSecurityManager().getAccess(railo.runtime.security.SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==railo.runtime.security.SecurityManager.VALUE_YES) {
			return Reflector.callMethod(coll,methodName,args);
			//Object res = Reflector.callMethod(coll,methodName,args,DEFAULT_VALUE);
	    	//if(res!=DEFAULT_VALUE) return res;
	    } 
		throw new ExpressionException("No matching function member ["+methodName+"] found, available function members are ["+railo.runtime.type.util.ListUtil.sort(CollectionUtil.getKeyList(members.keySet().iterator(), ","),"textnocase","asc",",")+"]");
	}

	public static Object callWithNamedValues(PageContext pc,Object coll, Collection.Key methodName, Struct args,short type, String strType) throws PageException {
		Map<Key, FunctionLibFunction> members = getMembers(pc, type);
		FunctionLibFunction member=members.get(methodName); 
		
		if(member!=null){
			List<FunctionLibFunctionArg> _args = member.getArg();
			FunctionLibFunctionArg arg;
			if(args.size()<_args.size()){
				Object val;
				ArrayList<Ref> refs=new ArrayList<Ref>();
				arg=_args.get(0);
				refs.add(new Casting(arg.getTypeAsString(),arg.getType(),new LFunctionValue(new LString(arg.getName()),coll)));
				for(int y=1;y<_args.size();y++){
					arg = _args.get(y);
					
					// match by name
					val = args.get(arg.getName(),null);
					
					//match by alias
					if(val==null) {
						String alias=arg.getAlias();
						if(!StringUtil.isEmpty(alias,true)) {
							String[] aliases = railo.runtime.type.util.ListUtil.trimItems(railo.runtime.type.util.ListUtil.listToStringArray(alias,','));
							for(int x=0;x<aliases.length;x++){
								val = args.get(aliases[x],null);
								if(val!=null) break;
							}
						}
					}
					
					if(val==null) {
						if(arg.getRequired()) {
							throw new ExpressionException("missing required argument ["+arg.getName()+"] for member function call ["+member.getMemberName()+"]");
						}
					}
					else{
						refs.add(new Casting(arg.getTypeAsString(),arg.getType(),new LFunctionValue(new LString(arg.getName()),val)));
						//refs.add(new LFunctionValue(new LString(arg.getName()),new Casting(pc,arg.getTypeAsString(),arg.getType(),val)));
					}
					
				}
				return new BIFCall(coll,member, refs.toArray(new Ref[refs.size()])).getValue(pc);
			}
			
		}
		throw new ExpressionException("No matching function member ["+methodName+"] for call with named arguments found, available function members are ["+railo.runtime.type.util.ListUtil.sort(CollectionUtil.getKeyList(members.keySet().iterator(), ","),"textnocase","asc",",")+"]");
	}
	
}
