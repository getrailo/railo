package railo.transformer.cfml.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Component;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.TemplateException;
import railo.runtime.functions.system.CFFunction;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.FunctionBody;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ClosureAsExpression;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.Condition;
import railo.transformer.bytecode.statement.Condition.Pair;
import railo.transformer.bytecode.statement.DoWhile;
import railo.transformer.bytecode.statement.ExpressionAsStatement;
import railo.transformer.bytecode.statement.For;
import railo.transformer.bytecode.statement.ForEach;
import railo.transformer.bytecode.statement.Return;
import railo.transformer.bytecode.statement.Switch;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.statement.While;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagOther;
import railo.transformer.bytecode.statement.tag.TagParam;
import railo.transformer.bytecode.statement.udf.Closure;
import railo.transformer.bytecode.statement.udf.Function;
import railo.transformer.bytecode.statement.udf.FunctionImpl;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.impl.ProcessingDirectiveException;
import railo.transformer.cfml.expression.AbstrCFMLExprTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibException;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.library.tag.TagLibTagScript;
import railo.transformer.util.CFMLString;


/**	
 * Innerhalb des Tag script kann in CFML eine eigene Scriptsprache verwendet werden, 
 * welche sich an Javascript orientiert. 
 * Da der data.cfml Transformer keine Spezialfaelle zulaesst, 
 * also Tags einfach anhand der eingegeben TLD einliest und transformiert, 
 * aus diesem Grund wird der Inhalt des Tag script einfach als Zeichenkette eingelesen.
 * Erst durch den Evaluator (siehe 3.3), der fuer das Tag script definiert ist, 
 * wird der Inhalt des Tag script uebersetzt.
 * 
 */
public abstract class AbstrCFMLScriptTransformer extends AbstrCFMLExprTransformer {

	private static final String[] IGNORE_LIST_COMPONENT = new String[]{
		"output","synchronized","extends","implements","displayname","style","persistent","accessors"};
	private static final String[] IGNORE_LIST_INTERFACE = new String[]{
		"output","extends","displayname","style","persistent","accessors"};
	private static final String[] IGNORE_LIST_PROPERTY = new String[]{
		"default","fieldtype","name","type","persistent","remotingFetch","column","generator","length",
		"ormtype","params","unSavedValue","dbdefault","formula","generated","insert","optimisticlock",
		"update","notnull","precision","scale","unique","uniquekey","source"
	};
 

	private static EndCondition SEMI_BLOCK=new EndCondition() {
		public boolean isEnd(Data data) {
			return data.cfml.isCurrent('{') || data.cfml.isCurrent(';');
		}
	};
	private static EndCondition SEMI=new EndCondition() {
		public boolean isEnd(Data data) {
			return data.cfml.isCurrent(';');
		}
	};
	private static EndCondition COMMA_ENDBRACKED=new EndCondition() {
		public boolean isEnd(Data data) {
			return data.cfml.isCurrent(',') || data.cfml.isCurrent(')');
		}
	};
	
	
	
	

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
	private static final Attribute ANY = new Attribute(false,"type",LitString.toExprString("any"),"string"); 

	
	/** 
	 * Liest saemtliche Statements des CFScriptString ein. 
	 * <br />
	 * EBNF:<br />
	 * <code>{statement spaces};</code>
	 * @return a statement
	 * @throws TemplateException
	 */
	protected final Body statements(Data data) throws TemplateException {
		ScriptBody body=new ScriptBody();
		
		statements(data,body,true);
	return body;
	}
	
