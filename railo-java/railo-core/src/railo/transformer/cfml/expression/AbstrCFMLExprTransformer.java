package railo.transformer.cfml.expression;

import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.Component;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.TemplateException;
import railo.runtime.functions.other.CreateUniqueId;
import railo.runtime.op.Caster;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.type.util.UDFUtil;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastDouble;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ClosureAsExpression;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionInvoker;
import railo.transformer.bytecode.expression.Invoker;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.Assign;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.DynAssign;
import railo.transformer.bytecode.expression.var.FunctionMember;
import railo.transformer.bytecode.expression.var.Member;
import railo.transformer.bytecode.expression.var.NamedArgument;
import railo.transformer.bytecode.expression.var.UDF;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.Identifier;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.literal.Null;
import railo.transformer.bytecode.op.OPDecision;
import railo.transformer.bytecode.op.OPUnary;
import railo.transformer.bytecode.op.OpBool;
import railo.transformer.bytecode.op.OpContional;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.bytecode.op.OpElvis;
import railo.transformer.bytecode.op.OpNegate;
import railo.transformer.bytecode.op.OpNegateNumber;
import railo.transformer.bytecode.op.OpString;
import railo.transformer.bytecode.op.OpVariable;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.udf.Closure;
import railo.transformer.bytecode.statement.udf.Function;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.script.DocComment;
import railo.transformer.cfml.script.DocCommentTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.library.tag.TagLibTagScript;
import railo.transformer.util.CFMLString;

/**
 * 
 * 
	Der CFMLExprTransfomer implementiert das Interface ExprTransfomer, 
	er bildet die Parser Grammatik ab, die unten definiert ist. 
	Er erh￤lt als Eingabe CFML Code, als String oder CFMLString, 
	der einen CFML Expression erh￤lt und liefert ein CFXD Element zur￼ck, 
	das diesen Ausdruck abbildet.
	Mithilfe der FunctionLibﾒs, kann er Funktionsaufrufe, 
	die Teil eines Ausdruck sein k￶nnen, erkennen und validieren. 
	Dies geschieht innerhalb der Methode function.
	Falls ein Funktionsaufruf, einer Funktion innerhalb einer FunctionLib entspricht, 
	werden diese gegeneinander verglichen und der Aufruf wird als Build-In-Funktion ￼bernommen, 
	andernfalls wird der Funktionsaufruf als User-Defined-Funktion interpretiert.
	Die Klasse Cast, Operator und ElementFactory (siehe 3.2) helfen ihm beim erstellen des Ausgabedokument CFXD.

 * <pre>
 * Parser Grammatik EBNF (Extended Backus-Naur Form) 

	transform      = spaces impOp;
	impOp          = eqvOp {"imp" spaces eqvOp};
	eqvOp          = xorOp {"eqv" spaces xorOp};
	xorOp          = orOp {"xor" spaces  orOp};
	orOp           = andOp {("or" | "||") spaces andOp}; 
			(* "||" Existiert in CFMX nicht *)
	andOp          = notOp {("and" | "&&") spaces notOp}; 
			(* "&&" Existiert in CFMX nicht *) 
	notOp          = [("not"|"!") spaces] decsionOp; 
			(* "!" Existiert in CFMX nicht *)
	decsionOp      = concatOp {("neq"|"eq"|"gte"|"gt"|"lte"|"lt"|"ct"|
	                 "contains"|"nct"|"does not contain") spaces concatOp}; 
			(* "ct"=conatains und "nct"=does not contain; Existiert in CFMX nicht *)
	concatOp       = plusMinusOp {"&" spaces plusMinusOp};
	plusMinusOp    = modOp {("-"|"+") spaces modOp};
	modOp          = divMultiOp {("mod" | "%") spaces divMultiOp}; 
	                (* modulus operator , "%" Existiert in CFMX nicht *)
	divMultiOp     = expoOp {("*"|"/") spaces expoOp};
	expoOp         = clip {("exp"|"^") spaces clip}; 
	                (*exponent operator, " exp " Existiert in CFMX nicht *)
	clip           = ("(" spaces impOp ")" spaces) | checker;
	checker        = string | number | dynamic | sharp;
	string         = ("'" {"##"|"''"|"#" impOp "#"| ?-"#"-"'" } "'") | 
	                 (""" {"##"|""""|"#" impOp "#"| ?-"#"-""" } """);
	number         = ["+"|"-"] digit {digit} {"." digit {digit}};
	digit          = "0"|..|"9";
	dynamic        = "true" | "false" | "yes" | "no" | startElement  
	                 {("." identifier | "[" structElement "]")[function] };
	startElement   = identifier "(" functionArg ")" | scope | identifier;
	scope          = "variable" | "cgi" | "url" | "form" | "session" | "application" | 
	                 "arguments" | "cookie" | "client ";
	identifier     = (letter | "_") {letter | "_"|digit};
	structElement  = "[" impOp "]";
	functionArg    = [impOp{"," impOp}];
	sharp          = "#" checker "#";
	spaces         = {space};
	space          = "\s"|"\t"|"\f"|"\t"|"\n";
	letter         = "a"|..|"z"|"A"|..|"Z";

{"x"}= 0 bis n mal "x"
["x"]= 0 bis 1 mal "x"
("x" | "y")"z" = "xz" oder "yz"

</pre>
 *
 */
public abstract class AbstrCFMLExprTransformer {

	private static final short STATIC=0;
	private static final short DYNAMIC=1;
	private static FunctionLibFunction JSON_ARRAY = null;
	private static FunctionLibFunction JSON_STRUCT = null;

	public static final short CTX_OTHER = TagLibTagScript.CTX_OTHER;
	public static final short CTX_NONE = TagLibTagScript.CTX_NONE;
	public static final short CTX_IF = TagLibTagScript.CTX_IF;
	public static final short CTX_ELSE_IF = TagLibTagScript.CTX_ELSE_IF;
	public static final short CTX_ELSE = TagLibTagScript.CTX_ELSE;
	public static final short CTX_FOR = TagLibTagScript.CTX_FOR;
	public static final short CTX_WHILE = TagLibTagScript.CTX_WHILE;
	public static final short CTX_DO_WHILE = TagLibTagScript.CTX_DO_WHILE;
	public static final short CTX_CFC = TagLibTagScript.CTX_CFC;
	public static final short CTX_INTERFACE = TagLibTagScript.CTX_INTERFACE;
	public static final short CTX_FUNCTION =TagLibTagScript.CTX_FUNCTION;
	public static final short CTX_BLOCK = TagLibTagScript.CTX_BLOCK;
	public static final short CTX_FINALLY = TagLibTagScript.CTX_FINALLY;
	public static final short CTX_SWITCH = TagLibTagScript.CTX_SWITCH;
	public static final short CTX_TRY = TagLibTagScript.CTX_TRY;
	public static final short CTX_CATCH = TagLibTagScript.CTX_CATCH;
	public static final short CTX_TRANSACTION = TagLibTagScript.CTX_TRANSACTION;
	public static final short CTX_THREAD = TagLibTagScript.CTX_THREAD;
	public static final short CTX_SAVECONTENT = TagLibTagScript.CTX_SAVECONTENT;
	public static final short CTX_LOCK = TagLibTagScript.CTX_LOCK;
	public static final short CTX_LOOP = TagLibTagScript.CTX_LOOP;
	public static final short CTX_QUERY = TagLibTagScript.CTX_QUERY;
	public static final short CTX_ZIP = TagLibTagScript.CTX_ZIP;
	
	
	private DocCommentTransformer docCommentTransformer= new DocCommentTransformer();
	

	protected short ATTR_TYPE_NONE=TagLibTagAttr.SCRIPT_SUPPORT_NONE;
	protected short ATTR_TYPE_OPTIONAL=TagLibTagAttr.SCRIPT_SUPPORT_OPTIONAL;
	protected short ATTR_TYPE_REQUIRED=TagLibTagAttr.SCRIPT_SUPPORT_REQUIRED;
	
	protected static final Expression NULL = LitString.toExprString("NULL"); 
	protected static final Attribute ANY = new Attribute(false,"type",LitString.toExprString("any"),"string"); 


