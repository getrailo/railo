<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	

	public void function testArrayMedian(){
		
		assertEquals(90, ArrayMedian([100,2,90,80,100]));
		
		assertEquals(95, ArrayMedian([100,90,80,100]));
		
		try{
			ArrayMedian([1,'hans',2]);
			// error
			fail("must throw: Non-numeric value found");
		}
		catch(local.exp){}
		
		
	}
} 
</cfscript>