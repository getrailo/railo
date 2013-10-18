<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}
	private function testGetFunctionCalledName(name) {
	 	assertEquals(name,GetFunctionCalledName());
	}

	public void function test(){
		
		assertEquals(GetFunctionCalledName(),'test');

		testGetFunctionCalledName('testGetFunctionCalledName');
		abcd=testGetFunctionCalledName;
		abcd("abcd");
	
		cloGetFunctionCalledName=function (name) {
	 		assertEquals(name,GetFunctionCalledName());
		};
		cloGetFunctionCalledName("cloGetFunctionCalledName");
	}
} 
</cfscript>