	protected static EndCondition SEMI_BLOCK=new EndCondition() {
		public boolean isEnd(ExprData data) {
			return data.cfml.isCurrent('{') || data.cfml.isCurrent(';');
		}
	};
	protected static EndCondition SEMI=new EndCondition() {
		public boolean isEnd(ExprData data) {
			return data.cfml.isCurrent(';');
		}
	};
	protected static EndCondition COMMA_ENDBRACKED=new EndCondition() {
		public boolean isEnd(ExprData data) {
			return data.cfml.isCurrent(',') || data.cfml.isCurrent(')');
		}
	};

	public static interface EndCondition {
		public boolean isEnd(ExprData data);
	}
	
	/*private short mode=0;
	protected CFMLString cfml;
	protected FunctionLib[] fld;
	private boolean ignoreScopes=false;
	private boolean allowLowerThan;*/
	
	public class ExprData extends Data {
		
		private short mode=0;
		private boolean ignoreScopes=false;
		private boolean allowLowerThan;
		public boolean insideFunction;
		public String tagName;
		public boolean isCFC;
		public boolean isInterface;
		public short context=CTX_NONE; 
		public DocComment docComment;
		
		public ExprData(Page page, EvaluatorPool ep, CFMLString cfml, TagLib[][] tlibs,FunctionLib[] flibs, TransfomerSettings settings,boolean allowLowerThan,TagLibTag[] scriptTags) {
			super(page,cfml,ep,settings,tlibs,flibs,scriptTags);
			this.allowLowerThan=allowLowerThan;
		}
	}
	
	protected Expression transformAsString(ExprData data,String[] breakConditions) throws TemplateException {
		Expression el=null;
		
		// parse the houle Page String
        comments(data);		
				
		// String
			if((el=string(data))!=null) {
				data.mode=STATIC;
				return el;
			} 
		// Sharp
			if((el=sharp(data))!=null) {
				data.mode=DYNAMIC;
				return el;
			}  
		// Simple
			return simple(data,breakConditions);
	}
	
	

	/**
	 * Initialmethode, wird aufgerufen um den internen Zustand des Objektes zu setzten.
	 * @param fld Function Libraries zum validieren der Funktionen
	 * @param cfml CFML Code der transfomiert werden soll.
	 */
	protected ExprData init(Page page,EvaluatorPool ep,TagLib[][] tld, FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings, boolean allowLowerThan) {
		ExprData data = new ExprData(page,ep,cfml,tld,fld,settings,allowLowerThan,scriptTags);
		if(JSON_ARRAY==null)JSON_ARRAY=getFLF(data,"_literalArray");
		if(JSON_STRUCT==null)JSON_STRUCT=getFLF(data,"_literalStruct");
		return data;
		//this.allowLowerThan=allowLowerThan;
		//this.fld = fld;
		//this.cfml = cfml;
	}
	
	/**
	 * Startpunkt zum transfomieren einer Expression, ohne dass das Objekt neu initialisiert wird, 
	 * dient vererbten Objekten als Einstiegspunkt.
	 * @return Element
	 * @throws TemplateException
	 */
	protected Expression expression(ExprData data) throws TemplateException {
		return assignOp(data);
	}

