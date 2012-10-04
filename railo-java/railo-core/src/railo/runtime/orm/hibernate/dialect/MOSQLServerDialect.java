package railo.runtime.orm.hibernate.dialect;

import org.hibernate.dialect.SQLServer2008Dialect;

/**
 * Created with IntelliJ IDEA.
 * User: marius
 * Date: 3-10-12
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class MOSQLServerDialect extends SQLServer2008Dialect
{
	public boolean supportsUnionAll()
	{
        return true;
    }
}
