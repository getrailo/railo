package railo.runtime.cfx.customtag;

//xxximport org.openbd.extension.cfx.CFXNativeLib;

import railo.runtime.cfx.CFXTagException;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

public class CPPCustomTag implements CustomTag {

	private boolean keepAlive;
	private String procedure;
	private String serverLibrary;

	public CPPCustomTag(String serverLibrary, String procedure, boolean keepAlive) throws CFXTagException{
		this.serverLibrary=serverLibrary;
		this.procedure=procedure;
		this.keepAlive=keepAlive;
		try{
			// invoked to make sure jar is available
			//xxxnew CFXNativeLib();
		}
		catch(Throwable t){
			throw new CFXTagException("C++ Custom tag library is missing, get the newest jars-zip from getrailo.org download");
		}
	}
	
	public void processRequest(Request request, Response response)throws Exception {
		//xxxCFXNativeLib.processRequest(serverLibrary, procedure, request, response, keepAlive);
	}

}
