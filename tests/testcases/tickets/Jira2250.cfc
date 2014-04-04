<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testIsEmpty(){
		
		assertTrue( stringIsEmpty("") );
		assertTrue( "".isEmpty() );

		assertFalse( stringIsEmpty("Susi") );
		assertFalse( "Susi".isEmpty() );

	}

} 
</cfscript>