	/**
	 * Liest saemtliche Statements des CFScriptString ein. 
	 * <br />
	 * EBNF:<br />
	 * <code>{statement spaces};</code>
	 * @param parent �bergeornetes Element dem das Statement zugewiesen wird.
	 * @param isRoot befindet sich der Parser im root des data.cfml Docs
	 * @throws TemplateException
	 */
	private final void statements(Data data,Body body, boolean isRoot) throws TemplateException {
		do {
			if(isRoot && isFinish(data))return;
			statement(data,body);
			comments(data);
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
	 * @param parent �bergeornetes Element dem das Statement zugewiesen wird.
	 * @throws TemplateException
	 */
	private final void statement(Data data,Body parent) throws TemplateException {
		statement(data, parent, data.context);
	}
	private boolean statement(Data data,Body parent,short context) throws TemplateException {
		short prior=data.context;
		data.context=context;
		comments(data);
		Statement child=null;
		if(data.cfml.forwardIfCurrent(';')){return true;}
		else if((child=ifStatement(data))!=null) 				parent.addStatement(child);
		else if((child=propertyStatement(data,parent))!=null)	parent.addStatement(child);
		else if((child=paramStatement(data,parent))!=null)	parent.addStatement(child);
		else if((child=funcStatement(data,parent))!=null)		parent.addStatement(child);
		else if((child=whileStatement(data))!=null) 			parent.addStatement(child);
		else if((child=doStatement(data))!=null) 				parent.addStatement(child);
		else if((child=forStatement(data))!=null) 				parent.addStatement(child);
		else if((child=returnStatement(data))!=null) 			parent.addStatement(child);
		else if((child=switchStatement(data))!=null) 			parent.addStatement(child);
		else if((child=tryStatement(data))!=null) 				parent.addStatement(child);
		else if((child=tagStatement(data,parent))!=null)	parent.addStatement(child);
		else if(block(data,parent)){}
		else parent.addStatement(expressionStatement(data,parent));
		data.docComment=null;
		data.context=prior;
		
		return false;
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
	private final Statement ifStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("if",'(')) return null;
		
		
		Position line = data.cfml.getPosition();
		
		Body body=new BodyBase();
		Condition cont=new Condition(condition(data),body,line,null);
		
		if(!data.cfml.forwardIfCurrent(')')) throw new TemplateException(data.cfml,"if statement must end with a [)]");
		// ex block
		statement(data,body,CTX_IF);
		// else if
		comments(data);
		while(elseifStatement(data,cont)) {
			comments(data);
		}
		// else
		 if(elseStatement(data,cont)) {
			comments(data);
		 }

		cont.setEnd(data.cfml.getPosition());
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
	private  final boolean elseifStatement(Data data,Condition cont) throws TemplateException {
		int pos=data.cfml.getPos();
		if(!data.cfml.forwardIfCurrent("else")) return false;
		
		comments(data);
		if(!data.cfml.forwardIfCurrent("if",'(')) {
			data.cfml.setPos(pos);
			return false;
		}
			
		Position line = data.cfml.getPosition();
		Body body=new BodyBase();
		Pair pair = cont.addElseIf(condition(data), body, line,null);

		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"else if statement must end with a [)]");
		// ex block
		statement(data,body,CTX_ELSE_IF);
		pair.end=data.cfml.getPosition();
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
	private final boolean elseStatement(Data data,Condition cont) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("else",'{') && !data.cfml.forwardIfCurrent("else ") && !data.cfml.forwardIfCurrent("else",'/')) 
			return false;

		// start (
		data.cfml.previous();
		// ex block
		Body body=new BodyBase();
		Pair p = cont.setElse(body, data.cfml.getPosition(),null);
		statement(data,body,CTX_ELSE);
		p.end=data.cfml.getPosition();
		return true;
	}
	

