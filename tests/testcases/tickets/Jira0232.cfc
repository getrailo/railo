<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}

	public void function test(){
		assertEquals(false,"0000000000000000000000000696E433" eq "5625627186710640143152E540486175");
	}
} 
</cfscript>