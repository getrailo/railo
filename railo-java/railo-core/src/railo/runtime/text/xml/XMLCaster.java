package railo.runtime.text.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.HTMLEntities;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.XMLException;
import railo.runtime.functions.decision.IsNumeric;
import railo.runtime.functions.list.ListLast;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.text.xml.struct.XMLStructFactory;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

/**
 * Cast Objects to XML Objects of different types
 */
public final class XMLCaster {

    
    /**
	 * casts a value to a XML Text
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Text Object
	 * @throws PageException
	 */
	public static Text toText(Document doc, Object o) throws PageException {
		if(o instanceof Text) return (Text)o;
		else if(o instanceof CharacterData) return doc.createTextNode(((CharacterData)o).getData());
		return doc.createTextNode(Caster.toString(o));
	}
	
	public static Text toCDATASection(Document doc, Object o) throws PageException {
		if(o instanceof CDATASection) return (CDATASection)o;
		else if(o instanceof CharacterData) return doc.createCDATASection(((CharacterData)o).getData());
		return doc.createCDATASection(Caster.toString(o));
	}
	
	/**
	 * casts a value to a XML Text Array
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Text Array
	 * @throws PageException
	 */
	public static Text[] toTextArray(Document doc,Object o) throws PageException {
	// Node[]
		if(o instanceof Node[]) {
			Node[] nodes=(Node[])o;
			if(_isAllOfSameType(nodes,Node.TEXT_NODE))return (Text[])nodes; 
			
			Text[] textes=new Text[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				textes[i]=toText(doc,nodes[i]);
			}
			return textes;
		}
	// Collection
		else if(o instanceof Collection) {
			Collection coll=(Collection)o;
			Iterator<Object> it = coll.valueIterator();
			List<Text> textes=new ArrayList<Text>();
			while(it.hasNext()) {
				textes.add(toText(doc,it.next()));
			}
			return textes.toArray(new Text[textes.size()]);
		}
	// Node Map and List
		Node[] nodes=_toNodeArray(doc,o);
		if(nodes!=null) return toTextArray(doc,nodes);
	// Single Text Node
		try {
			return new Text[]{toText(doc,o)};
		} catch (ExpressionException e) {
			throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Text Array");
		}
	}
	
	/**
	 * casts a value to a XML Attribute Object
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Comment Object
	 * @throws PageException
	 */
	public static Attr toAttr(Document doc, Object o) throws PageException {
		if(o instanceof Attr) return (Attr)o;
		if(o instanceof Struct && ((Struct)o).size()==1) {
			Struct sct=(Struct)o;
			Entry<Key, Object> e = sct.entryIterator().next();
			Attr attr= doc.createAttribute(e.getKey().getString());
			attr.setValue(Caster.toString(e.getValue()));
			return attr;
		}
		
		throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Attribute");
	}

	/**
	 * casts a value to a XML Attr Array
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Attr Array
	 * @throws PageException
	 */
	public static Attr[] toAttrArray(Document doc,Object o) throws PageException {
	// Node[]
		if(o instanceof Node[]) {
			Node[] nodes=(Node[])o;
			if(_isAllOfSameType(nodes,Node.ATTRIBUTE_NODE))return (Attr[])nodes; 
			
			Attr[] attres=new Attr[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				attres[i]=toAttr(doc,nodes[i]);
			}
			return attres;
		}
	// Collection
		else if(o instanceof Collection) {
			Collection coll=(Collection)o;
			Iterator<Entry<Key, Object>> it = coll.entryIterator();
			Entry<Key, Object> e;
			List<Attr> attres=new ArrayList<Attr>();
			Attr attr;
			Collection.Key k;
			while(it.hasNext()) {
				e = it.next();
				k=e.getKey();
				attr=doc.createAttribute(IsNumeric.call(null,k.getString())?"attribute-"+k.getString():k.getString());
				attr.setValue(Caster.toString(e.getValue()));
				attres.add(attr);
			}
			return attres.toArray(new Attr[attres.size()]);
		}
	// Node Map and List
		Node[] nodes=_toNodeArray(doc,o);
		if(nodes!=null) return toAttrArray(doc,nodes);
	// Single Text Node
		try {
			return new Attr[]{toAttr(doc,o)};
		} catch (ExpressionException e) {
			throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Attributes Array");
		}
	}

	/**
	 * casts a value to a XML Comment Object
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Comment Object
	 * @throws PageException
	 */
	public static Comment toComment(Document doc,Object o) throws PageException {
		if(o instanceof Comment) return (Comment)o;
		else if(o instanceof CharacterData) return doc.createComment(((CharacterData)o).getData());
		return doc.createComment(Caster.toString(o));
	}
	
