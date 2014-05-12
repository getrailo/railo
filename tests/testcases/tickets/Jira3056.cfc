<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testStartsWith(){
		assertEquals(true,"Susi Sorglos".startsWith("Susi"));
	}
	public void function testStartsWithWithOffset(){
		assertEquals(true,"Susi Sorglos".startsWith("Sorglos",5));
	}
	public void function testEndsWith(){
		assertEquals(true,"Susi Sorglos".endsWith("Sorglos"));
	}

	public void function testHasPrefix(){
		assertEquals(true,"Susi Sorglos".hasPrefix("Susi"));
	}
	public void function testHasSuffix(){
		assertEquals(true,"Susi Sorglos".hasSuffix("Sorglos"));
	}
} 
</cfscript>