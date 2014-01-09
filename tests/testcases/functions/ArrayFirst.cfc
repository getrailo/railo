<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testArrrayFirst(){
		if(server.ColdFusion.ProductName EQ "railo"){
			var x=array(1,2,3,4,5,6,7,8);
			assertEquals(1,ArrayFirst(x));
		}
	}
} 
</cfscript>