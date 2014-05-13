<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testMySQLWithBSTTimezone(){

		application action="update" timezone="BST";
		setTimeZone("BST");

		query name="local.qry" datasource="mysql" {
			echo("select 'a' as a");
		}
		//assertEquals("","");
		
	}
	public void function testMySQLWithLondonTimezone(){

		application action="update" timezone="Europe/London";
		setTimeZone("Europe/London");
		
		query name="local.qry" datasource="mysql" {
			echo("select 'a' as a");
		}
		//assertEquals("","");
		
	}
} 
</cfscript>