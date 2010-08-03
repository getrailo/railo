
package railo.runtime.sql.old;


// Referenced classes of package Zql:
//            ZStatement, ZExp

public final class ZDelete
    implements ZStatement
{

    public ZDelete(String s)
    {
        where_ = null;
        table_ = new String(s);
    }

    public void addWhere(ZExp zexp)
    {
        where_ = zexp;
    }

    public String getTable()
    {
        return table_;
    }

    public ZExp getWhere()
    {
        return where_;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("delete ");
        if(where_ != null)
            stringbuffer.append("from ");
        stringbuffer.append(table_);
        if(where_ != null)
            stringbuffer.append(" where " + where_.toString());
        return stringbuffer.toString();
    }

    String table_;
    ZExp where_;
}