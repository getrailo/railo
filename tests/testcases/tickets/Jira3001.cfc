<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testhasPrefix(){
		
		assertTrue( "abcde".hasPrefix("ab") );
		assertTrue( "abcde".hasPrefix("") );
		assertTrue( "abcde".hasPrefix("AbC", true) );
		
		assertFalse( "abcde".hasPrefix("AbCdeFghi", true) );
		assertFalse( "abcde".hasPrefix("abcdefghi") );
		assertFalse( "abcde".hasPrefix("x") );
		assertFalse( "abcde".hasPrefix("AbC") );

		assertTrue( StringhasPrefix("abcdef", "abc") );
		assertTrue( StringhasPrefix("Abcdef", "abc", true) );
		assertFalse( StringhasPrefix("Abcdef", "cde") );

	}

	public void function testhasSuffix(){
		
		assertTrue( "abcde".hasSuffix("") );
		assertTrue( "abcde".hasSuffix("de") );
		assertTrue( "abcde".hasSuffix("de", false) );
		assertTrue( "abcde".hasSuffix("De", true) );
		
		assertFalse( "abcde".hasSuffix("ab") );
		assertFalse( "abcde".hasSuffix("abcdefghi") );
		assertFalse( "abcde".hasSuffix("x") );
		assertFalse( "abcde".hasSuffix("AbC") );
		assertFalse( "abcde".hasSuffix("AbC", true) );
		assertFalse( "abcde".hasSuffix("AbCdeFghi", true) );
		assertFalse( "abcde".hasSuffix("De") );
		assertFalse( "abcde".hasSuffix("De", false) );

		assertTrue( StringhasSuffix("abcdef", "def") );
		assertTrue( StringhasSuffix("abcdef", "Def", true) );
	}
} 
</cfscript>