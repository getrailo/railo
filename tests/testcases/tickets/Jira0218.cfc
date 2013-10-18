<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function test(){
		
		var q = queryNew("c");
		structKeyExists(q, "c");
		assertEquals(true,structKeyExists(q, "c"));
		assertEquals(true,isDefined("q.c"));

	}
} 
</cfscript>