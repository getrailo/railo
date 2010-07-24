package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;



public final class ErrorFactory extends S3Factory {
	public ErrorFactory(InputStream in) throws IOException, SAXException {
		super();
		if(in==null) return;
		init(in);
	}
	public void doStartElement(String uri, String name, String qName, Attributes atts) {}
	public void doEndElement(String uri, String name, String qName) throws SAXException {}
	protected void setContent(String value) throws SAXException 	{}	
}