	/**
	* Liest einen gelableten  Funktionsparamter ein
	* <br />
	* EBNF:<br />
	* <code>assignOp [":" spaces assignOp];</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Argument functionArgument(ExprData data, boolean varKeyUpperCase) throws TemplateException {
		return functionArgument(data,null,varKeyUpperCase);
	}
	
	private Argument functionArgument(ExprData data,String type, boolean varKeyUpperCase) throws TemplateException {
		Expression expr = assignOp(data);
		try{
			if (data.cfml.forwardIfCurrent(":")) {
				comments(data);
				return new NamedArgument(expr,assignOp(data),type,varKeyUpperCase);
			}
			else if(expr instanceof DynAssign){
				DynAssign da=(DynAssign) expr;
				return new NamedArgument(da.getName(),da.getValue(),type,varKeyUpperCase);
			}
			else if(expr instanceof Assign && !(expr instanceof OpVariable)){
				Assign a=(Assign) expr;
				return new NamedArgument(a.getVariable(),a.getValue(),type,varKeyUpperCase);
			}
		}
		catch(BytecodeException be) {
			throw new TemplateException(data.cfml,be.getMessage());
		}
		return new Argument(expr,type);
	}

	
	
	
	/**
	* Transfomiert Zuweisungs Operation.
	* <br />
	* EBNF:<br />
	* <code>eqvOp ["=" spaces assignOp];</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression assignOp(ExprData data) throws TemplateException {
        
		Expression expr = conditionalOp(data);
        if (data.cfml.forwardIfCurrent('=')) {
        	
            comments(data);
            if(data.mode==STATIC) expr=new DynAssign(expr,assignOp(data));
			else {
				if(expr instanceof Variable) {
					Expression value = assignOp(data);
					expr=new Assign((Variable)expr,value,data.cfml.getPosition());
				}
				else if(expr instanceof Null) {
					Variable var = ((Null)expr).toVariable();
					Expression value = assignOp(data);
					expr=new Assign(var,value,data.cfml.getPosition());
				}
				else
					throw new TemplateException(data.cfml,"invalid assignment left-hand side ("+expr.getClass().getName()+")");
			}
		}
		return expr;
	}
	
	private Expression conditionalOp(ExprData data) throws TemplateException {
        
		Expression expr = impOp(data);
        if (data.cfml.forwardIfCurrent('?')) {
        	comments(data);
        	// Elvis
        	if(data.cfml.forwardIfCurrent(':')) {
        		comments(data);
            	Expression right = assignOp(data);
        		
        		if(!(expr instanceof Variable) )
        			throw new TemplateException(data.cfml,"left operant of the Elvis operator has to be a variable or a function call");
        		
        		return OpElvis.toExpr((Variable)expr, right);
        	}
        	
        	Expression left = assignOp(data);
        	comments(data);
        	if(!data.cfml.forwardIfCurrent(':'))throw new TemplateException("invalid conditional operator");
        	comments(data); 
        	Expression right = assignOp(data);
        	
            expr=OpContional.toExpr(expr, left, right);
		}
		return expr;
	}

	/**
	* Transfomiert eine Implication (imp) Operation.
	* <br />
	* EBNF:<br />
	* <code>eqvOp {"imp" spaces eqvOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression impOp(ExprData data) throws TemplateException {
		Expression expr = eqvOp(data);
		while(data.cfml.forwardIfCurrentAndNoWordAfter("imp")) { 
			comments(data);
            expr=OpBool.toExprBoolean(expr, eqvOp(data), OpBool.IMP);
		}
		return expr;
	}

	/**
	* Transfomiert eine  Equivalence (eqv) Operation.
	* <br />
	* EBNF:<br />
	* <code>xorOp {"eqv" spaces xorOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression eqvOp(ExprData data) throws TemplateException {
		Expression expr = xorOp(data);
		while(data.cfml.forwardIfCurrentAndNoWordAfter("eqv")) {
			comments(data);
            expr=OpBool.toExprBoolean(expr, xorOp(data), OpBool.EQV);
		}
		return expr;
	}

	/**
	* Transfomiert eine  Xor (xor) Operation.
	* <br />
	* EBNF:<br />
	* <code>orOp {"xor" spaces  orOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression xorOp(ExprData data) throws TemplateException {
		Expression expr = orOp(data);
		while(data.cfml.forwardIfCurrentAndNoWordAfter("xor")) {
			comments(data);
            expr=OpBool.toExprBoolean(expr, orOp(data), OpBool.XOR);
		}
		return expr;
	}

	/**
	* Transfomiert eine  Or (or) Operation. Im Gegensatz zu CFMX ,
	* werden "||" Zeichen auch als Or Operatoren anerkannt.
	* <br />
	* EBNF:<br />
	* <code>andOp {("or" | "||") spaces andOp}; (* "||" Existiert in CFMX nicht *)</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression orOp(ExprData data) throws TemplateException {
		Expression expr = andOp(data);
		
		while(data.cfml.forwardIfCurrent("||") || data.cfml.forwardIfCurrentAndNoWordAfter("or")) {
			comments(data);
            expr=OpBool.toExprBoolean(expr, andOp(data), OpBool.OR);
		}
		return expr;
	}

	/**
	* Transfomiert eine  And (and) Operation. Im Gegensatz zu CFMX ,
	* werden "&&" Zeichen auch als And Operatoren anerkannt.
	* <br />
	* EBNF:<br />
	* <code>notOp {("and" | "&&") spaces notOp}; (* "&&" Existiert in CFMX nicht *)</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression andOp(ExprData data) throws TemplateException {
		Expression expr = notOp(data);
		
		while(data.cfml.forwardIfCurrent("&&") || data.cfml.forwardIfCurrentAndNoWordAfter("and")) {
			comments(data);
	        expr=OpBool.toExprBoolean(expr, notOp(data), OpBool.AND);
		}
		return expr;
	}

	/**
	* Transfomiert eine  Not (not) Operation. Im Gegensatz zu CFMX ,
	* wird das "!" Zeichen auch als Not Operator anerkannt.
	* <br />
	* EBNF:<br />
	* <code>[("not"|"!") spaces] decsionOp; (* "!" Existiert in CFMX nicht *)</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression notOp(ExprData data) throws TemplateException {
		// And Operation
		Position line = data.cfml.getPosition();
		if (data.cfml.isCurrent('!') && !data.cfml.isCurrent("!=")) {
			data.cfml.next();
			comments(data);
			return OpNegate.toExprBoolean(notOp(data),line,data.cfml.getPosition());
		}
		else if (data.cfml.forwardIfCurrentAndNoWordAfter("not")) {
			comments(data);
			return OpNegate.toExprBoolean(notOp(data),line,data.cfml.getPosition());
		}
		return decsionOp(data);
	}

	/**
	* <font f>Transfomiert eine Vergleichs Operation.
	* <br />
	* EBNF:<br />
	* <code>concatOp {("neq"|"eq"|"gte"|"gt"|"lte"|"lt"|"ct"|
	                 "contains"|"nct"|"does not contain") spaces concatOp}; 
			(* "ct"=conatains und "nct"=does not contain; Existiert in CFMX nicht *)</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression decsionOp(ExprData data) throws TemplateException {

		Expression expr = concatOp(data);
		boolean hasChanged=false;
		// ct, contains
		do {
			hasChanged=false;
			if(data.cfml.isCurrent('c')) {
					if (data.cfml.forwardIfCurrent("ct",false,true)) {expr = decisionOpCreate(data,OPDecision.CT,expr);hasChanged=true;} 
					else if (data.cfml.forwardIfCurrent("contains",false,true)){ expr = decisionOpCreate(data,OPDecision.CT,expr);hasChanged=true;}
			}
			// does not contain
			else if (data.cfml.forwardIfCurrent("does","not","contain",false,true)){ expr = decisionOpCreate(data,OPDecision.NCT,expr); hasChanged=true;}

			// equal, eq
			else if (data.cfml.isCurrent("eq") && !data.cfml.isCurrent("eqv")) {
				int plus=2;
				data.cfml.setPos(data.cfml.getPos()+2);
				if(data.cfml.forwardIfCurrent("ual"))plus=5;
				
				if(data.cfml.isCurrentVariableCharacter()) {
					data.cfml.setPos(data.cfml.getPos()-plus);
				}
				else {
					expr = decisionOpCreate(data,OPDecision.EQ,expr);
					hasChanged=true;
				}
				
			}
			// ==
			else if (data.cfml.forwardIfCurrent("==")) {
				if(data.cfml.forwardIfCurrent('=')) 		expr = decisionOpCreate(data,OPDecision.EEQ,expr);
				else expr = decisionOpCreate(data,OPDecision.EQ,expr);
				hasChanged=true;
			}
			// !=
			else if (data.cfml.forwardIfCurrent("!=")) {
				if(data.cfml.forwardIfCurrent('=')) 		expr = decisionOpCreate(data,OPDecision.NEEQ,expr);
				else expr = decisionOpCreate(data,OPDecision.NEQ,expr); 
				hasChanged=true;
			}
			// <=/</<>
			else if (data.cfml.isCurrent('<')) {
				hasChanged=true;
				if(data.cfml.isNext('='))	{
					data.cfml.next();data.cfml.next();
					expr = decisionOpCreate(data,OPDecision.LTE,expr);
				}
				else if(data.cfml.isNext('>')) {
					data.cfml.next();data.cfml.next();
					expr = decisionOpCreate(data,OPDecision.NEQ,expr);
				}
				else if(data.cfml.isNext('/')) {
					hasChanged=false;
				}
				else	{
					data.cfml.next();
					expr = decisionOpCreate(data,OPDecision.LT,expr); 
				}
			}
			// >=/>
			else if (data.allowLowerThan && data.cfml.forwardIfCurrent('>')) {
				if(data.cfml.forwardIfCurrent('=')) 	expr = decisionOpCreate(data,OPDecision.GTE,expr);
				else 							expr = decisionOpCreate(data,OPDecision.GT,expr); 
				hasChanged=true;
			}
			
			// gt, gte, greater than or equal to, greater than
			else if (data.cfml.isCurrent('g')) {
				if (data.cfml.forwardIfCurrent("gt")) {
					if(data.cfml.forwardIfCurrentAndNoWordAfter("e")) {
						if(data.cfml.isCurrentVariableCharacter()) {
							data.cfml.setPos(data.cfml.getPos()-3);
						}
						else {
							expr = decisionOpCreate(data,OPDecision.GTE,expr);
							hasChanged=true;
						}
					}
					else {
						if(data.cfml.isCurrentVariableCharacter()) {
							data.cfml.setPos(data.cfml.getPos()-2);
						}
						else {
							expr = decisionOpCreate(data,OPDecision.GT,expr);
							hasChanged=true;
						}
					}
				} 
				else if (data.cfml.forwardIfCurrent("greater", "than",false,true)) {
					if(data.cfml.forwardIfCurrent("or","equal", "to",true,true)) expr = decisionOpCreate(data,OPDecision.GTE,expr);
					else expr = decisionOpCreate(data,OPDecision.GT,expr);
					hasChanged=true;
				}	
				else if (data.cfml.forwardIfCurrent("ge",false,true)) {
					expr = decisionOpCreate(data,OPDecision.GTE,expr);
					hasChanged=true;
				}				
			}
			
			// is, is not
			else if (data.cfml.forwardIfCurrent("is",false,true)) {
				if(data.cfml.forwardIfCurrent("not",true,true)) expr = decisionOpCreate(data,OPDecision.NEQ,expr);
				else expr = decisionOpCreate(data,OPDecision.EQ,expr);
				hasChanged=true;
			}
			
			// lt, lte, less than, less than or equal to
			else if (data.cfml.isCurrent('l')) {
				if (data.cfml.forwardIfCurrent("lt")) {
					if(data.cfml.forwardIfCurrentAndNoWordAfter("e")) {
						if(data.cfml.isCurrentVariableCharacter()) {
							data.cfml.setPos(data.cfml.getPos()-3);
						}
						else {
							expr = decisionOpCreate(data,OPDecision.LTE,expr);
							hasChanged=true;
						}
					}
					else {
						if(data.cfml.isCurrentVariableCharacter()) {
							data.cfml.setPos(data.cfml.getPos()-2);
						}
						else {
							expr = decisionOpCreate(data,OPDecision.LT,expr);
							hasChanged=true;
						}
					}
				} 
				else if (data.cfml.forwardIfCurrent("less","than",false,true)) {
					if(data.cfml.forwardIfCurrent("or", "equal", "to",true,true)) expr = decisionOpCreate(data,OPDecision.LTE,expr);
					else expr = decisionOpCreate(data,OPDecision.LT,expr);
					hasChanged=true;
				}	
				else if (data.cfml.forwardIfCurrent("le",false,true)) {
					expr = decisionOpCreate(data,OPDecision.LTE,expr);
					hasChanged=true;
				}				
			}
			
			// neq, not equal, nct
			else if (data.cfml.isCurrent('n')) {
				// Not Equal
					if (data.cfml.forwardIfCurrent("neq",false,true)){ expr = decisionOpCreate(data,OPDecision.NEQ,expr); hasChanged=true;}
				// Not Equal (Alias)
					else if (data.cfml.forwardIfCurrent("not","equal",false,true)){ expr = decisionOpCreate(data,OPDecision.NEQ,expr);hasChanged=true; }
				// nct
					else if (data.cfml.forwardIfCurrent("nct",false,true)){ expr = decisionOpCreate(data,OPDecision.NCT,expr); hasChanged=true;}	
			}
			
		}
		while(hasChanged);
		return expr;
	}
	private Expression decisionOpCreate(ExprData data,int operation, Expression left) throws TemplateException {
        comments(data);
        return OPDecision.toExprBoolean(left, concatOp(data), operation);
	}

	/**
	* Transfomiert eine  Konkatinations-Operator (&) Operation. Im Gegensatz zu CFMX ,
	* wird das "!" Zeichen auch als Not Operator anerkannt.
	* <br />
	* EBNF:<br />
	* <code>plusMinusOp {"&" spaces concatOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression concatOp(ExprData data) throws TemplateException {
		Expression expr = plusMinusOp(data);
		
		while(data.cfml.isCurrent('&') && !data.cfml.isCurrent("&&")) {
			data.cfml.next();
			
			// &=
			if (data.cfml.isCurrent('=') && expr instanceof Variable) {
				data.cfml.next();
				comments(data);
				Expression value = assignOp(data);
				
				expr = new OPUnary((Variable)expr,value,OPUnary.PRE,OPUnary.CONCAT,expr.getStart(),data.cfml.getPosition());
				
				
				//ExprString res = OpString.toExprString(expr, right);
				//expr=new OpVariable((Variable)expr,res,data.cfml.getPosition());
			}
			else {
	            comments(data);
	            expr=OpString.toExprString(expr, plusMinusOp(data));
			}
			
		}
		return expr;
	}

	/**
	* Transfomiert die mathematischen Operatoren Plus und Minus (1,-).
	* <br />
	* EBNF:<br />
	* <code>modOp [("-"|"+") spaces plusMinusOp];</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression plusMinusOp(ExprData data) throws TemplateException {
		Expression expr = modOp(data);
		
		while(!data.cfml.isLast()) {
			
			// Plus Operation
			if (data.cfml.forwardIfCurrent('+'))			expr=_plusMinusOp(data,expr,OpDouble.PLUS);
			// Minus Operation
			else if (data.cfml.forwardIfCurrent('-'))	expr=_plusMinusOp(data,expr,OpDouble.MINUS);
			else break;
		}
		return expr;
	}
	
	

	private Expression _plusMinusOp(ExprData data,Expression expr,int opr) throws TemplateException {
		// +=
		// plus|Minus Assignment
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data);
			Expression value = assignOp(data);
			//if(opr==OpDouble.MINUS) value=OpNegateNumber.toExprDouble(value, null, null);
			
			expr = new OPUnary((Variable)expr,value,OPUnary.PRE,opr,expr.getStart(),data.cfml.getPosition());
			
			//ExprDouble res = OpDouble.toExprDouble(expr, right,opr);
			//expr=new OpVariable((Variable)expr,res,data.cfml.getPosition());
		}
		
		else {
			comments(data);
            expr=OpDouble.toExprDouble(expr, modOp(data), opr);	
		}
		return expr;
	}
	

	/**
	* Transfomiert eine Modulus Operation. Im Gegensatz zu CFMX ,
	* wird das "%" Zeichen auch als Modulus Operator anerkannt.
	* <br />
	* EBNF:<br />
	* <code>divMultiOp {("mod" | "%") spaces divMultiOp}; (* modulus operator , "%" Existiert in CFMX nicht *)</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression modOp(ExprData data) throws TemplateException {
		Expression expr = divMultiOp(data);
		
		// Modulus Operation
		while(data.cfml.forwardIfCurrent('%') || data.cfml.forwardIfCurrentAndNoWordAfter("mod")) {
			expr=_modOp(data,expr);
			//comments(data);
            //expr=OpDouble.toExprDouble(expr, divMultiOp(), OpDouble.MODULUS);
		}
		return expr;
	}
	
	private Expression _modOp(ExprData data,Expression expr) throws TemplateException {
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data);
			Expression right = assignOp(data);
			ExprDouble res = OpDouble.toExprDouble(expr, right,OpDouble.MODULUS);
			return new OpVariable((Variable)expr,res,data.cfml.getPosition());
		}
        comments(data);
        return OpDouble.toExprDouble(expr, expoOp(data), OpDouble.MODULUS);
	}

	/**
	* Transfomiert die mathematischen Operatoren Mal und Durch (*,/).
	* <br />
	* EBNF:<br />
	* <code>expoOp {("*"|"/") spaces expoOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression divMultiOp(ExprData data) throws TemplateException {
		Expression expr = expoOp(data);

		while (!data.cfml.isLast()) {
			
				// Multiply Operation
				if(data.cfml.forwardIfCurrent('*')) {
					expr=_divMultiOp(data,expr,OpDouble.MULTIPLY);
					//comments(data);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.MULTIPLY);
				}
				// Divide Operation
				else if (data.cfml.isCurrent('/') && (!data.cfml.isCurrent('/','>') )) {
					data.cfml.next(); 
					expr=_divMultiOp(data,expr,OpDouble.DIVIDE);
					//comments(data);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.DIVIDE);
				}
				// Divide Operation
				else if (data.cfml.isCurrent('\\')) {
					data.cfml.next(); 
					expr=_divMultiOp(data,expr,OpDouble.INTDIV);
					//comments(data);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.INTDIV);
				}
				else {
					break;
				}
			
		}
		return expr;
	}

	private Expression _divMultiOp(ExprData data,Expression expr, int iOp) throws TemplateException {
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data);
			Expression value = assignOp(data);
			
			return new OPUnary((Variable)expr,value,OPUnary.PRE,iOp,expr.getStart(),data.cfml.getPosition());
			
			
			
			
			//ExprDouble res = OpDouble.toExprDouble(expr, right,iOp);
			//return new OpVariable((Variable)expr,res,data.cfml.getPosition());
		}
        comments(data);
        return OpDouble.toExprDouble(expr, expoOp(data), iOp);
	}

	/**
	* Transfomiert den Exponent Operator (^,exp). Im Gegensatz zu CFMX ,
	* werden die Zeichen " exp " auch als Exponent anerkannt.
	* <br />
	* EBNF:<br />
	* <code>clip {("exp"|"^") spaces clip};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression expoOp(ExprData data) throws TemplateException {
		Expression expr = unaryOp(data);

		// Modulus Operation
		while(data.cfml.forwardIfCurrent('^') || data.cfml.forwardIfCurrentAndNoWordAfter("exp")) {
			comments(data);
            expr=OpDouble.toExprDouble(expr, unaryOp(data), OpDouble.EXP);
		}
		return expr;
	}
	
	private Expression unaryOp(ExprData data) throws TemplateException {
		Expression expr = negatePlusMinusOp(data);
		
		// Plus Operation
		if (data.cfml.forwardIfCurrent("++") && expr instanceof Variable)			
			expr=_unaryOp(data,expr,OpDouble.PLUS);
		// Minus Operation
		else if (data.cfml.forwardIfCurrent("--") && expr instanceof Variable)	
			expr=_unaryOp(data,expr,OpDouble.MINUS);
		return expr;
	}
	
	private Expression _unaryOp(ExprData data,Expression expr,int op) throws TemplateException {
		Position leftEnd = expr.getEnd(),start=null,end=null;
		comments(data);
		if(leftEnd!=null){
			start=leftEnd;
			end=new Position(leftEnd.line, leftEnd.column+2, leftEnd.pos+2);
		}
		return new OPUnary((Variable)expr,LitDouble.ONE,OPUnary.POST,op,start,end);
		
		
		
		//ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,start,end),opr);
		//expr=new OpVariable((Variable)expr,res,data.cfml.getPosition());
		//return OpDouble.toExprDouble(expr,LitDouble.toExprDouble(1D,start,end),opr==OpDouble.PLUS? OpDouble.MINUS:OpDouble.PLUS);
	}
	
	
	

	/**
	* Negate Numbers
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression negatePlusMinusOp(ExprData data) throws TemplateException {
		// And Operation
		Position line=data.cfml.getPosition();
		if (data.cfml.forwardIfCurrent('-')) {
			// pre increment
			if (data.cfml.forwardIfCurrent('-')) {
				comments(data);
				Expression expr = clip(data);
				return new OPUnary((Variable)expr,LitDouble.ONE,OPUnary.PRE,OpDouble.MINUS,line,data.cfml.getPosition());
				
				//ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D),OpDouble.MINUS);
				//return new OpVariable((Variable)expr,res,data.cfml.getPosition());
				
				
			}
			comments(data);
			return OpNegateNumber.toExprDouble(clip(data),OpNegateNumber.MINUS,line,data.cfml.getPosition());
			
		}
		else if (data.cfml.forwardIfCurrent('+')) {
			if (data.cfml.forwardIfCurrent('+')) {
				comments(data);
				Expression expr = clip(data);
				
				return new OPUnary((Variable)expr,LitDouble.ONE,OPUnary.PRE,OpDouble.PLUS,line,data.cfml.getPosition());
				
				//ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D),OpDouble.PLUS);
				//return new OpVariable((Variable)expr,res,data.cfml.getPosition());
			}
			comments(data);
			return CastDouble.toExprDouble(clip(data));//OpNegateNumber.toExprDouble(clip(),OpNegateNumber.PLUS,line);
		}
		return clip(data);
	}

	/**
	* Verarbeitet Ausdr￼cke die inerhalb einer Klammer stehen.
	* <br />
	* EBNF:<br />
	* <code>("(" spaces impOp ")" spaces) | checker;</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression clip(ExprData data) throws TemplateException {
	    return checker(data);
	}
	/**
	* Hier werden die verschiedenen M￶glichen Werte erkannt 
	* und jenachdem wird mit der passenden Methode weitergefahren
	* <br />
	* EBNF:<br />
	* <code>string | number | dynamic | sharp;</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression checker(ExprData data) throws TemplateException {
		Expression expr=null;
			// String
			if((expr=string(data))!=null) {
				expr = subDynamic(data,expr);
				data.mode=STATIC;//(expr instanceof Literal)?STATIC:DYNAMIC;// STATIC
				return expr;
			} 
		// Number
			if((expr=number(data))!=null) {
				expr = subDynamic(data,expr);
				data.mode=STATIC;//(expr instanceof Literal)?STATIC:DYNAMIC;// STATIC
				return expr;
			}
			// closure
			if((expr=closure(data))!=null) {
				data.mode=DYNAMIC;
				return expr;
			} 
			
		// Dynamic
			if((expr=dynamic(data))!=null) {
				expr = newOp(data, expr);
				expr = subDynamic(data,expr);
				data.mode=DYNAMIC;
				return expr;
			} 
		// Sharp
			if((expr=sharp(data))!=null) {
				data.mode=DYNAMIC;
				return expr;
			} 
		// JSON
			if((expr=json(data,JSON_ARRAY,'[',']'))!=null) {
				expr = subDynamic(data,expr);
				data.mode=DYNAMIC;
				return expr;
			} 
			if((expr=json(data,JSON_STRUCT,'{','}'))!=null) {
				expr = subDynamic(data,expr);
				data.mode=DYNAMIC;
				return expr;
			} 
		// else Error
			throw new TemplateException(data.cfml,"Syntax Error, Invalid Construct");	
	}
	
	/*private Expression variable(Data data) throws TemplateException {
		Expression expr=null;
		
		// Dynamic
		if((expr=dynamic(data))!=null) {
			expr = subDynamic(data,expr);
			data.mode=DYNAMIC;
			return expr;
		} 
		return null;
	}*/
	
