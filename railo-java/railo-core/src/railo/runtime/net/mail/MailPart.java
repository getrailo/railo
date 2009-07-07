package railo.runtime.net.mail;

import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.List;


/**
 * 
 */
public final class MailPart {
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "railo.runtime.mail.MailPart(wraptext:"+wraptext+";isHTML:"+isHTML+";charset:"+charset+";body:"+body+";)";
    }
	/** IThe MIME media type of the part */
	private boolean isHTML;
	
	/** Specifies the maximum line length, in characters of the mail text */
	private int wraptext=-1;

	/** The character encoding in which the part text is encoded */
	private String charset;

    private String body;

    /**
     * 
     */
    public void clear() {
        isHTML=false;
        wraptext=-1;
        charset=null;
        body="null";
    }	
    
    

    /**
     * 
     */
    public MailPart() {
    }

    /**
     * @param charset
     */
    public MailPart(String charset) {
        this.charset = charset;
    }
    /**
     * @return Returns the body.
     */
    public String getBody() {
        return wrap(body);
    }
    /**
     * @param body The body to set.
     */
    public void setBody(String body) {
        this.body = body;
    }
    /**
     * @return Returns the charset.
     */
    public String getCharset() {
        return charset;
    }
    /**
     * @param charset The charset to set.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
    /**
     * @return Returns the isHTML.
     */
    public boolean isHTML() {
        return isHTML;
    }
    /**
     * @param isHTML The type to set.
     */
    public void isHTML(boolean isHTML) {
        this.isHTML = isHTML;
    }
    /**
     * @return Returns the wraptext.
     */
    public int getWraptext() {
        return wraptext;
    }
    /**
     * @param wraptext The wraptext to set.
     */
    public void setWraptext(int wraptext) {
        this.wraptext = wraptext;
    }


	/**
	 * wraps a String to specified length
	 * @param str string to erap
	 * @return wraped String
	 */
	private String wrap(String str) {
		if(body==null || wraptext<=0)return str;
		
		StringBuffer rtn=new StringBuffer();
		String ls=System.getProperty("line.separator");
		Array arr = List.listToArray(str,ls);
		int len=arr.size();
		
		for(int i=1;i<=len;i++) {
			rtn.append(wrapLine(Caster.toString(arr.get(i,""),"")));
			if(i+1<len)rtn.append(ls);
		}
		return rtn.toString();
	}

	/**
	 * wrap a single line
	 * @param str
	 * @return wraped Line
	 */
	private String wrapLine(String str) {
		int wtl=wraptext;
		
		if(str.length()<=wtl) return str;
		
		String sub=str.substring(0,wtl);
		String rest=str.substring(wtl);
		char firstR=rest.charAt(0);
		String ls=System.getProperty("line.separator");
		
		if(firstR==' ' || firstR=='\t') return sub+ls+wrapLine(rest.length()>1?rest.substring(1):"");
		
		
		int indexSpace = sub.lastIndexOf(' ');
		int indexTab = sub.lastIndexOf('\t');
		int index=indexSpace<=indexTab?indexTab:indexSpace;
		
		if(index==-1) return sub+ls+wrapLine(rest);
		return sub.substring(0,index) + ls + wrapLine(sub.substring(index+1)+rest);
		
	}
}