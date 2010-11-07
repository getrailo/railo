package railo.runtime.cfx.customtag;

import java.lang.reflect.Method;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.runtime.cfx.CFXTagException;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

public class CPPCustomTag implements CustomTag {

	// this is loaded dynamic, because the lib is optional
	private static Method processRequest;
	
	private boolean keepAlive;
	private String procedure;
	private String serverLibrary;

	public CPPCustomTag(String serverLibrary, String procedure, boolean keepAlive) throws CFXTagException{
		this.serverLibrary=serverLibrary;
		this.procedure=procedure;
		this.keepAlive=keepAlive;
	}
	
	public void processRequest(Request request, Response response) throws Exception {
		if(processRequest==null){
			Class clazz = null;
			try {
				clazz = ClassUtil.loadClass("org.openbd.extension.cfx.CFXNativeLib");
			} catch (ClassException e) {
				throw new CFXTagException("C++ Custom tag library is missing, get the newest jars-zip from getrailo.org download");
			}
			processRequest=clazz.getMethod("processRequest", new Class[]{String.class,String.class,Request.class,Response.class,boolean.class});
		}
		processRequest.invoke(null, new Object[]{serverLibrary, procedure, request, response, keepAlive});
		//CFXNativeLib.processRequest(serverLibrary, procedure, request, response, keepAlive);
	} 

}