	/**
	* Transfomiert einen lierale Zeichenkette.
	* <br />
	* EBNF:<br />
	* <code>("'" {"##"|"''"|"#" impOp "#"| ?-"#"-"'" } "'") | 
	                 (""" {"##"|""""|"#" impOp "#"| ?-"#"-""" } """);</code>
	 * @param data 
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression string(ExprData data) throws TemplateException {
		
		// check starting character for a string literal
		if(!data.cfml.isCurrent('"')&& !data.cfml.isCurrent('\''))
			return null;
		Position line = data.cfml.getPosition();
		
		// Init Parameter
		char quoter = data.cfml.getCurrentLower();
		StringBuffer str=new StringBuffer();
		Expression expr=null;
		
		while(data.cfml.hasNext()) {
			data.cfml.next();
			// check sharp
			if(data.cfml.isCurrent('#')) {
				
				// Ecaped sharp
				if(data.cfml.isNext('#')){
					data.cfml.next();
					str.append('#');
				}
				// get Content of sharp
				else {
					data.cfml.next();
                    comments(data);
					Expression inner=assignOp(data);
                    comments(data);
					if (!data.cfml.isCurrent('#'))
						throw new TemplateException(data.cfml,"Invalid Syntax Closing [#] not found");
					
					ExprString exprStr=null;
					if(str.length()!=0) {
						exprStr=new LitString(str.toString(),line,data.cfml.getPosition());
						if(expr!=null){
							expr = OpString.toExprString(expr, exprStr);
						}
						else expr=exprStr;
						str=new StringBuffer();
					}
					if(expr==null) {
						expr=inner;
					}
					else  {
						expr = OpString.toExprString(expr, inner);
					}	
				}
			}
			// check quoter
			else if(data.cfml.isCurrent(quoter)) {
				// Ecaped sharp
				if(data.cfml.isNext(quoter)){
					data.cfml.next();
					str.append(quoter);
				}
				// finsish
				else {
					break;
				}				
			}
			// all other character
			else {
				str.append(data.cfml.getCurrent());
			}
		}
		if(!data.cfml.forwardIfCurrent(quoter))
			throw new TemplateException(data.cfml,"Invalid Syntax Closing ["+quoter+"] not found");
		
		if(expr==null)
			expr=new LitString(str.toString(),line,data.cfml.getPosition());
		else if(str.length()!=0) {
			expr = OpString.toExprString(expr, new LitString(str.toString(),line,data.cfml.getPosition()));
		}
        comments(data);
        
        if(expr instanceof Variable) {
        	Variable var=(Variable) expr;
        	var.setFromHash(true);
        }
        
		return expr;
		
	}

	/**
	* Transfomiert einen numerische Wert. 
	* Die L￤nge des numerischen Wertes interessiert nicht zu ￜbersetzungszeit, 
	* ein "Overflow" f￼hrt zu einem Laufzeitfehler. 
	* Da die zu erstellende CFXD, bzw. dieser Transfomer, keine Vorwegnahme des Laufzeitsystems vornimmt. 
	* <br />
	* EBNF:<br />
	* <code>["+"|"-"] digit {digit} {"." digit {digit}};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private LitDouble number(ExprData data) throws TemplateException {
		// check first character is a number literal representation
		if(!(data.cfml.isCurrentBetween('0','9') || data.cfml.isCurrent('.'))) return null;
		
		Position line = data.cfml.getPosition();
		StringBuffer rtn=new StringBuffer();
		
		// get digit on the left site of the dot
		if(data.cfml.isCurrent('.')) rtn.append('0');
		else rtn.append(digit(data));
		// read dot if exist
		if(data.cfml.forwardIfCurrent('.')) {
			rtn.append('.');
			String rightSite=digit(data);
			if(rightSite.length()> 0 && data.cfml.forwardIfCurrent('e')) {
				Boolean expOp=null;
				if(data.cfml.forwardIfCurrent('+')) expOp=Boolean.TRUE;
				else if(data.cfml.forwardIfCurrent('-')) expOp=Boolean.FALSE;
				
				if(data.cfml.isCurrentBetween('0','9')) {
					if(expOp==Boolean.FALSE) rightSite+="e-";
					else if(expOp==Boolean.TRUE) rightSite+="e+";
					else rightSite+="e";
			        rightSite+=digit(data);
			    }
			    else {
			    	if(expOp!=null) data.cfml.previous();
			        data.cfml.previous();
			    }
			}
			// read right side of the dot
			if(rightSite.length()==0)
				rightSite="0";//throw new TemplateException(cfml, "Number can't end with [.]"); // DIFF 23
			rtn.append(rightSite);
		}
        comments(data);
        
		try {
			return LitDouble.toExprDouble(Caster.toDoubleValue(rtn.toString()),line,data.cfml.getPosition());
		} catch (CasterException e) {
			throw new TemplateException(data.cfml,e.getMessage());
		}
		
	}
	
	
	
	/**
	* Liest die reinen Zahlen innerhalb des CFMLString aus und gibt diese als Zeichenkette zur￼ck. 
	* <br />
	* EBNF:<br />
	* <code>"0"|..|"9";</code>
	* @return digit Ausgelesene Zahlen als Zeichenkette.
	*/
	private String digit(ExprData data) {
		String rtn="";
		while (data.cfml.isValidIndex()) {
			if(!data.cfml.isCurrentBetween('0','9'))break;
			rtn+=data.cfml.getCurrentLower();
			data.cfml.next();
		}
		return rtn;
	}

