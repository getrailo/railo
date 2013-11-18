<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrayAvg(){
		
		assertEquals(2,ArrayAvg([1,2,3]));
		
		assertEquals('12.3333333',left(tostring(ArrayAvg([1,2.5,33.5])),10));

		assertEquals('1.33333333',left(tostring(ArrayAvg([1,true,2])),10));


		try{
			ArrayAvg([1,'hans',2]);
			// error
			fail("must throw: Non-numeric value found");
		}
		catch(local.exp){}
		
		
		var arr=arrayNew(1);
		arr[3]=0;
		ArrayAppend( arr, 1 );
		ArrayAppend( arr, 2 );
		
		try{
			ArrayAvg(arr);
			// error
			fail("must throw: Non-numeric value found");
		}
		catch(local.exp){}
		
		arr=arrayNew(2);
		arr[1][1]=1;
		arr[1][2]=2;
		arr[1][3]=3;
		
		
		try{
			ArrayAvg(arr);
			// error
			fail("must throw: The array passed cannot contain more than one dimension.");
		}
		catch(local.exp){}
		
		
	}
} 
</cfscript>