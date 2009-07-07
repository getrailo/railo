package railo.runtime.sql.old;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class ZUpdate implements ZStatement	{

    public ZUpdate(String s)
    {
        where_ = null;
        columns_ = null;
        table_ = new String(s);
    }

    public String getTable()
    {
        return table_;
    }

    public void addSet(Hashtable hashtable)
    {
        set_ = hashtable;
    }

    public Hashtable getSet()
    {
        return set_;
    }

    public void addColumnUpdate(String s, ZExp zexp)
    {
        if(set_ == null)
            set_ = new Hashtable();
        set_.put(s, zexp);
        if(columns_ == null)
            columns_ = new Vector();
        columns_.addElement(s);
    }

    public ZExp getColumnUpdate(String s)
    {
        return (ZExp)set_.get(s);
    }

    public ZExp getColumnUpdate(int i)
    {
        if(--i < 0)
            return null;
        if(columns_ == null || i >= columns_.size())
        {
            return null;
        }
        String s = (String)columns_.elementAt(i);
        return (ZExp)set_.get(s);
        
    }

    public String getColumnUpdateName(int i)
    {
        if(--i < 0)
            return null;
        if(columns_ == null || i >= columns_.size())
            return null;
        return (String)columns_.elementAt(i);
    }

    public int getColumnUpdateCount()
    {
        if(set_ == null)
            return 0;
        return set_.size();
    }

    public void addWhere(ZExp zexp)
    {
        where_ = zexp;
    }

    public ZExp getWhere()
    {
        return where_;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("update " + table_);
        stringbuffer.append(" set ");
        Enumeration enumeration;
        if(columns_ != null)
            enumeration = columns_.elements();
        else
            enumeration = set_.keys();
        for(boolean flag = true; enumeration.hasMoreElements(); flag = false)
        {
            String s = enumeration.nextElement().toString();
            if(!flag)
                stringbuffer.append(", ");
            stringbuffer.append(s + "=" + set_.get(s).toString());
        }

        if(where_ != null)
            stringbuffer.append(" where " + where_.toString());
        return stringbuffer.toString();
    }

    String table_;
    Hashtable set_;
    ZExp where_;
    Vector columns_;
}