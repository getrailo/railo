package railo.transformer.bytecode.statement.tag;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.visitor.ParseBodyVisitor;
import railo.transformer.library.tag.TagLibTag;

/**
 * 
 */
public class TagBase extends StatementBase implements Tag {

	private Body body=null;
	private String appendix;
	private String fullname;
	private TagLibTag tagLibTag;
	Map attributes=new LinkedHashMap();
	Map missingAttributes=new HashMap();


	public TagBase(int startLine,int endLine) {
    	super(startLine,endLine);
	}
	public TagBase(int startLine) {
    	super(startLine,-1);
	}

    
	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getAppendix()
	 */
	public String getAppendix() {
		return appendix;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getAttributes()
	 */
	public Map getAttributes() {
		return attributes;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.Tag#getFullname()
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getTagLibTag()
	 */
	public TagLibTag getTagLibTag() {
		return tagLibTag;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#setAppendix(java.lang.String)
	 */
	public void setAppendix(String appendix) {
		this.appendix=appendix;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#setFullname(java.lang.String)
	 */
	public void setFullname(String fullname) {
		this.fullname=fullname;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#setTagLibTag(railo.transformer.library.tag.TagLibTag)
	 */
	public void setTagLibTag(TagLibTag tagLibTag) {
		this.tagLibTag=tagLibTag;
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#addAttribute(railo.transformer.bytecode.statement.tag.Attribute)
	 */
	public void addAttribute(Attribute attribute) {
		attributes.put(attribute.getName().toLowerCase(), attribute);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.Tag#containsAttribute(java.lang.String)
	 */
	public boolean containsAttribute(String name) {
		return attributes.containsKey(name.toLowerCase());
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.Tag#getBody()
	 */
	public Body getBody() {
		return body;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.Tag#setBody(railo.transformer.bytecode.Body)
	 */
	public void setBody(Body body) {
		this.body = body;
		body.setParent(this);
	}


	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		boolean output=tagLibTag.getParseBody() || Caster.toBooleanValue(getAttribute("output"), false);
		
		if(output) {
			ParseBodyVisitor pbv=new ParseBodyVisitor();
			pbv.visitBegin(bc);
				_writeOutTag(bc);
			pbv.visitEnd(bc);
			
			
		}
		else _writeOutTag(bc);
	}

	private void _writeOutTag(BytecodeContext bc) throws BytecodeException {
		TagOther.writeOut(this,bc);
		
	}

	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getAttribute(java.lang.String)
	 */
	public Attribute getAttribute(String name) {
		return (Attribute) attributes.get(name.toLowerCase());
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.Tag#removeAttribute(java.lang.String)
	 */
	public Attribute removeAttribute(String name) {
		return (Attribute) attributes.remove(name);
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return appendix+":"+fullname+":"+super.toString();
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#addMissingAttribute(java.lang.String, java.lang.String)
	 */
	public void addMissingAttribute(String name, String type) {
		missingAttributes.put(name, type);
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getMissingAttributes()
	 */
	public Map getMissingAttributes() {
		return missingAttributes;
	}
	
}
