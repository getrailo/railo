package railo.intergral.fusiondebug.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.intergral.fusiondebug.server.type.FDVariable;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.ClusterNotSupported;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.KeyConstants;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDThread;
import com.intergral.fusiondebug.server.IFDVariable;

public class FDStackFrameImpl implements IFDStackFrame {


	private static final int[] SCOPES_AS_INT = new int[]{
		Scope.SCOPE_VARIABLES,Scope.SCOPE_CGI,Scope.SCOPE_URL,Scope.SCOPE_FORM,
		Scope.SCOPE_COOKIE,Scope.SCOPE_CLIENT,Scope.SCOPE_APPLICATION,Scope.SCOPE_CALLER,
		Scope.SCOPE_CLUSTER,Scope.SCOPE_REQUEST,Scope.SCOPE_SERVER,Scope.SCOPE_SESSION
	};
	private static final String[] SCOPES_AS_STRING = new String[]{
		"variables","cgi","url","form",
		"cookie","client","application","caller",
		"cluster","request","server","session"
	};
 
	private PageContextImpl pc;
	private FDThreadImpl thread;

	//private StackTraceElement trace;

	private PageSource ps;

	private int line;

	private static Comparator comparator=new FDVariableComparator();

	public FDStackFrameImpl(FDThreadImpl thread,PageContextImpl pc, StackTraceElement trace, PageSource ps){
		this(thread, pc, ps, trace.getLineNumber());
	} 
	public FDStackFrameImpl(FDThreadImpl thread,PageContextImpl pc, PageSource ps, int line){
		this.thread=thread;
		this.pc=pc;
		this.line=line;
		this.ps=ps;
	} 
	
	@Override
	public IFDVariable evaluate(String expression) throws FDLanguageException {
		try {
			return new FDVariable(this,expression,FDCaster.toFDValue(this,pc.evaluate(expression)));
		} 
		catch (PageException e) {
			throw new FDLanguageException(e);
		}
	}

	@Override
	public String getExecutionUnitName() {
		return ps.getClassName();
	}

	@Override
	public String getExecutionUnitPackage() {
		return ps.getPackageName();
	}

	@Override
	public int getLineNumber() {
		return line;
		
	}
	
	@Override
	public String getSourceFileName() {
		return ps.getFileName();
	}

	@Override
	public String getSourceFilePath() {
		String name = getSourceFileName();
		String path=ps.getDisplayPath();
		if(StringUtil.endsWithIgnoreCase(path, name))
			path=path.substring(0,path.length()-name.length());
		return path;
	}

	@Override
	public IFDThread getThread() {
		return thread;
	}

	@Override
	public List<String> getScopeNames() {
		List<String> implScopes = pc.undefinedScope().getScopeNames();
		for(int i=0;i<SCOPES_AS_INT.length;i++){
			if(!implScopes.contains(SCOPES_AS_STRING[i]) && enabled(pc,SCOPES_AS_INT[i]))
				implScopes.add(SCOPES_AS_STRING[i]);
		}
		return implScopes;
	}
	public static List<String> testScopeNames(PageContextImpl pc) {
		return new FDStackFrameImpl(null,pc,null,null).getScopeNames();
	}
	
	private static boolean enabled(PageContextImpl pc,int scope) {
		if(Scope.SCOPE_CLIENT==scope){
			return pc.getApplicationContext().isSetClientManagement();
		}
		if(Scope.SCOPE_SESSION==scope){
			return pc.getApplicationContext().isSetSessionManagement();
		}
		if(Scope.SCOPE_CALLER==scope){
			return pc.undefinedScope().get(KeyConstants._caller,null) instanceof Struct;
		}
		if(Scope.SCOPE_CLUSTER==scope){
			try {
				return !(pc.clusterScope() instanceof ClusterNotSupported);
			} catch (PageException e) {
				//e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public List getVariables() {
		Iterator it = getScopeNames().iterator();
		List list=new ArrayList();
		
		while(it.hasNext()){
			try {
				getVariables(this,pc,list, (String)it.next());
			} 
			catch (FDLanguageException e) {e.printStackTrace();}
		}
		return sort(list);
	}

	public static List testVariables(PageContextImpl pc) {
		return new FDStackFrameImpl(null,pc,null,null).getVariables();
	}
	

	@Override
	public List getVariables(String strScope) throws FDLanguageException {
		return sort(getVariables(this,pc,new ArrayList(), strScope));
	}
	

	public static List testVariables(PageContextImpl pc,String strScope) throws FDLanguageException {
		return new FDStackFrameImpl(null,pc,null,null).getVariables(strScope);
	}
	
	private static List getVariables(FDStackFrameImpl frame,PageContextImpl pc,List list,String strScope) throws FDLanguageException {
		Scope scope;
		try {
			scope = pc.scope(strScope, null);
			if(scope!=null) return copyValues(frame,list,scope);
			
			Object value=pc.undefinedScope().get(strScope,null);
			if(value!=null) {
				if(value instanceof Struct)return copyValues(frame,new ArrayList(),(Struct)value);
				throw new FDLanguageException("["+strScope+"] is not of type scope, type is ["+Caster.toTypeName(value)+"]");
			}
			throw new FDLanguageException("["+strScope+"] does not exist in the current context");
		} 
		catch (PageException e) {
			throw new FDLanguageException(e);
		}
	}

	/**
	 * copy all data from given struct to given list and translate it to a FDValue
	 * @param to list to fill with values
	 * @param from struct to read values from
	 * @return the given list
	 */
	private static List copyValues(FDStackFrameImpl frame,List to,Struct from) {
		Iterator it = from.entrySet().iterator();
		Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			to.add(new FDVariable(frame,(String)entry.getKey(),FDCaster.toFDValue(frame,entry.getValue())));
		}
		return to;
	}

	private static List sort(List list) {
		Collections.sort(list,comparator);
		return list;
	}

	@Override
	public String getFrameInformation() {
		return ps.getFullRealpath();
	}
	
	public String toString(){
		return "path:"+getSourceFilePath()+";name:"+getSourceFileName()+";unit-pack:"+getExecutionUnitPackage()+";unit-name:"+getExecutionUnitName()+";line:"+getLineNumber();
	}


}

class FDVariableComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		return ((FDVariable)o1).getName().compareToIgnoreCase(((FDVariable)o2).getName());
	}
}
