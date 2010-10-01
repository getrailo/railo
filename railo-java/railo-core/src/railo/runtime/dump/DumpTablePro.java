package railo.runtime.dump;

// FUTURE add to loader

public class DumpTablePro extends DumpTable {


	private String type;
	private String id;
	private String ref;

	public DumpTablePro(String type,String highLightColor, String normalColor,String borderColor) {
		super(highLightColor, normalColor, borderColor);
		this.type=type;
	}
	
	public DumpTablePro(String type,String highLightColor, String normalColor,String borderColor, String fontColor) {
		super(highLightColor, normalColor, borderColor, fontColor);
		this.type=type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id=id;
	}

	public String getId() {
		return id;
	}

	public void setRef(String ref) {
		this.ref=ref;
	}
	public String getRef() {
		return ref;
	}


}
