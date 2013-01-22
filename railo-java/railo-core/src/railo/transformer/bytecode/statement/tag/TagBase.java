package railo.transformer.bytecode.statement.tag;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import railo.runtime.op.Caster;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.StatementBase;
import railo.transformer.bytecode.visitor.ParseBodyVisitor;
import railo.transformer.library.tag.TagLibTag;

/**
 * 
 */
public abstract class TagBase extends StatementBase implements Tag {

	private Body body=null;
	private String appendix;
	private String fullname;
	private TagLibTag tagLibTag;
	Map<String,Attribute> attributes=new LinkedHashMap<String,Attribute>();
	Map<String,String> missingAttributes=new HashMap<String,String>();
	private boolean scriptBase=false;
	
	private Map<String, Attribute> metadata;
	//private Label finallyLabel;


	public TagBase(Position start, Position end) {
    	super(start,end);
	}

    
	/**
	 * @see railo.transformer.bytecode.statement.tag.Tag#getAppendix()
	 */
	public String getAppendix() {
		return appendix;
	}

	@Override
	public Map getAttributes() {
		return attributes;
	}

	@Override
	public String getFullname() {
		return fullname;
	}

	@Override
	public TagLibTag getTagLibTag() {
		return tagLibTag;
	}

	@Override
	public void setAppendix(String appendix) {
		this.appendix=appendix;
	}

	@Override
	public void setFullname(String fullname) {
		this.fullname=fullname;
	}

	@Override
	public void setTagLibTag(TagLibTag tagLibTag) {
		this.tagLibTag=tagLibTag;
	}

	@Override
	public void addAttribute(Attribute attribute) {
		attributes.put(attribute.getName().toLowerCase(), attribute);
	}

	@Override
	public boolean containsAttribute(String name) {
		return attributes.containsKey(name.toLowerCase());
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public void setBody(Body body) {
		this.body = body;
		body.setParent(this);
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		_writeOut(bc,true,null);
	}
	
	public void _writeOut(BytecodeContext bc, boolean doReuse) throws BytecodeException {
		_writeOut(bc,doReuse,null);
	}
	
	protected void _writeOut(BytecodeContext bc, boolean doReuse, final FlowControlFinal fcf) throws BytecodeException {
		//_writeOut(bc, true);
		boolean output=tagLibTag.getParseBody() || Caster.toBooleanValue(getAttribute("output"), false);
		
		if(output) {
			ParseBodyVisitor pbv=new ParseBodyVisitor();
			pbv.visitBegin(bc);
				TagHelper.writeOut(this,bc, doReuse,fcf);
			pbv.visitEnd(bc);
		}
		else TagHelper.writeOut(this,bc, doReuse,fcf);
	}
	
	@Override
	public Attribute getAttribute(String name) {
		return attributes.get(name.toLowerCase());
	}

	@Override
	public Attribute removeAttribute(String name) {
		return attributes.remove(name);
	}

	@Override
	public String toString() {
		return appendix+":"+fullname+":"+super.toString();
	}
	
	@Override
	public boolean isScriptBase() {
		return scriptBase;
	}
	
	@Override
	public void setScriptBase(boolean scriptBase) {
		this.scriptBase = scriptBase;
	}
	
	@Override
	public void addMissingAttribute(String name, String type) {
		missingAttributes.put(name, type);
	}
	
	@Override
	public Map getMissingAttributes() {
		return missingAttributes;
	}
	
	@Override
	public void addMetaData(Attribute metadata) {
		if(this.metadata==null) this.metadata=new HashMap<String, Attribute>();
		this.metadata.put(metadata.getName(), metadata);
	}
	
	@Override
	public Map<String, Attribute> getMetaData() {
		return metadata;
	}
}
