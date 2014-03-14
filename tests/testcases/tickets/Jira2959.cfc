<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testToScript(){
		assertEquals("susi=""\""'"";",toScript("""'","susi"));
	}
	public void function testSerializeJson(){
		assertEquals("""\""'""",SerializeJson("""'"));
	}
} 
</cfscript>