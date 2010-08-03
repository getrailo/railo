/*
 * Created on Jan 20, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package railo.runtime.cfx.customtag;

import railo.runtime.cfx.CFXTagException;

import com.allaire.cfx.CustomTag;

/**
 *
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class CPPCFXTagClass implements CFXTagClass {
	
	private String name;
	private boolean readonly=false;
    private String serverLibrary;
    private String procedure;
    private boolean keepAlive;

    /**
     * @param name
     * @param readonly
     * @param serverLibrary
     * @param procedure
     * @param keepAlive
     */
    private CPPCFXTagClass(String name, boolean readonly, String serverLibrary,
            String procedure, boolean keepAlive) {
        super();
        this.name = name;
        this.readonly = readonly;
        this.serverLibrary = serverLibrary;
        this.procedure = procedure;
        this.keepAlive = keepAlive;
    }
    
	public CPPCFXTagClass(String name, String serverLibrary, String procedure, boolean keepAlive) {
		if(name.startsWith("cfx_"))name=name.substring(4);
		this.name=name;
		this.serverLibrary=serverLibrary;
		this.procedure=procedure;
		this.keepAlive=keepAlive;
	}
	
	/**
	 * @see railo.runtime.cfx.customtag.CFXTagClass#newInstance()
	 */
	public CustomTag newInstance() throws CFXTagException {
		return new CPPCustomTag(serverLibrary,procedure,keepAlive);
		
	}

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#isReadOnly()
     */
    public boolean isReadOnly() {
        return readonly;
    }

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#cloneReadOnly()
     */
    public CFXTagClass cloneReadOnly() {
        return new CPPCFXTagClass(name,true,serverLibrary,procedure,keepAlive);
    }

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#getDisplayType()
     */
    public String getDisplayType() {
        return "COM";
    }

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#getSourceName()
     */
    public String getSourceName() {
        return serverLibrary;
    }

    /**
     * @see railo.runtime.cfx.customtag.CFXTagClass#isValid()
     */
    public boolean isValid() {
        return false;
    }
}