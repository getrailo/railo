package railo.runtime.interpreter;

import java.util.ArrayList;

import railo.commons.lang.CFTypes;
import railo.commons.lang.ParserString;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.interpreter.ref.cast.Casting;
import railo.runtime.interpreter.ref.func.BIFCall;
import railo.runtime.interpreter.ref.func.UDFCall;
import railo.runtime.interpreter.ref.literal.LBoolean;
import railo.runtime.interpreter.ref.literal.LFunctionValue;
import railo.runtime.interpreter.ref.literal.LNumber;
import railo.runtime.interpreter.ref.literal.LString;
import railo.runtime.interpreter.ref.literal.LStringBuffer;
import railo.runtime.interpreter.ref.literal.Literal;
import railo.runtime.interpreter.ref.op.And;
import railo.runtime.interpreter.ref.op.CT;
import railo.runtime.interpreter.ref.op.Concat;
import railo.runtime.interpreter.ref.op.Cont;
import railo.runtime.interpreter.ref.op.Div;
import railo.runtime.interpreter.ref.op.EEQ;
import railo.runtime.interpreter.ref.op.EQ;
import railo.runtime.interpreter.ref.op.EQV;
import railo.runtime.interpreter.ref.op.Exp;
import railo.runtime.interpreter.ref.op.GT;
import railo.runtime.interpreter.ref.op.GTE;
import railo.runtime.interpreter.ref.op.Imp;
import railo.runtime.interpreter.ref.op.IntDiv;
import railo.runtime.interpreter.ref.op.LT;
import railo.runtime.interpreter.ref.op.LTE;
import railo.runtime.interpreter.ref.op.Minus;
import railo.runtime.interpreter.ref.op.Mod;
import railo.runtime.interpreter.ref.op.Multi;
import railo.runtime.interpreter.ref.op.NCT;
import railo.runtime.interpreter.ref.op.NEEQ;
import railo.runtime.interpreter.ref.op.NEQ;
import railo.runtime.interpreter.ref.op.Negate;
import railo.runtime.interpreter.ref.op.Not;
import railo.runtime.interpreter.ref.op.Or;
import railo.runtime.interpreter.ref.op.Plus;
import railo.runtime.interpreter.ref.op.Xor;
import railo.runtime.interpreter.ref.var.ArgumentBind;
import railo.runtime.interpreter.ref.var.Assign;
import railo.runtime.interpreter.ref.var.DynAssign;
import railo.runtime.interpreter.ref.var.Variable;
import railo.runtime.type.Scope;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

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
                     "arguments" | "cookie" | " client";
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
public class CFMLExpressionInterpreter {


    private static final LNumber PLUS_ONE = new LNumber(new Double(1));
    private static final LNumber MINUS_ONE = new LNumber(new Double(-1));
	
    protected static final short STATIC=0;
    private static final short DYNAMIC=1;
	private static FunctionLibFunction JSON_ARRAY = null;
	private static FunctionLibFunction JSON_STRUCT = null;
	

	//private static final int CASE_TYPE_UPPER = 0;
	//private static final int CASE_TYPE_LOWER = 1;
	//private static final int CASE_TYPE_ORIGINAL = 2;
	
    protected short mode=0;
    
    protected ParserString cfml;
    //protected Document doc;
    //protected FunctionLib[] fld;
    protected PageContext pc;
    private FunctionLib fld;
	protected boolean allowNullConstant=false;
    //private Null nulls=Null.getInstance();
    
    
    /**
     * Wird aufgerufen um aus dem ￼bergebenen CFMLString einen Ausdruck auszulesen 
     * und diese zu interpretieren.
     * <br />
     * Beispiel eines ￼bergebenen String:<br />
     * <code>session.firstName</code> oder <code>trim(left('test'&var1,3))</code>
     * <br />
     * EBNF:<br />
     * <code>spaces impOp;</code>
     * 
     * @param pc
     * @param cfml
     * @return
     * @throws PageException
     */
    public Object interpret(PageContext pc,ParserString cfml) throws PageException {    
        this.cfml = cfml;
        this.pc=pc;
        if(pc!=null)fld=((ConfigImpl)pc.getConfig()).getCombinedFLDs();
        
        if(JSON_ARRAY==null)JSON_ARRAY=fld.getFunction("_jsonArray");
		if(JSON_STRUCT==null)JSON_STRUCT=fld.getFunction("_jsonStruct");
        
        cfml.removeSpace();
        Ref ref=assignOp();
        cfml.removeSpace();
        
        if(cfml.isAfterLast()) {
            return ref.getValue();
        }
        throw new ExpressionException("Syntax Error, invalid Expression ["+cfml.toString()+"]");
    }

    
    /*private FunctionLibFunction getFLF(String name) {
		FunctionLibFunction flf=null;
		for (int i = 0; i < flds.length; i++) {
			flf = flds[i].getFunction(name);
			if (flf != null)
				break;
		}
		return flf;
	}*/
    
    
    protected Object interpretPart(PageContext pc,ParserString cfml) throws PageException { 
        this.cfml = cfml;
        this.pc=pc;
        if(pc!=null)fld=((ConfigImpl)pc.getConfig()).getCombinedFLDs();
        
        cfml.removeSpace();
        return assignOp().getValue();
    }
    
