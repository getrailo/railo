package railo.transformer.expression.var;

import org.objectweb.asm.Type;

import railo.transformer.Context;
import railo.transformer.TransformerException;
import railo.transformer.expression.Expression;
import railo.transformer.expression.Invoker;

public interface Variable extends Expression, Invoker {
	
	public int getScope();
	

	/**
	 * @return the first member or null if there no member
	 */
	public Member getFirstMember();

	/**
	 * @return the first member or null if there no member
	 */
	public Member getLastMember();
	
	public void ignoredFirstMember(boolean b);
	public boolean ignoredFirstMember();
	
	public void fromHash(boolean fromHash);
	public boolean fromHash();
	
	public Expression getDefaultValue();
	public void setDefaultValue(Expression defaultValue);
	
	public Boolean getAsCollection();
	public void setAsCollection(Boolean asCollection);

	public int getCount();
	public Type writeOutCollection(Context c, int mode) throws TransformerException;
	 
}