	/**
	 * casts a value to a XML Comment Array
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Comment Array
	 * @throws PageException
	 */
	public static Comment[] toCommentArray(Document doc,Object o) throws PageException {
	// Node[]
		if(o instanceof Node[]) {
			Node[] nodes=(Node[])o;
			if(_isAllOfSameType(nodes,Node.COMMENT_NODE))return (Comment[])nodes; 
			
			Comment[] comments=new Comment[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				comments[i]=toComment(doc,nodes[i]);
			}
			return comments;
		}
	// Collection
		else if(o instanceof Collection) {
			Collection coll=(Collection)o;
			Iterator<Object> it = coll.valueIterator();
			List<Comment> comments=new ArrayList<Comment>();
			while(it.hasNext()) {
				comments.add(toComment(doc,it.next()));
			}
			return comments.toArray(new Comment[comments.size()]);
		}
	// Node Map and List
		Node[] nodes=_toNodeArray(doc,o);
		if(nodes!=null) return toCommentArray(doc,nodes);
	// Single Text Node
		try {
			return new Comment[]{toComment(doc,o)};
		} catch (ExpressionException e) {
			throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Comment Array");
		}
	}

	/**
	 * casts a value to a XML Element
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Element Object
	 * @throws PageException
	 */
	public static Element toElement(Document doc,Object o) throws PageException {
		if(o instanceof Element)return (Element)o;
		else if(o instanceof Node)throw new ExpressionException("Object "+Caster.toClassName(o)+" must be a XML Element");
		return doc.createElement(Caster.toString(o));
	}
	
	/**
	 * casts a value to a XML Element Array
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Comment Array
	 * @throws PageException
	 */
	public static Element[] toElementArray(Document doc,Object o) throws PageException {
	// Node[]
		if(o instanceof Node[]) {
			Node[] nodes=(Node[])o;
			if(_isAllOfSameType(nodes,Node.ELEMENT_NODE))return (Element[])nodes; 
			
			Element[] elements=new Element[nodes.length];
			for(int i=0;i<nodes.length;i++) {
				elements[i]=toElement(doc,nodes[i]);
			}
			return elements;
		}
	// Collection
		else if(o instanceof Collection) {
			Collection coll=(Collection)o;
			Iterator<Object> it = coll.valueIterator();
			List<Element> elements=new ArrayList<Element>();
			while(it.hasNext()) {
				elements.add(toElement(doc,it.next()));
			}
			return elements.toArray(new Element[elements.size()]);
		}
	// Node Map and List
		Node[] nodes=_toNodeArray(doc,o);
		if(nodes!=null) return toElementArray(doc,nodes);
	// Single Text Node
		try {
			return new Element[]{toElement(doc,o)};
		} catch (ExpressionException e) {
			throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Element Array");
		}
	}
	
	/**
	 * casts a value to a XML Node
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Element Object
	 * @throws PageException
	 * @deprecated replaced with toRawNode
	 */
	public static Node toNode(Object o) throws PageException {
		if(o instanceof XMLStruct)return ((XMLStruct)o).toNode();
		if(o instanceof Node)return (Node)o;
		throw new CasterException(o,"node");
	}
	
	/**
	 * remove railo node wraps (XMLStruct) from node 
	 * @param node
	 * @return raw node (without wrap)
	 */
	public static Node toRawNode(Node node) {
		if(node instanceof XMLStruct)return ((XMLStruct)node).toNode();
		return node;
	}
	
	public static Node toNode(Document doc,Object o) throws PageException {
		if(o instanceof XMLStruct)return ((XMLStruct)o).toNode();
		if(o instanceof Node)return (Node)o;
		String nodeName=Caster.toString(o);
		if(nodeName.length()==0)nodeName="Empty";
		return doc.createElement(nodeName);
	}

	
	/**
	 * casts a value to a XML Element Array
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML Comment Array
	 * @throws PageException
	 */
	public static Node[] toNodeArray(Document doc,Object o) throws PageException {
		if(o instanceof Node) return new Node[]{(Node)o};
	// Node[]
		if(o instanceof Node[]) {
			return (Node[])o;
		}
	// Collection
		else if(o instanceof Collection) {
			Collection coll=(Collection)o;
			Iterator<Object> it = coll.valueIterator();
			List<Node> nodes=new ArrayList<Node>();
			while(it.hasNext()) {
				nodes.add(toNode(doc,it.next()));
			}
			return nodes.toArray(new Node[nodes.size()]);
		}
	// Node Map and List
		Node[] nodes=_toNodeArray(doc,o);
		if(nodes!=null) return nodes;
	// Single Text Node
		try {
			return new Node[]{toNode(doc,o)};
		} catch (ExpressionException e) {
			throw new XMLException("can't cast Object of type "+Caster.toClassName(o)+" to a XML Node Array");
		}
	}
	



