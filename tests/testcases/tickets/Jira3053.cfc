<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		defineDatasource();

		try{
			query {
				echo("drop TABLE T3053");
			}
		}
		catch(local.e){}
		
		
		query  {
			echo("CREATE TABLE T3053 (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("vc varchar(255),");		
			echo("c char(1)");		
			echo(") ");
		}
		
	}

	public function afterTests(){
		try{
			query {
				echo("drop TABLE T3053");
			}
		}
		catch(local.e){}
	}

	public void function testQueryExecuteInsert() localMode="modern" {
		queryExecute("insert into T3053 (id,vc,c,i) values(1,'1','1',1)");
	}

	public void function testQueryExecuteUpdate() localMode="modern" {
		queryExecute("update T3053 set c='2'");
	}
	
	private string function defineDatasource(){
		application action="update" 
			datasource="#{
	  		class: 'org.hsqldb.jdbcDriver'
			, connectionString: 'jdbc:hsqldb:file:#getDirectoryFromPath(getCurrentTemplatePath())#/datasource/db'
		}#";
	}

} 
</cfscript>