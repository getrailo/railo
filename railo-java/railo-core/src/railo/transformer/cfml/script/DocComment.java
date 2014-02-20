package railo.transformer.cfml.script;

import java.util.HashMap;
import java.util.Map;

import railo.transformer.Factory;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.expression.literal.Literal;

public class DocComment {

	private StringBuilder tmpHint=new StringBuilder();
	private String hint;
	//private List<DocCommentParam> params=new ArrayList<DocComment.DocCommentParam>();
	Map<String,Attribute> params=new HashMap<String, Attribute>();
	
	public void addHint(char c){
		tmpHint.append(c);
	}
	public void addParam(Attribute attribute){
		params.put(attribute.getName(),attribute);
	}
	
	/**
	 * @return the hint
	 */
	public String getHint() {
		if(hint==null) {
			Attribute attr = params.remove("hint");
			if(attr!=null) {
				Literal lit=(Literal) attr.getValue();
				hint=lit.getString().trim();
			}
			else {
				hint=DocCommentTransformer.unwrap(tmpHint.toString());
			}
		}
		return hint;
	}

	public Attribute getHintAsAttribute(Factory factory) {
		return new Attribute(true,"hint",factory.createLitString(getHint()),"string");
	}
	
	
	
	/**
	 * @return the params
	 */
	public Map<String,Attribute> getParams() {
		return params;
	}
}
