package railo.transformer.cfml.expression;


import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.exp.CasterException;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.runtime.type.Scope;
import railo.runtime.type.scope.ScopeSupport;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastDouble;
import railo.transformer.bytecode.cast.CastString;
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
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.op.OPDecision;
import railo.transformer.bytecode.op.OpBool;
import railo.transformer.bytecode.op.OpContional;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.bytecode.op.OpNegate;
import railo.transformer.bytecode.op.OpNegateNumber;
import railo.transformer.bytecode.op.OpString;
import railo.transformer.bytecode.op.OpVariable;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;
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
public class CFMLExprTransformer implements ExprTransformer {

	private static final short STATIC=0;
	private static final short DYNAMIC=1;
	private static FunctionLibFunction JSON_ARRAY = null;
	private static FunctionLibFunction JSON_STRUCT = null;

	public static final short CTX_OTHER = -1;
	public static final short CTX_NONE = 0;
	public static final short CTX_IF = 1;
	public static final short CTX_ELSE_IF = 2;
	public static final short CTX_ELSE = 3;
	public static final short CTX_FOR = 4;
	public static final short CTX_WHILE = 5;
	public static final short CTX_DO_WHILE = 6;
	public static final short CTX_CFC = 7;
	public static final short CTX_INTERFACE = 8;
	public static final short CTX_FUNCTION = 9;
	public static final short CTX_BLOCK = 10;
	public static final short CTX_FINALLY = 11;
	public static final short CTX_SWITCH = 12;
	public static final short CTX_TRY = 13;
	public static final short CTX_CATCH = 14;
	public static final short CTX_TRANSACTION = 15;
	public static final short CTX_THREAD = 16;
	public static final short CTX_SAVECONTENT = 17;
	public static final short CTX_LOCK = 18;
	public static final short CTX_LOOP = 19;
	public static final short CTX_QUERY = 20;
	public static final short CTX_ZIP = 21;
	
	
	/*private short mode=0;
	protected CFMLString cfml;
	protected FunctionLib[] fld;
	private boolean ignoreScopes=false;
	private boolean allowLowerThan;*/
	
	public class Data {
		
		private short mode=0;
		public CFMLString cfml;
		public FunctionLib[] fld;
		private boolean ignoreScopes=false;
		private boolean allowLowerThan;
		public boolean insideFunction;
		public String tagName;
		public boolean isCFC;
		public boolean isInterface;
		public EvaluatorPool ep;
		public short context=CTX_NONE;
		
		public Data(EvaluatorPool ep, CFMLString cfml, FunctionLib[] fld,boolean allowLowerThan) {
			this.ep=ep;
			this.cfml=cfml;
			this.fld=fld;
			this.allowLowerThan=allowLowerThan;
		}
	}
	
	/**
	 * Wird aufgerufen um aus dem ￼bergebenen CFMLString einen Ausdruck auszulesen 
	 * und diesen in ein CFXD Element zu ￼bersetzten.
	 * <br />
	 * Beispiel eines ￼bergebenen String:<br />
	 * <code>session.firstName</code> oder <code>trim(left('test'&var1,3))</code>
	 * <br />
	 * EBNF:<br />
	 * <code>spaces impOp;</code>
	 * 
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des CFML Codes erkennen 
	 * und validieren.
	 * @param doc XML Document des aktuellen zu erstellenden CFXD
	 * @param cfml Text der transfomiert werden soll.
	 * @return Element CFXD Element
	 * @throws TemplateException
	 */
	public Expression transform(EvaluatorPool ep,FunctionLib[] fld, CFMLString cfml) throws TemplateException {
		
		// Init Parameter
		Data data = init(ep,fld, cfml,false);

		// parse the houle Page String
        comments(data.cfml);
		//Expression expr = assignOp();

		// return the Root Element of the Document	
		return assignOp(data);
	}
	
