
package railo.runtime.sql.old;

import java.io.Serializable;

// Referenced classes of package Zql:
//            ZExp

public final class ZOrderBy
    implements Serializable
{

    public ZOrderBy(ZExp zexp)
    {
        asc_ = true;
        exp_ = zexp;
    }

    public void setAscOrder(boolean flag)
    {
        asc_ = flag;
    }

    public boolean getAscOrder()
    {
        return asc_;
    }

    public ZExp getExpression()
    {
        return exp_;
    }

    public String toString()
    {
        return exp_.toString() + " " + (asc_ ? "ASC" : "DESC");
    }

    ZExp exp_;
    boolean asc_;
}