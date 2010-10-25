package railo.transformer.library.tag;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.Md5;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageRuntimeException;
import railo.transformer.cfml.ExprTransformer;


/**
 * Die Klasse TagLib rep�sentiert eine Komplette TLD, 
 * mit ihrer Hilfe kann man alle Informationen, zu einer TLD Abfragen. 
 */
public class TagLib implements Cloneable {
    /**
     * Field <code>EXPR_TRANSFORMER</code>
     */
    public static String EXPR_TRANSFORMER="railo.transformer.cfml.expression.CFMLExprTransformer";
	
    private String shortName="";
    private String displayName=null;
    private String type="cfml";
    private String nameSpace;
	private String nameSpaceSeperator=":";
	private String ELClass=EXPR_TRANSFORMER;
	private HashMap tags=new HashMap();
	private HashMap appendixTags=new HashMap();
	private ExprTransformer exprTransformer;

    private char[] nameSpaceAndNameSpaceSeperator;

	private boolean isCore;

	private String source;

	private URI uri;

	private String description;

	
	/**
	 * Gesch�tzer Konstruktor ohne Argumente.
	 */
	protected TagLib(boolean isCore) {
		this.isCore=isCore;
	}
	protected TagLib() {
		this(false);
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * Gibt den Name-Space einer TLD als String zur�ck.
	 * @return String Name der TLD.
	 */
	public String getNameSpace() {
        
		return nameSpace;
	}

	/**
	 * Gibt den Trenner zwischen Name-Space und Name einer TLD zur�ck.
	 * @return Name zwischen Name-Space und Name.
	 */
	public String getNameSpaceSeparator() {
		return nameSpaceSeperator;
	}
    
    /**
     * Gibt den Name-Space inkl. dem Seperator zur�ck.
     * @return String
     */
    public String getNameSpaceAndSeparator() {
        return nameSpace+nameSpaceSeperator;
    }
    
    /**
     * Gibt den Name-Space inkl. dem Seperator zur�ck.
     * @return String
     */
    public char[] getNameSpaceAndSeperatorAsCharArray() {
        if(nameSpaceAndNameSpaceSeperator==null) {
            nameSpaceAndNameSpaceSeperator=getNameSpaceAndSeparator().toCharArray();
        }
        return nameSpaceAndNameSpaceSeperator;
    }
	
	/**
	 * Gibt einen Tag (TagLibTag)zur�ck, dessen Name mit dem �bergebenen Wert �bereinstimmt,
	 * falls keine �bereinstimmung gefunden wird, wird null zur�ck gegeben.
	 * @param name Name des Tag das zur�ck gegeben werden soll.
	 * @return TagLibTag Tag das auf den Namen passt.
	 */
	public TagLibTag getTag(String name)	{
		return (TagLibTag)tags.get(name);
	}
	
	/**
	 * Gibt einen Tag (TagLibTag)zur�ck, welches definiert hat, dass es einen Appendix besitzt.
	 * D.h. dass der Name des Tag mit weiteren Buchstaben erweitert sein kann, 
	 * also muss nur der erste Teil des Namen vom Tag mit dem �bergebenen Namen �bereinstimmen.
	 * Wenn keine �bereinstimmung gefunden wird, wird null zur�ck gegeben.
	 * @param name Name des Tag inkl. Appendix das zur�ck gegeben werden soll.
	 * @return TagLibTag Tag das auf den Namen passt.
	 */
	public TagLibTag getAppendixTag(String name)	{
		Iterator it=appendixTags.keySet().iterator();
		String match="";
		while(it.hasNext()) {
			String tagName=it.next().toString();
			if(match.length()<tagName.length() && name.indexOf(tagName)==0) {
				match=tagName;
			}
		}
		return (match==null)?null:getTag(match);
	}
	
	/**
	 * Gibt alle Tags (TagLibTag) als HashMap zur�ck.
	 * @return Alle Tags als HashMap.
	 */
	public Map getTags() {
		return tags;
	}

	/**
	 * Gibt die Klasse des ExprTransformer als Zeichenkette zur�ck.
	 * @return String
	 */
	public String getELClass() {
		return ELClass;
	}

	/**
	 * L�dt den innerhalb der TagLib definierten ExprTransfomer und gibt diesen zur�ck.
	 * Load Expression Transfomer defined in the tag library and return it.
	 * @return ExprTransformer
	 * @throws TagLibException
	 */
	public ExprTransformer getExprTransfomer() throws TagLibException {
		//Class cls;
		if(exprTransformer!=null)
			return exprTransformer;
		
		try {
			exprTransformer =  (ExprTransformer)ClassUtil.loadInstance(ELClass);
			//exprTransformer = (ExprTransformer) cls.newInstance();
		} 
		catch (ClassException e) {
			throw new TagLibException(e);
		}
		return exprTransformer;
	}
	
	/**
	 * F�gt der TagLib einen weiteren Tag hinzu.
	 * Diese Methode wird durch die Klasse TagLibFactory verwendet.
	 * @param tag Neuer Tag.
	 */
	public void setTag(TagLibTag tag)	{
		tag.setTagLib(this);
		tags.put(tag.getName(),tag);
		
		if(tag.hasAppendix())
			appendixTags.put(tag.getName(),tag);
		else if(appendixTags.containsKey(tag.getName()))
			appendixTags.remove(tag.getName());
	}
	
	/**
	 * F�gt der TagLib die Evaluator Klassendefinition als Zeichenkette hinzu.
	 * Diese Methode wird durch die Klasse TagLibFactory verwendet.
	 * @param eLClass Zeichenkette der Evaluator Klassendefinition.
	 */
	protected void setELClass(String eLClass) {
		ELClass = eLClass;
	}

	/**
	 * F�gt der TagLib die die Definition des Name-Space hinzu.
	 * Diese Methode wird durch die Klasse TagLibFactory verwendet.
	 * @param nameSpace Name-Space der TagLib.
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace.toLowerCase();
	}

	/**
	 * F�gt der TagLib die die Definition des Name-Space-Seperator hinzu.
	 * Diese Methode wird durch die Klasse TagLibFactory verwendet.
	 * @param nameSpaceSeperator Name-Space-Seperator der TagLib.
	 */
	public void setNameSpaceSeperator(String nameSpaceSeperator) {
		this.nameSpaceSeperator = nameSpaceSeperator;
	}

    /**
     * @return Returns the displayName.
     */
    public String getDisplayName() {
        if(displayName==null)return shortName;
        return displayName;
    }
    /**
     * @param displayName The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return shortName;
    }
    /**
     * @param shortName The shortName to set.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
        if(nameSpace==null)nameSpace=shortName.toLowerCase();
    }
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getDisplayName()+":"+getShortName()+":"+super.toString();
    }
    
    public String getHash() {
    	StringBuffer sb=new StringBuffer();
    	Iterator it = tags.keySet().iterator();
    	while(it.hasNext()) {
    		//"__filename"
    		
    		sb.append(((TagLibTag)tags.get(it.next())).getHash()+"\n");
    	}
    	try {
			return Md5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
    }

	public boolean isCore() {
		return isCore;
	}
	
	public void setIsCore(boolean isCore) {
		this.isCore=isCore;
	}
	


	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return duplicate(false);
	}

	/**
	 * duplicate the taglib, does not 
	 * @param deepCopy duplicate also the children (TagLibTag) of this TagLib
	 * @return clone of this taglib
	 */
	public TagLib duplicate(boolean deepCopy) {
		TagLib tl = new TagLib(isCore);
		tl.appendixTags=duplicate(this.appendixTags,deepCopy);
		tl.displayName=this.displayName;
		tl.ELClass=this.ELClass;
		tl.exprTransformer=this.exprTransformer;
		tl.isCore=this.isCore;
		tl.nameSpace=this.nameSpace;
		tl.nameSpaceAndNameSpaceSeperator=this.nameSpaceAndNameSpaceSeperator;
		tl.nameSpaceSeperator=this.nameSpaceSeperator;
		tl.shortName=this.shortName;
		tl.tags=duplicate(this.tags,deepCopy);
		tl.type=this.type;
		tl.source=this.source;
		
		return tl;
	}
	
	/**
	 * duplcate a hashmap with TagLibTag's
	 * @param tags
	 * @param deepCopy
	 * @return cloned map
	 */
	private HashMap duplicate(HashMap tags, boolean deepCopy) {
		if(deepCopy) throw new PageRuntimeException(new ExpressionException("deep copy not supported"));
		
		Iterator it = tags.entrySet().iterator();
		Map.Entry entry;
		HashMap cm = new HashMap();
		while(it.hasNext()){
			entry=(Entry) it.next();
			cm.put(
					entry.getKey(), 
					deepCopy?
							entry.getValue(): // TODO add support for deepcopy ((TagLibTag)entry.getValue()).duplicate(deepCopy):
							entry.getValue());
		}
		return cm;
	}
	public String getSource() {
		return source;
	}
	public URI getUri() {
		// TODO Auto-generated method stub
		return uri;
	}
	public void setUri(String strUri) throws URISyntaxException {
		this.uri=new URI(strUri);
	}
	public void setUri(URI uri) {
		this.uri=uri;
	}
	public void setDescription(String description) {
		this.description=description;
	}
	
	public String getDescription() {
		return description;
	}
	
}