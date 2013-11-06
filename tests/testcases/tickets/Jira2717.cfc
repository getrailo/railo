<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}
	
	public void function testRegularFunctionCall(){
		var testArray = [ ];
		arrayAppend( testArray, nullvalue() );
		assertEquals(1,arrayLen(testArray));
	}
	public void function testEvaluateRegularFunctionCall(){
		var testArray = [ ];
		evaluate('arrayAppend( testArray, nullvalue() )');
		assertEquals(1,arrayLen(testArray));
	}
	
	public void function testMemberFunctionCall(){
		var testArray = [ ];
		testArray.append( nullvalue());
		assertEquals(1,arrayLen(testArray));
	}
	
	public void function testEvaluateMemberFunctionCall(){
		var testArray = [ ];
		evaluate('testArray.append( nullvalue() )');
		assertEquals(1,arrayLen(testArray));
	}
} 
</cfscript>