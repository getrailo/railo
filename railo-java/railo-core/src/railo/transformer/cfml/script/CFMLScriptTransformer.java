package railo.transformer.cfml.script;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Component;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.exp.TemplateException;
import railo.runtime.functions.system.CFFunction;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.FunctionBody;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Condition;
import railo.transformer.bytecode.statement.DoWhile;
import railo.transformer.bytecode.statement.ExpressionStatement;
import railo.transformer.bytecode.statement.For;
import railo.transformer.bytecode.statement.ForEach;
import railo.transformer.bytecode.statement.Function;
import railo.transformer.bytecode.statement.Return;
import railo.transformer.bytecode.statement.Switch;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.statement.While;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagBase;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.evaluator.impl.ProcessingDirectiveException;
import railo.transformer.cfml.expression.CFMLExprTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.library.tag.TagLibTagScript;
import railo.transformer.util.CFMLString;


/**	
 * Innerhalb des Tag script kann in Cold Fusion eine eigene Scriptsprache verwendet werden, 
 * welche sich an Javascript orientiert. 
 * Da der data.cfml Transformer keine Spezialfälle zulässt, 
 * also Tags einfach anhand der eingegeben TLD einliest und transformiert, 
 * aus diesem Grund wird der Inhalt des Tag script einfach als Zeichenkette eingelesen.
 * Erst durch den Evaluator (siehe 3.3), der für das Tag script definiert ist, 
 * wird der Inhalt des Tag script übersetzt.
 * 
 */
public final class CFMLScriptTransformer extends CFMLExprTransformer implements TagDependentBodyTransformer {


	private short ATTR_TYPE_NONE=TagLibTagAttr.SCRIPT_SUPPORT_NONE;
	private short ATTR_TYPE_OPTIONAL=TagLibTagAttr.SCRIPT_SUPPORT_OPTIONAL;
	private short ATTR_TYPE_REQUIRED=TagLibTagAttr.SCRIPT_SUPPORT_REQUIRED;
	
	public static class ComponentTemplateException extends TemplateException {
		private static final long serialVersionUID = -8103635220891288231L;
		
		private TemplateException te;

		public ComponentTemplateException(TemplateException te){
			super(te.getPageSource(),te.getLine(),0,te.getMessage());
			this.te=te;
			
		}

		/**
		 * @return the te
		 */
		public TemplateException getTemplateException() {
			return te;
		}
	}

	private static final Expression NULL = LitString.toExprString("NULL"); 
	private static final Expression EMPTY_STRING = LitString.toExprString("sadsdasdasdadasdasdsas");
	private static final Attribute ANY = new Attribute(false,"type",LitString.toExprString("any"),"string"); 

	/**
	 * Einstiegsmethode für den CFScript Transformer, 
	 * die Methode erbt sich von der Transform Methode der data.cfmlExprTransformer Klasse.
	 * Der einzige Unterschied liegt darin, das der data.cfmlString der eingegeben wird als vererbte Klasse CFScriptString vorliegen muss.
	 * Der Parameter ist als data.cfmlString definiert, so dass er die transform Methode überschreibt.
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des data.cfml Codes erkennen 
	 * und validieren.
	 * <br />
	 * EBNF:<br />
	 * <code>statements;</code>
	 * @param cfxdTag XML Document des aktuellen zu erstellenden CFXD
	 * @param libTag Definition des aktuellen Tag.
	 * @param data.cfml data.cfml Code 
	 * @param parentTransformer
	 * @throws TemplateException
	 */

	public void transform(Config config,CFMLTransformer parentTransformer,EvaluatorPool ep,FunctionLib[] fld, Tag tag,TagLibTag libTag, CFMLString cfml) throws TemplateException	{
		Page page = ASMUtil.getAncestorPage(tag);
		boolean isCFC= page.isComponent();
		boolean isInterface= page.isInterface();
		
		Data data = init(ep,fld,cfml,true);
		data.insideFunction=false; 
		data.tagName=libTag.getFullName();
		data.isCFC=isCFC;
		data.isInterface=isInterface;
		data.scriptTags=((ConfigImpl) config).getCoreTagLib().getScriptTags();
		
		tag.setBody(statements(data));
	}

	/**
	 * @throws TemplateException 
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#transform(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.data.cfmlString)
	 */
	public Expression transform(FunctionLib[] fld,CFMLString cfml) throws TemplateException {// FUTURE is this method needed anymore?
		throw new TemplateException(cfml,"you can't use Method transform on class CFMLScriptTransformer");
	}
	/**
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.data.cfmlString)
	 */
	public Expression transformAsString(FunctionLib[] fld,CFMLString cfml, boolean allowLowerThan) throws TemplateException {// FUTURE is this method needed anymore?
		throw new TemplateException(cfml,"you can't use Method transformAsString on class CFMLScriptTransformer");
	}
	
	
	/** 
	 * Liest sämtliche Statements des CFScriptString ein. 
	 * <br />
	 * EBNF:<br />
	 * <code>{statement spaces};</code>
	 * @return a statement
	 * @throws TemplateException
	 */
	protected Body statements(Data data) throws TemplateException {
		ScriptBody body=new ScriptBody();
		
		statements(data,body,true);
	return body;
	}
	
	/**
	 * Liest sämtliche Statements des CFScriptString ein. 
	 * <br />
	 * EBNF:<br />
	 * <code>{statement spaces};</code>
	 * @param parent Übergeornetes Element dem das Statement zugewiesen wird.
	 * @param isRoot befindet sich der Parser im root des data.cfml Docs
	 * @throws TemplateException
	 */
	protected void statements(Data data,Body body, boolean isRoot) throws TemplateException {
		do {
			if(isRoot && isFinish(data))return;
			statement(data,body);
			comments(data.cfml);
		}
		while(data.cfml.isValidIndex() && !data.cfml.isCurrent('}'));
	}
	
	/** 
	 * Liest ein einzelnes Statement ein (if,for,while usw.).
	 * <br />
	 * EBNF:<br />
	 * <code>";" | "if" spaces "(" ifStatement | "function " funcStatement |  "while" spaces "(" whileStatement  |  
			  "do" spaces "{" doStatement  | "for" spaces "(" forStatement | "return" returnStatement | 
			  "break" breakStatement | "continue" continueStatement | "/*" comment | expressionStatement;</code>
	 * @param parent Übergeornetes Element dem das Statement zugewiesen wird.
	 * @throws TemplateException
	 */
	protected void statement(Data data,Body parent) throws TemplateException {
		statement(data, parent, data.context);
	}
	private void statement(Data data,Body parent,short context) throws TemplateException {
		short prior=data.context;
		data.context=context;
		comments(data.cfml);
		Statement child=null;
		if(data.cfml.forwardIfCurrent(';')){}
		else if((child=ifStatement(data))!=null) 				parent.addStatement(child);
		else if((child=propertyStatement(data,parent))!=null)	parent.addStatement(child);
		else if((child=funcStatement(data,parent))!=null)		parent.addStatement(child);
		else if((child=whileStatement(data))!=null) 			parent.addStatement(child);
		else if((child=doStatement(data))!=null) 				parent.addStatement(child);
		else if((child=forStatement(data))!=null) 				parent.addStatement(child);
		else if((child=returnStatement(data))!=null) 			parent.addStatement(child);
		else if((child=switchStatement(data))!=null) 			parent.addStatement(child);
		else if((child=tryStatement(data))!=null) 				parent.addStatement(child);
		else if((child=tagStatement(data,parent))!=null)	parent.addStatement(child);
		else if(block(data,parent)){}
		else parent.addStatement(expressionStatement(data));
		data.context=prior;
	}
	