	/**
	* Liest den folgenden idetifier ein und pr￼ft ob dieser ein boolscher Wert ist. 
	* Im Gegensatz zu CFMX wird auch "yes" und "no" als bolscher <wert akzeptiert, 
	* was bei CFMX nur beim Umwandeln einer Zeichenkette zu einem boolschen Wert der Fall ist.<br />
	* Wenn es sich um keinen bolschen Wert handelt wird der folgende Wert eingelesen mit seiner ganzen Hirarchie.
	* <br />
	* EBNF:<br />
	* <code>"true" | "false" | "yes" | "no" | startElement {("." identifier | "[" structElement "]" )[function] };</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Expression dynamic(ExprData data) throws TemplateException {
		// Die Implementation weicht ein wenig von der Grammatik ab, 
		// aber nicht in der Logik sondern rein wie es umgesetzt wurde.
		
	    
	    
		// get First Element of the Variable
		Position line = data.cfml.getPosition();
		Identifier id = identifier(data,false,true);
		if(id == null) {
		    if (!data.cfml.forwardIfCurrent('(')) return null;
		    
            comments(data);
			Expression expr = assignOp(data);

			if (!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(
					data.cfml,
					"Invalid Syntax Closing [)] not found");
            comments(data);
            return expr;//subDynamic(expr);
            
		}
			
		Variable var;
        comments(data);
		
		// Boolean constant 
		if(id.getString().equalsIgnoreCase("TRUE"))	{// || name.equals("YES"))	{
			comments(data);
			return new LitBoolean(true,line,data.cfml.getPosition());
		}
		else if(id.getString().equalsIgnoreCase("FALSE"))	{// || name.equals("NO"))	{
			comments(data);
			return new LitBoolean(false,line,data.cfml.getPosition());
		}
		else if(NullSupportHelper.full() && id.getString().equalsIgnoreCase("NULL"))	{
			comments(data);
			return new Null(line,data.cfml.getPosition());
		}
		
		// Extract Scope from the Variable
		//int c=data.cfml.getColumn();
		//Position l=data.cfml.getPosition();
		var = startElement(data,id,line);
		var.setStart(line);
		var.setEnd(data.cfml.getPosition());
		return var;
	}
	

	
	private Expression json(ExprData data,FunctionLibFunction flf, char start, char end) throws TemplateException {
		if(!data.cfml.forwardIfCurrent(start))return null;
		
		Position line = data.cfml.getPosition();
		BIF bif=new BIF(flf.getName(),flf);
		bif.setArgType(flf.getArgType());
		bif.setClass(flf.getClazz());
		bif.setReturnType(flf.getReturnTypeAsString());
		
		do {
			comments(data);
			if (data.cfml.isCurrent(end))break;
			
			bif.addArgument(functionArgument(data,data.settings.dotNotationUpper));
			comments(data);
		} 
		while (data.cfml.forwardIfCurrent(','));
		comments(data);
			
		if (!data.cfml.forwardIfCurrent(end))
			throw new TemplateException(data.cfml,"Invalid Syntax Closing ["+end+"] not found");
		comments(data);
		Variable var=new Variable(line,data.cfml.getPosition());
		var.addMember(bif);
		return var;
	}
	
	private Expression closure(ExprData data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent("function",'('))return null;
		data.cfml.previous();
		return new ClosureAsExpression((Closure) closurePart(data, "closure_"+CreateUniqueId.invoke(), Component.ACCESS_PUBLIC, "any", data.cfml.getPosition(),true));
	}
	
	protected  abstract Function closurePart(ExprData data, String id, int access, String rtnType, Position line,boolean closure) throws TemplateException;

	
	protected FunctionLibFunction getFLF(ExprData data,String name) {
		FunctionLibFunction flf=null;
		for (int i = 0; i < data.flibs.length; i++) {
			flf = data.flibs[i].getFunction(name);
			if (flf != null)
				break;
		}
		return flf;
	}

	private Expression subDynamic(ExprData data,Expression expr) throws TemplateException {
		
		
		

	    String name=null;
	    Invoker invoker=null;
		// Loop over nested Variables
		while (data.cfml.isValidIndex()) {
			ExprString nameProp = null,namePropUC = null;
			// .
			if (data.cfml.forwardIfCurrent('.')) {
				// Extract next Var String
                comments(data);
                Position line=data.cfml.getPosition();
                name = identifier(data,true);
				if(name==null) 
					throw new TemplateException(data.cfml, "Invalid identifier");
                comments(data);
				nameProp=Identifier.toIdentifier(name,line,data.cfml.getPosition());
				namePropUC=Identifier.toIdentifier(name,data.settings.dotNotationUpper?Identifier.CASE_UPPER:Identifier.CASE_ORIGNAL,line,data.cfml.getPosition());
			}
			// []
			else if (data.cfml.forwardIfCurrent('[')) {
				
				// get Next Var
				nameProp = structElement(data);
				namePropUC=nameProp;
				// Valid Syntax ???
				if (!data.cfml.forwardIfCurrent(']'))
					throw new TemplateException(
						data.cfml,
						"Invalid Syntax Closing []] not found");
			}
			/* / :
			else if (data.cfml.forwardIfCurrent(':')) {
				// Extract next Var String
                comments(data);
                int line=data.cfml.getLine();
				name = identifier(true,true);
				if(name==null) 
					throw new TemplateException(cfml, "Invalid identifier");
                comments(data);
                
				nameProp=LitString.toExprString(name,line);
			}*/
			// finish
			else {
				break;
			}

