package railo.runtime.search;

public class AddionalAttrs {

	private static ThreadLocal addAttrs=new ThreadLocal();
	private int contextBytes;
	private String contextHighlightBegin;
	private int contextPassages;
	private String contextHighlightEnd;
	
	public AddionalAttrs(int contextBytes, int contextPassages,String contextHighlightBegin, String contextHighlightEnd) {
		this.contextBytes=contextBytes;
		this.contextPassages=contextPassages;
		this.contextHighlightBegin=contextHighlightBegin;
		this.contextHighlightEnd=contextHighlightEnd;
	}
	
	public static AddionalAttrs getAddionlAttrs(){
		AddionalAttrs aa = (AddionalAttrs) addAttrs.get();
		if(aa==null)aa=new AddionalAttrs(300,0,"<b>","</b>");
		return aa;
	}
	public static void setAddionalAttrs(AddionalAttrs aa){
		addAttrs.set(aa);
	}
	
	public static void setAddionalAttrs(int contextBytes, int contextPassages, String contextHighlightBegin, String contextHighlightEnd) {
		setAddionalAttrs(new AddionalAttrs(contextBytes,contextPassages,contextHighlightBegin,contextHighlightEnd));
	}

	public static void removeAddionalAttrs(){
		addAttrs.set(null);
	}
	

	/**
	 * @return the contextBytes
	 */
	public int getContextBytes() {
		return contextBytes;
	}

	/**
	 * @return the contextHighlightBegin
	 */
	public String getContextHighlightBegin() {
		return contextHighlightBegin;
	}

	/**
	 * @return the contextPassages
	 */
	public int getContextPassages() {
		return contextPassages;
	}

	/**
	 * @return the contextHighlightEnd
	 */
	public String getContextHighlightEnd() {
		return contextHighlightEnd;
	}
	
}
