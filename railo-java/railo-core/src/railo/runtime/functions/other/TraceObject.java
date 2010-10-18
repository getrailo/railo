package railo.runtime.functions.other;

import java.io.IOException;
import java.util.Iterator;

import railo.print;
import railo.commons.io.log.LogResource;
import railo.commons.io.log.LogUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.functions.system.GetCurrentContext;
import railo.runtime.java.JavaObject;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.tag.Log;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.util.VariableUtil;
import railo.runtime.util.VariableUtilImpl;

public class TraceObject implements Objects, Collection,UDF {
	public synchronized static Object call(PageContext pc , Object obj,String label,String logFile) throws PageException {
		
		// Resource
		Resource res = ResourceUtil.toResourceNotExisting(pc.getConfig(), logFile);
		if(!res.exists()){
			try {
				res.createFile(true);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}			
		}
		else if(res.isDirectory()) throw new FunctionException(pc,"tracePoint",3,"logFile","can't create file ["+res.getPath()+"], resource already exists as a directory");
		
		// Log level
		/*int level=LogUtil.toIntType(logLevel, -1);
		if(level==-1)
			throw new FunctionException(pc,"tracePoint",2,"logLevel","valid values are [information,warning,error,fatal,debug]");
    	*/
		int level=railo.commons.io.log.Log.LEVEL_INFO;
		
		// Log
		LogResource log=null;
		try {
			log = new LogResource(res, level, "UTF-8");
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return new TraceObject(obj,label,log);
	}

	
	

	private Object o;
	private Collection coll;
	private String label;
	private LogResource log;
	private UDF udf;
	
	private TraceObject(Object obj, String label, LogResource log) {
		this.o=obj;
		this.label=label;
		this.log=log;
	}
	

	private Collection coll() throws PageRuntimeException {
		if(coll==null) {
			try {
				coll=Caster.toCollection(o);
			} catch (PageException e) {
				throw new PageRuntimeException(e);
			}
		}
		return coll;
	}
	
	private UDF udf() throws PageRuntimeException {
		if(udf==null) {
			try {
				udf=Caster.toUDF(o);
			} catch (PageException e) {
				throw new PageRuntimeException(e);
			}
		}
		return udf;
	}

	private PageContext pc() {
		return ThreadLocalPageContext.get();
	}

	private void log(String addional) {
		
		Throwable t=new Exception("Stack trace");
		Throwable cause = t.getCause(); 
		while(cause!=null){
			t=cause;
			cause = t.getCause(); 
		}
		StackTraceElement[] traces = t.getStackTrace();
		
        int line=0;
		String template=null;
		StackTraceElement trace=null;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template).equals("java") ||
					template.endsWith("Dump.cfc"))// MUST bad impl 
				continue;
			line=trace.getLineNumber();
			break;
		}
		//print.e(t);
		if(line==0) return;
		
		String type=traces[1].getMethodName();
		
		log.info(label, type(type)+(StringUtil.isEmpty(addional)?"":" ["+addional+"]")+" at "+template+":"+line);
	}	
	
	private static String type(String type) {
		if(type.equals("setEL")) return "set";
		if(type.equals("removeEL")) return "remove";
		if(type.equals("keys")) return "list";
		return type;
	}


	public Object get(PageContext pc, Key key) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key);
	}
	
	public Object get(PageContext pc, Key key, Object defaultValue) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key, defaultValue);
	}
	
	public Object set(PageContext pc, Key key, Object value) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.set(pc, o, key, value);
	}
	
	public Object setEL(PageContext pc, Key key, Object value) {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.setEL(pc, o, key, value);
	}

	public Object call(PageContext pc, Key key, Object[] args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithoutNamedValues(pc, o, key, args);
	}
	
	public Object callWithNamedValues(PageContext pc, Key key, Struct args) throws PageException {
		log(key.getString());
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.callFunctionWithNamedValues(pc, o, key, args);
	}
	
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties props) {
		log(null);
		return DumpUtil.toDumpData(o, pageContext, maxlevel, props);
	}
	
	public boolean isInitalized() {
		return true;
	}
	
	public String castToString() throws PageException {
		log(null);
		return Caster.toString(o);
	}
	
	public String castToString(String defaultValue) {
		log(null);
		return Caster.toString(o,defaultValue);
	}
	
	public boolean castToBooleanValue() throws PageException {
		log(null);
		return Caster.toBooleanValue(o);
	}
	
	public Boolean castToBoolean(Boolean defaultValue) {
		log(null);
		return Caster.toBoolean(o,defaultValue);
	}
	
	public double castToDoubleValue() throws PageException {
		log(null);
		return Caster.toDoubleValue(o);
	}
	
	public double castToDoubleValue(double defaultValue) {
		log(null);
		return Caster.toDoubleValue(o,defaultValue);
	}
	
	public DateTime castToDateTime() throws PageException {
		log(null);
		return Caster.toDate(o, false,null);
	}
	
	public DateTime castToDateTime(DateTime defaultValue) {
		log(null);
		return Caster.toDate(o, false,null,defaultValue);
	}
	
	public int compareTo(boolean b) throws PageException {
		log(null);
		return Operator.compare(o, b);
	}
	
	public int compareTo(DateTime dt) throws PageException {
		log(null);
		return Operator.compare(o, (Object)dt);
	}

	public int compareTo(double d) throws PageException {
		log(null);
		return Operator.compare(o, d);
	}
	
	public int compareTo(String str) throws PageException {
		log(null);
		return Operator.compare(o, str);
	}



	public Iterator keyIterator() {
		log(null);
		return coll().keyIterator();
	}



	public Iterator valueIterator() {
		log(null);
		return coll().valueIterator();
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		log(null);
		return coll().iterator();
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		log(null);
		return coll().size();
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		log(null);
		return coll().keys();
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		log(null);
		return coll().keysAsString();
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.remove(o, key);
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.removeEL(o, key);
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		log(null);
		coll().clear();
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key);
	}





	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o, key, defaultValue);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.set(pc, o, key, value);
	}




	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.setEL(pc, o, key, value);
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		log(null);
		Object c=Duplicator.duplicate(o,deepCopy);
		return new TraceObject(c,"Copy of "+label, log);
	}



	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		log(key.getString());
		PageContext pc = pc();
		VariableUtilImpl var = (VariableUtilImpl) pc.getVariableUtil();
		return var.get(pc, o,key, null)!=null;
	}
	
