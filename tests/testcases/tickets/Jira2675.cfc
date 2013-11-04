<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testDouble(){
		d=0.0905;
		assertEquals("0.0905:",d&":");
		
	}
	public void function testFloat(){
		f=createObject('java','java.lang.Float').parseFloat("0.0905");
		assertEquals("0.0905:",f&":");
		
	}
} 
</cfscript>