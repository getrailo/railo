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
	 * @return the serverLibrary
	 */
	public String getServerLibrary() {
		return serverLibrary;
	}

	/**
	 * @return the procedure
	 */
	public String getProcedure() {
		return procedure;
	}

	@Override
	public CustomTag newInstance() throws CFXTagException {
		return new CPPCustomTag(serverLibrary,procedure,keepAlive);
		
	}

    @Override
    public boolean isReadOnly() {
        return readonly;
    }

    @Override
    public CFXTagClass cloneReadOnly() {
        return new CPPCFXTagClass(name,true,serverLibrary,procedure,keepAlive);
    }

    @Override
    public String getDisplayType() {
        return "cpp";
    }

    @Override
    public String getSourceName() {
        return serverLibrary;
    }

    @Override
    public boolean isValid() {
        return false;
    }
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the keepAlive
	 */
	public boolean getKeepAlive() {
		return keepAlive;
	}
	
}