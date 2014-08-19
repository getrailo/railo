<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		
		query name="qry" datasource="mysql" cachedwithin="#createTimespan(0,0,1,1)#" {
			echo("select CURTIME() as a");
		}
		objectcache action="clear";
	}
} 
</cfscript>