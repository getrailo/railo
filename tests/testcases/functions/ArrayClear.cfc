<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrayClear(){
		arr=arrayNew(1);
		ArrayAppend( arr, 1 );
		ArrayAppend( arr, 2 );
		ArrayAppend( arr, 3 );
		arrayClear(arr);
		assertEquals(0,arrayLen(arr));

		arr=arrayNew(1);
		arr[10]=3;
		ArrayResize(arr, 20);
		ArrayAppend( arr, 1 );
		ArrayAppend( arr, 2 );
		ArrayAppend( arr, 3 );
		arrayClear(arr);
		assertEquals(0,arrayLen(arr));
		
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>