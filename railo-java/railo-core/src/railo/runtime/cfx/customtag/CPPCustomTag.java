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
		if(processRequest==null){
			Class clazz = null;
			try {
				clazz = ClassUtil.loadClass("com.naryx.tagfusion.cfx.CFXNativeLib");
			} catch (ClassException e) {
				

				throw new CFXTagException(
					"cannot initialize C++ Custom tag library, make sure you have added all the required jar files. "+
					"GO to the Railo Server Administrator and on the page Services/Update, click on \"Update JARs\"");
				
			}
			try {
				processRequest=clazz.getMethod("processRequest", new Class[]{String.class,String.class,Request.class,Response.class,boolean.class});
			} catch (NoSuchMethodException e) {
				throw new CFXTagException(e);
			}
		}
	}
	
	public void processRequest(Request request, Response response) throws Exception {
		
		processRequest.invoke(null, new Object[]{serverLibrary, procedure, request, response, keepAlive});
		//CFXNativeLib.processRequest(serverLibrary, procedure, request, response, keepAlive);
	} 

}
