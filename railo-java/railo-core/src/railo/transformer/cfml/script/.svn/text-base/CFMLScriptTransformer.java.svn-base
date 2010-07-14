package railo.transformer.cfml.script;

import java.util.ArrayList;

import railo.print;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.TemplateException;
import railo.runtime.functions.system.CFFunction;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.FunctionBody;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Abort;
import railo.transformer.bytecode.statement.Break;
import railo.transformer.bytecode.statement.Continue;
import railo.transformer.bytecode.statement.Contition;
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
import railo.transformer.bytecode.statement.tag.TagComponent;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.CFMLExprTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
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
	
	//private boolean insideFunction=false;
	//private String tagName="";
	//private boolean isCFC;

	public class ComponentBodyException extends TemplateException {
		private TemplateException te;

		public ComponentBodyException(TemplateException te){
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

	public void transform(CFMLTransformer parentTransformer,EvaluatorPool ep,FunctionLib[] fld, Tag tag,TagLibTag libTag, CFMLString cfml) throws TemplateException	{
		
		
		boolean isCFC=true;
        try {
			isCFC = ASMUtil.getAncestorPage(tag).isComponent();
		} catch (BytecodeException e) {}
		
		
		Data data = init(ep,fld,cfml,true);
		data.insideFunction=false; 
		data.tagName=libTag.getFullName();
		data.isCFC=isCFC;

		tag.setBody(statements(data));
	}

	/**
	 * @throws TemplateException 
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#transform(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.data.cfmlString)
	 */
	public Expression transform(FunctionLib[] fld,CFMLString cfml) throws TemplateException {
		throw new TemplateException(cfml,"you can't use Method transform on class CFMLScriptTransformer");
	}
	/**
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.data.cfmlString)
	 */
	public Expression transformAsString(FunctionLib[] fld,CFMLString cfml, boolean allowLowerThan) throws TemplateException {
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
		else if((child=interfaceStatement(data,parent))!=null)	parent.addStatement(child);
		else if((child=propertyStatement(data,parent))!=null)	parent.addStatement(child);
		else if((child=cfcStatement(data,parent))!=null)		parent.addStatement(child);
		else if((child=funcStatement(data,parent))!=null)		parent.addStatement(child);
		else if((child=whileStatement(data))!=null) 			parent.addStatement(child);
		else if((child=doStatement(data))!=null) 				parent.addStatement(child);
		else if((child=forStatement(data))!=null) 				parent.addStatement(child);
		else if((child=returnStatement(data))!=null) 			parent.addStatement(child);
		else if((child=switchStatement(data))!=null) 			parent.addStatement(child);
		else if((child=tryStatement(data))!=null) 				parent.addStatement(child);
		else if((child=breakStatement(data))!=null) 			parent.addStatement(child);
		else if((child=continueStatement(data))!=null)			parent.addStatement(child);
		else if((child=abortStatement(data))!=null)				parent.addStatement(child);
		else if(block(data,parent)){}
		else parent.addStatement(expressionStatement(data));
		data.context=prior;
	}
	
	/**
	 * Liest ein if Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces contition spaces ")" spaces block {"else if" spaces "(" elseifStatement spaces }
			 [("else"  spaces "(" | "else ") elseStatement spaces];</code>
	 * @return if Statement
	 * @throws TemplateException
	 */
	protected Statement ifStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("if",'(')) return null;
		
		
		int line=data.cfml.getLine();
		
		Body body=new BodyBase();
		Contition cont=new Contition(contition(data),body,line);
		
		
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
	 * <code>spaces contition spaces ")" spaces block;</code>
	 * @return else if Statement
	 * @throws TemplateException
	 */
	protected boolean elseifStatement(Data data,Contition cont) throws TemplateException {
		int pos=data.cfml.getPos();
		if(!data.cfml.forwardIfCurrent("else")) return false;
		
		comments(data.cfml);
		if(!data.cfml.forwardIfCurrent("if",'(')) {
			data.cfml.setPos(pos);
			return false;
		}
			
		int line=data.cfml.getLine();
		Body body=new BodyBase();
		cont.addElseIf(contition(data), body, line);

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
	protected boolean elseStatement(Data data,Contition cont) throws TemplateException {
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
	 * <code>spaces contition spaces ")" spaces block;</code>
	 * @return while Statement
	 * @throws TemplateException
	 */
	public While whileStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("while",'('))
			return null;
		
		int line=data.cfml.getLine();
		Body body=new BodyBase();
		While whil=new While(contition(data),body,line,-1);
		
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
		if(!data.cfml.forwardIfCurrent("case "))
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
	 * <code>block spaces "while" spaces "(" spaces contition spaces ")";</code>
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
		
		DoWhile doWhile=new DoWhile(contition(data),body,line,data.cfml.getLine());
		
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"do statement must end with a [)]");
		
		
		return doWhile;
	}
	
	/**
	 * Liest ein for Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>expression spaces ";" spaces contition spaces ";" spaces expression spaces ")" spaces block;</code>
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
				// contition
					comments(data.cfml);
					if(!data.cfml.isCurrent(';')) {
						cont=contition(data);
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
				// contition
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
						
			if(!data.isCFC){
				FunctionLibFunction flf = getFLF(data,id);
				if(flf!=null && flf.getCazz()!=CFFunction.class)throw new TemplateException(data.cfml,"The name ["+id+"] is already used by a Build in Function");
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
				String idName=variableDeclaration(data, false, false);
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
					func.addArgument(idName,typeName,true,expression(data));
				}
				else func.addArgument(idName,typeName,true);
				
				
				comments(data.cfml);
			}
			while(data.cfml.forwardIfCurrent(','));

		
		// end )
			comments(data.cfml);
			if(!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing ending [)]");
		
		// attributes
		Attribute[] attrs = attributes(null,data,true,EMPTY_STRING);
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
		return func;
	}
	
	public Statement cfcStatement(Data data,Body parent) throws TemplateException  {
		/*try {
			return _cfcStatement(data, parent,"component",CTX_CFC);
		} catch (TemplateException e) {
			throw new ComponentBodyException(e);
		}*/
		
		int pos = data.cfml.getPos();
		try {
			return _cfcStatement(data, parent,"component",CTX_CFC);
		} catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}
	public Statement interfaceStatement(Data data,Body parent) throws TemplateException  {
		/*try {
			return _cfcStatement(data, parent,"interface",CTX_INTERFACE);
		} catch (TemplateException e) {
			throw new ComponentBodyException(e);
		}*/
		
		int pos = data.cfml.getPos();
		try {
			return _cfcStatement(data, parent,"interface",CTX_INTERFACE);
		} catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data);
			} catch (TemplateException e1) {
				throw e;
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	public TagComponent _cfcStatement(Data data,Body parent,String type,short context) throws TemplateException  {
		if(data.ep==null) return null;
		
		if(data.cfml.forwardIfCurrent(type)){
			if(!data.cfml.isCurrent(' ') && !data.cfml.isCurrent('{')){
				data.cfml.setPos(data.cfml.getPos()-type.length());
				return null;
			}
		}
		else return null;
		int line=data.cfml.getLine();
		
		
		TagComponent cfc=new TagComponent(line);
		//cfc.set
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,type);
		cfc.setTagLibTag(tlt);
		EvaluatorPool.getPool();// MUST should be part if class Data
		comments(data.cfml);
		
		// attributes
		//attributes(func,data);
		Attribute[] attrs = attributes(tlt,data,true,EMPTY_STRING);
		
		for(int i=0;i<attrs.length;i++){
			cfc.addAttribute(attrs[i]);
		}
		
		comments(data.cfml);
	
		// body
		Body body=new BodyBase();
		statement(data,body,context);
		cfc.setBody(body);
		data.ep.add(tlt, cfc, data.fld, data.cfml);
		return cfc;
	}
	
	//
	
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
		Attribute[] attrs = attributes(tlt,data,false,NULL);
		
		
		/*
		String type="any";
		String name=variableDeclaration(data, false, false);

		comments(data.cfml);
		
		if(name==null) throw new TemplateException(data.cfml,"invalid property defintion, missing name");
		String tmp=variableDeclaration(data, false, false);

		comments(data.cfml);
		
		if(tmp!=null){
			type=name;
			name=tmp;
		}
		comments(data.cfml);
		*/
		checkSemiColonLineFeed(data);

		Tag property=new TagBase(line);
		property.setTagLibTag(tlt);
		
		Attribute name=null,type=null;
		boolean hasName=false,hasType=false;
		
		Attribute attr;
		
		// first fill all regular attribute -> name="value"
		for(int i=attrs.length-1;i>=0;i--){
			attr=attrs[i];
			
			if(!attr.getValue().equals(NULL)){
				if(attr.getName().equalsIgnoreCase("name"))hasName=true;
				if(attr.getName().equalsIgnoreCase("type"))hasType=true;
				property.addAttribute(attr);
			}
		}
		
		// now fill name named attributes -> attr1 attr2
		boolean checkForNT=true;
		for(int i=attrs.length-1;i>=0;i--){
			attr=attrs[i];
			
			if(attr.getValue().equals(NULL)){
				// name
				if(checkForNT && !hasName){
					hasName=true;
					name=new Attribute(false,"name",LitString.toExprString(attr.getName()),"string");
					property.addAttribute(name);
				}
				// type
				else if(checkForNT && !hasType){
					hasType=true;
					type=new Attribute(false,"type",LitString.toExprString(attr.getName()),"string");
					property.addAttribute(type);
				}
				// attr with no value
				else {
					attr=new Attribute(true,attr.getName(),EMPTY_STRING,"string");
					//property.addAttribute(attr);
				}
				
			}
			else {
				checkForNT=false;
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
				if(flf!=null && flf.getCazz()!=CFFunction.class)throw new TemplateException(data.cfml,"The name ["+id+"] is already used by a Build in Function");
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
	    if(data.cfml.getCurrent()==';') rtn=new Return(line);
	    else {
	    	Expression expr = expression(data);
	    	
	    	if(!data.cfml.forwardIfCurrent(';'))
				throw new TemplateException(data.cfml,"Missing [;] after expression");
			
	    	
	    	rtn=new Return(expr,line);
	    }
		comments(data.cfml);

		return rtn;
	}

	/**
	 * Liest ein break Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces;</code>
	 * @return break Statement
	 * @throws TemplateException
	 */
	protected Break breakStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("break",';')) 
			return null;
		
		Break brk=new Break(data.cfml.getLine());
		comments(data.cfml);
		return brk;
	}
	
	protected Abort abortStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("abort",';')) 
			return null;
		
		Abort abort=new Abort(data.cfml.getLine());
		comments(data.cfml);
		return abort;
	}

	/**
	 * Liest ein continue Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces;</code>
	 * @return continue Statement
	 * @throws TemplateException
	 */
	protected Continue continueStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("continue",';')) 
		return null;
		
		Continue cnt=new Continue(data.cfml.getLine()); 
		comments(data.cfml);
		return cnt;
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
		checkSemiColonLineFeed(data);
		
		return new ExpressionStatement(expr);
	}
	
	private void checkSemiColonLineFeed(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent(';')){
			if(!data.cfml.hasNLBefore() && !data.cfml.isCurrent("</",data.tagName) && !data.cfml.isCurrent('}'))
				throw new TemplateException(data.cfml,"Missing [;] or [line feed] after expression");
		}
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
	 * @return Contition
	 * @throws TemplateException
	 */
	public ExprBoolean contition(Data data) throws TemplateException {
		ExprBoolean contition=null;
		comments(data.cfml);
		contition=CastBoolean.toExprBoolean(super.expression(data));
		comments(data.cfml);
		return contition;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Attribute[] attributes(TagLibTag tlt, Data data, boolean allowBlock,Expression defaultValue) throws TemplateException {
		ArrayList<Attribute> attrs=new ArrayList<Attribute>();
		ArrayList<String> ids=new ArrayList<String>();
		
		while(data.cfml.isValidIndex())	{
			data.cfml.removeSpace();
			// if no more attributes break
			if((allowBlock && data.cfml.isCurrent('{')) || data.cfml.isCurrent(';')) break;
			Attribute attr = attribute(tlt,data,ids,defaultValue);
			attrs.add(attr);
		}
		return attrs.toArray(new Attribute[attrs.size()]);
	}
	
	private Attribute attribute(TagLibTag tlt, Data data, ArrayList<String> args, Expression defaultValue) throws TemplateException {
    	// Name
    	String name=attributeName(data.cfml,args);
    	Expression value=null;
    	
    	CFMLTransformer.comment(data.cfml,true);
    	
    	// value
    	if(data.cfml.forwardIfCurrent('='))	{
    		CFMLTransformer.comment(data.cfml,true);
    		value=attributeValue(data);	
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
		String type="string";
		if(tlta!=null){
			type=tlta.getType();
		}
    	
    	return new Attribute(false,name,Cast.toExpression(value, type),type);
    }
	
	private String attributeName(CFMLString cfml, ArrayList<String> args) throws TemplateException {
		String id=StringUtil.toLowerCase(CFMLTransformer.identifier(cfml,true));
        if(args.contains(id)) throw new TemplateException(cfml,"you can't use the same attribute ["+id+"] twice");
		args.add(id);
		return id;
	}
		
	private Expression attributeValue(Data data) throws TemplateException {
		Expression expr=super.expression(data);
		return expr;
	}
}