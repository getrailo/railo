package railo.runtime.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.SystemOut;
import railo.runtime.Info;

public abstract class ConfigFactory {
	static boolean doNew(Resource contextDir) {

		final boolean readonly = false;
		try {
			Resource version = contextDir.getRealResource("version");
			String v = Info.getVersionAsString() + "-" + Info.getStateAsString() + "-" + Info.getRealeaseTime();
			if (!version.exists()) {
				if (!readonly) {
					version.createNewFile();
					IOUtil.write(version, v, SystemUtil.getCharset(), false);
				}
				return true;
			}
			else if (!IOUtil.toString(version, SystemUtil.getCharset()).equals(v)) {
				if (!readonly)
					IOUtil.write(version, v, SystemUtil.getCharset(), false);

				return true;
			}
		}
		catch (Throwable t) {
		}
		return false;
	}

	/**
	 * load XML Document from XML File
	 * 
	 * @param xmlFile
	 *            XML File to read
	 * @return returns the Document
	 * @throws SAXException
	 * @throws IOException
	 */
	static Document loadDocument(Resource xmlFile) throws SAXException, IOException {

		InputStream is = null;
		try {
			return _loadDocument(is = IOUtil.toBufferedInputStream(xmlFile.getInputStream()));
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	/**
	 * load XML Document from XML File
	 * 
	 * @param is
	 *            InoutStream to read
	 * @return returns the Document
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Document _loadDocument(InputStream is) throws SAXException, IOException {
		DOMParser parser = new DOMParser();
		InputSource source = new InputSource(is);
		parser.parse(source);
		is.close();
		return parser.getDocument();
	}
	

	/**
	 * return first direct child Elements of a Element with given Name
	 * 
	 * @param parent
	 * @param nodeName
	 * @return matching children
	 */
	public static Element getChildByName(Node parent, String nodeName) {
		return getChildByName(parent, nodeName, false);
	}

	public static Element getChildByName(Node parent, String nodeName, boolean insertBefore) {
		return getChildByName(parent, nodeName, insertBefore, false);
	}

	public static Element getChildByName(Node parent, String nodeName, boolean insertBefore, boolean doNotCreate) {
		if (parent == null)
			return null;
		NodeList list = parent.getChildNodes();
		int len = list.getLength();

		for (int i = 0; i < len; i++) {
			Node node = list.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase(nodeName)) {
				return (Element) node;
			}
		}
		if (doNotCreate)
			return null;

		Element newEl = parent.getOwnerDocument().createElement(nodeName);
		if (insertBefore)
			parent.insertBefore(newEl, parent.getFirstChild());
		else
			parent.appendChild(newEl);

		return newEl;
	}

	/**
	 * return all direct child Elements of a Element with given Name
	 * 
	 * @param parent
	 * @param nodeName
	 * @return matching children
	 */
	public static Element[] getChildren(Node parent, String nodeName) {
		if (parent == null)
			return new Element[0];
		NodeList list = parent.getChildNodes();
		int len = list.getLength();
		ArrayList<Element> rtn = new ArrayList<Element>();

		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase(nodeName)) {
				rtn.add((Element)node);
			}
		}
		return rtn.toArray(new Element[rtn.size()]);
	}

	/**
	 * creates a File and his content froma a resurce
	 * 
	 * @param resource
	 * @param file
	 * @param password
	 * @throws IOException
	 */
	static void createFileFromResource(String resource, Resource file, String password) throws IOException {
		SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT), "write file:" + file);
		file.createNewFile();
		IOUtil.copy(new Info().getClass().getResourceAsStream(resource), file, true);
	}
	

	/**
	 * creates a File and his content froma a resurce
	 * 
	 * @param resource
	 * @param file
	 * @throws IOException
	 */
	static void createFileFromResource(String resource, Resource file) throws IOException {
		createFileFromResource(resource, file, null);
	}

	public static void createFileFromResourceEL(String resource, Resource file) {
		try {
			createFileFromResource(resource, file, null);
		}
		catch (Throwable e) {
			SystemOut.printDate(e.toString(), SystemUtil.ERR);
		}
	}

	static void create(String srcPath, String[] names, Resource dir, boolean doNew) {
		for(int i=0;i<names.length;i++){
			create(srcPath, names[i], dir, doNew);
		}
	}
		
	static Resource create(String srcPath, String name, Resource dir, boolean doNew) {
		if(!dir.exists())dir.mkdirs();
		
		Resource f = dir.getRealResource(name);
		if (!f.exists() || doNew)
			ConfigWebFactory.createFileFromResourceEL(srcPath+name, f);
		return f;
		
	}

	static void delete(Resource dbDir, String[] names) {
		for(int i=0;i<names.length;i++){
			delete(dbDir, names[i]);
		}
	}

	static void delete(Resource dbDir, String name) {
		Resource f = dbDir.getRealResource(name);
		if (f.exists()) f.delete();
		
	}
	
}
