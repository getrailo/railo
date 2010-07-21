package railo.runtime.sql.old;

import java.io.Serializable;
import java.util.StringTokenizer;

public class ZAliasedName
    implements Serializable
{

    public ZAliasedName()
    {
        strform_ = "";
        schema_ = null;
        table_ = null;
        column_ = null;
        alias_ = null;
        form_ = FORM_COLUMN;
    }

    public ZAliasedName(String s, int i)
    {
    	strform_ = "";
        schema_ = null;
        table_ = null;
        column_ = null;
        alias_ = null;
        form_ = FORM_COLUMN;
        form_ = i;
        strform_ = new String(s);
        StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
        switch(stringtokenizer.countTokens())
        {
        case 1: // '\001'
            if(i == FORM_TABLE)
                table_ = new String(stringtokenizer.nextToken());
            else
                column_ = new String(stringtokenizer.nextToken());
            break;

        case 2: // '\002'
            if(i == FORM_TABLE)
            {
                schema_ = new String(stringtokenizer.nextToken());
                table_ = new String(stringtokenizer.nextToken());
            } else
            {
                table_ = new String(stringtokenizer.nextToken());
                column_ = new String(stringtokenizer.nextToken());
            }
            break;

        case 3: // '\003'
        default:
            schema_ = new String(stringtokenizer.nextToken());
            table_ = new String(stringtokenizer.nextToken());
            column_ = new String(stringtokenizer.nextToken());
            break;
        }
    }

    public String toString()
    {
        if(alias_ == null)
            return strform_;
        return strform_ + " " + alias_;
    }

    public String getSchema()
    {
        return schema_;
    }

    public String getTable()
    {
        return table_;
    }

    public String getColumn()
    {
        return column_;
    }

    public boolean isWildcard()
    {
        if(form_ == FORM_TABLE)
            return table_ != null && table_.equals("*");
        return column_ != null && column_.indexOf('*') >= 0;
    }

    public String getAlias()
    {
        return alias_;
    }

    public void setAlias(String s)
    {
        alias_ = new String(s);
    }
    

    String strform_;
    String schema_;
    String table_;
    String column_;
    String alias_;
    public static int FORM_TABLE = 1;
    public static int FORM_COLUMN = 2;
    int form_;

}