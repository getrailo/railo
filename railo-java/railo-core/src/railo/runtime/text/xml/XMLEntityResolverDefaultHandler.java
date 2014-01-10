package railo.runtime.text.xml;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import railo.commons.io.IOUtil;
import railo.commons.net.HTTPUtil;



public class XMLEntityResolverDefaultHandler extends DefaultHandler {

	private InputSource entityRes;

	public XMLEntityResolverDefaultHandler(InputSource entityRes) {
		this.entityRes=entityRes;
	}
	
	@Override
	public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
		//if(entityRes!=null)print.out("resolveEntity("+(entityRes!=null)+"):"+publicID+":"+systemID);
		
		if(entityRes!=null) return entityRes;
		try {
			// TODO user resources
			return new InputSource(IOUtil.toBufferedInputStream(HTTPUtil.toURL(systemID,true).openStream()));
		} 
		catch (Throwable t) {
			return null;
		}
	}
	
}