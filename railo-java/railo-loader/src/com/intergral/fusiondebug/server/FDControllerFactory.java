package com.intergral.fusiondebug.server;

import railo.loader.engine.CFMLEngineFactory;


/**
 * 
 */
public class FDControllerFactory {
	
	public static long complete = 0;
	
	// make sure FD see this class
	static {
		try{
			Class.forName( "com.intergral.fusiondebug.server.FDSignalException" );
		}
		catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	
	/**
	 * Constructor of the class
	 * should never be invoked but still public to be shure that we do not run into problems
	 */
	public FDControllerFactory(){}

	public static void notifyPageComplete()	{
		complete++;
	} 
	  
	/**
	 * returns a singelton instance of the class
	 * @return singelton instance
	 */
	public static Object getInstance(){
		return CFMLEngineFactory.getInstance().getFDController();
	}
	
	/**
	 * makes the class visible for the FD Client
	 */
	public static void makeVisible() {
		// this method does nothing, only make this class visible for the FD Client
	}

	


}