	/**
	 * @see railo.transformer.data.cfml.ExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transformAsString(EvaluatorPool ep,FunctionLib[] fld, CFMLString cfml, boolean allowLowerThan) throws TemplateException {
		
		return transformAsString(init(ep,fld, cfml,allowLowerThan),new String[]{" ", ">", "/>"});
	}
	
	protected Expression transformAsString(Data data,String[] breakContitions) throws TemplateException {
		Expression el=null;
		
		// parse the houle Page String
        comments(data.cfml);		
				
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
			return simple(data,breakContitions);
	}
	
	

	/**
	 * Initialmethode, wird aufgerufen um den internen Zustand des Objektes zu setzten.
	 * @param fld Function Libraries zum validieren der Funktionen
	 * @param doc XML Document des aktuellen CFXD
	 * @param cfml CFML Code der transfomiert werden soll.
	 */
	protected Data init(EvaluatorPool ep,FunctionLib[] fld, CFMLString cfml, boolean allowLowerThan) {
		Data data = new Data(ep,cfml,fld,allowLowerThan);
		if(JSON_ARRAY==null)JSON_ARRAY=getFLF(data,"_jsonArray");
		if(JSON_STRUCT==null)JSON_STRUCT=getFLF(data,"_jsonStruct");
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
	protected Expression expression(Data data) throws TemplateException {
		return assignOp(data);
	}

	/**
	* Liest einen gelableten  Funktionsparamter ein
	* <br />
	* EBNF:<br />
	* <code>assignOp [":" spaces assignOp];</code>
	 * @param type 
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Argument functionArgument(Data data) throws TemplateException {
		return functionArgument(data,null);
	}
	
	protected Argument functionArgument(Data data,String type) throws TemplateException {
		Expression expr = assignOp(data);
		try{
			if (data.cfml.forwardIfCurrent(":")) {
				comments(data.cfml);
	            return new NamedArgument(expr,assignOp(data),type);
			}
			else if(expr instanceof DynAssign){
				DynAssign da=(DynAssign) expr;
				return new NamedArgument(da.getName(),da.getValue(),type);
			}
			else if(expr instanceof Assign && !(expr instanceof OpVariable)){
				Assign a=(Assign) expr;
				return new NamedArgument(a.getVariable(),a.getValue(),type);
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
	protected Expression assignOp(Data data) throws TemplateException {
        
		Expression expr = conditionalOp(data);
        if (data.cfml.forwardIfCurrent('=')) {
        	
            comments(data.cfml);
            if(data.mode==STATIC) expr=new DynAssign(expr,assignOp(data));
			else {
				if(expr instanceof Variable)
					expr=new Assign((Variable)expr,assignOp(data));
				else
					throw new TemplateException(data.cfml,"invalid assignment left-hand side ("+expr.getClass().getName()+")");
			}
		}
		return expr;
	}
	
	protected Expression conditionalOp(Data data) throws TemplateException {
        
		Expression expr = impOp(data);
        if (data.cfml.forwardIfCurrent('?')) {
        	comments(data.cfml);
        	Expression left = assignOp(data);
        	comments(data.cfml);
        	if(!data.cfml.forwardIfCurrent(':'))throw new TemplateException("invalid conditional operator");
        	comments(data.cfml); 
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
	protected Expression impOp(Data data) throws TemplateException {
		Expression expr = eqvOp(data);
		while(data.cfml.forwardIfCurrentAndNoWordAfter("imp")) {
			comments(data.cfml);
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
	protected Expression eqvOp(Data data) throws TemplateException {
		Expression expr = xorOp(data);
		while(data.cfml.forwardIfCurrent("eqv")) {
			comments(data.cfml);
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
	protected Expression xorOp(Data data) throws TemplateException {
		Expression expr = orOp(data);
		while(data.cfml.forwardIfCurrent("xor")) {
			comments(data.cfml);
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
	protected Expression orOp(Data data) throws TemplateException {
		Expression expr = andOp(data);
		
		while(data.cfml.forwardIfCurrent("||") || data.cfml.forwardIfCurrent("or")) {
			comments(data.cfml);
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
	protected Expression andOp(Data data) throws TemplateException {
		Expression expr = notOp(data);
		
		while(data.cfml.forwardIfCurrent("&&") || data.cfml.forwardIfCurrent("and")) {
			comments(data.cfml);
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
	protected Expression notOp(Data data) throws TemplateException {
		// And Operation
		int line=data.cfml.getLine();
		if (data.cfml.isCurrent('!') && !data.cfml.isCurrent("!=")) {
			data.cfml.next();
			comments(data.cfml);
			return OpNegate.toExprBoolean(notOp(data),line);
		}
		else if (data.cfml.forwardIfCurrentAndNoWordAfter("not")) {
			comments(data.cfml);
			return OpNegate.toExprBoolean(notOp(data),line);
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
	protected Expression decsionOp(Data data) throws TemplateException {

		Expression expr = concatOp(data);
		boolean hasChanged=false;
		// ct, contains
		do {
			hasChanged=false;
			if(data.cfml.isCurrent('c')) {
					if (data.cfml.forwardIfCurrent("ct")) {expr = decisionOpCreate(data,OPDecision.CT,expr);hasChanged=true;} 
					else if (data.cfml.forwardIfCurrent("contains")){ expr = decisionOpCreate(data,OPDecision.CT,expr);hasChanged=true;}
			}
			// does not contain
			else if (data.cfml.forwardIfCurrent("does","not","contain")){ expr = decisionOpCreate(data,OPDecision.NCT,expr); hasChanged=true;}

			// equal, eq
			else if (data.cfml.isCurrent("eq") && !data.cfml.isCurrent("eqv")) {
				data.cfml.setPos(data.cfml.getPos()+2);
				data.cfml.forwardIfCurrent("ual");
				expr = decisionOpCreate(data,OPDecision.EQ,expr);
				hasChanged=true;
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
					if(data.cfml.forwardIfCurrent("e")) expr = decisionOpCreate(data,OPDecision.GTE,expr);
					else expr = decisionOpCreate(data,OPDecision.GT,expr);
					hasChanged=true;
				} 
				else if (data.cfml.forwardIfCurrent("greater", "than")) {
					if(data.cfml.forwardIfCurrent(" or equal to")) expr = decisionOpCreate(data,OPDecision.GTE,expr);
					else expr = decisionOpCreate(data,OPDecision.GT,expr);
					hasChanged=true;
				}	
				else if (data.cfml.forwardIfCurrent("ge")) {
					expr = decisionOpCreate(data,OPDecision.GTE,expr);
					hasChanged=true;
				}				
			}
			
			// is, is not
			else if (data.cfml.forwardIfCurrent("is")) {
				if(data.cfml.forwardIfCurrent(" not")) expr = decisionOpCreate(data,OPDecision.NEQ,expr);
				else expr = decisionOpCreate(data,OPDecision.EQ,expr);
				hasChanged=true;
			}
			
			// lt, lte, less than, less than or equal to
			else if (data.cfml.isCurrent('l')) {
				if (data.cfml.forwardIfCurrent("lt")) {
					if(data.cfml.forwardIfCurrent("e")) expr = decisionOpCreate(data,OPDecision.LTE,expr);
					else expr = decisionOpCreate(data,OPDecision.LT,expr);
					hasChanged=true;
				} 
				else if (data.cfml.forwardIfCurrent("less than")) {
					if(data.cfml.forwardIfCurrent(" or equal to")) expr = decisionOpCreate(data,OPDecision.LTE,expr);
					else expr = decisionOpCreate(data,OPDecision.LT,expr);
					hasChanged=true;
				}	
				else if (data.cfml.forwardIfCurrent("le")) {
					expr = decisionOpCreate(data,OPDecision.LTE,expr);
					hasChanged=true;
				}				
			}
			
			// neq, not equal, nct
			else if (data.cfml.isCurrent('n')) {
				// Not Equal
					if (data.cfml.forwardIfCurrent("neq")){ expr = decisionOpCreate(data,OPDecision.NEQ,expr); hasChanged=true;}
				// Not Equal (Alias)
					else if (data.cfml.forwardIfCurrent("not equal")){ expr = decisionOpCreate(data,OPDecision.NEQ,expr);hasChanged=true; }
				// nct
					else if (data.cfml.forwardIfCurrent("nct")){ expr = decisionOpCreate(data,OPDecision.NCT,expr); hasChanged=true;}	
			}
			
		}
		while(hasChanged);
		return expr;
	}
	private Expression decisionOpCreate(Data data,int operation, Expression left) throws TemplateException {
        comments(data.cfml);
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
	protected Expression concatOp(Data data) throws TemplateException {
		Expression expr = plusMinusOp(data);
		
		while(data.cfml.isCurrent('&') && !data.cfml.isCurrent("&&")) {
			data.cfml.next();
			
			// &=
			if (data.cfml.isCurrent('=') && expr instanceof Variable) {
				data.cfml.next();
				comments(data.cfml);
				Expression right = assignOp(data);
				ExprString res = OpString.toExprString(expr, right);
				expr=new OpVariable((Variable)expr,res);
			}
			else {
	            comments(data.cfml);
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
	protected Expression plusMinusOp(Data data) throws TemplateException {
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
	
	

	private Expression _plusMinusOp(Data data,Expression expr,int opr) throws TemplateException {
		// +=
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data.cfml);
			Expression right = assignOp(data);
			ExprDouble res = OpDouble.toExprDouble(expr, right,opr);
			expr=new OpVariable((Variable)expr,res);
		}
		/*/ ++
		else if (data.cfml.isCurrent(opr==OpDouble.PLUS?'+':'-') && expr instanceof Variable) {
			data.cfml.next();
			comments(data.cfml);
			ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),opr);
			expr=new OpVariable((Variable)expr,res);
			expr=OpDouble.toExprDouble(expr,LitDouble.toExprDouble(1D, -1),opr==OpDouble.PLUS? OpDouble.MINUS:OpDouble.PLUS);
			//comments(data.cfml);
		}*/
		else {
			comments(data.cfml);
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
	protected Expression modOp(Data data) throws TemplateException {
		Expression expr = divMultiOp(data);
		
		// Modulus Operation
		while(data.cfml.forwardIfCurrent('%') || data.cfml.forwardIfCurrent("mod")) {
			expr=_modOp(data,expr);
			//comments(data.cfml);
            //expr=OpDouble.toExprDouble(expr, divMultiOp(), OpDouble.MODULUS);
		}
		return expr;
	}
	
	private Expression _modOp(Data data,Expression expr) throws TemplateException {
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data.cfml);
			Expression right = assignOp(data);
			ExprDouble res = OpDouble.toExprDouble(expr, right,OpDouble.MODULUS);
			return new OpVariable((Variable)expr,res);
		}
        comments(data.cfml);
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
	protected Expression divMultiOp(Data data) throws TemplateException {
		Expression expr = expoOp(data);

		while (!data.cfml.isLast()) {
			
				// Multiply Operation
				if(data.cfml.forwardIfCurrent('*')) {
					expr=_divMultiOp(data,expr,OpDouble.MULTIPLY);
					//comments(data.cfml);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.MULTIPLY);
				}
				// Divide Operation
				else if (data.cfml.isCurrent('/') && (!data.cfml.isCurrent('/','>') )) {
					data.cfml.next(); 
					expr=_divMultiOp(data,expr,OpDouble.DIVIDE);
					//comments(data.cfml);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.DIVIDE);
				}
				// Divide Operation
				else if (data.cfml.isCurrent('\\')) {
					data.cfml.next(); 
					expr=_divMultiOp(data,expr,OpDouble.INTDIV);
					//comments(data.cfml);
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.INTDIV);
				}
				else {
					break;
				}
			
		}
		return expr;
	}

	private Expression _divMultiOp(Data data,Expression expr, int iOp) throws TemplateException {
		if (data.cfml.isCurrent('=') && expr instanceof Variable) {
			data.cfml.next();
			comments(data.cfml);
			Expression right = assignOp(data);
			ExprDouble res = OpDouble.toExprDouble(expr, right,iOp);
			return new OpVariable((Variable)expr,res);
		}
        comments(data.cfml);
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
	protected Expression expoOp(Data data) throws TemplateException {
		Expression expr = unaryOp(data);

		// Modulus Operation
		while(data.cfml.forwardIfCurrent('^') || data.cfml.forwardIfCurrent("exp")) {
			comments(data.cfml);
            expr=OpDouble.toExprDouble(expr, unaryOp(data), OpDouble.EXP);
		}
		return expr;
	}
	
	protected Expression unaryOp(Data data) throws TemplateException {
		Expression expr = negatePlusMinusOp(data);
		
		// Plus Operation
		if (data.cfml.forwardIfCurrent("++") && expr instanceof Variable)			
			expr=_unaryOp(data,expr,OpDouble.PLUS);
		// Minus Operation
		else if (data.cfml.forwardIfCurrent("--") && expr instanceof Variable)	
			expr=_unaryOp(data,expr,OpDouble.MINUS);
		return expr;
	}
	
	private Expression _unaryOp(Data data,Expression expr,int opr) throws TemplateException {
		comments(data.cfml);
		ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),opr);
		expr=new OpVariable((Variable)expr,res);
		return OpDouble.toExprDouble(expr,LitDouble.toExprDouble(1D, -1),opr==OpDouble.PLUS? OpDouble.MINUS:OpDouble.PLUS);
	}
	
	
	

	/**
	* Negate Numbers
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression negatePlusMinusOp(Data data) throws TemplateException {
		// And Operation
		int line=data.cfml.getLine();
		if (data.cfml.forwardIfCurrent('-')) {
			if (data.cfml.forwardIfCurrent('-')) {
				comments(data.cfml);
				Expression expr = clip(data);
				ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),OpDouble.MINUS);
				return new OpVariable((Variable)expr,res);
			}
			comments(data.cfml);
			return OpNegateNumber.toExprDouble(clip(data),OpNegateNumber.MINUS,line);
			
		}
		else if (data.cfml.forwardIfCurrent('+')) {
			if (data.cfml.forwardIfCurrent('+')) {
				comments(data.cfml);
				Expression expr = clip(data);
				ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),OpDouble.PLUS);
				return new OpVariable((Variable)expr,res);
			}
			comments(data.cfml);
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
	protected Expression clip(Data data) throws TemplateException {
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
	protected Expression checker(Data data) throws TemplateException {
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
		// Dynamic
			if((expr=dynamic(data))!=null) {
				expr = newOp(data, expr);
				//if(res==expr)
					expr = subDynamic(data,expr);
				//else expr=res;
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
				data.mode=DYNAMIC;
				return expr;
			} 
			if((expr=json(data,JSON_STRUCT,'{','}'))!=null) {
				data.mode=DYNAMIC;
				return expr;
			} 
		// else Error
			throw new TemplateException(data.cfml,"Syntax Error, Invalid Construct");	
	}
	
	protected Expression variable(Data data) throws TemplateException {
		Expression expr=null;
		
		// Dynamic
		if((expr=dynamic(data))!=null) {
			expr = subDynamic(data,expr);
			data.mode=DYNAMIC;
			return expr;
		} 
		return null;
	}
	
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
	protected Expression string(Data data) throws TemplateException {
		
		// check starting character for a string literal
		if(!data.cfml.isCurrent('"')&& !data.cfml.isCurrent('\''))
			return null;
		int line=data.cfml.getLine();
		
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
                    comments(data.cfml);
					Expression inner=assignOp(data);
                    comments(data.cfml);
					if (!data.cfml.isCurrent('#'))
						throw new TemplateException(data.cfml,"Invalid Syntax Closing [#] not found");
					
					ExprString exprStr=null;
					if(str.length()!=0) {
						exprStr=new LitString(str.toString(),line);
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
			expr=new LitString(str.toString(),line);
		else if(str.length()!=0) {
			expr = OpString.toExprString(expr, new LitString(str.toString(),line));
		}
        comments(data.cfml);
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
	protected LitDouble number(Data data) throws TemplateException {
		// check first character is a number literal representation
		if(!(data.cfml.isCurrentBetween('0','9') || data.cfml.isCurrent('.'))) return null;
		
		int line=data.cfml.getLine();
		StringBuffer rtn=new StringBuffer();
		
		// get digit on the left site of the dot
		if(data.cfml.isCurrent('.')) rtn.append('0');
		else rtn.append(digit(data));
		// read dot if exist
		if(data.cfml.forwardIfCurrent('.')) {
			rtn.append('.');
			String rightSite=digit(data);
			if(rightSite.length()> 0 && data.cfml.forwardIfCurrent('e')) {
			    if(data.cfml.isCurrentBetween('0','9')) {
			        rightSite+='e'+digit(data);
			    }
			    else {
			        data.cfml.previous();
			    }
			}
			// read right side of the dot
			if(rightSite.length()==0)
				rightSite="0";//throw new TemplateException(cfml, "Number can't end with [.]"); // DIFF 23
			rtn.append(rightSite);
		}
        comments(data.cfml);
        
		try {
			return new LitDouble(Caster.toDoubleValue(rtn.toString()),line);
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
	protected String digit(Data data) {
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
	protected Expression dynamic(Data data) throws TemplateException {
		// Die Implementation weicht ein wenig von der Grammatik ab, 
		// aber nicht in der Logik sondern rein wie es umgesetzt wurde.
		
	    
	    
		// get First Element of the Variable
		int line=data.cfml.getLine();
		String name = identifier(data,false,true);
		if(name == null) {
		    if (!data.cfml.forwardIfCurrent('(')) return null;
		    
            comments(data.cfml);
			Expression expr = assignOp(data);

			if (!data.cfml.forwardIfCurrent(')'))
				throw new TemplateException(
					data.cfml,
					"Invalid Syntax Closing [)] not found");
            comments(data.cfml);
            return expr;//subDynamic(expr);
            
		}
			
		Variable var;
        comments(data.cfml);
		
		// Boolean constant 
		if(name.equals("TRUE"))	{// || name.equals("YES"))	{
			comments(data.cfml);
			return new LitBoolean(true,line);
		}
		else if(name.equals("FALSE"))	{// || name.equals("NO"))	{
			comments(data.cfml);
			return new LitBoolean(false,line);
		}
		
		// Extract Scope from the Variable
		//int c=data.cfml.getColumn();
		int l=data.cfml.getLine();
		var = startElement(data,name,line);
		var.setLine(l);
		
		return var;
	}
	

	
	protected Expression json(Data data,FunctionLibFunction flf, char start, char end) throws TemplateException {
		if(!data.cfml.forwardIfCurrent(start))return null;
		
		int line = data.cfml.getLine();
		BIF bif=new BIF(flf.getName(),flf);
		bif.setArgType(flf.getArgType());
		bif.setClassName(flf.getCls());
		bif.setReturnType(flf.getReturnTypeAsString());
		
		do {
			comments(data.cfml);
			if (data.cfml.isCurrent(end))break;
			
			bif.addArgument(functionArgument(data));
			comments(data.cfml);
		} 
		while (data.cfml.forwardIfCurrent(','));
		comments(data.cfml);
			
		if (!data.cfml.forwardIfCurrent(end))
			throw new TemplateException(data.cfml,"Invalid Syntax Closing ["+end+"] not found");
		comments(data.cfml);
		Variable var=new Variable(line);
		var.addMember(bif);
		return var;
	}
	

	
	
	protected FunctionLibFunction getFLF(Data data,String name) {
		FunctionLibFunction flf=null;
		for (int i = 0; i < data.fld.length; i++) {
			flf = data.fld[i].getFunction(name);
			if (flf != null)
				break;
		}
		return flf;
	}

	private Expression subDynamic(Data data,Expression expr) throws TemplateException {
		
		
		

	    String name=null;
	    Invoker invoker=null;
		// Loop over nested Variables
		while (data.cfml.isValidIndex()) {
			Expression nameProp = null;
			// .
			if (data.cfml.forwardIfCurrent('.')) {
				// Extract next Var String
                comments(data.cfml);
                int line=data.cfml.getLine();
				name = identifier(data,true,true);
				if(name==null) 
					throw new TemplateException(data.cfml, "Invalid identifier");
                comments(data.cfml);
				nameProp=LitString.toExprString(name,line);
			}
			// []
			else if (data.cfml.forwardIfCurrent('[')) {
				
				// get Next Var
				nameProp = structElement(data);

				// Valid Syntax ???
				if (!data.cfml.forwardIfCurrent(']'))
					throw new TemplateException(
						data.cfml,
						"Invalid Syntax Closing []] not found");
			}
			/* / :
			else if (data.cfml.forwardIfCurrent(':')) {
				// Extract next Var String
                comments(data.cfml);
                int line=data.cfml.getLine();
				name = identifier(true,true);
				if(name==null) 
					throw new TemplateException(cfml, "Invalid identifier");
                comments(data.cfml);
                
				nameProp=LitString.toExprString(name,line);
			}*/
			// finish
			else {
				break;
			}

            comments(data.cfml);
            
            if(expr instanceof Invoker)  {
            	invoker=(Invoker) expr;
            }
            else {
            	invoker=new ExpressionInvoker(expr);
            	expr=invoker;
            }
			// Method
			if (data.cfml.isCurrent('(')) invoker.addMember(getFunctionMember(data,name, false, nameProp));
			
			// property
			else invoker.addMember(new DataMember(nameProp));
			
		}
		
		return expr;  
	}
	
	private Expression newOp(Data data,Expression expr) throws TemplateException {
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
	    name = identifier(data,true,false);
	    
		
		ExprString exprName;
		if(name!=null)	{
			StringBuilder fullName=new StringBuilder();
			fullName.append(name);
			// Loop over addional identifier
			while (data.cfml.isValidIndex()) {
				if (data.cfml.forwardIfCurrent('.')) {
					comments(data.cfml);
	                name = identifier(data,true,false);
					if(name==null) {
						data.cfml.setPos(start);
						return expr;//throw new TemplateException(data.cfml,"invalid Component declaration ");
					}
					fullName.append('.');
					fullName.append(name);
					comments(data.cfml);
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

        comments(data.cfml);
        
        if (data.cfml.isCurrent('(')) {
			FunctionMember func = getFunctionMember(data,"_createComponent", true, null);
			func.addArgument(new Argument(exprName,"string"));
			Variable v=new Variable(expr.getLine());
			v.addMember(func);
            comments(data.cfml);
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
	protected Variable startElement(Data data,String name, int line) throws TemplateException {
		
		
		
		String lc = name;
		
		// check function
		if (data.cfml.isCurrent('(')) {
			FunctionMember func = getFunctionMember(data,lc, true, null);
			
			Variable var=new Variable(line);
			var.addMember(func);
            comments(data.cfml);
			return var;
		} 
		
		//check scope
		Variable var = scope(data,lc,line);
		if(var!=null) return var;
		
		// undefined variable
		var=new Variable(line);
		var.addMember(new DataMember(LitString.toExprString(name, data.cfml.getLine())));

        comments(data.cfml);
		return var;
		
	}
	
	/**
	* Liest einen CFML Scope aus, 
	* falls der folgende identifier keinem Scope entspricht, 
	* gibt die Variable null zur￼ck.
	* <br />
	* EBNF:<br />
	* <code>"variable" | "cgi" | "url" | "form" | "session" | "application" | "arguments" | "cookie" | " client";</code>
	 * @param idStr String identifier, 
	 * wird aus Optimierungszwechen nicht innerhalb dieser Funktion ausgelsen.
	 * @return CFXD Variable Element oder null
	 * @throws TemplateException 
	*/
	protected Variable scope(Data data,String idStr, int line) throws TemplateException {
		if(data.ignoreScopes)return null;
		if (idStr.equals("CGI")) 				return new Variable(Scope.SCOPE_CGI,line);
		else if (idStr.equals("ARGUMENTS"))  	return new Variable(Scope.SCOPE_ARGUMENTS,line);
		else if (idStr.equals("REQUEST"))		return new Variable(Scope.SCOPE_REQUEST,line);
		else if (idStr.equals("SESSION"))		return new Variable(Scope.SCOPE_SESSION,line);
		else if (idStr.equals("APPLICATION"))	return new Variable(Scope.SCOPE_APPLICATION,line);
		else if (idStr.equals("VARIABLES"))		return new Variable(Scope.SCOPE_VARIABLES,line);
		else if (idStr.equals("FORM")) 			return new Variable(Scope.SCOPE_FORM,line);
		else if (idStr.equals("URL"))			return new Variable(Scope.SCOPE_URL,line);
		else if (idStr.equals("SERVER")) 		return new Variable(Scope.SCOPE_SERVER,line);
		else if (idStr.equals("CLIENT"))		return new Variable(Scope.SCOPE_CLIENT,line);
		else if (idStr.equals("COOKIE"))		return new Variable(Scope.SCOPE_COOKIE,line);
		else if (idStr.equals("CLUSTER"))		return new Variable(Scope.SCOPE_CLUSTER,line);
		else if (idStr.equals("LOCAL"))			return new Variable(Scope.SCOPE_LOCAL,line);
		else if (idStr.equals("VAR")) {
			String name=identifier(data,false,true);
			if(name!=null){
				comments(data.cfml);
				Variable local = new Variable(ScopeSupport.SCOPE_VAR,line);
				if(!"LOCAL".equals(name))local.addMember(new DataMember(LitString.toExprString(name, data.cfml.getLine())));
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
	 * @param lowerCase 
	* @return Identifier.
	*/
	protected String identifier(Data data,boolean firstCanBeNumber,boolean upper) {
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
		if(upper)
			return data.cfml.substring(start,data.cfml.getPos()-start).toUpperCase();
		
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
	protected Expression structElement(Data data) throws TemplateException {
        comments(data.cfml);
		Expression name = CastString.toExprString(assignOp(data));
		if(name instanceof LitString)((LitString)name).fromBracket(true);
        comments(data.cfml);
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
	* @param nameProp Identifier als CFXD Element, wenn dieses null ist wird es ignoriert.
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected FunctionMember getFunctionMember(Data data,
		String name,
		boolean checkLibrary,
		Expression exprName)
		throws TemplateException {

		// get Function Library
		checkLibrary=checkLibrary && data.fld!=null;
		FunctionLibFunction flf = null;
		if (checkLibrary) {
			for (int i = 0; i < data.fld.length; i++) {
				flf = data.fld[i].getFunction(name);
				if (flf != null)
					break;
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
			bif.setClassName(flf.getCls());
			bif.setReturnType(flf.getReturnTypeAsString());
			fm=bif;
			
			if(flf.getArgType()== FunctionLibFunction.ARG_DYNAMIC && flf.hasDefaultValues()){
        		ArrayList args = flf.getArg();
				Iterator it = args.iterator();
        		FunctionLibFunctionArg arg;
        		while(it.hasNext()){
        			arg=(FunctionLibFunctionArg) it.next();
        			if(arg.getDefaultValue()!=null)
        				bif.addArgument(
        						new NamedArgument(
        								LitString.toExprString(arg.getName()),
        								LitString.toExprString(arg.getDefaultValue()),
        								arg.getTypeAsString()
        								));
        		}
			}
		}
		else {
			if(exprName==null)fm = new UDF(name);
			else fm = new UDF(exprName);
		}
		
		
		

		// Function Attributes
		ArrayList arrFuncLibAtt = null;
		int libLen = 0;
		if (checkLibrary) {
			arrFuncLibAtt = flf.getArg();
			libLen = arrFuncLibAtt.size();
		}
		int count = 0;
		do {
			data.cfml.next();
            comments(data.cfml);

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
							"too many Attributes in function [" + flf.getName() + "]");
				}
			// Fix
				else {
					if(libLen <= count){
						
						TemplateException te = new TemplateException(
							data.cfml,
							"too many Attributes in function call [" + flf.getName() + "]");
						te.setAdditional("pattern", createFunctionPattern(flf));
						throw te;
					}
				}
				
			}
			
			//Argument arg;
			if (checkLibrary && !isDynamic) {
				// current attribues from library
				FunctionLibFunctionArg funcLibAtt =(FunctionLibFunctionArg) arrFuncLibAtt.get(count);
				fm.addArgument(functionArgument(data,funcLibAtt.getTypeAsString()));	
			} 
			else {
				fm.addArgument(functionArgument(data));
			}

            comments(data.cfml);
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
					+ flf.getName()
					+ "] not found");

		// check min attributes
		if (checkLibrary && flf.getArgMin() > count){
			TemplateException te = new TemplateException(
				data.cfml,
				"too few attributes in function [" + flf.getName() + "]");
			if(flf.getArgType()==FunctionLibFunction.ARG_FIX) te.setAdditional("pattern", createFunctionPattern(flf));
			throw te;
		}

        comments(data.cfml);
        
        // evaluator
        if(checkLibrary && flf.hasTteClass()){
        	flf.getEvaluator().evaluate((BIF) fm, flf);
        }
        
		return fm;
	}
	
	public static String createFunctionPattern(FunctionLibFunction flf) {
		ArrayList<FunctionLibFunctionArg> args=flf.getArg();
		Iterator<FunctionLibFunctionArg> it = args.iterator();
		
		// regular call
		StringBuilder pattern=new StringBuilder(flf.getName());
		StringBuilder end=new StringBuilder();
		pattern.append("(");
		FunctionLibFunctionArg arg;
		int c=0;
		while(it.hasNext()){
			arg = it.next();
			if(!arg.isRequired()) {
				pattern.append(" [");
				end.append("]");
			}
			if(c++>0)pattern.append(", ");
			pattern.append(arg.getName());
			pattern.append(":");
			pattern.append(arg.getType());
			
		}
		pattern.append(end);
		pattern.append("):");
		pattern.append(flf.getReturnTypeAsString());
		
		
		
		
		
		return pattern.toString();
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
	protected Expression sharp(Data data) throws TemplateException {
		if(!data.cfml.forwardIfCurrent('#'))
			return null;
		Expression expr;
        comments(data.cfml);
        boolean old=data.allowLowerThan;
        data.allowLowerThan=true;
		expr = assignOp(data);
		data.allowLowerThan=old;
        comments(data.cfml);
		if (!data.cfml.forwardIfCurrent('#'))
			throw new TemplateException(
				data.cfml,
				"Syntax Error, Invalid Construct "+(data.cfml.length()<30?data.cfml.toString():""));
        comments(data.cfml);
		return expr;
	}
	
	/**
	 * @param data 
	 * @return parsed Element
	 * @throws TemplateException
	 */
	private Expression simple(Data data,String[] breakContitions) throws TemplateException {
		StringBuffer sb=new StringBuffer();
		int line=data.cfml.getLine();
		outer:while(data.cfml.isValidIndex()) {
			for(int i=0;i<breakContitions.length;i++){
				if(data.cfml.isCurrent(breakContitions[i]))break outer;
			}
			
			//if(data.cfml.isCurrent(' ') || data.cfml.isCurrent('>') || data.cfml.isCurrent("/>")) break;
			
			if(data.cfml.isCurrent('"') || data.cfml.isCurrent('#') || data.cfml.isCurrent('\'')) {
				throw new TemplateException(data.cfml,"simple attribute value can't contain ["+data.cfml.getCurrent()+"]");
			}
			else sb.append(data.cfml.getCurrent());
			data.cfml.next();
		}
        comments(data.cfml);
		
		return LitString.toExprString(sb.toString(),line);
	}
    

    /**
     *  Liest alle folgenden Komentare ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @param data 
     * @throws TemplateException
     */
    protected void comments(CFMLString cfml) throws TemplateException {
        cfml.removeSpace();
        while(comment(cfml)){cfml.removeSpace();}
    }
    
    /**
     *  Liest einen Einzeiligen Kommentar ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @return bool Wurde ein Kommentar entfernt?
     * @throws TemplateException
     */
    protected boolean comment(CFMLString cfml) throws TemplateException {
        if(singleLineComment(cfml) || multiLineComment(cfml) || CFMLTransformer.comment(cfml)) return true;
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
    private boolean multiLineComment(CFMLString cfml) throws TemplateException {
        if(!cfml.forwardIfCurrent("/*")) return false;
        int pos=cfml.getPos();
        while(cfml.isValidIndex()) {
            if(cfml.isCurrent("*/")) break;
            cfml.next();
        }
        if(!cfml.forwardIfCurrent("*/")){
            cfml.setPos(pos);
            throw new TemplateException(cfml,"comment is not closed");
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

	/* *
	 * @return the ignoreScopes
	 * /
	public boolean isIgnoreScopes() {
		return ignoreScopes;
	}*/

	/* *
	 * @param ignoreScopes the ignoreScopes to set
	 * /
	public void setIgnoreScopes(boolean ignoreScopes) {
		this.ignoreScopes = ignoreScopes;
	}*/
    
}