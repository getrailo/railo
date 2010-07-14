
package railo.runtime.sql.old;


// Referenced classes of package Zql:
//            ZStatement

public final class ZTransactStmt
    implements ZStatement
{

    public ZTransactStmt(String s)
    {
        comment_ = null;
        readOnly_ = false;
        statement_ = new String(s);
    }

    public void setComment(String s)
    {
        comment_ = new String(s);
    }

    public String getComment()
    {
        return comment_;
    }

    public boolean isReadOnly()
    {
        return readOnly_;
    }

    String statement_;
    String comment_;
    boolean readOnly_;
}