    /**
     * Wird aufgerufen um aus dem ￼bergebenen String einen Ausdruck auszulesen 
     * und diesen zu interpretieren.
     * <br />
     * Beispiel eines ￼bergebenen String:<br />
     * <code>session.firstName</code> oder <code>trim(left('test'&var1,3))</code>
     * <br />
     * EBNF:<br />
     * <code>spaces impOp;</code>
     * 
     * @param pc
     * @param str
     * @return
     * @throws PageException
     */
    public Object interpret(PageContext pc,String str) throws PageException {
    	return interpret(pc,new ParserString(str));
    }

    /**
    * Liest einen gelableten  Funktionsparamter ein
    * <br />
    * EBNF:<br />
    * <code>assignOp [":" spaces assignOp];</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref functionArgDeclarationVarString() throws PageException {
        
            cfml.removeSpace();
            StringBuffer str=new StringBuffer();
            String id=null;
            while((id=identifier(false))!=null) {
                if(str.length()>0)str.append('.');
                str.append(id);
                cfml.removeSpace();
                if(!cfml.forwardIfCurrent('.')) break;
                cfml.removeSpace();
            }
            cfml.removeSpace();
            if(str.length()>0 && cfml.charAt(cfml.getPos()-1)!='.') 
                return new LString(str.toString());

        throw new ExpressionException("invalid variable name definition");
    }

    /**
    * Liest einen gelableten  Funktionsparamter ein
    * <br />
    * EBNF:<br />
    * <code>assignOp [":" spaces assignOp];</code>
     * @param isVariableString
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref functionArgDeclaration() throws PageException {
        Ref ref = impOp();
        if (cfml.forwardIfCurrent(':') || cfml.forwardIfCurrent('=')) {
            cfml.removeSpace();
            ref=new LFunctionValue(ref,assignOp());
        }
        return ref;
    }

    /**
    * Transfomiert Zuweisungs Operation.
    * <br />
    * EBNF:<br />
    * <code>eqvOp ["=" spaces assignOp];</code>
    * @return CFXD Element
    * @throws PageException 
    */
    protected Ref assignOp() throws PageException {
        Ref ref = contOp();

        if (cfml.forwardIfCurrent('=')) {
            cfml.removeSpace();
            if(mode==STATIC || ref instanceof Literal) {
                ref=new DynAssign(pc,ref,assignOp());
            }
            else {
                ref=new Assign(ref,assignOp());
            }
        }
        return ref;
    }
    

    private Ref contOp() throws PageException {
        Ref ref = impOp();
        while(cfml.forwardIfCurrent('?')) {
            cfml.removeSpace();
            Ref left = assignOp();            
            if(!cfml.forwardIfCurrent(':'))
            	throw new ExpressionException("Syntax Error, invalid conditional operator ["+cfml.toString()+"]");
            cfml.removeSpace();
            Ref right = assignOp();
            ref=new Cont(ref,left,right);
        }
        return ref;
    }