	/**
	 * Liest ein if Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces condition spaces ")" spaces block {"else if" spaces "(" elseifStatement spaces }
			 [("else"  spaces "(" | "else ") elseStatement spaces];</code>
	 * @return if Statement
	 * @throws TemplateException
	 */
	protected Statement ifStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("if",'(')) return null;
		
		
		int line=data.cfml.getLine();
		
		Body body=new BodyBase();
		Condition cont=new Condition(condition(data),body,line);
		
		if(!data.cfml.forwardIfCurrent(')')) throw new TemplateException(data.cfml,"if statement must end with a [)]");
		// ex block
		statement(data,body,CTX_IF);
		// else if
		comments(data.cfml);
		while(elseifStatement(data,cont)) {
			comments(data.cfml);
		}
		// else
		 if(elseStatement(data,cont)) {
			comments(data.cfml);
		 }
		
		return cont;
	}
	
	/**
	 * Liest ein else if Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces condition spaces ")" spaces block;</code>
	 * @return else if Statement
	 * @throws TemplateException
	 */
	protected boolean elseifStatement(Data data,Condition cont) throws TemplateException {
		int pos=data.cfml.getPos();
		if(!data.cfml.forwardIfCurrent("else")) return false;
		
		comments(data.cfml);
		if(!data.cfml.forwardIfCurrent("if",'(')) {
			data.cfml.setPos(pos);
			return false;
		}
			
		int line=data.cfml.getLine();
		Body body=new BodyBase();
		cont.addElseIf(condition(data), body, line);

		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"else if statement must end with a [)]");
		// ex block
		statement(data,body,CTX_ELSE_IF);
		
		return true;
	}
	
	/**
	 * Liest ein else Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>block;</code>
	 * @return else Statement
	 * @throws TemplateException
	 * 
	 */
	protected boolean elseStatement(Data data,Condition cont) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("else",'{') && !data.cfml.forwardIfCurrent("else ") && !data.cfml.forwardIfCurrent("else",'/')) 
			return false;

		// start (
		data.cfml.previous();
		// ex block
		Body body=new BodyBase();
		cont.setElse(body, data.cfml.getLine());
		statement(data,body,CTX_ELSE);
		
		return true;
	}
	

	protected boolean finallyStatement(Data data,TryCatchFinally tcf) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("finally",'{') && !data.cfml.forwardIfCurrent("finally ") && !data.cfml.forwardIfCurrent("finally",'/')) 
			return false;

		// start (
		data.cfml.previous();
		// ex block
		Body body=new BodyBase();
		tcf.setFinally(body, data.cfml.getLine());
		statement(data,body,CTX_FINALLY);
		
		return true;
	}
	
	/**
	 * Liest ein while Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces condition spaces ")" spaces block;</code>
	 * @return while Statement
	 * @throws TemplateException
	 */
	public While whileStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("while",'('))
			return null;
		
		int line=data.cfml.getLine();
		Body body=new BodyBase();
		While whil=new While(condition(data),body,line,-1);
		
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"while statement must end with a [)]");
		
		statement(data,body,CTX_WHILE);
		whil.setEndLine(data.cfml.getLine());
		return whil;
	}
	
	/**
	 * Liest ein switch Statment ein
	 * @return switch Statement
	 * @throws TemplateException
	 */
	public Switch switchStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("switch",'('))
			return null;
		
		int line=data.cfml.getLine();
		
		comments(data.cfml);
		Expression expr = super.expression(data);
		comments(data.cfml);
		// end )
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"switch statement must end with a [)]");
		comments(data.cfml);

		if(!data.cfml.forwardIfCurrent('{'))
			throw new TemplateException(data.cfml,"switch statement must have a starting  [{]");

		Switch swit=new Switch(expr,line,-1);
		
		//	cases
		 //Node child=null;
		 comments(data.cfml);
		 while(caseStatement(data,swit)) {
			 comments(data.cfml);
		 }
		 // default
		  if(defaultStatement(data,swit)) {
			comments(data.cfml);
		  }
		  
		  while(caseStatement(data,swit)) {
				 comments(data.cfml);
			 }
		  
		  
		// }
		if(!data.cfml.forwardIfCurrent('}'))
			throw new TemplateException(data.cfml,"invalid construct in switch statement");
		swit.setEndLine(data.cfml.getLine());
		return swit;
	}
	
	/**
	 * Liest ein Case Statement ein
	 * @return case Statement
	 * @throws TemplateException
	 */
	public boolean caseStatement(Data data,Switch swit) throws TemplateException {
		if(!data.cfml.forwardIfCurrentAndNoWordAfter("case"))
			return false;
		
		//int line=data.cfml.getLine();		
		comments(data.cfml);
		Expression expr = super.expression(data);
		comments(data.cfml);
		
		if(!data.cfml.forwardIfCurrent(':'))
			throw new TemplateException(data.cfml,"case body must start with [:]");
		
		Body body=new BodyBase();
		switchBlock(data,body);
		swit.addCase(expr, body);
		return true;
	}
	
	/**
	 * Liest ein default Statement ein
	 * @return default Statement
	 * @throws TemplateException
	 */
	public boolean defaultStatement(Data data,Switch swit) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("default",':'))
			return false;
		
		//int line=data.cfml.getLine();
		
		Body body=new BodyBase();
		swit.setDefaultCase(body);
		switchBlock(data,body);
		return true;
	}
	
	/**
	 * Liest ein Switch Block ein
	 * @param block
	 * @throws TemplateException
	 */
	public void switchBlock(Data data,Body body) throws TemplateException {
		while(data.cfml.isValidIndex()) {
			comments(data.cfml);
			if(data.cfml.isCurrent("case ") || data.cfml.isCurrent("default",':') || data.cfml.isCurrent('}')) 
				return;
			statement(data,body,CTX_SWITCH);
		}
	}
	
	
	/**
	 * Liest ein do Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>block spaces "while" spaces "(" spaces condition spaces ")";</code>
	 * @return do Statement
	 * @throws TemplateException
	 */
	public DoWhile doStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("do",'{') && !data.cfml.forwardIfCurrent("do ") && !data.cfml.forwardIfCurrent("do",'/'))
			return null;
		
		int line=data.cfml.getLine();
		Body body=new BodyBase();
		
		data.cfml.previous();
		statement(data,body,CTX_DO_WHILE);
		
		
		data.cfml.removeSpace();
		if(!data.cfml.forwardIfCurrent("while",'('))
			throw new TemplateException(data.cfml,"do statement must have a while at the end");
		
		DoWhile doWhile=new DoWhile(condition(data),body,line,data.cfml.getLine());
		
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"do statement must end with a [)]");
		
		
		return doWhile;
	}
	
	/**
	 * Liest ein for Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>expression spaces ";" spaces condition spaces ";" spaces expression spaces ")" spaces block;</code>
	 * @return for Statement
	 * @throws TemplateException
	 */
	public Statement forStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("for",'(')) 
			return null;
		Expression left=null;
		Body body=new BodyBase();
		int line=data.cfml.getLine();
		comments(data.cfml);
		if(!data.cfml.isCurrent(';')) {
			// left
			left=expression(data);
			comments(data.cfml);
		}
		// middle for
			if(data.cfml.forwardIfCurrent(';')) {

				Expression cont=null;
				Expression update=null;
				// condition
					comments(data.cfml);
					if(!data.cfml.isCurrent(';')) {
						cont=condition(data);
						comments(data.cfml);
					}
				// middle
				if(!data.cfml.forwardIfCurrent(';'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement");
				// update
					comments(data.cfml);
					if(!data.cfml.isCurrent(')')) {
						update=expression(data);
						comments(data.cfml);
					}
				// start )
				if(!data.cfml.forwardIfCurrent(')'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, for statement must end with a [)]");
				// ex block
				statement(data,body,CTX_FOR);
		
				return new For(left,cont,update,body,line,data.cfml.getLine());					
			}
		// middle foreach
			else if(data.cfml.forwardIfCurrent("in")) {
				// condition
					comments(data.cfml);
					Expression value = expression(data);
					comments(data.cfml);
				if(!data.cfml.forwardIfCurrent(')'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, for statement must end with a [)]");
				
				// ex block
				statement(data,body,CTX_FOR);
				if(!(left instanceof Variable))
					throw new TemplateException(data.cfml,"invalid syntax in for statment, left value is invalid");
				
				if(!(value instanceof Variable))
					throw new TemplateException(data.cfml,"invalid syntax in for statment, right value is invalid");
				return new ForEach((Variable)left,(Variable)value,body,line,data.cfml.getLine());	
			}
			else 
				throw new TemplateException(data.cfml,"invalid syntax in for statement");
	}
	
	/**
	 * Liest ein function Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>identifier spaces "(" spaces identifier spaces {"," spaces identifier spaces} ")" spaces block;</code>
	 * @return function Statement
	 * @throws TemplateException
	 */
	public Function funcStatement(Data data,Body parent) throws TemplateException {
		int pos=data.cfml.getPos();
		
		// access modifier
		String strAccess=variableDeclaration(data, false, false);
		if(strAccess==null) {
			data.cfml.setPos(pos);
			return null;
		}
		
		String rtnType=null;
		
		if(strAccess.equalsIgnoreCase("FUNCTION")){
			strAccess=null;
		}
		else{
			comments(data.cfml);
			rtnType=variableDeclaration(data, false, false);
			
			if(rtnType==null){
				data.cfml.setPos(pos);
				return null;
			}
			if(rtnType.equalsIgnoreCase("FUNCTION")){
				rtnType=null;
			}
			comments(data.cfml);
			
			if(rtnType!=null && !data.cfml.forwardIfCurrent("function ")){
				data.cfml.setPos(pos);
				return null;
			}
			comments(data.cfml);
		}
		
		
		// check access returntype
		int access=Component.ACCESS_PUBLIC;
		if(strAccess!=null && rtnType!=null){
			access = ComponentUtil.toIntAccess(strAccess,-1);
			if(access==-1)
				throw new TemplateException(data.cfml,"invalid access type ["+strAccess+"], access types are remote, public, package, private");
		}
		if(strAccess!=null && rtnType==null){
			access = ComponentUtil.toIntAccess(strAccess,-1);
			if(access==-1){
				rtnType=strAccess;
				strAccess=null;
				access=Component.ACCESS_PUBLIC;
			}
		}
		
		
		
		int line=data.cfml.getLine();
		
		comments(data.cfml);
		
		// Name
			String id=identifier(data,false,false);
			if(id==null) throw new TemplateException(data.cfml,"invalid name for a function");
			if(!data.isCFC && !data.isInterface){
				FunctionLibFunction flf = getFLF(data,id);
				if(flf!=null && flf.getCazz()!=CFFunction.class)throw new TemplateException(data.cfml,"The name ["+id+"] is already used by a built in Function");
			}
				
			Body body=new FunctionBody();
			Function func=new Function(id,access,rtnType,body,line,-1);

		// start (
			comments(data.cfml);
			if(!data.cfml.forwardIfCurrent('('))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing begin [(]");
		
			// arguments
			do	{
				comments(data.cfml);
				// finish
				if(data.cfml.isCurrent(')'))break;
				
				// attribute
				
				// name
				//String idName=identifier(data,false,true);
				boolean required=false;
				
				String idName=variableDeclaration(data, false, false);
				// required
				if("required".equalsIgnoreCase(idName)){
					comments(data.cfml);
					String idName2=variableDeclaration(data, false, false);
					if(idName2!=null){
						idName=idName2;
						required=true;
					}
				}
				
				
				String typeName="any";
				if(idName==null) throw new TemplateException(data.cfml,"invalid argument definition");
				comments(data.cfml);
				if(!data.cfml.isCurrent(')') && !data.cfml.isCurrent('=') && !data.cfml.isCurrent(':') && !data.cfml.isCurrent(',')) {
					typeName=idName.toLowerCase();
					idName=identifier(data,false,true);
				}
				else if(idName.indexOf('.')!=-1 || idName.indexOf('[')!=-1) {
					throw new TemplateException(data.cfml,"invalid argument name ["+idName+"] definition");
				}
				
				comments(data.cfml);
				if(data.cfml.isCurrent('=') || data.cfml.isCurrent(':')) {
					data.cfml.next();
					comments(data.cfml);
					func.addArgument(idName,typeName,required,expression(data));
				}
				else func.addArgument(idName,typeName,required);
				
				
				comments(data.cfml);
			}
			while(data.cfml.forwardIfCurrent(','));

		
		// end )
			comments(data.cfml);
			if(!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing ending [)]");
		
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,"function");
			
		// attributes
		Attribute[] attrs = attributes(null,null,data,true,EMPTY_STRING,true,null,false);
		for(int i=0;i<attrs.length;i++){
			func.addAttribute(attrs[i]);
		}
			
		// body
		boolean oldInsideFunction=data.insideFunction;
		data.insideFunction=true;
		try {
		// ex block
		statement(data,body,CTX_FUNCTION);
		}
		finally{
			data.insideFunction=oldInsideFunction;
		}
		func.setEndLine(data.cfml.getLine());
		//eval(tlt,data,func);
		return func;
	}
	

	
	private Statement tagStatement(Data data, Body parent) throws TemplateException {
		Statement child;
		
		//TagLibTag[] tags = getScriptTags(data);
		for(int i=0;i<data.scriptTags.length;i++){
			// single
			if(data.scriptTags[i].getScript().getType()==TagLibTagScript.TYPE_SINGLE) { 
				if((child=_singleAttrStatement(parent,data,data.scriptTags[i]))!=null)return child;
			}
			// multiple
			else {//if(tags[i].getScript().getType()==TagLibTagScript.TYPE_MULTIPLE) { 
				if((child=_multiAttrStatement(parent,data,data.scriptTags[i]))!=null)return child;
			}
		}
		
		//if((child=_singleAttrStatement(parent,data,"abort","showerror",ATTR_TYPE_OPTIONAL,true))!=null)		return child;
		//if((child=_multiAttrStatement(parent,data,"admin",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"application",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"associate",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_singleAttrStatement(parent,data,"break",null,ATTR_TYPE_NONE,false))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"cache",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"content",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"collection",CTX_OTHER,true,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"cookie",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"component",CTX_CFC,true,false))!=null)				return child;
		//else if((child=_singleAttrStatement(parent,data,"continue",null,ATTR_TYPE_NONE,false))!=null)		return child;
		//else if((child=_multiAttrStatement(parent,data,"dbinfo",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"execute",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_singleAttrStatement(parent,data,"exit","method",ATTR_TYPE_OPTIONAL,true))!=null)	return child;
		//else if((child=_multiAttrStatement(parent,data,"feed",CTX_OTHER,false,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"file",CTX_OTHER,false,true))!=null)					return child;
		//else if((child=_singleAttrStatement(parent,data,"flush","interval",ATTR_TYPE_OPTIONAL,true))!=null)	return child;
		//else if((child=_multiAttrStatement(parent,data,"ftp",CTX_OTHER,false,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"http",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"httpparam",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"imap",CTX_OTHER,false,true))!=null)					return child;
		//else if((child=_singleAttrStatement(parent,data,"import","path",ATTR_TYPE_REQUIRED,false))!=null)	return child;
		//else if((child=_multiAttrStatement(parent,data,"index",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_singleAttrStatement(parent,data,"include","template",ATTR_TYPE_REQUIRED,true))!=null)return child;
		//else if((child=_multiAttrStatement(parent,data,"interface",CTX_INTERFACE,true,false))!=null)		return child;
		//else if((child=_multiAttrStatement(parent,data,"ldap",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"lock",CTX_LOCK,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"loop",CTX_LOOP,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"login",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"loginuser",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_singleAttrStatement(parent,data,"logout",null,ATTR_TYPE_NONE,false))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"mail",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"mailpart",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"mailparam",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"module",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_singleAttrStatement(parent,data,"pageencoding","charset",ATTR_TYPE_OPTIONAL,true))!=null)	return child;
		//else if((child=_multiAttrStatement(parent,data,"param",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"pdf",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"pdfparam",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"procparam",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"procresult",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"query",CTX_QUERY,true,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"queryparam",CTX_OTHER,false,true))!=null)			return child;
		//else if((child=_singleAttrStatement(parent,data,"rethrow",null,ATTR_TYPE_NONE,false))!=null)		return child;
		//else if((child=_multiAttrStatement(parent,data,"savecontent",CTX_SAVECONTENT,true,true))!=null)		return child;
		//else if((child=_multiAttrStatement(parent,data,"schedule",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"search",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"setting",CTX_OTHER,false,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"stopwatch",CTX_OTHER,true,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"storedproc",CTX_OTHER,true,true))!=null)			return child;
		//else if((child=_multiAttrStatement(parent,data,"thread",CTX_THREAD,true,true))!=null)				return child;
		//else if((child=_multiAttrStatement(parent,data,"trace",CTX_OTHER,true,true))!=null)					return child;
		//else if((child=_singleAttrStatement(parent,data,"throw","message",ATTR_TYPE_OPTIONAL,true))!=null)	return child;
		//else if((child=_multiAttrStatement(parent,data,"transaction",CTX_TRANSACTION,true,true))!=null)		return child;
		//else if((child=_multiAttrStatement(parent,data,"wddx",CTX_OTHER,false,true))!=null)					return child;
		//else if((child=_multiAttrStatement(parent,data,"zip",CTX_ZIP,true,true))!=null)						return child;
		//else if((child=_multiAttrStatement(parent,data,"zipparam",CTX_ZIP,false,true))!=null)				return child;
		
		
		return null;
	}
	
	

	
	
	public Statement _multiAttrStatement(Body parent, Data data,TagLibTag tlt) throws TemplateException  {
		int pos = data.cfml.getPos();
		try {
			return __multiAttrStatement(parent,data,tlt);
		} 
		catch (ProcessingDirectiveException e) {
			throw e;
		}
		catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data);
			} catch (TemplateException e1) {
				if(tlt.getScript().getContext()==CTX_CFC) throw new ComponentTemplateException(e);
				throw e;
			}
		}
	}

	/*public Statement _multiAttrStatement(Body parent, Data data,String type,short context,boolean hasBody,boolean allowExpression) throws TemplateException  {
		int pos = data.cfml.getPos();
		try {
			return __multiAttrStatement(parent,data,type,context,hasBody,allowExpression);
		} 
		catch (ProcessingDirectiveException e) {
			throw e;
		}
		catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data);
			} catch (TemplateException e1) {
				if(context==CTX_CFC) throw new ComponentBodyException(e);
				throw e;
			}
		}
	}*/
	
	public Tag __multiAttrStatement(Body parent, Data data,TagLibTag tlt) throws TemplateException  {
		if(data.ep==null) return null;
		String type=tlt.getName();
		if(data.cfml.forwardIfCurrent(type)){
			boolean isValid=(data.cfml.isCurrent(' ') || (tlt.getHasBody() && data.cfml.isCurrent('{')));
			if(!isValid){
				data.cfml.setPos(data.cfml.getPos()-type.length());
				return null;
			}
		}
		else return null;
		int line=data.cfml.getLine();
		
		TagLibTagScript script = tlt.getScript();
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,type);
		if(script.getContext()==CTX_CFC)data.isCFC=true;
		else if(script.getContext()==CTX_INTERFACE)data.isInterface=true;
		//Tag tag=new TagComponent(line);
		Tag tag=getTag(parent,tlt, line);
		tag.setTagLibTag(tlt);
		tag.setScriptBase(true);
		//EvaluatorPool.getPool();
		comments(data.cfml);
		
		// attributes
		//attributes(func,data);
		Attribute[] attrs = attributes(tag,tlt,data,true,EMPTY_STRING,script.getRtexpr(),null,false);
		
		for(int i=0;i<attrs.length;i++){
			tag.addAttribute(attrs[i]);
		}
		
		comments(data.cfml);
	
		// body
		if(tlt.getHasBody()){
			Body body=new BodyBase();
			statement(data,body,script.getContext());
			tag.setBody(body);
		}
		else checkSemiColonLineFeed(data,true);
		
		eval(tlt,data,tag);
		return tag;
	}
	
	
	
	/*public Tag __multiAttrStatement(Body parent, Data data,String type,short context,boolean hasBody,boolean allowExpression) throws TemplateException  {
		if(data.ep==null) return null;
		
		if(data.cfml.forwardIfCurrent(type)){
			boolean isValid=(data.cfml.isCurrent(' ') || (hasBody && data.cfml.isCurrent('{')));
			if(!isValid){
				data.cfml.setPos(data.cfml.getPos()-type.length());
				return null;
			}
		}
		else return null;
		int line=data.cfml.getLine();
		
		
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,type);
		if(context==CTX_CFC)data.isCFC=true;
		else if(context==CTX_INTERFACE)data.isInterface=true;
		//Tag tag=new TagComponent(line);
		Tag tag=getTag(parent,tlt, line);
		tag.setTagLibTag(tlt);
		tag.setScriptBase(true);
		//EvaluatorPool.getPool();
		comments(data.cfml);
		
		// attributes
		//attributes(func,data);
		Attribute[] attrs = attributes(tag,tlt,data,true,EMPTY_STRING,allowExpression,null,false);
		
		for(int i=0;i<attrs.length;i++){
			tag.addAttribute(attrs[i]);
		}
		
		comments(data.cfml);
	
		// body
		if(hasBody){
			Body body=new BodyBase();
			statement(data,body,context);
			tag.setBody(body);
		}
		else checkSemiColonLineFeed(data,true);
		
		eval(tlt,data,tag);
		return tag;
	}*/
	
	
	
	public Statement propertyStatement(Data data,Body parent) throws TemplateException  {
		int pos = data.cfml.getPos();
		try {
			return _propertyStatement(data, parent);
		} catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}
	
	private Tag _propertyStatement(Data data,Body parent) throws TemplateException  {
		if(data.context!=CTX_CFC || !data.cfml.forwardIfCurrent("property "))
			return null;
		int line=data.cfml.getLine();
		
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,"property");
		Tag property=new TagBase(line);
		
		

		boolean hasName=false,hasType=false;

		// TODO allow the following pattern property "a.b.C" d;
		//Expression t = string(data);
		// print.o("name:"+t.getClass().getName());
		
		int pos = data.cfml.getPos();
		String tmp=variableDeclaration(data, true, false);
		if(!StringUtil.isEmpty(tmp)) {
			if(tmp.indexOf('.')!=-1) {
				property.addAttribute(new Attribute(false,"type",LitString.toExprString(tmp),"string"));
				hasType=true;
			}
			else {
				data.cfml.setPos(pos);
			}
		}
		else data.cfml.setPos(pos);
		
		
		
		// folgend wird tlt extra nicht Ÿbergeben, sonst findet prŸfung statt
		Attribute[] attrs = attributes(property,tlt,data,false,	NULL,false,"name",true);
		
		checkSemiColonLineFeed(data,true);

		property.setTagLibTag(tlt);
		property.setScriptBase(true);
		
		
		Attribute attr;
		
		// first fill all regular attribute -> name="value"
		for(int i=attrs.length-1;i>=0;i--){
			attr=attrs[i];
			
			if(!attr.getValue().equals(NULL)){
				if(attr.getName().equalsIgnoreCase("name")){
					hasName=true;
					//attr=new Attribute(attr.isDynamicType(),attr.getName(),CastString.toExprString(attr.getValue()),"string");
				}
				else if(attr.getName().equalsIgnoreCase("type")){
					hasType=true;
					//attr=new Attribute(attr.isDynamicType(),attr.getName(),CastString.toExprString(attr.getValue()),"string");
				}
				property.addAttribute(attr);
			}
		}
		
		// now fill name named attributes -> attr1 attr2
		
		String first=null,second=null;
		for(int i=0;i<attrs.length;i++){
			attr=attrs[i];
			
			if(attr.getValue().equals(NULL)){
				// type
				if(first==null && (!hasName || !hasType)){
					first=attr.getName();
					//type=new Attribute(false,"type",LitString.toExprString(attr.getName()),"string");
					//property.addAttribute(type);
				}
				// name
				else if(second==null && !hasName && !hasType){
					second=attr.getName();
					//name=new Attribute(false,"name",LitString.toExprString(attr.getName()),"string");
					//property.addAttribute(name);
				}
				// attr with no value
				else {
					attr=new Attribute(true,attr.getName(),EMPTY_STRING,"string");
					property.addAttribute(attr);
				}
			}
		}

		
		
		if(first!=null) {
				hasName=true;
			if(second!=null){
				hasType=true;
				property.addAttribute(new Attribute(false,"name",LitString.toExprString(second),"string"));
				property.addAttribute(new Attribute(false,"type",LitString.toExprString(first),"string"));
			}
			else {
				property.addAttribute(new Attribute(false,"name",LitString.toExprString(first),"string"));
			}
		}
		
		if(!hasType)
			property.addAttribute(ANY);
		
		if(!hasName)
			throw new TemplateException(data.cfml,"missing name declaration for property");

		/*Tag property=new TagBase(line);
		property.setTagLibTag(tlt);
		property.addAttribute(new Attribute(false,"name",LitString.toExprString(name),"string"));
		property.addAttribute(new Attribute(false,"type",LitString.toExprString(type),"string"));
		*/
		
		
		return property;
	}


	protected String variableDeclaration(Data data,boolean firstCanBeNumber,boolean upper) {
		
		String id=identifier(data, firstCanBeNumber, upper);
		if(id==null) return null;
		
		StringBuffer rtn=new StringBuffer(id);
		data.cfml.removeSpace();
		
		while(data.cfml.forwardIfCurrent('.')){
			data.cfml.removeSpace();
			rtn.append('.');
			id=identifier(data, firstCanBeNumber, upper);
			if(id==null)return null;
			rtn.append(id);
			data.cfml.removeSpace();
		}
		
		while(data.cfml.forwardIfCurrent("[]")){
			data.cfml.removeSpace();
			rtn.append("[]");
		}
		return rtn.toString();
	}

	public Function funcStatementOld(Data data,Body parent) throws TemplateException {
		// access modifier
		int pos=data.cfml.getPos();
		
		
		String strAccess=identifier(data,false,false);
		if(strAccess==null) return null;
		
		String rtnType=null;
		
		if(strAccess.equals("function")){
			if(!data.cfml.forwardIfCurrent(' ')){
				data.cfml.setPos(pos);
				return null;
			}
			strAccess=null;
		}
		else{
			comments(data.cfml);
			rtnType=identifier(data,false,false);
			
			if(rtnType==null){
				data.cfml.setPos(pos);
				return null;
			}
			if(rtnType.equals("function")){
				if(!data.cfml.forwardIfCurrent(' ')){
					data.cfml.setPos(pos);
					return null;
				}
				rtnType=null;
			}
			comments(data.cfml);
			
			if(rtnType!=null && !data.cfml.forwardIfCurrent("function ")){
				data.cfml.setPos(pos);
				return null;
			}
			comments(data.cfml);
		}

		// check access returntype
		int access=Component.ACCESS_PUBLIC;
		if(strAccess!=null && rtnType!=null){
			access = ComponentUtil.toIntAccess(strAccess,-1);
			if(access==-1)
				throw new TemplateException(data.cfml,"invalid access type ["+strAccess+"], access types are remote, public, package, private");
		}
		if(strAccess!=null && rtnType==null){
			access = ComponentUtil.toIntAccess(strAccess,-1);
			if(access==-1){
				rtnType=strAccess;
				strAccess=null;
				access=Component.ACCESS_PUBLIC;
			}
		}
		
		
		
		int line=data.cfml.getLine();
		
		comments(data.cfml);
		
		// Name
			String id=identifier(data,false,false);
			if(id==null) throw new TemplateException(data.cfml,"invalid name for a function");
						
			if(!data.isCFC){
				FunctionLibFunction flf = getFLF(data,id);
				if(flf!=null && flf.getCazz()!=CFFunction.class)throw new TemplateException(data.cfml,"The name ["+id+"] is already used by a built in Function");
			}
				
			Body body=new FunctionBody();
			Function func=new Function(id,access,rtnType,body,line,-1);

		// start (
			comments(data.cfml);
			if(!data.cfml.forwardIfCurrent('('))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing begin [(]");
		
			// arguments
			do	{
				comments(data.cfml);
				// finish
				if(data.cfml.isCurrent(')'))break;
				
				// attribute
				
				// name
				String idName=identifier(data,false,true);
				String typeName="any";
				if(idName==null) throw new TemplateException(data.cfml,"invalid argument definition");
				comments(data.cfml);
				if(!data.cfml.isCurrent(')') && !data.cfml.isCurrent('=') && !data.cfml.isCurrent(':') && !data.cfml.isCurrent(',')) {
					typeName=idName.toLowerCase();
					idName=identifier(data,false,true);
				}
				
				comments(data.cfml);
				if(data.cfml.isCurrent('=') || data.cfml.isCurrent(':')) {
					data.cfml.next();
					comments(data.cfml);
					func.addArgument(idName,typeName,true,expression(data));
				}
				else func.addArgument(idName,typeName,true);
				
				
				comments(data.cfml);
			}
			while(data.cfml.forwardIfCurrent(','));

		
		// end )
			comments(data.cfml);
			if(!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing begin [(]");
		
		// body
			
		boolean oldInsideFunction=data.insideFunction;
		data.insideFunction=true;
		try {
		// ex block
		statement(data,body,CTX_FUNCTION);
		}
		finally{
			data.insideFunction=oldInsideFunction;
		}
		func.setEndLine(data.cfml.getLine());
		return func;
	}
	
	/**
	 * Liest ein return Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces expressionStatement spaces;</code>
	 * @return return Statement
	 * @throws TemplateException
	 */
	protected Return returnStatement(Data data) throws TemplateException {
	    if(!data.cfml.forwardIfCurrentAndNoVarExt("return")) return null;
	    
	    int line=data.cfml.getLine();
	    Return rtn;
	    
	    comments(data.cfml);
	    if(data.cfml.forwardIfCurrent(';')) rtn=new Return(line);
	    else {
	    	Expression expr = expression(data);
	    	
	    	if(!data.cfml.forwardIfCurrent(';'))
				throw new TemplateException(data.cfml,"Missing [;] after expression");
			
	    	
	    	rtn=new Return(expr,line);
	    }
		comments(data.cfml);

		return rtn;
	}

	
	protected Statement _singleAttrStatement(Body parent, Data data, TagLibTag tlt) throws TemplateException   {
		int pos = data.cfml.getPos();
		try {
			return __singleAttrStatement(parent,data,tlt, false);
		} 
		catch (ProcessingDirectiveException e) {
			throw e;
		} 
		catch (TemplateException e) {
			data.cfml.setPos(pos);
			try {
				return expressionStatement(data);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}
	
	/*protected Statement _singleAttrStatement(Body parent, Data data, String tagName,String attrName,int attrType, boolean allowExpression) throws TemplateException   {
		int pos = data.cfml.getPos();
		try {
			return __singleAttrStatement(parent,data,tagName,attrName,attrType,allowExpression, false);
		} 
		catch (ProcessingDirectiveException e) {
			throw e;
		} 
		catch (TemplateException e) {
			data.cfml.setPos(pos);
			try {
				return expressionStatement(data);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}*/
	
	protected Statement __singleAttrStatement(Body parent, Data data, TagLibTag tlt, boolean allowTwiceAttr) throws TemplateException {
		String tagName = tlt.getName();
		if(data.cfml.forwardIfCurrent(tagName)){
			if(!data.cfml.isCurrent(' ') && !data.cfml.isCurrent(';')){
				data.cfml.setPos(data.cfml.getPos()-tagName.length());
				return null;
			}
		}
		else return null;
		
		
		int pos=data.cfml.getPos()-tagName.length();
		int line=data.cfml.getLine();
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,tagName.equals("pageencoding")?"processingdirective":tagName);
		
		Tag tag=getTag(parent,tlt,line);
		tag.setScriptBase(true);
		tag.setTagLibTag(tlt);
		
		comments(data.cfml);
		
		// attribute
		TagLibTagAttr attr = tlt.getScript().getSingleAttr();
		String attrName=null;
		Expression attrValue=null;
		short attrType=ATTR_TYPE_NONE;
		if(attr!=null){
			attrType = attr.getScriptSupport();
			if(ATTR_TYPE_REQUIRED==attrType || (!data.cfml.isCurrent(';') && ATTR_TYPE_OPTIONAL==attrType))
				attrValue =attributeValue(data, tlt.getScript().getRtexpr());
		}
		
		if(attrValue!=null){
			attrName=attr.getName();
			TagLibTagAttr tlta = tlt.getAttribute(attr.getName());
			tag.addAttribute(new Attribute(false,attrName,Cast.toExpression(attrValue,tlta.getType()),tlta.getType()));
		}
		else if(ATTR_TYPE_REQUIRED==attrType){
			data.cfml.setPos(pos);
			return null;
		}
		
		checkSemiColonLineFeed(data,true);
		if(!StringUtil.isEmpty(tlt.getTteClassName()))data.ep.add(tlt, tag, data.fld, data.cfml);
		
		if(!StringUtil.isEmpty(attrName))validateAttributeName(attrName, data.cfml, new ArrayList<String>(), tlt, new RefBooleanImpl(false), new StringBuffer(), allowTwiceAttr);
		
		eval(tlt,data,tag);
		return tag;
	}

	/*protected Statement __singleAttrStatement(Body parent, Data data, String tagName,String attrName,int attrType, boolean allowExpression, boolean allowTwiceAttr) throws TemplateException {
		
		if(data.cfml.forwardIfCurrent(tagName)){
			if(!data.cfml.isCurrent(' ') && !data.cfml.isCurrent(';')){
				data.cfml.setPos(data.cfml.getPos()-tagName.length());
				return null;
			}
		}
		else return null;
		
		
		int pos=data.cfml.getPos()-tagName.length();
		int line=data.cfml.getLine();
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,tagName.equals("pageencoding")?"processingdirective":tagName);
		
		Tag tag=getTag(parent,tlt,line);
		tag.setScriptBase(true);
		tag.setTagLibTag(tlt);
		
		comments(data.cfml);
		
		// attribute
		Expression attrValue=null;
		if(ATTR_TYPE_REQUIRED==attrType || (!data.cfml.isCurrent(';') && ATTR_TYPE_OPTIONAL==attrType))
			attrValue =attributeValue(data, allowExpression);
				//allowExpression?super.expression(data):string(data);
		
		if(attrValue!=null){
			TagLibTagAttr tlta = tlt.getAttribute(attrName);
			tag.addAttribute(new Attribute(false,attrName,Cast.toExpression(attrValue,tlta.getType()),tlta.getType()));
		}
		else if(ATTR_TYPE_REQUIRED==attrType){
			data.cfml.setPos(pos);
			return null;
		}
		
		checkSemiColonLineFeed(data,true);
		if(!StringUtil.isEmpty(tlt.getTteClassName()))data.ep.add(tlt, tag, data.fld, data.cfml);
		
		if(!StringUtil.isEmpty(attrName))validateAttributeName(attrName, data.cfml, new ArrayList<String>(), tlt, new RefBooleanImpl(false), new StringBuffer(), allowTwiceAttr);
		
		eval(tlt,data,tag);
		return tag;
	}*/
	
	

	private void eval(TagLibTag tlt, railo.transformer.cfml.expression.CFMLExprTransformer.Data data, Tag tag) throws TemplateException {
		if(!StringUtil.isEmpty(tlt.getTteClassName())){
			try {
				tlt.getEvaluator().execute(ThreadLocalConfig.get(), tag, tlt,data.fld, data.cfml);
			} catch (EvaluatorException e) {
				throw new TemplateException(e.getMessage());
			}
			data.ep.add(tlt, tag, data.fld, data.cfml);
		}
	}

	private Tag getTag(Body parent, TagLibTag tlt, int line) {
		Tag tag =null;
		if(StringUtil.isEmpty(tlt.getTttClassName()))tag= new TagBase(line);
		else {
			try {
				Class<Tag> clazz = ClassUtil.loadClass(tlt.getTttClassName());
				Constructor<Tag> constr = clazz.getConstructor(new Class[]{int.class});
				tag = constr.newInstance(new Object[]{Caster.toInteger(line)});
				
			} 
			catch (Exception e) {
				e.printStackTrace();
				tag= new TagBase(line);
			}
		}
		tag.setParent(parent);
		return tag;
	}
	
	
	
	/**
	 * List mithilfe des data.cfmlExprTransformer einen Ausruck ein.
	 * <br />
	 * EBNF:<br />
	 * <code>expression ";";</code>
	 * @return Ausdruck
	 * @throws TemplateException
	 */
	public ExpressionStatement expressionStatement(Data data) throws TemplateException {
		Expression expr=expression(data);
		checkSemiColonLineFeed(data,true);
		
		return new ExpressionStatement(expr);
	}
	
	private boolean checkSemiColonLineFeed(Data data,boolean throwError) throws TemplateException {
		comments(data.cfml);
		if(!data.cfml.forwardIfCurrent(';')){
			if(!data.cfml.hasNLBefore() && !data.cfml.isCurrent("</",data.tagName) && !data.cfml.isCurrent('}')){
				if(!throwError) return false;
				throw new TemplateException(data.cfml,"Missing [;] or [line feed] after expression");
			}
		}
		return true;
	}

	/**
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#expression()
	 */
	public Expression expression(Data data) throws TemplateException {
		Expression expr;
		expr = super.expression(data);
		comments(data.cfml);
		return expr;
	}
	
	/**
	 * Ruft die Methode expression der zu vererbenten Klasse auf 
	 * und prüft ob der Rückgabewert einen boolschen Wert repräsentiert und castet den Wert allenfalls.
	 * <br />
	 * EBNF:<br />
	 * <code>TemplateException::expression;</code>
	 * @return condition
	 * @throws TemplateException
	 */
	public ExprBoolean condition(Data data) throws TemplateException {
		ExprBoolean condition=null;
		comments(data.cfml);
		condition=CastBoolean.toExprBoolean(super.expression(data));
		comments(data.cfml);
		return condition;
	}
	
	/**
	 * Liest eine try Block ein
	 * <br />
	 * EBNF:<br />
	 * <code>;</code>
	 * @return Try Block
	 * @throws TemplateException
	*/
	public TryCatchFinally tryStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("try",'{') && !data.cfml.forwardIfCurrent("try ") && !data.cfml.forwardIfCurrent("try",'/'))
			return null;
		data.cfml.previous();

		Body body=new BodyBase();
		TryCatchFinally tryCatchFinally=new TryCatchFinally(body,data.cfml.getLine(),-1);
		
		statement(data,body,CTX_TRY);
		comments(data.cfml);
		
		// catches
		short catchCount=0;
		while(data.cfml.forwardIfCurrent("catch",'(')) {
			catchCount++;
			comments(data.cfml);
			
			// type
			int pos=data.cfml.getPos();
			int line=data.cfml.getLine();
			Expression name = null,type = null;
			
			StringBuffer sbType=new StringBuffer();
            String tmp;
            while(true) {
                tmp=identifier(data,false,false);
                if(tmp==null)break;
                sbType.append(tmp);
                data.cfml.removeSpace();
                if(!data.cfml.forwardIfCurrent('.'))break;
                sbType.append('.');
                data.cfml.removeSpace();
            }
				
            
			if(sbType.length()==0) {
			    type=string(data);
			    if(type==null)			    
			        throw new TemplateException(data.cfml,"a catch statement must begin with the throwing type (query, application ...).");
			}
			else {
				type=LitString.toExprString(sbType.toString());
			} 
            
            
			//name = expression();
			comments(data.cfml);
			
			// name
			if(!data.cfml.isCurrent(')')) {
				name=expression(data);
			}
			else {
				data.cfml.setPos(pos);
				name=expression(data);
			}
			comments(data.cfml);

            Body b=new BodyBase();
			try {
				tryCatchFinally.addCatch(type,name,b,line);
			} 
			catch (BytecodeException e) {
				throw new TemplateException(data.cfml,e.getMessage());
			}
			comments(data.cfml);
			
			if(!data.cfml.forwardIfCurrent(')')) throw new TemplateException(data.cfml,"invalid catch statement, missing closing )");
			
            statement(data,b,CTX_CATCH);
			comments(data.cfml);	
		}
        
		
// finally
		 if(finallyStatement(data,tryCatchFinally)) {
			comments(data.cfml);
		 }
		 else if(catchCount==0)
			throw new TemplateException(data.cfml,"a try statement must have at least one catch statement");
		
        //if(body.isEmpty()) return null;
		tryCatchFinally.setEndLine(data.cfml.getLine());
		return tryCatchFinally;
	}
	
	/**
	 * Prüft ob sich der Zeiger am Ende eines Script Blockes befindet
	 * @return Ende ScriptBlock?
	 * @throws TemplateException
	 */
	public boolean isFinish(Data data) throws TemplateException {
		comments(data.cfml);
		return data.cfml.isCurrent("</",data.tagName);		
	}
	
	
	/**
	 * Liest den Block mit Statements ein.
	 * <br />
	 * EBNF:<br />
	 * <code>"{" spaces {statements} "}" | statement;</code>
	 * @param block
	 * @return was a block
	 * @throws TemplateException
	 */
	private boolean block(Data data,Body body) throws TemplateException {
		if(!data.cfml.forwardIfCurrent('{'))
			return false;
		comments(data.cfml);
		if(data.cfml.forwardIfCurrent('}')) {
			
			return true;
		}
		statements(data,body,false);
		
		if(!data.cfml.forwardIfCurrent('}'))
			throw new TemplateException(data.cfml,"Missing ending [}]");
		return true;
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Attribute[] attributes(Tag tag,TagLibTag tlt, Data data, boolean allowBlock,Expression defaultValue,boolean allowExpression, 
			String ignoreAttrReqFor, boolean allowTwiceAttr) throws TemplateException {
		ArrayList<Attribute> attrs=new ArrayList<Attribute>();
		ArrayList<String> ids=new ArrayList<String>();
		
		while(data.cfml.isValidIndex())	{
			data.cfml.removeSpace();
			// if no more attributes break
			if((allowBlock && data.cfml.isCurrent('{')) || data.cfml.isCurrent(';')) break;
			Attribute attr = attribute(tlt,data,ids,defaultValue,allowExpression, allowTwiceAttr);
			attrs.add(attr);
		}
		
		// not defined attributes
		if(tlt!=null){
			boolean hasAttributeCollection=attrs.contains("attributecollection");
			int type=tlt.getAttributeType();
			if(type==TagLibTag.ATTRIBUTE_TYPE_FIXED || type==TagLibTag.ATTRIBUTE_TYPE_MIXED)	{
				Map<String, TagLibTagAttr> hash = tlt.getAttributes();
				Iterator<String> it = hash.keySet().iterator();
				
				while(it.hasNext())	{
					TagLibTagAttr att=(TagLibTagAttr) hash.get(it.next());
					if(att.isRequired() && !contains(attrs,att.getName()) && att.getDefaultValue()==null && !att.getName().equals(ignoreAttrReqFor))	{
						if(!hasAttributeCollection)throw new TemplateException(data.cfml,"attribute "+att.getName()+" is required for statement "+tlt.getName());
						if(tag!=null)tag.addMissingAttribute(att.getName(),att.getType());
					}
				}
			}
		}
		return attrs.toArray(new Attribute[attrs.size()]);
	}
	
	private boolean contains(ArrayList<Attribute> attrs, String name) {
		Iterator<Attribute> it = attrs.iterator();
		while(it.hasNext()){
			if(it.next().getName().equals(name)) return true;
		}
		return false;
	}

	private Attribute attribute(TagLibTag tlt, Data data, ArrayList<String> args, Expression defaultValue,boolean allowExpression, boolean allowTwiceAttr) throws TemplateException {
		StringBuffer sbType=new StringBuffer();
    	RefBoolean dynamic=new RefBooleanImpl(false);
    	
		// Name
    	String name=attributeName(data.cfml,args,tlt,dynamic,sbType, allowTwiceAttr);
    	Expression value=null;
    	
    	CFMLTransformer.comment(data.cfml,true);
    	
    	// value
    	if(data.cfml.forwardIfCurrent('='))	{
    		CFMLTransformer.comment(data.cfml,true);
    		value=attributeValue(data,allowExpression);	
    	}
    	else {
    		value=defaultValue;
    	}		
    	CFMLTransformer.comment(data.cfml,true);
    	
    	
    	// Type
    	TagLibTagAttr tlta=null;
		if(tlt!=null){
			tlta = tlt.getAttribute(name);
		}
		return new Attribute(dynamic.toBooleanValue(),name,tlta!=null?Cast.toExpression(value, tlta.getType()):value,sbType.toString());
    }
	
	/*private String attributeName(CFMLString cfml, ArrayList<String> args,TagLibTag tag, RefBoolean dynamic, StringBuffer sbType) throws TemplateException {
		String id=StringUtil.toLowerCase(CFMLTransformer.identifier(cfml,true));
        if(args.contains(id)) throw new TemplateException(cfml,"you can't use the same attribute ["+id+"] twice");
		args.add(id);
		
		
		
		int typeDef=tag.getAttributeType();
		if("attributecollection".equals(id)){
			dynamic.setValue(tag.getAttribute(id)==null);
			sbType.append("struct");
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED || typeDef==TagLibTag.ATTRIBUTE_TYPE_MIXED) {
			TagLibTagAttr attr=tag.getAttribute(id);
			if(attr==null) {
				if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED) {
					String names=tag.getAttributeNames();
					if(StringUtil.isEmpty(names))
						throw new TemplateException(cfml,"Attribute "+id+" is not allowed for tag "+tag.getFullName());
					
						throw new TemplateException(cfml,
							"Attribute "+id+" is not allowed for statement "+tag.getName(),
							"valid attribute names are ["+names+"]");
				}
			}
			else {
				sbType.append(attr.getType());
				//parseExpression[0]=attr.getRtexpr();
			}
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC){
			dynamic.setValue(true);
		}
		return id;
	}*/
	
	private String attributeName(CFMLString cfml, ArrayList<String> args,TagLibTag tag, RefBoolean dynamic, StringBuffer sbType, boolean allowTwiceAttr) throws TemplateException {
		String id=StringUtil.toLowerCase(CFMLTransformer.identifier(cfml,true));
		return validateAttributeName(id, cfml, args, tag, dynamic, sbType,allowTwiceAttr);
	}
	
	
	
	private String validateAttributeName(String id,CFMLString cfml, ArrayList<String> args,TagLibTag tag, RefBoolean dynamic, StringBuffer sbType, boolean allowTwiceAttr) throws TemplateException {
		if(args.contains(id) && !allowTwiceAttr) throw new TemplateException(cfml,"you can't use the same attribute ["+id+"] twice");
		args.add(id);
		
		
		if(tag==null) return id;
		int typeDef=tag.getAttributeType();
		if("attributecollection".equals(id)){
			dynamic.setValue(tag.getAttribute(id)==null);
			sbType.append("struct");
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED || typeDef==TagLibTag.ATTRIBUTE_TYPE_MIXED) {
			TagLibTagAttr attr=tag.getAttribute(id);
			if(attr==null) {
				if(typeDef==TagLibTag.ATTRIBUTE_TYPE_FIXED) {
					String names=tag.getAttributeNames();
					if(StringUtil.isEmpty(names))
						throw new TemplateException(cfml,"Attribute "+id+" is not allowed for tag "+tag.getFullName());
					
					throw new TemplateException(cfml,
						"Attribute "+id+" is not allowed for statement "+tag.getName(),
						"valid attribute names are ["+names+"]");
				}
				else dynamic.setValue(true);
				
			}
			else {
				sbType.append(attr.getType());
				//parseExpression[0]=attr.getRtexpr();
			}
		}
		else if(typeDef==TagLibTag.ATTRIBUTE_TYPE_DYNAMIC){
			dynamic.setValue(true);
		}
		return id;
	}
	
		
	private Expression attributeValue(Data data, boolean allowExpression) throws TemplateException {
		return allowExpression?super.expression(data):transformAsString(data,new String[]{" ", ";", "{"});
		//return transformAsString(data);
	}
	
	/*public static Expression attributeValue(Data data,TagLibTag tag, String type,boolean isNonName, Expression noExpression) throws TemplateException {
		Expression expr;
		try {
			
			if(isNonName) {
			    int pos=data.cfml.getPos();
			    try {
			    expr=transfomer.transform(data.ep,data.flibs,data.cfml);
			    }
			    catch(TemplateException ete) {
			       if(data.cfml.getPos()==pos)expr=noExpression;
			       else throw ete;
			    }
			}
			else expr=transfomer.transformAsString(data.ep,data.flibs,data.cfml,true);
			if(type.length()>0) {
				expr=Cast.toExpression(expr, type);
			}
		} catch (TagLibException e) {
			throw new TemplateException(data.cfml,e);
		} 
		return expr;
	}*/
	
	
	
}