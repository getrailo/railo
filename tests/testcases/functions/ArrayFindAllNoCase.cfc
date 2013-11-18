<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrayFindAllNoCase(){
		var arr=["aaa","bb","aaa","ccc","AAA"];
res=arrayFindAllNoCase(arr,"aaa");
valueEquals(arraytoList(res),'1,3,5');
res=arrayFindAllNoCase(arr,"a");
valueEquals(arraytoList(res),'');
		
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