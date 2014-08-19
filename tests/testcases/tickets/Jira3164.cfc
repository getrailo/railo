<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function test(){
		var sct=structnew("linked");
		sct.z="ZZZ";
		sct.a="AAA";
		sct.m="MMM";
		res=structMap(sct,function(){});

		assertEquals("Z,A,M",structKeyList(res));
	}
} 
</cfscript>