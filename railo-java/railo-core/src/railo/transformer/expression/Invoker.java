package railo.transformer.expression;

import java.util.List;

import railo.transformer.expression.var.Member;

public interface Invoker extends Expression {

	/**add a member to the invoker
	 * @param member
	 */
	public void addMember(Member member);
	
	/**
	 * returns all members as a List
	 * @return
	 */
	public List<Member> getMembers();
}
