component extends="org.railo.cfml.test.RailoTestCase"	{


	public function testDoubleDot() {

		var test = isValid("email", "user@bigpond..com");

		assertFalse( test );
	}


}