package railo.runtime.text.xml.struct;

import org.w3c.dom.Node;

import railo.runtime.text.xml.XMLNodeList;
import railo.runtime.type.Struct;


/**
 * 
 */
public interface XMLStruct extends Struct,Node,XMLObject {
	/**
	 * @return casts XML Struct to a XML Node
	 */
	public Node toNode();

    /**
     * @return returns the children of the Node
     */
    public XMLNodeList getXMLNodeList();
    
    public boolean isCaseSensitive();
}