	/**
	 * casts a value to a XML Object defined by type parameter
	 * @param doc XML Document
	 * @param o Object to cast
	 * @param type type to cast to
	 * @return XML Text Object
	 * @throws PageException
	 */
	public static Node toNode(Document doc, Object o, short type) throws PageException {
		
		if(Node.TEXT_NODE == type) toText(doc, o);
		else if(Node.ATTRIBUTE_NODE == type) toAttr(doc, o);
		else if(Node.COMMENT_NODE == type) toComment(doc, o);
		else if(Node.ELEMENT_NODE == type) toElement(doc, o);
		
		
		throw new ExpressionException("invalid node type definition");
	}

	/**
	 * casts a value to a XML Object Array defined by type parameter
	 * @param doc XML Document
	 * @param o Object to cast
	 * @param type type to cast to
	 * @return XML Node Array Object
	 * @throws PageException
	 */
	public static Node[] toNodeArray(Document doc, Object o, short type) throws PageException {
		
		if(Node.TEXT_NODE == type) toTextArray(doc, o);
		else if(Node.ATTRIBUTE_NODE == type) toAttrArray(doc, o);
		else if(Node.COMMENT_NODE == type) toCommentArray(doc, o);
		else if(Node.ELEMENT_NODE == type) toElementArray(doc, o);
		
		
		throw new ExpressionException("invalid node type definition");
	}

	/* *
	 * cast a xml node to a String
	 * @param node
	 * @return xml node as String
	 * @throws ExpressionException
	 * /
	public static String toString(Node node) throws ExpressionException  {
        //Transformer tf;
		try {
	        OutputFormat format = new OutputFormat();
	        
	        StringWriter writer = new StringWriter();
	        XMLSerializer serializer = new XMLSerializer(writer, format);
	        if(node instanceof Element)serializer.serialize((Element)node);
	        else serializer.serialize(XMLUtil.getDocument(node));
	        return writer.toString();
	        
		} catch (Exception e) {
		    throw ExpressionException.newInstance(e);
		}
	}
	
	public static String toString(Node node,String defaultValue)  {
        //Transformer tf;
		try {
	        OutputFormat format = new OutputFormat();
	        
	        StringWriter writer = new StringWriter();
	        XMLSerializer serializer = new XMLSerializer(writer, format);
	        if(node instanceof Element)serializer.serialize((Element)node);
	        else serializer.serialize(XMLUtil.getDocument(node));
	        return writer.toString();
	        
		} catch (Exception e) {
		    return defaultValue;
		}
	}*/
	
	public static String toHTML(Node node) throws ExpressionException  {
		if(Node.DOCUMENT_NODE==node.getNodeType()) 
        	return toHTML(XMLUtil.getRootElement(node,true));
		
		StringBuffer sb=new StringBuffer();
		toHTML(node, sb);
		return sb.toString();
	}
    
    private static void toHTML(Node node,StringBuffer sb) throws ExpressionException  {
		short type=node.getNodeType();
        if(Node.ELEMENT_NODE==type) {
        	Element el = (Element) node;
        	String tagName=el.getTagName();
        	sb.append('<');
			sb.append(tagName);
			
			NamedNodeMap attrs = el.getAttributes();
			Attr attr;
			int len = attrs.getLength();
			for(int i=0;i<len;i++) {
				attr=(Attr) attrs.item(i);
				sb.append(' ');
				sb.append(attr.getName());
				sb.append("=\"");
				sb.append(attr.getValue());
				sb.append('"');
			}
			NodeList children = el.getChildNodes();
			len = children.getLength();
			
			boolean doEndTag=len!=0 || (tagName.length()==4 && (tagName.equalsIgnoreCase("head") || tagName.equalsIgnoreCase("body")));
	        
	        
			if(!doEndTag)sb.append(" />");
			else sb.append('>');
	        
			for(int i=0;i<len;i++) {
				toHTML(children.item(i),sb);
			}
	        
			if(doEndTag) {
				sb.append("</");
				sb.append(el.getTagName());
				sb.append('>');
			}
        }
        else if(node instanceof CharacterData) {
        	sb.append(HTMLEntities.escapeHTML(node.getNodeValue()));
		}
	}
	
