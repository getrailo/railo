package railo.runtime.dump;


/**
 * this class is to hold all information to a dumpwriter together in a single class, Dumpwriter, name and if it is a default.
 */
public class DumpWriterEntry {
	private String name;
	private DumpWriter writer;
	private int defaultType;
	
	public DumpWriterEntry(int defaultType, String name, DumpWriter writer) {
		//print.err(name+":"+defaultType);
		this.defaultType = defaultType;
		this.name = name;
		this.writer = writer;
	}

	/**
	 * @return the def
	 */
	public int getDefaultType() {
		return defaultType;
	}

	/**
	 * @param def the def to set
	 */
	public void setDefaultType(int defaultType) {
		this.defaultType = defaultType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the writer
	 */
	public DumpWriter getWriter() {
		return writer;
	}
	
}
