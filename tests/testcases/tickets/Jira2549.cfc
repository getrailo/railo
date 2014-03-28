component extends="org.railo.cfml.test.RailoTestCase"	{
	

	function testLeft() {

		assert( left("12345678", 2) == "12" );
		assert( left("12345678", 10) == "12345678" );

		assert( left("12345678", -2) == "123456" );

		assert( left("12345678", -8) == "12345678" );

		assert( left("12345678", -10) == "12345678" );

		assert( left("C:\Windows\Programs\", -1) == "C:\Windows\Programs" );
	}


	function testRight() {

		assert( right("12345678", 2) == "78" );
		assert( right("12345678", 10) == "12345678" );

		assert( right("12345678", -2) == "345678" );

		assert( right("12345678", -8) == "12345678" );

		assert( right("12345678", -10) == "12345678" );

		assert( right("C:\Windows\Programs\", -1) == ":\Windows\Programs\" );
	}


}