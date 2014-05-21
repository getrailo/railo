<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test(){
		var a = [1,2,3];

		assertEquals(2,a.contains(2));
		assertEquals(2,a.containsNoCase(2));
		assertEquals(2,arrayContains(a, 2));
		assertEquals(2,arrayContainsNoCase(a, 2));
	}

	private void function testNull1(){
		var a = [1,2,nullValue()];

		assertEquals(2,a.contains(2));
		assertEquals(2,a.containsNoCase(2));
		assertEquals(2,arrayContains(a, 2));
		assertEquals(2,arrayContainsNoCase(a, 2));
	}

	private void function testNull2(){
		var a = [1,nullValue(),3];

		assertEquals(2,a.contains(2));
		assertEquals(2,a.containsNoCase(2));
		assertEquals(2,arrayContains(a, 2));
		assertEquals(2,arrayContainsNoCase(a, 2));
	}
} 
</cfscript>