package railo.commons.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hsqldb.jdbcDriver;

public final class HDSQLDriver extends jdbcDriver {
    
    @Override
    public Connection connect(String arg0, Properties arg1) throws SQLException {
        
        
        return super.connect(arg0, arg1);
    }
    
    
    
/// <cfset this.dsn="jdbc:hsqldb:file:{path}{database}">
}
