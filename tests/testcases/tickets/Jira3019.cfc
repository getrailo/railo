<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		parseDateTime(dateTimeFormat(now(),"iso8601"));
	}
} 
</cfscript>