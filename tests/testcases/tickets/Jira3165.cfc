<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testInFunction(){
		local.x = "test";
		local.x = local.x &"-123";
		local.x &= "-123";
	}

	public void function testOutsiteFunction(){
		module template="Jira3165/index.cfm";
	}
} 
</cfscript>