    /**
    * Transfomiert eine Implication (imp) Operation.
    * <br />
    * EBNF:<br />
    * <code>eqvOp {"imp" spaces eqvOp};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref impOp() throws PageException {
        Ref ref = eqvOp();
        while(cfml.forwardIfCurrentAndNoWordAfter("imp")) {
            cfml.removeSpace();
            ref=new Imp(ref,eqvOp());
        }
        return ref;
    }

    /**
    * Transfomiert eine  Equivalence (eqv) Operation.
    * <br />
    * EBNF:<br />
    * <code>xorOp {"eqv" spaces xorOp};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref eqvOp() throws PageException {
        Ref ref = xorOp();
        while(cfml.forwardIfCurrent("eqv")) {
            cfml.removeSpace();
            ref=new EQV(ref,xorOp());
        }
        return ref;
    }

    /**
    * Transfomiert eine  Xor (xor) Operation.
    * <br />
    * EBNF:<br />
    * <code>orOp {"xor" spaces  orOp};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref xorOp() throws PageException {
        Ref ref = orOp();
        while(cfml.forwardIfCurrent("xor")) {
            cfml.removeSpace();
            ref=new Xor(ref,orOp());
        }
        return ref;
    }

    /**
    * Transfomiert eine  Or (or) Operation. Im Gegensatz zu CFMX ,
    * werden "||" Zeichen auch als Or Operatoren anerkannt.
    * <br />
    * EBNF:<br />
    * <code>andOp {("or" | "||") spaces andOp}; (* "||" Existiert in CFMX nicht *)</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref orOp() throws PageException {
        Ref ref = andOp();
        while(cfml.isValidIndex() && (cfml.forwardIfCurrent("||") || cfml.forwardIfCurrent("or"))) {
            cfml.removeSpace();
            ref=new Or(ref,andOp());
        }
        return ref;
    }

    /**
    * Transfomiert eine  And (and) Operation. Im Gegensatz zu CFMX ,
    * werden "&&" Zeichen auch als And Operatoren anerkannt.
    * <br />
    * EBNF:<br />
    * <code>notOp {("and" | "&&") spaces notOp}; (* "&&" Existiert in CFMX nicht *)</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref andOp() throws PageException {
        Ref ref = notOp();
        while(cfml.isValidIndex() && (cfml.forwardIfCurrent("&&") || cfml.forwardIfCurrent("and"))) {
            cfml.removeSpace();
            ref=new And(ref,notOp());
        }
        return ref;
    }

    /**
    * Transfomiert eine  Not (not) Operation. Im Gegensatz zu CFMX ,
    * wird das "!" Zeichen auch als Not Operator anerkannt.
    * <br />
    * EBNF:<br />
    * <code>[("not"|"!") spaces] decsionOp; (* "!" Existiert in CFMX nicht *)</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref notOp() throws PageException {
    	if(cfml.isValidIndex()) {
	    	if (cfml.isCurrent('!') && !cfml.isCurrent("!=")) {
	    		cfml.next();
	            cfml.removeSpace();
	            return new Not(decsionOp());
	        }
	    	else if (cfml.forwardIfCurrentAndNoWordAfter("not")) {
	            cfml.removeSpace();
	            return new Not(decsionOp());
	        }
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
    * @throws PageException 
    */
    private Ref decsionOp() throws PageException {

        Ref ref = concatOp();
        boolean hasChanged=false;
        // ct, contains
        if(cfml.isValidIndex()){
            do {
                hasChanged=false;
                if(cfml.isCurrent('c')) {
                        if (cfml.forwardIfCurrent("ct")) {
                            cfml.removeSpace();
                            ref=new CT(ref,concatOp());
                            hasChanged=true;
                        } 
                        else if (cfml.forwardIfCurrent("contains")){ 
                            cfml.removeSpace();
                            ref=new CT(ref,concatOp());
                            hasChanged=true;
                        }
                }
                // does not contain
                else if (cfml.forwardIfCurrent("does not contain")){ 
                    cfml.removeSpace();
                    ref=new NCT(ref,concatOp());
                    hasChanged=true;
                }
                
                // equal, eq
                else if (cfml.isCurrent("eq") && !cfml.isCurrent("eqv")) {
                    cfml.setPos(cfml.getPos()+2);
                    cfml.forwardIfCurrent("ual");
                    cfml.removeSpace();
                    ref=new EQ(ref,concatOp());
                    hasChanged=true;
                }
                // ==
                else if (cfml.forwardIfCurrent("==")) {
                    if(cfml.forwardIfCurrent('=')) 		{
                    	cfml.removeSpace();
                    	ref = new EEQ(ref,concatOp());
                    }
    				else {
    					cfml.removeSpace();
                    	ref=new EQ(ref,concatOp());
    				}
                    hasChanged=true;
                }
                
                // !=
                else if (cfml.forwardIfCurrent("!=")) {
                    if(cfml.forwardIfCurrent('=')) {
                    	cfml.removeSpace();
                    	ref = new NEEQ(ref,concatOp());
                    }
    				else {
    					cfml.removeSpace();
                    	ref=new NEQ(ref,concatOp());
    				}
                    hasChanged=true;
                }

                // <=/</<>
    			else if (cfml.forwardIfCurrent('<')) {
    				if(cfml.forwardIfCurrent('=')) 		{
    					cfml.removeSpace();
                    	ref = new LTE(ref,concatOp());
    				}
    				else if(cfml.forwardIfCurrent('>')) {
    					cfml.removeSpace();
                    	ref = new NEQ(ref,concatOp());
    				}
    				else 								{
    					cfml.removeSpace();
                    	ref = new LT(ref,concatOp());
    				}
    				hasChanged=true;
    			}
                // >/>=
    			else if (cfml.forwardIfCurrent('>')) {
    				if(cfml.forwardIfCurrent('=')) 		{
    					cfml.removeSpace();
                    	ref = new GTE(ref,concatOp());
    				}
    				else 								{
    					cfml.removeSpace();
                    	ref = new GT(ref,concatOp());
    				}
    				hasChanged=true;
    			}
                
                // gt, gte, greater than or equal to, greater than
                else if (cfml.isCurrent('g')) {
                    if (cfml.forwardIfCurrent("gt")) {
                        if(cfml.forwardIfCurrent('e')) {
                            cfml.removeSpace();
                            ref=new GTE(ref,concatOp());
                        }
                        else {
                            cfml.removeSpace();
                            ref=new GT(ref,concatOp());
                        }
                        hasChanged=true;
                    } 
                    else if (cfml.forwardIfCurrent("greater than")) {
                        if(cfml.forwardIfCurrent(" or equal to")) {
                            cfml.removeSpace();
                            ref=new GTE(ref,concatOp());
                        }
                        else {
                            cfml.removeSpace();
                            ref=new GT(ref,concatOp());
                        }
                        hasChanged=true;
                    }   
                    else if (cfml.forwardIfCurrent("ge")) {
                        cfml.removeSpace();
                        ref=new GTE(ref,concatOp());
                        hasChanged=true;
                    }               
                }
                
                // is, is not
                else if (cfml.forwardIfCurrent("is")) {
                    if(cfml.forwardIfCurrent(" not")) {
                        cfml.removeSpace();
                        ref=new NEQ(ref,concatOp());
                    }
                    else {
                        cfml.removeSpace();
                        ref=new EQ(ref,concatOp());
                    }
                    hasChanged=true;
                }
                
                // lt, lte, less than, less than or equal to
                else if (cfml.isCurrent('l')) {
                    if (cfml.forwardIfCurrent("lt")) {
                        if(cfml.forwardIfCurrent('e')) {
                            cfml.removeSpace();
                            ref=new LTE(ref,concatOp());
                        }
                        else {
                            cfml.removeSpace();
                            ref=new LT(ref,concatOp());
                        }
                        hasChanged=true;
                    } 
                    else if (cfml.forwardIfCurrent("less than")) {
                        if(cfml.forwardIfCurrent(" or equal to")) {
                            cfml.removeSpace();
                            ref=new LTE(ref,concatOp());
                        }
                        else {
                            cfml.removeSpace();
                            ref=new LT(ref,concatOp());
                        }
                        hasChanged=true;
                    }   
                    else if (cfml.forwardIfCurrent("le")) {
                        cfml.removeSpace();
                        ref=new LTE(ref,concatOp());
                        hasChanged=true;
                    }               
                }
                
                // neq, not equal, nct
                else if (cfml.isCurrent('n')) {
                    // Not Equal
                        if (cfml.forwardIfCurrent("neq"))   {
                            cfml.removeSpace(); 
                            ref=new NEQ(ref,concatOp());
                            hasChanged=true;
                        }
                    // Not Equal (Alias)
                        else if (cfml.forwardIfCurrent("not equal")){ 
                            cfml.removeSpace();
                            ref=new NEQ(ref,concatOp());
                            hasChanged=true; 
                        }
                    // nct
                        else if (cfml.forwardIfCurrent("nct"))  { 
                            cfml.removeSpace();
                            ref=new NCT(ref,concatOp());
                            hasChanged=true;
                        }   
                }
            }while(hasChanged);
        }
        return ref;
    }

