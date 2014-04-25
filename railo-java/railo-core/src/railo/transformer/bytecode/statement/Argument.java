package railo.transformer.bytecode.statement;

import java.util.Map;

import railo.runtime.type.FunctionArgument;
import railo.transformer.Factory;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.literal.Literal;

public final class Argument {

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
		LitString re = name.getFactory().createLitString("[runtime expression]");
		
		this.name=name.getFactory().toExprString(name);
		this.type=name.getFactory().toExprString(type);
		this.required=name.getFactory().toExprBoolean(required);
		this.defaultValue=defaultValue;
		this.displayName=litString(name.getFactory().toExprString(displayName),re);
		this.hint=litString(hint, re);
		this.passByReference=passByReference;
		this.meta=meta;
	}

	private LitString litString(Expression expr, LitString defaultValue) {
		ExprString str = expr.getFactory().toExprString(expr);
		if(str instanceof LitString) return (LitString) str;
		return defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public Expression getDefaultValue() {
		return defaultValue;
	}
	
	public Expression getDefaultValueType(Factory f){
		if(defaultValue==null) return f.createLitInteger(FunctionArgument.DEFAULT_TYPE_NULL);
		if(defaultValue instanceof Literal) return f.createLitInteger(FunctionArgument.DEFAULT_TYPE_LITERAL);
		return f.createLitInteger(FunctionArgument.DEFAULT_TYPE_RUNTIME_EXPRESSION);
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