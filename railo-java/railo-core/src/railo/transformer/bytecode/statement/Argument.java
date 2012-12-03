package railo.transformer.bytecode.statement;

import java.util.Map;

import railo.runtime.type.FunctionArgumentImpl;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.literal.LitString;

public final class Argument {

	
	private static final Expression DEFAULT_TYPE_NULL = 				LitInteger.toExpr(FunctionArgumentImpl.DEFAULT_TYPE_NULL);
	private static final Expression DEFAULT_TYPE_LITERAL =				LitInteger.toExpr(FunctionArgumentImpl.DEFAULT_TYPE_LITERAL);
	private static final Expression DEFAULT_TYPE_RUNTIME_EXPRESSION =	LitInteger.toExpr(FunctionArgumentImpl.DEFAULT_TYPE_RUNTIME_EXPRESSION);
	private static final LitString RUNTIME_EXPRESSION =				(LitString) LitString.toExprString("[runtime expression]");
	
	
	private ExprString name;
	private ExprString type;
	private ExprBoolean required;
	private Expression defaultValue;
	private ExprString displayName;
	private ExprString hint;
	private Map meta;
	private ExprBoolean passByReference;


	/**
	 * Constructor of the class
	 * @param name
	 * @param type
	 * @param required
	 * @param defaultValue
	 * @param displayName
	 * @param hint
	 * @param hint2 
	 * @param meta 
	 */
	public Argument(Expression name, Expression type, Expression required, Expression defaultValue, ExprBoolean passByReference,Expression displayName, Expression hint, Map meta) {
		this.name=CastString.toExprString(name);
		this.type=CastString.toExprString(type);
		this.required=CastBoolean.toExprBoolean(required);
		this.defaultValue=defaultValue;
		this.displayName=litString(CastString.toExprString(displayName),RUNTIME_EXPRESSION);
		this.hint=litString(hint, RUNTIME_EXPRESSION);
		this.passByReference=passByReference;
		this.meta=meta;
	}

	private LitString litString(Expression expr, LitString defaultValue) {
		ExprString str = CastString.toExprString(expr);
		if(str instanceof LitString) return (LitString) str;
		return defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public Expression getDefaultValue() {
		return defaultValue;
	}
	
	public Expression getDefaultValueType(){
		if(defaultValue==null) return DEFAULT_TYPE_NULL;
		if(defaultValue instanceof Literal) return DEFAULT_TYPE_LITERAL;
		return DEFAULT_TYPE_RUNTIME_EXPRESSION;
	}

	/**
	 * @return the displayName
	 */
	public ExprString getDisplayName() {
		return displayName;
	}

	/**
	 * @return the hint
	 */
	public ExprString getHint() {
		return hint;
	}

	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}

	/**
	 * @return the passBy
	 */
	public ExprBoolean isPassByReference() {
		return passByReference;
	}

	/**
	 * @return the required
	 */
	public ExprBoolean getRequired() {
		return required;
	}

	/**
	 * @return the type
	 */
	public ExprString getType() {
		return type;
	}
	public Map getMetaData() {
		return meta;
	}

}