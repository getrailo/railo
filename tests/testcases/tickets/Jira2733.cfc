<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public function beforeTests(){
		variables.ds="test_ms";
		
		try{
			query datasource="#ds#" {
				echo("drop TABLE [dbo].[T2733]");
			}
		}
		catch(local.e){}
		
		
		query datasource="#ds#" {
			echo("CREATE TABLE dbo.T2733 (");
			echo("id int NOT NULL,");
			echo("i int,");		
			echo("vc varchar(255),");		
			echo("c char(1)");		
			echo(") ON [PRIMARY]");
		}
		
		query datasource="#ds#" {
			echo("insert into dbo.T2733 (id,vc,c,i)");
			echo("values(1,'1','1',1)");
		}
		
		
	}

	public void function test(){
		
		var str="12";
		query datasource="#ds#" name="local.qry" {
			echo("select * from dbo.T2733");
		}
		
		
		// char
		assertEquals(1,ListFind(ValueList(qry.c,","),str.charAt(0)));
		assertEquals(1,ListFind(ValueList(qry.vc,","),str.charAt(0)));
		assertEquals(1,ListFind(ValueList(qry.i,","),str.charAt(0)));
		
		// string/varchar
		assertEquals(1,ListFind(ValueList(qry.c,","),"1"));
		assertEquals(1,ListFind(ValueList(qry.vc,","),"1"));
		assertEquals(1,ListFind(ValueList(qry.i,","),"1"));
		
		// number
		assertEquals(1,ListFind(ValueList(qry.c,","),1));
		assertEquals(1,ListFind(ValueList(qry.vc,","),1));
		assertEquals(1,ListFind(ValueList(qry.i,","),1));
		
		// char from query
		assertEquals(1,ListFind(ValueList(qry.c,","),qry.c));
		assertEquals(1,ListFind(ValueList(qry.vc,","),qry.c));
		assertEquals(1,ListFind(ValueList(qry.i,","),qry.c));
		
		// int from query
		assertEquals(1,ListFind(ValueList(qry.c,","),qry.i));
		assertEquals(1,ListFind(ValueList(qry.vc,","),qry.i));
		assertEquals(1,ListFind(ValueList(qry.i,","),qry.i));
		
		// varchar from query
		assertEquals(1,ListFind(ValueList(qry.c,","),qry.vc));
		assertEquals(1,ListFind(ValueList(qry.vc,","),qry.vc));
		assertEquals(1,ListFind(ValueList(qry.i,","),qry.vc));
		
	}
} 
</cfscript>