	/**
	 * write a xml Dom to a file
	 * @param node
	 * @param file
	 * @throws PageException
	 */
	public static void writeTo(Node node, Resource file) throws PageException {
        OutputStream os=null;
		try {
			os=IOUtil.toBufferedOutputStream(file.getOutputStream());
			writeTo(node, new StreamResult(os),false,false,null,null,null);
		}
		catch(IOException ioe){
			throw Caster.toPageException(ioe);
		}
		finally {
			IOUtil.closeEL(os);
		}
	}
	

	public static String toString(Node node) throws PageException {
		StringWriter sw=new StringWriter();
		try {
			writeTo(node, new StreamResult(sw),false,false,null,null,null);
		} 
		finally {
			IOUtil.closeEL(sw);
		}
		return sw.getBuffer().toString();
	}

	public static String toString(Node node,boolean omitXMLDecl, boolean indent) throws PageException {
		StringWriter sw=new StringWriter();
		try {
			writeTo(node, new StreamResult(sw),omitXMLDecl,indent,null,null,null);
		} 
		finally {
			IOUtil.closeEL(sw);
		}
		return sw.getBuffer().toString();
	}
	
	
	public static String toString(Node node,boolean omitXMLDecl,boolean indent,String publicId,String systemId,String encoding) throws PageException {
		StringWriter sw=new StringWriter();
		try {
			writeTo(node, new StreamResult(sw),omitXMLDecl,indent,publicId,systemId,encoding);
		} 
		finally {
			IOUtil.closeEL(sw);
		}
		return sw.getBuffer().toString();
	}
	
	public static String toString(NodeList nodes,boolean omitXMLDecl, boolean indent) throws PageException {
		StringWriter sw=new StringWriter();
		try {
			int len = nodes.getLength();
			for(int i=0;i<len;i++){
				writeTo(nodes.item(i), new StreamResult(sw),omitXMLDecl,indent,null,null,null);
			}
		} 
		finally {
			IOUtil.closeEL(sw);
		}
		return sw.getBuffer().toString();
	}
	
	public static String toString(Node node,String defaultValue) {
		StringWriter sw=new StringWriter();
		try {
			writeTo(node, new StreamResult(sw),false,false,null,null,null);
		} 
		catch(Throwable t){
			return defaultValue;
		}
		finally {
			IOUtil.closeEL(sw);
		}
		return sw.getBuffer().toString();
	}
	
	
	public static void writeTo(Node node,Result res,boolean omitXMLDecl,boolean indent, String publicId,String systemId,String encoding) throws PageException {
		try {
			Transformer t = XMLUtil.getTransformerFactory().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT,indent?"yes":"no");
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,omitXMLDecl?"yes":"no");
			//t.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2"); 
			
