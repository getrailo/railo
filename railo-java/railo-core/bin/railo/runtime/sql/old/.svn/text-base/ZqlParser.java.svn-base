
package railo.runtime.sql.old;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Vector;

// Referenced classes of package Zql:
//            ZqlJJParser, ParseException, ZUtils, ZStatement, 
//            ZExp

public final class ZqlParser
{

    public static void main(String args[])
        throws ParseException
    {
        ZqlParser zqlparser = null;
        if(args.length < 1)
        {
            System.out.println("/* Reading from stdin (exit; to finish) */");
            zqlparser = new ZqlParser(System.in);
        } else
        {
            try
            {
                zqlparser = new ZqlParser(new DataInputStream(new FileInputStream(args[0])));
            }
            catch(FileNotFoundException filenotfoundexception)
            {
                System.out.println("/* File " + args[0] + " not found. Reading from stdin */");
                zqlparser = new ZqlParser(System.in);
            }
        }
        if(args.length > 0)
            System.out.println("/* Reading from " + args[0] + "*/");
        for(ZStatement zstatement = null; (zstatement = zqlparser.readStatement()) != null;)
            System.out.println(zstatement.toString() + ";");

        System.out.println("exit;");
        System.out.println("/* Parse Successful */");
    }

    public ZqlParser(InputStream inputstream)
    {
        _parser = null;
        initParser(inputstream);
    }

    public ZqlParser()
    {
        _parser = null;
    }

    public void initParser(InputStream inputstream)
    {
        if(_parser == null)
            _parser = new ZqlJJParser(inputstream);
        else
            _parser.ReInit(inputstream);
    }

    public void addCustomFunction(String s, int i)
    {
        ZUtils.addCustomFunction(s, i);
    }

    public ZStatement readStatement()
        throws ParseException
    {
        if(_parser == null)
            throw new ParseException("Parser not initialized: use initParser(InputStream);");
        return _parser.SQLStatement();
    }

    public Vector readStatements()
        throws ParseException
    {
        if(_parser == null)
            throw new ParseException("Parser not initialized: use initParser(InputStream);");
        return _parser.SQLStatements();
    }

    public ZExp readExpression()
        throws ParseException
    {
        if(_parser == null)
            throw new ParseException("Parser not initialized: use initParser(InputStream);");
        return _parser.SQLExpression();
    }

    ZqlJJParser _parser;
}