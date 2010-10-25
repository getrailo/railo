package railo.runtime.text.xml.struct;

import java.lang.reflect.Method;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.util.ArrayUtil;

/**
 * 
 */
public final class XMLCDATASectionStruct extends XMLNodeStruct implements CDATASection {

    private CDATASection section;

    /**
     * constructor of the class
     * @param section
     * @param caseSensitive
     */
    public XMLCDATASectionStruct(CDATASection section, boolean caseSensitive) {
        super(section,caseSensitive);
        this.section=section;
    }

    /**
     * @see org.w3c.dom.Text#splitText(int)
     */
    public Text splitText(int offset) throws DOMException {
        return section.splitText(offset);
    }

    /**
     * @see org.w3c.dom.CharacterData#getLength()
     */
    public int getLength() {
        return section.getLength();
    }

    /**
     * @see org.w3c.dom.CharacterData#deleteData(int, int)
     */
    public void deleteData(int offset, int count) throws DOMException {
        section.deleteData(offset,count);
    }

    /**
     * @see org.w3c.dom.CharacterData#getData()
     */
    public String getData() throws DOMException {
        return section.getData();
    }

    /**
     * @see org.w3c.dom.CharacterData#substringData(int, int)
     */
    public String substringData(int offset, int count) throws DOMException {
        return section.substringData(offset,count);
    }

    /**
     * @see org.w3c.dom.CharacterData#replaceData(int, int, java.lang.String)
     */
    public void replaceData(int offset, int count, String arg)
            throws DOMException {
        section.replaceData(offset,count,arg);
    }

    /**
     * @see org.w3c.dom.CharacterData#insertData(int, java.lang.String)
     */
    public void insertData(int offset, String arg) throws DOMException {
        section.insertData(offset,arg);
    }

    /**
     * @see org.w3c.dom.CharacterData#appendData(java.lang.String)
     */
    public void appendData(String arg) throws DOMException {
        section.appendData(arg);
    }

    /**
     *
     * @see org.w3c.dom.CharacterData#setData(java.lang.String)
     */
    public void setData(String data) throws DOMException {
        section.setData(data);
    }

	/**
	 *
	 * @see org.w3c.dom.Text#getWholeText()
	 */
	public String getWholeText() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = section.getClass().getMethod("getWholeText", new Class[]{});
			return Caster.toString(m.invoke(section, ArrayUtil.OBJECT_EMPTY));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Text#isElementContentWhitespace()
	 */
	public boolean isElementContentWhitespace() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = section.getClass().getMethod("isElementContentWhitespace", new Class[]{});
			return Caster.toBooleanValue(m.invoke(section, ArrayUtil.OBJECT_EMPTY));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Text#replaceWholeText(java.lang.String)
	 */
	public Text replaceWholeText(String arg0) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = section.getClass().getMethod("replaceWholeText", new Class[]{arg0.getClass()});
			return (Text)m.invoke(section, new Object[]{arg0});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}
	

	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new XMLCDATASectionStruct((CDATASection)section.cloneNode(deepCopy),caseSensitive);
	}
	

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		return new XMLCDATASectionStruct((CDATASection)section.cloneNode(deep),caseSensitive);
	}

}