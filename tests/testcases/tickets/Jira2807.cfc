<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testSerializeJson() {
		var str="a\b/c'd""e";
		var serJson=serializeJson(str);
		var ser=serialize(str);
		
		assertEquals('"a\\b/c''d\"e"',serJson);
	}
} 
</cfscript>