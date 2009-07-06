package railo.transformer.bytecode.statement.tag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Function;
import railo.transformer.bytecode.statement.IFunction;

public final class TagFunction extends TagBase implements IFunction {

	private static final ExprString ANY = LitString.toExprString("any", -1);

	private static final Expression PUBLIC = LitString.toExprString("public",-1);

	private static final Expression EMPTY = LitString.toExprString("", -1);

	public TagFunction(int startline) {
		this(startline,-1);
	}
	
	public TagFunction(int startline,int endline) {
		super(startline,endline);
		
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.IFunction#writeOut(railo.transformer.bytecode.BytecodeContext, int)
	 */
	public void writeOut(BytecodeContext bc, int type) throws BytecodeException {
    	//ExpressionUtil.visitLine(bc, getStartLine());
    	_writeOut(bc,type);
    	//ExpressionUtil.visitLine(bc, getEndLine());
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		_writeOut(bc,Function.PAGE_TYPE_REGULAR);
	}

	public void _writeOut(BytecodeContext bc, int type) throws BytecodeException {
		Body functionBody = new BodyBase();
		Function func = createFunction(functionBody);
		func.setParent(getParent());

		List statements = getBody().getStatements();
		Statement stat;
		Tag tag;
		Iterator it = statements.iterator();
		while (it.hasNext()) {
			stat = (Statement) it.next();
			if (stat instanceof Tag) {
				tag = (Tag) stat;
				if (tag.getTagLibTag().getTagClassName().equals(
						"railo.runtime.tag.Argument")) {
					addArgument(func, tag);
					continue;
				}
			}
			functionBody.addStatement(stat);
		}
		;
		func._writeOut(bc,type);

	}

	private void addArgument(Function func, Tag tag) {
		Attribute attr;
		// name
		Expression name = tag.removeAttribute("name").getValue();
		//if(name instanceof LitString) {
			//print.out(((LitString)name).getString());
			//((LitString)name).upperCase();
		//}
		
		// type
		attr = tag.removeAttribute("type");
		Expression type = (attr == null) ? ANY : attr.getValue();

		// required
		attr = tag.removeAttribute("required");
		Expression required = (attr == null) ? LitBoolean.FALSE : attr
				.getValue();

		// default
		attr = tag.removeAttribute("default");
		Expression defaultValue = (attr == null) ? null : attr.getValue();
		
		// passby
		attr = tag.removeAttribute("passby");
		LitBoolean passByReference = LitBoolean.TRUE;
		if(attr!=null) {
			// i can cast irt to LitString because he evulator check this before
			 String str = ((LitString)attr.getValue()).getString();
			 if(str.trim().equalsIgnoreCase("value"))
				 passByReference=LitBoolean.FALSE;
		}
		
		
		// displayname
		attr = tag.removeAttribute("displayname");
		Expression displayName = (attr == null) ? EMPTY : attr.getValue();

		// hint
		attr = tag.removeAttribute("hint");
		if (attr == null)
			attr = tag.removeAttribute("description");
		Expression hint = (attr == null) ? EMPTY : attr.getValue();

		
		func.addArgument(name, type, required, defaultValue, passByReference,displayName, hint,tag.getAttributes());

	}

	private Function createFunction(Body body) {
		Attribute attr;

		// name
		Expression name = removeAttribute("name").getValue();
		/*if(name instanceof LitString) {
			((LitString)name).upperCase();
		}*/
		// return
		attr = removeAttribute("returntype");
		// if(attr==null) attr = getAttribute("return");
		// if(attr==null) attr = getAttribute("type");
		Expression returnType = (attr == null) ? ANY : attr.getValue();

		// output
		attr = removeAttribute("output");
		Expression output = (attr == null) ? LitBoolean.TRUE : attr.getValue();

		// abstract
		attr = removeAttribute("abstract");
		Expression abstr = (attr == null) ? LitBoolean.FALSE : attr.getValue();

		// access
		attr = removeAttribute("access");
		Expression access = (attr == null) ? PUBLIC : attr.getValue();

		// dspLabel
		attr = removeAttribute("displayname");
		Expression displayname = (attr == null) ? EMPTY : attr.getValue();

		// hint
		attr = removeAttribute("hint");
		Expression hint = (attr == null) ? EMPTY : attr.getValue();

		// description
		attr = removeAttribute("description");
		Expression description = (attr == null) ? EMPTY : attr.getValue();

		// returnformat
		attr = removeAttribute("returnformat");
		Expression returnFormat = (attr == null) ? null : attr.getValue();

		// secureJson
		attr = removeAttribute("securejson");
		Expression secureJson = (attr == null) ? null : attr.getValue();

		// verifyClient
		attr = removeAttribute("verifyclient");
		Expression verifyClient = (attr == null) ? null : attr.getValue();


		Function func = new Function(name, returnType,returnFormat, output,abstr, access, displayname,description,
				hint,secureJson,verifyClient, body, getStartLine(),getEndLine());
		
//		 %**%
		Map attrs = getAttributes();
		Iterator it = attrs.entrySet().iterator();
		HashMap metadatas=new HashMap();
		while(it.hasNext()){
			attr=(Attribute) ((Map.Entry)it.next()).getValue();
			metadatas.put(attr.getName(),attr);
		}
		func.setMetaData(metadatas);
		return func;
	}

}