			// optional properties
			if(!StringUtil.isEmpty(publicId,true))t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,publicId);
			if(!StringUtil.isEmpty(systemId,true))t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,systemId);
			if(!StringUtil.isEmpty(encoding,true))t.setOutputProperty(OutputKeys.ENCODING,encoding);
			
			t.transform(new DOMSource(node), res);
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	

    /**
	 * Casts a XML Node to a HTML Presentation
	 * @param node
	 * @param pageContext
	 * @return html output
	 */
	public static DumpData toDumpData(Node node, PageContext pageContext, int maxlevel, DumpProperties props) {
		if(maxlevel<=0) {
			return new SimpleDumpData("maxumum dump level reached");
		}
		maxlevel--;
		// Document
		if(node instanceof Document) {
			DumpTable table = new DumpTable("xml","#cc9999","#ffffff","#000000");
			table.setTitle("XML Document");
			table.appendRow(1,new SimpleDumpData("XmlComment"),new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLCOMMENT,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlRoot"),	DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLROOT,null), pageContext,maxlevel,props));
			return table;
			
		}
		// Element
		if(node instanceof Element) {
			DumpTable table = new DumpTable("xml","#cc9999","#ffffff","#000000");
			table.setTitle("XML Element");
			table.appendRow(1,new SimpleDumpData("xmlName"),		new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNAME,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlNsPrefix"),	new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNSPREFIX,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlNsURI"),		new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNSURI,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlText"),		DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLTEXT,null), pageContext,maxlevel,props));
			table.appendRow(1,new SimpleDumpData("XmlComment"),	new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLCOMMENT,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlAttributes"),DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLATTRIBUTES,null), pageContext,maxlevel,props));
			table.appendRow(1,new SimpleDumpData("XmlChildren"),	DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLCHILDREN,null), pageContext,maxlevel,props));
			return table;
			
		}
		// Attr
		if(node instanceof Attr) {
			DumpTable table = new DumpTable("xml","#cc9999","#ffffff","#000000");
			table.setTitle("XML Attr");
			table.appendRow(1,new SimpleDumpData("xmlName"),		new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNAME,null).toString()));
			table.appendRow(1,new SimpleDumpData("XmlValue"),	DumpUtil.toDumpData(((Attr)node).getValue(), pageContext,maxlevel,props));
			table.appendRow(1,new SimpleDumpData("XmlType"),	new SimpleDumpData(XMLUtil.getTypeAsString(node,true)));
			
			return table;
			
		}
		// Node
		DumpTable table = new DumpTable("xml","#cc9999","#ffffff","#000000");
		table.setTitle("XML Node ("+ListLast.call(null,node.getClass().getName(),".")+")");
		table.appendRow(1,new SimpleDumpData("xmlName"),		new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNAME,null).toString()));
		table.appendRow(1,new SimpleDumpData("XmlNsPrefix"),	new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNSPREFIX,null).toString()));
		table.appendRow(1,new SimpleDumpData("XmlNsURI"),		new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLNSURI,null).toString()));
		table.appendRow(1,new SimpleDumpData("XmlText"),		DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLTEXT,null), pageContext,maxlevel,props));
		table.appendRow(1,new SimpleDumpData("XmlComment"),	new SimpleDumpData(XMLUtil.getProperty(node,XMLUtil.XMLCOMMENT,null).toString()));
		table.appendRow(1,new SimpleDumpData("XmlAttributes"),DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLATTRIBUTES,null), pageContext,maxlevel,props));
		table.appendRow(1,new SimpleDumpData("XmlChildren"),	DumpUtil.toDumpData(XMLUtil.getProperty(node,XMLUtil.XMLCHILDREN,null), pageContext,maxlevel,props));
			
		table.appendRow(1,new SimpleDumpData("XmlType"),	new SimpleDumpData(XMLUtil.getTypeAsString(node,true)));
		
		return table;	
	}
	
	/**
	 * casts a value to a XML named Node Map
	 * @param doc XML Document
	 * @param o Object to cast
	 * @return XML named Node Map Object
	 */
	private static Node[] _toNodeArray(Document doc,Object o) {
		if(o instanceof Node) return new Node[]{(Node)o};
	// Node[]
		if(o instanceof Node[]) return ((Node[])o);
	// NamedNodeMap
		else if(o instanceof NamedNodeMap) {
			NamedNodeMap map=(NamedNodeMap)o;
			int len=map.getLength();
			Node[] nodes=new Node[len];
			for(int i=0;i<len;i++) {
				nodes[i]=map.item(i);
			}
			return nodes;
		}
	// XMLAttributes
		else if(o instanceof XMLAttributes) {
			return _toNodeArray(doc, ((XMLAttributes)o).toNamedNodeMap());
		}
	// NodeList
		else if(o instanceof NodeList)  {
			NodeList list=(NodeList)o;
			int len=list.getLength();
			Node[] nodes=new Node[len];
			for(int i=0;i<nodes.length;i++) {
				nodes[i]=list.item(i);
			}
			return nodes;
		}
		return null;
	}
	
	/**
	 * Check if all Node are of the type defnined by para,meter 
	 * @param nodes nodes to check
	 * @param type to compare
	 * @return are all of the same type
	 */
	private static boolean _isAllOfSameType(Node[] nodes, short type) {
		for(int i=0;i<nodes.length;i++) {
			if(nodes[i].getNodeType()!=type)return false;
		}
		return true;
	}

	/**
	 * creates a XMLCollection Object from a Node
	 * @param node
	 * @param caseSensitive
	 * @return xmlstruct from node
	 */
	public static XMLStruct toXMLStruct(Node node, boolean caseSensitive) {
	    return XMLStructFactory.newInstance(node,caseSensitive);
	}
	
	public static Element toRawElement(Object value, Element defaultValue) {
		if(value instanceof Node) {
			Node node=XMLCaster.toRawNode((Node) value);
			if(node instanceof Document) return ((Document) node).getDocumentElement();
			if(node instanceof Element) return (Element) node;
			return defaultValue;
		}
		try {
			return XMLUtil.parse(new InputSource(new StringReader(Caster.toString(value))),null,false).getDocumentElement();
		} catch (Throwable t) {
			return defaultValue;
		}
	}

}