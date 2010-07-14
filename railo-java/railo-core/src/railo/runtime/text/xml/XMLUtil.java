package railo.runtime.text.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import railo.aprint;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.XMLException;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.struct.XMLMultiElementStruct;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.text.xml.struct.XMLStructFactory;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

/**
 * 
 */
public final class XMLUtil {
    public static final Collection.Key XMLCOMMENT = KeyImpl.getInstance("xmlcomment");
    public static final Collection.Key XMLTEXT = KeyImpl.getInstance("xmltext");
    public static final Collection.Key XMLCDATA = KeyImpl.getInstance("xmlcdata");
    public static final Collection.Key XMLCHILDREN = KeyImpl.getInstance("xmlchildren");
    public static final Collection.Key XMLNSURI = KeyImpl.getInstance("xmlnsuri");
    public static final Collection.Key XMLNSPREFIX = KeyImpl.getInstance("xmlnsprefix");
    public static final Collection.Key XMLROOT = KeyImpl.getInstance("xmlroot");
    public static final Collection.Key XMLNAME = KeyImpl.getInstance("xmlname");
    public static final Collection.Key XMLTYPE = KeyImpl.getInstance("xmltype");
    public static final Collection.Key XMLVALUE = KeyImpl.getInstance("xmlvalue");
    public static final Collection.Key XMLATTRIBUTES = KeyImpl.getInstance("xmlattributes");
	/*
	private static final Collection.Key  = KeyImpl.getInstance();
	private static final Collection.Key  = KeyImpl.getInstance();
	private static final Collection.Key  = KeyImpl.getInstance();
	private static final Collection.Key  = KeyImpl.getInstance();
	private static final Collection.Key  = KeyImpl.getInstance();
	private static final Collection.Key  = KeyImpl.getInstance();
	*/
    
    
    
	//static DOMParser parser = new DOMParser();
	private static DocumentBuilder docBuilder;
	//private static DocumentBuilderFactory factory;
    private static TransformerFactory transformerFactory;
	

    public static String unescapeXMLString(String str) {
      	
      	StringBuffer rtn=new StringBuffer();
      	int posStart=-1;
      	int posFinish=-1;
      	while((posStart=str.indexOf('&',posStart))!=-1) {
      		int last=posFinish+1;
      		
      		posFinish=str.indexOf(';',posStart);
      		if(posFinish==-1)break;
      		rtn.append(str.substring(last,posStart));
      		if(posStart+1<posFinish) {
      			rtn.append(unescapeXMLEntity(str.substring(posStart+1,posFinish)));
      		}
      		else {
      			rtn.append("&;");
      		}
      		
      		posStart=posFinish+1;
      	}
      	rtn.append(str.substring(posFinish+1));
      	return rtn.toString();
    }

    public static String unescapeXMLString2(String str) {

    	StringBuffer sb=new StringBuffer();
    	int index,last=0,indexSemi;
    	while((index=str.indexOf('&',last))!=-1) {
    		sb.append(str.substring(last,index));
    		indexSemi=str.indexOf(';',index+1);

    		if(indexSemi==-1) {
    			sb.append('&');
    			last=index+1;
    		}
    		else if(index+1==indexSemi) {
    			sb.append("&;");
    			last=index+2;
    		}
    		else {
    			sb.append(unescapeXMLEntity(str.substring(index+1,indexSemi)));
    			last=indexSemi+1;
    		}
    	}
      	sb.append(str.substring(last));
      	return sb.toString();
    }
    
    public static void main(String[] args) { 
    	aprint.out(unescapeXMLString2("<h1><a href=\"/start\" target=\"_self\" name=\"&amp;lid=/start\">11 Sicherheitsupdates fﾟr XP & Co.</a>"));
    	aprint.out(unescapeXMLString2("abcd"));
    	aprint.out(unescapeXMLString2("ab&cd&"));
    	aprint.out(unescapeXMLString2("a&b&&&c&d&"));
    	aprint.out(unescapeXMLString2("a&;b&;c&;d&;"));
    	aprint.out(unescapeXMLString2("ab&amp;cd"));
    	aprint.out(unescapeXMLString2("ab&amp;&susi;cd"));
        
    }
    
    
    private static String unescapeXMLEntity(String str) {
    	if("lt".equals(str)) return "<";
    	if("gt".equals(str)) return ">";
    	if("amp".equals(str)) return "&";
    	if("apos".equals(str)) return "'";
    	if("quot".equals(str)) return "\"";
		return "&"+str+";";
	}

