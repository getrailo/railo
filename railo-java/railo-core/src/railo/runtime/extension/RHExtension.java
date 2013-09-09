package railo.runtime.extension;


/**
 * Extension completely handled by Railo and not by the Install.cfc/config.xml 
 */
public class RHExtension {

	private String id;
	private String name;
	private String version;
	private String[] jars;
	private String[] flds;
	private String[] tlds;
	private String[] contexts;
	private String[] applications;

	public RHExtension(String id, String name, String version, String[] jars, String[] flds, String[] tlds, String[] contexts, String[] applications) {
		this.id=id;
		this.name=name;
		this.version=version;
		this.jars=jars;
		this.flds=flds;
		this.tlds=tlds;
		this.contexts=contexts;
		this.applications=applications;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String[] getJars() {
		return jars;
	}

	public String[] getFlds() {
		return flds;
	}

	public String[] getTlds() {
		return tlds;
	}

	public String[] getContexts() {
		return contexts;
	}

	public String[] getApplications() {
		return applications;
	}

}
