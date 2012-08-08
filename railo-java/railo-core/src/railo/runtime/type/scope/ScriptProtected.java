package railo.runtime.type.scope;

import railo.runtime.listener.ApplicationContext;

public interface ScriptProtected {

	public static final int UNDEFINED=0;
	public static final int YES=1;
	public static final int NO=2;
	
	
	/**
     * @return returns if the values of the scope are already protected against cross site scripting
     */
    public boolean isScriptProtected();

    /**
     * transform the string values of the scope do a script protecting way
     */
    public void setScriptProtecting(ApplicationContext ac,boolean scriptProtecting);
    
}