	public static String escapeXMLString(String xmlStr) {
    	char c;
    	StringBuffer sb=new StringBuffer();
    	int len=xmlStr.length();
    	for(int i=0;i<len;i++) {
    		c=xmlStr.charAt(i);
    		if(c=='<') 		sb.append("&lt;");
    		else if(c=='>')	sb.append("&gt;");
    		else if(c=='&')	sb.append("&amp;");
    		//else if(c=='\'')	sb.append("&amp;");
    		else if(c=='"')	sb.append("&quot;");
    		//else if(c>127) sb.append("&#"+((int)c)+";");
    		else sb.append(c);
    	}
    	return sb.toString();
    }
    
    
    /**
     * @return returns a singelton TransformerFactory
     */
    public static TransformerFactory getTransformerFactory() {
        // Saxon
        //if(transformerFactory==null)transformerFactory=new com.icl.saxon.TransformerFactoryImpl();
        // Xalan
        if(transformerFactory==null)transformerFactory=new TransformerFactoryImpl();
        // Trax
        //if(transformerFactory==null)transformerFactory=new com.jclark.xsl.trax.TransformerFactoryImpl();
        // Trax
        //if(transformerFactory==null)transformerFactory=new jd.xml.xslt.trax.TransformerFactoryImpl();
        // Caucho
        //if(transformerFactory==null)transformerFactory=new Xsl();
        // xsltc
        //if(transformerFactory==null)transformerFactory=new org.apache.xalan.xsltc.trax.TransformerFactoryImpl();
        
        
        return transformerFactory;
    }
    