	private final boolean finallyStatement(Data data,TryCatchFinally tcf) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("finally",'{') && !data.cfml.forwardIfCurrent("finally ") && !data.cfml.forwardIfCurrent("finally",'/')) 
			return false;

		// start (
		data.cfml.previous();
		// ex block
		Body body=new BodyBase();
		tcf.setFinally(body, data.cfml.getPosition());
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
	private final While whileStatement(Data data) throws TemplateException {
		int pos=data.cfml.getPos();
		
		// id
		String id=variableDec(data, false);
		if(id==null) {
			data.cfml.setPos(pos);
			return null;
		}
		if(id.equalsIgnoreCase("while")){
			id=null;
			data.cfml.removeSpace();
			if(!data.cfml.forwardIfCurrent('(')){
				data.cfml.setPos(pos);
				return null;
			}	
		}
		else {
			data.cfml.removeSpace();
			if(!data.cfml.forwardIfCurrent(':')){
				data.cfml.setPos(pos);
				return null;
			}
			data.cfml.removeSpace();
			
			if(!data.cfml.forwardIfCurrent("while",'(')){
				data.cfml.setPos(pos);
				return null;
			}
		}
		
		Position line = data.cfml.getPosition();
		Body body=new BodyBase();
		While whil=new While(condition(data),body,line,null,id);
		
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"while statement must end with a [)]");
		
		statement(data,body,CTX_WHILE);
		whil.setEnd(data.cfml.getPosition());
		return whil;
	}
	
	/**
	 * Liest ein switch Statment ein
	 * @return switch Statement
	 * @throws TemplateException
	 */
	private final Switch switchStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("switch",'('))
			return null;
		
		Position line = data.cfml.getPosition();
		
		comments(data);
		Expression expr = super.expression(data);
		comments(data);
		// end )
		if(!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(data.cfml,"switch statement must end with a [)]");
		comments(data);

		if(!data.cfml.forwardIfCurrent('{'))
			throw new TemplateException(data.cfml,"switch statement must have a starting  [{]");

		Switch swit=new Switch(expr,line,null);
		
		//	cases
		 //Node child=null;
		 comments(data);
		 while(caseStatement(data,swit)) {
			 comments(data);
		 }
		 // default
		  if(defaultStatement(data,swit)) {
			comments(data);
		  }
		  
		  while(caseStatement(data,swit)) {
				 comments(data);
			 }
		  
		  
		// }
		if(!data.cfml.forwardIfCurrent('}'))
			throw new TemplateException(data.cfml,"invalid construct in switch statement");
		swit.setEnd(data.cfml.getPosition());
		return swit;
	}
	
	/**
	 * Liest ein Case Statement ein
	 * @return case Statement
	 * @throws TemplateException
	 */
	private final boolean caseStatement(Data data,Switch swit) throws TemplateException {
		if(!data.cfml.forwardIfCurrentAndNoWordAfter("case"))
			return false;
		
		//int line=data.cfml.getLine();		
		comments(data);
		Expression expr = super.expression(data);
		comments(data);
		
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
	private final boolean defaultStatement(Data data,Switch swit) throws TemplateException {
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
	private final void switchBlock(Data data,Body body) throws TemplateException {
		while(data.cfml.isValidIndex()) {
			comments(data);
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
	private final DoWhile doStatement(Data data) throws TemplateException {
		int pos=data.cfml.getPos();
		
		// id
		String id=variableDec(data, false);
		if(id==null) {
			data.cfml.setPos(pos);
			return null;
		}
		if(id.equalsIgnoreCase("do")){
			id=null;
			if(!data.cfml.isCurrent('{') && !data.cfml.isCurrent(' ') && !data.cfml.isCurrent('/')) {
				data.cfml.setPos(pos);
				return null;
			}	
		}
		else {
			data.cfml.removeSpace();
			if(!data.cfml.forwardIfCurrent(':')){
				data.cfml.setPos(pos);
				return null;
			}
			data.cfml.removeSpace();
			
			if(!data.cfml.forwardIfCurrent("do",'{') && !data.cfml.forwardIfCurrent("do ") && !data.cfml.forwardIfCurrent("do",'/')) {
				data.cfml.setPos(pos);
				return null;
			}
			data.cfml.previous();
		}
		
		//if(!data.cfml.forwardIfCurrent("do",'{') && !data.cfml.forwardIfCurrent("do ") && !data.cfml.forwardIfCurrent("do",'/'))
		//	return null;
		
		Position line = data.cfml.getPosition();
		Body body=new BodyBase();
		
		//data.cfml.previous();
		statement(data,body,CTX_DO_WHILE);
		
		
		comments(data);
		if(!data.cfml.forwardIfCurrent("while",'('))
			throw new TemplateException(data.cfml,"do statement must have a while at the end");
		
		DoWhile doWhile=new DoWhile(condition(data),body,line,data.cfml.getPosition(),id);
		
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
	private final Statement forStatement(Data data) throws TemplateException {
		
int pos=data.cfml.getPos();
		
		// id
		String id=variableDec(data, false);
		if(id==null) {
			data.cfml.setPos(pos);
			return null;
		}
		if(id.equalsIgnoreCase("for")){
			id=null;
			data.cfml.removeSpace();
			if(!data.cfml.forwardIfCurrent('(')){
				data.cfml.setPos(pos);
				return null;
			}	
		}
		else {
			data.cfml.removeSpace();
			if(!data.cfml.forwardIfCurrent(':')){
				data.cfml.setPos(pos);
				return null;
			}
			data.cfml.removeSpace();
			
			if(!data.cfml.forwardIfCurrent("for",'(')){
				data.cfml.setPos(pos);
				return null;
			}
		}
		
		
		
		
		//if(!data.cfml.forwardIfCurrent("for",'(')) 
		//	return null;
		
		
		
		Expression left=null;
		Body body=new BodyBase();
		Position line = data.cfml.getPosition();
		comments(data);
		if(!data.cfml.isCurrent(';')) {
			// left
			left=expression(data);
			comments(data);
		}
		// middle for
			if(data.cfml.forwardIfCurrent(';')) {

				Expression cont=null;
				Expression update=null;
				// condition
					comments(data);
					if(!data.cfml.isCurrent(';')) {
						cont=condition(data);
						comments(data);
					}
				// middle
				if(!data.cfml.forwardIfCurrent(';'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement");
				// update
					comments(data);
					if(!data.cfml.isCurrent(')')) {
						update=expression(data);
						comments(data);
					}
				// start )
				if(!data.cfml.forwardIfCurrent(')'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, for statement must end with a [)]");
				// ex block
				statement(data,body,CTX_FOR);
		
				return new For(left,cont,update,body,line,data.cfml.getPosition(),id);					
			}
		// middle foreach
			else if(data.cfml.forwardIfCurrent("in")) {
				// condition
					comments(data);
					Expression value = expression(data);
					comments(data);
				if(!data.cfml.forwardIfCurrent(')'))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, for statement must end with a [)]");
				
				// ex block
				statement(data,body,CTX_FOR);
				if(!(left instanceof Variable))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, left value is invalid");
				
				if(!(value instanceof Variable))
					throw new TemplateException(data.cfml,"invalid syntax in for statement, right value is invalid");
				return new ForEach((Variable)left,(Variable)value,body,line,data.cfml.getPosition(),id);	
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
	private final Function funcStatement(Data data,Body parent) throws TemplateException {
		int pos=data.cfml.getPos();
		
		// access modifier
		String strAccess=variableDec(data, false);
		if(strAccess==null) {
			data.cfml.setPos(pos);
			return null;
		}
		
		String rtnType=null;
		if(strAccess.equalsIgnoreCase("FUNCTION")){
			strAccess=null;
			comments(data);
			// only happens when return type is function
			if(data.cfml.forwardIfCurrent("function ")){
				rtnType="function";
				comments(data);
			}
		}
		else{
			comments(data);
			rtnType=variableDec(data, false);
			if(rtnType==null){
				data.cfml.setPos(pos);
				return null;
			}
			if(rtnType.equalsIgnoreCase("FUNCTION")){
				comments(data);
				// only happens when return type is function
				if(data.cfml.forwardIfCurrent("function ")){
					comments(data);
				}
				else rtnType=null;
			}
			comments(data);
			
			if(rtnType!=null && !data.cfml.forwardIfCurrent("function ") && !rtnType.equalsIgnoreCase("FUNCTION")){
				data.cfml.setPos(pos);
				return null;
			}
			comments(data);
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
		
		
		
		Position line = data.cfml.getPosition();
		
		comments(data);
		
		// Name
			String id=identifier(data,false);
			
			
			if(id==null) {
				if(data.cfml.isCurrent('(')) {
					data.cfml.setPos(pos);
					return null;
				}
				throw new TemplateException(data.cfml,"invalid name for a function");
			}
			
			if(!data.isCFC && !data.isInterface){
				FunctionLibFunction flf = getFLF(data,id);
				if(flf!=null && flf.getClazz()!=CFFunction.class)throw new TemplateException(data.cfml,"The name ["+id+"] is already used by a built in Function");
			}
			return closurePart(data, id,access,rtnType,line,false);
	}

	protected  final Function closurePart(Data data, String id, int access, String rtnType, Position line,boolean closure) throws TemplateException {		
		
		Body body=new FunctionBody();
		Function func=closure?
				new Closure(data.page,id,access,rtnType,body,line,null)
				:new FunctionImpl(data.page,id,access,rtnType,body,line,null);
		
			comments(data);
			if(!data.cfml.forwardIfCurrent('('))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing begin [(]");
		
			// arguments
			LitBoolean passByRef;
			Expression displayName;
			Expression hint;
			Map<String,Attribute> meta;
			String _name;
			do	{
				comments(data);
				// finish
				if(data.cfml.isCurrent(')'))break;
				
				// attribute
				
				// name
				//String idName=identifier(data,false,true);
				boolean required=false;
				
				String idName = variableDec(data, false);
				// required
				if("required".equalsIgnoreCase(idName)){
					comments(data);
					String idName2=variableDec(data, false);
					if(idName2!=null){
						idName=idName2;
						required=true;
					}
				}
				
				
				String typeName="any";
				if(idName==null) throw new TemplateException(data.cfml,"invalid argument definition");
				comments(data);
				if(!data.cfml.isCurrent(')') && !data.cfml.isCurrent('=') && !data.cfml.isCurrent(':') && !data.cfml.isCurrent(',')) {
					typeName=idName.toLowerCase();
					idName=identifier(data,false); // MUST was upper case before, is this a problem?
				}
				else if(idName.indexOf('.')!=-1 || idName.indexOf('[')!=-1) {
					throw new TemplateException(data.cfml,"invalid argument name ["+idName+"] definition");
				}
				
				comments(data);
				Expression defaultValue;
				if(data.cfml.isCurrent('=') || data.cfml.isCurrent(':')) {
					data.cfml.next();
					comments(data);
					defaultValue=expression(data);
				}
				else defaultValue=null;
				
				// assign meta data defined in doc comment
				passByRef = LitBoolean.TRUE;
				displayName=LitString.EMPTY;
				hint=LitString.EMPTY;
				meta=null;
				if(data.docComment!=null){
					Map<String, Attribute> params = data.docComment.getParams();
					Attribute[] attrs = params.values().toArray(new Attribute[params.size()]);
					Attribute attr;
					String name;
					
					for(int i=0;i<attrs.length;i++){
						attr=attrs[i];
						name=attr.getName();
						// hint
						if(idName.equalsIgnoreCase(name) || name.equalsIgnoreCase(idName+".hint")) {
							hint=CastString.toExprString(attr.getValue());
							params.remove(name);
						}
						//meta
						if(StringUtil.startsWithIgnoreCase(name, idName+".")) {
							if(name.length()>idName.length()+1){
								if(meta==null) meta=new HashMap<String, Attribute>();
								_name=name.substring(idName.length()+1);
								meta.put(_name, new Attribute(attr.isDynamicType(), _name,attr.getValue(), attr.getType()));
							}
							params.remove(name);
						}
					}
					
				}
				
				// argument attributes
				Attribute[] _attrs = attributes(null,null,data,COMMA_ENDBRACKED,LitString.EMPTY,Boolean.TRUE,null,false);
				Attribute _attr;
				if(!ArrayUtil.isEmpty(_attrs)){
					if(meta==null) meta=new HashMap<String, Attribute>();
					for(int i=0;i<_attrs.length;i++){
						_attr=_attrs[i];
						meta.put(_attr.getName(), _attr);
					}
				}
				
				func.addArgument(
						LitString.toExprString(idName),
						LitString.toExprString(typeName),
						LitBoolean.toExprBoolean(required),
						defaultValue,passByRef,displayName,hint,meta);
				
				comments(data);
			}
			while(data.cfml.forwardIfCurrent(','));

		
		// end )
			comments(data);
			if(!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(data.cfml,"invalid syntax in function head, missing ending [)]");
		
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,"function");
		
		// doc comment
		if(data.docComment!=null){
			func.setHint(data.docComment.getHint());
			
			
			// params
			/*Map<String, Attribute> params = data.docComment.getParams();
			Iterator<Attribute> it = params.values().iterator();
			Attribute attr;
			String name;
			while(it.hasNext()){
				attr=it.next();
				name=attr.getName();
			}*/
			
			func.setMetaData(data.docComment.getParams());
			data.docComment=null;
		}

		comments(data);
			
		// attributes
		Attribute[] attrs = attributes(null,null,data,SEMI_BLOCK,LitString.EMPTY,Boolean.TRUE,null,false);
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
		func.setEnd(data.cfml.getPosition());
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
	
	

	
	
	private final Statement _multiAttrStatement(Body parent, Data data,TagLibTag tlt) throws TemplateException  {
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
				return expressionStatement(data,parent);
			} catch (TemplateException e1) {
				if(tlt.getScript().getContext()==CTX_CFC) throw new ComponentTemplateException(e);
				throw e;
			}
		}
	}

	private final Tag __multiAttrStatement(Body parent, Data data,TagLibTag tlt) throws TemplateException  {
		if(data.ep==null) return null;
		String type=tlt.getName();
		if(data.cfml.forwardIfCurrent(type)) {
			boolean isValid=(data.cfml.isCurrent(' ') || (tlt.getHasBody() && data.cfml.isCurrent('{')));
			if(!isValid){
				data.cfml.setPos(data.cfml.getPos()-type.length());
				return null;
			}
		}
		else return null;
		Position line = data.cfml.getPosition();
		
		TagLibTagScript script = tlt.getScript();
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,type);
		if(script.getContext()==CTX_CFC)data.isCFC=true;
		else if(script.getContext()==CTX_INTERFACE)data.isInterface=true;
		//Tag tag=new TagComponent(line);
		Tag tag=getTag(data,parent,tlt, line,null);
		tag.setTagLibTag(tlt);
		tag.setScriptBase(true);
		
		// add component meta data
		if(data.isCFC) {
			addMetaData(data,tag,IGNORE_LIST_COMPONENT);
		}
		if(data.isInterface) {
			addMetaData(data,tag,IGNORE_LIST_INTERFACE);
		}
		//EvaluatorPool.getPool();
		comments(data);
		
		// attributes
		//attributes(func,data);
		Attribute[] attrs = attributes(tag,tlt,data,SEMI_BLOCK,LitString.EMPTY,script.getRtexpr()?Boolean.TRUE:Boolean.FALSE,null,false);
		
		for(int i=0;i<attrs.length;i++){
			tag.addAttribute(attrs[i]);
		}
		
		comments(data);
	
		// body
		if(tlt.getHasBody()){
			Body body=new BodyBase();
			boolean wasSemiColon=statement(data,body,script.getContext());
			if(!wasSemiColon || !tlt.isBodyFree() || body.hasStatements())
				tag.setBody(body);
			
			
			
		}
		else checkSemiColonLineFeed(data,true,true);
		
		tag.setEnd(data.cfml.getPosition());
		eval(tlt,data,tag);
		return tag;
	}
	
	
	
	private final void addMetaData(Data data, Tag tag, String[] ignoreList) {
		if(data.docComment==null) return;
		

		tag.addMetaData(data.docComment.getHintAsAttribute());
		
		Map<String, Attribute> params = data.docComment.getParams();
		Iterator<Attribute> it = params.values().iterator();
		Attribute attr;
		outer:while(it.hasNext()){
			attr = it.next();
			// ignore list
			if(!ArrayUtil.isEmpty(ignoreList)) {
				for(int i=0;i<ignoreList.length;i++){
					if(ignoreList[i].equalsIgnoreCase(attr.getName())) continue outer;
				}
			}
			tag.addMetaData(attr);	
		}
		data.docComment=null;
	}
	
	private final Statement propertyStatement(Data data,Body parent) throws TemplateException  {
		int pos = data.cfml.getPos();
		try {
			return _propertyStatement(data, parent);
		} catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data,parent);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}
	
	private final Tag _propertyStatement(Data data,Body parent) throws TemplateException  {
		if(data.context!=CTX_CFC || !data.cfml.forwardIfCurrent("property "))
			return null;
		Position line = data.cfml.getPosition();
		
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,"property");
		Tag property=new TagOther(line,null);
		addMetaData(data, property,IGNORE_LIST_PROPERTY);
		

		boolean hasName=false,hasType=false;

		// TODO allow the following pattern property "a.b.C" d;
		//Expression t = string(data);
		// print.o("name:"+t.getClass().getName());
		
		int pos = data.cfml.getPos();
		String tmp=variableDec(data, true);
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
		
		
		
		// folgend wird tlt extra nicht uebergeben, sonst findet pruefung statt
		Attribute[] attrs = attributes(property,tlt,data,SEMI,	NULL,Boolean.FALSE,"name",true);
		
		checkSemiColonLineFeed(data,true,true);

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
					attr=new Attribute(true,attr.getName(),LitString.EMPTY,"string");
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
		property.setEnd(data.cfml.getPosition());
		
		return property;
	}
	
	public Statement paramStatement(Data data,Body parent) throws TemplateException  {
		int pos = data.cfml.getPos();
		try {
			return _paramStatement(data, parent);
		} catch (TemplateException e) {
			try {
				data.cfml.setPos(pos);
				return expressionStatement(data,parent);
			} catch (TemplateException e1) {
				throw e;
			}
		}
	}
	
	private Tag _paramStatement(Data data,Body parent) throws TemplateException  {
		if(!data.cfml.forwardIfCurrent("param "))
			return null;
		Position line = data.cfml.getPosition();
		
		TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,"param");
		TagParam param=new TagParam(line,null);
		
		// type
		boolean hasType=false;
		int pos = data.cfml.getPos();
		String tmp=variableDec(data, true);
		if(!StringUtil.isEmpty(tmp)) {
			if(tmp.indexOf('.')!=-1) {
				param.addAttribute(new Attribute(false,"type",LitString.toExprString(tmp),"string"));
				hasType=true;
			}
			else data.cfml.setPos(pos);
		}
		else data.cfml.setPos(pos);
		
		
		
		// folgend wird tlt extra nicht uebergeben, sonst findet pruefung statt
		Attribute[] attrs = attributes(param,tlt,data,SEMI,	NULL,Boolean.TRUE,"name",true);
		checkSemiColonLineFeed(data,true,true);

		param.setTagLibTag(tlt);
		param.setScriptBase(true);
		
		
		Attribute attr;
		
		// first fill all regular attribute -> name="value"
		boolean hasDynamic=false;
		boolean hasName=false;
		for(int i=attrs.length-1;i>=0;i--){
			attr=attrs[i];
			if(!attr.getValue().equals(NULL)){
				if(attr.getName().equalsIgnoreCase("name")){
					hasName=true;
					param.addAttribute(attr);
				}
				else if(attr.getName().equalsIgnoreCase("type")){
					hasType=true;
					param.addAttribute(attr);
				}
				else if(attr.isDynamicType()){
					hasName=true;
					if(hasDynamic) throw attrNotSupported(data.cfml,tlt,attr.getName());
					hasDynamic=true;
					param.addAttribute(new Attribute(false,"name",LitString.toExprString(attr.getName()),"string"));
					param.addAttribute(new Attribute(false,"default",attr.getValue(),"any"));
				}
				else 
					param.addAttribute(attr);
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
				}
				// name
				else if(second==null && !hasName && !hasType){
					second=attr.getName();
				}
				// attr with no value
				else {
					attr=new Attribute(true,attr.getName(),LitString.EMPTY,"string");
					param.addAttribute(attr);
				}
			}
		}

		
		if(first!=null) {
			if(second!=null){
				hasName=true;
				hasType=true;
				if(hasDynamic) throw attrNotSupported(data.cfml,tlt,first);
				hasDynamic=true;
				param.addAttribute(new Attribute(false,"name",LitString.toExprString(second),"string"));
				param.addAttribute(new Attribute(false,"type",LitString.toExprString(first),"string"));
			}
			else {
				param.addAttribute(new Attribute(false,hasName?"type":"name",LitString.toExprString(first),"string"));
				hasName=true;
			}
		}
		
		//if(!hasType)
		//	param.addAttribute(ANY);
		
		if(!hasName)
			throw new TemplateException(data.cfml,"missing name declaration for param");

		param.setEnd(data.cfml.getPosition());
		return param;
	}


	private TemplateException attrNotSupported(CFMLString cfml, TagLibTag tag, String id) {
		String names=tag.getAttributeNames();
		if(StringUtil.isEmpty(names))
			return new TemplateException(cfml,"Attribute "+id+" is not allowed for tag "+tag.getFullName());
		
		return new TemplateException(cfml,
			"Attribute "+id+" is not allowed for statement "+tag.getName(),
			"valid attribute names are ["+names+"]");
	}



	private final String variableDec(Data data,boolean firstCanBeNumber) {
		
		String id=identifier(data, firstCanBeNumber);
		if(id==null) return null;
		
		StringBuffer rtn=new StringBuffer(id);
		data.cfml.removeSpace();
		
		while(data.cfml.forwardIfCurrent('.')){
			data.cfml.removeSpace();
			rtn.append('.');
			id=identifier(data, firstCanBeNumber);
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
	
	/**
	 * Liest ein return Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>spaces expressionStatement spaces;</code>
	 * @return return Statement
	 * @throws TemplateException
	 */
	private final Return returnStatement(Data data) throws TemplateException {
	    if(!data.cfml.forwardIfCurrentAndNoVarExt("return")) return null;
	    
	    Position line = data.cfml.getPosition();
	    Return rtn;
	    
	    comments(data);
	    if(checkSemiColonLineFeed(data, false,false)) rtn=new Return(line,data.cfml.getPosition());
	    else {
	    	Expression expr = expression(data);
	    	checkSemiColonLineFeed(data, true,true);
	    	rtn=new Return(expr,line,data.cfml.getPosition());
	    }
		comments(data);

		return rtn;
	}

	
	private final Statement _singleAttrStatement(Body parent, Data data, TagLibTag tlt) throws TemplateException   {
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
				return expressionStatement(data,parent);
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
	
	private final Statement __singleAttrStatement(Body parent, Data data, TagLibTag tlt, boolean allowTwiceAttr) throws TemplateException {
		String tagName = tlt.getName();
		if(data.cfml.forwardIfCurrent(tagName)){
			if(!data.cfml.isCurrent(' ') && !data.cfml.isCurrent(';')){
				data.cfml.setPos(data.cfml.getPos()-tagName.length());
				return null;
			}
		}
		else return null;
		
		
		int pos=data.cfml.getPos()-tagName.length();
		Position line = data.cfml.getPosition();
		//TagLibTag tlt = CFMLTransformer.getTLT(data.cfml,tagName.equals("pageencoding")?"processingdirective":tagName);
		
		Tag tag=getTag(data,parent,tlt,line,null);
		tag.setScriptBase(true);
		tag.setTagLibTag(tlt);
		
		comments(data);
		
		// attribute
		TagLibTagAttr attr = tlt.getScript().getSingleAttr();
		String attrName=null;
		Expression attrValue=null;
		short attrType=ATTR_TYPE_NONE;
		if(attr!=null){
			attrType = attr.getScriptSupport();
			char c = data.cfml.getCurrent();
			if(ATTR_TYPE_REQUIRED==attrType || (!data.cfml.isCurrent(';') && ATTR_TYPE_OPTIONAL==attrType)) {
				attrValue =attributeValue(data, tlt.getScript().getRtexpr());
				if(attrValue!=null && isOperator(c)) {
					data.cfml.setPos(pos);
					return null;
				}
			}
		}
		
		if(attrValue!=null){
			attrName=attr.getName();
			TagLibTagAttr tlta = tlt.getAttribute(attr.getName());
			tag.addAttribute(new Attribute(false,attrName,CastOther.toExpression(attrValue,tlta.getType()),tlta.getType()));
		}
		else if(ATTR_TYPE_REQUIRED==attrType){
			data.cfml.setPos(pos);
			return null;
		}
		
		checkSemiColonLineFeed(data,true,true);
		if(!StringUtil.isEmpty(tlt.getTteClassName()))data.ep.add(tlt, tag, data.fld, data.cfml);
		
		if(!StringUtil.isEmpty(attrName))validateAttributeName(attrName, data.cfml, new ArrayList<String>(), tlt, new RefBooleanImpl(false), new StringBuffer(), allowTwiceAttr);
		tag.setEnd(data.cfml.getPosition());
		eval(tlt,data,tag);
		return tag;
	}

	private boolean isOperator(char c) {
		return c=='=' || c=='+' || c=='-';
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
		
		comments(data);
		
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
	
	

	private final void eval(TagLibTag tlt, railo.transformer.cfml.expression.CFMLExprTransformer.Data data, Tag tag) throws TemplateException {
		if(!StringUtil.isEmpty(tlt.getTteClassName())){
			try {
				tlt.getEvaluator().execute(ThreadLocalPageContext.getConfig(), tag, tlt,data.fld, data.cfml);
			} catch (EvaluatorException e) {
				throw new TemplateException(e.getMessage());
			}
			data.ep.add(tlt, tag, data.fld, data.cfml);
		}
	}

	private final Tag getTag(Data data,Body parent, TagLibTag tlt, Position start,Position end) throws TemplateException {
		try {
			Tag tag = tlt.getTag(start, end);
			tag.setParent(parent);
			return tag;
		} catch (TagLibException e) {
			throw new TemplateException(data.cfml,e);
		}
		/*if(StringUtil.isEmpty(tlt.getTttClassName()))tag= new TagBase(line);
		else {
			try {
				Class<Tag> clazz = ClassUtil.loadClass(tlt.getTttClassName());
				Constructor<Tag> constr = clazz.getConstructor(new Class[]{Position.class});
				tag = constr.newInstance(new Object[]{line});
				
			} 
			catch (Exception e) {
				e.printStackTrace();
				tag= new TagBase(line);
			}
		}*/
		
	}
	
	
	
	/**
	 * List mithilfe des data.cfmlExprTransformer einen Ausruck ein.
	 * <br />
	 * EBNF:<br />
	 * <code>expression ";";</code>
	 * @param parent 
	 * @return Ausdruck
	 * @throws TemplateException
	 */
	private Statement expressionStatement(Data data, Body parent) throws TemplateException {
		Expression expr=expression(data);
		checkSemiColonLineFeed(data,true,true);
		if(expr instanceof ClosureAsExpression)
			return ((ClosureAsExpression)expr).getClosure();
			
		return new ExpressionAsStatement(expr);
	}
	
	private final boolean checkSemiColonLineFeed(Data data,boolean throwError, boolean checkNLBefore) throws TemplateException {
		comments(data);
		if(!data.cfml.forwardIfCurrent(';')){
			if((!checkNLBefore || !data.cfml.hasNLBefore()) && !data.cfml.isCurrent("</",data.tagName) && !data.cfml.isCurrent('}')){
				if(!throwError) return false;
				throw new TemplateException(data.cfml,"Missing [;] or [line feed] after expression");
			}
		}
		return true;
	}

	
	/**
	 * Ruft die Methode expression der zu vererbenten Klasse auf 
	 * und prueft ob der Rueckgabewert einen boolschen Wert repraesentiert und castet den Wert allenfalls.
	 * <br />
	 * EBNF:<br />
	 * <code>TemplateException::expression;</code>
	 * @return condition
	 * @throws TemplateException
	 */
	private final ExprBoolean condition(Data data) throws TemplateException {
		ExprBoolean condition=null;
		comments(data);
		condition=CastBoolean.toExprBoolean(super.expression(data));
		comments(data);
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
	private final TryCatchFinally tryStatement(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("try",'{') && !data.cfml.forwardIfCurrent("try ") && !data.cfml.forwardIfCurrent("try",'/'))
			return null;
		data.cfml.previous();

		Body body=new BodyBase();
		TryCatchFinally tryCatchFinally=new TryCatchFinally(body,data.cfml.getPosition(),null);
		
		statement(data,body,CTX_TRY);
		comments(data);
		
		// catches
		short catchCount=0;
		while(data.cfml.forwardIfCurrent("catch",'(')) {
			catchCount++;
			comments(data);
			
			// type
			int pos=data.cfml.getPos();
			Position line=data.cfml.getPosition();
			Expression name = null,type = null;
			
			StringBuffer sbType=new StringBuffer();
            String id;
            while(true) {
            	id=identifier(data,false);
                if(id==null)break;
                sbType.append(id);
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
			comments(data);
			
			// name
			if(!data.cfml.isCurrent(')')) {
				name=expression(data);
			}
			else {
				data.cfml.setPos(pos);
				name=expression(data);
				type = LitString.toExprString( "any" );
			}
			comments(data);

            Body b=new BodyBase();
			try {
				tryCatchFinally.addCatch(type,name,b,line);
			} 
			catch (BytecodeException e) {
				throw new TemplateException(data.cfml,e.getMessage());
			}
			comments(data);
			
			if(!data.cfml.forwardIfCurrent(')')) throw new TemplateException(data.cfml,"invalid catch statement, missing closing )");
			
            statement(data,b,CTX_CATCH);
			comments(data);	
		}
        
		
// finally
		 if(finallyStatement(data,tryCatchFinally)) {
			comments(data);
		 }
		 else if(catchCount==0)
			throw new TemplateException(data.cfml,"a try statement must have at least one catch statement");
		
        //if(body.isEmpty()) return null;
		tryCatchFinally.setEnd(data.cfml.getPosition());
		return tryCatchFinally;
	}
	
	/**
	 * Prueft ob sich der Zeiger am Ende eines Script Blockes befindet
	 * @return Ende ScriptBlock?
	 * @throws TemplateException
	 */
	private final boolean isFinish(Data data) throws TemplateException {
		comments(data);
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
	private final boolean block(Data data,Body body) throws TemplateException {
		if(!data.cfml.forwardIfCurrent('{'))
			return false;
		comments(data);
		if(data.cfml.forwardIfCurrent('}')) {
			
			return true;
		}
		statements(data,body,false);
		
		if(!data.cfml.forwardIfCurrent('}'))
			throw new TemplateException(data.cfml,"Missing ending [}]");
		return true;
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private final Attribute[] attributes(Tag tag,TagLibTag tlt, Data data, EndCondition endCond,Expression defaultValue,Object oAllowExpression, 
			String ignoreAttrReqFor, boolean allowTwiceAttr) throws TemplateException {
		ArrayList<Attribute> attrs=new ArrayList<Attribute>();
		ArrayList<String> ids=new ArrayList<String>();
		
		while(data.cfml.isValidIndex())	{
			data.cfml.removeSpace();
			// if no more attributes break
			if(endCond.isEnd(data)) break;
			//if((allowBlock && data.cfml.isCurrent('{')) || data.cfml.isCurrent(';')) break;
			Attribute attr = attribute(tlt,data,ids,defaultValue,oAllowExpression, allowTwiceAttr);
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
					TagLibTagAttr att=hash.get(it.next());
					if(att.isRequired() && !contains(attrs,att.getName()) && att.getDefaultValue()==null && !att.getName().equals(ignoreAttrReqFor))	{
						if(!hasAttributeCollection)throw new TemplateException(data.cfml,"attribute "+att.getName()+" is required for statement "+tlt.getName());
						if(tag!=null)tag.addMissingAttribute(att.getName(),att.getType());
					}
				}
			}
		}
		return attrs.toArray(new Attribute[attrs.size()]);
	}
	
	private final boolean contains(ArrayList<Attribute> attrs, String name) {
		Iterator<Attribute> it = attrs.iterator();
		while(it.hasNext()){
			if(it.next().getName().equals(name)) return true;
		}
		return false;
	}

	private final Attribute attribute(TagLibTag tlt, Data data, ArrayList<String> args, Expression defaultValue,Object oAllowExpression, boolean allowTwiceAttr) throws TemplateException {
		StringBuffer sbType=new StringBuffer();
    	RefBoolean dynamic=new RefBooleanImpl(false);
    	
		// Name
    	String name=attributeName(data.cfml,args,tlt,dynamic,sbType, allowTwiceAttr);
    	boolean allowExpression=false;
    	if(oAllowExpression instanceof Boolean)allowExpression=((Boolean)oAllowExpression).booleanValue();
    	else if(oAllowExpression instanceof String)allowExpression=((String)oAllowExpression).equalsIgnoreCase(name);

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
		return new Attribute(dynamic.toBooleanValue(),name,tlta!=null?CastOther.toExpression(value, tlta.getType()):value,sbType.toString());
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
	
	private final String attributeName(CFMLString cfml, ArrayList<String> args,TagLibTag tag, RefBoolean dynamic, StringBuffer sbType, boolean allowTwiceAttr) throws TemplateException {
		String id=StringUtil.toLowerCase(CFMLTransformer.identifier(cfml,true));
		return validateAttributeName(id, cfml, args, tag, dynamic, sbType,allowTwiceAttr);
	}
	
	
	
	private final String validateAttributeName(String id,CFMLString cfml, ArrayList<String> args,TagLibTag tag, RefBoolean dynamic, StringBuffer sbType, boolean allowTwiceAttr) throws TemplateException {
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
				dynamic.setValue(true);
				
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
	
		
	private final Expression attributeValue(Data data, boolean allowExpression) throws TemplateException {
		return allowExpression?super.expression(data):transformAsString(data,new String[]{" ", ";", "{"});
	}
	
	public static interface EndCondition {
		public boolean isEnd(Data data);
	}
}