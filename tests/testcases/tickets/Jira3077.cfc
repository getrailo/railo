<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		defineDatasource();

		try{
			query {
				echo("drop TABLE T3077");
			}
		}
		catch(local.e){}
		
		
		query  {
			echo("CREATE TABLE T3077 (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("vc varchar(255)");		
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
		"insert into T3077(id, i, vc) values(:col0,:col1,:col2)",
		{col0:0, col1 = 1, col2 = 2, col3 = 3 }
		); 
	}
} 
</cfscript>