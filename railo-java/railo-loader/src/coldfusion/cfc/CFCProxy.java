/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package coldfusion.cfc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.loader.engine.CFMLEngine;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;


public class CFCProxy {
	
	private CFMLEngine engine;
	private Cast caster;
	private Creation creator;
	
	private Component cfc=null;
    private String path;
	private Map thisData;
	private boolean invokeDirectly=true;
	private boolean autoFlush;

	public CFCProxy(String path) throws Throwable {
		this(path, null, true);
    }

    public CFCProxy(String path, boolean invokeDirectly) throws Throwable {
		this(path, null, invokeDirectly);
    }

    public CFCProxy(String path, Map initialThis) throws Throwable {
        this(path, initialThis, true);
    }

    public CFCProxy(String path, Map initialThis, boolean invokeDirectly) throws Throwable {
    	engine = CFMLEngineFactory.getInstance();
    	caster = engine.getCastUtil();
    	creator = engine.getCreationUtil();
        
    	this.path=path;
		this.invokeDirectly=invokeDirectly;
		setThisScope(initialThis);
    }

    private void initCFC(PageContext pc) {
    	if(cfc==null && (invokeDirectly || pc!=null)) {
			try {
				if(pc==null)pc=engine.getThreadPageContext();
				cfc=engine.getCreationUtil().createComponentFromPath(pc, path);
			} catch (PageException pe) {}
    	}
	}
	
    public void setThisScope(Map data) {
		if(data!=null) {
			if(thisData==null)this.thisData=new HashMap();
			
			Iterator<Entry> it = data.entrySet().iterator();
	        Entry entry;
	        while(it.hasNext()){
	        	entry = it.next();
	        	thisData.put(entry.getKey(), entry.getValue());
	        }
		}
    }

    public Map getThisScope() {
    	initCFC(null);
    	if(cfc==null)return null;
    	
    	Struct rtn=creator.createStruct();
        Iterator<Entry<Key, Object>> it = cfc.entryIterator();
        Entry<Key, Object> entry;
        while(it.hasNext()){
        	entry = it.next();
        	rtn.setEL(entry.getKey(), entry.getValue());
        }
    	return rtn;
    }
    
    public final Object invoke(String methodName, Object args[]) throws Throwable {
        if(invokeDirectly) return _invoke(methodName, args);
        return _invoke(methodName, args, null, null, null);
    }

	public final Object invoke(String methodName, Object args[], HttpServletRequest request, HttpServletResponse response) throws Throwable {
		if(invokeDirectly) return _invoke(methodName, args);
        return _invoke(methodName, args, request, response, null);
    }
	
    public final Object invoke(String methodName, Object args[], HttpServletRequest request, HttpServletResponse response, OutputStream out) throws Throwable {
    	if(invokeDirectly) return _invoke(methodName, args);
        return _invoke(methodName, args, request, response, out);
    }

    public static boolean inInvoke() {
        return false;
    }

    private Object _invoke(String methodName, Object[] args) throws PageException {
    	CFMLEngine engine = CFMLEngineFactory.getInstance();
		PageContext pc = engine.getThreadPageContext();
		initCFC(pc);
    	return cfc.call(pc, methodName, args);
	}
    
    private Object _invoke(String methodName, Object[] args, HttpServletRequest req, HttpServletResponse rsp, OutputStream out) throws PageException {
    	CFMLEngine engine = CFMLEngineFactory.getInstance();
		Creation creator = engine.getCreationUtil();
		PageContext originalPC = engine.getThreadPageContext();
		
		// no OutputStream
		if(out==null)out=DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
		
		// no Request
		if(req==null){
			// TODO new File
			req=creator.createHttpServletRequest(new File("."), "Railo", "/", "", null, null, null, null, null);
		}
		// noRespone
		if(rsp==null){
			rsp=creator.createHttpServletResponse(out);
		}
		
		
		PageContext pc = creator.createPageContext(req,rsp,out);
		try{
			engine.registerThreadPageContext(pc);
			initCFC(pc);
	    	return cfc.call(pc, methodName, args);
		}
		finally{
			if(autoFlush) {
				try {
					pc.getRootWriter().flush();
				} catch (Throwable t) {}
			}
			engine.registerThreadPageContext(originalPC);
		}
	}

    public void flush() throws IOException {
    	CFMLEngine engine = CFMLEngineFactory.getInstance();
		PageContext pc = engine.getThreadPageContext();
		pc.getRootWriter().flush();
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    public void setApplicationExecution(boolean doApp)
    {
        //executeApplication = doApp;
    }

}

final class DevNullOutputStream extends OutputStream implements Serializable {
	
	public static final DevNullOutputStream DEV_NULL_OUTPUT_STREAM=new DevNullOutputStream();
	
	/**
	 * Constructor of the class
	 */
	private DevNullOutputStream() {}
	
    /**
     * @see java.io.OutputStream#close()
     */
    public void close(){}

    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() {}

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) {}

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) {}

    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) {}

}
