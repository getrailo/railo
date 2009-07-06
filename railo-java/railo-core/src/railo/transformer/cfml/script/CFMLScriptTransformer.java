package railo.transformer.cfml.script;

import railo.runtime.exp.TemplateException;
import railo.runtime.functions.system.CFFunction;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.FunctionBody;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
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
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.expression.CFMLExprTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;


/**	
 * Innerhalb des Tag script kann in Cold Fusion eine eigene Scriptsprache verwendet werden, 
 * welche sich an Javascript orientiert. 
 * Da der CFMLTransformer keine Spezialfälle zulässt, 
 * also Tags einfach anhand der eingegeben TLD einliest und transformiert, 
 * aus diesem Grund wird der Inhalt des Tag script einfach als Zeichenkette eingelesen.
 * Erst durch den Evaluator (siehe 3.3), der für das Tag script definiert ist, 
 * wird der Inhalt des Tag script übersetzt.
 * 
 */
public final class CFMLScriptTransformer extends CFMLExprTransformer implements TagDependentBodyTransformer {
	
	private boolean insideFunction=false;
	private String tagName="";
	private boolean isCFC;
	
	/**
	 * Einstiegsmethode für den CFScript Transformer, 
	 * die Methode erbt sich von der Transform Methode der CFMLExprTransformer Klasse.
	 * Der einzige Unterschied liegt darin, das der CFMLString der eingegeben wird als vererbte Klasse CFScriptString vorliegen muss.
	 * Der Parameter ist als CFMLString definiert, so dass er die transform Methode überschreibt.
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * <br />
	 * EBNF:<br />
	 * <code>statements;</code>
	 * @param cfxdTag XML Document des aktuellen zu erstellenden CFXD
	 * @param libTag Definition des aktuellen Tag.
	 * @param cfml CFML Code 
	 * @param parentTransformer
	 * @throws TemplateException
	 */
	public void transform(CFMLTransformer parentTransformer,FunctionLib[] fld, Tag tag,TagLibTag libTag, CFMLString cfml) 
						throws TemplateException	{
		
		//public Body transform(CFMLTransformer parent, FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag, CFMLString cfml)
		// Init Parameter

		isCFC=true;
        try {
			isCFC = ASMUtil.getAncestorPage(tag).isComponent();
		} catch (BytecodeException e) {}

		
		init(fld,cfml,true);
		insideFunction=false; 
		tagName=libTag.getFullName();

		tag.setBody(statements());
	}

