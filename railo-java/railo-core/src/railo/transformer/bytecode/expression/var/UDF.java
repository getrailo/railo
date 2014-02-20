package railo.transformer.bytecode.expression.var;

import railo.transformer.bytecode.Page;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;

public final class UDF extends FunctionMember {
	
	private ExprString name;

	public UDF(Expression name) {
		this.name=name.getFactory().toExprString(name);
	}
	public UDF(Page page,String name) {
		this.name=page.getFactory().createLitString(name);
	}
	
	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}
}
