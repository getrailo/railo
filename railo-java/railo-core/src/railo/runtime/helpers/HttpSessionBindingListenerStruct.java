package railo.runtime.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import railo.runtime.type.Collection;
import railo.runtime.type.StructImpl;

public final class HttpSessionBindingListenerStruct extends StructImpl implements HttpSessionBindingListener {
    
    private URL url;

    /**
     * Constructor of the class
     * @param strUrl
     * @throws MalformedURLException
     */
    public HttpSessionBindingListenerStruct(String strUrl) throws MalformedURLException {
        this(new URL(strUrl));
    }
    
    /**
     * Constructor of the class
     * @param url
     */
    public HttpSessionBindingListenerStruct(URL url) {
        this.url=url;
    }
    
    public void valueBound(HttpSessionBindingEvent event) {
        //SystemOut.printDate("------------------------------- bound session -------------------------------");
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        //SystemOut.printDate("------------------------------- unbound session -------------------------------");
        try {
            url.getContent();
        } 
        catch (IOException e) {}
    }

	/**
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy, Map<Object, Object> done) {
		HttpSessionBindingListenerStruct trg=new HttpSessionBindingListenerStruct(url);
		copy(this, trg, deepCopy,done);
		return trg;
	}
}
