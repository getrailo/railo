package railo.runtime.text.xml;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.XMLException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;



public class XMLValidator extends XMLEntityResolverDefaultHandler {

	@Override
	public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
		//print.out(publicID+":"+systemID);
		return super.resolveEntity(publicID, systemID);
	}

	private Array warnings;
	private Array errors;
	private Array fatals;
	private boolean hasErrors;
	private String strSchema;

	public XMLValidator(InputSource validator, String strSchema) {
		super(validator);
		this.strSchema=strSchema;
	}
	

	private void release() {
		warnings=null;
		errors=null;
		fatals=null;
		hasErrors=false;
	}
	
    @Override
    public void warning(SAXParseException spe)	{
    	log(spe,"Warning",warnings);
    }
	
	@Override
	public void error(SAXParseException spe) {
		hasErrors=true;
    	log(spe,"Error",errors);
    }

    @Override
    public void fatalError(SAXParseException spe) throws SAXException	{
		hasErrors=true;
    	log(spe,"Fatal Error",fatals);
    }
    
    private void log(SAXParseException spe, String type,Array array)	{
        StringBuffer sb = new StringBuffer("["+type+"] ");
        
        String id = spe.getSystemId();
        if(!StringUtil.isEmpty(id)) {
        	int li=id.lastIndexOf('/');
        	if(li!=-1)sb.append(id.substring(li+1));
        	else sb.append(id);
        }
        sb.append(':');
        sb.append(spe.getLineNumber());
        sb.append(':');
        sb.append(spe.getColumnNumber());
        sb.append(": ");
        sb.append(spe.getMessage());
        sb.append(" ");
        array.appendEL(sb.toString());
    }
    
	public Struct validate(InputSource xml) throws XMLException {
		warnings=new ArrayImpl();
		errors=new ArrayImpl();
		fatals=new ArrayImpl();
		
		try {
            XMLReader parser = XMLUtil.createXMLReader("org.apache.xerces.parsers.SAXParser");
            parser.setContentHandler(this);
            parser.setErrorHandler(this);
            parser.setEntityResolver(this);
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            //if(!validateNamespace)
            if(!StringUtil.isEmpty(strSchema))
            	parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", strSchema);
            parser.parse(xml);
        }
        catch(SAXException e) { }
        catch(IOException e){
        	
        	throw new XMLException(e.getMessage());
        }
        
        // result
        Struct result = new StructImpl();
        result.setEL("warnings", warnings);
        result.setEL("errors", errors);
        result.setEL("fatalerrors", fatals);
        result.setEL("status", Caster.toBoolean(!hasErrors));
        release();
        return result;
	}

}
