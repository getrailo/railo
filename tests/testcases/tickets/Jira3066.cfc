<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){

		var repl = getDirectoryFromPath( CGI.SCRIPT_NAME ).replace( "", '' );
		assertEquals(getDirectoryFromPath( CGI.SCRIPT_NAME ),repl);
		
	}
} 
</cfscript>