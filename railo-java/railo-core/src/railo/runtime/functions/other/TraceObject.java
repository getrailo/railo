package railo.runtime.functions.other;

import java.io.IOException;
import java.util.Iterator;

import railo.commons.io.log.LogResource;
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
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.trace.TraceObjectSupport;
import railo.runtime.util.VariableUtilImpl;

public class TraceObject {
	public synchronized static Object call(PageContext pc , Object obj,String label,String logFile) throws PageException {
		if(true) throw new ApplicationException("this function is not supported yet");
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
		return TraceObjectSupport.toTraceObject(obj,label,log);
	}

	

}
