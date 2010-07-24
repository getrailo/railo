<cfset stDBDriver = StructNew()>
<cfset stDBDriver.Other.Class         = "The class that implements the driver">
<cfset stDBDriver.Other.dsn           = "The string needed for the datasource connection">
<cfset stDBDriver.Other.description   = "Connect to an other JDBC Driver installed on the system">
<cfset stDBDriver.ODBC.DatabaseName   = "The name of the ODBC-DSN defined on the system">
<cfset stDBDriver.ODBC.description    = "JDBC-ODBC brige driver to access an ODBC connection on windows systems">
<cfset stDBDriver.MySQL.ServerHost    = "The hostname of the MySQL database server">
<cfset stDBDriver.MySQL.DatabaseName  = "The name of the MySQL database from the database server">
<cfset stDBDriver.MySQL.description   = "Database driver to connect to a MySQL Database resided on a MySQL server">
<cfset stDBDriver.MSSQL.ServerHost    = "Servername or IP-address of the SQL Server you want to connect to">
<cfset stDBDriver.MSSQL.DatabaseName  = "The name of the database on the SQL Server">
<cfset stDBDriver.MSSQL.description   = "MSSQL Driver for Microsoft SQL Database">
<cfset stDBDriver.HSQLDB.DatabaseName = "The name of the HSQL database">
<cfset stDBDriver.HSQLDB.description  = "Hypersonic SQL DB Driver">