            comments(data);
            
            if(expr instanceof Invoker)  {
            	invoker=(Invoker) expr;
            }
            else {
            	invoker=new ExpressionInvoker(expr);
            	expr=invoker;
            }
			// Method
			if (data.cfml.isCurrent('(')) {
				if(nameProp==null && name!=null)nameProp=Identifier.toIdentifier(name, Identifier.CASE_ORIGNAL,null,null);// properly this is never used
				invoker.addMember(getFunctionMember(data,nameProp, false));
			}
			
			// property
			else invoker.addMember(new DataMember(namePropUC));
			
		}
		
		return expr;  
	}
	
	private Expression newOp(ExprData data,Expression expr) throws TemplateException {
		if(!(expr instanceof Variable)) return expr;
		Variable var=(Variable) expr;
		Member m= var.getFirstMember();
		if(!(m instanceof DataMember)) return expr;
		
		ExprString n = ((DataMember)m).getName();
		if(!(n instanceof LitString)) return expr;
		
		LitString ls=(LitString) n;
		
		
		if(!"new".equalsIgnoreCase(ls.getString())) return expr;
		
		int start=data.cfml.getPos();
	    String name=null;
	    
	    
	    // first identifier
	    name = identifier(data,true);
	    
		
		ExprString exprName;
		if(name!=null)	{
			StringBuilder fullName=new StringBuilder();
			fullName.append(name);
			// Loop over addional identifier
			while (data.cfml.isValidIndex()) {
				if (data.cfml.forwardIfCurrent('.')) {
					comments(data);
	                name = identifier(data,true);
					if(name==null) {
						data.cfml.setPos(start);
						return expr;//throw new TemplateException(data.cfml,"invalid Component declaration ");
					}
					fullName.append('.');
					fullName.append(name);
					comments(data);
				}
				else break;
			}
			
			exprName=LitString.toExprString(fullName.toString());
		}
		else {
			
			Expression str=string(data);
			if(str!=null){
				exprName=CastString.toExprString(str);
			}
			else {
				data.cfml.setPos(start);
				return expr;
			}
		}

        comments(data);
        
        if (data.cfml.isCurrent('(')) {
			FunctionMember func = getFunctionMember(data,Identifier.toIdentifier("_createComponent",Identifier.CASE_ORIGNAL,null,null), true);
			func.addArgument(new Argument(exprName,"string"));
			Variable v=new Variable(expr.getStart(),expr.getEnd());
			v.addMember(func);
            comments(data);
			return v;
		} 
        data.cfml.setPos(start);
        return expr;//throw new TemplateException(data.cfml,"invalid Component declaration ");
		
	}
	
	
	
	
	
	/**
	* Extrahiert den Start Element einer Variale, 
	* dies ist entweder eine Funktion, eine Scope Definition oder eine undefinierte Variable. 
	* <br />
	* EBNF:<br />
	* <code>identifier "(" functionArg ")" | scope | identifier;</code>
	* @param name Einstiegsname
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private Variable startElement(ExprData data,Identifier name, Position line) throws TemplateException {
		
		
		
		
		// check function
		if (data.cfml.isCurrent('(')) {
			FunctionMember func = getFunctionMember(data,name, true);
			
			Variable var=new Variable(line,data.cfml.getPosition());
			var.addMember(func);
            comments(data);
			return var;
		} 
		
		//check scope
		Variable var = scope(data,name,line);
		if(var!=null) return var;
		
		// undefined variable
		var=new Variable(line,data.cfml.getPosition());
		var.addMember(new DataMember(name));

        comments(data);
		return var;
		
	}
	
	/**
	* Liest einen CFML Scope aus, 
	* falls der folgende identifier keinem Scope entspricht, 
	* gibt die Variable null zur￼ck.
	* <br />
	* EBNF:<br />
	* <code>"variable" | "cgi" | "url" | "form" | "session" | "application" | "arguments" | "cookie" | " client";</code>
	 * @param id String identifier,
	 * wird aus Optimierungszwechen nicht innerhalb dieser Funktion ausgelsen.
	 * @return CFXD Variable Element oder null
	 * @throws TemplateException 
	*/
	private Variable scope(ExprData data,Identifier id, Position line) throws TemplateException {
		String idStr=id.getUpper();
		if(data.ignoreScopes)return null;
		if (idStr.equals("CGI")) 				return new Variable(Scope.SCOPE_CGI,line,data.cfml.getPosition());
		else if (idStr.equals("ARGUMENTS"))  	return new Variable(Scope.SCOPE_ARGUMENTS,line,data.cfml.getPosition());
		else if (idStr.equals("REQUEST"))		return new Variable(Scope.SCOPE_REQUEST,line,data.cfml.getPosition());
		else if (idStr.equals("SESSION"))		return new Variable(Scope.SCOPE_SESSION,line,data.cfml.getPosition());
		else if (idStr.equals("APPLICATION"))	return new Variable(Scope.SCOPE_APPLICATION,line,data.cfml.getPosition());
		else if (idStr.equals("VARIABLES"))		return new Variable(Scope.SCOPE_VARIABLES,line,data.cfml.getPosition());
		else if (idStr.equals("FORM")) 			return new Variable(Scope.SCOPE_FORM,line,data.cfml.getPosition());
		else if (idStr.equals("URL"))			return new Variable(Scope.SCOPE_URL,line,data.cfml.getPosition());
		else if (idStr.equals("SERVER")) 		return new Variable(Scope.SCOPE_SERVER,line,data.cfml.getPosition());
		else if (idStr.equals("CLIENT"))		return new Variable(Scope.SCOPE_CLIENT,line,data.cfml.getPosition());
		else if (idStr.equals("COOKIE"))		return new Variable(Scope.SCOPE_COOKIE,line,data.cfml.getPosition());
		else if (idStr.equals("CLUSTER"))		return new Variable(Scope.SCOPE_CLUSTER,line,data.cfml.getPosition());
		else if (idStr.equals("LOCAL"))			return new Variable(Scope.SCOPE_LOCAL,line,data.cfml.getPosition());
		else if (idStr.equals("VAR")) {
			Identifier _id = identifier(data,false,true);
			if(_id!=null){
				comments(data);
				Variable local = new Variable(ScopeSupport.SCOPE_VAR,line,data.cfml.getPosition());
				if(!"LOCAL".equalsIgnoreCase(_id.getString()))local.addMember(new DataMember(_id));
				else {
					local.ignoredFirstMember(true);
				}
				return local;
			}
		} 
		return null;
	}
    
	/**
	* Liest einen Identifier aus und gibt diesen als String zur￼ck.
	* <br />
	* EBNF:<br />
	* <code>(letter | "_") {letter | "_"|digit};</code>
	 * @param firstCanBeNumber 
	 * @param upper
	* @return Identifier.
	*/
	protected Identifier identifier(ExprData data,boolean firstCanBeNumber,boolean upper) {
		Position start = data.cfml.getPosition();
		if(!data.cfml.isCurrentLetter() && !data.cfml.isCurrentSpecial() ) {
		    if(!firstCanBeNumber) return null;
            else if(!data.cfml.isCurrentBetween('0','9'))return null;
        }
		do {
			data.cfml.next();
			if(!(data.cfml.isCurrentLetter()
				|| data.cfml.isCurrentBetween('0','9')
				|| data.cfml.isCurrentSpecial())) {
					break;
				}
		}
		while (data.cfml.isValidIndex());
		return Identifier.toIdentifier(data.cfml.substring(start.pos,data.cfml.getPos()-start.pos), 
				upper && data.settings.dotNotationUpper?Identifier.CASE_UPPER:Identifier.CASE_ORIGNAL, start,data.cfml.getPosition());
	}
	
	protected String identifier(ExprData data,boolean firstCanBeNumber) {
		int start = data.cfml.getPos();
		if(!data.cfml.isCurrentLetter() && !data.cfml.isCurrentSpecial() ) {
		    if(!firstCanBeNumber) return null;
            else if(!data.cfml.isCurrentBetween('0','9'))return null;
        }
		do {
			data.cfml.next();
			if(!(data.cfml.isCurrentLetter()
				|| data.cfml.isCurrentBetween('0','9')
				|| data.cfml.isCurrentSpecial())) {
					break;
				}
		}
		while (data.cfml.isValidIndex());
		return data.cfml.substring(start,data.cfml.getPos()-start);
	}

	/**
	* Transfomiert ein Collection Element das in eckigen Klammern aufgerufen wird. 
	* <br />
	* EBNF:<br />
	* <code>"[" impOp "]"</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private ExprString structElement(ExprData data) throws TemplateException {
        comments(data);
        ExprString name = CastString.toExprString(assignOp(data));
		if(name instanceof LitString)((LitString)name).fromBracket(true);
        comments(data);
		return name;
	}

	/**
	* Liest die Argumente eines Funktonsaufruf ein und pr￼ft ob die Funktion 
	* innerhalb der FLD (Function Library Descriptor) definiert ist. 
	* Falls sie existiert wird die Funktion gegen diese gepr￼ft und ein build-in-function CFXD Element generiert, 
	* ansonsten ein normales funcion-call Element.
	* <br />
	* EBNF:<br />
	* <code>[impOp{"," impOp}];</code>
	* @param name Identifier der Funktion als Zeichenkette
	* @param checkLibrary Soll gepr￼ft werden ob die Funktion innerhalb der Library existiert.
	* @return CFXD Element
	* @throws TemplateException 
	*/
	private FunctionMember getFunctionMember(ExprData data,
			final ExprString name,
		boolean checkLibrary)
		throws TemplateException {

		// get Function Library
		checkLibrary=checkLibrary && data.flibs!=null;
		FunctionLibFunction flf = null;
		if (checkLibrary) {
			if(!(name instanceof Literal))
				throw new TemplateException(data.cfml,"syntax error"); // should never happen!
			
			for (int i = 0; i < data.flibs.length; i++) {
				flf = data.flibs[i].getFunction(((Literal)name).getString());
				if (flf != null)break;
			}
			if (flf == null) {
				checkLibrary = false;
			}
		}
		// Element Function
		FunctionMember fm;
		if(checkLibrary) {
			BIF bif=new BIF(name,flf);
			bif.setArgType(flf.getArgType());
			bif.setClass(flf.getClazz());
			bif.setReturnType(flf.getReturnTypeAsString());
			fm=bif;
			
			if(flf.getArgType()== FunctionLibFunction.ARG_DYNAMIC && flf.hasDefaultValues()){
        		ArrayList<FunctionLibFunctionArg> args = flf.getArg();
				Iterator<FunctionLibFunctionArg> it = args.iterator();
        		FunctionLibFunctionArg arg;
        		while(it.hasNext()){
        			arg=it.next();
        			if(arg.getDefaultValue()!=null)
        				bif.addArgument(
        						new NamedArgument(
        								LitString.toExprString(arg.getName()),
        								LitString.toExprString(arg.getDefaultValue()),
        								arg.getTypeAsString(),false
        								));
        		}
			}
		}
		else {
			fm = new UDF(name);
		}
		
		
		

		// Function Attributes
		ArrayList<FunctionLibFunctionArg> arrFuncLibAtt = null;
		int libLen = 0;
		if (checkLibrary) {
			arrFuncLibAtt = flf.getArg();
			libLen = arrFuncLibAtt.size();
		}
		int count = 0;
		do {
			data.cfml.next();
            comments(data);

			// finish
			if (count==0 && data.cfml.isCurrent(')'))
				break;

			// too many Attributes
			boolean isDynamic=false;
			int max=-1;
			if(checkLibrary) {
				isDynamic=flf.getArgType()==FunctionLibFunction.ARG_DYNAMIC;
				max=flf.getArgMax();
			// Dynamic
				if(isDynamic) {
					if(max!=-1 && max <= count)
						throw new TemplateException(
							data.cfml,
							"too many Attributes in function [" + ASMUtil.display(name) + "]");
				}
			// Fix
				else {
					if(libLen <= count){
						
						TemplateException te = new TemplateException(
							data.cfml,
							"too many Attributes in function call [" + ASMUtil.display(name) + "]");
						UDFUtil.addFunctionDoc(te, flf);
						throw te;
					}
				}
				
			}
			
			//Argument arg;
			if (checkLibrary && !isDynamic) {
				// current attribues from library
				FunctionLibFunctionArg funcLibAtt =arrFuncLibAtt.get(count);
				fm.addArgument(functionArgument(data,funcLibAtt.getTypeAsString(),false));	
			} 
			else {
				fm.addArgument(functionArgument(data,false));
			}

            comments(data);
			count++;
			if (data.cfml.isCurrent(')'))
				break;
		} 
		while (data.cfml.isCurrent(','));

		// end with ) ??		
		if (!data.cfml.forwardIfCurrent(')'))
			throw new TemplateException(
				data.cfml,
				"Invalid Syntax Closing [)] for function ["
					+ ASMUtil.display(name)
					+ "] not found");

		// check min attributes
		if (checkLibrary && flf.getArgMin() > count){
			TemplateException te = new TemplateException(
				data.cfml,
				"too few attributes in function [" + ASMUtil.display(name) + "]");
			if(flf.getArgType()==FunctionLibFunction.ARG_FIX) UDFUtil.addFunctionDoc(te, flf);
			throw te;
		}

        comments(data);
        
        // evaluator
        if(checkLibrary && flf.hasTteClass()){
        	flf.getEvaluator().evaluate((BIF) fm, flf);
        }
        
		return fm;
	}
	
	/**
	 * Sharps (#) die innerhalb von Expressions auftauchen haben in CFML keine weitere Beteutung 
	 * und werden durch diese Methode einfach entfernt.
	 * <br />
	 * Beispiel:<br />
	 * <code>arrayLen(#arr#)</code> und <code>arrayLen(arr)</code> sind identisch.
	 * EBNF:<br />
	 * <code>"#" checker "#";</code>
	 * @return CFXD Element
	 * @throws TemplateException 
	*/
	private Expression sharp(ExprData data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent('#'))
			return null;
		Expression expr;
        comments(data);
        boolean old=data.allowLowerThan;
        data.allowLowerThan=true;
		expr = assignOp(data);
		data.allowLowerThan=old;
        comments(data);
		if (!data.cfml.forwardIfCurrent('#'))
			throw new TemplateException(
				data.cfml,
				"Syntax Error, Invalid Construct "+(data.cfml.length()<30?data.cfml.toString():""));
        comments(data);
		return expr;
	}
	
	/**
	 * @param data 
	 * @return parsed Element
	 * @throws TemplateException
	 */
	private Expression simple(ExprData data,String[] breakConditions) throws TemplateException {
		StringBuffer sb=new StringBuffer();
		Position line = data.cfml.getPosition();
		outer:while(data.cfml.isValidIndex()) {
			for(int i=0;i<breakConditions.length;i++){
				if(data.cfml.isCurrent(breakConditions[i]))break outer;
			}
			
			//if(data.cfml.isCurrent(' ') || data.cfml.isCurrent('>') || data.cfml.isCurrent("/>")) break;
			
			if(data.cfml.isCurrent('"') || data.cfml.isCurrent('#') || data.cfml.isCurrent('\'')) {
				throw new TemplateException(data.cfml,"simple attribute value can't contain ["+data.cfml.getCurrent()+"]");
			}
			sb.append(data.cfml.getCurrent());
			data.cfml.next();
		}
        comments(data);
		
		return LitString.toExprString(sb.toString(),line,data.cfml.getPosition());
	}
    

    /**
     *  Liest alle folgenden Komentare ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @param data 
     * @throws TemplateException
     */
    protected void comments(ExprData data) throws TemplateException {
        data.cfml.removeSpace();
        while(comment(data)){data.cfml.removeSpace();}
    }
    
    /**
     *  Liest einen Einzeiligen Kommentar ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @return bool Wurde ein Kommentar entfernt?
     * @throws TemplateException
     */
    private boolean comment(ExprData data) throws TemplateException {
        if(singleLineComment(data.cfml) || multiLineComment(data) || CFMLTransformer.comment(data.cfml)) return true;
        return false;
    }

    /**
     * Liest einen Mehrzeiligen Kommentar ein.
     * <br />
     * EBNF:<br />
     * <code>?-"*<!-- -->/";</code>
     * @return bool Wurde ein Kommentar entfernt?
     * @throws TemplateException
     */
    private boolean multiLineComment(ExprData data) throws TemplateException {
       CFMLString cfml = data.cfml;
    	if(!cfml.forwardIfCurrent("/*")) return false;
        int pos=cfml.getPos();
        boolean isDocComment=cfml.isCurrent('*');
        while(cfml.isValidIndex()) {
            if(cfml.isCurrent("*/")) break;
            cfml.next();
        }
        if(!cfml.forwardIfCurrent("*/")){
            cfml.setPos(pos);
            throw new TemplateException(cfml,"comment is not closed");
        }
        if(isDocComment) {
        	String comment = cfml.substring(pos-2,cfml.getPos()-pos);
        	data.docComment=docCommentTransformer.transform(comment);
        }
        return true;
    }
    
    
    
    /**
     *  Liest einen Einzeiligen Kommentar ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @return bool Wurde ein Kommentar entfernt?
     */
    private boolean singleLineComment(CFMLString cfml) {
        if(!cfml.forwardIfCurrent("//")) return false;
        return cfml.nextLine();
    }

}