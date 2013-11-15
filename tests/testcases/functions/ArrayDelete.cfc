<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrayDelete(){
		var arr=[1,2,3,4];
		assertEquals("1,2,3,4",arrayToList(arr));
		arrayDelete(arr,2)
		assertEquals("1,3,4",arrayToList(arr));
		arrayDelete(arr,1);
		assertEquals("3,4",arrayToList(arr));
		assertEquals(false,arrayDelete(arr,1));
		assertEquals(true,arrayDelete(arr,3));
		
		
		arr=['SUSI'];
		assertEquals("SUSI",arrayToList(arr));
		arrayDelete(arr,'SUSI');
		assertEquals("",arrayToList(arr));

		arr=[1,1,1];
		assertEquals("1,1,1",arrayToList(arr));
		arrayDelete(arr,1)
		assertEquals("1,1",arrayToList(arr));
	
		arr=['SUSI','susi'];
		assertEquals("SUSI,susi",arrayToList(arr));
		arrayDelete(arr,'SUSI');
		assertEquals("susi",arrayToList(arr));
		
		n=now();
		arr=[now()];
		arrayDelete(arr,n);
		assertEquals("",arrayToList(arr));
		
		
		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>