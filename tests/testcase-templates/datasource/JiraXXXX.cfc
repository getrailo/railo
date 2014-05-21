<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	variables.tableName="TXXXX";

	public function beforeTests(){
		defineDatasource();

		try{
			query {
				echo("drop TABLE "&variables.tableName);
			}
		}
		catch(local.e){}
		
		
		query  {
			echo("CREATE TABLE "&variables.tableName&" (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("vc varchar(255),");		
			echo("c char(1)");		
			echo(") ");
		}
		
	}
	private string function defineDatasource(){
		application action="update" 
			datasource="#{
	  		class: 'org.hsqldb.jdbcDriver'
			, connectionString: 'jdbc:hsqldb:file:#getDirectoryFromPath(getCurrentTemplatePath())#/datasource/db'
		}#";
	}

	public void function testNoSpace() {
		var qry=query(a:[1,2,3]);

		var qry=queryExecute(
		"insert into "&variables.tableName&"( i, vc,c) values( :col1,:col2,:col3)",
		{ col1 = 1, col2 = 2, col3 = 3 }
		); 
	}
} 
</cfscript>