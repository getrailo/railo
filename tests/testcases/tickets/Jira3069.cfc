<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testLog(){
		log type="information" application="true" text="myApp inited";
	}
} 
</cfscript>