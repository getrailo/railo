<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
	
	private function valueEquals(left,right) {
		assertEquals(arguments.right,arguments.left);
	}
} 
</cfscript>