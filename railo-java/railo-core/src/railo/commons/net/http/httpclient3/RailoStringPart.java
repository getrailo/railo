package railo.commons.net.http.httpclient3;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.multipart.StringPart;


public class RailoStringPart extends StringPart {

	private String value;

	public RailoStringPart(String name, String value) {
		super(name, value);
		this.value=value;
	}
	
	public RailoStringPart(String name, String value, String charset) {
		super(name, value, charset);
		this.value=value;		
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	@Override
	protected void sendDispositionHeader(OutputStream out)  throws IOException {
		ResourcePart.sendDispositionHeader(getName(),null,getCharSet(),out);
	}

}