	/**
	 * @throws TemplateException 
	 * @see railo.transformer.cfml.expression.CFMLExprTransformer#transform(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transform(FunctionLib[] fld,CFMLString cfml) throws TemplateException {
		throw new TemplateException(cfml,"you can't use Method transform on class CFMLScriptTransformer");
	}
	/**
	 * @see railo.transformer.cfml.expression.CFMLExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
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
	protected Body statements() throws TemplateException {
		ScriptBody body=new ScriptBody();
		
		statements(body,true);
	return body;
	}
	
	/**
	 * Liest sämtliche Statements des CFScriptString ein. 
	 * <br />
	 * EBNF:<br />
	 * <code>{statement spaces};</code>
	 * @param parent Übergeornetes Element dem das Statement zugewiesen wird.
	 * @param isRoot befindet sich der Parser im root des CFMl Docs
	 * @throws TemplateException
	 */
	protected void statements(Body body, boolean isRoot) throws TemplateException {
		do {
			if(isRoot && isFinish())return;
			statement(body);
			comments();
		}
		while(cfml.isValidIndex() && !cfml.isCurrent('}'));
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
	protected void statement(Body parent) throws TemplateException {
		comments();
		Statement child=null;
		if(cfml.forwardIfCurrent(';')){}
		else if((child=ifStatement())!=null) 		parent.addStatement(child);
		else if((child=funcStatement(parent))!=null)parent.addStatement(child);
		else if((child=whileStatement())!=null) 	parent.addStatement(child);
		else if((child=doStatement())!=null) 		parent.addStatement(child);
		else if((child=forStatement())!=null) 		parent.addStatement(child);
		else if((child=returnStatement())!=null) 	parent.addStatement(child);
		else if((child=switchStatement())!=null) 	parent.addStatement(child);
		else if((child=tryStatement())!=null) 		parent.addStatement(child);
		else if((child=breakStatement())!=null) 	parent.addStatement(child);
		else if((child=continueStatement())!=null)	parent.addStatement(child);
		else if((child=abortStatement())!=null)		parent.addStatement(child);
		else if(block(parent)){}
		else parent.addStatement(expressionStatement());
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
	protected Statement ifStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("if",'(')) return null;
		
		
		int line=cfml.getLine();
		
		Body body=new BodyBase();
		Contition cont=new Contition(contition(),body,line);
		
		
		if(!cfml.forwardIfCurrent(')')) throw new TemplateException(cfml,"if statement must end with a [)]");
		// ex block
		statement(body);
		// else if
		comments();
		while(elseifStatement(cont)) {
			comments();
		}
		// else
		 if(elseStatement(cont)) {
			comments();
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
	protected boolean elseifStatement(Contition cont) throws TemplateException {
		int pos=cfml.getPos();
		if(!cfml.forwardIfCurrent("else")) return false;
		
		comments();
		if(!cfml.forwardIfCurrent("if",'(')) {
			cfml.setPos(pos);
			return false;
		}
			
		int line=cfml.getLine();
		Body body=new BodyBase();
		cont.addElseIf(contition(), body, line);

		if(!cfml.forwardIfCurrent(')'))
			throw new TemplateException(cfml,"else if statement must end with a [)]");
		// ex block
		statement(body);
		
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
	protected boolean elseStatement(Contition cont) throws TemplateException {
		if(!cfml.forwardIfCurrent("else",'{') && !cfml.forwardIfCurrent("else ") && !cfml.forwardIfCurrent("else",'/')) 
			return false;

		// start (
		cfml.previous();
		// ex block
		Body body=new BodyBase();
		cont.setElse(body, cfml.getLine());
		statement(body);
		
		return true;
	}
	

	protected boolean finallyStatement(TryCatchFinally tcf) throws TemplateException {
		if(!cfml.forwardIfCurrent("finally",'{') && !cfml.forwardIfCurrent("finally ") && !cfml.forwardIfCurrent("finally",'/')) 
			return false;

		// start (
		cfml.previous();
		// ex block
		Body body=new BodyBase();
		tcf.setFinally(body, cfml.getLine());
		statement(body);
		
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
	public While whileStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("while",'('))
			return null;
		
		int line=cfml.getLine();
		Body body=new BodyBase();
		While whil=new While(contition(),body,line,-1);
		
		if(!cfml.forwardIfCurrent(')'))
			throw new TemplateException(cfml,"while statement must end with a [)]");
		
		statement(body);
		whil.setEndLine(cfml.getLine());
		return whil;
	}
	
	/**
	 * Liest ein switch Statment ein
	 * @return switch Statement
	 * @throws TemplateException
	 */
	public Switch switchStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("switch",'('))
			return null;
		
		int line=cfml.getLine();
		
		comments();
		Expression expr = super.expression();
		comments();
		// end )
		if(!cfml.forwardIfCurrent(')'))
			throw new TemplateException(cfml,"switch statement must end with a [)]");
		comments();

		if(!cfml.forwardIfCurrent('{'))
			throw new TemplateException(cfml,"switch statement must have a starting  [{]");

		Switch swit=new Switch(expr,line,-1);
		
		//	cases
		 //Node child=null;
		 comments();
		 while(caseStatement(swit)) {
			 comments();
		 }
		 // default
		  if(defaultStatement(swit)) {
			comments();
		  }
		  
		  while(caseStatement(swit)) {
				 comments();
			 }
		  
		  
		// }
		if(!cfml.forwardIfCurrent('}'))
			throw new TemplateException(cfml,"invalid construct in switch statement");
		swit.setEndLine(cfml.getLine());
		return swit;
	}
	
	/**
	 * Liest ein Case Statement ein
	 * @return case Statement
	 * @throws TemplateException
	 */
	public boolean caseStatement(Switch swit) throws TemplateException {
		if(!cfml.forwardIfCurrent("case "))
			return false;
		
		//int line=cfml.getLine();		
		comments();
		Expression expr = super.expression();
		comments();
		
		if(!cfml.forwardIfCurrent(':'))
			throw new TemplateException(cfml,"case body must start with [:]");
		
		Body body=new BodyBase();
		switchBlock(body);
		swit.addCase(expr, body);
		return true;
	}
	
	/**
	 * Liest ein default Statement ein
	 * @return default Statement
	 * @throws TemplateException
	 */
	public boolean defaultStatement(Switch swit) throws TemplateException {
		if(!cfml.forwardIfCurrent("default",':'))
			return false;
		
		//int line=cfml.getLine();
		
		Body body=new BodyBase();
		swit.setDefaultCase(body);
		switchBlock(body);
		return true;
	}
	
