<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testStruct(){
		assertEquals(1,{a:1}.len());
	}

	public void function testArray(){
		assertEquals(1,[1].len());
	}
	public void function testString(){
		assertEquals(1,"S".length());
	}
} 
</cfscript>