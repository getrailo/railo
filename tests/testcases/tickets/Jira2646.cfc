<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testListGetAt(){
		local.bla = "1||2||3";
		assertEquals(3,listlen(bla,"||"));
		assertEquals(1,listgetat(bla,1,"||"));
		assertEquals(2,listgetat(bla,2,"||"));
		assertEquals(3,listgetat(bla,3,"||"));
		
		local.bla = "1|2|3";
		assertEquals(3,listlen(bla,"||"));
		assertEquals(1,listgetat(bla,1,"||"));
		assertEquals(2,listgetat(bla,2,"||"));
		assertEquals(3,listgetat(bla,3,"||"));
		
	}
} 
</cfscript>