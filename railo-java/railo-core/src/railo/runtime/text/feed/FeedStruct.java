package railo.runtime.text.feed;

import railo.runtime.type.Collection;
import railo.runtime.type.StructImpl;

public class FeedStruct extends StructImpl {

	private boolean hasAttribute;
	private String path;
	private Key inside;

	private StringBuilder content;
	private String uri;
	

	public FeedStruct(String path, Key inside, String uri) {
		this.path=path;
		this.inside=inside;
		this.uri=uri;
	}

	public FeedStruct() {
	}
	

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param hasAttribute the hasAttribute to set
	 */
	public void setHasAttribute(boolean hasAttribute) {
		this.hasAttribute = hasAttribute;
	}

	public boolean hasAttribute() {
		return hasAttribute || !isEmpty();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the inside
	 */
	public Key getInside() {
		return inside;
	}
	
	public void append(String str) {
		if(content==null) content=new StringBuilder();
		content.append(str);
	}
	
	public String getString() {
		if(content==null) return"";
		return content.toString();
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
		FeedStruct trg=new FeedStruct(path,inside,uri);
		trg.hasAttribute=hasAttribute;
		copy(this, trg, deepCopy);
		return trg;
	}

}
