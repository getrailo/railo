package railo.transformer.bytecode;

import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.transformer.Context;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.visitor.OnFinally;

public class BytecodeContext implements Context {
	

	private ClassWriter classWriter;
	private GeneratorAdapter adapter;
	private String className;
	private List<LitString> keys;
	private int count=0;
	private Method method;
	private boolean doSubFunctions=true;
	private BytecodeContext staticConstr;
	private BytecodeContext constr;
	private final boolean supressWSbeforeArg;
	
	private static long _id=0;
	private synchronized static String id() {
		if(_id<0)_id=0;
		return StringUtil.addZeros(++_id,4);
	}
	
	private String id=id();
	private Page page;
	private PageSource source;

	public BytecodeContext(PageSource source,BytecodeContext statConstr,BytecodeContext constr,Page page,List<LitString> keys,ClassWriter classWriter,String className, GeneratorAdapter adapter,
			Method method,boolean writeLog, boolean supressWSbeforeArg) {
		this.classWriter = classWriter;
		this.className = className;
		this.writeLog = writeLog;
		this.adapter = adapter;
		this.keys = keys;
		this.method=method;
		this.staticConstr=statConstr;
		this.constr=constr;
		this.page=page;
		this.supressWSbeforeArg=supressWSbeforeArg;
		if(source!=null)this.source=source;
		else if(constr!=null)this.source=constr.source;
		else if(statConstr!=null)this.source=statConstr.source;
	}
	
	public BytecodeContext(BytecodeContext statConstr,BytecodeContext constr,List<LitString> keys,BytecodeContext bc, GeneratorAdapter adapter,Method method) {
		this.classWriter = bc.getClassWriter();
		this.className = bc.getClassName();
		this.writeLog = bc.writeLog();
		
		this.adapter = adapter;
		this.keys = keys;
		this.method=method;
		this.staticConstr=statConstr;
		this.constr=constr;
		this.page=bc.getPage();
		this.supressWSbeforeArg=bc.supressWSbeforeArg;
		this.source=bc.source;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public int incCount() {
		return ++this.count;
	}
	public void resetCount() {
		this.count=0;
	}
	/**
	 * @return the adapter
	 */
	public GeneratorAdapter getAdapter() {
		return adapter;
	}
	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(BytecodeContext bc) {
		this.adapter = bc.getAdapter();
	}
	/**
	 * @return the classWriter
	 */
	public ClassWriter getClassWriter() {
		return classWriter;
	}
	/**
	 * @param classWriter the classWriter to set
	 */
	public void setClassWriter(ClassWriter classWriter) {
		this.classWriter = classWriter;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	public synchronized int registerKey(LitString lit)  {
		int index = keys.indexOf(lit);
		if(index!=-1)return index;// calls the toString method of litString
		
		keys.add(lit);
		
		return keys.size()-1;
	}

	public List<LitString> getKeys() {
		return keys;
	}


	Stack<OnFinally> tcf=new Stack<OnFinally>();
	private int currentTag;
	private int line;
	private BytecodeContext root;
	private boolean writeLog;
	//private static BytecodeContext staticConstr;
	
	public void pushOnFinally(OnFinally onFinally) {
		tcf.push(onFinally);
	}
	public void popOnFinally() {
		tcf.pop();
	}
	
	/*public void pushTryCatchFinallyData(TryCatchFinallyData data) {
		tcf.push(data);
	}
	public void popTryCatchFinallyData() {
		tcf.pop();
	}*/
	
	public Stack<OnFinally> getOnFinallyStack() {
		return tcf;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @return the doSubFunctions
	 */
	public boolean doSubFunctions() {
		return doSubFunctions;
	}

	/**
	 * @param doSubFunctions the doSubFunctions to set
	 * @return 
	 */
	public boolean changeDoSubFunctions(boolean doSubFunctions) {
		boolean old=this.doSubFunctions;
		this.doSubFunctions = doSubFunctions;
		return old;
	}

	/**
	 * @return the currentTag
	 */
	public int getCurrentTag() {
		return currentTag;
	}

	/**
	 * @param currentTag the currentTag to set
	 */
	public void setCurrentTag(int currentTag) {
		this.currentTag = currentTag;
	}

	public BytecodeContext getStaticConstructor() {
		return staticConstr;
	}
	public BytecodeContext getConstructor() {
		return constr;
	}

	public void visitLineNumber(int line) {
		this.line=line;
		getAdapter().visitLineNumber(line,getAdapter().mark());
	}

	public int getLine() {
		return line;
	}

	public BytecodeContext getRoot() {
		return root;
	}
	public void setRoot(BytecodeContext root) {
		this.root= root;
	}

	public boolean writeLog() {
		return this.writeLog;
	}

	public Page getPage() {
		return page;
	}
	
	public boolean getSupressWSbeforeArg(){
		return supressWSbeforeArg;
	}

	public PageSource getPageSource() {
		return source;
	}
}
