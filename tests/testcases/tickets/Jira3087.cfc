<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		defineDatasource();

		try{
			query {
				echo("drop TABLE T3087");
			}
		}
		catch(local.e){}
		
		
		query  {
			echo("CREATE TABLE T3087 (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("dec DECIMAL");		
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
		queryExecute(
			"UPDATE T3087 SET dec = ?", 
			[ 
			{ value: 2.95, cfsqltype: "cf_sql_decimal", scale: 2 } ], {} );

		
	}
} 
</cfscript>