<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testUCase(){
		assertEquals(asc("A"),asc("a".ucase()));
	}

	public void function testLCase(){
		assertEquals(asc("a"),asc("A".lcase()));
	}

	public void function testTrim(){
		assertEquals("a"," a ".trim());
	}
} 
</cfscript>