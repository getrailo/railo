
package railo.runtime.sql.old;

import java.util.Vector;


public final class ZQuery
    implements ZStatement, ZExp
{

    public ZQuery()
    {
        distinct_ = false;
        where_ = null;
        groupby_ = null;
        setclause_ = null;
        orderby_ = null;
        forupdate_ = false;
    }

    public void addSelect(Vector vector)
    {
        select_ = vector;
    }

    public void addFrom(Vector vector)
    {
        from_ = vector;
    }

    public void addWhere(ZExp zexp)
    {
        where_ = zexp;
    }

    public void addGroupBy(ZGroupBy zgroupby)
    {
        groupby_ = zgroupby;
    }

    public void addSet(ZExpression zexpression)
    {
        setclause_ = zexpression;
    }

    public void addOrderBy(Vector vector)
    {
        orderby_ = vector;
    }

    public Vector getSelect()
    {
        return select_;
    }

    public Vector getFrom()
    {
        return from_;
    }

    public ZExp getWhere()
    {
        return where_;
    }

    public ZGroupBy getGroupBy()
    {
        return groupby_;
    }

    public ZExpression getSet()
    {
        return setclause_;
    }

    public Vector getOrderBy()
    {
        return orderby_;
    }

    public boolean isDistinct()
    {
        return distinct_;
    }

    public boolean isForUpdate()
    {
        return forupdate_;
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer("select ");
        if(distinct_) stringbuffer.append("distinct ");
        stringbuffer.append(select_.elementAt(0).toString());
        for(int i = 1; i < select_.size(); i++)
            stringbuffer.append(", " + select_.elementAt(i).toString());
        
        stringbuffer.append(" from ");
        stringbuffer.append(from_.elementAt(0).toString());
        for(int j = 1; j < from_.size(); j++)
            stringbuffer.append(", " + from_.elementAt(j).toString());
        
        if(where_ != null)
            stringbuffer.append(" where " + where_.toString());
        
        if(groupby_ != null)
            stringbuffer.append(" " + groupby_.toString());
        
        if(setclause_ != null)
            stringbuffer.append(" " + setclause_.toString());
        
        if(orderby_ != null)
        {
            stringbuffer.append(" order by ");
            stringbuffer.append(orderby_.elementAt(0).toString());
            for(int k = 1; k < orderby_.size(); k++)
                stringbuffer.append(", " + orderby_.elementAt(k).toString());

        }
        if(forupdate_)
            stringbuffer.append(" for update");
        return stringbuffer.toString();
    }

    Vector select_;
    boolean distinct_;
    Vector from_;
    ZExp where_;
    ZGroupBy groupby_;
    ZExpression setclause_;
    Vector orderby_;
    boolean forupdate_;
}