    /**
    * Transfomiert eine  Konkatinations-Operator (&) Operation. Im Gegensatz zu CFMX ,
    * wird das "!" Zeichen auch als Not Operator anerkannt.
    * <br />
    * EBNF:<br />
    * <code>plusMinusOp {"&" spaces concatOp};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref concatOp() throws PageException {
        Ref ref = plusMinusOp();
        
        while(cfml.isCurrent('&') && !cfml.isNext('&')) {
            cfml.next();
            ref=_concat(ref);
            //cfml.removeSpace();
            //ref=new Concat(pc,ref,plusMinusOp());
        }
        return ref;
    }

    /**
    * Transfomiert die mathematischen Operatoren Plus und Minus (1,-).
    * <br />
    * EBNF:<br />
    * <code>modOp [("-"|"+") spaces plusMinusOp];</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref plusMinusOp() throws PageException {
        Ref ref = modOp();
        
        while(!cfml.isLast()) {
            // Plus Operation
            if (cfml.forwardIfCurrent('+')) {
                ref=_plus(ref);
            	//cfml.removeSpace();
                //ref=new Plus(ref,modOp());
            }
            // Minus Operation
            else if (cfml.forwardIfCurrent('-')) {
                ref=_minus(ref);
            	//cfml.removeSpace();
                //ref=new Minus(ref,modOp());
            }
            else break;
        }
        return ref;
    }

    private Ref _plus(Ref ref) throws PageException {
		// +=
		if (cfml.isCurrent('=')) {
			cfml.next();
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new Plus(ref,right);
			ref=new Assign(ref,res);
		}
		/*/ ++
		else if (cfml.isCurrent('+')) {
			cfml.next();
			cfml.removeSpace();
			Ref res = new Plus(ref,new LNumber(new Double(1)));
			ref=new Assign(ref,res);
			ref=new Minus(ref,new LNumber(new Double(1)));
		}*/
		else {	
            cfml.removeSpace();
            ref=new Plus(ref,modOp());
		}
		return ref;
	}
    
    private Ref _minus(Ref ref) throws PageException {
		// -=
		if (cfml.isCurrent('=')) {
			cfml.next();
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new Minus(ref,right);
			ref=new Assign(ref,res);
		}
		/*/ --
		else if (cfml.isCurrent('-')) {
			cfml.next();
			cfml.removeSpace();
			Ref res = new Minus(ref,new LNumber(new Double(1)));
			ref=new Assign(ref,res);
			ref=new Plus(ref,new LNumber(new Double(1)));
		}*/
		else {	
            cfml.removeSpace();
            ref=new Minus(ref,modOp());
		}
		return ref;
	}
    

    private Ref _div(Ref ref) throws PageException {
		// /=
		if (cfml.forwardIfCurrent('=')) {
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new Div(ref,right);
			ref=new Assign(ref,res);
		}
		else {	
            cfml.removeSpace();
            ref=new Div(ref,expoOp());
		}
		return ref;
	}
    
    private Ref _intdiv(Ref ref) throws PageException {
		// \=
		if (cfml.forwardIfCurrent('=')) {
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new IntDiv(ref,right);
			ref=new Assign(ref,res);
		}
		else {	
            cfml.removeSpace();
            ref=new IntDiv(ref,expoOp());
		}
		return ref;
	}

    private Ref _mod(Ref ref) throws PageException {
		// %=
		if (cfml.forwardIfCurrent('=')) {
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new Mod(ref,right);
			ref=new Assign(ref,res);
		}
		else {	
            cfml.removeSpace();
            ref=new Mod(ref,divMultiOp());
		}
		return ref;
	}
    private Ref _concat(Ref ref) throws PageException {
		// &=
		if (cfml.forwardIfCurrent('=')) {
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new  Concat(pc,ref,right);
			ref=new Assign(ref,res);
		}
		else {	
            cfml.removeSpace();
            ref=new Concat(pc,ref,plusMinusOp());
		}
		return ref;
	}
    
    private Ref _multi(Ref ref) throws PageException {
		// \=
		if (cfml.forwardIfCurrent('=')) {
			cfml.removeSpace();
			Ref right = assignOp();
			Ref res = new Multi(ref,right);
			ref=new Assign(ref,res);
		}
		else {	
            cfml.removeSpace();
            ref=new Multi(ref,expoOp());
		}
		return ref;
	}
    
    
    
    

    /**
    * Transfomiert eine Modulus Operation. Im Gegensatz zu CFMX ,
    * wird das "%" Zeichen auch als Modulus Operator anerkannt.
    * <br />
    * EBNF:<br />
    * <code>divMultiOp {("mod" | "%") spaces divMultiOp}; (* modulus operator , "%" Existiert in CFMX nicht *)</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref modOp() throws PageException {
        Ref ref = divMultiOp();
        
        while(cfml.isValidIndex() && (cfml.forwardIfCurrent('%') || cfml.forwardIfCurrent("mod"))) {
            ref=_mod(ref);
        	
        	//cfml.removeSpace();
            //ref=new Mod(ref,divMultiOp());
        }
        return ref;
    }

    /**
    * Transfomiert die mathematischen Operatoren Mal und Durch (*,/).
    * <br />
    * EBNF:<br />
    * <code>expoOp {("*"|"/") spaces expoOp};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref divMultiOp() throws PageException {
        Ref ref = expoOp();

        while (!cfml.isLast()) {
            // Multiply Operation
            if(cfml.forwardIfCurrent('*')) {
                ref=_multi(ref);
            	//cfml.removeSpace();
                //ref=new Multi(ref,expoOp());
            }
            // Divide Operation
            else if (cfml.isCurrent('/') && (!cfml.isCurrent("/>") )) {
                cfml.next(); 
                ref=_div(ref);
                //cfml.removeSpace();
                //ref=new Div(ref,expoOp());
            }
            // Divide Operation
            else if (cfml.isCurrent('\\')) {
                cfml.next(); 
                ref=_intdiv(ref);
                //cfml.removeSpace();
                //ref=new IntDiv(ref,expoOp());
            }
            else {
                break;
            }
        }
        return ref;
    }

    /**
    * Transfomiert den Exponent Operator (^,exp). Im Gegensatz zu CFMX ,
    * werden die Zeichen " exp " auch als Exponent anerkannt.
    * <br />
    * EBNF:<br />
    * <code>clip {("exp"|"^") spaces clip};</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref expoOp() throws PageException {
        Ref ref = unaryOp();

        while(cfml.isValidIndex() && (cfml.forwardIfCurrent('^') || cfml.forwardIfCurrent("exp"))) {
            cfml.removeSpace();
            ref=new Exp(ref,unaryOp());
        }
        return ref;
    }
    

    private Ref unaryOp() throws PageException {
        Ref ref = negateMinusOp();
        
		if (cfml.forwardIfCurrent("--")) 
			ref=_unaryOp(ref, false);
		
		else if (cfml.forwardIfCurrent("++")) 
			ref=_unaryOp(ref, true);
		return ref;
	}
    
    private Ref _unaryOp(Ref ref,boolean isPlus) throws PageException {
        cfml.removeSpace();
		Ref res = new Plus(ref,isPlus?PLUS_ONE:MINUS_ONE);
		ref=new Assign(ref,res);
		return new Plus(ref,isPlus?MINUS_ONE:PLUS_ONE);
	}
    

    /**
    * Liest die Vordlobe einer Zahl ein
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref negateMinusOp() throws PageException {
        // And Operation
        if (cfml.forwardIfCurrent('-')) {
        	if (cfml.forwardIfCurrent('-')) {
        		cfml.removeSpace();
				Ref expr = clip();
				Minus res = new Minus(expr,new LNumber(new Double(1)));
				return new Assign(expr,res);
			}	
            cfml.removeSpace();
            return new Negate(clip());
        	
        }
        if (cfml.forwardIfCurrent('+')) {
        	if (cfml.forwardIfCurrent('+')) {
        		cfml.removeSpace();
				Ref expr = clip();
				Plus res = new Plus(expr,new LNumber(new Double(1)));
				return new Assign(expr,res);
			}
        	cfml.removeSpace();
	        return new Casting(pc,"numeric",CFTypes.TYPE_NUMERIC,clip());
        	
        }
        return clip();
    }

    /**
    * Verarbeitet Ausdr￼cke die inerhalb einer Klammer stehen.
    * <br />
    * EBNF:<br />
    * <code>("(" spaces impOp ")" spaces) | checker;</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref clip() throws PageException {
        return checker();
    }
    
    /**
    * Hier werden die verschiedenen M￶glichen Werte erkannt 
    * und jenachdem wird mit der passenden Methode weitergefahren
    * <br />
    * EBNF:<br />
    * <code>string | number | dynamic | sharp;</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref checker() throws PageException {
        
        Ref ref=null;  
        // String
            if(cfml.isCurrentQuoter()) {
                // mode=STATIC; is at the end of the string function because must set after execution
                return string();
            } 
        // Number
            if(cfml.isCurrentDigit() || cfml.isCurrent('.')) {
                // mode=STATIC; is at the end of the string function because must set after execution
                return number();
            } 
        // Dynamic
            if((ref=dynamic())!=null) {
                mode=DYNAMIC;
                return ref;
            } 
        // Sharp
            if((ref=sharp())!=null) {
                mode=DYNAMIC;
                return ref;
            }  
        // JSON
            if((ref=json(JSON_ARRAY,'[',']'))!=null) {
				mode=DYNAMIC;
				return ref;
			} 
			if((ref=json(JSON_STRUCT,'{','}'))!=null) {
				mode=DYNAMIC;
				return ref;
			} 
            
			if(cfml.isAfterLast() && cfml.toString().trim().length()==0)
				return new LString("");
            
        // else Error
			throw new ExpressionException("Syntax Error, Invalid Construct","at position "+cfml.getPos()+" in ["+cfml.toString()+"]");  
    }
    
    
    protected Ref json(FunctionLibFunction flf, char start, char end) throws PageException {
		//print.out("start:"+start+":"+cfml.getCurrent());
		if(!cfml.isCurrent(start))return null;
		
		Ref[] args = functionArg(flf.getName(), false, flf,end);
		
		//if (!cfml.forwardIfCurrent(end))
		//	throw new ExpressionException("Invalid Syntax Closing ["+end+"] not found");
		
		return new BIFCall(pc,flf,args);
	}
    
    /**
    * Transfomiert einen lierale Zeichenkette.
    * <br />
    * EBNF:<br />
    * <code>("'" {"##"|"''"|"#" impOp "#"| ?-"#"-"'" } "'") | 
                     (""" {"##"|""""|"#" impOp "#"| ?-"#"-""" } """);</code>
    * @return CFXD Element
    * @throws PageException 
    */
    protected Ref string() throws PageException {
                        
        // Init Parameter
        char quoter = cfml.getCurrentLower();
        //String str="";
        LStringBuffer str=new LStringBuffer();
        Ref value=null;
        
        while(cfml.hasNext()) {
            cfml.next();
            // check sharp
            if(cfml.isCurrent('#')) {
                if(cfml.isNext('#')){
                    cfml.next();
                    str.append('#');
                }
                else {
                    cfml.next();
                    cfml.removeSpace();
                    if(!str.isEmpty() || value!=null) str.append(assignOp());
                    else value=assignOp();
                    cfml.removeSpace();
                    if (!cfml.isCurrent('#')) throw new ExpressionException("Invalid Syntax Closing [#] not found");
                }
            }
            else if(cfml.isCurrent(quoter)) {
                if(cfml.isNext(quoter)){
                    cfml.next();
                    str.append(quoter);
                }
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
            throw new ExpressionException("Invalid String Literal Syntax Closing ["+quoter+"] not found");
        
        cfml.removeSpace();
        mode=STATIC;
        if(value!=null) {
            if(str.isEmpty()) return value;
            return new Concat(pc,value,str);
        }
        return str;
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
    * @throws PageException 
    */
    private Ref number() throws PageException {
        // check first character is a number literal representation
        //if(!cfml.isCurrentDigit()) return null;
        
        StringBuffer rtn=new StringBuffer(6);
        
        // get digit on the left site of the dot
        if(cfml.isCurrent('.')) rtn.append('0');
        else digit(rtn);
        // read dot if exist
        if(cfml.forwardIfCurrent('.')) {
            rtn.append('.');
            int before=cfml.getPos();
            digit(rtn);

            if(before<cfml.getPos() && cfml.forwardIfCurrent('e')) {
                if(cfml.isCurrentDigit()) {
                    rtn.append('e');
                    digit(rtn);
                }
                else {
                    cfml.previous();
                }
            }
            
            
            // read right side of the dot
            if(before==cfml.getPos())
                throw new ExpressionException("Number can't end with [.]");
            //rtn.append(rightSite);
        }
        cfml.removeSpace();
        mode=STATIC;
        return new LNumber(rtn.toString());
        
    }
    
    /**
    * Liest die reinen Zahlen innerhalb des CFMLString aus und gibt diese als Zeichenkette zur￼ck. 
    * <br />
    * EBNF:<br />
    * <code>"0"|..|"9";</code>
     * @param rtn
    */
    private void digit(StringBuffer rtn) {
        
        while (cfml.isValidIndex()) {
            if(!cfml.isCurrentDigit())break;
            rtn.append(cfml.getCurrentLower());
            cfml.next();
        }
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
    * @throws PageException 
    */
    private Ref dynamic() throws PageException {
        // Die Implementation weicht ein wenig von der Grammatik ab, 
        // aber nicht in der Logik sondern rein wie es umgesetzt wurde.
        
        // get First Element of the Variable
        String name = identifier(false);
        if(name == null) {
            if (!cfml.forwardIfCurrent('('))return null;
            cfml.removeSpace();
            Ref ref = assignOp();

            if (!cfml.forwardIfCurrent(')'))
                throw new ExpressionException("Invalid Syntax Closing [)] not found");
            cfml.removeSpace();
            return subDynamic(ref);
        }

        //Element el;
        cfml.removeSpace();
        char first=name.charAt(0);
        
        // Boolean constant 
        if(first=='T' && name.equals("TRUE"))   {
            cfml.removeSpace();
            return LBoolean.TRUE;
        }
        else if(first=='F' && name.equals("FALSE")) {
            cfml.removeSpace();
            return LBoolean.FALSE;
        }   
        else if(first=='Y' && name.equals("YES"))   {
            cfml.removeSpace();
            return LBoolean.TRUE;
        }
        else if(first=='N')    {
        	if(name.equals("NO")){
        		cfml.removeSpace();
        		return LBoolean.FALSE;
        	}
        	else if(allowNullConstant && name.equals("NULL")){
        		cfml.removeSpace();
        		return new  LString("");
        	}
        }  
        
        // Extract Scope from the Variable

        //Object value = startElement(name);
        return subDynamic(startElement(name));

    }
    
    private Ref subDynamic(Ref ref) throws PageException {
        String name=null;
        
        // Loop over nested Variables
        while (cfml.isValidIndex()) {
            // .
            if (cfml.forwardIfCurrent('.')) {
                // Extract next Var String
                cfml.removeSpace();
                name = identifier(true);
                if(name==null) throw new ExpressionException("Invalid identifier");
                cfml.removeSpace();
                ref=new Variable(pc,ref,name);
            }
            // []
            else if (cfml.forwardIfCurrent('[')) {
            	cfml.removeSpace();
                ref=new Variable(pc,ref,assignOp());
                cfml.removeSpace();
                if (!cfml.forwardIfCurrent(']'))
                    throw new ExpressionException("Invalid Syntax Closing []] not found");
            }
            // finish
            else {
                break;
            }

            cfml.removeSpace();
            
            if (cfml.isCurrent('(')) {
                if(!(ref instanceof Set)) throw new ExpressionException("invalid syntax "+ref.getTypeName()+" can't called as function");
                Set set=(Set) ref;
                ref=new UDFCall(pc,set.getParent(),set.getKey(),functionArg(name,false, null,')'));
            }
        }
        if(ref instanceof railo.runtime.interpreter.ref.var.Scope) { 
            railo.runtime.interpreter.ref.var.Scope s=(railo.runtime.interpreter.ref.var.Scope)ref;
            if(s.getScope()==Scope.SCOPE_ARGUMENTS) {
                ref=new ArgumentBind(s);
            }
        }
        return ref;
    }

    /**
    * Extrahiert den Start Element einer Variale, 
    * dies ist entweder eine Funktion, eine Scope Definition oder eine undefinierte Variable. 
    * <br />
    * EBNF:<br />
    * <code>identifier "(" functionArg ")" | scope | identifier;</code>
    * @param name Einstiegsname
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref startElement(String name) throws PageException {
        
        // check function
        if (cfml.isCurrent('(')) {
            FunctionLibFunction function = fld.getFunction(name);
            Ref[] arguments = functionArg(name,true, function,')');
        	//print.out(name+":"+(function!=null));
            if(function!=null) return new BIFCall(pc,function,arguments);

            Ref ref = new railo.runtime.interpreter.ref.var.Scope(pc,Scope.SCOPE_UNDEFINED);
            return new UDFCall(pc,ref,name,arguments);
        }
        //check scope
        return scope(name);
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
    */
    private Ref scope(String idStr) {
        if (idStr.equals("var")) {
            String name=identifier(false);
            if(name!=null){
                cfml.removeSpace();
                return new Variable(pc,new railo.runtime.interpreter.ref.var.Scope(pc,Scope.SCOPE_LOCAL),name);
            }
        }
        int scope = VariableInterpreter.scopeString2Int(idStr);
        if(scope==Scope.SCOPE_UNDEFINED) {
            return new Variable(pc,new railo.runtime.interpreter.ref.var.Scope(pc,Scope.SCOPE_UNDEFINED),idStr);
            //return new VariableReference(pc.undefinedScope(),idStr);
        }
        return new railo.runtime.interpreter.ref.var.Scope(pc,scope);
        //return new ScopeReference(pc,scope);
        
    }
    
    /**
    * Liest einen Identifier aus und gibt diesen als String zur￼ck.
    * <br />
    * EBNF:<br />
    * <code>(letter | "_") {letter | "_"|digit};</code>
     * @param firstCanBeNumber 
    * @return Identifier.
    */
    private String identifier(boolean firstCanBeNumber) {
        //int start = cfml.getPos();
        if(!cfml.isCurrentLetter() && !cfml.isCurrentSpecial()) {
            if(!firstCanBeNumber)return null;
            else if(!cfml.isCurrentDigit())return null;
        }
        
        StringBuffer sb=new StringBuffer();
        //if(CASE_TYPE_UPPER==caseType)
        	sb.append(cfml.getCurrentUpper());
        /*else if(CASE_TYPE_ORIGINAL==caseType)
        	sb.append(cfml.getCurrent());
        else 
        	sb.append(cfml.getCurrentLower());*/
        do {
            cfml.next();
            if(!(cfml.isCurrentLetter()
                || cfml.isCurrentDigit()
                || cfml.isCurrentSpecial())) {
                    break;
                }

            //if(CASE_TYPE_UPPER==caseType)
            	sb.append(cfml.getCurrentUpper());
            /*else if(CASE_TYPE_ORIGINAL==caseType)
            	sb.append(cfml.getCurrent());
            else 
            	sb.append(cfml.getCurrentLower());*/
            
            
        }
        while (cfml.isValidIndex());
        return sb.toString();//cfml.substringLower(start,cfml.getPos()-start);
    }

    /**
    * Transfomiert ein Collection Element das in eckigen Klammern aufgerufen wird. 
    * <br />
    * EBNF:<br />
    * <code>"[" impOp "]"</code>
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref structElement() throws PageException {
        cfml.removeSpace();
        Ref ref = new Casting(pc,"string",CFTypes.TYPE_STRING,assignOp());
        cfml.removeSpace();
        return ref;
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
    * @param flf FLD Function definition .
    * @return CFXD Element
    * @throws PageException 
    */
    private Ref[] functionArg(String name,boolean checkLibrary,FunctionLibFunction flf,char end) throws PageException {

        // get Function Library
        checkLibrary=checkLibrary && flf!=null;     
        

        // Function Attributes
        ArrayList arr = new ArrayList();
        
        ArrayList arrFuncLibAtt = null;
        int libLen = 0;
        if (checkLibrary) {
            arrFuncLibAtt = flf.getArg();
            libLen = arrFuncLibAtt.size();
        }
        int count = 0;
        do {
            cfml.next();
            cfml.removeSpace();

            // finish
            if (cfml.isCurrent(end))
                break;

            // too many Attributes
            boolean isDynamic=false;
            int max=-1;
            if(checkLibrary) {
                isDynamic=isDynamic(flf);
                max=flf.getArgMax();
            // Dynamic
                if(isDynamic) {
                    if(max!=-1 && max <= count)
                        throw new ExpressionException("too many Attributes in function [" + name + "]");
                }
            // Fix
                else {
                    if(libLen <= count)
                        throw new ExpressionException("too many Attributes in function [" + name + "]");
                }
            }

            
            if (checkLibrary && !isDynamic) {
                // current attribues from library
                FunctionLibFunctionArg funcLibAtt = (FunctionLibFunctionArg) arrFuncLibAtt.get(count);
                short type=CFTypes.toShort(funcLibAtt.getType());
                if(type==CFTypes.TYPE_VARIABLE_STRING) {
                    arr.add(functionArgDeclarationVarString());
                }
                else {
                    arr.add(new Casting(pc,funcLibAtt.getTypeAsString(),type,functionArgDeclaration()));
                }
            } 
            else {
                arr.add(functionArgDeclaration());
            }

            // obj=andOrXor();
            cfml.removeSpace();
            count++;
        } 
        while (cfml.isCurrent(','));

        // end with ) ??        
        if (!cfml.forwardIfCurrent(end)) {
            if(name.startsWith("_json")) throw new ExpressionException("Invalid Syntax Closing ["+end+"] not found");
            throw new ExpressionException("Invalid Syntax Closing ["+end+"] for function ["+ name + "] not found");
        }

        // check min attributes
        if (checkLibrary && flf.getArgMin() > count)
            throw new ExpressionException("to less Attributes in function [" + name + "]");

        cfml.removeSpace();
        return (Ref[]) arr.toArray(new Ref[arr.size()]);
    }
 
    
    private boolean isDynamic(FunctionLibFunction flf) {
        return flf.getArgType()==FunctionLibFunction.ARG_DYNAMIC;
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
     * @throws PageException 
    */
    private Ref sharp() throws PageException {
        if(!cfml.forwardIfCurrent('#'))
            return null;
        Ref ref;
        cfml.removeSpace();
        ref = assignOp();
        cfml.removeSpace();
        if (!cfml.forwardIfCurrent('#'))
            throw new ExpressionException("Syntax Error, Invalid Construct");
        return ref;
    }

    /* *
     * Wandelt eine variable und ein key in eine reference um
     * @param value
     * @param name
     * @return cast a vlue in a reference
     * @throws PageException
     * /
    private Reference toReference(Object value, String name) {
        return NativeReference.getInstance(value, name);
    }*/
    
}