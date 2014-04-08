<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		dump(dateTimeFormat(now(),"iso8601"));
		parseDateTime(dateTimeFormat(now(),"iso8601"));

		/*assertEquals("","");
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
} 
</cfscript>