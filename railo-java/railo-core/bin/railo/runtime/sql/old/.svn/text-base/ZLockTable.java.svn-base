
package railo.runtime.sql.old;

import java.util.Vector;

// Referenced classes of package Zql:
//            ZStatement

public final class ZLockTable
    implements ZStatement
{

    public ZLockTable()
    {
        nowait_ = false;
        lockMode_ = null;
        tables_ = null;
    }

    public void addTables(Vector vector)
    {
        tables_ = vector;
    }

    public Vector getTables()
    {
        return tables_;
    }

    public void setLockMode(String s)
    {
        lockMode_ = new String(s);
    }

    public String getLockMode()
    {
        return lockMode_;
    }

    public boolean isNowait()
    {
        return nowait_;
    }

    boolean nowait_;
    String lockMode_;
    Vector tables_;
}