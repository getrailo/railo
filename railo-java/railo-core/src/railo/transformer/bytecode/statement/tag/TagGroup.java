package railo.transformer.bytecode.statement.tag;

import railo.transformer.bytecode.Position;

public abstract class TagGroup extends TagBase {

	public static final short TAG_LOOP=1;
	public static final short TAG_OUTPUT=2;

	 private int numberIterator=-1;
	 private int query=-1;
	 private int group=-1;
	 private int pid;
	
	public TagGroup(Position start, Position end) {
		super(start, end);
		// TODO Auto-generated constructor stub
	}

	public abstract short getType();

	//public abstract boolean hasQuery();

	//public abstract boolean hasGroup();
	

	public final int getNumberIterator()	{
		return numberIterator;
	}
	
	public final void setNumberIterator(int numberIterator)	{
		this.numberIterator= numberIterator;
	}
	
	public final boolean hasNumberIterator()	{
		return numberIterator!=-1;
	}


	/**
	 * returns if output has query
	 * @return has query
	 */
	public final boolean hasQuery()	{
		return getAttribute("query")!=null;
	}

	/**
	 * returns if output has query
	 * @return has query
	 */
	public final boolean hasGroup()	{
		return getAttribute("group")!=null;
	}
	
	public final int getQuery()	{
		return query;
	}
	
	public final void setQuery(int query)	{
		this.query= query;
	}
	
	public final int getGroup()	{
		return group;
	}
	
	public final void setGroup(int group)	{
		this.group= group;
	}
	
	public final int getPID()	{
		return pid;
	}
	
	public final void setPID(int pid)	{
		this.pid= pid;
	}
}
