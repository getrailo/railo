<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		application action="update" sessionType = "j2ee";
		ObjectSave(session);
	}
} 
</cfscript>