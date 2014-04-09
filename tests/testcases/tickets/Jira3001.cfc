<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testStartsWith(){
		
		assertTrue( "abcde".startsWith("ab") );
		assertTrue( "abcde".startsWith("") );
		assertTrue( "abcde".startsWith("AbC", true) );
		
		assertFalse( "abcde".startsWith("AbCdeFghi", true) );
		assertFalse( "abcde".startsWith("abcdefghi") );
		assertFalse( "abcde".startsWith("x") );
		assertFalse( "abcde".startsWith("AbC") );

		assertTrue( StringStartsWith("abcdef", "abc") );
		assertTrue( StringStartsWith("Abcdef", "abc", true) );
		assertFalse( StringStartsWith("Abcdef", "cde") );

	}

	public void function testEndsWith(){
		
		assertTrue( "abcde".endsWith("") );
		assertTrue( "abcde".endsWith("de") );
		assertTrue( "abcde".endsWith("de", false) );
		assertTrue( "abcde".endsWith("De", true) );
		
		assertFalse( "abcde".endsWith("ab") );
		assertFalse( "abcde".endsWith("abcdefghi") );
		assertFalse( "abcde".endsWith("x") );
		assertFalse( "abcde".endsWith("AbC") );
		assertFalse( "abcde".endsWith("AbC", true) );
		assertFalse( "abcde".endsWith("AbCdeFghi", true) );
		assertFalse( "abcde".endsWith("De") );
		assertFalse( "abcde".endsWith("De", false) );

		assertTrue( StringEndsWith("abcdef", "def") );
		assertTrue( StringEndsWith("abcdef", "Def", true) );
	}
} 
</cfscript>