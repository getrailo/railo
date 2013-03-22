package railo.runtime.sql.old;


public final class Token
{

    public Token()
    {
    }

    public final String toString()
    {
        return image;
    }

    public static final Token newToken(int i)
    {
        switch(i)
        {
        default:
            return new Token();
        }
    }

    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;
}