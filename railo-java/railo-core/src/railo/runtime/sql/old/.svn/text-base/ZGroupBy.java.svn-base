
package railo.runtime.sql.old;

import java.io.Serializable;
import java.util.Vector;

// Referenced classes of package Zql:
//            ZExp

public final class ZGroupBy
    implements Serializable
{

    public ZGroupBy(Vector vector)
    {
        having_ = null;
        groupby_ = vector;
    }

    public void setHaving(ZExp zexp)
    {
        having_ = zexp;
    }

    public Vector getGroupBy()
    {
        return groupby_;
    }

    public ZExp getHaving()
    {
        return having_;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("group by ");
        stringbuffer.append(groupby_.elementAt(0).toString());
        for(int i = 1; i < groupby_.size(); i++)
            stringbuffer.append(", " + groupby_.elementAt(i).toString());

        if(having_ != null)
            stringbuffer.append(" having " + having_.toString());
        return stringbuffer.toString();
    }

    Vector groupby_;
    ZExp having_;
}