package railo.runtime.thread;

import java.io.Serializable;

import javax.servlet.http.Cookie;

public class SerializableCookie implements Serializable {

	private String comment;
	private String domain;
	private int maxAge;
	private String name;
	private String path;
	private boolean secure;
	private String value;
	private int version;


	public SerializableCookie(String comment, String domain, int maxAge, String name, String path, boolean secure, String value, int version) {
		this.comment = comment;
		this.domain = domain;
		this.maxAge = maxAge;
		this.name = name;
		this.path = path;
		this.secure = secure;
		this.value = value;
		this.version = version;
	}
	
	public SerializableCookie(Cookie cookie) {
		this.comment = cookie.getComment();
		this.domain = cookie.getDomain();
		this.maxAge = cookie.getMaxAge();
		this.name = cookie.getName();
		this.path = cookie.getPath();
		this.secure = cookie.getSecure();
		this.value = cookie.getValue();
		this.version = cookie.getVersion();
	}

	/**
	 * @see javax.servlet.http.Cookie#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see javax.servlet.http.Cookie#getDomain()
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @see javax.servlet.http.Cookie#getMaxAge()
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * @see javax.servlet.http.Cookie#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see javax.servlet.http.Cookie#getPath()
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @see javax.servlet.http.Cookie#getSecure()
	 */
	public boolean getSecure() {
		return secure;
	}

	/**
	 * @see javax.servlet.http.Cookie#getValue()
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @see javax.servlet.http.Cookie#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @see javax.servlet.http.Cookie#setComment(java.lang.String)
	 */
	public void setComment(String purpose) {
		this.comment=purpose;
	}

	/**
	 * @see javax.servlet.http.Cookie#setDomain(java.lang.String)
	 */
	public void setDomain(String pattern) {
		this.domain=pattern;
	}

	/**
	 * @see javax.servlet.http.Cookie#setMaxAge(int)
	 */
	public void setMaxAge(int expiry) {
		this.maxAge=expiry;
	}

	/**
	 * @see javax.servlet.http.Cookie#setPath(java.lang.String)
	 */
	public void setPath(String uri) {
		this.path=uri;
	}

	/**
	 * @see javax.servlet.http.Cookie#setSecure(boolean)
	 */
	public void setSecure(boolean secure) {
		this.secure=secure;
	}

	/**
	 * @see javax.servlet.http.Cookie#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value=value;
	}

	/**
	 * @see javax.servlet.http.Cookie#setVersion(int)
	 */
	public void setVersion(int version) {
		this.version=version;
	}

	
	public Cookie toCookie() {
		Cookie c = new Cookie(name,value);
		if(comment!=null)c.setComment(comment);
		if(domain!=null)c.setDomain(domain);
		c.setMaxAge(maxAge);
		if(path!=null)c.setPath(path);
		c.setSecure(secure);
		c.setVersion(version);
		return c;
	}

	public static Cookie[] toCookies(SerializableCookie[] src) {
		Cookie[] dest=new Cookie[src.length];
		for(int i=0;i<src.length;i++) {
			dest[i]=src[i].toCookie();
		}
		return dest;
	}
	
	public static SerializableCookie[] toSerializableCookie(Cookie[] src) {
		SerializableCookie[] dest=new SerializableCookie[src.length];
		for(int i=0;i<src.length;i++) {
			dest[i]=new SerializableCookie(src[i]);
		}
		return dest;
	}
}
