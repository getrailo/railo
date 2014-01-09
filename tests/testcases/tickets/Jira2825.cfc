<cfscript>
	pageencoding "utf-8"; 
component extends="org.railo.cfml.test.RailoTestCase"	{
	// processingdirective pageencoding="utf-8";
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testReplaceNoCase(){
	
		var str="LİELİELİELİE....";
		var expectedResult="Lİa very long string to replace ELİa very long string to replace ELİa very long string to replace ELİa very long string to replace E....";
	
		var repl="a very long string to replace E";
		var resultNoCase=replaceNoCase(str,"E",repl,"all");
		var result=replaceNoCase(str,"E",repl,"all");

		assertEquals(expectedResult,result);
		assertEquals(expectedResult,resultNoCase);
		assertEquals(result,resultNoCase);
	}
} 
</cfscript>