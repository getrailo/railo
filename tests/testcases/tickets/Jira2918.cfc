<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test() localmode="true" {
		sct = StructNew(type="Linked");
		sct.b=2;
		sct.c=3;
		sct.d=4;
		sct.a=1;
		var initalList=structKeyList(sct);
		ser=serializeJson(sct);
		sct=deserializeJson(ser);

		assertEquals(initalList,structKeyList(sct));
	}
} 
</cfscript>