    /**
     * parse XML/HTML String to a XML DOM representation
     * @param xml XML InputSource
     * @param isHtml is a HTML or XML Object
     * @return parsed Document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException 
     */
    public static final Document parse(InputSource xml,InputSource validator, boolean isHtml) 
        throws SAXException, IOException {
        
        if(!isHtml) {
        	
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if(validator==null) {
            	factory.setAttribute(XMLConstants.NON_VALIDATING_DTD_EXTERNAL, Boolean.FALSE);
            	factory.setAttribute(XMLConstants.NON_VALIDATING_DTD_GRAMMAR, Boolean.FALSE);
            }
            else {
            	factory.setAttribute(XMLConstants.VALIDATION_SCHEMA, Boolean.TRUE);
                factory.setAttribute(XMLConstants.VALIDATION_SCHEMA_FULL_CHECKING, Boolean.TRUE);
            }
            factory.setNamespaceAware(true);
            factory.setValidating(validator!=null);
            
            try {
				DocumentBuilder builder = factory.newDocumentBuilder();
	            builder.setEntityResolver(new XMLEntityResolverDefaultHandler(validator));
	            builder.setErrorHandler(new ThrowingErrorHandler(true,true,false));
	            return  builder.parse(xml);
			} 
            catch (ParserConfigurationException e) {
				throw new SAXException(e);
			}
            
	        /*DOMParser parser = new DOMParser();
	        print.out("parse");
	        parser.setEntityResolver(new XMLEntityResolverDefaultHandler(validator));
	        parser.parse(xml);
	        return parser.getDocument();*/
        }
        
        XMLReader reader = new Parser();
            reader.setFeature(Parser.namespacesFeature, true);
            reader.setFeature(Parser.namespacePrefixesFeature, true);
        
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            
            DOMResult result = new DOMResult();
            transformer.transform(new SAXSource(reader, xml), result);
            return XMLUtil.getDocument(result.getNode());
        } 
        catch (Exception e) {
            throw new SAXException(e);
        }
    }
	
	/**
	 * sets a node to a node (Expression Less)
	 * @param node
	 * @param key
	 * @param value
	 * @return Object set
	 */
	public static Object setPropertyEL(Node node, Collection.Key key, Object value) {
		try {
			return setProperty(node,key,value);
		} catch (PageException e) {
			return null;
		}
	}
	public static Object setPropertyEL(Node node, Collection.Key key, Object value,boolean caseSensitive) {
		try {
			return setProperty(node,key,value,caseSensitive);
		} catch (PageException e) {
			return null;
		}
	}
	
	/**
	 * sets a node to a node
	 * @param node
	 * @param key
	 * @param value
	 * @return Object set
	 * @throws PageException
	 */

	public static Object setProperty(Node node, Collection.Key k, Object value) throws PageException {
		return setProperty(node, k, value, isCaseSensitve(node));
	}
	
	public static Object setProperty(Node node, Collection.Key k, Object value,boolean caseSensitive) throws PageException {
		
		Document doc=(node instanceof Document)?(Document)node:node.getOwnerDocument();
		
		// Comment
			if(k.equals(XMLCOMMENT)) {
				removeChilds(XMLCaster.toRawNode(node),Node.COMMENT_NODE,false);
				node.appendChild(XMLCaster.toRawNode(XMLCaster.toComment(doc,value)));
			}
		// NS URI
			else if(k.equals(XMLNSURI)) {
				// TODO impl
				throw new ExpressionException("XML NS URI can't be set","not implemented");
			}
		// Prefix
			else if(k.equals(XMLNSPREFIX)) {
				// TODO impl
				throw new ExpressionException("XML NS Prefix can't be set","not implemented");
				//node.setPrefix(Caster.toString(value));
			}			
		// Root
			else if(k.equals(XMLROOT)) {
				doc.appendChild(XMLCaster.toNode(doc,value));
			}
		// Name	
			else if(k.equals(XMLNAME)) {
				throw new XMLException("You can't assign a new value for the property [xmlname]");
			}
		// Type	
			else if(k.equals(XMLTYPE)) {
				throw new XMLException("You can't change type of a xml node [xmltype]");
			}
		// value	
			else if(k.equals(XMLVALUE)) {
				node.setNodeValue(Caster.toString(value));
			}
		// Attributes	
			else if(k.equals(XMLATTRIBUTES)) {
				Element parent=XMLCaster.toElement(doc,node);
				Attr[] attres=XMLCaster.toAttrArray(doc,value);
				//print.ln("=>"+value);
				for(int i=0;i<attres.length;i++) {
					if(attres[i]!=null) {
						parent.setAttributeNode(attres[i]);
						//print.ln(attres[i].getName()+"=="+attres[i].getValue());
					}
				}
			}
		// Text	
			else if(k.equals(XMLTEXT)) {
				removeChilds(XMLCaster.toRawNode(node),Node.TEXT_NODE,false);
				node.appendChild(XMLCaster.toRawNode(XMLCaster.toText(doc,value)));
			}
		// CData	
			else if(k.equals(XMLCDATA)) {
				removeChilds(XMLCaster.toRawNode(node),Node.CDATA_SECTION_NODE,false);
				node.appendChild(XMLCaster.toRawNode(XMLCaster.toCDATASection(doc,value)));
			}
		// Children	
			else if(k.equals(XMLCHILDREN)) {
				Node[] nodes=XMLCaster.toNodeArray(doc,value);
				removeChilds(XMLCaster.toRawNode(node),Node.ELEMENT_NODE,false);
				for(int i=0;i<nodes.length;i++) {
					if(nodes[i]==node) throw new XMLException("can't assign a XML Node to himself");
					if(nodes[i]!=null)node.appendChild(XMLCaster.toRawNode(nodes[i]));
				}
			}
			else {
			    Node child = XMLCaster.toNode(doc,value);
				if(!k.getString().equalsIgnoreCase(child.getNodeName())) {
				    throw new XMLException("if you assign a XML Element to a XMLStruct , assignment property must have same name like XML Node Name", "Property Name is "+k.getString()+" and XML Element Name is "+child.getNodeName());
				}
				NodeList list = XMLUtil.getChildNodes(node, Node.ELEMENT_NODE);
				int len = list.getLength();
				Node n;
				for(int i=0;i<len;i++) {
					n=list.item(i);
					if(nameEqual(n, k.getString(), caseSensitive)) {
						node.replaceChild(XMLCaster.toRawNode(child), XMLCaster.toRawNode(n));
						return value;
					}
				}
				node.appendChild(XMLCaster.toRawNode(child));
			}
			return value;
	}


	public static Object getPropertyEL(Node node, Collection.Key key) {
		return getPropertyEL(node, key,isCaseSensitve(node));
	}
	
	
	/**
	 * returns a property from a XMl Node  (Expression Less)
	 * @param node
	 * @param key
	 * @param caseSensitive
	 * @return Object matching key
	 */
	public static Object getPropertyEL(Node node, Collection.Key k,boolean caseSensitive) {
		try {
			return getProperty(node, k,caseSensitive);
		} catch (SAXException e) {
			return null;
		}
	}
	
	public static Object getProperty(Node node, Collection.Key key) throws SAXException {
		return getProperty(node, key,isCaseSensitve(node));
	}
	
	/**
	 * returns a property from a XMl Node
	 * @param node
	 * @param key
	 * @param caseSensitive
	 * @return Object matching key
	 * @throws SAXException
	 */
	public static Object getProperty(Node node, Collection.Key k,boolean caseSensitive) throws SAXException {
	    
		
		//String lcKey=StringUtil.toLowerCase(key);
		if(k.getLowerString().startsWith("xml")) {
		// Comment
			if(k.equals(XMLCOMMENT)) {
				StringBuffer sb=new StringBuffer();
				NodeList list = node.getChildNodes();
				int len=list.getLength();
				for(int i=0;i<len;i++) {
					Node n=list.item(i);
					if(n instanceof Comment) {
						sb.append(((Comment)n).getData());
					}
				}
				return sb.toString();
			}
		// NS URI
			if(k.equals(XMLNSURI)) {
				return param(node.getNamespaceURI(),"");
			}
		// Prefix
			if(k.equals(XMLNSPREFIX)) {
				return param(node.getPrefix(),"");
			}
		// Root
			else if(k.equals(XMLROOT)) {
				Element re = getRootElement(node,caseSensitive);
				if(re==null) throw new SAXException("Attribute ["+k.getString()+"] not found in XML, XML is empty");
				return param(re,"");
			}
		// Name	
			else if(k.equals(XMLNAME)) {
				return node.getNodeName();
			}
		// Value	
			else if(k.equals(XMLVALUE)) {
				return node.getNodeValue();
			}
		// type	
			else if(k.equals(XMLTYPE)) {
				return getTypeAsString(node,true);
			}
		// Attributes	
			else if(k.equals(XMLATTRIBUTES)) {
				return new XMLAttributes(node.getOwnerDocument(),node.getAttributes(),caseSensitive);
			}
		// Text	
			else if(k.equals(XMLTEXT)) {
				StringBuffer sb=new StringBuffer();
				NodeList list = node.getChildNodes();
				int len=list.getLength();
				for(int i=0;i<len;i++) {
					Node n=list.item(i);
                    if(n instanceof Text || n instanceof CDATASection) {
                        sb.append(((CharacterData)n).getData());
					}
				}
                return sb.toString();
			}
			else if(k.equals(XMLCDATA)) {
				StringBuffer sb=new StringBuffer();
				NodeList list = node.getChildNodes();
				int len=list.getLength();
				for(int i=0;i<len;i++) {
					Node n=list.item(i);
                    if(n instanceof Text || n instanceof CDATASection) {
                        sb.append(((CharacterData)n).getData());
					}
				}
                return sb.toString();
			}
			// children	
			else if(k.equals(XMLCHILDREN)) {
				return new XMLNodeList(node,caseSensitive);
			}
		}
		
		if(node instanceof Document) {
		    node=((Document)node).getDocumentElement();
		    if(node==null) throw new SAXException("Attribute ["+k.getString()+"] not found in XML, XML is empty");
		    
		    //if((!caseSensitive && node.getNodeName().equalsIgnoreCase(k.getString())) || (caseSensitive && node.getNodeName().equals(k.getString()))) {
		    if(nameEqual(node, k.getString(), caseSensitive)) {
				return XMLStructFactory.newInstance(node,caseSensitive);
			}
		}
		else {	
			XMLNodeList xmlNodeList=new XMLNodeList(node,caseSensitive);
			Array array=new ArrayImpl();
			
			int[] ints=xmlNodeList.intKeys();
			for(int i=0;i<ints.length;i++) {
				Object o=xmlNodeList.get(ints[i],null);
				if(o instanceof Element) {
					Element el=(Element) o;
					
					//if((!caseSensitive && el.getNodeName().equalsIgnoreCase(k.getString())) || (caseSensitive && el.getNodeName().equals(k.getString()))) {
					if(XMLUtil.nameEqual(el,k.getString(),caseSensitive)) {
						try {
							array.append(XMLCaster.toXMLStruct(el,caseSensitive));
						} catch (PageException e) {}
					}
				}
			}
			
			if(array.size()>0) {
				try {
					return new XMLMultiElementStruct(array,false);
				} catch (PageException e) {}
			}
			try {
				return XMLCaster.toXMLStruct((Node)xmlNodeList.get(k),caseSensitive);
			} 
            catch (ExpressionException e) {}
		}
		throw new SAXException("Attribute ["+k.getString()+"] not found in XML Node ("+Caster.toClassName(node)+")");
	}
	

    /**
     * check if given name is equal to name of the element (with and without namespace)
     * @param node
     * @param k
     * @param caseSensitive
     * @return
     */
    public static boolean nameEqual(Node node, String name, boolean caseSensitive) {
		if(name==null) return false;
    	if(caseSensitive){
    		return name.equals(node.getNodeName()) || name.equals(node.getLocalName());
    	}
    	return name.equalsIgnoreCase(node.getNodeName()) || name.equalsIgnoreCase(node.getLocalName());
	}

	public static boolean isCaseSensitve(Node node) {
		if(node instanceof XMLStruct) return ((XMLStruct)node).isCaseSensitive();
    	return true;
	}

	/**
     * removes child from a node
     * @param node
     * @param key
     * @param caseSensitive
     * @return removed property
     */
    public static Object removeProperty(Node node, Collection.Key k,boolean caseSensitive) {
        
        //String lcKeyx=k.getLowerString();
        if(k.getLowerString().startsWith("xml")) {
        // Comment
            if(k.equals(XMLCOMMENT)) {
                StringBuffer sb=new StringBuffer();
                NodeList list = node.getChildNodes();
                int len=list.getLength();
                for(int i=0;i<len;i++) {
                    Node n=list.item(i);
                    if(n instanceof Comment) {
                        sb.append(((Comment)n).getData());
                        node.removeChild(XMLCaster.toRawNode(n));
                    }
                }
                return sb.toString();
            }
        // Text 
            else if(k.equals(XMLTEXT)) {
                StringBuffer sb=new StringBuffer();
                NodeList list = node.getChildNodes();
                int len=list.getLength();
                for(int i=0;i<len;i++) {
                    Node n=list.item(i);
                    if(n instanceof Text || n instanceof CDATASection) {
                        sb.append(((CharacterData)n).getData());
                        node.removeChild(XMLCaster.toRawNode(n));
                    }
                }
                return sb.toString();
            }
            // children 
            else if(k.equals(XMLCHILDREN)) {
                NodeList list=node.getChildNodes();
                for(int i=list.getLength()-1;i>=0;i--) {
                    node.removeChild(XMLCaster.toRawNode(list.item(i)));
                }
                return list;
            }
        }
         
            NodeList nodes = node.getChildNodes();
            Array array=new ArrayImpl();
            for(int i=nodes.getLength()-1;i>=0;i--) {
                Object o=nodes.item(i);
                if(o instanceof Element) {
                    Element el=(Element) o;
                    if(nameEqual(el, k.getString(), caseSensitive)) {
                        array.appendEL(XMLCaster.toXMLStruct(el,caseSensitive));
                        node.removeChild(XMLCaster.toRawNode(el));
                    }
                }
            }
            
            if(array.size()>0) {
                try {
                    return new XMLMultiElementStruct(array,false);
                } catch (PageException e) {}
            }
            return null;
    }
    

	private static Object param(Object o1, Object o2) {
		if(o1==null)return o2;
		return o1;
	}
	
	/**
	 * return the root Element from a node
	 * @param node node to get root element from
	 * @param caseSensitive
	 * @return Root Element
	 */
	public static Element getRootElement(Node node, boolean caseSensitive) {
	    Document doc=null;
		if(node instanceof Document) doc=(Document) node;
		else doc=node.getOwnerDocument();
		Element el = doc.getDocumentElement();
		if(el==null) return null;
		return (Element)XMLStructFactory.newInstance(el,caseSensitive);
	}

	/**
	 * returns a new Empty XMl Document
	 * @return new Document
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 */
	public static Document newDocument() throws ParserConfigurationException, FactoryConfigurationError {
		if(docBuilder==null) {
			docBuilder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		return docBuilder.newDocument();
	}
	
	/**
	 * return the Owner Document of a Node List
	 * @param nodeList
	 * @return XML Document
	 * @throws XMLException
	 */
	public static Document getDocument(NodeList nodeList) throws XMLException {
		if(nodeList instanceof Document) return (Document)nodeList;
		int len=nodeList.getLength();
		for(int i=0;i<len;i++) {
			Node node=nodeList.item(i);
			if(node!=null) return node.getOwnerDocument();
		}
		throw new XMLException("can't get Document from NodeList, in NoteList are no Nodes");
	}
	
	/**
	 * return the Owner Document of a Node
	 * @param node
	 * @return XML Document
	 */
	public static Document getDocument(Node node) {
		if(node instanceof Document) return (Document)node;
		return node.getOwnerDocument();
	}
	
	
	/**
	 * removes all comments from a node
	 * @param node node to remove elements from
	 * @param type Type Definition to remove (Constant value from class Node)
	 * @param deep remove also in sub nodes
	 */
	private synchronized static void removeChilds(Node node, short type, boolean deep) {
		NodeList list = node.getChildNodes();
		
		for(int i=list.getLength();i>=0;i--) {
			Node n=list.item(i);
			if(n ==null )continue;
			else if(n.getNodeType()==type)node.removeChild(XMLCaster.toRawNode(n));
			else if(deep)removeChilds(n,type,deep);
		}
	}

	/**
	 * return all Children of a node by a defined type as Node List
	 * @param node node to get children from
	 * @param type type of returned node
	 * @param filter 
	 * @param caseSensitive 
	 * @return all matching child node
	 */
	public synchronized static NodeList getChildNodes(Node node, short type) {
		return getChildNodes(node, type, false, null);
	}
	public synchronized static NodeList getChildNodes(Node node, short type, boolean caseSensitive, String filter) {
		ArrayNodeList rtn=new ArrayNodeList();
		NodeList nodes=node.getChildNodes();
		int len=nodes.getLength();
		Node n;
		for(int i=0;i<len;i++) {
			try {
				n=nodes.item(i);
				if(n!=null && n.getNodeType()==type){
					if(filter==null || (caseSensitive?filter.equals(n.getLocalName()):filter.equalsIgnoreCase(n.getLocalName())))
					rtn.add(n);
				}
			}
			catch(Throwable t){}
		}
		return rtn;
	}
	
    /**
     * return all Children of a node by a defined type as Node Array
     * @param node node to get children from
     * @param type type of returned node
     * @param filter 
     * @param caseSensitive 
     * @return all matching child node
     */
    public static Node[] getChildNodesAsArray(Node node, short type) {
        ArrayNodeList nodeList=(ArrayNodeList)getChildNodes(node, type);
        return (Node[]) nodeList.toArray(new Node[nodeList.getLength()]);
    }

    public static Node[] getChildNodesAsArray(Node node, short type, boolean caseSensitive, String filter) {
        ArrayNodeList nodeList=(ArrayNodeList)getChildNodes(node, type,caseSensitive,filter);
        return (Node[]) nodeList.toArray(new Node[nodeList.getLength()]);
    }
    
    /**
     * return all Element Children of a node
     * @param node node to get children from
     * @return all matching child node
     */
    public static Element[] getChildElementsAsArray(Node node) {
        ArrayNodeList nodeList=(ArrayNodeList)getChildNodes(node,Node.ELEMENT_NODE);
        return (Element[]) nodeList.toArray(new Element[nodeList.getLength()]);
    }

    /**
     * transform a XML Object to a other format, with help of a XSL Stylesheet
     * @param strXML XML String 
     * @param strXSL XSL String
     * @return transformed Object
     * @throws TransformerException
     * @throws IOException
     * @throws SAXException
     * @throws  
     */
    public static String transform(InputSource xml, InputSource xsl) throws TransformerException, SAXException, IOException {
    	//toInputSource(pc, xml)
        return transform(parse(xml,null , false), xsl);
    }

	/**
	 * transform a XML Object to a other format, with help of a XSL Stylesheet
	 * @param doc XML Document Object
	 * @param strXSL XSL String
	 * @return transformed Object
	 * @throws TransformerException
	 */
	public static String transform(Document doc, InputSource xsl) throws TransformerException {
		StringWriter sw = new StringWriter();
		Transformer transformer = 
            XMLUtil.getTransformerFactory().newTransformer(new StreamSource(xsl.getCharacterStream()));
		transformer.transform(new DOMSource(doc), new StreamResult(sw));
		return sw.toString();
	}

    /**
     * returns the Node Type As String
     * @param node
	 * @param cftype 
	 * @return
     */
	public static String getTypeAsString(Node node, boolean cftype) {
		String suffix=cftype?"":"_NODE";
		
        switch(node.getNodeType()) {
    		case Node.ATTRIBUTE_NODE: 				return "ATTRIBUTE"+suffix;
    		case Node.CDATA_SECTION_NODE: 			return "CDATA_SECTION"+suffix;
    		case Node.COMMENT_NODE: 				return "COMMENT"+suffix;
    		case Node.DOCUMENT_FRAGMENT_NODE: 		return "DOCUMENT_FRAGMENT"+suffix;
    		case Node.DOCUMENT_NODE: 				return "DOCUMENT"+suffix;
    		case Node.DOCUMENT_TYPE_NODE: 			return "DOCUMENT_TYPE"+suffix;
    		case Node.ELEMENT_NODE: 				return "ELEMENT"+suffix;
    		case Node.ENTITY_NODE: 					return "ENTITY"+suffix;
    		case Node.ENTITY_REFERENCE_NODE: 		return "ENTITY_REFERENCE"+suffix;
    		case Node.NOTATION_NODE: 				return "NOTATION"+suffix;
    		case Node.PROCESSING_INSTRUCTION_NODE: 	return "PROCESSING_INSTRUCTION"+suffix;
    		case Node.TEXT_NODE: 					return "TEXT"+suffix;
    		default: 								return "UNKNOW"+suffix;
        }
    }

	public synchronized static Element getChildWithName(String name, Element el) {
		Element[] children = XMLUtil.getChildElementsAsArray(el);
		for(int i=0;i<children.length;i++) {
			if(name.equalsIgnoreCase(children[i].getNodeName()))
				return children[i];
		}
		return null;
	}
	
	public static InputSource toInputSource(Resource res) throws IOException {
        	String str = IOUtil.toString((res), null);
        	return new InputSource(new StringReader(str));
    }

	public static InputSource toInputSource(PageContext pc, Object value) throws IOException, ExpressionException {
		if(value instanceof InputSource) {
            return (InputSource) value;
        }
		if(value instanceof String) {
            return toInputSource(pc, (String)value);
        }
		if(value instanceof StringBuffer) {
            return toInputSource(pc, value.toString());
        }
        if(value instanceof Resource) {
        	String str = IOUtil.toString(((Resource)value), null);
        	return new InputSource(new StringReader(str));
        }
		if(value instanceof File) {
        	String str = IOUtil.toString(ResourceUtil.toResource(((File)value)), null);
        	return new InputSource(new StringReader(str));
        }
		if(value instanceof InputStream) {
			InputStream is = (InputStream)value;
			try {
				String str = IOUtil.toString(is, null);
	        	return new InputSource(new StringReader(str));
			}
			finally {
				IOUtil.closeEL(is);
			}
        }
		if(value instanceof Reader) {
			Reader reader = (Reader)value;
			try {
				String str = IOUtil.toString(reader);
	        	return new InputSource(new StringReader(str));
			}
			finally {
				IOUtil.closeEL(reader);
			}
        }
		if(value instanceof byte[]) {
			return new InputSource(new ByteArrayInputStream((byte[])value));
        }
		throw new ExpressionException("cat cast object of type ["+Caster.toClassName(value)+"] to a Input for xml parser");
        	
	}
	
	public static InputSource toInputSource(PageContext pc, String xml) throws IOException, ExpressionException {
		return toInputSource(pc, xml,true);
	}
	
	public static InputSource toInputSource(PageContext pc, String xml, boolean canBePath) throws IOException, ExpressionException {
		// xml text
		xml=xml.trim(); 
		if(!canBePath || xml.startsWith("<"))	{
			return new InputSource(new StringReader(xml));
		}
		// xml link
		pc=ThreadLocalPageContext.get(pc);
		Resource res = ResourceUtil.toResourceExisting(pc, xml);
		return toInputSource(pc, res);
	}
	
	public static Struct validate(InputSource xml, InputSource schema, String strSchema) throws XMLException {
    	return new XMLValidator(schema,strSchema).validate(xml);
    }

	/**
	 * adds a child at the first place 
	 * @param parent
	 * @param child
	 */
	public static void prependChild(Element parent, Element child) {
		Node first = parent.getFirstChild();
		if(first==null)parent.appendChild(child);
		else {
			parent.insertBefore(child, first);
		}
	}
}