/////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return duplicate(true);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return containsKey(KeyImpl.init(key));
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return setEL(KeyImpl.init(key), value); 
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return set(KeyImpl.init(key),value);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return get(KeyImpl.init(key),defaultValue);
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String key, Struct args) throws PageException {
		return callWithNamedValues(pc, KeyImpl.init(key), args);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String key, Object[] arguments) throws PageException {
		return call(pc, KeyImpl.init(key), arguments);
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return setEL(pc, KeyImpl.init(propertyName), value);
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		return set(pc, KeyImpl.init(propertyName), value);
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String propertyName, Object defaultValue) {
		return get(pc, KeyImpl.init(propertyName),defaultValue);
	}
	
	/* (non-Javadoc)
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String propertyName) throws PageException {
		return get(pc, KeyImpl.init(propertyName));
	}


	public int getAccess() {
		log(null);
		return udf().getAccess();
	}


	public Object getValue() {
		log(null);
		return udf().getValue();
	}


	public Object implementation(PageContext pageContext) throws Throwable {
		log(null);
		return udf().implementation(pageContext);
	}


	public FunctionArgument[] getFunctionArguments() {
		log(null);
		return udf().getFunctionArguments();
	}


	public Object getDefaultValue(PageContext pc, int index)
			throws PageException {
		log(null);
		return udf().getDefaultValue(pc, index);
	}


	public String getFunctionName() {
		log(null);
		return udf().getFunctionName();
	}


	public boolean getOutput() {
		log(null);
		return udf().getOutput();
	}


	public int getReturnType() {
		log(null);
		return udf().getReturnType();
	}


	public int getReturnFormat() {
		log(null);
		return udf().getReturnFormat();
	}


	public Boolean getSecureJson() {
		log(null);
		return udf().getSecureJson();
	}


	public Boolean getVerifyClient() {
		log(null);
		return udf().getVerifyClient();
	}


	public String getReturnTypeAsString() {
		log(null);
		return udf().getReturnTypeAsString();
	}


	public String getDescription() {
		log(null);
		return udf().getDescription();
	}


	public Object callWithNamedValues(PageContext pageContext, Struct values,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf().callWithNamedValues(pageContext, values, doIncludePath);
	}


	public Object call(PageContext pageContext, Object[] args,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf().call(pageContext, args, doIncludePath);
	}


	public String getDisplayName() {
		log(null);
		return udf().getDisplayName();
	}


	public String getHint() {
		log(null);
		return udf().getHint();
	}


	public Page getPage() {
		log(null);
		return udf().getPage();
	}


	public Struct getMetaData(PageContext pc) throws PageException {
		log(null);
		return udf().getMetaData(pc);
	}


	public UDF duplicate() {
		log(null);
		return udf().duplicate();
	}


	public Component getOwnerComponent() {
		log(null);
		return udf().getOwnerComponent();
	}
}
