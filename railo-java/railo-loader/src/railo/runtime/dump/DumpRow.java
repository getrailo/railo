package railo.runtime.dump;

public class DumpRow {

	private int highlightType;
	private DumpData[] items;

	/**
     * constructor of the class
     * @param highlightType binary Values define wich columns are higlithed
     * @param items items as DumpData Array
     */
    public DumpRow(int highlightType,DumpData[] items) {
    	this.highlightType=highlightType;
    	this.items=items;
    }
    
    /**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1) {
		this(highlightType,new DumpData[]{item1});
	}

    /**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 * @param item2 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1, DumpData item2) {
		this(highlightType,new DumpData[]{item1,item2});
	}

	/**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 * @param item2 item for the array
	 * @param item3 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1, DumpData item2, DumpData item3) {
		this(highlightType,new DumpData[]{item1,item2,item3});
	}

	/**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 * @param item2 item for the array
	 * @param item3 item for the array
	 * @param item4 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4) {
		this(highlightType,new DumpData[]{item1,item2,item3,item4});
	}

	/**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 * @param item2 item for the array
	 * @param item3 item for the array
	 * @param item4 item for the array
	 * @param item5 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4, DumpData item5) {
		this(highlightType,new DumpData[]{item1,item2,item3,item4,item5});
	}

	/**
	 * Constructor of the class
	 * @param highlightType binary Values define wich columns are higlithed
     * @param item1 item for the array
	 * @param item2 item for the array
	 * @param item3 item for the array
	 * @param item4 item for the array
	 * @param item5 item for the array
	 * @param item6 item for the array
	 */
	public DumpRow(int highlightType, DumpData item1, DumpData item2, DumpData item3,DumpData item4, DumpData item5, DumpData item6) {
		this(highlightType,new DumpData[]{item1,item2,item3,item4,item5,item6});
	}

	/**
	 * @return the highlightType
	 */
	public int getHighlightType() {
		return highlightType;
	}

	/**
	 * @return the items
	 */
	public DumpData[] getItems() {
		return items;
	}
}
