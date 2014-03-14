package railo.transformer.bytecode.statement.tag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.statement.IFunction;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.udf.Function;
import railo.transformer.bytecode.statement.udf.FunctionImpl;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.literal.Literal;
 
public final class TagFunction extends TagBase implements IFunction {
	
	public TagFunction(Factory f,Position start,Position end) {
		super(f,start,end);
		
	}
	
	@Override
	public void writeOut(BytecodeContext bc, int type) throws TransformerException {
    	//ExpressionUtil.visitLine(bc, getStartLine());
    	_writeOut(bc,type);
    	//ExpressionUtil.visitLine(bc, getEndLine());
	}
	
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		_writeOut(bc,Function.PAGE_TYPE_REGULAR);
	}

	public void _writeOut(BytecodeContext bc, int type) throws TransformerException {

		//private static final Expression EMPTY = LitString.toExprString("");
		
		Body functionBody = new BodyBase(bc.getFactory());
		Function func = createFunction(bc.getPage(),functionBody);
		func.setParent(getParent());

		List<Statement> statements = getBody().getStatements();
		Statement stat;
		Tag tag;
		
		// suppress WS between cffunction and the last cfargument
		Tag last=null;
		if(bc.getSupressWSbeforeArg()){
			// check if there is a cfargument at all
			Iterator<Statement> it = statements.iterator();
			while (it.hasNext()) {
				stat = it.next();
				if (stat instanceof Tag) {
					tag = (Tag) stat;
					if (tag.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Argument")) {
						last=tag;
					}
				}
			}
			
			// check if there are only literal WS printouts
			if(last!=null) {
				it = statements.iterator();
				while (it.hasNext()) {
					stat = it.next();
					if(stat==last) break;
					
					if(stat instanceof PrintOut){
						PrintOut po=(PrintOut) stat;
						Expression expr = po.getExpr();
						if(!(expr instanceof LitString) || !StringUtil.isWhiteSpace(((LitString)expr).getString())) {
							last=null;
							break;
						}
					}
				}
			}
		}
		
		
		
		Iterator<Statement> it = statements.iterator();
		boolean beforeLastArgument=last!=null;
		while (it.hasNext()) {
			stat = it.next();
			if(beforeLastArgument) {
				if(stat==last) {
					beforeLastArgument=false;
				}
				else if(stat instanceof PrintOut){
					PrintOut po=(PrintOut) stat;
					Expression expr = po.getExpr();
					if(expr instanceof LitString) {
						LitString ls=(LitString) expr;
						if(StringUtil.isWhiteSpace(ls.getString())) continue;
					}
				}
				
			}
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
		func._writeOut(bc,type);

	}

	private void addArgument(Function func, Tag tag) {
		Attribute attr;
		// name
		Expression name = tag.removeAttribute("name").getValue();
		
		// type
		attr = tag.removeAttribute("type");
		Expression type = (attr == null) ? tag.getFactory().createLitString("any") : attr.getValue();

		// required
		attr = tag.removeAttribute("required");
		Expression required = (attr == null) ? tag.getFactory().FALSE() : attr
				.getValue();

		// default
		attr = tag.removeAttribute("default");
		Expression defaultValue = (attr == null) ? null : attr.getValue();
		
		// passby
		attr = tag.removeAttribute("passby");
		LitBoolean passByReference = tag.getFactory().TRUE();
		if(attr!=null) {
			// i can cast irt to LitString because he evulator check this before
			 String str = ((LitString)attr.getValue()).getString();
			 if(str.trim().equalsIgnoreCase("value"))
				 passByReference=tag.getFactory().FALSE();
		}
		
		
		// displayname
		attr = tag.removeAttribute("displayname");
		Expression displayName = (attr == null) ? tag.getFactory().EMPTY() : attr.getValue();

		// hint
		attr = tag.removeAttribute("hint");
		if (attr == null)
			attr = tag.removeAttribute("description");
		
		Expression hint;
		if(attr == null)hint=tag.getFactory().EMPTY();
		else hint=attr.getValue();
		
		func.addArgument(name, type, required, defaultValue, passByReference,displayName, hint,tag.getAttributes());

	}

	private Function createFunction(Page page, Body body) throws TransformerException {
		Attribute attr;
		LitString ANY = body.getFactory().createLitString("any");
		LitString PUBLIC = body.getFactory().createLitString("public");
		
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
		Expression output = (attr == null) ? page.getFactory().FALSE() : attr.getValue();
		
		// bufferOutput
		attr = removeAttribute("bufferoutput");
		Expression bufferOutput = (attr == null) ? null : attr.getValue();

		// modifier
		boolean _abstract=false,_final=false;
		attr = removeAttribute("modifier");
		if(attr!=null) {
			Expression val = attr.getValue();
			if(val instanceof Literal) {
				Literal l=(Literal) val;
				String str = StringUtil.emptyIfNull(l.getString()).trim();
				if("abstract".equalsIgnoreCase(str))_abstract=true;
				else if("final".equalsIgnoreCase(str))_final=true;
			}
		}

		// access
		attr = removeAttribute("access");
		Expression access = (attr == null) ? PUBLIC : attr.getValue();

		// dspLabel
		attr = removeAttribute("displayname");
		Expression displayname = (attr == null) ? body.getFactory().EMPTY() : attr.getValue();

		// hint
		attr = removeAttribute("hint");
		Expression hint = (attr == null) ? body.getFactory().EMPTY() : attr.getValue();

		// description
		attr = removeAttribute("description");
		Expression description = (attr == null) ? body.getFactory().EMPTY() : attr.getValue();

		// returnformat
		attr = removeAttribute("returnformat");
		Expression returnFormat = (attr == null) ? null : attr.getValue();

		// secureJson
		attr = removeAttribute("securejson");
		Expression secureJson = (attr == null) ? null : attr.getValue();

		// verifyClient
		attr = removeAttribute("verifyclient");
		Expression verifyClient = (attr == null) ? null : attr.getValue();

		// localMode
		attr = removeAttribute("localmode");
		Expression localMode = (attr == null) ? null : attr.getValue();
		
		
		
		// cachedWithin
		Literal cachedWithin=null;
		attr = removeAttribute("cachedwithin");
		if(attr!=null) {
			Expression val = attr.getValue();
			if(val instanceof Literal)
				cachedWithin=((Literal)val);
		}
		String strAccess = ((LitString)access).getString();
		int acc = ComponentUtil.toIntAccess(strAccess,-1);
		if(acc==-1)
			throw new TransformerException("invalid access type ["+strAccess+"], access types are remote, public, package, private",getStart());
        
		Function func = new FunctionImpl(page,name, returnType,returnFormat, output, bufferOutput, acc, displayname,description,
				hint,secureJson,verifyClient,localMode,cachedWithin,_abstract,_final, body, getStart(),getEnd());
		 
		
		
		
//		 %**%
		Map attrs = getAttributes();
		Iterator it = attrs.entrySet().iterator();
		HashMap<String,Attribute> metadatas=new HashMap<String,Attribute>();
		while(it.hasNext()){
			attr=(Attribute) ((Map.Entry)it.next()).getValue();
			metadatas.put(attr.getName(),attr);
		}
		func.setMetaData(metadatas);
		return func;
	}
	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
