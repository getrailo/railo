<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testAbs(){
		
		assertEquals(1,abs(1));
		assertEquals(1.9,abs(1.9));
		assertEquals(1.9,abs(-1.9));
		assertEquals(1.9,abs(+1.9));
		assertEquals(0,abs(0));
		assertEquals(0,abs(-0));
		assertEquals(0,abs("0"));
		
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>