	/**
	 * Liest ein Switch Block ein
	 * @param block
	 * @throws TemplateException
	 */
	public void switchBlock(Body body) throws TemplateException {
		while(cfml.isValidIndex()) {
			comments();
			if(cfml.isCurrent("case ") || cfml.isCurrent("default",':') || cfml.isCurrent('}')) 
				return;
			statement(body);
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
	public DoWhile doStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("do",'{') && !cfml.forwardIfCurrent("do ") && !cfml.forwardIfCurrent("do",'/'))
			return null;
		
		int line=cfml.getLine();
		Body body=new BodyBase();
		
		cfml.previous();
		statement(body);
		
		
		cfml.removeSpace();
		if(!cfml.forwardIfCurrent("while",'('))
			throw new TemplateException(cfml,"do statement must have a while at the end");
		
		DoWhile doWhile=new DoWhile(contition(),body,line,cfml.getLine());
		
		if(!cfml.forwardIfCurrent(')'))
			throw new TemplateException(cfml,"do statement must end with a [)]");
		
		
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
	public Statement forStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("for",'(')) 
			return null;
		Expression left=null;
		Body body=new BodyBase();
		int line=cfml.getLine();
		comments();
		if(!cfml.isCurrent(';')) {
			// left
			left=expression();
			comments();
		}
		// middle for
			if(cfml.forwardIfCurrent(';')) {

				Expression cont=null;
				Expression update=null;
				// contition
					comments();
					if(!cfml.isCurrent(';')) {
						cont=contition();
						comments();
					}
				// middle
				if(!cfml.forwardIfCurrent(';'))
					throw new TemplateException(cfml,"invalid syntax in for statement");
				// update
					comments();
					if(!cfml.isCurrent(')')) {
						update=expression();
						comments();
					}
				// start )
				if(!cfml.forwardIfCurrent(')'))
					throw new TemplateException(cfml,"invalid syntax in for statement, for statement must end with a [)]");
				// ex block
				statement(body);
		
				return new For(left,cont,update,body,line,cfml.getLine());					
			}
		// middle foreach
			else if(cfml.forwardIfCurrent("in")) {
				// contition
					comments();
					Expression value = expression();
					comments();
				if(!cfml.forwardIfCurrent(')'))
					throw new TemplateException(cfml,"invalid syntax in for statement, for statement must end with a [)]");
				
				// ex block
				statement(body);
				if(!(left instanceof Variable))
					throw new TemplateException(cfml,"invalid syntax in for statment, left value is invalid");
				
				if(!(value instanceof Variable))
					throw new TemplateException(cfml,"invalid syntax in for statment, right value is invalid");
				return new ForEach((Variable)left,(Variable)value,body,line,cfml.getLine());	
			}
			else 
				throw new TemplateException(cfml,"invalid syntax in for statement");
	}
	
	/**
	 * Liest ein function Statement ein.
	 * <br />
	 * EBNF:<br />
	 * <code>identifier spaces "(" spaces identifier spaces {"," spaces identifier spaces} ")" spaces block;</code>
	 * @return function Statement
	 * @throws TemplateException
	 */
	public Function funcStatement(Body parent) throws TemplateException {
		if(!cfml.forwardIfCurrent("function "))
			return null;
		
		int line=cfml.getLine();
		
		comments();
		
		// Name
			String id=identifier(false,false);
			if(id==null) throw new TemplateException(cfml,"invalid name for a function");
			
			
			if(!isCFC){
				FunctionLibFunction flf = getFLF(id);
				if(flf!=null && flf.getCazz()!=CFFunction.class)throw new TemplateException(cfml,"The name ["+id+"] is already used by a Build in Function");
			}
				
			Body body=new FunctionBody();
			Function func=new Function(id,body,line,-1);

		// start (
			comments();
			if(!cfml.forwardIfCurrent('('))
				throw new TemplateException(cfml,"invalid syntax in function head, missing begin [(]");
		
			// arguments
			do	{
				comments();
				// finish
				if(cfml.isCurrent(')'))break;
				
				// attribute
				
				// name
				String idName=identifier(false,true);
				String typeName="any";
				if(idName==null) throw new TemplateException(cfml,"invalid argument definition");
				comments();
				if(!cfml.isCurrent(')') && !cfml.isCurrent('=') && !cfml.isCurrent(':') && !cfml.isCurrent(',')) {
					typeName=idName.toLowerCase();
					idName=identifier(false,true);
				}
				
				comments();
				if(cfml.isCurrent('=') || cfml.isCurrent(':')) {
					cfml.next();
					comments();
					func.addArgument(idName,typeName,true,expression());
				}
				else func.addArgument(idName,typeName,true);
				
				
				comments();
			}
			while(cfml.forwardIfCurrent(','));

		
		// end )
			comments();
			if(!cfml.forwardIfCurrent(')'))
				throw new TemplateException(cfml,"invalid syntax in function head, missing begin [(]");
		
		// body
			
		boolean oldInsideFunction=insideFunction;
		insideFunction=true;
		try {
		// ex block
		statement(body);
		}
		finally{
			insideFunction=oldInsideFunction;
		}
		func.setEndLine(cfml.getLine());
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
	protected Return returnStatement() throws TemplateException {
	    if(!cfml.forwardIfCurrentAndNoVarExt("return")) return null;
	    
	    int line=cfml.getLine();
	    Return rtn;
	    
	    comments();
	    if(cfml.getCurrent()==';') rtn=new Return(line);
	    else {
	    	Expression expr = expression();
	    	
	    	if(!cfml.forwardIfCurrent(';'))
				throw new TemplateException(cfml,"Missing [;] after expression");
			
	    	
	    	rtn=new Return(expr,line);
	    }
		comments();

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
	protected Break breakStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("break",';')) 
			return null;
		
		Break brk=new Break(cfml.getLine());
		comments();
		return brk;
	}
	
	protected Abort abortStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("abort",';')) 
			return null;
		
		Abort abort=new Abort(cfml.getLine());
		comments();
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
	protected Continue continueStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("continue",';')) 
		return null;
		
		Continue cnt=new Continue(cfml.getLine()); 
		comments();
		return cnt;
	}
	
	
	
	/**
	 * List mithilfe des CFMLExprTransformer einen Ausruck ein.
	 * <br />
	 * EBNF:<br />
	 * <code>expression ";";</code>
	 * @return Ausdruck
	 * @throws TemplateException
	 */
	public ExpressionStatement expressionStatement() throws TemplateException {
		Expression expr=expression();
		if(!cfml.forwardIfCurrent(';')){
			if(!cfml.hasNLBefore() && !cfml.isCurrent("</",tagName))
				throw new TemplateException(cfml,"Missing [;] or [line feed] after expression");
		}
		return new ExpressionStatement(expr);
	}
	
	/**
	 * @see railo.transformer.cfml.expression.CFMLExprTransformer#expression()
	 */
	public Expression expression() throws TemplateException {
		Expression expr;
		expr = super.expression();
		comments();
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
	public ExprBoolean contition() throws TemplateException {
		ExprBoolean contition=null;
		comments();
		contition=CastBoolean.toExprBoolean(super.expression());
		comments();
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
	public TryCatchFinally tryStatement() throws TemplateException {
		if(!cfml.forwardIfCurrent("try",'{') && !cfml.forwardIfCurrent("try ") && !cfml.forwardIfCurrent("try",'/'))
			return null;
		cfml.previous();

		Body body=new BodyBase();
		TryCatchFinally tryCatchFinally=new TryCatchFinally(body,cfml.getLine(),-1);
		
		statement(body);
		comments();
		
		// catches
		short catchCount=0;
		while(cfml.forwardIfCurrent("catch",'(')) {
			catchCount++;
			comments();
			
			// type
			int pos=cfml.getPos();
			int line=cfml.getLine();
			Expression name = null,type = null;
			
			StringBuffer sbType=new StringBuffer();
            String tmp;
            while(true) {
                tmp=identifier(false,false);
                if(tmp==null)break;
                sbType.append(tmp);
                cfml.removeSpace();
                if(!cfml.forwardIfCurrent('.'))break;
                sbType.append('.');
                cfml.removeSpace();
            }
				
            
			if(sbType.length()==0) {
			    type=string();
			    if(type==null)			    
			        throw new TemplateException(cfml,"a catch statement must begin with the throwing type (query, application ...).");
			}
			else {
				type=LitString.toExprString(sbType.toString());
			} 
            
            
			//name = expression();
			comments();
			
			// name
			if(!cfml.isCurrent(')')) {
				name=expression();
			}
			else {
				cfml.setPos(pos);
				name=expression();
			}
			comments();

            Body b=new BodyBase();
			try {
				tryCatchFinally.addCatch(type,name,b,line);
			} 
			catch (BytecodeException e) {
				throw new TemplateException(cfml,e.getMessage());
			}
			comments();
			
			if(!cfml.forwardIfCurrent(')')) throw new TemplateException(cfml,"invalid catch statement, missing closing )");
			
            statement(b);
			comments();	
		}
        
		
// finally
		 if(finallyStatement(tryCatchFinally)) {
			comments();
		 }
		 else if(catchCount==0)
			throw new TemplateException(cfml,"a try statement must have at least one catch statement");
		
        //if(body.isEmpty()) return null;
		tryCatchFinally.setEndLine(cfml.getLine());
		return tryCatchFinally;
	}
	
	/**
	 * Prüft ob sich der Zeiger am Ende eines Script Blockes befindet
	 * @return Ende ScriptBlock?
	 * @throws TemplateException
	 */
	public boolean isFinish() throws TemplateException {
		comments();
		return cfml.isCurrent("</",tagName);		
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
	private boolean block(Body body) throws TemplateException {
		if(!cfml.forwardIfCurrent('{'))
			return false;
		comments();
		if(cfml.forwardIfCurrent('}')) {
			
			return true;
		}
		statements(body,false);
		
		if(!cfml.forwardIfCurrent('}'))
			throw new TemplateException(cfml,"Missing ending [}]");
		return true;
	}	
}