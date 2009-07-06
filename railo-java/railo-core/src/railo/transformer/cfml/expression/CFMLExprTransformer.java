package railo.transformer.cfml.expression;


import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.exp.CasterException;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.runtime.type.Scope;
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
	//public static final boolean CASE_TYPE_UPPER = true;
	//public static final int CASE_TYPE_LOWER = 1;
	//public static final int CASE_TYPE_ORIGINAL = 2;
	private static FunctionLibFunction JSON_ARRAY = null;
	private static FunctionLibFunction JSON_STRUCT = null;
	private short mode=0;
	
	/**
	 * Field <code>cfml</code>
	 */
	protected CFMLString cfml;
	
	/**
	 * Field <code>fld</code>
	 */
	protected FunctionLib[] fld;
	private boolean ignoreScopes=false;
	private boolean allowLowerThan;
	
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
	public Expression transform(FunctionLib[] fld, CFMLString cfml)
		throws TemplateException {
		
		// Init Parameter
		init(fld, cfml,false);

		// parse the houle Page String
        comments();
		//Expression expr = assignOp();

		// return the Root Element of the Document	
		return assignOp();
	}
	
	/**
	 * @see railo.transformer.cfml.ExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transformAsString(FunctionLib[] fld, CFMLString cfml, boolean allowLowerThan) throws TemplateException {
		Expression el=null;
		
		// Init Parameter
		init(fld, cfml,allowLowerThan);

		// parse the houle Page String
        comments();		
				
		// String
			if((el=string())!=null) {
				mode=STATIC;
				return el;
			} 
		// Sharp
			if((el=sharp())!=null) {
				mode=DYNAMIC;
				return el;
			}  
		// Simple
			return simple();
	}

	/**
	 * Initialmethode, wird aufgerufen um den internen Zustand des Objektes zu setzten.
	 * @param fld Function Libraries zum validieren der Funktionen
	 * @param doc XML Document des aktuellen CFXD
	 * @param cfml CFML Code der transfomiert werden soll.
	 */
	protected void init(FunctionLib[] fld, CFMLString cfml, boolean allowLowerThan) {
		this.fld = fld;
		this.cfml = cfml;
		if(JSON_ARRAY==null)JSON_ARRAY=getFLF("_jsonArray");
		if(JSON_STRUCT==null)JSON_STRUCT=getFLF("_jsonStruct");
		this.allowLowerThan=allowLowerThan;
	}
	
	/**
	 * Startpunkt zum transfomieren einer Expression, ohne dass das Objekt neu initialisiert wird, 
	 * dient vererbten Objekten als Einstiegspunkt.
	 * @return Element
	 * @throws TemplateException
	 */
	protected Expression expression() throws TemplateException {
		return assignOp();
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
	protected Argument functionArgument() throws TemplateException {
		return functionArgument(null);
	}
	
	protected Argument functionArgument(String type) throws TemplateException {
		Expression expr = assignOp();
		try{
			if (cfml.forwardIfCurrent(":")) {
				comments();
	            return new NamedArgument(expr,assignOp(),type);
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
			throw new TemplateException(cfml,be.getMessage());
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
	protected Expression assignOp() throws TemplateException {
        
		Expression expr = conditionalOp();
        if (cfml.forwardIfCurrent('=')) {
        	
            comments();
            if(mode==STATIC) expr=new DynAssign(expr,assignOp());
			else expr=new Assign((Variable)expr,assignOp());
		}
		return expr;
	}
	
	protected Expression conditionalOp() throws TemplateException {
        
		Expression expr = impOp();
        if (cfml.forwardIfCurrent('?')) {
        	comments();
        	Expression left = assignOp();
        	comments();
        	if(!cfml.forwardIfCurrent(':'))throw new TemplateException("invalid conditional operator");
        	comments(); 
        	Expression right = assignOp();
        	
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
	protected Expression impOp() throws TemplateException {
		Expression expr = eqvOp();
		while(cfml.forwardIfCurrentAndNoWordAfter("imp")) {
			comments();
            expr=OpBool.toExprBoolean(expr, eqvOp(), OpBool.IMP);
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
	protected Expression eqvOp() throws TemplateException {
		Expression expr = xorOp();
		while(cfml.forwardIfCurrent("eqv")) {
			comments();
            expr=OpBool.toExprBoolean(expr, xorOp(), OpBool.EQV);
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
	protected Expression xorOp() throws TemplateException {
		Expression expr = orOp();
		while(cfml.forwardIfCurrent("xor")) {
			comments();
            expr=OpBool.toExprBoolean(expr, orOp(), OpBool.XOR);
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
	protected Expression orOp() throws TemplateException {
		Expression expr = andOp();
		
		while(cfml.forwardIfCurrent("||") || cfml.forwardIfCurrent("or")) {
			comments();
            expr=OpBool.toExprBoolean(expr, andOp(), OpBool.OR);
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
	protected Expression andOp() throws TemplateException {
		Expression expr = notOp();
		
		while(cfml.forwardIfCurrent("&&") || cfml.forwardIfCurrent("and")) {
			comments();
	        expr=OpBool.toExprBoolean(expr, notOp(), OpBool.AND);
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
	protected Expression notOp() throws TemplateException {
		// And Operation
		int line=cfml.getLine();
		if (cfml.isCurrent('!') && !cfml.isCurrent("!=")) {
			cfml.next();
			comments();
			return OpNegate.toExprBoolean(decsionOp(),line);
		}
		else if (cfml.forwardIfCurrentAndNoWordAfter("not")) {
			comments();
			return OpNegate.toExprBoolean(decsionOp(),line);
		}
		return decsionOp();
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
	protected Expression decsionOp() throws TemplateException {

		Expression expr = concatOp();
		boolean hasChanged=false;
		// ct, contains
		do {
			hasChanged=false;
			if(cfml.isCurrent('c')) {
					if (cfml.forwardIfCurrent("ct")) {expr = decisionOpCreate(OPDecision.CT,expr);hasChanged=true;} 
					else if (cfml.forwardIfCurrent("contains")){ expr = decisionOpCreate(OPDecision.CT,expr);hasChanged=true;}
			}
			// does not contain
			else if (cfml.forwardIfCurrent("does","not","contain")){ expr = decisionOpCreate(OPDecision.NCT,expr); hasChanged=true;}

			// equal, eq
			else if (cfml.isCurrent("eq") && !cfml.isCurrent("eqv")) {
				cfml.setPos(cfml.getPos()+2);
				cfml.forwardIfCurrent("ual");
				expr = decisionOpCreate(OPDecision.EQ,expr);
				hasChanged=true;
			}
			// ==
			else if (cfml.forwardIfCurrent("==")) {
				if(cfml.forwardIfCurrent('=')) 		expr = decisionOpCreate(OPDecision.EEQ,expr);
				else expr = decisionOpCreate(OPDecision.EQ,expr);
				hasChanged=true;
			}
			// !=
			else if (cfml.forwardIfCurrent("!=")) {
				if(cfml.forwardIfCurrent('=')) 		expr = decisionOpCreate(OPDecision.NEEQ,expr);
				else expr = decisionOpCreate(OPDecision.NEQ,expr); 
				hasChanged=true;
			}
			// <=/</<>
			else if (cfml.isCurrent('<')) {
				hasChanged=true;
				if(cfml.isNext('='))	{
					cfml.next();cfml.next();
					expr = decisionOpCreate(OPDecision.LTE,expr);
				}
				else if(cfml.isNext('>')) {
					cfml.next();cfml.next();
					expr = decisionOpCreate(OPDecision.NEQ,expr);
				}
				else if(cfml.isNext('/')) {
					hasChanged=false;
				}
				else	{
					cfml.next();
					expr = decisionOpCreate(OPDecision.LT,expr); 
				}
			}
			// >=/>
			else if (allowLowerThan && cfml.forwardIfCurrent('>')) {
				if(cfml.forwardIfCurrent('=')) 	expr = decisionOpCreate(OPDecision.GTE,expr);
				else 							expr = decisionOpCreate(OPDecision.GT,expr); 
				hasChanged=true;
			}
			
			// gt, gte, greater than or equal to, greater than
			else if (cfml.isCurrent('g')) {
				if (cfml.forwardIfCurrent("gt")) {
					if(cfml.forwardIfCurrent("e")) expr = decisionOpCreate(OPDecision.GTE,expr);
					else expr = decisionOpCreate(OPDecision.GT,expr);
					hasChanged=true;
				} 
				else if (cfml.forwardIfCurrent("greater", "than")) {
					if(cfml.forwardIfCurrent(" or equal to")) expr = decisionOpCreate(OPDecision.GTE,expr);
					else expr = decisionOpCreate(OPDecision.GT,expr);
					hasChanged=true;
				}	
				else if (cfml.forwardIfCurrent("ge")) {
					expr = decisionOpCreate(OPDecision.GTE,expr);
					hasChanged=true;
				}				
			}
			
			// is, is not
			else if (cfml.forwardIfCurrent("is")) {
				if(cfml.forwardIfCurrent(" not")) expr = decisionOpCreate(OPDecision.NEQ,expr);
				else expr = decisionOpCreate(OPDecision.EQ,expr);
				hasChanged=true;
			}
			
			// lt, lte, less than, less than or equal to
			else if (cfml.isCurrent('l')) {
				if (cfml.forwardIfCurrent("lt")) {
					if(cfml.forwardIfCurrent("e")) expr = decisionOpCreate(OPDecision.LTE,expr);
					else expr = decisionOpCreate(OPDecision.LT,expr);
					hasChanged=true;
				} 
				else if (cfml.forwardIfCurrent("less than")) {
					if(cfml.forwardIfCurrent(" or equal to")) expr = decisionOpCreate(OPDecision.LTE,expr);
					else expr = decisionOpCreate(OPDecision.LT,expr);
					hasChanged=true;
				}	
				else if (cfml.forwardIfCurrent("le")) {
					expr = decisionOpCreate(OPDecision.LTE,expr);
					hasChanged=true;
				}				
			}
			
			// neq, not equal, nct
			else if (cfml.isCurrent('n')) {
				// Not Equal
					if (cfml.forwardIfCurrent("neq")){ expr = decisionOpCreate(OPDecision.NEQ,expr); hasChanged=true;}
				// Not Equal (Alias)
					else if (cfml.forwardIfCurrent("not equal")){ expr = decisionOpCreate(OPDecision.NEQ,expr);hasChanged=true; }
				// nct
					else if (cfml.forwardIfCurrent("nct")){ expr = decisionOpCreate(OPDecision.NCT,expr); hasChanged=true;}	
			}
			
		}
		while(hasChanged);
		return expr;
	}
	private Expression decisionOpCreate(int operation, Expression left) throws TemplateException {
        comments();
        return OPDecision.toExprBoolean(left, concatOp(), operation);
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
	protected Expression concatOp() throws TemplateException {
		Expression expr = plusMinusOp();
		
		while(cfml.isCurrent('&') && !cfml.isCurrent("&&")) {
			cfml.next();
			
			// &=
			if (cfml.isCurrent('=') && expr instanceof Variable) {
				cfml.next();
				comments();
				Expression right = assignOp();
				ExprString res = OpString.toExprString(expr, right);
				expr=new OpVariable((Variable)expr,res);
			}
			else {
	            comments();
	            expr=OpString.toExprString(expr, plusMinusOp());
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
	protected Expression plusMinusOp() throws TemplateException {
		Expression expr = modOp();
		
		while(!cfml.isLast()) {
			
			// Plus Operation
			if (cfml.forwardIfCurrent('+'))			expr=_plusMinusOp(expr,OpDouble.PLUS);
			// Minus Operation
			else if (cfml.forwardIfCurrent('-'))	expr=_plusMinusOp(expr,OpDouble.MINUS);
			else break;
		}
		return expr;
	}
	
	

	private Expression _plusMinusOp(Expression expr,int opr) throws TemplateException {
		// +=
		if (cfml.isCurrent('=') && expr instanceof Variable) {
			cfml.next();
			comments();
			Expression right = assignOp();
			ExprDouble res = OpDouble.toExprDouble(expr, right,opr);
			expr=new OpVariable((Variable)expr,res);
		}
		/*/ ++
		else if (cfml.isCurrent(opr==OpDouble.PLUS?'+':'-') && expr instanceof Variable) {
			cfml.next();
			comments();
			ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),opr);
			expr=new OpVariable((Variable)expr,res);
			expr=OpDouble.toExprDouble(expr,LitDouble.toExprDouble(1D, -1),opr==OpDouble.PLUS? OpDouble.MINUS:OpDouble.PLUS);
			//comments();
		}*/
		else {
			comments();
            expr=OpDouble.toExprDouble(expr, modOp(), opr);	
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
	protected Expression modOp() throws TemplateException {
		Expression expr = divMultiOp();
		
		// Modulus Operation
		while(cfml.forwardIfCurrent('%') || cfml.forwardIfCurrent("mod")) {
			expr=_modOp(expr);
			//comments();
            //expr=OpDouble.toExprDouble(expr, divMultiOp(), OpDouble.MODULUS);
		}
		return expr;
	}
	
	private Expression _modOp(Expression expr) throws TemplateException {
		if (cfml.isCurrent('=') && expr instanceof Variable) {
			cfml.next();
			comments();
			Expression right = assignOp();
			ExprDouble res = OpDouble.toExprDouble(expr, right,OpDouble.MODULUS);
			return new OpVariable((Variable)expr,res);
		}
        comments();
        return OpDouble.toExprDouble(expr, expoOp(), OpDouble.MODULUS);
	}

	/**
	* Transfomiert die mathematischen Operatoren Mal und Durch (*,/).
	* <br />
	* EBNF:<br />
	* <code>expoOp {("*"|"/") spaces expoOp};</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression divMultiOp() throws TemplateException {
		Expression expr = expoOp();

		while (!cfml.isLast()) {
			
				// Multiply Operation
				if(cfml.forwardIfCurrent('*')) {
					expr=_divMultiOp(expr,OpDouble.MULTIPLY);
					//comments();
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.MULTIPLY);
				}
				// Divide Operation
				else if (cfml.isCurrent('/') && (!cfml.isCurrent('/','>') )) {
					cfml.next(); 
					expr=_divMultiOp(expr,OpDouble.DIVIDE);
					//comments();
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.DIVIDE);
				}
				// Divide Operation
				else if (cfml.isCurrent('\\')) {
					cfml.next(); 
					expr=_divMultiOp(expr,OpDouble.INTDIV);
					//comments();
                    //expr=OpDouble.toExprDouble(expr, expoOp(), OpDouble.INTDIV);
				}
				else {
					break;
				}
			
		}
		return expr;
	}

	private Expression _divMultiOp(Expression expr, int iOp) throws TemplateException {
		if (cfml.isCurrent('=') && expr instanceof Variable) {
			cfml.next();
			comments();
			Expression right = assignOp();
			ExprDouble res = OpDouble.toExprDouble(expr, right,iOp);
			return new OpVariable((Variable)expr,res);
		}
        comments();
        return OpDouble.toExprDouble(expr, expoOp(), iOp);
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
	protected Expression expoOp() throws TemplateException {
		Expression expr = unaryOp();

		// Modulus Operation
		while(cfml.forwardIfCurrent('^') || cfml.forwardIfCurrent("exp")) {
			comments();
            expr=OpDouble.toExprDouble(expr, unaryOp(), OpDouble.EXP);
		}
		return expr;
	}
	
	protected Expression unaryOp() throws TemplateException {
		Expression expr = negatePlusMinusOp();
		
		// Plus Operation
		if (cfml.forwardIfCurrent("++") && expr instanceof Variable)			
			expr=_unaryOp(expr,OpDouble.PLUS);
		// Minus Operation
		else if (cfml.forwardIfCurrent("--") && expr instanceof Variable)	
			expr=_unaryOp(expr,OpDouble.MINUS);
		return expr;
	}
	
	private Expression _unaryOp(Expression expr,int opr) throws TemplateException {
		comments();
		ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),opr);
		expr=new OpVariable((Variable)expr,res);
		return OpDouble.toExprDouble(expr,LitDouble.toExprDouble(1D, -1),opr==OpDouble.PLUS? OpDouble.MINUS:OpDouble.PLUS);
	}
	
	
	

	/**
	* Negate Numbers
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression negatePlusMinusOp() throws TemplateException {
		// And Operation
		int line=cfml.getLine();
		if (cfml.forwardIfCurrent('-')) {
			if (cfml.forwardIfCurrent('-')) {
				comments();
				Expression expr = clip();
				ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),OpDouble.MINUS);
				return new OpVariable((Variable)expr,res);
			}
			comments();
			return OpNegateNumber.toExprDouble(clip(),OpNegateNumber.MINUS,line);
			
		}
		else if (cfml.forwardIfCurrent('+')) {
			if (cfml.forwardIfCurrent('+')) {
				comments();
				Expression expr = clip();
				ExprDouble res = OpDouble.toExprDouble(expr, LitDouble.toExprDouble(1D,-1),OpDouble.PLUS);
				return new OpVariable((Variable)expr,res);
			}
			comments();
			return CastDouble.toExprDouble(clip());//OpNegateNumber.toExprDouble(clip(),OpNegateNumber.PLUS,line);
		}
		return clip();
	}
	
	
	
	
	
	
	

	/**
	* Verarbeitet Ausdr￼cke die inerhalb einer Klammer stehen.
	* <br />
	* EBNF:<br />
	* <code>("(" spaces impOp ")" spaces) | checker;</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression clip() throws TemplateException {
	    return checker();
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
	protected Expression checker() throws TemplateException {
		Expression expr=null;
		// String
			if((expr=string())!=null) {
				expr = subDynamic(expr);
				mode=STATIC;//(expr instanceof Literal)?STATIC:DYNAMIC;// STATIC
				return expr;
			} 
		// Number
			if((expr=number())!=null) {
				expr = subDynamic(expr);
				mode=STATIC;//(expr instanceof Literal)?STATIC:DYNAMIC;// STATIC
				return expr;
			} 
		// Dynamic
			if((expr=dynamic())!=null) {
				expr = subDynamic(expr);
				mode=DYNAMIC;
				return expr;
			} 
		// Sharp
			if((expr=sharp())!=null) {
				mode=DYNAMIC;
				return expr;
			} 
		// JSON
			if((expr=json(JSON_ARRAY,'[',']'))!=null) {
				mode=DYNAMIC;
				return expr;
			} 
			if((expr=json(JSON_STRUCT,'{','}'))!=null) {
				mode=DYNAMIC;
				return expr;
			} 
		// else Error
			throw new TemplateException(cfml,"Syntax Error, Invalid Construct");	
	}
	
	/**
	* Transfomiert einen lierale Zeichenkette.
	* <br />
	* EBNF:<br />
	* <code>("'" {"##"|"''"|"#" impOp "#"| ?-"#"-"'" } "'") | 
	                 (""" {"##"|""""|"#" impOp "#"| ?-"#"-""" } """);</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression string() throws TemplateException {
		
		// check starting character for a string literal
		if(!cfml.isCurrent('"')&& !cfml.isCurrent('\''))
			return null;
		int line=cfml.getLine();
		
		// Init Parameter
		char quoter = cfml.getCurrentLower();
		StringBuffer str=new StringBuffer();
		Expression expr=null;
		
		while(cfml.hasNext()) {
			cfml.next();
			// check sharp
			if(cfml.isCurrent('#')) {
				
				// Ecaped sharp
				if(cfml.isNext('#')){
					cfml.next();
					str.append('#');
				}
				// get Content of sharp
				else {
					cfml.next();
                    comments();
					Expression inner=assignOp();
                    comments();
					if (!cfml.isCurrent('#'))
						throw new TemplateException(cfml,"Invalid Syntax Closing [#] not found");
					
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
			else if(cfml.isCurrent(quoter)) {
				// Ecaped sharp
				if(cfml.isNext(quoter)){
					cfml.next();
					str.append(quoter);
				}
				// finsish
				else {
					break;
				}				
			}
			// all other character
			else {
				str.append(cfml.getCurrent());
			}
		}
		if(!cfml.forwardIfCurrent(quoter))
			throw new TemplateException(cfml,"Invalid Syntax Closing ["+quoter+"] not found");
		
		if(expr==null)
			expr=new LitString(str.toString(),line);
		else if(str.length()!=0) {
			expr = OpString.toExprString(expr, new LitString(str.toString(),line));
		}
        comments();
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
	protected LitDouble number() throws TemplateException {
		// check first character is a number literal representation
		if(!(cfml.isCurrentBetween('0','9') || cfml.isCurrent('.'))) return null;
		
		int line=cfml.getLine();
		StringBuffer rtn=new StringBuffer();
		
		// get digit on the left site of the dot
		if(cfml.isCurrent('.')) rtn.append('0');
		else rtn.append(digit());
		// read dot if exist
		if(cfml.forwardIfCurrent('.')) {
			rtn.append('.');
			String rightSite=digit();
			if(rightSite.length()> 0 && cfml.forwardIfCurrent('e')) {
			    if(cfml.isCurrentBetween('0','9')) {
			        rightSite+='e'+digit();
			    }
			    else {
			        cfml.previous();
			    }
			}
			// read right side of the dot
			if(rightSite.length()==0)
				rightSite="0";//throw new TemplateException(cfml, "Number can't end with [.]"); // DIFF 23
			rtn.append(rightSite);
		}
        comments();
        
		try {
			return new LitDouble(Caster.toDoubleValue(rtn.toString()),line);
		} catch (CasterException e) {
			throw new TemplateException(cfml,e.getMessage());
		}
		
	}
	
	
	
	/**
	* Liest die reinen Zahlen innerhalb des CFMLString aus und gibt diese als Zeichenkette zur￼ck. 
	* <br />
	* EBNF:<br />
	* <code>"0"|..|"9";</code>
	* @return digit Ausgelesene Zahlen als Zeichenkette.
	*/
	protected String digit() {
		String rtn="";
		while (cfml.isValidIndex()) {
			if(!cfml.isCurrentBetween('0','9'))break;
			rtn+=cfml.getCurrentLower();
			cfml.next();
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
	* <code>"true" | "false" | "yes" | "no" | startElement  
	                 {("." identifier | "[" structElement "]" )[function] };</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression dynamic() throws TemplateException {
		// Die Implementation weicht ein wenig von der Grammatik ab, 
		// aber nicht in der Logik sondern rein wie es umgesetzt wurde.
		
	    
	    
		// get First Element of the Variable
		int line=cfml.getLine();
		String name = identifier(false,true);
		if(name == null) {
		    if (!cfml.forwardIfCurrent('(')) return null;
		    
            comments();
			Expression expr = assignOp();

			if (!cfml.forwardIfCurrent(')'))
				throw new TemplateException(
					cfml,
					"Invalid Syntax Closing [)] not found");
            comments();
            return expr;//subDynamic(expr);
            
		}
			
		Variable var;
        comments();
		
		// Boolean constant 
		if(name.equals("TRUE"))	{// || name.equals("YES"))	{
			comments();
			return new LitBoolean(true,line);
		}
		else if(name.equals("FALSE"))	{// || name.equals("NO"))	{
			comments();
			return new LitBoolean(false,line);
		}
		
		// Extract Scope from the Variable
		//int c=cfml.getColumn();
		int l=cfml.getLine();
		var = startElement(name,line);
		var.setLine(l);
		
		return var;
	}
	

	
	protected Expression json(FunctionLibFunction flf, char start, char end) throws TemplateException {
		if(!cfml.forwardIfCurrent(start))return null;
		
		int line = cfml.getLine();
		BIF bif=new BIF(flf.getName());
		bif.setArgType(flf.getArgType());
		bif.setClassName(flf.getCls());
		bif.setReturnType(flf.getReturnTypeAsString());
		
		do {
			comments();
			if (cfml.isCurrent(end))break;
			
			bif.addArgument(functionArgument());
			comments();
		} 
		while (cfml.forwardIfCurrent(','));
		comments();
			
		if (!cfml.forwardIfCurrent(end))
			throw new TemplateException(cfml,"Invalid Syntax Closing ["+end+"] not found");
		comments();
		Variable var=new Variable(line);
		var.addMember(bif);
		//print.out("current:"+cfml.getCurrent());
		return var;
	}
	

	
	
	protected FunctionLibFunction getFLF(String name) {
		FunctionLibFunction flf=null;
		for (int i = 0; i < fld.length; i++) {
			flf = fld[i].getFunction(name);
			if (flf != null)
				break;
		}
		return flf;
	}

	private Expression subDynamic(Expression expr) throws TemplateException {
		
		
		

	    String name=null;
	    Invoker invoker=null;
		// Loop over nested Variables
		while (cfml.isValidIndex()) {
			Expression nameProp = null;
			// .
			if (cfml.forwardIfCurrent('.')) {
				// Extract next Var String
                comments();
                int line=cfml.getLine();
				name = identifier(true,true);
				if(name==null) 
					throw new TemplateException(cfml, "Invalid identifier");
                comments();
				nameProp=LitString.toExprString(name,line);
			}
			// []
			else if (cfml.forwardIfCurrent('[')) {
				
				// get Next Var
				nameProp = structElement();

				// Valid Syntax ???
				if (!cfml.forwardIfCurrent(']'))
					throw new TemplateException(
						cfml,
						"Invalid Syntax Closing []] not found");
			}
			/* / :
			else if (cfml.forwardIfCurrent(':')) {
				// Extract next Var String
                comments();
                int line=cfml.getLine();
				name = identifier(true,true);
				if(name==null) 
					throw new TemplateException(cfml, "Invalid identifier");
                comments();
                
				nameProp=LitString.toExprString(name,line);
			}*/
			// finish
			else {
				break;
			}

            comments();
            
            if(expr instanceof Invoker)  {
            	invoker=(Invoker) expr;
            }
            else {
            	invoker=new ExpressionInvoker(expr);
            	expr=invoker;
            }
			// Method
			if (cfml.isCurrent('(')) invoker.addMember(getFunctionMember(name, false, nameProp));
			
			// property
			else invoker.addMember(new DataMember(nameProp));
			
		}
		
		return expr;  
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
	protected Variable startElement(String name, int line) throws TemplateException {
		
		
		
		String lc = name;
		
		// check function
		if (cfml.isCurrent('(')) {
			FunctionMember func = getFunctionMember(lc, true, null);
			
			Variable var=new Variable(line);
			var.addMember(func);
            comments();
			return var;
		} 
		
		//check scope
		Variable var = scope(lc,line);
		if(var!=null) return var;
		
		// undefined variable
		var=new Variable(line);
		var.addMember(new DataMember(LitString.toExprString(name, cfml.getLine())));

        comments();
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
	protected Variable scope(String idStr, int line) throws TemplateException {
		if(ignoreScopes)return null;
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
		else if (idStr.equals("VAR")) {
			String name=identifier(false,true);
			if(name!=null){
				comments();
				Variable local = new Variable(Scope.SCOPE_LOCAL,line);
				local.addMember(new DataMember(LitString.toExprString(name, cfml.getLine())));
				return local;
			}
		} 
		return null;
	}

    /* *
    * Liest einen Identifier aus und gibt diesen als String zur￼ck.
    * <br />
    * EBNF:<br />
    * <code>(letter | "_") {letter | "_"|digit};</code>
     * @param firstCanBeNumber 
    * @return Identifier.
    * @deprecated this method is replaced by <code>identifier(boolean firstCanBeNumber, boolean lowerCase)</code>
    * /
    protected String identifier(boolean firstCanBeNumber) {
        return identifier(firstCanBeNumber,true);
    }*/
    
	/**
	* Liest einen Identifier aus und gibt diesen als String zur￼ck.
	* <br />
	* EBNF:<br />
	* <code>(letter | "_") {letter | "_"|digit};</code>
	 * @param firstCanBeNumber 
	 * @param lowerCase 
	* @return Identifier.
	*/
	protected String identifier(boolean firstCanBeNumber,boolean upper) {
		int start = cfml.getPos();
		if(!cfml.isCurrentLetter() && !cfml.isCurrentSpecial() ) {
		    if(!firstCanBeNumber) return null;
            else if(!cfml.isCurrentBetween('0','9'))return null;
        }
		do {
			cfml.next();
			if(!(cfml.isCurrentLetter()
				|| cfml.isCurrentBetween('0','9')
				|| cfml.isCurrentSpecial())) {
					break;
				}
		}
		while (cfml.isValidIndex());
		if(upper)
			return cfml.substring(start,cfml.getPos()-start).toUpperCase();
		
		return cfml.substring(start,cfml.getPos()-start);
		
	}

	/**
	* Transfomiert ein Collection Element das in eckigen Klammern aufgerufen wird. 
	* <br />
	* EBNF:<br />
	* <code>"[" impOp "]"</code>
	* @return CFXD Element
	* @throws TemplateException 
	*/
	protected Expression structElement() throws TemplateException {
        comments();
		Expression name = CastString.toExprString(assignOp());
        comments();
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
	protected FunctionMember getFunctionMember(
		String name,
		boolean checkLibrary,
		Expression exprName)
		throws TemplateException {

		// get Function Library
		checkLibrary=checkLibrary && fld!=null;
		FunctionLibFunction flf = null;
		if (checkLibrary) {
			for (int i = 0; i < fld.length; i++) {
				flf = fld[i].getFunction(name);
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
			BIF bif=new BIF(name);
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
			cfml.next();
            comments();

			// finish
			if (count==0 && cfml.isCurrent(')'))
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
							cfml,
							"too many Attributes in function [" + name + "]");
				}
			// Fix
				else {
					if(libLen <= count)
						throw new TemplateException(
							cfml,
							"too many Attributes in function [" + name + "]");
				}
				
			}
			
			//Argument arg;
			if (checkLibrary && !isDynamic) {
				// current attribues from library
				FunctionLibFunctionArg funcLibAtt =(FunctionLibFunctionArg) arrFuncLibAtt.get(count);
				fm.addArgument(functionArgument(funcLibAtt.getTypeAsString()));	
			} 
			else {
				fm.addArgument(functionArgument());
			}

            comments();
			count++;
			if (cfml.isCurrent(')'))
				break;
		} 
		while (cfml.isCurrent(','));

		// end with ) ??		
		if (!cfml.forwardIfCurrent(')'))
			throw new TemplateException(
				cfml,
				"Invalid Syntax Closing [)] for function ["
					+ name
					+ "] not found");

		// check min attributes
		if (checkLibrary && flf.getArgMin() > count)
			throw new TemplateException(
				cfml,
				"to few Attributes in function [" + name + "]");

        comments();
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
	protected Expression sharp() throws TemplateException {
		if(!cfml.forwardIfCurrent('#'))
			return null;
		Expression expr;
        comments();
        boolean old=allowLowerThan;
        allowLowerThan=true;
		expr = assignOp();
		allowLowerThan=old;
        comments();
		if (!cfml.forwardIfCurrent('#'))
			throw new TemplateException(
				cfml,
				"Syntax Error, Invalid Construct "+(cfml.length()<30?cfml.toString():""));
        comments();
		return expr;
	}
	
	/**
	 * @return parsed Element
	 * @throws TemplateException
	 */
	protected Expression simple() throws TemplateException {
		StringBuffer sb=new StringBuffer();
		int line=cfml.getLine();
		while(cfml.isValidIndex()) {
			if(cfml.isCurrent(' ') || cfml.isCurrent('>') || cfml.isCurrent("/>")) break;
			else if(cfml.isCurrent('"') || cfml.isCurrent('#') || cfml.isCurrent('\'')) {
				throw new TemplateException(cfml,"simple attribute value can't contain ["+cfml.getCurrent()+"]");
			}
			else sb.append(cfml.getCurrent());
			cfml.next();
		}
        comments();
		
		return LitString.toExprString(sb.toString(),line);
	}
    

    /**
     *  Liest alle folgenden Komentare ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @throws TemplateException
     */
    protected void comments() throws TemplateException {
        cfml.removeSpace();
        while(comment()){cfml.removeSpace();}
    }
    
    /**
     *  Liest einen Einzeiligen Kommentar ein.
      * <br />
     * EBNF:<br />
     * <code>{?-"\n"} "\n";</code>
     * @return bool Wurde ein Kommentar entfernt?
     * @throws TemplateException
     */
    protected boolean comment() throws TemplateException {
        if(singleLineComment() || multiLineComment() || CFMLTransformer.comment(cfml)) return true;
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
    private boolean multiLineComment() throws TemplateException {
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
    private boolean singleLineComment() {
        if(!cfml.forwardIfCurrent("//")) return false;
        return cfml.nextLine();
    }

	/**
	 * @return the ignoreScopes
	 */
	public boolean isIgnoreScopes() {
		return ignoreScopes;
	}

	/**
	 * @param ignoreScopes the ignoreScopes to set
	 */
	public void setIgnoreScopes(boolean ignoreScopes) {
		this.ignoreScopes = ignoreScopes;
	}
    
}