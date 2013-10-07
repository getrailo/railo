component extends="org.railo.cfml.test.RailoTestCase"	{

	public function testReplaceListNull() {

		var result = replaceList( "abcdefg", "#chr(0)#,c", "A,B" );

		// assertEquals( result, "abBdefg" );
	}
}