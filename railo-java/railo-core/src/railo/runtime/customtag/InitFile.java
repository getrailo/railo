package railo.runtime.customtag;

import railo.runtime.PageSource;

public class InitFile {

	private PageSource ps;
	private String filename;
	private boolean isCFC;

	public InitFile(PageSource ps,String filename,boolean isCFC){
		this.ps=ps;
		this.filename=filename;
		this.isCFC=isCFC;
	}
	
	public PageSource getPageSource() {
		return ps;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isCFC() {
		return isCFC;
	}
}